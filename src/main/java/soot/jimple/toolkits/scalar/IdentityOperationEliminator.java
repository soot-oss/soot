package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.IntType;
import soot.LongType;
import soot.Singletons;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.MulExpr;
import soot.jimple.OrExpr;
import soot.jimple.SubExpr;

/**
 * Transformer that eliminates unnecessary logic operations such as
 * 
 * $z0 = a | 0
 * 
 * which can more easily be repesented as
 * 
 * $z0 = a
 * 
 * @author Steven Arzt
 *
 */
public class IdentityOperationEliminator extends BodyTransformer {

  public IdentityOperationEliminator(Singletons.Global g) {
  }

  public static IdentityOperationEliminator v() {
    return G.v().soot_jimple_toolkits_scalar_IdentityOperationEliminator();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    for (Iterator<Unit> unitIt = b.getUnits().iterator(); unitIt.hasNext();) {
      Unit u = unitIt.next();
      if (u instanceof AssignStmt) {
        AssignStmt assignStmt = (AssignStmt) u;

        // a = b + 0 --> a = b
        // a = 0 + b --> a = b
        if (assignStmt.getRightOp() instanceof AddExpr) {
          BinopExpr aer = (BinopExpr) assignStmt.getRightOp();
          if (isConstZero(aer.getOp1())) {
            assignStmt.setRightOp(aer.getOp2());
          } else if (isConstZero(aer.getOp2())) {
            assignStmt.setRightOp(aer.getOp1());
          }
        }

        // a = b - 0 --> a = b
        if (assignStmt.getRightOp() instanceof SubExpr) {
          BinopExpr aer = (BinopExpr) assignStmt.getRightOp();
          if (isConstZero(aer.getOp2())) {
            assignStmt.setRightOp(aer.getOp1());
          }
        }

        // a = b * 0 --> a = 0
        // a = 0 * b --> a = 0
        if (assignStmt.getRightOp() instanceof MulExpr) {
          BinopExpr aer = (BinopExpr) assignStmt.getRightOp();
          if (isConstZero(aer.getOp1())) {
            assignStmt.setRightOp(getZeroConst(assignStmt.getLeftOp().getType()));
          } else if (isConstZero(aer.getOp2())) {
            assignStmt.setRightOp(getZeroConst(assignStmt.getLeftOp().getType()));
          }
        }

        // a = b | 0 --> a = b
        // a = 0 | b --> a = b
        if (assignStmt.getRightOp() instanceof OrExpr) {
          OrExpr orExpr = (OrExpr) assignStmt.getRightOp();
          if (isConstZero(orExpr.getOp1())) {
            assignStmt.setRightOp(orExpr.getOp2());
          } else if (isConstZero(orExpr.getOp2())) {
            assignStmt.setRightOp(orExpr.getOp1());
          }
        }
      }
    }

    // In a second step, we remove assingments such as <a = a>
    for (Iterator<Unit> unitIt = b.getUnits().iterator(); unitIt.hasNext();) {
      Unit u = unitIt.next();
      if (u instanceof AssignStmt) {
        AssignStmt assignStmt = (AssignStmt) u;
        if (assignStmt.getLeftOp() == assignStmt.getRightOp()) {
          unitIt.remove();
        }
      }
    }
  }

  /**
   * Gets the constant value 0 with the given type (integer, float, etc.)
   * 
   * @param type
   *          The type for which to get the constant zero value
   * @return The constant zero value of the given type
   */
  private Value getZeroConst(Type type) {
    if (type instanceof IntType) {
      return IntConstant.v(0);
    } else if (type instanceof LongType) {
      return LongConstant.v(0);
    } else if (type instanceof FloatType) {
      return FloatConstant.v(0);
    } else if (type instanceof DoubleType) {
      return DoubleConstant.v(0);
    }

    throw new RuntimeException("Unsupported numeric type");
  }

  /**
   * Checks whether the given value is the constant integer 0
   * 
   * @param op
   *          The value to check
   * @return True if the given value is the constant integer 0, otherwise false
   */
  private boolean isConstZero(Value op) {
    if (op instanceof IntConstant) {
      IntConstant ic = (IntConstant) op;
      return ic.value == 0;
    }
    return false;
  }

}
