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

class WeightedDirectedEdge {
    Object from, to;
    int weight;
    public WeightedDirectedEdge(Object from, Object to, int weight)
    {
	this.from = from;
	this.to = to;
	this.weight = weight;
    }

    public int hashCode()
    {
	return from.hashCode()+to.hashCode()+weight;
    }

    public boolean equals(Object other)
    {
	if (other instanceof WeightedDirectedEdge)
	{
	    WeightedDirectedEdge another = (WeightedDirectedEdge)other;
	    return ( (this.from == another.from)
		   &&(this.to == another.to)
		   &&(this.weight==another.weight) );
	}
	return false;
    }
    
    public String toString()
    {
	return from+"->"+to+"="+weight;
    }
}
