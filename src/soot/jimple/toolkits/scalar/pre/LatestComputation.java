/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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
 * Performs a Latest-Computation on the given graph.<br>
 * a computation is latest, when we can't delay it anymore. This uses the
 * Delayability-analysis.<br>
 * More precise: The delayability-analysis says us already until which point we
 * can delay a computation from the earliest computation-point. We just have to
 * search for points, where there's a computation, or, where we can't delay the
 * computation to one of the succesors.
 */
public class LatestComputation {
  private Map unitToLatest;

  /**
   * given a DelayabilityAnalysis, the EarliestnessComputation and the
   * computations of each unit, calculates the latest computation-point for each
   * expression.<br>
   * the <code>equivRhsMap</code> could be calculated on the fly, but it is
   * <b>very</b> likely that it already exists (as similar maps are used for
   * calculating Earliestness, Delayed,...
   *
   * @param dg a CompleteUnitGraph
   * @param earliest the earliest-computation of the same graph.
   * @param delayed the delayability-analysis of the same graph.
   * @param equivRhsMap all computations of the graph
   */
  public LatestComputation(UnitGraph unitGraph, EarliestnessComputation
      earliest, DelayabilityAnalysis delayed, Map equivRhsMap) {
    unitToLatest = new HashMap(unitGraph.size() + 1, 0.7f);

    Iterator unitIt = unitGraph.iterator();
    while (unitIt.hasNext()) {
      /* create a new Earliest-list for each unit */
      Unit currentUnit = (Unit)unitIt.next();

      /* basically the latest-set is: 
       *  (delayed) * (computation + (SUM successors -Delayed))

      /* make a copy of the delayedSet */
      FlowSet delaySet =
        (FlowSet)((FlowSet)delayed.getFlowBefore(currentUnit)).clone();

      /* We'll calculate (SUM successors -Delayed) by 
       * -(INTER successors Delayed) */
      /* TODO we can't be sure, that we have a ArraySparseSet here! */
      /* FlowSet succSet = delayEarliestSet.emptySet() would be better */
      ToppedSet succCompSet = new ToppedSet(new ArraySparseSet());
      succCompSet.setTop(true);
      List succList = unitGraph.getSuccsOf(currentUnit);
      Iterator succIt = succList.iterator();
      while(succIt.hasNext()) {
        Unit successor = (Unit)succIt.next();
        succCompSet.intersection((FlowSet)delayed.getFlowBefore(successor),
            succCompSet);
      }
      /* remove the computation of this set: succCompSet is then:
       * -((Intersection successors Delayed) - comp) */
      if (equivRhsMap.get(currentUnit) != null)
        succCompSet.remove(equivRhsMap.get(currentUnit), succCompSet);

      /* make the difference: */
      FlowSet latest = delaySet; //just to have a nicer name:)
      delaySet.difference(succCompSet, latest);

      unitToLatest.put(currentUnit, latest);
    }
  }

  /**
   * returns the set of expressions, that have their latest computation just
   * before <code>node</code>.
   *
   * @param node a Unit of the flow-graph.
   * @return a FlowSet containing the expressions.
   */
  public FlowSet getLatestBefore(Unit node) {
    return (FlowSet)unitToLatest.get(node);
  }
}
