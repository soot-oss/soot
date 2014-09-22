/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
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


package soot.jimple.internal;

import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.util.*;

import java.util.*;


@SuppressWarnings("serial")
public abstract class AbstractInterfaceInvokeExpr extends AbstractInstanceInvokeExpr 
                             implements InterfaceInvokeExpr, ConvertToBaf
{
    protected AbstractInterfaceInvokeExpr(ValueBox baseBox, SootMethodRef methodRef,
                                  ValueBox[] argBoxes)
    {
    	super(methodRef, baseBox, argBoxes); 
    	if( methodRef.isStatic() ) throw new RuntimeException("wrong static-ness");
    }

    public boolean equivTo(Object o)
    {
        if (o instanceof AbstractInterfaceInvokeExpr)
        {
            AbstractInterfaceInvokeExpr ie = (AbstractInterfaceInvokeExpr)o;
            if (!(baseBox.getValue().equivTo(ie.baseBox.getValue()) &&
                    getMethod().equals(ie.getMethod()) && 
                    argBoxes.length == ie.argBoxes.length))
                return false;
            for(int i=0; i< argBoxes.length; i++){
          	  if(!(argBoxes[i]).getValue().equivTo(ie.argBoxes[i].getValue()))
          	    return false;
          	}
            return true;
        }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return baseBox.getValue().equivHashCode() * 101 + getMethod().equivHashCode() * 17;
    }

    public abstract Object clone();

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(Jimple.INTERFACEINVOKE + " " + baseBox.getValue().toString() +
            "." + methodRef.getSignature() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(argBoxes[i].getValue().toString());
        }

        buffer.append(")");

        return buffer.toString();
    }

    public void toString(UnitPrinter up)
    {
        up.literal(Jimple.INTERFACEINVOKE);
        up.literal(" ");
        baseBox.toString(up);
        up.literal(".");
        up.methodRef(methodRef);
        up.literal("(");
        
        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                up.literal(", ");
                
            argBoxes[i].toString(up);
        }

        up.literal(")");
    }

    
    public void apply(Switch sw)
    {
        ((ExprSwitch) sw).caseInterfaceInvokeExpr(this);
    }

    private static int sizeOfType(Type t)
    {
        if(t instanceof DoubleType || t instanceof LongType)
            return 2;
        else if(t instanceof VoidType)
            return 0;
        else
            return 1;
    }

    private static int argCountOf(SootMethodRef m)
    {
        int argCount = 0;
        for ( Type t : m.parameterTypes()) {
        	argCount += sizeOfType(t);
        }

        return argCount;
    }

    public void convertToBaf(JimpleToBafContext context, List<Unit> out)
    {
        ((ConvertToBaf)getBase()).convertToBaf(context, out);;

       for (ValueBox element : argBoxes) {
	    ((ConvertToBaf)(element.getValue())).convertToBaf(context, out);
	}
       
       Unit u = Baf.v().newInterfaceInvokeInst(methodRef, argCountOf(methodRef));
       out.add(u);
       u.addAllTagsOf(context.getCurrentUnit());	
    }
}

