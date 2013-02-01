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
package soot.jimple.spark.geom.helper;

import soot.jimple.spark.pag.Node;

public abstract class PtSensVisitor {

	/**
	 * The user should implement how to deal with the variable with the contexts [L, R).
	 * The return value indicates that the method sm_int has no call edges, which is used to terminate the context enumeration.
	 * 
	 * @param var
	 * @param L
	 * @param R
	 * @param sm_int : the integer ID of the SootMethod
	 */
	public abstract boolean visit( Node var, long L, long R, int sm_int );
	
	/**
	 * Called before each round of collection.
	 */
	public abstract void prepare();
	
	/**
	 * Called after each round of collection.
	 */
	public abstract void finish();
}
