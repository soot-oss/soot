/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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






package soot.jimple.internal;

import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public abstract class AbstractInterfaceInvokeExpr extends AbstractInstanceInvokeExpr 
                             implements InterfaceInvokeExpr, ConvertToBaf
{
    protected AbstractInterfaceInvokeExpr(ValueBox baseBox, SootMethod method,
                                  ValueBox[] argBoxes)
    {
        this.baseBox = baseBox; this.method = method;
        this.argBoxes = argBoxes;
    }

    public abstract Object clone();

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(Jimple.v().INTERFACEINVOKE + " " + baseBox.getValue().toString() +
            "." + method.getJimpleStyleSignature() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(argBoxes[i].getValue().toString());
        }

        buffer.append(")");

        return buffer.toString();
    }

    
    public String toBriefString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(((ToBriefString) baseBox.getValue()).toBriefString() +
            "." + method.getName() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(((ToBriefString) argBoxes[i].getValue()).toBriefString());
        }

        buffer.append(")");

        return buffer.toString();
    }

    public void apply(Switch sw)
    {
        ((ExprSwitch) sw).caseInterfaceInvokeExpr(this);
    }

    int sizeOfType(Type t)
    {
        if(t instanceof DoubleType || t instanceof LongType)
            return 2;
        else if(t instanceof VoidType)
            return 0;
        else
            return 1;
    }

    int argCountOf(SootMethod m)
    {
        int argCount = 0;
        Iterator typeIt = m.getParameterTypes().iterator();

        while(typeIt.hasNext())
        {
            Type t = (Type) typeIt.next();

            argCount += sizeOfType(t);
        }

        return argCount;
    }

    public void convertToBaf(JimpleToBafContext context, List out)
    {
        ((ConvertToBaf)getBase()).convertToBaf(context, out);;

       for(int i = 0; i < argBoxes.length; i++)
        {
            ((ConvertToBaf)(argBoxes[i].getValue())).convertToBaf(context, out);
        }

        out.add(Baf.v().newInterfaceInvokeInst(method, argCountOf(method)));
    }
}

