package soot.jimple.toolkits.annotation.purity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Antoine Mine
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.SootMethod;
import soot.SourceLocator;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

/**
 * Inter-procedural iterator skeleton for summary-based analysis
 *
 * A "summary" is an abstract element associated to each method that fully models the effect of calling the method. In a
 * summary-based analysis, the summary of a method can be computed using solely the summary of all methods it calls: the
 * summary does not depend upon the context in which a method is called. The inter-procedural analysis interacts with a
 * intra-procedural analysis that is able to compute the summary of one method, given the summary of all the method it calls.
 * The inter-procedural analysis calls the intra-procedural analysis in a reverse topological order of method dependencies to
 * resolve unknown summaries. It iterates over recursively dependant methods.
 *
 * Generally, the intra-procedural works by maintaining an abstract value that represent the effect of the method from its
 * entry point and up to the current point. At the entry point, this value is empty. The summary of the method is then the
 * merge of the abstract values at all its return points.
 *
 * You can provide off-the-shelf summaries for methods you do not which to analyse. Any method using these "filtered-out"
 * methods will use the off-the-shelf summary instead of performing an intra-procedural analysis. This is useful for native
 * methods, incremental analysis, or when you hand-made summary. Methods that are called solely by filtered-out ones will
 * never be analysed, effectively trimming the call-graph dependencies.
 *
 * This class tries to use the same abstract methods and data management policy as regular FlowAnalysis classes.
 *
 * @param <S>
 */
public abstract class AbstractInterproceduralAnalysis<S> {
  private static final Logger logger = LoggerFactory.getLogger(AbstractInterproceduralAnalysis.class);

  public static final boolean doCheck = false;

  protected final CallGraph cg; // analysed call-graph
  protected final DirectedGraph<SootMethod> dg; // filtered trimed call-graph
  protected final Map<SootMethod, S> data; // SootMethod -> summary
  protected final Map<SootMethod, Integer> order; // SootMethod -> topo order
  protected final Map<SootMethod, S> unanalysed; // SootMethod -> summary

  /**
   * The constructor performs some preprocessing, but you have to call doAnalysis to preform the real stuff.
   *
   * @param cg
   * @param filter
   * @param verbose
   * @param heads
   */
  public AbstractInterproceduralAnalysis(CallGraph cg, SootMethodFilter filter, Iterator<SootMethod> heads,
      boolean verbose) {
    this.cg = cg;

    System.out.println("this.cg = " + System.identityHashCode(this.cg));
    this.dg = new DirectedCallGraph(cg, filter, heads, verbose);
    this.data = new HashMap<SootMethod, S>();
    this.unanalysed = new HashMap<SootMethod, S>();

    // construct reverse pseudo topological order on filtered methods
    this.order = new HashMap<SootMethod, Integer>();

    int i = 0;
    for (SootMethod m : new PseudoTopologicalOrderer<SootMethod>().newList(dg, true)) {
      this.order.put(m, i);
      i++;
    }
  }

  /**
   * Initial summary value for analysed functions.
   *
   * @return
   */
  protected abstract S newInitialSummary();

  /**
   * Whenever the analyse requires the summary of a method you filtered-out, this function is called instead of
   * analyseMethod.
   *
   * <p>
   * Note: This function is called at most once per filtered-out method. It is the equivalent of entryInitialFlow!
   *
   * @param method
   *
   * @return
   */
  protected abstract S summaryOfUnanalysedMethod(SootMethod method);

  /**
   * Compute the summary for a method by analysing its body.
   *
   * Will be called only on methods not filtered-out.
   *
   * @param method
   *          is the method to be analysed
   * @param dst
   *          is where to put the computed method summary
   */
  protected abstract void analyseMethod(SootMethod method, S dst);

  /**
   * Interprocedural analysis will call applySummary repeatedly as a consequence to
   * {@link #analyseCall(Object, Stmt, Object)}, once for each possible target method of the {@code callStmt}, provided with
   * its summary.
   *
   * @param src
   *          summary valid before the call statement
   * @param callStmt
   *          a statement containing a InvokeStmt or InvokeExpr
   * @param summary
   *          summary of the possible target of callStmt considered here
   * @param dst
   *          where to put the result
   *
   * @see analyseCall
   */
  protected abstract void applySummary(S src, Stmt callStmt, S summary, S dst);

  /**
   * Merge in1 and in2 into out.
   *
   * Note: in1 or in2 can be aliased to out (e.g., analyseCall).
   *
   * @param in1
   * @param in2
   * @param out
   */
  protected abstract void merge(S in1, S in2, S out);

  /**
   * Copy src into dst.
   *
   * @param sr
   * @param dst
   */
  protected abstract void copy(S sr, S dst);

  /**
   * Called by drawAsOneDot to fill dot subgraph out with the contents of summary o.
   *
   * @param prefix
   *          gives you a unique string to prefix your node names and avoid name-clash
   * @param o
   * @param out
   */
  protected void fillDotGraph(String prefix, S o, DotGraph out) {
    throw new Error("abstract function AbstractInterproceduralAnalysis.fillDotGraph called but not implemented.");
  }

  /**
   * Analyse the call {@code callStmt} in the context {@code src}, and put the result into {@code dst}. For each possible
   * target of the call, this will get the summary for the target method (possibly
   * {@link #summaryOfUnanalysedMethod(SootMethod)}) and {@link #applySummary(Object, Stmt, Object, Object)}, then merge the
   * results into {@code dst} using {@link #merge(Object, Object, Object)}.
   *
   * @param src
   * @param dst
   * @param callStmt
   *
   * @see #summaryOfUnanalysedMethod(SootMethod)
   * @see #applySummary(Object, Stmt, Object, Object)
   */
  public void analyseCall(S src, Stmt callStmt, S dst) {
    S accum = newInitialSummary();
    copy(accum, dst);
    System.out.println("Edges out of " + callStmt + "...");
    for (Iterator<Edge> it = cg.edgesOutOf(callStmt); it.hasNext();) {
      Edge edge = it.next();
      SootMethod m = edge.tgt();
      System.out.println("\t-> " + m.getSignature());
      S elem;
      if (data.containsKey(m)) {
        // analysed method
        elem = data.get(m);
      } else {
        // unanalysed method
        if (!unanalysed.containsKey(m)) {
          unanalysed.put(m, summaryOfUnanalysedMethod(m));
        }
        elem = unanalysed.get(m);
      }
      applySummary(src, callStmt, elem, accum);
      merge(dst, accum, dst);
    }
  }

  /**
   * Dump the interprocedural analysis result as a graph. One node / subgraph for each analysed method that contains the
   * method summary, and call-to edges.
   *
   * Note: this graph does not show filtered-out methods for which a conservative summary was asked via
   * summaryOfUnanalysedMethod.
   *
   * @param name
   *          output filename
   *
   * @see fillDotGraph
   */
  public void drawAsOneDot(String name) {
    DotGraph dot = new DotGraph(name);
    dot.setGraphLabel(name);
    dot.setGraphAttribute("compound", "true");
    // dot.setGraphAttribute("rankdir","LR");
    int id = 0;
    Map<SootMethod, Integer> idmap = new HashMap<SootMethod, Integer>();

    // draw sub-graph cluster
    // draw sub-graph cluster
    for (SootMethod m : dg) {
      DotGraph sub = dot.createSubGraph("cluster" + id);
      DotGraphNode label = sub.drawNode("head" + id);
      idmap.put(m, id);
      sub.setGraphLabel("");
      label.setLabel("(" + order.get(m) + ") " + m.toString());
      label.setAttribute("fontsize", "18");
      label.setShape("box");
      if (data.containsKey(m)) {
        fillDotGraph("X" + id, data.get(m), sub);
      }
      id++;
    }

    // connect edges
    for (SootMethod m : dg) {
      for (SootMethod mm : dg.getSuccsOf(m)) {
        DotGraphEdge edge = dot.drawEdge("head" + idmap.get(m), "head" + idmap.get(mm));
        edge.setAttribute("ltail", "cluster" + idmap.get(m));
        edge.setAttribute("lhead", "cluster" + idmap.get(mm));
      }
    }

    File f = new File(SourceLocator.v().getOutputDir(), name + DotGraph.DOT_EXTENSION);
    dot.plot(f.getPath());
  }

  /**
   * Dump the each summary computed by the interprocedural analysis as a separate graph.
   *
   * @param prefix
   *          is prepended before method name in output filename
   * @param drawUnanalysed
   *          do you also want info for the unanalysed methods required by the analysis via summaryOfUnanalysedMethod ?
   *
   * @see fillDotGraph
   */
  public void drawAsManyDot(String prefix, boolean drawUnanalysed) {
    for (SootMethod m : data.keySet()) {
      DotGraph dot = new DotGraph(m.toString());
      dot.setGraphLabel(m.toString());
      fillDotGraph("X", data.get(m), dot);
      File f = new File(SourceLocator.v().getOutputDir(), prefix + m.toString() + DotGraph.DOT_EXTENSION);
      dot.plot(f.getPath());
    }

    if (drawUnanalysed) {
      for (SootMethod m : unanalysed.keySet()) {
        DotGraph dot = new DotGraph(m.toString());
        dot.setGraphLabel(m.toString() + " (unanalysed)");
        fillDotGraph("X", unanalysed.get(m), dot);
        File f = new File(SourceLocator.v().getOutputDir(), prefix + m.toString() + "_u" + DotGraph.DOT_EXTENSION);
        dot.plot(f.getPath());
      }
    }
  }

  /**
   * Query the analysis result.
   *
   * @param m
   *
   * @return
   */
  public S getSummaryFor(SootMethod m) {
    if (data.containsKey(m)) {
      return data.get(m);
    }
    if (unanalysed.containsKey(m)) {
      return unanalysed.get(m);
    }
    return newInitialSummary();
  }

  /**
   * Get an iterator over the list of SootMethod with an associated summary. (Does not contain filtered-out or native
   * methods.)
   *
   * @return
   */
  public Iterator<SootMethod> getAnalysedMethods() {
    return data.keySet().iterator();
  }

  /**
   * Carry out the analysis.
   *
   * Call this from your InterproceduralAnalysis constructor, just after super(cg). Then , you will be able to call
   * drawAsDot, for instance.
   *
   * @param verbose
   */
  protected void doAnalysis(boolean verbose) {
    // queue class
    class IntComparator implements Comparator<SootMethod> {

      @Override
      public int compare(SootMethod o1, SootMethod o2) {
        return order.get(o1) - order.get(o2);
      }
    }

    SortedSet<SootMethod> queue = new TreeSet<SootMethod>(new IntComparator());

    // init
    for (SootMethod o : order.keySet()) {
      data.put(o, newInitialSummary());
      queue.add(o);
    }

    Map<SootMethod, Integer> nb = new HashMap<SootMethod, Integer>(); // only for debug pretty-printing

    // fixpoint iterations
    while (!queue.isEmpty()) {
      SootMethod m = queue.first();
      queue.remove(m);
      S newSummary = newInitialSummary();
      S oldSummary = data.get(m);

      if (nb.containsKey(m)) {
        nb.put(m, nb.get(m) + 1);
      } else {
        nb.put(m, 1);
      }
      if (verbose) {
        logger.debug(" |- processing " + m.toString() + " (" + nb.get(m) + "-st time)");
      }

      analyseMethod(m, newSummary);
      if (!oldSummary.equals(newSummary)) {
        // summary for m changed!
        data.put(m, newSummary);
        queue.addAll(dg.getPredsOf(m));
      }
    }

    // fixpoint verification
    if (doCheck) {
      for (SootMethod m : order.keySet()) {
        S newSummary = newInitialSummary();
        S oldSummary = data.get(m);
        analyseMethod(m, newSummary);
        if (!oldSummary.equals(newSummary)) {
          logger.debug("inter-procedural fixpoint not reached for method " + m.toString());
          DotGraph gm = new DotGraph("false_fixpoint");
          DotGraph gmm = new DotGraph("next_iterate");
          gm.setGraphLabel("false fixpoint: " + m.toString());
          gmm.setGraphLabel("fixpoint next iterate: " + m.toString());
          fillDotGraph("", oldSummary, gm);
          fillDotGraph("", newSummary, gmm);
          gm.plot(m.toString() + "_false_fixpoint.dot");
          gmm.plot(m.toString() + "_false_fixpoint_next.dot");
          throw new Error("AbstractInterproceduralAnalysis sanity check failed!!!");
        }
      }
    }
  }
}
