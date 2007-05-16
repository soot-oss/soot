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
 * Extension of soot.jimple.toolkits.scalar.Evaluator to handle Phi
 * expressions. 
 *
 * @author Navindra Umanee.
 * @see soot.jimple.toolkits.scalar.Evaluator
 * @see SConstantPropagatorAndFolder
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
            Iterator<Value> argsIt = ((PhiExpr) op).getValues().iterator();
            Constant firstConstant = null;

            while(argsIt.hasNext()){
                Value arg = argsIt.next();

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
        
        return ((PhiExpr) op).getValue(0);
    }

    /**
     * If a normal expression contains Bottom, always return Bottom.
     * Otherwise, if a normal expression contains Top, returns Top.
     * Else determine the constant value of the expression if
     * possible, if not return Bottom.
     *
     * <p> If a Phi expression contains Bottom, always return Bottom.
     * Otherwise, if all the constant arguments are the same (ignoring
     * Top and locals) return that constant or Top if no concrete
     * constant is present, else return Bottom.
     *
     * @see SEvaluator.TopConstant
     * @see SEvaluator.BottomConstant
     **/
    public static Constant getFuzzyConstantValueOf(Value v)
    {
        if(v instanceof Constant)
            return (Constant) v;

        if(v instanceof Local)
            return BottomConstant.v();

        if(!(v instanceof Expr))
            return BottomConstant.v();

        Expr expr = (Expr) v;
        Constant constant = null;

        if(expr instanceof PhiExpr){
            PhiExpr phi = (PhiExpr) expr;
            Iterator<Value> argsIt = phi.getValues().iterator();

            while(argsIt.hasNext()){
                Value arg = argsIt.next();
            
                if(!(arg instanceof Constant))
                    continue;

                if(arg instanceof TopConstant)
                    continue;

                if(constant == null)
                    constant = (Constant) arg;
                else if(!constant.equals(arg)){
                    constant = BottomConstant.v();
                    break;
                }
            }

            if(constant == null)
                constant = TopConstant.v();
        }
        else{
            Iterator valueBoxesIt = expr.getUseBoxes().iterator();
            while(valueBoxesIt.hasNext()){
                Value value = ((ValueBox)valueBoxesIt.next()).getValue();

                if(value instanceof BottomConstant){
                    constant = BottomConstant.v();
                    break;
                }
                
                if(value instanceof TopConstant)
                    constant = TopConstant.v();
            }

            if(constant == null)
                constant = (Constant) getConstantValueOf(expr);

            if(constant == null)
                constant = BottomConstant.v();
        }
        
        return constant;
    }

    /**
     * Get the constant value of the expression given the assumptions
     * in the localToConstant map (may contain Top and Bottom).  Does
     * not change expression.
     *
     * @see SEvaluator.TopConstant
     * @see SEvaluator.BottomConstant
     **/
    public static Constant getFuzzyConstantValueOf(Value v, Map<Local, Constant> localToConstant)
    {
        if(v instanceof Constant)
            return (Constant) v;

        if(v instanceof Local)
            return localToConstant.get(v);

        if(!(v instanceof Expr))
            return BottomConstant.v();

        /* clone expr and update the clone with our assumptions */

        Expr expr = (Expr) v.clone();
        Iterator useBoxIt = expr.getUseBoxes().iterator();

        while(useBoxIt.hasNext()){
            ValueBox useBox = (ValueBox) useBoxIt.next();
            Value use = useBox.getValue();
            if(use instanceof Local){
                Constant constant = localToConstant.get(use);
                if(useBox.canContainValue(constant))
                    useBox.setValue(constant);
            }
        }

        // oops -- clear spurious pointers to the unit chain!
        if(expr instanceof UnitBoxOwner)
            ((UnitBoxOwner)expr).clearUnitBoxes();
        
        /* evaluate the expression */
        
        return(getFuzzyConstantValueOf(expr));
    }

    /**
     * Head of a new hierarchy of constants -- Top and Bottom.
     **/
    public static abstract class MetaConstant extends Constant
    {
    }
    
    /**
     * Top i.e. assumed to be a constant, but of unknown value.
     **/
    public static class TopConstant extends MetaConstant
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
    public static class BottomConstant extends MetaConstant
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
}

