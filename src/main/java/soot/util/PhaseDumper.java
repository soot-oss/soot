package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 John Jorgensen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.G;
import soot.Printer;
import soot.Scene;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalGraph;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.dot.DotGraph;

/**
 * The <tt>PhaseDumper</tt> is a debugging aid. It maintains two lists of phases to be debugged. If a phase is on the
 * <code>bodyDumpingPhases</code> list, then the intermediate representation of the bodies being manipulated by the phase is
 * dumped before and after the phase is applied. If a phase is on the <code>cfgDumpingPhases</code> list, then whenever a CFG
 * is constructed during the phase, a dot file is dumped representing the CFG constructed.
 */
public class PhaseDumper {
  private static final Logger logger = LoggerFactory.getLogger(PhaseDumper.class);

  private static final String ALL_WILDCARD = "ALL";
  private final PhaseStack phaseStack = new PhaseStack();

  // As a minor optimization, we leave these lists null in the case were
  // no phases at all are to be dumped, which is the most likely case.
  private List<String> bodyDumpingPhases = null;
  private List<String> cfgDumpingPhases = null;

  // soot.Printer itself needs to create a BriefUnitGraph in order
  // to format the text for a method's instructions, so this flag is
  // a hack to avoid dumping graphs that we create in the course of
  // dumping bodies or other graphs.
  //
  // Note that this hack would not work if a PhaseDumper might be
  // accessed by multiple threads. So long as there is a single
  // active PhaseDumper accessed through soot.G, it seems
  // safe to assume it will be accessed by only a single thread.
  private boolean alreadyDumping = false;

  private class PhaseStack extends ArrayList<String> {
    // We eschew java.util.Stack to avoid synchronization overhead.

    private static final int initialCapacity = 4;
    private static final String EMPTY_STACK_PHASE_NAME = "NOPHASE";

    PhaseStack() {
      super(initialCapacity);
    }

    String currentPhase() {
      if (this.isEmpty()) {
        return EMPTY_STACK_PHASE_NAME;
      } else {
        return this.get(this.size() - 1);
      }
    }

    String pop() {
      return this.remove(this.size() - 1);
    }

    String push(String phaseName) {
      this.add(phaseName);
      return phaseName;
    }
  }

  public PhaseDumper(Singletons.Global g) {
    List<String> bodyPhases = Options.v().dump_body();
    if (!bodyPhases.isEmpty()) {
      bodyDumpingPhases = bodyPhases;
    }
    List<String> cfgPhases = Options.v().dump_cfg();
    if (!cfgPhases.isEmpty()) {
      cfgDumpingPhases = cfgPhases;
    }
  }

  /**
   * Returns the single instance of <code>PhaseDumper</code>.
   *
   * @return Soot's <code>PhaseDumper</code>.
   */
  public static PhaseDumper v() {
    return G.v().soot_util_PhaseDumper();
  }

  private boolean isBodyDumpingPhase(String phaseName) {
    return ((bodyDumpingPhases != null)
        && (bodyDumpingPhases.contains(phaseName) || bodyDumpingPhases.contains(ALL_WILDCARD)));
  }

  private boolean isCFGDumpingPhase(String phaseName) {
    if (cfgDumpingPhases == null) {
      return false;
    }
    if (cfgDumpingPhases.contains(ALL_WILDCARD)) {
      return true;
    } else {
      while (true) { // loop exited by "return" or "break".
        if (cfgDumpingPhases.contains(phaseName)) {
          return true;
        }
        // Go on to check if phaseName is a subphase of a
        // phase in cfgDumpingPhases.
        int lastDot = phaseName.lastIndexOf('.');
        if (lastDot < 0) {
          break;
        } else {
          phaseName = phaseName.substring(0, lastDot);
        }
      }
      return false;
    }
  }

  private static File makeDirectoryIfMissing(Body b) throws IOException {
    StringBuilder buf = new StringBuilder(soot.SourceLocator.v().getOutputDir());
    buf.append(File.separatorChar);
    buf.append(b.getMethod().getDeclaringClass().getName());
    buf.append(File.separatorChar);
    buf.append(b.getMethod().getSubSignature().replace('<', '[').replace('>', ']'));
    File dir = new File(buf.toString());
    if (dir.exists()) {
      if (!dir.isDirectory()) {
        throw new IOException(dir.getPath() + " exists but is not a directory.");
      }
    } else {
      if (!dir.mkdirs()) {
        throw new IOException("Unable to mkdirs " + dir.getPath());
      }
    }
    return dir;
  }

  private static PrintWriter openBodyFile(Body b, String baseName) throws IOException {
    File dir = makeDirectoryIfMissing(b);
    String filePath = dir.toString() + File.separatorChar + baseName;
    return new PrintWriter(new java.io.FileOutputStream(filePath));
  }

  /**
   * Returns the next available name for a graph file.
   */

  private static String nextGraphFileName(Body b, String baseName) throws IOException {
    // We number output files to allow multiple graphs per phase.
    File dir = makeDirectoryIfMissing(b);
    final String prefix = dir.toString() + File.separatorChar + baseName;
    File file = null;
    int fileNumber = 0;
    do {
      file = new File(prefix + fileNumber + DotGraph.DOT_EXTENSION);
      fileNumber++;
    } while (file.exists());
    return file.toString();
  }

  private static void deleteOldGraphFiles(final Body b, final String phaseName) {
    try {
      final File dir = makeDirectoryIfMissing(b);
      final File[] toDelete = dir.listFiles(new java.io.FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.startsWith(phaseName) && name.endsWith(DotGraph.DOT_EXTENSION);
        }
      });
      if (toDelete != null) {
        for (File element : toDelete) {
          element.delete();
        }
      }
    } catch (IOException e) {
      // Don't abort execution because of an I/O error, but report the error.
      logger.debug("PhaseDumper.dumpBody() caught: " + e.toString());
      logger.error(e.getMessage(), e);
    }
  }

  public void dumpBody(Body b, String baseName) {
    final Printer printer = Printer.v();
    alreadyDumping = true;
    try (PrintWriter out = openBodyFile(b, baseName)) {
      printer.setOption(Printer.USE_ABBREVIATIONS);
      printer.printTo(b, out);
    } catch (IOException e) {
      // Don't abort execution because of an I/O error, but let
      // the user know.
      logger.debug("PhaseDumper.dumpBody() caught: " + e.toString());
      logger.error(e.getMessage(), e);
    } finally {
      alreadyDumping = false;
    }
  }

  private void dumpAllBodies(String baseName, boolean deleteGraphFiles) {
    for (SootClass cls : Scene.v().getClasses(SootClass.BODIES)) {
      for (SootMethod method : cls.getMethods()) {
        if (method.hasActiveBody()) {
          Body body = method.getActiveBody();
          if (deleteGraphFiles) {
            deleteOldGraphFiles(body, baseName);
          }
          dumpBody(body, baseName);
        }
      }
    }
  }

  /**
   * Tells the <code>PhaseDumper</code> that a {@link Body} transforming phase has started, so that it can dump the phases's
   * &ldquo;before&rdquo; file. If the phase is to be dumped, <code>dumpBefore</code> deletes any old graph files dumped
   * during previous runs of the phase.
   *
   * @param b
   *          the {@link Body} being transformed.
   * @param phaseName
   *          the name of the phase that has just started.
   */
  public void dumpBefore(Body b, String phaseName) {
    phaseStack.push(phaseName);
    if (isBodyDumpingPhase(phaseName)) {
      deleteOldGraphFiles(b, phaseName);
      dumpBody(b, phaseName + ".in");
    }
  }

  /**
   * Tells the <code>PhaseDumper</code> that a {@link Body} transforming phase has ended, so that it can dump the phases's
   * &ldquo;after&rdquo; file.
   *
   * @param b
   *          the {@link Body} being transformed.
   *
   * @param phaseName
   *          the name of the phase that has just ended.
   *
   * @throws IllegalArgumentException
   *           if <code>phaseName</code> does not match the <code>PhaseDumper</code>'s record of the current phase.
   */
  public void dumpAfter(Body b, String phaseName) {
    String poppedPhaseName = phaseStack.pop();
    if (poppedPhaseName != phaseName) {
      throw new IllegalArgumentException("dumpAfter(" + phaseName + ") when poppedPhaseName == " + poppedPhaseName);
    }
    if (isBodyDumpingPhase(phaseName)) {
      dumpBody(b, phaseName + ".out");
    }
  }

  /**
   * Tells the <code>PhaseDumper</code> that a {@link Scene} transforming phase has started, so that it can dump the phases's
   * &ldquo;before&rdquo; files. If the phase is to be dumped, <code>dumpBefore</code> deletes any old graph files dumped
   * during previous runs of the phase.
   *
   * @param phaseName
   *          the name of the phase that has just started.
   */
  public void dumpBefore(String phaseName) {
    phaseStack.push(phaseName);
    if (isBodyDumpingPhase(phaseName)) {
      dumpAllBodies(phaseName + ".in", true);
    }
  }

  /**
   * Tells the <code>PhaseDumper</code> that a {@link Scene} transforming phase has ended, so that it can dump the phases's
   * &ldquo;after&rdquo; files.
   *
   * @param phaseName
   *          the name of the phase that has just ended.
   *
   * @throws IllegalArgumentException
   *           if <code>phaseName</code> does not match the <code>PhaseDumper</code>'s record of the current phase.
   */
  public void dumpAfter(String phaseName) {
    String poppedPhaseName = phaseStack.pop();
    if (poppedPhaseName != phaseName) {
      throw new IllegalArgumentException("dumpAfter(" + phaseName + ") when poppedPhaseName == " + poppedPhaseName);
    }
    if (isBodyDumpingPhase(phaseName)) {
      dumpAllBodies(phaseName + ".out", false);
    }
  }

  /**
   * Asks the <code>PhaseDumper</code> to dump the passed {@link DirectedGraph} if the current phase is being dumped.
   *
   * @param g
   *          the graph to dump.
   * @param b
   *          the {@link Body} represented by <code>g</code>.
   */
  public <N> void dumpGraph(DirectedGraph<N> g, Body b) {
    dumpGraph(g, b, false);
  }

  /**
   * Asks the <code>PhaseDumper</code> to dump the passed {@link DirectedGraph} if the current phase is being dumped or
   * {@code skipPhaseCheck == true}.
   *
   * @param g
   *          the graph to dump.
   * @param b
   *          the {@link Body} represented by <code>g</code>.
   * @param skipPhaseCheck
   */
  public <N> void dumpGraph(DirectedGraph<N> g, Body b, boolean skipPhaseCheck) {
    if (!alreadyDumping) {
      try {
        alreadyDumping = true;
        String phaseName = phaseStack.currentPhase();
        if (skipPhaseCheck || isCFGDumpingPhase(phaseName)) {
          try {
            String outputFile = nextGraphFileName(b, phaseName + '-' + getClassIdent(g) + '-');
            CFGToDotGraph drawer = new CFGToDotGraph();
            drawer.drawCFG(g, b).plot(outputFile);
          } catch (IOException e) {
            // Don't abort execution because of an I/O error, but
            // report the error.
            logger.debug("PhaseDumper.dumpBody() caught: " + e.toString());
            logger.error(e.getMessage(), e);
          }
        }
      } finally {
        alreadyDumping = false;
      }
    }
  }

  /**
   * Asks the <code>PhaseDumper</code> to dump the passed {@link ExceptionalGraph} if the current phase is being dumped.
   *
   * @param g
   *          the graph to dump.
   */
  public <N> void dumpGraph(ExceptionalGraph<N> g) {
    dumpGraph(g, false);
  }

  /**
   * Asks the <code>PhaseDumper</code> to dump the passed {@link ExceptionalGraph} if the current phase is being dumped or
   * {@code skipPhaseCheck == true}.
   *
   * @param g
   *          the graph to dump.
   * @param skipPhaseCheck
   */
  public <N> void dumpGraph(ExceptionalGraph<N> g, boolean skipPhaseCheck) {
    if (!alreadyDumping) {
      try {
        alreadyDumping = true;
        String phaseName = phaseStack.currentPhase();
        if (skipPhaseCheck || isCFGDumpingPhase(phaseName)) {
          try {
            String outputFile = nextGraphFileName(g.getBody(), phaseName + '-' + getClassIdent(g) + '-');
            CFGToDotGraph drawer = new CFGToDotGraph();
            drawer.setShowExceptions(Options.v().show_exception_dests());
            drawer.drawCFG(g).plot(outputFile);
          } catch (IOException e) {
            // Don't abort execution because of an I/O error, but
            // report the error.
            logger.debug("PhaseDumper.dumpBody() caught: " + e.toString());
            logger.error(e.getMessage(), e);
          }
        }
      } finally {
        alreadyDumping = false;
      }
    }
  }

  /**
   * A utility routine that returns the unqualified identifier naming the class of an object.
   *
   * @param obj
   *          The object whose class name is to be returned.
   */
  private String getClassIdent(Object obj) {
    String qualifiedName = obj.getClass().getName();
    return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
  }

  /**
   * Prints the current stack trace, as a brute force tool for program understanding. This method appeared in response to the
   * many times dumpGraph() was being called while the phase stack was empty. Turned out that the Printer needs to build a
   * BriefUnitGraph in order to print a graph. Doh!
   */
  public void printCurrentStackTrace() {
    IOException e = new IOException("FAKE");
    logger.error(e.getMessage(), e);
  }
}
