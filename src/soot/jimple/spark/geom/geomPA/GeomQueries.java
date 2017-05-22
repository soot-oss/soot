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
import soot.PointsToSet;
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
import soot.jimple.spark.pag.VarNode;
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
	protected GeomPointsTo geomPTA = null;
	
	// Call graph information
	protected int n_func;
	
	// A reduced call graph that does not have SCC internal edges
	protected CgEdge call_graph[];
	
	// Basic call graph info copied from geomPTA
	protected int vis_cg[], rep_cg[], scc_size[];
	protected int block_num[];
	protected long max_context_size_block[];
	
	// Topological order of the call graph SCC representative nodes
	protected int top_rank[];
	
	// Temporary data structures reused across queries
	private boolean prop_initialized = false;
	private Queue<Integer> topQ;
	private int in_degree[];
	private ContextsCollector[] contextsForMethods;
	
	/**
	 * We copy and make a condensed version of call graph.
	 * @param geom_pts
	 */
	public GeomQueries(GeomPointsTo geom_pta)
	{
		geomPTA = geom_pta;
		n_func = geomPTA.n_func;
		vis_cg = geomPTA.vis_cg;
		rep_cg = geomPTA.rep_cg;
		scc_size = geomPTA.scc_size;
		block_num = geomPTA.block_num;
		max_context_size_block = geomPTA.max_context_size_block;
		
		// Initialize an empty call graph
		call_graph = new CgEdge[n_func];
		Arrays.fill(call_graph, null);
		
		// We duplicate a call graph without SCC edges
		in_degree = new int[n_func];
		Arrays.fill(in_degree, 0);
		
		CgEdge[] raw_call_graph = geomPTA.call_graph;
		for (int i = 0; i < n_func; ++i) {
			if ( vis_cg[i] == 0 ) continue;
			CgEdge p = raw_call_graph[i];
			int rep = rep_cg[i];
			while ( p != null ) {
				// To speedup context searching, SCC edges are all removed
				if ( p.scc_edge == false ) {
					CgEdge q = p.duplicate();
					
					// The non-SCC edge is attached to the SCC representative
					q.next = call_graph[rep];
					call_graph[rep] = q;
					in_degree[ rep_cg[q.t] ]++;
				}
				p = p.next;
			}
		}
		
		// We also add the edges dropped in the last round of geomPTA
		// The are needed because the contexts mapping are built with them
//		for (CgEdge p : geomPts.obsoletedEdges) {
//			if ( p.scc_edge == true ) 
//				continue;
//			
//			// The non-SCC edge is attached to the SCC representative
//			int s = rep_cg[p.s];
//			
//			if ( vis_cg[s] != 0 ) {
//				CgEdge q = p.duplicate();
//				q.next = call_graph[s];
//				call_graph[s] = q;
//				in_degree[ rep_cg[q.t] ]++;
//			}
//		}
	}
	
	/**
	 * Only needed by part of the queries.
	 * Therefore, it is called on demand.
	 */
	private void prepareIntervalPropagations()
	{
		if ( prop_initialized ) return;
		
		// We layout the nodes hierarchically by topological sorting
		// The topological labels are used for speeding up reachability
		top_rank = new int[n_func];
		Arrays.fill(top_rank, 0);

		topQ = new LinkedList<Integer>();
		topQ.add(Constants.SUPER_MAIN);

		while (!topQ.isEmpty()) {
			int s = topQ.poll();
			CgEdge p = call_graph[s];

			while (p != null) {
				int t = p.t;
				int rep_t = rep_cg[t];
				int w = top_rank[s] + 1;
				if (top_rank[rep_t] < w)
					top_rank[rep_t] = w;
				if (--in_degree[rep_t] == 0)
					topQ.add(rep_t);
				p = p.next;
			}
		}

		// Prepare for querying artifacts
		contextsForMethods = new ContextsCollector[n_func];
		for (int i = 0; i < n_func; ++i) {
			ContextsCollector cc = new ContextsCollector();
			cc.setBudget(Parameters.qryBudgetSize);
			contextsForMethods[i] = cc;
		}
		
		prop_initialized = true;
	}
	
	/**
	 * Retrieve the subgraph from s->target.
	 * An edge s->t is included in the subgraph iff target is reachable from t.
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
			if (scc_size[s] == 1) {
				/*
				 * If s is not a member of mutually recursive call SCC,
				 * it's unnecessary to pollute all blocks of t.
				 */
				tContexts.insert(L, R);
				return;
			}
		}
		
		/*
		 *  We assume all blocks of target method are reachable for soundness and for simplicity.
		 */
		int n_blocks = block_num[t];
		long block_size = max_context_size_block[rep_cg[s]];
		
		// Compute the offset to the nearest context block for s
		// We use (L - 1) because the contexts are numbered from 1 
		long offset = (L-1) % block_size;
		long ctxtLength = R - L;
		long block_offset = 0;
		long lEnd, rEnd;
		
		// We iterate all blocks of target method
		for ( int i = 0; i < n_blocks; ++i ) {
			lEnd = 1 + offset + block_offset; 
			rEnd = lEnd + ctxtLength;
			tContexts.insert(lEnd, rEnd);
			block_offset += block_size;
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
		// We first identify the subgraph, where all edges in the subgraph lead to the target
		if ( !dfsScanSubgraph(start, target) ) return false;
		
		// Now we prepare for iteration
		int rep_start = rep_cg[start];
		int rep_target = rep_cg[target];
		
		ContextsCollector targetContexts = contextsForMethods[target];
		
		if ( rep_start == rep_target ) {
			// Fast path for the special case
			transferInSCC(start, target, L, R, targetContexts);
		}
		else {
			// We start traversal from the representative method
			transferInSCC(start, rep_start, L, R, contextsForMethods[rep_start]);
			
			// Start topsort
			topQ.clear();
			topQ.add(rep_start);
			
			while ( !topQ.isEmpty() ) {
				// Every function in the queue is representative function
				int s = topQ.poll();
				ContextsCollector sContexts = contextsForMethods[s];
				
				// Loop over the edges
				CgEdge p = call_graph[s];
				while ( p != null ) {		
					int t = p.t;
					int rep_t = rep_cg[t];
					
					if ( in_degree[rep_t] != 0 ) {
						// This node has a path to target
						ContextsCollector reptContexts = contextsForMethods[rep_t];
						long block_size = max_context_size_block[s];
						
						for ( SimpleInterval si : sContexts.bars ) {
							// Compute the offset within the block for si
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
					
					p = p.next;
				}
				
				sContexts.clear();
			}
		}
		
		return true;
	}
	
	/**
	 * Answer contexts-go-by query.
	 * 
	 * Usually, users specify the last K paths as the context. We call it k-CFA context.
	 * However, k-CFA is too restrictive. 
	 * In contexts-go-by query, user specifies arbitrary call edge in the call graph.
	 * The query searches for all contexts induced by the specified call edge and collect points-to results under these contexts.
	 * 
	 * @param sootEdge: the specified context edge in soot edge format
	 * @param l: the querying pointer
	 * @param visitor: container for querying result
	 * @return false, l does not have points-to information under the contexts induced by the given call edge
	 */
	@SuppressWarnings("rawtypes")
	public boolean contextsGoBy( Edge sootEdge, Local l, PtSensVisitor visitor )
	{
		// Obtain the internal representation of specified context
		CgEdge ctxt = geomPTA.getInternalEdgeFromSootEdge(sootEdge);
		if ( ctxt == null ||
				ctxt.is_obsoleted == true ) return false;
				
		// Obtain the internal representation for querying pointer
		LocalVarNode vn = geomPTA.findLocalVarNode(l);
		if ( vn == null ) {
			// Normally this could not happen, perhaps it's a bug
			return false;
		}
		
		IVarAbstraction pn = geomPTA.findInternalNode(vn);
		if ( pn == null ) {
			// This pointer is no longer reachable
			return false;
		}
		
		pn = pn.getRepresentative();
		if ( !pn.hasPTResult() ) return false;
		
		// Obtain the internal representation of the method that encloses the querying pointer
		SootMethod sm = vn.getMethod();
		int target = geomPTA.getIDFromSootMethod(sm);
		if ( target == -1 ) return false;
		
		// Start call graph traversal
		long L = ctxt.map_offset;
		long R = L + max_context_size_block[rep_cg[ctxt.s]];
		assert L < R;
		visitor.prepare();
		prepareIntervalPropagations();
		
		if ( propagateIntervals(ctxt.t, L, R, target) ) {
			// We calculate the points-to results
			ContextsCollector targetContexts = contextsForMethods[target];
			
			for ( SimpleInterval si : targetContexts.bars ) {
				assert si.L < si.R;
				pn.get_all_context_sensitive_objects(si.L, si.R, visitor);
			}
			
			// Reset
			targetContexts.clear();
		}
		
		visitor.finish();
		return visitor.numOfDiffObjects() != 0;
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public boolean contexsByAnyCallEdge( Edge sootEdge, Local l, PtSensVisitor visitor )
	{
		return contextsGoBy(sootEdge, l, visitor);
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
	@SuppressWarnings("rawtypes")
	public boolean contextsGoBy(Edge sootEdge, Local l, SparkField field, PtSensVisitor visitor)
	{
		Obj_full_extractor pts_l = new Obj_full_extractor();
		if ( contextsGoBy(sootEdge, l, pts_l) == false )
			return false;
		
		visitor.prepare();
		for ( IntervalContextVar icv : pts_l.outList ) {
			AllocNode obj = (AllocNode)icv.var;
			AllocDotField obj_f = geomPTA.findAllocDotField(obj, field);
			if ( obj_f == null ) continue;
			IVarAbstraction objField = geomPTA.findInternalNode(obj_f);
			if ( objField == null ) continue;
			
			long L = icv.L;
			long R = icv.R;
			assert L < R;
			objField.get_all_context_sensitive_objects(L, R, visitor);
		}
		
		pts_l = null;
		
		visitor.finish();
		return visitor.numOfDiffObjects() != 0;
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public boolean contextsByAnyCallEdge(Edge sootEdge, Local l, SparkField field, PtSensVisitor visitor)
	{
		return contextsGoBy(sootEdge, l, visitor);
	}
	
	/**
	 * Standard K-CFA querying for arbitrary K.
	 * 
	 * @param callEdgeChain: last K call edges leading to the method that contains l. callEdgeChain[0] is the farthest call edge in the chain.
	 * @param l: the querying pointer
	 * @param visitor: the querying result container
	 * @return false, l does not have points-to information under the given context
	 */
	@SuppressWarnings("rawtypes")
	public boolean kCFA(Edge[] callEdgeChain, Local l, PtSensVisitor visitor)
	{
		// Prepare for initial contexts
		SootMethod firstMethod = callEdgeChain[0].src();
		int firstMethodID = geomPTA.getIDFromSootMethod(firstMethod);
		if ( firstMethodID == -1 ) return false;
				
		// Obtain the internal representation for querying pointer
		LocalVarNode vn = geomPTA.findLocalVarNode(l);
		if ( vn == null ) {
			// Normally this could not happen, perhaps it's a bug
			return false;
		}
		
		IVarAbstraction pn = geomPTA.findInternalNode(vn);
		if (pn == null) {
			// This pointer is no longer reachable
			return false;
		}
		
		pn = pn.getRepresentative();
		if ( !pn.hasPTResult() ) return false;
		
		SootMethod sm = vn.getMethod();
		if ( geomPTA.getIDFromSootMethod(sm) == -1 )
			return false;
		
		// Iterate the call edges and compute the contexts mapping iteratively
		visitor.prepare();
		
		long L = 1;
		for ( int i = 0; i < callEdgeChain.length; ++i ) {
			Edge sootEdge = callEdgeChain[i];
			CgEdge ctxt = geomPTA.getInternalEdgeFromSootEdge(sootEdge);
			if ( ctxt == null ||
					ctxt.is_obsoleted == true ) return false;
			
			// Following searching procedure works for both methods in SCC and out of SCC
			// with blocking scheme or without blocking scheme
			int caller = geomPTA.getIDFromSootMethod(sootEdge.src());
			
			// We obtain the block that contains current offset L
			long block_size = max_context_size_block[rep_cg[caller]];
			long in_block_offset = (L-1) % block_size;
			// Transfer to the target block with the same in-block offset
			L = ctxt.map_offset + in_block_offset;
		}
		
		long ctxtLength = max_context_size_block[rep_cg[firstMethodID]];
		long R = L + ctxtLength;
		pn.get_all_context_sensitive_objects(L, R, visitor);
		
		visitor.finish();
		return visitor.numOfDiffObjects() != 0;
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public boolean contextsByCallChain(Edge[] callEdgeChain, Local l, PtSensVisitor visitor)
	{
		return kCFA(callEdgeChain, l, visitor);
	}
	
	/**
	 * Standard K-CFA querying for field expression.
	 * 
	 * @param callEdgeChain: callEdgeChain[0] is the farthest call edge in the chain.
	 * @param l
	 * @param field
	 * @param visitor
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean kCFA(Edge[] callEdgeChain, Local l, SparkField field, PtSensVisitor visitor)
	{
		// We first obtain the points-to information for l
		Obj_full_extractor pts_l = new Obj_full_extractor();
		if ( kCFA(callEdgeChain, l, pts_l) == false )
			return false;
		
		// We compute the points-to information for l.field
		visitor.prepare();
		
		for ( IntervalContextVar icv : pts_l.outList ) {
			AllocNode obj = (AllocNode)icv.var;
			AllocDotField obj_f = geomPTA.findAllocDotField(obj, field);
			if ( obj_f == null ) continue;
			IVarAbstraction objField = geomPTA.findInternalNode(obj_f);
			if ( objField == null ) continue;
			
			long L = icv.L;
			long R = icv.R;
			assert L < R;
			objField.get_all_context_sensitive_objects(L, R, visitor);
		}
		
		pts_l = null;
		
		visitor.finish();
		return visitor.numOfDiffObjects() != 0;
	}
	
	@Deprecated
	@SuppressWarnings("rawtypes")
	public boolean contextsByCallChain(Edge[] callEdgeChain, Local l, SparkField field, PtSensVisitor visitor)
	{
		return kCFA(callEdgeChain, l, field, visitor);
	}
	
	/**
	 * Are the two pointers an alias with context insensitive points-to information?
	 */
	public boolean isAliasCI(Local l1, Local l2)
	{
		PointsToSet pts1 = geomPTA.reachingObjects(l1);
		PointsToSet pts2 = geomPTA.reachingObjects(l2);
		return pts1.hasNonEmptyIntersection(pts2);
	}
	
	/**
	 * Test if two pointers given in geomPTA form are an alias under any contexts.
	 * @param pn1 and @param pn2 cannot be null.
	 */
	public boolean isAlias(IVarAbstraction pn1, IVarAbstraction pn2)
	{
		pn1 = pn1.getRepresentative();
		pn2 = pn2.getRepresentative();
		
		if ( !pn1.hasPTResult() || !pn2.hasPTResult() ) {
			VarNode vn1 = (VarNode)pn1.getWrappedNode();
			VarNode vn2 = (VarNode)pn2.getWrappedNode();
			return isAliasCI((Local)vn1.getVariable(), 
					(Local)vn2.getVariable());
		}
		
		return pn1.heap_sensitive_intersection(pn2);
	}
	
	/**
	 * Decide if under any contexts, pointers @param l1 and @param l2 can be an alias.
	 */
	public boolean isAlias(Local l1, Local l2)
	{
		// Obtain the internal representation for querying pointers
		LocalVarNode vn1 = geomPTA.findLocalVarNode(l1);
		LocalVarNode vn2 = geomPTA.findLocalVarNode(l2);
		if (vn1 == null || vn2 == null) {
			// Normally this could not happen, perhaps it's a bug
			return false;
		}

		IVarAbstraction pn1 = geomPTA.findInternalNode(vn1);
		IVarAbstraction pn2 = geomPTA.findInternalNode(vn2);
		if (pn1 == null || pn2 == null) {
			return isAliasCI(l1, l2);
		}

		pn1 = pn1.getRepresentative();
		pn2 = pn2.getRepresentative();
		if ( !pn1.hasPTResult() ||
				!pn2.hasPTResult() ) {
			return isAliasCI(l1, l2);
		}
		
		return pn1.heap_sensitive_intersection(pn2);
	}
}
