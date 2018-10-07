package soot.jimple.toolkits.scalar.pre;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
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

import java.util.HashMap;
import java.util.Map;

import soot.EquivalentValue;
import soot.Unit;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArrayPackedSet;
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.CollectionFlowUniverse;
import soot.toolkits.scalar.FlowSet;

/**
 * Performs a Latest-Computation on the given graph. a computation is latest, when we can't delay it anymore. This uses the
 * Delayability-analysis. More precise: The delayability-analysis says us already until which point we can delay a
 * computation from the earliest computation-point. We just have to search for points, where there's a computation, or, where
 * we can't delay the computation to one of the successors.
 */
public class LatestComputation {
  private Map<Unit, FlowSet<EquivalentValue>> unitToLatest;

  /**
   * given a DelayabilityAnalysis and the computations of each unit, calculates the latest computation-point for each
   * expression. the <code>equivRhsMap</code> could be calculated on the fly, but it is <b>very</b> likely that it already
   * exists (as similar maps are used for calculating Earliestness, Delayed,...
   *
   * @param dg
   *          a ExceptionalUnitGraph
   * @param delayed
   *          the delayability-analysis of the same graph.
   * @param equivRhsMap
   *          all computations of the graph
   */
  public LatestComputation(UnitGraph unitGraph, DelayabilityAnalysis delayed, Map<Unit, EquivalentValue> equivRhsMap) {
    this(unitGraph, delayed, equivRhsMap,
        new ArrayPackedSet<EquivalentValue>(new CollectionFlowUniverse<EquivalentValue>(equivRhsMap.values())));
  }

  /**
   * given a DelayabilityAnalysis and the computations of each unit, calculates the latest computation-point for each
   * expression.<br>
   * the <code>equivRhsMap</code> could be calculated on the fly, but it is <b>very</b> likely that it already exists (as
   * similar maps are used for calculating Earliestness, Delayed,...<br>
   * the shared set allows more efficient set-operations, when they the computation is merged with other
   * analyses/computations.
   *
   * @param dg
   *          a ExceptionalUnitGraph
   * @param delayed
   *          the delayability-analysis of the same graph.
   * @param equivRhsMap
   *          all computations of the graph
   * @param set
   *          the shared flowSet
   */
  public LatestComputation(UnitGraph unitGraph, DelayabilityAnalysis delayed, Map<Unit, EquivalentValue> equivRhsMap,
      BoundedFlowSet<EquivalentValue> set) {
    unitToLatest = new HashMap<Unit, FlowSet<EquivalentValue>>(unitGraph.size() + 1, 0.7f);

    for (Unit currentUnit : unitGraph) {
      /* create a new Earliest-list for each unit */
      /*
       * basically the latest-set is: (delayed) INTERSECT (comp UNION (UNION_successors ~Delayed)) = (delayed) MINUS
       * ((INTERSECTION_successors Delayed) MINUS comp).
       */

      FlowSet<EquivalentValue> delaySet = delayed.getFlowBefore(currentUnit);

      /* Calculate (INTERSECTION_successors Delayed) */
      FlowSet<EquivalentValue> succCompSet = set.topSet();
      for (Unit successor : unitGraph.getSuccsOf(currentUnit)) {
        succCompSet.intersection(delayed.getFlowBefore(successor), succCompSet);
      }
      /*
       * remove the computation of this set: succCompSet is then: ((INTERSECTION_successors Delayed) MINUS comp)
       */
      if (equivRhsMap.get(currentUnit) != null) {
        succCompSet.remove(equivRhsMap.get(currentUnit));
      }

      /* make the difference: */
      FlowSet<EquivalentValue> latest = delaySet.emptySet();
      delaySet.difference(succCompSet, latest);

      unitToLatest.put(currentUnit, latest);
    }
  }

  /**
   * returns the set of expressions, that have their latest computation just before <code>node</code>.
   *
   * @param node
   *          an Object of the flow-graph (in our case always a unit).
   * @return a FlowSet containing the expressions.
   */
  public FlowSet<EquivalentValue> getFlowBefore(Object node) {
    return unitToLatest.get(node);
  }
}
