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
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
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

  protected final Map<Unit, List<UnitValueBoxPair>> unitToPairsAfter;
  protected final Map<Unit, List<UnitValueBoxPair>> unitToPairsBefore;
  protected final Map<Unit, Chain<EquivalentValue>> unitToEquivsAfter;
  protected final Map<Unit, Chain<EquivalentValue>> unitToEquivsBefore;

  /** Wrapper for AvailableExpressionsAnalysis. */
  public FastAvailableExpressions(Body b, SideEffectTester st) {
    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "] Finding available expressions...");
    }

    final Chain<Unit> units = b.getUnits();
    this.unitToPairsAfter = new HashMap<Unit, List<UnitValueBoxPair>>(units.size() * 2 + 1, 0.7f);
    this.unitToPairsBefore = new HashMap<Unit, List<UnitValueBoxPair>>(units.size() * 2 + 1, 0.7f);
    this.unitToEquivsAfter = new HashMap<Unit, Chain<EquivalentValue>>(units.size() * 2 + 1, 0.7f);
    this.unitToEquivsBefore = new HashMap<Unit, Chain<EquivalentValue>>(units.size() * 2 + 1, 0.7f);

    FastAvailableExpressionsAnalysis analysis
        = new FastAvailableExpressionsAnalysis(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(b), b.getMethod(), st);
    // Build unitToExprs map
    for (Unit s : units) {
      FlowSet<Value> set = analysis.getFlowBefore(s);
      if (set instanceof ToppedSet && ((ToppedSet<Value>) set).isTop()) {
        throw new RuntimeException("top! on " + s);
      }
      {
        List<UnitValueBoxPair> pairsBefore = new ArrayList<UnitValueBoxPair>();
        Chain<EquivalentValue> equivsBefore = new HashChain<EquivalentValue>();

        for (Value v : set) {
          Stmt containingStmt = (Stmt) analysis.rhsToContainingStmt.get(v);
          pairsBefore.add(new UnitValueBoxPair(containingStmt, ((AssignStmt) containingStmt).getRightOpBox()));
          EquivalentValue ev = new EquivalentValue(v);
          if (!equivsBefore.contains(ev)) {
            equivsBefore.add(ev);
          }
        }

        unitToPairsBefore.put(s, pairsBefore);
        unitToEquivsBefore.put(s, equivsBefore);
      }
      {
        List<UnitValueBoxPair> pairsAfter = new ArrayList<UnitValueBoxPair>();
        Chain<EquivalentValue> equivsAfter = new HashChain<EquivalentValue>();

        for (Value v : analysis.getFlowAfter(s)) {
          Stmt containingStmt = (Stmt) analysis.rhsToContainingStmt.get(v);
          pairsAfter.add(new UnitValueBoxPair(containingStmt, ((AssignStmt) containingStmt).getRightOpBox()));
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
  @Override
  public List<UnitValueBoxPair> getAvailablePairsBefore(Unit u) {
    return unitToPairsBefore.get(u);
  }

  /** Returns a Chain containing the EquivalentValue objects corresponding to expressions available before u. */
  @Override
  public Chain<EquivalentValue> getAvailableEquivsBefore(Unit u) {
    return unitToEquivsBefore.get(u);
  }

  /** Returns a List containing the EquivalentValue corresponding to expressions available after u. */
  @Override
  public List<UnitValueBoxPair> getAvailablePairsAfter(Unit u) {
    return unitToPairsAfter.get(u);
  }

  /** Returns a List containing the UnitValueBox pairs corresponding to expressions available after u. */
  @Override
  public Chain<EquivalentValue> getAvailableEquivsAfter(Unit u) {
    return unitToEquivsAfter.get(u);
  }
}
