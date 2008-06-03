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
import java.util.*;

abstract public class AbstractInvokeExpr implements InvokeExpr
{
    protected SootMethodRef methodRef;
    protected ValueBox[] argBoxes;

	public void setMethodRef(SootMethodRef methodRef) {
		this.methodRef = methodRef;
	}
	
    public SootMethodRef getMethodRef()
    {
        return methodRef;
    }

    public SootMethod getMethod()
    {
        return methodRef.resolve();
    }

    public abstract Object clone();
    
    public Value getArg(int index)
    {
        return argBoxes[index].getValue();
    }

    public List getArgs()
    {
        List l = new ArrayList();
        for (ValueBox element : argBoxes)
			l.add(element.getValue());

        return l;
    }

    public int getArgCount()
    {
        return argBoxes.length;
    }

    public void setArg(int index, Value arg)
    {
        argBoxes[index].setValue(arg);
    }

    public ValueBox getArgBox(int index)
    {
        return argBoxes[index];
    }

    public Type getType()
    {
        return methodRef.returnType();
    }
}
