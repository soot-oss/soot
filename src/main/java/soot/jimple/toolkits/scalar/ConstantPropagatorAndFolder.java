package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Phong Co
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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.RefType;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.NullConstant;
import soot.jimple.NumericConstant;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.Orderer;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.LocalDefs;

/**
 * Does constant propagation and folding. Constant folding is the compile-time evaluation of constant expressions (i.e. 2 *
 * 3).
 */
public class ConstantPropagatorAndFolder extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(ConstantPropagatorAndFolder.class);

  public ConstantPropagatorAndFolder(Singletons.Global g) {
  }

  public static ConstantPropagatorAndFolder v() {
    return G.v().soot_jimple_toolkits_scalar_ConstantPropagatorAndFolder();
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    int numFolded = 0;
    int numPropagated = 0;

    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "] Propagating and folding constants...");
    }

    UnitGraph g = new ExceptionalUnitGraph(b);
    LocalDefs localDefs = LocalDefs.Factory.newLocalDefs(g);

    // Perform a constant/local propagation pass.
    Orderer<Unit> orderer = new PseudoTopologicalOrderer<Unit>();

    // go through each use box in each statement
    for (Unit u : orderer.newList(g, false)) {

      // propagation pass
      for (ValueBox useBox : u.getUseBoxes()) {
        Value value = useBox.getValue();
        if (value instanceof Local) {
          Local local = (Local) value;
          List<Unit> defsOfUse = localDefs.getDefsOfAt(local, u);
          if (defsOfUse.size() == 1) {
            DefinitionStmt defStmt = (DefinitionStmt) defsOfUse.get(0);
            Value rhs = defStmt.getRightOp();
            if (rhs instanceof NumericConstant || rhs instanceof StringConstant || rhs instanceof NullConstant) {
              if (useBox.canContainValue(rhs)) {
                useBox.setValue(rhs);
                numPropagated++;
              }
            } else if (rhs instanceof CastExpr) {
              CastExpr ce = (CastExpr) rhs;
              if (ce.getCastType() instanceof RefType && ce.getOp() instanceof NullConstant) {
                defStmt.getRightOpBox().setValue(NullConstant.v());
                numPropagated++;
              }
            }
          }
        }
      }

      // folding pass
      for (ValueBox useBox : u.getUseBoxes()) {
        Value value = useBox.getValue();
        if (!(value instanceof Constant)) {
          if (Evaluator.isValueConstantValued(value)) {
            Value constValue = Evaluator.getConstantValueOf(value);
            if (useBox.canContainValue(constValue)) {
              useBox.setValue(constValue);
              numFolded++;
            }
          }
        }
      }
    }

    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     Propagated: " + numPropagated + ", Folded:  " + numFolded);
    }

  } // optimizeConstants

}
