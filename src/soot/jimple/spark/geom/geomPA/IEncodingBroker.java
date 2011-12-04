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

public abstract class IEncodingBroker 
{
	public static int n_added_flowedge = 0;
	public static int n_added_pts = 0;
	
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
