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

package soot.shimple.internal;

import soot.*;
import soot.shimple.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.*;
import java.util.*;

/**
 * Internal class implementing our PHI expression interface.
 *
 * <p> A PHI expression takes a list of Values (Locals/Constants) as
 * args.
 *
 * <p> Example: PHI(local1, local2, ...).
 *
 * @author Navindra Umanee
 * @see soot.shimple.PhiExpr
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
**/
public class SPhiExpr implements PhiExpr
{
    protected List argBoxes;
    protected Type type;

    /**
     * Create a trivial PHI expression for leftLocal.  numberOfControlPreds
     * determines how many arguments the PHI expression requires.
     **/
    public SPhiExpr(Local leftLocal, int numberOfControlPreds)
    {
        type = leftLocal.getType();
        argBoxes = new ArrayList();

        // *** Should we use something more restrictive than RValueBox?
        for(int i = 0; i < numberOfControlPreds; i++){
            ValueBox vb = new RValueBox(leftLocal);
            argBoxes.add(vb);
        }
    }

    /**
     * Create a PHI expression from the given list of Values
     **/
    public SPhiExpr(List args)
    {
        if(args.size() == 0)
            throw new RuntimeException("Arg list may not be empty");

        type = ((Value) args.get(0)).getType();

        argBoxes = new ArrayList();
    
        Iterator argsIt = args.iterator();

        while(argsIt.hasNext()){
            Value arg = (Value) argsIt.next();
            argBoxes.add(new RValueBox(arg));
        }
    }

    /* The rest of these functions are fairly self-documenting. */
    
    public Value getArg(int index)
    {
        return ((ValueBox)argBoxes.get(index)).getValue();
    }
    
    public List getArgs()
    {
        List args = new ArrayList();
        
        Iterator argBoxesIt = argBoxes.iterator();

        while(argBoxesIt.hasNext()){
            Value arg = ((ValueBox)argBoxesIt.next()).getValue();

            args.add(arg);
        }
        
        return args;
    }
        
    public int getArgCount()
    {
        return argBoxes.size();
    }

    public void setArg(int index, Value arg)
    {
        ValueBox argBox = (ValueBox) argBoxes.get(index);
        
        argBoxes.set(index, arg);
    }

    public Value removeArg(int index)
    {
        Value value = getArg(index);
        argBoxes.remove(index);
        return value;
    }

    public ValueBox getArgBox(int index)
    {
        return (ValueBox) argBoxes.get(index);
    }

    public boolean equivTo(Object o)
    {
        if(o instanceof SPhiExpr){
            SPhiExpr pe = (SPhiExpr) o;

            if(argBoxes.size() != pe.getArgCount())
                return false;

            for(int i = 0; i < argBoxes.size(); i++){
                if(!getArg(i).equivTo(pe.getArg(i)))
                    return false;
            }

            return true;
        }

        return false;
    }

    public int equivHashCode()
    {
        // *** TODO: Do we need this?
        
        throw new RuntimeException("Not Yet Implemented");
    }

    public String toString()
    {
        StringBuffer expr = new StringBuffer("PHI(");

        Iterator argBoxesIt = argBoxes.iterator();
        while(argBoxesIt.hasNext()){
            Value arg = ((ValueBox)argBoxesIt.next()).getValue();
            expr.append(arg.toString());

            if(argBoxesIt.hasNext())
                expr.append(", ");
        }

        expr.append(")");

        return expr.toString();
    }

    public String toBriefString()
    {
        StringBuffer expr = new StringBuffer("PHI(");

        Iterator argBoxesIt = argBoxes.iterator();
        while(argBoxesIt.hasNext()){
            Value arg = ((ValueBox)argBoxesIt.next()).getValue();
            expr.append(((ToBriefString)arg).toBriefString());

            if(argBoxesIt.hasNext())
                expr.append(", ");
        }

        expr.append(")");

        return expr.toString();
    }

    public List getUseBoxes()
    {
        Set set = new HashSet();

        Iterator argBoxesIt = argBoxes.iterator();

        while(argBoxesIt.hasNext()){
            ValueBox argBox = (ValueBox) argBoxesIt.next();
            
            set.addAll(argBox.getValue().getUseBoxes());
            set.add(argBox);
        }

        return new ArrayList(set);
    }

    public Type getType()
    {
        return type;
    }

    public void apply(Switch sw)
    {
        ((ShimpleExprSwitch) sw).casePhiExpr(this);
    }

    public Object clone()
    {
        return new SPhiExpr(getArgs());
    }
}
