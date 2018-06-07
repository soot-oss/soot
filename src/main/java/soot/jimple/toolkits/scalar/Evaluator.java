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

import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArithmeticConstant;
import soot.jimple.BinopExpr;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.Constant;
import soot.jimple.DivExpr;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.IntConstant;
import soot.jimple.LeExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NullConstant;
import soot.jimple.NumericConstant;
import soot.jimple.OrExpr;
import soot.jimple.RealConstant;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.UnopExpr;
import soot.jimple.UshrExpr;
import soot.jimple.XorExpr;

public class Evaluator {

  public static boolean isValueConstantValued(Value op) {

    if (op instanceof Constant) {
      return true;
    } else if ((op instanceof UnopExpr)) {
      Value innerOp = ((UnopExpr) op).getOp();
      if (innerOp == NullConstant.v()) {
        // operations on null will throw an exception and the operation
        // is therefore not considered constant-valued; see posting on Soot list
        // on 18 September 2007 14:36
        return false;
      }
      if (isValueConstantValued(innerOp)) {
        return true;
      }
    } else if (op instanceof BinopExpr) {
      /* Handle weird cases. */
      if (op instanceof DivExpr || op instanceof RemExpr) {
        if (!isValueConstantValued(((BinopExpr) op).getOp1()) || !isValueConstantValued(((BinopExpr) op).getOp2())) {
          return false;
        }

        Value c1 = getConstantValueOf(((BinopExpr) op).getOp1());
        Value c2 = getConstantValueOf(((BinopExpr) op).getOp2());

        /* check for a 0 value. If so, punt. */
        if (c2 instanceof IntConstant && ((IntConstant) c2).value == 0) {
          return false;
        }

        if (c2 instanceof LongConstant && ((LongConstant) c2).value == 0) {
          return false;
        }
      }

      if (isValueConstantValued(((BinopExpr) op).getOp1()) && isValueConstantValued(((BinopExpr) op).getOp2())) {
        return true;
      }
    }
    return false;
  } // isValueConstantValued

  /**
   * Returns the constant value of <code>op</code> if it is easy to find the constant value; else returns <code>null</code>.
   */
  public static Value getConstantValueOf(Value op) {

    if (!isValueConstantValued(op)) {
      return null;
    }

    if (op instanceof Constant) {
      return op;
    } else if (op instanceof UnopExpr) {
      Value c = getConstantValueOf(((UnopExpr) op).getOp());
      if (op instanceof NegExpr) {
        return ((NumericConstant) c).negate();
      }
    } else if (op instanceof BinopExpr) {
      Value c1 = getConstantValueOf(((BinopExpr) op).getOp1());
      Value c2 = getConstantValueOf(((BinopExpr) op).getOp2());
      if (op instanceof AddExpr) {
        return ((NumericConstant) c1).add((NumericConstant) c2);
      } else if (op instanceof SubExpr) {
        return ((NumericConstant) c1).subtract((NumericConstant) c2);
      } else if (op instanceof MulExpr) {
        return ((NumericConstant) c1).multiply((NumericConstant) c2);
      } else if (op instanceof DivExpr) {
        return ((NumericConstant) c1).divide((NumericConstant) c2);
      } else if (op instanceof RemExpr) {
        return ((NumericConstant) c1).remainder((NumericConstant) c2);
      } else if (op instanceof EqExpr || op instanceof NeExpr) {
        if (c1 instanceof NumericConstant) {
          if (op instanceof EqExpr) {
            return ((NumericConstant) c1).equalEqual((NumericConstant) c2);
          } else if (op instanceof NeExpr) {
            return ((NumericConstant) c1).notEqual((NumericConstant) c2);
          }
        } else if (c1 instanceof StringConstant || c1 instanceof NullConstant || c1 instanceof ClassConstant) {
          boolean equality = c1.equals(c2);

          boolean truth = (op instanceof EqExpr) ? equality : !equality;

          // Yeah, this variable name sucks, but I couldn't resist.
          IntConstant beauty = IntConstant.v(truth ? 1 : 0);
          return beauty;
        }
        throw new RuntimeException("constant neither numeric nor string");
      } else if (op instanceof GtExpr) {
        return ((NumericConstant) c1).greaterThan((NumericConstant) c2);
      } else if (op instanceof GeExpr) {
        return ((NumericConstant) c1).greaterThanOrEqual((NumericConstant) c2);
      } else if (op instanceof LtExpr) {
        return ((NumericConstant) c1).lessThan((NumericConstant) c2);
      } else if (op instanceof LeExpr) {
        return ((NumericConstant) c1).lessThanOrEqual((NumericConstant) c2);
      } else if (op instanceof AndExpr) {
        return ((ArithmeticConstant) c1).and((ArithmeticConstant) c2);
      } else if (op instanceof OrExpr) {
        return ((ArithmeticConstant) c1).or((ArithmeticConstant) c2);
      } else if (op instanceof XorExpr) {
        return ((ArithmeticConstant) c1).xor((ArithmeticConstant) c2);
      } else if (op instanceof ShlExpr) {
        return ((ArithmeticConstant) c1).shiftLeft((ArithmeticConstant) c2);
      } else if (op instanceof ShrExpr) {
        return ((ArithmeticConstant) c1).shiftRight((ArithmeticConstant) c2);
      } else if (op instanceof UshrExpr) {
        return ((ArithmeticConstant) c1).unsignedShiftRight((ArithmeticConstant) c2);
      } else if (op instanceof CmpExpr) {
        if ((c1 instanceof LongConstant) && (c2 instanceof LongConstant)) {
          return ((LongConstant) c1).cmp((LongConstant) c2);
        } else {
          throw new IllegalArgumentException("CmpExpr: LongConstant(s) expected");
        }
      } else if ((op instanceof CmpgExpr) || (op instanceof CmplExpr)) {
        if ((c1 instanceof RealConstant) && (c2 instanceof RealConstant)) {

          if (op instanceof CmpgExpr) {
            return ((RealConstant) c1).cmpg((RealConstant) c2);
          } else if (op instanceof CmplExpr) {
            return ((RealConstant) c1).cmpl((RealConstant) c2);
          }

        } else {
          throw new IllegalArgumentException("CmpExpr: RealConstant(s) expected");
        }
      } else {
        throw new RuntimeException("unknown binop: " + op);
      }
    }

    throw new RuntimeException("couldn't getConstantValueOf of: " + op);
  } // getConstantValueOf

} // Evaluator
