/* Soot - a J*va Optimization Framework
 * Copyright (C) 2013 Richard Xiao
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import soot.Local;
import soot.SootMethod;
import soot.jimple.spark.geom.dataMgr.ContextsCollector;
import soot.jimple.spark.geom.dataMgr.Obj_full_extractor;
import soot.jimple.spark.geom.dataMgr.PtSensVisitor;
import soot.jimple.spark.geom.dataRep.CgEdge;
import soot.jimple.spark.geom.dataRep.IntervalContextVar;
import soot.jimple.spark.geom.dataRep.SimpleInterval;
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
	protected int vis_cg[], rep_cg[], top_rank[];
	protected int block_num[];
	protected long max_context_size_block[];
	
	protected Queue<Integer> topQ;
	protected int in_degree[];
	protected ContextsCollector[] contextsForMethods;
	
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
		Arrays.fill(call_graph, null);
		
		// We duplicate a call graph without SCC edges
		in_degree = new int[n_func];
		Arrays.fill(in_degree, 0);
		
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
						in_degree[ rep_cg[q.t] ]++;
					}
				}
				p = p.next;
			}
		}
		
		// We layout the nodes hierarchically and give each of them a label
		top_rank = new int[n_func];
		Arrays.fill(top_rank, 0);
		
		topQ = new LinkedList<Integer>();
		topQ.add(Constants.SUPER_MAIN);

		while ( !topQ.isEmpty() ) {
			int s = topQ.poll();
			CgEdge p = call_graph[s];
			
			while ( p != null ) {
				int t = p.t;
				int rep_t = rep_cg[t];
				int w = top_rank[s] + 1;
				if ( top_rank[rep_t] < w ) top_rank[rep_t] = w;
				if ( --in_degree[rep_t] == 0 ) topQ.add(rep_t);
				p = p.next;
			}
		}
		
		// Prepare for querying artifacts
		contextsForMethods = new ContextsCollector[n_func];
		for ( int i = 0; i < n_func; ++i ) {
			ContextsCollector cc = new ContextsCollector();
			contextsForMethods[i] = cc;
		}
	}
	
	/**
	 * Retrieve the subgraph from s->target.
	 * @param s
	 * @param target
	 * @return
	 */
	protected boolean dfsScanSubgraph(int s, int target)
	{
		int rep_s = rep_cg[s];
		int rep_target = rep_cg[target];
		
		if ( rep_s == rep_target ) return true;
		
		s = rep_s;
		boolean reachable = false;
		
		// We only traverse the SCC representatives
		CgEdge p = call_graph[s];
		while ( p != null ) {		
			int t = p.t;
			int rep_t = rep_cg[t];
			if ( in_degree[rep_t] != 0 ||
					( top_rank[rep_t] <= top_rank[rep_target] && dfsScanSubgraph(t, target) == true ) ) {
				in_degree[rep_t]++;
				reachable = true;
			}
			
			p = p.next;
		}
		
		return reachable;
	}
	
	protected void transferInSCC(int s, int t, long L, long R, ContextsCollector tContexts)
	{
		if ( s == t ) {
			tContexts.insert(L, R);
			return;
		}
		
		/*
		 *  We assume all blocks of target method are reachable for soundness and for simplicity.
		 */
		int n_blocks = block_num[t];
		long block_size = max_context_size_block[s];
		
		// Compute the offset to the nearest context block for s
		// We use (lEnd - 1) because the context numbers start from 1 
		long offset = (L-1) % block_size;
		long ctxtLength = R - L;
		long sum = 0;
		long lEnd, rEnd;
		
		// We iterate all blocks of target method
		for ( int i = 0; i < n_blocks; ++i ) {
			lEnd = 1 + offset + sum; 
			rEnd = lEnd + ctxtLength;
			tContexts.insert(lEnd, rEnd);
			sum += block_size;
		}
	}
	
	/**
	 * Compute the mapping from interval [L, R) of method start to the intervals of method target.
	 * Return true if the mapping is feasible.
	 * 
	 * @param start
	 * @param L
	 * @param R
	 * @param target
	 * @return
	 */
	protected boolean propagateIntervals(int start, long L, long R, int target)
	{
		// We start traversal from the callee, because the caller->callee mapping is straightforward
		if ( !dfsScanSubgraph(start, target) ) return false;
		
		// Now we prepare for iteration
		int rep_start = rep_cg[start];
		int rep_target = rep_cg[target];
		
		ContextsCollector targetContexts = contextsForMethods[target];
		targetContexts.setBudget(Parameters.qry_targetBudgetSize);
		
		if ( rep_start == rep_target ) {
			// Fast path for special case
			transferInSCC(start, target, L, R, targetContexts);
		}
		else {
			transferInSCC(start, rep_start, L, R, contextsForMethods[rep_start]);
			
			// Start topsort
			topQ.clear();
			topQ.add(rep_start);
			
			while ( !topQ.isEmpty() ) {
				int s = topQ.poll();
				int rep_s = rep_cg[s];
				
				// We reset the container parameters every time before use
				ContextsCollector sContexts = contextsForMethods[rep_s];
				sContexts.setBudget(Parameters.qry_defaultBudgetSize);
				
				// Loop over the edges
				CgEdge p = call_graph[s];
				while ( p != null ) {		
					int t = p.t;
					
					if ( t != s ) {
						// Discard the self-loop edges
						int rep_t = rep_cg[t];
						if ( in_degree[rep_t] != 0 ) {
							// This node has a path to target
							ContextsCollector reptContexts = contextsForMethods[rep_t];
							long block_size = max_context_size_block[rep_s];
							
							for ( SimpleInterval si : sContexts.bars ) {
								// Compute the offset to the nearest context block for s
								// We use (lEnd - 1) because the context numbers start from 1		
								long in_block_offset = (si.L-1) % block_size;
								long newL = p.map_offset + in_block_offset;
								long newR = si.R - si.L + newL;
								
								if ( rep_t == rep_target ) {
									// t and target are in the same SCC
									// We directly transfer this context interval to target
									transferInSCC(t, target, newL, newR, targetContexts);
								}
								else {
									// We transfer this interval to its SCC representative
									// It might be t == rep_t
									transferInSCC(t, rep_t, newL, newR, reptContexts);
								}
							}
							
							if ( --in_degree[rep_t] == 0 &&
									rep_t != rep_target ) {
								topQ.add(rep_t);
							}
						}
					}
					
					p = p.next;
				}
				
				sContexts.clear();
			}
		}
		
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
		// Obtain the internal representation of specified context
		CgEdge ctxt = geomPts.getInternalEdgeFromSootEdge(sootEdge);
		if ( ctxt == null ) return false;
				
		// Obtain the internal representation for querying pointer
		LocalVarNode vn = geomPts.findLocalVarNode(l);
		IVarAbstraction pn = geomPts.findInternalNode(vn);
		pn = pn.getRepresentative();
		if ( pn == null ) {
			// This pointer is no longer reachable
			return false;
		}
		
		// Obtain the internal representation of the method that encloses the querying pointer
		SootMethod sm = vn.getMethod();
		int target = geomPts.getIDFromSootMethod(sm);
		if ( target == -1 ) return false;
		
		long L = ctxt.map_offset;
		long R = L + max_context_size_block[ctxt.s];
		
		if ( propagateIntervals(ctxt.t, L, R, target) ) {
			// We calculate the points-to results
			ContextsCollector targetContexts = contextsForMethods[target];
			
			for ( SimpleInterval si : targetContexts.bars ) {
				pn.get_all_context_sensitive_objects(si.L, si.R, visitor);
			}
			
			// Reset
			targetContexts.clear();
			return true;
		}
		
		return false;
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
			if ( obj_f == null ) continue;
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
		// Prepare for initial contexts
		SootMethod firstMethod = callEdgeChain[0].src();
		int firstMethodID = geomPts.getIDFromSootMethod(firstMethod);
		if ( firstMethodID == -1 ) return false;
				
		// Obtain the internal representation for querying pointer
		LocalVarNode vn = geomPts.findLocalVarNode(l);
		IVarAbstraction pn = geomPts.findInternalNode(vn);
		pn = pn.getRepresentative();
		if (pn == null) {
			// This pointer is no longer reachable
			return false;
		}
		
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
			long in_block_offset = (L-1) % block_size;
			// Transfer to the target block with the same in-block offset
			L = ctxt.map_offset + in_block_offset;
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
			if ( obj_f == null ) continue;
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
