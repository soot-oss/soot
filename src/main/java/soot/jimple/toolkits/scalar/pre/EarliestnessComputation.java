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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.EquivalentValue;
import soot.SideEffectTester;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;

/**
 * Computes the earliest points for the given expressions.<br>
 * This basically finds the highest point in the flow-graph where we can put each computation, without introducing new
 * computations on any path.<br>
 * More technically: A computation is earliest, if at the current point the computation is down-safe, and if either:
 * <ul>
 * <li>any of the predecessors is not transparent, or
 * <li>if any predecessors is not "safe" (ie. the insertion of this computation would insert it on a path, where it was not
 * before).
 * </ul>
 * <br>
 * Intuitively: If the one predecessor is not transparent, we can't push the computation further up. If one of the
 * predecessor is not safe, we would introduce a new computation on its path. Hence we can't push further up.
 * <p>
 * Note that this computation is linear in the number of units, as we don't need to find any fixed-point.
 *
 * @see UpSafetyAnalysis
 * @see DownSafetyAnalysis
 */
public class EarliestnessComputation {
  private Map<Unit, FlowSet<EquivalentValue>> unitToEarliest;

  /**
   * given an UpSafetyAnalysis and a DownSafetyAnalysis, performs the earliest-computation.<br>
   *
   * @param unitGraph
   *          the Unitgraph we'll work on.
   * @param upSafe
   *          a UpSafetyAnalysis of <code>unitGraph</code>
   * @param downSafe
   *          a DownSafetyAnalysis of <code>unitGraph</code>
   * @param sideEffect
   *          the SideEffectTester that will tell if a node is transparent or not.
   */
  public EarliestnessComputation(UnitGraph unitGraph, UpSafetyAnalysis upSafe, DownSafetyAnalysis downSafe,
      SideEffectTester sideEffect) {
    this(unitGraph, upSafe, downSafe, sideEffect, new ArraySparseSet<EquivalentValue>());
  }

  /**
   * given an UpSafetyAnalysis and a DownSafetyAnalysis, performs the earliest-computation.<br>
   * allows to share sets over multiple analyses (set-operations are usually more efficient, if the sets come from the same
   * source).
   *
   * @param unitGraph
   *          the Unitgraph we'll work on.
   * @param upSafe
   *          a UpSafetyAnalysis of <code>unitGraph</code>
   * @param downSafe
   *          a DownSafetyAnalysis of <code>unitGraph</code>
   * @param sideEffect
   *          the SideEffectTester that will tell if a node is transparent or not.
   * @param set
   *          the shared set.
   */
  public EarliestnessComputation(UnitGraph unitGraph, UpSafetyAnalysis upSafe, DownSafetyAnalysis downSafe,
      SideEffectTester sideEffect, FlowSet<EquivalentValue> set) {
    unitToEarliest = new HashMap<Unit, FlowSet<EquivalentValue>>(unitGraph.size() + 1, 0.7f);

    for (Unit currentUnit : unitGraph) {
      /* create a new Earliest-list for each unit */
      FlowSet<EquivalentValue> earliest = set.emptySet();
      unitToEarliest.put(currentUnit, earliest);

      /* get a copy of the downSafe-set at the current unit */
      FlowSet<EquivalentValue> downSafeSet = downSafe.getFlowBefore(currentUnit).clone();

      List<Unit> predList = unitGraph.getPredsOf(currentUnit);
      if (predList.isEmpty()) { // no predecessor
        /*
         * we are obviously at the earliest position for any downsafe computation
         */
        earliest.union(downSafeSet);
      } else {
        for (Unit predecessor : predList) {
          { /*
             * if a predecessor is not transparent for a certain computation, that is downsafe here, we can't push the
             * computation further up, and the earliest computation is before the current point.
             */

            /*
             * for each element in the downSafe-set, look if it passes through the predecessor
             */
            for (Iterator<EquivalentValue> downSafeIt = downSafeSet.iterator(); downSafeIt.hasNext();) {
              EquivalentValue equiVal = downSafeIt.next();
              Value avail = equiVal.getValue();

              if (avail instanceof FieldRef) {
                if (sideEffect.unitCanWriteTo(predecessor, avail)) {
                  earliest.add(equiVal);
                  downSafeIt.remove();
                }
              } else {
                // iterate over uses in each avail.
                for (ValueBox useBox : avail.getUseBoxes()) {
                  Value use = useBox.getValue();
                  if (sideEffect.unitCanWriteTo(predecessor, use)) {
                    earliest.add(equiVal);
                    downSafeIt.remove();
                    break;
                  }
                }
              }
            }
          }

          { /*
             * look if one of the expressions is not upsafe and not downsafe in one of the predecessors
             */
            for (Iterator<EquivalentValue> downSafeIt = downSafeSet.iterator(); downSafeIt.hasNext();) {
              EquivalentValue equiVal = downSafeIt.next();
              FlowSet<EquivalentValue> preDown = downSafe.getFlowBefore(predecessor);
              FlowSet<EquivalentValue> preUp = upSafe.getFlowBefore(predecessor);
              if (!preDown.contains(equiVal) && !preUp.contains(equiVal)) {
                earliest.add(equiVal);
                downSafeIt.remove();
              }
            }
          }
        }
      }
    }
  }

  /**
   * returns the FlowSet of expressions, that have their earliest computation just before <code>node</code>.
   *
   * @param node
   *          a Object of the flow-graph (in our case always a unit).
   * @return a FlowSet containing the expressions.
   */
  public FlowSet<EquivalentValue> getFlowBefore(Object node) {
    return unitToEarliest.get(node);
  }
}
