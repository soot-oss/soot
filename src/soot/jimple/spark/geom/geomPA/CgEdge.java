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


import soot.jimple.spark.pag.VarNode;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * The internal call graph edge representation.
 * 
 * @author xiao
 *
 */
public class CgEdge {
	// The edge structure in soot
	public Edge sootEdge;
	// The source/destination
	public int s, t;
	// The starting context of function t
	// Thus, the interval is: (1, |s|, map_offset + |s| - 1)
	public long map_offset;
	// Is this call edge a SCC edge, i.e two ends both in the same SCC?
	public boolean scc_edge = false;
	// Is this call edge still in service?
	public boolean is_obsoleted = false;
	// Base variable of this virtual call edge
	public VarNode base_var = null;
	// Next call edge
	public CgEdge next = null;
	//cg_edge inv_next = null;
	
	public CgEdge(int ss, int tt, Edge se, CgEdge ne) {
		s = ss;
		t = tt;
		sootEdge = se;
		next = ne;
	}
	
	/**
	 * Copy itself.
	 * @return
	 */
	public CgEdge duplicate()
	{
		if ( is_obsoleted ) 
			return null;
		
		CgEdge new_edge = new CgEdge(s, t, sootEdge, null);
		new_edge.map_offset = map_offset;
		new_edge.scc_edge = scc_edge;
		new_edge.base_var = base_var;
		return new_edge;
	}
	
	@Override
	public String toString()
	{
		if ( sootEdge != null )
			return sootEdge.toString();
		
		return "(" + s + "->" + t + ", " + map_offset + ")";
	}
}
