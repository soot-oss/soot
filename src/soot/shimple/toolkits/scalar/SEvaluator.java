/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee
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
 * "Extension" of soot.jimple.toolkits.scalar.Evaluator to handle
 * Phi expressions.  Also provides a couple of convenience
 * functions.
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
            Iterator argsIt = ((PhiExpr) op).getValueArgs().iterator();
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
        
        return (Value) ((PhiExpr) op).getValueArgs().get(0);
    }

    /**
     * Convenience function...  Checks if all constant args in a
     * PhiExpr are the same (local args are ignored) if present.
     **/
    public static boolean isPhiFuzzyConstantValued(PhiExpr op)
    {
        Iterator argsIt = op.getValueArgs().iterator();
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
        Iterator argsIt = op.getValueArgs().iterator();

        while(argsIt.hasNext()){
            Value arg = (Value) argsIt.next();

            if(!(arg instanceof Constant))
                continue;

            return (Constant) arg;
        }
        
        return null;
    }
}

