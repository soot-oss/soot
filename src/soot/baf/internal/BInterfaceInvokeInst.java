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
import soot.util.*;
import java.util.*;

public class BInterfaceInvokeInst extends AbstractInvokeInst 
                                  implements InterfaceInvokeInst
{
    int argCount;
    
    public int getInCount()
    {
        return getMethod().getParameterCount() +1;
        
    }

    public int getInMachineCount()
    {
        return super.getInMachineCount() +1;        
    }
    

    public BInterfaceInvokeInst(SootMethod method, int argCount) 
        { setMethod(method); this.argCount = argCount; }


    public Object clone() 
    {
        return new  BInterfaceInvokeInst(getMethod(), getArgCount());
    }

    

    final public String getName() { return "interfaceinvoke"; }
    final String getParameters(boolean isBrief, Map unitToName)
        { return super.getParameters(isBrief, unitToName) + " " + argCount; }

    public int getArgCount() { return argCount; }
    public void setArgCount(int x) { argCount = x; }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseInterfaceInvokeInst(this);
    }   
}




