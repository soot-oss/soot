/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-2002.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.scalar.pre;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.scalar.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** 
 * Performs a Latest-Computation on the given graph.
 * a computation is latest, when we can't delay it anymore. This uses the
 * Delayability-analysis.
 * More precise: The delayability-analysis says us already until which point we
 * can delay a computation from the earliest computation-point. We just have to
 * search for points, where there's a computation, or, where we can't delay the
 * computation to one of the succesors.
 */
public class LatestComputation {
  private Map unitToLatest;


  /**
   * given a DelayabilityAnalysis and the computations of each unit, calculates
   * the latest computation-point for each expression.
   * the <code>equivRhsMap</code> could be calculated on the fly, but it is
   * <b>very</b> likely that it already exists (as similar maps are used for
   * calculating Earliestness, Delayed,...
   *
   * @param dg a ExceptionalUnitGraph
   * @param delayed the delayability-analysis of the same graph.
   * @param equivRhsMap all computations of the graph
   */
  public LatestComputation(UnitGraph unitGraph, DelayabilityAnalysis delayed,
                           Map equivRhsMap) {
    this(unitGraph, delayed, equivRhsMap, new
      ArrayPackedSet(new CollectionFlowUniverse(equivRhsMap.values())));
  }

  /**
   * given a DelayabilityAnalysis and the computations of each unit, calculates
   * the latest computation-point for each expression.<br>
   * the <code>equivRhsMap</code> could be calculated on the fly, but it is
   * <b>very</b> likely that it already exists (as similar maps are used for
   * calculating Earliestness, Delayed,...<br>
   * the shared set allows more efficient set-operations, when they the
   * computation is merged with other analyses/computations.
   *
   * @param dg a ExceptionalUnitGraph
   * @param delayed the delayability-analysis of the same graph.
   * @param equivRhsMap all computations of the graph
   * @param set the shared flowSet
   */
  public LatestComputation(UnitGraph unitGraph, DelayabilityAnalysis delayed,
                           Map equivRhsMap, BoundedFlowSet set) {
    unitToLatest = new HashMap(unitGraph.size() + 1, 0.7f);

    Iterator unitIt = unitGraph.iterator();
    while (unitIt.hasNext()) {
      /* create a new Earliest-list for each unit */
      Unit currentUnit = (Unit)unitIt.next();

      /* basically the latest-set is: 
       * (delayed) INTERSECT (comp UNION (UNION_successors ~Delayed)) =
       * (delayed) MINUS ((INTERSECTION_successors Delayed) MINUS comp).
       */

      FlowSet delaySet = (FlowSet)delayed.getFlowBefore(currentUnit);

      /* Calculate (INTERSECTION_successors Delayed) */
      FlowSet succCompSet = (FlowSet)set.topSet();
      List succList = unitGraph.getSuccsOf(currentUnit);
      Iterator succIt = succList.iterator();
      while(succIt.hasNext()) {
        Unit successor = (Unit)succIt.next();
        succCompSet.intersection((FlowSet)delayed.getFlowBefore(successor),
            succCompSet);
      }
      /* remove the computation of this set: succCompSet is then:
       * ((INTERSECTION_successors Delayed) MINUS comp) */
      if (equivRhsMap.get(currentUnit) != null)
        succCompSet.remove(equivRhsMap.get(currentUnit));

      /* make the difference: */
      FlowSet latest = (FlowSet)delaySet.emptySet();
      delaySet.difference(succCompSet, latest);

      unitToLatest.put(currentUnit, latest);
    }
  }

  /**
   * returns the set of expressions, that have their latest computation just
   * before <code>node</code>.
   *
   * @param node an Object of the flow-graph (in our case always a unit).
   * @return a FlowSet containing the expressions.
   */
  public Object getFlowBefore(Object node) {
    return unitToLatest.get(node);
  }
}
