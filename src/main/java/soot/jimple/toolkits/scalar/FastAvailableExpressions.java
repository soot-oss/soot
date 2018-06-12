package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrick Lam
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
import soot.EquivalentValue;
import soot.SideEffectTester;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;
import soot.util.HashChain;

/**
 * Provides an user-interface for the AvailableExpressionsAnalysis class. Returns, for each statement, the list of
 * expressions available before and after it.
 */
public class FastAvailableExpressions implements AvailableExpressions {
  private static final Logger logger = LoggerFactory.getLogger(FastAvailableExpressions.class);
  Map<Unit, List<UnitValueBoxPair>> unitToPairsAfter;
  Map<Unit, List<UnitValueBoxPair>> unitToPairsBefore;
  Map<Unit, Chain<EquivalentValue>> unitToEquivsAfter;
  Map<Unit, Chain<EquivalentValue>> unitToEquivsBefore;

  /** Wrapper for AvailableExpressionsAnalysis. */
  public FastAvailableExpressions(Body b, SideEffectTester st) {
    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "] Finding available expressions...");
    }

    FastAvailableExpressionsAnalysis analysis
        = new FastAvailableExpressionsAnalysis(new ExceptionalUnitGraph(b), b.getMethod(), st);

    // Build unitToExprs map
    {
      unitToPairsAfter = new HashMap<Unit, List<UnitValueBoxPair>>(b.getUnits().size() * 2 + 1, 0.7f);
      unitToPairsBefore = new HashMap<Unit, List<UnitValueBoxPair>>(b.getUnits().size() * 2 + 1, 0.7f);
      unitToEquivsAfter = new HashMap<Unit, Chain<EquivalentValue>>(b.getUnits().size() * 2 + 1, 0.7f);
      unitToEquivsBefore = new HashMap<Unit, Chain<EquivalentValue>>(b.getUnits().size() * 2 + 1, 0.7f);

      for (Unit s : b.getUnits()) {
        FlowSet<Value> set = analysis.getFlowBefore(s);

        List<UnitValueBoxPair> pairsBefore = new ArrayList<UnitValueBoxPair>();
        List<UnitValueBoxPair> pairsAfter = new ArrayList<UnitValueBoxPair>();

        Chain<EquivalentValue> equivsBefore = new HashChain<EquivalentValue>();
        Chain<EquivalentValue> equivsAfter = new HashChain<EquivalentValue>();

        if (set instanceof ToppedSet && ((ToppedSet<Value>) set).isTop()) {
          throw new RuntimeException("top! on " + s);
        }

        for (Value v : set) {
          Stmt containingStmt = (Stmt) analysis.rhsToContainingStmt.get(v);
          UnitValueBoxPair p = new UnitValueBoxPair(containingStmt, ((AssignStmt) containingStmt).getRightOpBox());
          pairsBefore.add(p);

          EquivalentValue ev = new EquivalentValue(v);
          if (!equivsBefore.contains(ev)) {
            equivsBefore.add(ev);
          }
        }

        unitToPairsBefore.put(s, pairsBefore);
        unitToEquivsBefore.put(s, equivsBefore);

        for (Value v : analysis.getFlowAfter(s)) {
          Stmt containingStmt = (Stmt) analysis.rhsToContainingStmt.get(v);
          UnitValueBoxPair p = new UnitValueBoxPair(containingStmt, ((AssignStmt) containingStmt).getRightOpBox());
          pairsAfter.add(p);

          EquivalentValue ev = new EquivalentValue(v);
          if (!equivsAfter.contains(ev)) {
            equivsAfter.add(ev);
          }
        }

        unitToPairsAfter.put(s, pairsAfter);
        unitToEquivsAfter.put(s, equivsAfter);
      }
    }

    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     Found available expressions...");
    }
  }

  /** Returns a List containing the UnitValueBox pairs corresponding to expressions available before u. */
  public List<UnitValueBoxPair> getAvailablePairsBefore(Unit u) {
    return unitToPairsBefore.get(u);
  }

  /** Returns a Chain containing the EquivalentValue objects corresponding to expressions available before u. */
  public Chain<EquivalentValue> getAvailableEquivsBefore(Unit u) {
    return unitToEquivsBefore.get(u);
  }

  /** Returns a List containing the EquivalentValue corresponding to expressions available after u. */
  public List<UnitValueBoxPair> getAvailablePairsAfter(Unit u) {
    return unitToPairsAfter.get(u);
  }

  /** Returns a List containing the UnitValueBox pairs corresponding to expressions available after u. */
  public Chain<EquivalentValue> getAvailableEquivsAfter(Unit u) {
    return unitToEquivsAfter.get(u);
  }
}
