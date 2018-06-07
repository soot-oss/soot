package soot.tools;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Sable Research Group
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.PhaseOptions;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.util.cfgcmd.AltClassLoader;
import soot.util.cfgcmd.CFGGraphType;
import soot.util.cfgcmd.CFGIntermediateRep;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.dot.DotGraph;

/**
 * A utility class for generating dot graph file for a control flow graph
 *
 * @author Feng Qian
 */
public class CFGViewer extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(CFGViewer.class);

  private static final String packToJoin = "jtp";
  private static final String phaseSubname = "printcfg";
  private static final String phaseFullname = packToJoin + '.' + phaseSubname;
  private static final String altClassPathOptionName = "alt-class-path";
  private static final String graphTypeOptionName = "graph-type";
  private static final String defaultGraph = "BriefUnitGraph";
  private static final String irOptionName = "ir";
  private static final String defaultIR = "jimple";
  private static final String multipageOptionName = "multipages";
  private static final String briefLabelOptionName = "brief";

  private CFGGraphType graphtype;
  private CFGIntermediateRep ir;
  private CFGToDotGraph drawer;
  private Map<String, String> methodsToPrint; // If the user specifies
  // particular

  // methods to print, this is a map
  // from method name to the class
  // name declaring the method.

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    initialize(options);
    SootMethod meth = b.getMethod();

    if ((methodsToPrint == null) || (meth.getDeclaringClass().getName() == methodsToPrint.get(meth.getName()))) {
      Body body = ir.getBody((JimpleBody) b);
      print_cfg(body);
    }
  }

  public static void main(String[] args) {
    CFGViewer viewer = new CFGViewer();
    Transform printTransform = new Transform(phaseFullname, viewer);
    printTransform.setDeclaredOptions("enabled " + altClassPathOptionName + ' ' + graphTypeOptionName + ' ' + irOptionName
        + ' ' + multipageOptionName + ' ' + briefLabelOptionName + ' ');
    printTransform.setDefaultOptions(
        "enabled " + altClassPathOptionName + ": " + graphTypeOptionName + ':' + defaultGraph + ' ' + irOptionName + ':'
            + defaultIR + ' ' + multipageOptionName + ":false " + ' ' + briefLabelOptionName + ":false ");
    PackManager.v().getPack("jtp").add(printTransform);
    args = viewer.parse_options(args);
    if (args.length == 0) {
      usage();
    } else {
      soot.Main.main(args);
    }
  }

  private static void usage() {
    logger.debug("Usage:\n" + "   java soot.util.CFGViewer [soot options] [CFGViewer options] [class[:method]]...\n\n"
        + "   CFGViewer options:\n" + "      (When specifying the value for an '=' option, you only\n"
        + "       need to type enough characters to specify the choice\n" + "       unambiguously, and case is ignored.)\n"
        + "\n" + "       --alt-classpath PATH :\n" + "                specifies the classpath from which to load classes\n"
        + "                that implement graph types whose names begin with 'Alt'.\n" + "       --graph={"
        + CFGGraphType.help(0, 70, "                ".length()) + "} :\n"
        + "                show the specified type of graph.\n" + "                Defaults to " + defaultGraph + ".\n"
        + "       --ir={" + CFGIntermediateRep.help(0, 70, "                ".length()) + "} :\n"
        + "                create the CFG from the specified intermediate\n" + "                representation. Defaults to "
        + defaultIR + ".\n" + "       --brief :\n" + "                label nodes with the unit or block index,\n"
        + "                instead of the text of their statements.\n" + "       --multipages :\n"
        + "                produce dot file output for multiple 8.5x11\" pages.\n"
        + "                By default, a single page is produced.\n" + "       --help :\n"
        + "                print this message.\n" + "\n"
        + "   Particularly relevant soot options (see \"soot --help\" for details):\n" + "       --soot-class-path PATH\n"
        + "       --show-exception-dests\n" + "       --throw-analysis {pedantic|unit}\n"
        + "       --omit-excepting-unit-edges\n" + "       --trim-cfgs\n");
  }

  /**
   * Parse the command line arguments specific to CFGViewer, and convert them into phase options for jtp.printcfg.
   *
   * @return an array of arguments to pass on to Soot.Main.main().
   */
  private String[] parse_options(String[] args) {
    List<String> sootArgs = new ArrayList<String>(args.length);

    for (int i = 0, n = args.length; i < n; i++) {
      if (args[i].equals("--alt-classpath") || args[i].equals("--alt-class-path")) {
        sootArgs.add("-p");
        sootArgs.add(phaseFullname);
        sootArgs.add(altClassPathOptionName + ':' + args[++i]);
      } else if (args[i].startsWith("--graph=")) {
        sootArgs.add("-p");
        sootArgs.add(phaseFullname);
        sootArgs.add(graphTypeOptionName + ':' + args[i].substring("--graph=".length()));
      } else if (args[i].startsWith("--ir=")) {
        sootArgs.add("-p");
        sootArgs.add(phaseFullname);
        sootArgs.add(irOptionName + ':' + args[i].substring("--ir=".length()));
      } else if (args[i].equals("--brief")) {
        sootArgs.add("-p");
        sootArgs.add(phaseFullname);
        sootArgs.add(briefLabelOptionName + ":true");
      } else if (args[i].equals("--multipages")) {
        sootArgs.add("-p");
        sootArgs.add(phaseFullname);
        sootArgs.add(multipageOptionName + ":true");
      } else if (args[i].equals("--help")) {
        return new String[0]; // This is a cheesy method to inveigle
        // our caller into printing the help
        // and exiting.
      } else if (args[i].equals("--soot-class-path") || args[i].equals("-soot-class-path")
          || args[i].equals("--soot-classpath") || args[i].equals("-soot-classpath") || args[i].equals("--process-dir")
          || args[i].equals("-process-dir") || args[i].equals("--android-jars") || args[i].equals("-android-jars")
          || args[i].equals("--force-android-jar") || args[i].equals("-force-android-jar")) {
        // Pass classpaths without treating ":" as a method specifier.
        sootArgs.add(args[i]);
        sootArgs.add(args[++i]);
      } else if (args[i].equals("-p") || args[i].equals("--phase-option") || args[i].equals("-phase-option")) {
        // Pass phase options without treating ":" as a method
        // specifier.
        sootArgs.add(args[i]);
        sootArgs.add(args[++i]);
        sootArgs.add(args[++i]);
      } else {
        int smpos = args[i].indexOf(':');
        if (smpos == -1) {
          sootArgs.add(args[i]);
        } else {
          String clsname = args[i].substring(0, smpos);
          sootArgs.add(clsname);
          String methname = args[i].substring(smpos + 1);
          if (methodsToPrint == null) {
            methodsToPrint = new HashMap<String, String>();
          }
          methodsToPrint.put(methname, clsname);
        }
      }
    }
    String[] sootArgsArray = new String[sootArgs.size()];
    return (String[]) sootArgs.toArray(sootArgsArray);
  }

  private void initialize(Map<String, String> options) {
    if (drawer == null) {
      drawer = new CFGToDotGraph();
      drawer.setBriefLabels(PhaseOptions.getBoolean(options, briefLabelOptionName));
      drawer.setOnePage(!PhaseOptions.getBoolean(options, multipageOptionName));
      drawer.setUnexceptionalControlFlowAttr("color", "black");
      drawer.setExceptionalControlFlowAttr("color", "red");
      drawer.setExceptionEdgeAttr("color", "lightgray");
      drawer.setShowExceptions(Options.v().show_exception_dests());
      ir = CFGIntermediateRep.getIR(PhaseOptions.getString(options, irOptionName));
      graphtype = CFGGraphType.getGraphType(PhaseOptions.getString(options, graphTypeOptionName));

      AltClassLoader.v().setAltClassPath(PhaseOptions.getString(options, altClassPathOptionName));
      AltClassLoader.v()
          .setAltClasses(new String[] { "soot.toolkits.graph.ArrayRefBlockGraph", "soot.toolkits.graph.Block",
              "soot.toolkits.graph.Block$AllMapTo", "soot.toolkits.graph.BlockGraph", "soot.toolkits.graph.BriefBlockGraph",
              "soot.toolkits.graph.BriefUnitGraph", "soot.toolkits.graph.CompleteBlockGraph",
              "soot.toolkits.graph.CompleteUnitGraph", "soot.toolkits.graph.TrapUnitGraph", "soot.toolkits.graph.UnitGraph",
              "soot.toolkits.graph.ZonedBlockGraph", });
    }
  }

  protected void print_cfg(Body body) {
    DirectedGraph<Unit> graph = graphtype.buildGraph(body);
    DotGraph canvas = graphtype.drawGraph(drawer, graph, body);

    String methodname = body.getMethod().getSubSignature();
    String classname = body.getMethod().getDeclaringClass().getName().replaceAll("\\$", "\\.");
    String filename = soot.SourceLocator.v().getOutputDir();
    if (filename.length() > 0) {
      filename = filename + java.io.File.separator;
    }
    filename = filename + classname + " " + methodname.replace(java.io.File.separatorChar, '.') + DotGraph.DOT_EXTENSION;

    logger.debug("Generate dot file in " + filename);
    canvas.plot(filename);
  }
}
