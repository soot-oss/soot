/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Phong Co
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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

    static boolean debug = soot.Main.isInDebugMode;
    static boolean verbose = soot.Main.isVerbose;

    public static boolean isValueConstantValued(Value op) {

        if (op instanceof Constant)
            return true;
        else if ((op instanceof UnopExpr)) {
            if (isValueConstantValued(((UnopExpr)op).getOp()))
                return true;
        }
        else if (op instanceof BinopExpr) {
            if (isValueConstantValued(((BinopExpr)op).getOp1()) &&
                isValueConstantValued(((BinopExpr)op).getOp2()))
                return true;
        }
        return false;
    } // isValueConstantValued

    public static Value getConstantValueOf(Value op) {
        
        if (!isValueConstantValued(op))
            return null;
        
        if (op instanceof Constant)
            return op;
        else if (op instanceof UnopExpr) {
            Value c = getConstantValueOf(((UnopExpr)op).getOp());
            if (c instanceof NegExpr)
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
            else if (op instanceof DivExpr)
                return ((NumericConstant)c1).divide((NumericConstant)c2);
            else if (op instanceof RemExpr)
                return ((NumericConstant)c1).remainder((NumericConstant)c2);
            else if (op instanceof EqExpr)
                return ((NumericConstant)c1).equalEqual((NumericConstant)c2);
            else if (op instanceof NeExpr)
                return ((NumericConstant)c1).notEqual((NumericConstant)c2);
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
                }
                else throw new IllegalArgumentException(
                                  "CmpExpr: RealConstant(s) expected");
            }
            else
                throw new RuntimeException("unknown binop: " + op);
        }

        return null;
    } // getConstantValueOf

} // Evaluator
    




