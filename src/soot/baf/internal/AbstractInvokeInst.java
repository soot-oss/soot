/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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





package soot.baf.internal;

import soot.*;
import soot.baf.*;
import java.util.*;

abstract class AbstractInvokeInst extends AbstractInst
{
    SootMethod method;

    public SootMethod getMethod()
    {
        return method;
    }

    public void setMethod(SootMethod m)
    {
        method = m;
    }

    public Type getType()
    {
        return method.getReturnType();
    }

    protected String toString(boolean isBrief, Map unitToName, String indentation)
    {
        return indentation + getName() + getParameters(isBrief, unitToName);
    }

    abstract public String getName();
    String getParameters(boolean isBrief, Map unitToName)
        { return " " + (isBrief ? method.getName() : method.getSignature()); }

    
    public int getOutMachineCount()
    {
        
        Type returnType = getMethod().getReturnType();
        
        if(returnType instanceof VoidType)
            return 0;
        else if(returnType instanceof LongType || returnType instanceof DoubleType) 
            return 2;
        else 
            return 1;
    }

    public int getInMachineCount()
    {
        int count = 0;
        
        Iterator it = getMethod().getParameterTypes().iterator();
        while(it.hasNext()) {
            count += JasminClass.sizeOfType((Type) it.next());            
        }
        return count;
    }
    

}
