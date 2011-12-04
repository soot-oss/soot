/* Soot - a J*va Optimization Framework
 * Copyright (C) 2011 Richard Xiao
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
package soot.jimple.spark.geom.geomPA;

import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * A general interface for generating traditional context represented variables.
 * @author xiao
 *
 */
public class CallsiteContextVar implements Numberable
{
	// private fields
	public CgEdge context = null;
	public Node var = null;
	public int id;
	public boolean inQ = false;
	
	public CallsiteContextVar()
	{
		
	}
	
	public CallsiteContextVar( CgEdge c, Node v )
	{
		context = c;
		var = v;
	}
	
	public boolean equals( Object o )
	{
		CallsiteContextVar other = (CallsiteContextVar)o;
		return (other.context == context) && (other.var == var); 
	}

	
	public int hashCode()
	{
		return var.hashCode();
	}
	
	
	public void setNumber(int number) 
	{
		id = number;
	}

	
	public int getNumber() 
	{
		return id;
	}
}

