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
 * Computes the earliest points for the given expressions.<br>
 * This basicly finds the highest point in the flow-graph where we can put each
 * computation (without introducing new computations on any path).<br>
 * More technically: A computation is earliest, if at the current point the
 * computation is down-safe, and if either:
 * <ul>
 * <li> any of the predecessors is not transparent, or 
 * <li> if any predecessors is not "safe" (ie. the insertion of
 * this computation would insert it on a path, where it was not before).
 * </ul><p>
 * Note that this computation is linear, as we don't need to find any
 * fixed-point.
 *
 * @see UpSafetyAnalysis
 * @see DownSafetyAnalysis
 */
public class EarliestnessComputation {
  private Map unitToEarliest;
  private SideEffectTester sideEffect;

  /**
   * given an UpSafetyAnalysis and a DownSafetyAnalysis, performs the
   * earliest-computation.<br>
   * the naive side-effect tester will be used to decide if a node is
   * transparent.
   *
   * @param unitGraph the Unitgraph we'll work on.
   * @param upSafe a UpSafetyAnalysis of <code>unitGraph</code>
   * @param downSafe a DownSafetyAnalysis of <code>unitGraph</code>
   */
  public EarliestnessComputation(UnitGraph unitGraph, UpSafetyAnalysis upSafe,
      DownSafetyAnalysis downSafe) {
    this(unitGraph, upSafe, downSafe, new NaiveSideEffectTester());
  }

  /**
   * given an UpSafetyAnalysis and a DownSafetyAnalysis, performs the
   * earliest-computation.<br>
   *
   * @param unitGraph the Unitgraph we'll work on.
   * @param upSafe a UpSafetyAnalysis of <code>unitGraph</code>
   * @param downSafe a DownSafetyAnalysis of <code>unitGraph</code>
   * @param sideEffect the SideEffectTester that will tell if a node is
   * transparent or not.
   */
  public EarliestnessComputation(UnitGraph unitGraph, UpSafetyAnalysis upSafe,
      DownSafetyAnalysis downSafe, SideEffectTester sideEffect) {
    this.sideEffect = sideEffect;

    unitToEarliest = new HashMap(unitGraph.size() + 1, 0.7f);

    Iterator unitIt = unitGraph.iterator();
    while (unitIt.hasNext()) {
      /* create a new Earliest-list for each unit */
      Unit currentUnit = (Unit)unitIt.next();
      List earliest = new ArrayList();
      unitToEarliest.put(currentUnit, earliest);

      /* get a copy of the downSafe-set at the current unit */
      FlowSet downSafeSet =
        (FlowSet)((FlowSet)downSafe.getFlowBefore(currentUnit)).clone();

      List predList = unitGraph.getPredsOf(currentUnit);
      if (predList.size() == 0) { //no predecessor
        /* we are obviously at the earliest position for any downsafe
         * computation */ 
	earliest.addAll(downSafeSet.toList());
      } else {
        Iterator predIt = predList.iterator();
        while(predIt.hasNext()) {
          Unit predecessor = (Unit)predIt.next();

          { /* if a predecessor is not transparent for a certain computation,
             * that is downsafe here, we can't push the computation further up,
             * and the earliest computation is before the current point.*/

            /* for each element in the downSafe-set, look if it passes through
             * the predecessor */
            List downSafeList = new ArrayList(downSafeSet.toList());
            Iterator downSafeIt = downSafeList.iterator();
            while (downSafeIt.hasNext()) {
              EquivalentValue equiVal = (EquivalentValue)downSafeIt.next();
              Value avail = equiVal.getValue();
              if (avail instanceof FieldRef) {
                if (sideEffect.unitCanWriteTo(predecessor, avail)) {
                  earliest.add(equiVal);
                  downSafeSet.remove(equiVal, downSafeSet);
                }
              } else {
                Iterator usesIt = avail.getUseBoxes().iterator();

                // iterate over uses in each avail.
                while (usesIt.hasNext()) {
                  Value use = ((ValueBox)usesIt.next()).getValue();

                  if (sideEffect.unitCanWriteTo(predecessor, use)) {
                    earliest.add(equiVal);
                    downSafeSet.remove(equiVal, downSafeSet);
                  }
                }
              }
            }
          }

          { /* look if one of the expressions is not upsafe and not downsafe in
             * one of the predecessors */
            List downSafeList = new ArrayList(downSafeSet.toList());
            Iterator downSafeIt = downSafeList.iterator();
            while (downSafeIt.hasNext()) {
              EquivalentValue equiVal = (EquivalentValue)downSafeIt.next();
              Value avail = equiVal.getValue();
              FlowSet preDown = (FlowSet)downSafe.getFlowBefore(predecessor);
              FlowSet preUp = (FlowSet)upSafe.getFlowBefore(predecessor);
              if (!preDown.contains(avail) && !preUp.contains(avail)) {
                    earliest.add(equiVal);
                    downSafeSet.remove(equiVal, downSafeSet);
              }
            }
          }
        }
      }
    }
  }

  /**
   * returns the set of expressions, that have their earliest computation just
   * before <code>node</code>.
   *
   * @param node a Unit of the flow-graph.
   * @return a List containing the expressions.
   */
  public List getEarliestBefore(Unit node) {
    return (List)unitToEarliest.get(node);
  }
}
