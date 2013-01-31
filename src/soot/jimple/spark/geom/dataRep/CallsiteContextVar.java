/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012 Richard Xiao
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
package soot.jimple.spark.geom.dataRep;

import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * A general interface for generating the traditional context sensitive variable representation.
 * 
 * @author xiao
 *
 */
public class CallsiteContextVar extends ContextVar
{
	/*
	 * If var is a local pointer or object, context is the callsite for the creation of the pointer or object.
	 * If var is a instance field, context is the callsite for the creation of its base object.
	 */
	public CgEdge context = null;
	
	
	public CallsiteContextVar() {}
	
	public CallsiteContextVar( CgEdge c, Node v )
	{
		context = c;
		var = v;
	}
	
	@Override
	public boolean equals( Object o )
	{
		CallsiteContextVar other = (CallsiteContextVar)o;
		return (other.context == context) && (other.var == var); 
	}

	@Override
	public int hashCode()
	{
		int ch = 0;
		if ( context != null ) ch = context.hashCode();
		
		int ans = var.hashCode() + ch;
		if ( ans < 0 ) ans = var.hashCode();
		return ans;
	}
}

