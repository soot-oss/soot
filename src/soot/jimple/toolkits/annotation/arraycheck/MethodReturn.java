/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Feng Qian
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

package soot.jimple.toolkits.annotation.arraycheck;

import soot.*;
import soot.jimple.*;

class MethodReturn
{
    private SootMethod m;
    public MethodReturn(SootMethod m)
    {
	this.m = m;
    }

    public SootMethod getMethod()
    {
	return m;
    }

    public Type getType()
    {
	return m.getReturnType();
    }

    public int hashCode()
    {
	return m.hashCode()+m.getParameterCount();
    }

    public boolean equals(Object other)
    {
	if (other instanceof MethodReturn)
	{
	    return m.equals( ((MethodReturn)other).getMethod() );
	}

	return false;
    }

    public String toString()
    {
    	return "["+m.getSignature()+" : R]";
    }
}
		
