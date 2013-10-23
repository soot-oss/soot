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

import soot.Local;
import soot.SootMethod;
import soot.jimple.spark.geom.dataRep.IntervalContextVar;
import soot.jimple.spark.geom.helper.Obj_full_extractor;
import soot.jimple.spark.geom.helper.PtSensVisitor;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.SparkField;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * A powerful interface for querying points-to results in many ways.
 * It is an extension for SPARK standard querying system.
 * 
 * @author xiao
 *
 */
public class GeomQueries 
{
	protected GeomPointsTo geomPts = null;
	
	// Call graph information
	protected int n_func;
	protected CgEdge call_graph[];
	protected int vis_cg[], rep_cg[];
	protected int block_num[];
	protected long max_context_size_block[];
	
	// Constant information in context path searching
	protected class ctxtSearchPacket {
		// Querying pointer
		public IVarAbstraction pn;
		// The method that encloses the querying pointer
		public int targetMethod;
		// Context length of querying pointer
		public long ctxtLength;
	}
		
	// DFS traversal recordings
	protected ctxtSearchPacket packet;
	
	/**
	 * We copy and make a condensed version of call graph.
	 * @param geom_pts
	 */
	public GeomQueries(GeomPointsTo geom_pts)
	{
		geomPts = geom_pts;
		n_func = geomPts.n_func;
		vis_cg = geomPts.vis_cg;
		rep_cg = geomPts.rep_cg;
		block_num = geomPts.block_num;
		max_context_size_block = geomPts.max_context_size_block;
		
		// Initialize an empty call graph
		call_graph = new CgEdge[n_func];
		for ( int i = 0; i < n_func; ++i ) {
			call_graph[i] = null;
		}
		
		// We duplicate a call graph without SCC edges
		CgEdge[] raw_call_graph = geomPts.call_graph;
		for (int i = 0; i < n_func; ++i) {
			if ( vis_cg[i] == 0 ) continue;
			CgEdge p = raw_call_graph[i];
			int rep = rep_cg[i];
			while ( p != null ) {
				// To speedup context searching, SCC edges are all removed
				if ( p.scc_edge == false ) {
					CgEdge q = p.duplicate();
					if ( q != null ) {
						// And, a non-SCC edge is attached to the SCC representative node
						q.next = call_graph[rep];
						call_graph[rep] = q; 
					}
				}
				p = p.next;
			}
		}
		
		// Prepare for DFS traversal
		packet = new ctxtSearchPacket();
	}
	
	
	protected void dfsCalcMapping(int s, long lEnd, PtSensVisitor visitor)
	{
		int target = packet.targetMethod;
		
		/*
		 * There are two cases for terminating DFS traversal:
		 * 1. We exactly reach the target method;
		 * 2. We reach a method that lies in the same SCC with the target method.
		 */
		// Case 1
		if ( s == target ) {
			IVarAbstraction pn = packet.pn;
			long rEnd = lEnd + packet.ctxtLength;
			pn.get_all_context_sensitive_objects(lEnd, rEnd, visitor);
			return;
		}
	
		/*
		 *  Case 2.
		 *  It's very complicated, because the target method is in an SCC.
		 *  We first assume all blocks of target method are reachable, for soundness.
		 *  When blocking scheme is enabled, we should re-map the contexts to every block respectively.
		 */
		if ( rep_cg[s] == rep_cg[target] ) {
			IVarAbstraction pn = packet.pn;
			
			// Perhaps the blocking scheme is enabled
			int n_blocks = block_num[target];
			long block_size = max_context_size_block[s];
			
			// Compute the offset to the nearest context block for s
			// We use (lEnd - 1) because the context numbers start from 1 
			long offset = (lEnd-1) % block_size;				
			
			// We iterate all blocks of target method
			for ( int i = 0; i < n_blocks; ++i ) {
				lEnd = offset + 1 + (i * block_size); 
				long rEnd = lEnd + packet.ctxtLength;
				pn.get_all_context_sensitive_objects(lEnd, rEnd, visitor);
			}
			
			return;
		}
		
		// We only traverse the SCC representatives
		s = rep_cg[s];
		CgEdge p = call_graph[s];
		
		while ( p != null ) {
			// All edges go to another SCC
			int t = p.t;
			
			// Transfer to the target block with the same in-block offset
			long block_size = max_context_size_block[s];
			int j = (int) ((lEnd-1) / block_size);
			long newL = p.map_offset + lEnd - (j * block_size + 1);
			
			dfsCalcMapping(t, newL, visitor);
			p = p.next;
		}
	}
	
	protected boolean searchCallString(CgEdge ctxt, int targetMethod, IVarAbstraction pn, PtSensVisitor visitor)
	{
		// Querying pointer
		packet.pn = pn;
		
		// Enclosing method of querying pointer
		packet.targetMethod = targetMethod;
		
		// We start iteration from callee, because the querying edge might be in an SCC
		// We do not walk the SCC edges during context path searching
		int fStart = ctxt.t;
		long lEnd = ctxt.map_offset;
		
		// The context length for specified context edge
		packet.ctxtLength = geomPts.max_context_size_block[ctxt.s];
		
		dfsCalcMapping(fStart, lEnd, visitor);
		return true;
	}
	
	/**
	 * Usually, users specify the last K paths as the context. We call it k-CFA context.
	 * However, k-CFA is too restrictive, users want to specify the call edges anywhere in the context path.
	 * A common usage is specifying only one edge in the context path.
	 * We implement this common usage here.
	 * 
	 * @param soot_edge: the specified context edge in soot edge format
	 * @param l: the querying pointer
	 * @param visitor: container for querying result
	 * @return false, if the passed in context call edge is obsoleted
	 */
	public boolean contexsByAnyCallEdge( Edge sootEdge, Local l, PtSensVisitor visitor )
	{
		// Obtain the internal representation for querying pointer
		LocalVarNode vn = geomPts.findLocalVarNode(l);
		IVarAbstraction pn = geomPts.findInternalNode(vn);
		pn = pn.getRepresentative();
		if ( pn == null ) {
			// This pointer is no longer reachable
			return false;
		}
		
		// Obtain the internal representation of specified context
		CgEdge ctxt = geomPts.getInternalEdgeFromSootEdge(sootEdge);
		if ( ctxt == null ) return false;
		
		// Obtain the internal representation of the method that encloses the querying pointer
		SootMethod sm = vn.getMethod();
		int smID = geomPts.getIDFromSootMethod(sm);
		if ( smID == -1 ) return false;
		
		return searchCallString(ctxt, smID, pn, visitor);
	}
	
	/**
	 * Searching the points-to results for field expression such as p.f.
	 * 
	 * @param sootEdge
	 * @param l
	 * @param field
	 * @param visitor
	 * @return
	 */
	public boolean contextsByAnyCallEdge(Edge sootEdge, Local l, SparkField field, PtSensVisitor visitor)
	{
		Obj_full_extractor pts_l = new Obj_full_extractor();
		if ( contexsByAnyCallEdge(sootEdge, l, pts_l) == false )
			return false;
		
		for ( IntervalContextVar icv : pts_l.outList ) {
			AllocNode obj = (AllocNode)icv.var;
			AllocDotField obj_f = geomPts.findAllocDotField(obj, field);
			IVarAbstraction objField = geomPts.findInternalNode(obj_f);
			if ( objField == null ) continue;
			
			long L = icv.L;
			long R = icv.R;
			objField.get_all_context_sensitive_objects(L, R, visitor);
		}
		
		pts_l = null;
		return true;
	}
	
	
	/**
	 * Standard K-CFA querying for arbitrary K.
	 * 
	 * @param callEdgeChain: last K call edges leading to the method that contains l. callEdgeChain[0] is the farthest call edge in the chain.
	 * @param l: the querying pointer
	 * @param visitor: the querying result container
	 * @return false, if any of the call edge in the call chain is obsoleted
	 */
	public boolean contextsByCallChain(Edge[] callEdgeChain, Local l, PtSensVisitor visitor)
	{
		// Obtain the internal representation for querying pointer
		LocalVarNode vn = geomPts.findLocalVarNode(l);
		IVarAbstraction pn = geomPts.findInternalNode(vn);
		pn = pn.getRepresentative();
		if (pn == null) {
			// This pointer is no longer reachable
			return false;
		}

		// Prepare for initial contexts
		SootMethod firstMethod = callEdgeChain[0].src();
		int firstMethodID = geomPts.getIDFromSootMethod(firstMethod);
		if ( firstMethodID == -1 ) return false;
		
		// Iterate the call edges and compute the contexts mapping iteratively
		long L = 1;
		for ( int i = 0; i < callEdgeChain.length; ++i ) {
			Edge sootEdge = callEdgeChain[i];
			CgEdge ctxt = geomPts.getInternalEdgeFromSootEdge(sootEdge);
			if ( ctxt == null ) return false;
			int caller = geomPts.getIDFromSootMethod(sootEdge.src());
			
			// Following searching procedure works for both methods in SCC and out of SCC
			// with blocking scheme or without blocking scheme
			
			// We obtain the block that contains current offset L
			long block_size = max_context_size_block[caller];
			int j = (int) ((L-1) / block_size);

			// Transfer to the target block with the same in-block offset
			L = ctxt.map_offset + L - (j * block_size + 1);
		}
		
		
		long ctxtLength = max_context_size_block[firstMethodID];
		long R = L + ctxtLength;
		
		pn.get_all_context_sensitive_objects(L, R, visitor);
		return true;
	}
	
	/**
	 * Standard K-CFA querying for field expression.
	 * 
	 * @param callEdgeChain
	 * @param l
	 * @param field
	 * @param visitor
	 * @return
	 */
	public boolean contextByCallChain(Edge[] callEdgeChain, Local l, SparkField field, PtSensVisitor visitor)
	{
		Obj_full_extractor pts_l = new Obj_full_extractor();
		if ( contextsByCallChain(callEdgeChain, l, pts_l) == false )
			return false;
		
		for ( IntervalContextVar icv : pts_l.outList ) {
			AllocNode obj = (AllocNode)icv.var;
			AllocDotField obj_f = geomPts.findAllocDotField(obj, field);
			IVarAbstraction objField = geomPts.findInternalNode(obj_f);
			if ( objField == null ) return false;
			
			long L = icv.L;
			long R = icv.R;
			objField.get_all_context_sensitive_objects(L, R, visitor);
		}
		
		pts_l = null;
		return true;
	}
}
