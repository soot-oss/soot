/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

package soot.shimple.toolkits.scalar;

import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.shimple.*;
import soot.jimple.toolkits.scalar.*;
import java.util.*;

/**
 * "Extension" of soot.jimple.toolkits.scalar.Evaluator to handle Phi
 * expressions.  Basically a dumping ground for functionality found
 * useful in SConstantPropagatorAndFolder.
 *
 * @author Navindra Umanee.
 * @see soot.jimple.toolkits.scalar.Evaluator
 **/
public class SEvaluator
{
    /**
     * Returns true if given value is determined to be constant valued,
     * false otherwise
     **/
    public static boolean isValueConstantValued(Value op)
    {
        if(op instanceof PhiExpr) {
            Iterator argsIt = ((PhiExpr) op).getValues().iterator();
            Constant firstConstant = null;

            while(argsIt.hasNext()){
                Value arg = (Value) argsIt.next();

                if(!(arg instanceof Constant))
                    return false;

                if(firstConstant == null)
                    firstConstant = (Constant) arg;
                else if(!firstConstant.equals(arg))
                    return false;
            }

            return true;
        }

        return Evaluator.isValueConstantValued(op);
    }

    /**
     * Returns the constant value of <code>op</code> if it is easy to
     * find the constant value; else returns <code>null</code>.
     **/
    public static Value getConstantValueOf(Value op) 
    {
        if(!(op instanceof PhiExpr))
            return Evaluator.getConstantValueOf(op);

        if(!(isValueConstantValued(op)))
            return null;
        
        return (Value) ((PhiExpr) op).getValues().get(0);
    }

    /**
     * Convenience function...  Checks if all constant args in a
     * PhiExpr are the same (local args are ignored) if present.
     **/
    public static boolean isPhiFuzzyConstantValued(PhiExpr op)
    {
        Iterator argsIt = op.getValues().iterator();
        Constant firstConstant = null;

        while(argsIt.hasNext()){
            Value arg = (Value) argsIt.next();

            if(!(arg instanceof Constant))
                continue;

            if(firstConstant == null)
                firstConstant = (Constant) arg;
            else if(!firstConstant.equals(arg))
                return false;
        }
        
        return true;
    }

    /**
     * Gets the first constant argument in a PhiExpr, returns null if
     * not found.  Convenience function can be used in conjunction
     * with isPhiFuzzyConstantValued()
     *
     * @see #isPhiFuzzyConstantValued(PhiExpr)
     **/
    public static Constant getFirstConstantInPhi(PhiExpr op)
    {
        Iterator argsIt = op.getValues().iterator();

        while(argsIt.hasNext()){
            Value arg = (Value) argsIt.next();

            if(!(arg instanceof Constant))
                continue;

            return (Constant) arg;
        }
        
        return null;
    }

    /**
     * Get the constant value of the expression given the assumptions in
     * the localToConstant map.  Does not change expression.
     **/
    public static Constant getConstantValueOf(Expr e, Map localToConstant)
    {
        Constant ret = null;
        
        EVALUATE:
        {
        // weed out expressions we can't handle
        if(!(e instanceof UnopExpr ||
             e instanceof BinopExpr ||
             e instanceof PhiExpr)){
            ret = BottomConstant.v();
            break EVALUATE;
        }
         
        /* clone expr and update the clone with our assumptions */

        Expr expr = (Expr) e.clone();
        Iterator useBoxIt = expr.getUseBoxes().iterator();
        boolean cannotfold = false;

        while(useBoxIt.hasNext()){
            ValueBox useBox = (ValueBox) useBoxIt.next();
            Value use = useBox.getValue();
            if(use instanceof Local){
                Constant assumedConstant = (Constant) localToConstant.get(use);

                // we can't do anything with Bottom
                if(assumedConstant instanceof BottomConstant){
                    ret = BottomConstant.v();
                    break EVALUATE;
                }
                // expressions containing Top need special treatment
                else if(assumedConstant instanceof TopConstant)
                    cannotfold = true;
                // normal case
                else
                    if(useBox.canContainValue(assumedConstant))
                        useBox.setValue(assumedConstant);
            }
        }

        /* evalute the expr */

        // cannotfold means Top is present in the expression.  Hence
        // the expression resolves to Top, except for PhiExpr which
        // needs special handling.
        if(cannotfold){
            if(expr instanceof PhiExpr){
                PhiExpr pe = (PhiExpr) expr;
                if(isPhiFuzzyConstantValued(pe)){
                    Constant first = getFirstConstantInPhi(pe);
                    if(first != null)
                        ret = first;
                    else
                        ret = TopConstant.v();
                    break EVALUATE;
                }
                else{
                    ret = BottomConstant.v();
                    break EVALUATE;
                }
            }
        }
        else{
            Constant constant = (Constant) getConstantValueOf(expr);
            if(constant != null)
                ret = constant;
            else
                ret = BottomConstant.v();
            break EVALUATE;
        }
        } // end EVALUATE

        if(ret == null)
            throw new RuntimeException("Assertion failed.");
        
        return ret;
    }

    /**
     * Top i.e. assumed to be a constant, but of unknown value.
     **/
    public static class TopConstant extends BogusConstant
    {
        private static final TopConstant constant = new TopConstant();
        
        private TopConstant() {}
        
        public static Constant v()
        {
            return constant;
        }
    
        public Type getType()
        {
            return UnknownType.v();
        }

        public void apply(Switch sw)
        {
            throw new RuntimeException("Not implemented.");
        }
    }
    
    /**
     * Bottom i.e. known not to be a constant.
     **/
    public static class BottomConstant extends BogusConstant
    {
        private static final BottomConstant constant = new BottomConstant();
        
        private BottomConstant() {}

        public static Constant v()
        {
            return constant;
        }
        
        public Type getType()
        {
        return UnknownType.v();
        }
    
        public void apply(Switch sw)
        {
            throw new RuntimeException("Not implemented.");
        }
    }

    /**
     * Create a new bogus hierarchy of constants -- Top and Bottom.
     **/
    public static abstract class BogusConstant extends Constant
    {
    }
}

