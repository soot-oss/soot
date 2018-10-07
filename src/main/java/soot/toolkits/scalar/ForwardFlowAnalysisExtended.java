package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Timers;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.Orderer;
import soot.toolkits.graph.PseudoTopologicalOrderer;

/**
 * Abstract class that provides a fixed-point iteration for forward flow analyses that need to distinguish between the
 * different successors of a unit in an exceptional unit graph.
 *
 * Note that this class does not extend FlowAnalysis as it has a different definition of a flow function (namely one that
 * does not only include the current unit, but also the successor). For the same reason, it does not integrate into the
 * interactive analysis infrastructure of Soot either.
 *
 * @author Steven Arzt
 *
 */
public abstract class ForwardFlowAnalysisExtended<N, A> {
  /** Maps graph nodes to IN sets. */
  protected Map<N, Map<N, A>> unitToBeforeFlow;

  /** Maps graph nodes to OUT sets. */
  protected Map<N, Map<N, A>> unitToAfterFlow;

  /** The graph being analysed. */
  protected DirectedGraph<N> graph;

  /**
   * Construct the analysis from a DirectedGraph representation of a Body.
   */
  public ForwardFlowAnalysisExtended(DirectedGraph<N> graph) {
    this.graph = graph;
    this.unitToBeforeFlow = new IdentityHashMap<N, Map<N, A>>(graph.size() * 2 + 1);
    this.unitToAfterFlow = new IdentityHashMap<N, Map<N, A>>(graph.size() * 2 + 1);
  }

  /**
   * Default implementation constructing a PseudoTopologicalOrderer.
   *
   * @return an Orderer to order the nodes for the fixed-point iteration
   */
  protected Orderer<N> constructOrderer() {
    return new PseudoTopologicalOrderer<N>();
  }

  /**
   * Returns the flow object corresponding to the initial values for each graph node.
   */
  protected abstract A newInitialFlow();

  /**
   * Returns the initial flow value for entry/exit graph nodes.
   */
  protected abstract A entryInitialFlow();

  /** Creates a copy of the <code>source</code> flow object in <code>dest</code>. */
  protected abstract void copy(A source, A dest);

  /**
   * Compute the merge of the <code>in1</code> and <code>in2</code> sets, putting the result into <code>out</code>. The
   * behavior of this function depends on the implementation ( it may be necessary to check whether <code>in1</code> and
   * <code>in2</code> are equal or aliased ). Used by the doAnalysis method.
   */
  protected abstract void merge(A in1, A in2, A out);

  /**
   * Merges in1 and in2 into out, just before node succNode. By default, this method just calls merge(A,A,A), ignoring the
   * node.
   */
  protected void merge(N succNode, A in1, A in2, A out) {
    merge(in1, in2, out);
  }

  /**
   * Merges in into inout, just before node succNode.
   */
  protected void mergeInto(N succNode, A inout, A in) {
    A tmp = newInitialFlow();
    merge(succNode, inout, in, tmp);
    copy(tmp, inout);
  }

  public A getFromMap(Map<N, Map<N, A>> map, N s, N t) {
    Map<N, A> m = map.get(s);
    if (m == null) {
      return null;
    }
    return m.get(t);
  }

  public void putToMap(Map<N, Map<N, A>> map, N s, N t, A val) {
    Map<N, A> m = map.get(s);
    if (m == null) {
      m = new IdentityHashMap<N, A>();
      map.put(s, m);
    }
    m.put(t, val);
  }

  protected void doAnalysis() {
    List<N> orderedUnits = constructOrderer().newList(graph, false);

    final int n = orderedUnits.size();
    BitSet head = new BitSet();
    BitSet work = new BitSet(n);
    work.set(0, n);

    final Map<N, Integer> index = new IdentityHashMap<N, Integer>(n * 2 + 1);
    {
      int i = 0;
      for (N s : orderedUnits) {
        index.put(s, i++);

        // Set initial Flows
        for (N v : graph.getSuccsOf(s)) {
          putToMap(unitToBeforeFlow, s, v, newInitialFlow());
          putToMap(unitToAfterFlow, s, v, newInitialFlow());
        }
      }
    }

    // Feng Qian: March 07, 2002
    // Set initial values for entry points
    for (N s : graph.getHeads()) {
      head.set(index.get(s));

      // this is a forward flow analysis
      for (N v : graph.getSuccsOf(s)) {
        putToMap(unitToBeforeFlow, s, v, entryInitialFlow());
      }
    }

    int numComputations = 0;

    // Perform fixed point flow analysis
    {
      A previousFlow = newInitialFlow();

      for (int i = work.nextSetBit(0); i >= 0; i = work.nextSetBit(i + 1)) {
        work.clear(i);
        N s = orderedUnits.get(i);

        // For all successors, compute the flow function
        int k = i;
        for (N v : graph.getSuccsOf(s)) {
          A beforeFlow = getFromMap(unitToBeforeFlow, s, v);
          A afterFlow = getFromMap(unitToAfterFlow, s, v);
          copy(afterFlow, previousFlow);

          // Compute and store beforeFlow
          {
            final Iterator<N> it = graph.getPredsOf(s).iterator();

            if (it.hasNext()) {
              copy(getFromMap(unitToAfterFlow, it.next(), s), beforeFlow);

              while (it.hasNext()) {
                mergeInto(s, beforeFlow, getFromMap(unitToAfterFlow, it.next(), s));
              }

              if (head.get(k)) {
                mergeInto(s, beforeFlow, entryInitialFlow());
              }
            }
          }

          // Compute afterFlow and store it.
          flowThrough(beforeFlow, s, v, afterFlow);

          boolean hasChanged = !previousFlow.equals(afterFlow);

          // Update queue appropriately
          if (hasChanged) {
            int j = index.get(v);
            work.set(j);
            i = Math.min(i, j - 1);
          }

          numComputations++;
        }
      }
    }

    Timers.v().totalFlowNodes += n;
    Timers.v().totalFlowComputations += numComputations;
  }

  protected abstract void flowThrough(A in, N cur, N next, A out);

  /** Accessor function returning value of IN set for s. */
  public A getFlowBefore(N s) {
    final Iterator<N> it = graph.getPredsOf(s).iterator();
    A beforeFlow = null;

    if (it.hasNext()) {
      beforeFlow = getFromMap(unitToAfterFlow, it.next(), s);
      while (it.hasNext()) {
        mergeInto(s, beforeFlow, getFromMap(unitToAfterFlow, it.next(), s));
      }
    }
    return beforeFlow;
  }

}
