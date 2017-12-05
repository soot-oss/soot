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

class FlowGraphEdge
{
    Object from;
    Object to;

    public FlowGraphEdge()
    {
        this.from = null; 
	this.to = null;
    }
    
    public FlowGraphEdge(Object from, Object to)
    {
        this.from = from;
	this.to = to;
    }

    public int hashCode()
    {
        return this.from.hashCode()^this.to.hashCode();
    }

    public Object getStartUnit()
    {
        return this.from;
    }

    public Object getTargetUnit()
    {
        return this.to;
    }

    public void changeEndUnits(Object from, Object to)
    {
        this.from = from;
	this.to = to;
    }
    
    public boolean equals(Object other)
    {
	if (other == null)
	    return false;

        if (other instanceof FlowGraphEdge)
	{
	    Object otherstart = ((FlowGraphEdge)other).getStartUnit();
	    Object othertarget = ((FlowGraphEdge)other).getTargetUnit();

	    return (this.from.equals(otherstart)&&this.to.equals(othertarget));
	}
	else
	    return false;
    }
}
