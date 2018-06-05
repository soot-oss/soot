package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.toolkits.scalar.ArrayPackedSet;
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.CollectionFlowUniverse;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.FlowUniverse;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * Wrapper class for a simple dominators analysis based on a simple flow analysis algorithm. Works with any DirectedGraph
 * with a single head.
 *
 * @author Navindra Umanee
 **/
public class SimpleDominatorsFinder<N> implements DominatorsFinder<N> {
  private static final Logger logger = LoggerFactory.getLogger(SimpleDominatorsFinder.class);
  protected DirectedGraph<N> graph;
  protected Map<N, FlowSet<N>> nodeToDominators;

  /**
   * Compute dominators for provided singled-headed directed graph.
   **/
  public SimpleDominatorsFinder(DirectedGraph<N> graph) {
    // if(Options.v().verbose())
    // logger.debug("[" + graph.getBody().getMethod().getName() +
    // "] Finding Dominators...");

    this.graph = graph;
    SimpleDominatorsAnalysis<N> analysis = new SimpleDominatorsAnalysis<N>(graph);

    // build node to dominators map
    {
      nodeToDominators = new HashMap<N, FlowSet<N>>(graph.size() * 2 + 1, 0.7f);

      for (Iterator<N> nodeIt = graph.iterator(); nodeIt.hasNext();) {
        N node = nodeIt.next();
        FlowSet<N> set = analysis.getFlowAfter(node);
        nodeToDominators.put(node, set);
      }
    }
  }

  public DirectedGraph<N> getGraph() {
    return graph;
  }

  public List<N> getDominators(N node) {
    // non-backed list since FlowSet is an ArrayPackedFlowSet
    return nodeToDominators.get(node).toList();
  }

  public N getImmediateDominator(N node) {
    // root node
    if (getGraph().getHeads().contains(node)) {
      return null;
    }

    // avoid the creation of temp-lists
    FlowSet<N> head = (FlowSet<N>) nodeToDominators.get(node).clone();
    head.remove(node);

    for (N dominator : head) {
      if (nodeToDominators.get(dominator).isSubSet(head)) {
        return dominator;
      }
    }

    return null;
  }

  public boolean isDominatedBy(N node, N dominator) {
    // avoid the creation of temp-lists
    return nodeToDominators.get(node).contains(dominator);
  }

  public boolean isDominatedByAll(N node, Collection<N> dominators) {
    FlowSet<N> f = nodeToDominators.get(node);
    for (N n : dominators) {
      if (!f.contains(n)) {
        return false;
      }
    }
    return true;
  }
}

/**
 * Calculate dominators for basic blocks.
 * <p>
 * Uses the algorithm contained in Dragon book, pg. 670-1.
 *
 * <pre>
 *       D(n0) := { n0 }
 *       for n in N - { n0 } do D(n) := N;
 *       while changes to any D(n) occur do
 *         for n in N - {n0} do
 *             D(n) := {n} U (intersect of D(p) over all predecessors p of n)
 * </pre>
 **/
class SimpleDominatorsAnalysis<N> extends ForwardFlowAnalysis<N, FlowSet<N>> {
  private FlowSet<N> emptySet;
  private BoundedFlowSet<N> fullSet;

  SimpleDominatorsAnalysis(DirectedGraph<N> graph) {
    super(graph);

    // define empty set, with proper universe for complementation

    List<N> nodes = new ArrayList<N>(graph.size());

    for (N n : graph) {
      nodes.add(n);
    }

    FlowUniverse<N> nodeUniverse = new CollectionFlowUniverse<N>(nodes);
    emptySet = new ArrayPackedSet<N>(nodeUniverse);
    fullSet = (BoundedFlowSet<N>) emptySet.clone();
    fullSet.complement();

    doAnalysis();
  }

  /**
   * All OUTs are initialized to the full set of definitions OUT(Start) is tweaked in customizeInitialFlowGraph.
   **/
  @Override
  protected FlowSet<N> newInitialFlow() {
    return (FlowSet<N>) fullSet.clone();
  }

  /**
   * OUT(Start) contains all head nodes at initialization time.
   **/
  @Override
  protected FlowSet<N> entryInitialFlow() {
    FlowSet<N> initSet = (FlowSet<N>) emptySet.clone();
    for (N h : graph.getHeads()) {
      initSet.add(h);
    }
    return initSet;
  }

  /**
   * We compute out straightforwardly.
   **/
  @Override
  protected void flowThrough(FlowSet<N> in, N block, FlowSet<N> out) {
    // Perform generation
    in.copy(out);
    out.add(block);
  }

  /**
   * All paths == Intersection.
   **/
  @Override
  protected void merge(FlowSet<N> in1, FlowSet<N> in2, FlowSet<N> out) {
    in1.intersection(in2, out);
  }

  @Override
  protected void mergeInto(N block, FlowSet<N> inout, FlowSet<N> in) {
    inout.intersection(in);
  }

  @Override
  protected void copy(FlowSet<N> source, FlowSet<N> dest) {
    source.copy(dest);
  }
}
