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
import java.util.Map;

import soot.SideEffectTester;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Expr;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * Implements an available expressions analysis on local variables. The current implementation is slow but correct. A better
 * implementation would use an implicit universe and the kill rule would be computed on-the-fly for each statement.
 */
public class FastAvailableExpressionsAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<Value>> {

  protected final FlowSet<Value> emptySet = new ToppedSet<Value>(new ArraySparseSet<Value>());

  protected final SideEffectTester st;
  /** maps an rhs to its containing stmt. object equality in rhs. */
  protected final Map<Value, Unit> rhsToContainingStmt;
  protected final Map<Unit, FlowSet<Value>> unitToGenerateSet;

  public FastAvailableExpressionsAnalysis(DirectedGraph<Unit> dg, SootMethod m, SideEffectTester st) {
    super(dg);
    this.st = st;
    this.rhsToContainingStmt = new HashMap<Value, Unit>();
    this.unitToGenerateSet = new HashMap<Unit, FlowSet<Value>>(dg.size() * 2 + 1, 0.7f);

    // Create generate sets
    for (Unit s : dg) {
      FlowSet<Value> genSet = this.emptySet.clone();
      // In Jimple, expressions only occur as the RHS of an
      // AssignStmt.
      if (s instanceof AssignStmt) {
        Value gen = ((AssignStmt) s).getRightOp();
        if (gen instanceof Expr || gen instanceof FieldRef) {
          this.rhsToContainingStmt.put(gen, s);

          if (!(gen instanceof NewExpr || gen instanceof NewArrayExpr || gen instanceof NewMultiArrayExpr
              || gen instanceof InvokeExpr)) {
            genSet.add(gen, genSet);
          }
        }
      }

      this.unitToGenerateSet.put(s, genSet);
    }

    doAnalysis();
  }

  @Override
  protected FlowSet<Value> newInitialFlow() {
    FlowSet<Value> newSet = emptySet.clone();
    ((ToppedSet<Value>) newSet).setTop(true);
    return newSet;
  }

  @Override
  protected FlowSet<Value> entryInitialFlow() {
    return emptySet.clone();
  }

  @Override
  protected void flowThrough(FlowSet<Value> in, Unit u, FlowSet<Value> out) {
    in.copy(out);
    if (((ToppedSet<Value>) in).isTop()) {
      return;
    }

    // Perform generation
    out.union(unitToGenerateSet.get(u), out);

    // Perform kill.
    if (((ToppedSet<Value>) out).isTop()) {
      throw new RuntimeException("trying to kill on topped set!");
    }

    // iterate over things (avail) in out set.
    for (Value avail : new ArrayList<Value>(out.toList())) {
      if (avail instanceof FieldRef) {
        if (st.unitCanWriteTo(u, avail)) {
          out.remove(avail, out);
        }
      } else {
        for (ValueBox vb : avail.getUseBoxes()) {
          Value use = vb.getValue();
          if (st.unitCanWriteTo(u, use)) {
            out.remove(avail, out);
          }
        }
      }
    }
  }

  @Override
  protected void merge(FlowSet<Value> inSet1, FlowSet<Value> inSet2, FlowSet<Value> outSet) {
    inSet1.intersection(inSet2, outSet);
  }

  @Override
  protected void copy(FlowSet<Value> sourceSet, FlowSet<Value> destSet) {
    sourceSet.copy(destSet);
  }
}
