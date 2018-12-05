package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;

/**
 * Transformer for removing unnecessary casts on primitive values. An assignment a = (float) 42 will for instance be
 * transformed to a = 42f;
 *
 * @author Steven Arzt
 *
 */
public class ConstantCastEliminator extends BodyTransformer {

  public ConstantCastEliminator(Singletons.Global g) {
  }

  public static ConstantCastEliminator v() {
    return G.v().soot_jimple_toolkits_scalar_ConstantCastEliminator();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    // Check for all assignments that perform casts on primitive constants
    for (Unit u : b.getUnits()) {
      if (u instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) u;
        if (assign.getRightOp() instanceof CastExpr) {
          CastExpr ce = (CastExpr) assign.getRightOp();
          if (ce.getOp() instanceof Constant) {
            // a = (float) 42
            if (ce.getType() instanceof FloatType && ce.getOp() instanceof IntConstant) {
              IntConstant it = (IntConstant) ce.getOp();
              assign.setRightOp(FloatConstant.v(it.value));
            }
            // a = (double) 42
            else if (ce.getType() instanceof DoubleType && ce.getOp() instanceof IntConstant) {
              IntConstant it = (IntConstant) ce.getOp();
              assign.setRightOp(DoubleConstant.v(it.value));
            }
          }
        }
      }
    }
  }

}
