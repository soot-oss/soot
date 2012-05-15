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

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

import soot.SootClass;
import soot.SootMethod;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.PlainConstraint;
import soot.jimple.spark.geom.geomPA.ZArrayNumberer;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.GlobalVarNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.SparkField;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.P2SetVisitor;

/**
 * This class contains the implementation of all kinds of preprocessing algorithms performed prior to the pointer analysis.
 * The implemented techniques are:
 * 
 * 1. Intra-procedural equivalent pointer detection;
 * 2. Unreachable library code removal;
 * 3. Pointer serialization for worklist prioritizing.
 * 
 * @author xiao
 * 
 */
public class OfflineProcessor 
{
	class off_graph_edge
	{
		// Start and end of this edge
		int s, t;
		// If this edge is created via complex constraint (e.g. p.f = q), base_var = p
		int base_var = -1;
		
		off_graph_edge next;
	}
	
	// Used in anonymous class visitor
	private boolean visitedFlag;
	
	GeomPointsTo ptAnalyzer;
	ZArrayNumberer<IVarAbstraction> int2var;
	ArrayList<off_graph_edge> varGraph;
	int pre[], low[], count[], rep[], repsize[];
	boolean usefulVar[];
	Deque<Integer> queue;
	int pre_cnt;
	int n_var;
	
	public OfflineProcessor( int size, GeomPointsTo pta ) 
	{
		ptAnalyzer = pta;
		int2var = ptAnalyzer.pointers;
		n_var = size;
		varGraph = new ArrayList<off_graph_edge>(size);
		queue = new LinkedList<Integer>();
		pre = new int[size];
		low = new int[size];
		count = new int[size];
		rep = new int[size];
		repsize = new int[size];
		usefulVar = new boolean[n_var];
		
		for ( int i = 0; i < n_var; ++i ) varGraph.add(null);
	}
	
	public void runOptimizations( boolean useSpark, Set<VarNode> basePointers )
	{
		// We prepare the essential data structure first
		queue.clear();
		for ( int i = 0; i < n_var; ++i ) {
			varGraph.set(i, null);
			usefulVar[i] = false;
		}
		
		// We first run the constraints distillation
		buildInstanceAssignmentGraph( useSpark );
		// We second label the pointers that will be evaluated in the geometric points-to analysis
		setAllUserCodeVariablesUseful();
		addUsefulVariables(basePointers);
		eliminateUselessConstraints( useSpark );
		cleanSparkResults();
		
		// Then, we do the rest of the work on the symbolic assignment graph
		buildSymbolicAssignmentGraph( useSpark );
		makeTopologicalOrder();
		if ( useSpark == true ) {
			// We only do once the local variable merging
			mergeLocalVariables();
		}
	}
	
	public void destroy()
	{
		pre = null;
		low = null;
		count = null;
		rep = null;
		repsize = null;
		varGraph = null;
		queue = null;
	}
	
	/**
	 * Return true if the points-to information of the queried pointer is computed by our geometric points-to engine.
	 */
	public boolean isUsefulVar( Node node )
	{
		IVarAbstraction var = ptAnalyzer.findInternalNode(node);
		if ( var == null ) return false;
		int k = var.getNumber();
		return usefulVar[ k ];
	}
	
	public boolean isUsefulVar( IVarAbstraction pn )
	{
		int k = pn.getNumber();
		if ( k == -1 ) return false;
		return usefulVar[ k ];
	}
	
	/**
	 * Build the inverse assignment graph with all the points-to facts.
	 * Note that, the assignments that are eliminated by local variable merging should be used here.
	 * Otherwise, the graph is fragile.
	 */
	protected void buildInstanceAssignmentGraph(boolean useSpark)
	{
		IVarAbstraction[] container = new IVarAbstraction[2];
		
		// Note, the edge direction of the inverse assignment graph is opposite to the assigns-to relation
		// e.g. p = q, then we have an edge : p -> q
		for ( PlainConstraint cons : ptAnalyzer.constraints ) {
			// We should keep all the constraints that are deleted by the offline variable merging
			if ( cons.isViable == false &&
					cons.type != GeomPointsTo.ASSIGN_CONS )
				continue;
			
			// In our constraint representation, lhs -> rhs means rhs = lhs.
			final IVarAbstraction lhs = cons.expr.getO1();
			final IVarAbstraction rhs = cons.expr.getO2();
			final SparkField field = cons.f;
			
			// We delete the constraint that includes unreachable code
			container[0] = lhs;
			container[1] = rhs;
			for ( IVarAbstraction pn : container ) {
				if ( pn.getWrappedNode() instanceof LocalVarNode ) {
					SootMethod sm = ((LocalVarNode)pn.getWrappedNode()).getMethod();
					int sm_int = ptAnalyzer.getIDFromSootMethod(sm);
					if ( !ptAnalyzer.isReachableMethod(sm_int) ) {
						cons.isViable = false;
						break;
					}
				}
			}
			
			// Now we use this contraint for graph construction
			switch ( cons.type ) {
			
			// rhs = lhs
			case GeomPointsTo.ASSIGN_CONS:
				add_graph_edge( rhs.id, lhs.id );
				break;
				
			// rhs = lhs.f
			case GeomPointsTo.LOAD_CONS:
				if ( useSpark ) {
					lhs.getWrappedNode().getP2Set().forall( new P2SetVisitor() {
						@Override
						public void visit(Node n) {
							IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)n, field);
							if ( padf == null ) return;
							off_graph_edge e = add_graph_edge(rhs.id, padf.id);
							e.base_var = lhs.id;
						}
					});
				}
				else {
					// Use geom
					for ( AllocNode o : lhs.getRepresentative().get_all_points_to_objects() ) {
						IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)o, field);
						if ( padf == null ) return;
						off_graph_edge e = add_graph_edge(rhs.id, padf.id);
						e.base_var = lhs.id;
					}
				}
				
				break;
			
			// rhs.f = lhs
			case GeomPointsTo.STORE_CONS:
				if ( useSpark ) {
					rhs.getWrappedNode().getP2Set().forall( new P2SetVisitor() {
						@Override
						public void visit(Node n) {
							IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)n, field);
							if ( padf == null ) return;
							off_graph_edge e = add_graph_edge(padf.id, lhs.id);
							e.base_var = rhs.id;
						}
					});
				}
				else {
					// use geom
					for ( AllocNode o : rhs.getRepresentative().get_all_points_to_objects() ) {
						IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)o, field);
						if ( padf == null ) return;
						off_graph_edge e = add_graph_edge(padf.id, lhs.id);
						e.base_var = rhs.id;
					}
				}
				
				break;
			}
		}
	}
	
	/**
	 * The user can provide a set of variables that need refined points-to result.
	 * @param initVars
	 */
	protected void addUsefulVariables( Set<? extends Node> initVars )
	{
		for ( int i = 0; i < n_var; ++i ) {
			IVarAbstraction node = int2var.get(i);
			if ( initVars.contains( node.getWrappedNode() ) ) {
				queue.add(i);
				usefulVar[i] = true;
			}
		}
	}
	
	/**
	 * All the pointers that we need their points-to information are marked.
	 * @param virtualBaseSet
	 */
	protected void setAllUserCodeVariablesUseful()
	{
		for ( int i = 0; i < n_var; ++i ) {
			Node node = int2var.get(i).getWrappedNode();
			int sm_id = ptAnalyzer.getMappedMethodID(node);
			if ( sm_id == GeomPointsTo.UNKNOWN_FUNCTION )
				continue;
			
			if ( node instanceof VarNode ) {
				
				// flag == true if node is defined in the Java library
				boolean defined_in_lib = false;
				
				if ( node instanceof LocalVarNode ) {
					defined_in_lib = ((LocalVarNode)node).getMethod().isJavaLibraryMethod();
				}
				else if ( node instanceof GlobalVarNode ) {
					SootClass sc = ((GlobalVarNode)node).getDeclaringClass();
					if ( sc != null )
						defined_in_lib = sc.isJavaLibraryClass();
				}
				
				if ( !defined_in_lib ) {
					// Defined in the user code
					queue.add(i);
					usefulVar[i] = true;
				}
			}
		}
	}
	
	/**
	 * Heavy-weight library is the major reason for the non-scalability of pointer analysis.
	 * However, most of the points-to relation of the library code is not useful for computing the points-to relations in the user's code.
	 * Finding out and eliminating the irrelevant library code is our optimization target.
	 * 
	 * Methodology:
	 * We build the complete pointer assignment graph made by the Anderson's analysis.
	 * Performing a graph traversal to identify the variables that are defined in the library code and does not affect the user's code.
	 * Delete all the constraints containing those useless variables.
	 */
	protected void eliminateUselessConstraints( boolean useSpark )
	{
		int i;
		IVarAbstraction rhs;
		off_graph_edge p;
		
		// Worklist based graph traversal
		while ( !queue.isEmpty() ) {
			i = queue.getFirst();
			queue.removeFirst();
			
			p = varGraph.get(i);
			while ( p != null ) {
				if ( usefulVar[p.t] == false ) {
					usefulVar[p.t] = true;
					queue.add(p.t);
				}
				
				if ( p.base_var != -1 && usefulVar[p.base_var] == false ) {
					usefulVar[p.base_var] = true;
					queue.add(p.base_var);
				}
				
				p = p.next;
			}
		}
		
		// The last step, we revisit the constraints and eliminate the useless ones
		for ( PlainConstraint cons : ptAnalyzer.constraints ) {
			if ( cons.isViable == false ) continue;
			
			rhs = cons.expr.getO2();
			final SparkField field = cons.f;
			visitedFlag = false;
			
			switch ( cons.type ) {
			case GeomPointsTo.NEW_CONS:
			case GeomPointsTo.ASSIGN_CONS:
			case GeomPointsTo.LOAD_CONS:
				visitedFlag = usefulVar[rhs.id];
				break;
			
			case GeomPointsTo.STORE_CONS:
				if ( useSpark ) {
					rhs.getWrappedNode().getP2Set().forall( new P2SetVisitor() {
						@Override
						public void visit(Node n) {
							if ( !visitedFlag ) {
								IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)n, field);
								if ( padf == null ) return;
								visitedFlag = usefulVar[padf.id];
							}
						}
					});
				}
				else {
					// Use the geometric points-to result
					for ( AllocNode o : rhs.getRepresentative().get_all_points_to_objects() ) {
						IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)o, field);
						if ( padf == null ) return;
						visitedFlag = usefulVar[padf.id];
						if ( visitedFlag ) break;
					}
				}
				
				break;
			}
			
			cons.isViable = visitedFlag;
		}
	}
	
	/**
	 * The pointers that we will refine in our context sensitive analysis do not need the spark results any more.
	 */
	protected void cleanSparkResults() 
	{
		// Later the evaluator would use the SPARK points-to facts
		// Thereby, we keep it
		if ( ptAnalyzer.getOpts().geom_eval() > 0 )
			return;
		
		for ( int i = 0; i < n_var; ++i ) {
			if ( usefulVar[i] == true ) {
				IVarAbstraction node = int2var.get(i);
				node.getWrappedNode().discardP2Set();
			}
		}
		
		ptAnalyzer.cleanPAG();
	}
	
	/**
	 * We totally rebuild the graph. The previous graph is destroyed.
	 */
	protected void buildSymbolicAssignmentGraph(boolean useSpark)
	{
		for ( int i = 0; i < n_var; ++i ) {
			varGraph.set(i, null);
		}
		queue.clear();
		
		for ( PlainConstraint cons : ptAnalyzer.constraints ) {
			if ( cons.isViable == false ) 
				continue;

			final IVarAbstraction lhs = cons.expr.getO1();
			final IVarAbstraction rhs = cons.expr.getO2();
			final SparkField field = cons.f;
			
			switch ( cons.type ) {
			case GeomPointsTo.NEW_CONS:
				// We enqueue the pointers that are allocation result receivers
				queue.add(rhs.id);
				break;
				
			case GeomPointsTo.ASSIGN_CONS:
				add_graph_edge( lhs.id, rhs.id );
				break;
				
			case GeomPointsTo.LOAD_CONS:
				if ( useSpark ) {
					lhs.getWrappedNode().getP2Set().forall( new P2SetVisitor() {
						@Override
						public void visit(Node n) {
							IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)n, field);
							if ( padf == null ) return;
							add_graph_edge(padf.id, rhs.id);
						}
					});
				}
				else {
					// use geom
					for ( AllocNode o : lhs.getRepresentative().get_all_points_to_objects() ) {
						IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)o, field);
						if ( padf == null ) return;
						add_graph_edge(padf.id, rhs.id);
					}
				}
				break;
				
			case GeomPointsTo.STORE_CONS:
				if ( useSpark ) {
					rhs.getWrappedNode().getP2Set().forall( new P2SetVisitor() {
						@Override
						public void visit(Node n) {
							IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)n, field);
							if ( padf == null ) return;
							add_graph_edge(lhs.id, padf.id);
						}
					});
				}
				else {
					// use geom
					for ( AllocNode o : rhs.getRepresentative().get_all_points_to_objects() ) {
						IVarAbstraction padf = ptAnalyzer.findAndInsertInstanceField((AllocNode)o, field);
						if ( padf == null ) return;
						add_graph_edge(lhs.id, padf.id);
					}
				}
				
				break;
			}
		}
	}
	
	/**
	 *  Prepare for a near optimal worklist selection strategy inspired by Ben's PLDI 07 work.
	 */
	protected void makeTopologicalOrder()
	{
		int i;
		int s, t;
		off_graph_edge p;
		IVarAbstraction node;
		
		// prepare the data
		pre_cnt = 0;
		for ( i = 0; i < n_var; ++i ) {
			pre[i] = -1;
			count[i] = 0;
			rep[i] = i;
			repsize[i] = 1;
			node = int2var.get(i);
			node.top_value = Integer.MIN_VALUE;
		}
		
		// perform the SCC identification
		for ( i = 0; i < n_var; ++ i )
			if ( pre[i] == -1 )
				tarjan_scc(i);
		
		// In-degree counting
		for ( i = 0; i < n_var; ++i ) {
			p = varGraph.get(i);
			s = find_parent(i);
			while ( p != null ) {
				t = find_parent(p.t);
				if ( t != s )
					count[ t ]++;
				p = p.next;
			}
		}
		
		// Reconstruct the graph with condensed cycles
		for ( i = 0; i < n_var; ++i ) {
			p = varGraph.get(i);
			if ( p != null && rep[i] != i ) {
				t = find_parent(i);
				while ( p.next != null ) 
					p = p.next;
				p.next = varGraph.get(t);
				varGraph.set(t, varGraph.get(i) );
				varGraph.set(i, null);
			}
		}
		
		queue.clear();
		for ( i = 0; i < n_var; ++i )
			if ( rep[i] == i && 
					count[i] == 0 ) queue.addLast( i );
		
		// Assign the topological value to every node
		// We also reserve space for the cycle members, i.e. linearize all the nodes not only the SCCs
		i = 0;
		while ( !queue.isEmpty() ) {
			s = queue.getFirst();
			queue.removeFirst();
			node = int2var.get(s);
			node.top_value = i;
			i += repsize[s];
						
			p = varGraph.get(s);
			while ( p != null ) {
				t = find_parent(p.t);
				if ( t != s ) {
					if ( --count[t] == 0 )
						queue.addLast(t);
				}
				p = p.next;
			}
		}
		
		// Assign the non-representative node with the reserved positions
		for ( i = n_var - 1; i > -1; --i ) {
			if ( rep[i] != i ) {
				node = int2var.get( find_parent(i) );
				IVarAbstraction me = int2var.get(i);
				me.top_value = node.top_value + repsize[node.id] - 1;
				--repsize[node.id];
			}
		}
	}
	
	/**
	 * As pointed out by the single entry graph contraction, temporary variables incur high redundancy in points-to relations.
	 * Find and eliminate the redundancies as early as possible.
	 * 
	 * Our approach is :
	 * 1. Reuse the instance assignment graph;
	 * 2. If a variable q has only one incoming edge p -> q and p, q both local to the same function, then we merge them.
	 */
	protected void mergeLocalVariables()
	{
		IVarAbstraction my_lhs, my_rhs;
		Node lhs, rhs;
		
		// First time scan, in-degree counting
		// count is zero now
		for ( int i = 0; i < n_var; ++i ) {
			off_graph_edge p = varGraph.get(i);
			while ( p != null ) {
				count[ p.t ]++;
				p = p.next;
			}
		}
		
		// If this pointer is a allocation result receiver
		// We charge the degree counting with new constraint
		while ( !queue.isEmpty() ) {
			int id = queue.removeFirst();
			count[ id ]++;
		}
		
		// Second time scan, we delete those constraints that only duplicate points-to information
		for ( PlainConstraint cons : ptAnalyzer.constraints ) {
			if ( (cons.isViable == true ) &&
					(cons.type == GeomPointsTo.ASSIGN_CONS) ) {
				my_lhs = cons.expr.getO1();
				my_rhs = cons.expr.getO2();
				lhs = my_lhs.getWrappedNode();
				rhs = my_rhs.getWrappedNode();
				
				if ( (lhs instanceof LocalVarNode) &&
						(rhs instanceof LocalVarNode) ) {
					SootMethod sm1 = ((LocalVarNode)lhs).getMethod();
					SootMethod sm2 = ((LocalVarNode)rhs).getMethod();
					
					// They are local to the same function and the receiver variable has only one incoming edge
					if ( sm1 == sm2 && 
							count[my_rhs.id] == 1 
							&& lhs.getType() == rhs.getType() ) {
						// We directly merge the SPARK nodes to save effort for maintaining additional data structures
						// Maybe lhs and rhs have different types, however, we have already delete the SPARK points-to result.
						// Therefore, there is no exception thrown.
						my_rhs.merge(my_lhs);
						cons.isViable = false;
					}
				}
			}
		}
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private off_graph_edge add_graph_edge( int s, int t )
	{
		off_graph_edge e = new off_graph_edge();
		
		e.s = s;
		e.t = t;
		e.next = varGraph.get(s);
		varGraph.set(s, e);
		
		return e;
	}
	
	// Contract the graph
	private void tarjan_scc( int s )
	{
		int t;
		off_graph_edge p;
		
		pre[s] = low[s] = pre_cnt++;
		queue.addLast( s );
		p = varGraph.get(s);
		
		while ( p != null ) {
			t = p.t;
			if ( pre[t] == -1 ) tarjan_scc(t);
			if ( low[t] < low[s] ) low[s] = low[t];
			p = p.next;
		}
		
		if ( low[s] < pre[s] ) return;
		
		int w = s;
		
		do {
			t = queue.getLast();
			queue.removeLast();
			low[t] += n_var;
			w = merge_nodes(w, t);
		} while ( t != s );
	}
	
	// Find-union
	private int find_parent( int v )
	{
		return v == rep[v] ? v : (rep[v] = find_parent(rep[v]) );
	}
	
	// Find-union
	private int merge_nodes( int v1, int v2 )
	{
		v1 = find_parent(v1);
		v2 = find_parent(v2);
		
		if ( v1 != v2 ) {
			// Select v1 as the representative
			if ( repsize[v1] < repsize[v2]) {
				int t = v1;
				v1 = v2;
				v2 = t;
			}
			
			rep[v2] = v1;
			repsize[v1] += repsize[v2];
		}
		
		return v1;
	}
}