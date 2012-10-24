/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
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
}
