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

class MethodParameter
{
    private SootMethod m;
    private int param;

    public MethodParameter(SootMethod m, int i)
    {
	this.m = m;
	this.param = i;
    }

    public Type getType()
    {
	return m.getParameterType(param);
    }

    public int hashCode()
    {
	return m.hashCode()+param;
    }

    public SootMethod getMethod()
    {
	return m;
    }

    public int getIndex()
    {
	return param;
    }

    public boolean equals(Object other)
    {
	if (other instanceof MethodParameter)
	{
	    MethodParameter another = (MethodParameter)other;
	    
	    return (m.equals(another.getMethod()) && param == another.getIndex());
	}
	
	return false;
    }

    public String toString()
    {
        return "["+m.getSignature()+" : P"+param+"]";
    }
}















