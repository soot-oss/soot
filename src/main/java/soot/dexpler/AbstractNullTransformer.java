package soot.dexpler;

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

import soot.RefLikeType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NeExpr;
import soot.jimple.NullConstant;

/**
 * Abstract base class for {@link DexNullTransformer} and {@link DexIfTransformer}.
 *
 * @author Steven Arzt
 */
public abstract class AbstractNullTransformer extends DexTransformer {

  /**
   * Examine expr if it is a comparison with 0.
   *
   * @param expr
   *          the ConditionExpr to examine
   */
  protected boolean isZeroComparison(ConditionExpr expr) {
    if (expr instanceof EqExpr || expr instanceof NeExpr) {
      if (expr.getOp2() instanceof IntConstant && ((IntConstant) expr.getOp2()).value == 0) {
        return true;
      }
      if (expr.getOp2() instanceof LongConstant && ((LongConstant) expr.getOp2()).value == 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Replace 0 with null in the given unit.
   *
   * @param u
   *          the unit where 0 will be replaced with null.
   */
  protected void replaceWithNull(Unit u) {
    if (u instanceof IfStmt) {
      ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
      if (isZeroComparison(expr)) {
        expr.setOp2(NullConstant.v());
      }
    } else if (u instanceof AssignStmt) {
      AssignStmt s = (AssignStmt) u;
      Value v = s.getRightOp();
      if ((v instanceof IntConstant && ((IntConstant) v).value == 0)
          || (v instanceof LongConstant && ((LongConstant) v).value == 0)) {
        // If this is a field assignment, double-check the type. We
        // might have a.f = 2 with a being a null candidate, but a.f
        // being an int.
        if (!(s.getLeftOp() instanceof InstanceFieldRef)
            || ((InstanceFieldRef) s.getLeftOp()).getFieldRef().type() instanceof RefLikeType) {
          s.setRightOp(NullConstant.v());
        }
      }
    }
  }

  protected static boolean isObject(Type t) {
    return t instanceof RefLikeType;
  }

}
