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
import soot.options.SparkOptions;

/**
 * An abstract class for hiding different encoding methods, e.g. Geom, HeapIns, PtIns.
 * 
 * @author xiao
 *
 */
public abstract class IEncodingBroker 
{
	// Define the mapping types
	public final static int Undefined_Mapping = -1;
	public final static int ONE_TO_ONE = 0;
	public final static int MANY_TO_MANY = 1;
	
	public static final int full_convertor[] = { 
		ONE_TO_ONE, MANY_TO_MANY, 
		MANY_TO_MANY, MANY_TO_MANY 
	};
	
	
	/**
	 * Generate a node of proper kind.
	 * @param v
	 * @return
	 */
	public abstract IVarAbstraction generateNode( Node v );
	
	/**
	 * Build the initial encoding of the pointer assignments and points-to facts.
	 */
	public abstract void initFlowGraph( GeomPointsTo ptAnalyzer );
	
	/**
	 * @see SparkOptions#geom_encoding_Geom
	 * @see SparkOptions#geom_encoding_HeapIns
	 * @see SparkOptions#geom_encoding_PtIns
	 */
	public abstract int getEncodingType();
}