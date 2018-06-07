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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * Implements an available expressions analysis on local variables. The current implementation is slow but correct. A better
 * implementation would use an implicit universe and the kill rule would be computed on-the-fly for each statement.
 */
public class FastAvailableExpressionsAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<Value>> {
  SideEffectTester st;

  Map<Unit, FlowSet<Value>> unitToGenerateSet;
  Map<Unit, FlowSet<Value>> unitToPreserveSet;
  Map<Value, Unit> rhsToContainingStmt;

  FlowSet<Value> emptySet;

  public FastAvailableExpressionsAnalysis(DirectedGraph<Unit> dg, SootMethod m, SideEffectTester st) {
    super(dg);
    this.st = st;

    ExceptionalUnitGraph g = (ExceptionalUnitGraph) dg;
    // LocalDefs ld = new SmartLocalDefs(g, new SimpleLiveLocals(g));

    // maps an rhs to its containing stmt. object equality in rhs.
    rhsToContainingStmt = new HashMap<Value, Unit>();

    emptySet = new ToppedSet<Value>(new ArraySparseSet<Value>());

    // Create generate sets
    {
      unitToGenerateSet = new HashMap<Unit, FlowSet<Value>>(g.size() * 2 + 1, 0.7f);

      for (Unit s : g) {
        FlowSet<Value> genSet = emptySet.clone();
        // In Jimple, expressions only occur as the RHS of an
        // AssignStmt.
        if (s instanceof AssignStmt) {
          AssignStmt as = (AssignStmt) s;
          if (as.getRightOp() instanceof Expr || as.getRightOp() instanceof FieldRef) {
            Value gen = as.getRightOp();
            rhsToContainingStmt.put(gen, s);

            boolean cantAdd = false;
            if (gen instanceof NewExpr || gen instanceof NewArrayExpr || gen instanceof NewMultiArrayExpr) {
              cantAdd = true;
            }
            if (gen instanceof InvokeExpr) {
              cantAdd = true;
            }

            // Whee, double negative!
            if (!cantAdd) {
              genSet.add(gen, genSet);
            }
          }
        }

        unitToGenerateSet.put(s, genSet);
      }
    }

    doAnalysis();
  }

  protected FlowSet<Value> newInitialFlow() {
    FlowSet<Value> newSet = emptySet.clone();
    ((ToppedSet<Value>) newSet).setTop(true);
    return newSet;
  }

  protected FlowSet<Value> entryInitialFlow() {
    return emptySet.clone();
  }

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

    List<Value> l = new LinkedList<Value>(out.toList());

    // iterate over things (avail) in out set.
    for (Value avail : l) {
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

  protected void merge(FlowSet<Value> inSet1, FlowSet<Value> inSet2, FlowSet<Value> outSet) {
    inSet1.intersection(inSet2, outSet);
  }

  protected void copy(FlowSet<Value> sourceSet, FlowSet<Value> destSet) {
    sourceSet.copy(destSet);
  }
}
