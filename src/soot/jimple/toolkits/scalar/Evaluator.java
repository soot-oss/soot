/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Phong Co
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */



package soot.jimple.toolkits.scalar;

import soot.util.*;
import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;


public class Evaluator {

    public static boolean isValueConstantValued(Value op) {

        if (op instanceof Constant)
            return true;
        else if ((op instanceof UnopExpr)) {
            if (isValueConstantValued(((UnopExpr)op).getOp()))
                return true;
        }
        else if (op instanceof BinopExpr) 
        {
            /* Handle weird cases. */
            if (op instanceof DivExpr || op instanceof RemExpr)
            {
                if (!isValueConstantValued(((BinopExpr)op).getOp1()) ||
                    !isValueConstantValued(((BinopExpr)op).getOp2()))
                    return false;

                Value c1 = getConstantValueOf(((BinopExpr)op).getOp1());
                Value c2 = getConstantValueOf(((BinopExpr)op).getOp2());

                /* check for a 0 value.  If so, punt. */
                if (c2 instanceof IntConstant && ((IntConstant)c2).value == 0)
                    return false;

                if (c2 instanceof LongConstant && 
                         ((LongConstant)c2).value == 0)
                    return false;
            }

            if (isValueConstantValued(((BinopExpr)op).getOp1()) &&
                isValueConstantValued(((BinopExpr)op).getOp2()))
                return true;
        }
        return false;
    } // isValueConstantValued

    /** Returns the constant value of <code>op</code> if it is easy
     * to find the constant value; else returns <code>null</code>. */
    public static Value getConstantValueOf(Value op) {
        
        if (!isValueConstantValued(op))
            return null;
        
        if (op instanceof Constant)
            return op;
        else if (op instanceof UnopExpr) {
            Value c = getConstantValueOf(((UnopExpr)op).getOp());
            if (op instanceof NegExpr)
                return ((NumericConstant)c).negate();
        }
        else if (op instanceof BinopExpr) {
            Value c1 = getConstantValueOf(((BinopExpr)op).getOp1());
            Value c2 = getConstantValueOf(((BinopExpr)op).getOp2());
            if (op instanceof AddExpr)
                return ((NumericConstant)c1).add((NumericConstant)c2);
            else if (op instanceof SubExpr)
                return ((NumericConstant)c1).subtract((NumericConstant)c2);
            else if (op instanceof MulExpr)
                return ((NumericConstant)c1).multiply((NumericConstant)c2);
            // punting handled by isValueConstantValued().
            else if (op instanceof DivExpr)
                return ((NumericConstant)c1).divide((NumericConstant)c2);
            else if (op instanceof RemExpr)
                return ((NumericConstant)c1).remainder((NumericConstant)c2);
            else if (op instanceof EqExpr || op instanceof NeExpr)
            {
                if (c1 instanceof NumericConstant)
                {
                    if (op instanceof EqExpr)
                        return ((NumericConstant)c1).equalEqual
                            ((NumericConstant)c2);
                    else if (op instanceof NeExpr)
                        return ((NumericConstant)c1).notEqual
                            ((NumericConstant)c2);
                }
                else if (c1 instanceof StringConstant)
                {
                    boolean equality = ((StringConstant)c1).equals
                        ((StringConstant)c2);

                    boolean truth = (op instanceof EqExpr) ? equality :
                        !equality;

                    // Yeah, this variable name sucks, but I couldn't resist.
                    IntConstant beauty = IntConstant.v(truth ? 1 : 0);
                    return beauty;
                }
                else if (c1 instanceof NullConstant)
                    return IntConstant.v
                        (((NullConstant)c1).equals(c2) ? 1 : 0);
                throw new RuntimeException
                    ("constant neither numeric nor string");
            }
            else if (op instanceof GtExpr)
                return ((NumericConstant)c1).greaterThan((NumericConstant)c2);
            else if (op instanceof GeExpr)
                return ((NumericConstant)c1).greaterThanOrEqual((NumericConstant)c2);
            else if (op instanceof LtExpr)
                return ((NumericConstant)c1).lessThan((NumericConstant)c2);
            else if (op instanceof LeExpr)
                return ((NumericConstant)c1).lessThanOrEqual((NumericConstant)c2);
            else if (op instanceof AndExpr)
                return ((ArithmeticConstant)c1).and((ArithmeticConstant)c2);
            else if (op instanceof OrExpr)
                return ((ArithmeticConstant)c1).or((ArithmeticConstant)c2);
            else if (op instanceof XorExpr)
                return ((ArithmeticConstant)c1).xor((ArithmeticConstant)c2);
            else if (op instanceof ShlExpr)
                return ((ArithmeticConstant)c1).shiftLeft((ArithmeticConstant)c2);
            else if (op instanceof ShrExpr)
                return ((ArithmeticConstant)c1).shiftRight((ArithmeticConstant)c2);
            else if (op instanceof UshrExpr)
                return ((ArithmeticConstant)c1).unsignedShiftRight((ArithmeticConstant)c2);
            else if (op instanceof CmpExpr) {
                if ((c1 instanceof LongConstant) &&
                    (c2 instanceof LongConstant))
                    return ((LongConstant)c1).cmp((LongConstant)c2);
                else throw new IllegalArgumentException(
                                  "CmpExpr: LongConstant(s) expected");
            }
            else if ((op instanceof CmpgExpr) || (op instanceof CmplExpr)) {
                if ((c1 instanceof RealConstant) &&
                    (c2 instanceof RealConstant)) {
                    
                    if(op instanceof CmpgExpr)
                        return ((RealConstant) c1).cmpg((RealConstant) c2);
                    else if(op instanceof CmplExpr)
                        return ((RealConstant) c1).cmpl((RealConstant) c2);
                        
                }
                else throw new IllegalArgumentException(
                                  "CmpExpr: RealConstant(s) expected");
            }
            else
                throw new RuntimeException("unknown binop: " + op);
        }

        throw new RuntimeException("couldn't getConstantValueOf of: " + op);
    } // getConstantValueOf

} // Evaluator
    




