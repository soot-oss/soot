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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.AnySubType;
import soot.ArrayType;
import soot.Local;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.EvalHelper;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.Histogram;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.GlobalVarNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.queue.QueueReader;

/**
 * We provide a set of methods to evaluate the quality of geometric points-to analysis.
 * The evaluation methods are:
 * 
 * 1. Count the computed points-to matrix for avg. points-to set size, constraints evaluation graph size, etc;
 * 2. Virtual function resolution comparison;
 * 3. Static casts checking;
 * 
 * @author xiao
 *
 */
public class GeomEvaluator {
	
	private GeomPointsTo ptsProvider;
	private PrintStream outputer;
	private boolean solved;					// Used in the anonymous class visitor
	
	public GeomEvaluator( GeomPointsTo gpts, PrintStream ps )
	{
		ptsProvider = gpts;
		outputer = ps;
	}
	
	/**
	 * We assess the quality of building the 1-cfa call graph with the geometric points-to result.
	 */
	private void test_1cfa_call_graph (LocalVarNode vn, 
			SootMethod caller, SootMethod callee_signature, Histogram ce_range) 
	{	
		long l, r;
		IVarAbstraction pn = ptsProvider.makeInternalNode(vn);
		Set<SootMethod> tgts = new HashSet<SootMethod>();
		Set<AllocNode> set = pn.get_all_points_to_objects();
		
		LinkedList<CgEdge> list = ptsProvider.getCallEdgesInto( ptsProvider.getIDFromSootMethod(caller) );
		
		for ( Iterator<CgEdge> it = list.iterator(); it.hasNext(); ) {
			CgEdge p = it.next();
			
			l = p.map_offset;
			r = l + ptsProvider.max_context_size_block[p.s];
			tgts.clear();
			
			for ( AllocNode obj : set ) {
				if ( !pn.pointer_interval_points_to(l, r, obj) )
					continue;
				
				Type t = obj.getType();
				
				if ( t == null )
					continue;
				else if ( t instanceof AnySubType )
					t = ((AnySubType)t).getBase();
				else if ( t instanceof ArrayType )
					t = RefType.v( "java.lang.Object" );

				try {
					tgts.add(
							Scene.v().getOrMakeFastHierarchy()
								.resolveConcreteDispatch( ((RefType)t).getSootClass(), callee_signature ) );
				} 
				catch (Exception e) {
				
				}
			}
			
			tgts.remove(null);
			ce_range.addNumber( tgts.size() );
		}
	}
	
	/**
	 * Summarize the geometric points-to analysis and report the basic metrics.
	 */
	public void reportBasicMetrics() 
	{
		int size;
		int n_legal_var = 0, n_alloc_dot_fields = 0;
		long total_geom_ins_pts = 0, total_geom_sen_pts = 0, total_spark_pts = 0;
		int max_pts_geom = 0, max_pts_spark = 0;
		final Set<Type> geom_types = new HashSet<Type>(), spark_types = new HashSet<Type>();
		
		int[] limits = new int[] { 1, 5, 10, 25, 50, 75, 100 };
		Histogram pts_size_bar_geom = new Histogram(limits), pts_size_bar_spark = new Histogram(limits);;
		Histogram type_size_bar_geom = new Histogram(limits), type_size_bar_spark = new Histogram(limits);
		
		n_legal_var = 0;
		n_alloc_dot_fields = 0;
		total_geom_ins_pts = 0;
		total_geom_sen_pts = 0;
		max_pts_geom = 0;
		
		for ( IVarAbstraction pn : ptsProvider.pointers ) {
			// We don't consider those un-processed pointers because their points-to information is equivalent to SPARK
			if ( !pn.willUpdate ) continue;
			if ( ptsProvider.isLegalPointer(pn) == false ) continue;
			pn = pn.getRepresentative();
			Node var = pn.getWrappedNode();
			if ( var instanceof AllocDotField ) ++n_alloc_dot_fields;
			++n_legal_var;
			
			// ...geom
			size = pn.num_of_diff_objs();
			pts_size_bar_geom.addNumber(size);
			total_geom_ins_pts += size;
			if (size > max_pts_geom) max_pts_geom = size;

			// ...spark
			size = var.getP2Set().size();
			pts_size_bar_spark.addNumber(size);
			total_spark_pts += size;
			if ( size > max_pts_spark ) max_pts_spark = size;
			
			// ...geom
			geom_types.clear();
			Set<AllocNode> obj_set = pn.get_all_points_to_objects();
			for (AllocNode obj : obj_set ) {
				if ( obj.getType() instanceof AnySubType ) {
					SootClass rc = ((AnySubType)obj.getType()).getBase().getSootClass();
					List<SootClass> list = null;
					if ( rc.isInterface() )
						list = Scene.v().getActiveHierarchy().getImplementersOf(rc);
					else
						list = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(rc);
					
					for ( SootClass sc : list )
						geom_types.add( sc.getType() );
				}
				else
					geom_types.add(obj.getType());

				total_geom_sen_pts += pn.count_pts_intervals(obj);
			}
			type_size_bar_geom.addNumber( geom_types.size() );
			
			// ...spark
			spark_types.clear();
			var.getP2Set().forall(new P2SetVisitor() {
				public final void visit(Node n) {
					AllocNode an = (AllocNode)n;
					
					if ( an.getType() instanceof AnySubType ) {
						SootClass rc = ((AnySubType)an.getType()).getBase().getSootClass();
						List<SootClass> list = null;
						if ( rc.isInterface() )
							list = Scene.v().getActiveHierarchy().getImplementersOf(rc);
						else
							list = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(rc);
						
						for ( SootClass sc : list )
							spark_types.add( sc.getType() );
					}
					else
						spark_types.add(an.getType());
				}
			});
			type_size_bar_spark.addNumber( spark_types.size() );
		}
		
		outputer.println("");
		outputer.println("--------------------Points-to Analysis Basic Information-------------------");
		outputer.println("------>>>> Format:  Geometric Analysis (SPARK)" );
		outputer.println("All Pointers : " + ptsProvider.n_var);
		outputer.println("Reachable Methods : " + ptsProvider.n_reach_methods + " (" + (ptsProvider.n_func - 1) + ")" );
		outputer.printf("Reachable User Methods : %d (%d)\n", ptsProvider.n_reach_user_methods, ptsProvider.n_reach_spark_user_methods );
		outputer.println("Legal pointers (app code): " + n_legal_var + ", in which the #AllocDot Fields : " + n_alloc_dot_fields );
		outputer.printf("Total/Average Projected Points-to Tuples (app code): %d (%d) / %.3f (%.3f) \n", 
				total_geom_ins_pts, total_spark_pts, 
				(double) total_geom_ins_pts / (n_legal_var), (double) total_spark_pts / n_legal_var );
		outputer.printf("Total/Average Context Sensitive Points-to Tuples (app code): %d / %.3f \n", 
				total_geom_sen_pts, (double) total_geom_sen_pts / (n_legal_var) );
		outputer.println("The largest points-to set size (app code): " + max_pts_geom + " (" + max_pts_spark + ")");
		
		outputer.println();
		pts_size_bar_geom.printResult( ptsProvider.ps, "Points-to Set Sizes Distribution (app code):", pts_size_bar_spark );
		type_size_bar_geom.printResult( ptsProvider.ps, "Points-to Set Types Distribution (app code):", type_size_bar_spark );
	}

	/**
	 * Report the virtual callsites resolution result for the user's code.
	 */
	public void checkCallGraph()
	{
		Map<Stmt, Set<SootMethod>> my_vir_tgts = new HashMap<Stmt, Set<SootMethod>>();
		int n_func;
		int total_virtual_calls = 0;
		int n_geom_call_edges = 0, n_geom_user_edges = 0;
		int n_spark_user_edges = 0, n_spark_cinit_edges = 0;
		int geom_solved = 0, spark_solved = 0;
		
		n_func = ptsProvider.n_func;
		
		// We first collect the internal call graph information
		for (int i = 1; i < n_func; ++i) {
			if ( !ptsProvider.isReachableMethod(i) )
				continue;
			
			CgEdge p = ptsProvider.getCallEgesOutFrom(i);
			int edge_cnt = 0;
			while (p != null) {
				if ( p.base_var != null ) {
					Stmt expr = p.sootEdge.srcStmt();
					if (expr != null) {
						Set<SootMethod> tgts = my_vir_tgts.get(expr);
						if (tgts == null) {
							tgts = new HashSet<SootMethod>();
							my_vir_tgts.put(expr, tgts);
						}
						tgts.add( p.sootEdge.tgt().method() );
					}
				}
				
				++edge_cnt;
				p = p.next;
			}
			
			n_geom_call_edges += edge_cnt;
			if ( !ptsProvider.getSootMethodFromID(i).isJavaLibraryMethod() )
				n_geom_user_edges += edge_cnt;
		}

		int[] limits = new int[] { 1, 2, 4, 8 };
		Histogram total_call_edges = new Histogram(limits);
		
		System.gc();
		System.gc();
		System.gc();
		
		CallGraph cGraph = Scene.v().getCallGraph();
		
		// Scan all the callsites
		for ( SootMethod sm : ptsProvider.getAllReachableMethods() ) {
			
			// Skip the uninteresting methods
			if (sm.isJavaLibraryMethod())
				continue;
			if (!sm.isConcrete())
				continue;
			if (!sm.hasActiveBody()) {
				sm.retrieveActiveBody();
			}
			if ( !ptsProvider.isValidMethod(sm) )
				continue;
			
			// All the statements in the method
			for (Iterator stmts = sm.getActiveBody().getUnits().iterator(); stmts.hasNext();) {
				Stmt st = (Stmt) stmts.next();
				if (st.containsInvokeExpr()) {
					InvokeExpr ie = st.getInvokeExpr();
					if (ie instanceof VirtualInvokeExpr) {
						total_virtual_calls++;
						Local l = (Local) ((VirtualInvokeExpr)ie).getBase();
						LocalVarNode vn = ptsProvider.findLocalVarNode(l);
						
						// Test my points-to analysis
						solved = false;
						if (my_vir_tgts.containsKey(st)) {
							Set<SootMethod> tgts = my_vir_tgts.get(st);
							if (tgts.size() == 1) {
								++geom_solved;
								solved = true;
							} else {
								// We try to test if this callsite is solvable under some contexts
								Histogram call_edges = new Histogram(limits);
								test_1cfa_call_graph(vn, sm, ie.getMethod(), call_edges);
								total_call_edges.merge(call_edges);
								call_edges = null;
							}
						} else {
							// It has zero target, dead code
							++geom_solved;
							solved = true;
						}
						
						
						int count = 0;
						for ( Iterator<Edge> it = cGraph.edgesOutOf(st); it.hasNext(); ) {
							it.next();
							++count;
						}
						
						if ( count <= 1 )
							spark_solved++;
						
						if ( count > 1 && solved == true &&
								ptsProvider.getOpts().verbose() ) {
							
							outputer.println();
							outputer.println("<<<<<<<<<   Additional Solved Call   >>>>>>>>>>");
							outputer.println(sm.toString());
							outputer.println(ie.toString());
							EvalHelper.debug_succint_pointsto_info(vn, ptsProvider);
						}
					}
				}
			}
			
			for ( Iterator<Edge> it = cGraph.edgesOutOf(sm); it.hasNext(); ) {
				Edge e = it.next();
				if ( !e.isClinit() )
					++n_spark_user_edges;
			}
		}

		// Now we count the cinit edges
		QueueReader<Edge> edgeList = Scene.v().getCallGraph().listener();
		while (edgeList.hasNext()) {
			Edge edge = edgeList.next();
			if ( edge.isClinit() )
				++n_spark_cinit_edges;
		}
		
		ptsProvider.ps.println();
		ptsProvider.ps.printf( "Call graph edges (total): Geom = %d, SPARK = %d\n", n_geom_call_edges, cGraph.size() - n_spark_cinit_edges );
		ptsProvider.ps.printf( "Call graph edges (app code): Geom = %d, SPARK = %d\n", n_geom_user_edges, n_spark_user_edges );
		ptsProvider.ps.println("Total virtual callsites (app code): " + total_virtual_calls);
		ptsProvider.ps.println("Resolved virtual callsites : Geom = " + geom_solved + ", SPARK = " + spark_solved );
		total_call_edges.printResult( ptsProvider.ps, "Random testing of the 1-CFA call graph : " );
		
		if ( ptsProvider.getOpts().verbose() )
			ptsProvider.outputNotEvaluatedMethods();
	}
	
	/**
	 * Count how many alias pairs in each function that are made up by all the variables accessed by that function.
	 */
	public void checkAliasAnalysis()
	{
		IVarAbstraction pn, qn;
		final Set<Node> access_expr = new HashSet<Node>();
		ArrayList<Node> al = new ArrayList<Node>();
		Value[] values = new Value[2];
		long cnt_all = 0;
		long cnt_hs_alias = 0, cnt_hi_alias = 0;
		
		
		for ( SootMethod sm : ptsProvider.getAllReachableMethods() ) {
			if (sm.isJavaLibraryMethod())
				continue;
			if (!sm.isConcrete())
				continue;
			if (!sm.hasActiveBody()) {
				sm.retrieveActiveBody();
			}
			if ( !ptsProvider.isValidMethod(sm) )
				continue;
			
			// We first gather all the memory access expressions
			access_expr.clear();
			for (Iterator stmts = sm.getActiveBody().getUnits().iterator(); stmts
					.hasNext();) {
				Stmt st = (Stmt) stmts.next();
				
				if ( st instanceof AssignStmt ) {
					AssignStmt a = (AssignStmt) st;
					values[0] = a.getLeftOp();
					values[1] = a.getRightOp();
					
					for ( Value v : values ) {
						if ( v instanceof Local ) {
							Local l = (Local)v;
							LocalVarNode vn = ptsProvider.findLocalVarNode(l);
							access_expr.add( vn );
						}
						else if (v instanceof StaticFieldRef) {
							StaticFieldRef sfr = (StaticFieldRef) v;
							GlobalVarNode vn = ptsProvider.findGlobalVarNode( sfr.getField() );
							access_expr.add( vn );
						}
						else if (v instanceof ArrayRef) {
							ArrayRef ar = (ArrayRef) v;
							LocalVarNode vn = ptsProvider.findLocalVarNode((Local) ar.getBase());
							access_expr.add( vn );
						}
					}
				}
			}
			
			// Next, we pair up all the pointers
			access_expr.remove(null);
			al.clear();
			
			for ( Node v : access_expr ) {
				if ( v.getType() instanceof RefType ) {
					SootClass sc = ((RefType)v.getType()).getSootClass();
					if ( !sc.isInterface() && Scene.v().getActiveHierarchy().isClassSubclassOfIncluding(
							sc, GeomPointsTo.exeception_type.getSootClass()) ) {
						continue;
					}
				}
				al.add(v);
			}
			
			for ( int i = 0; i < al.size(); ++i ) {
				Node n1 = al.get(i);
				pn =  ptsProvider.findInternalNode(n1);
				pn = pn.getRepresentative();
				
				for ( int j = i + 1; j < al.size(); ++j ) {
					Node n2 = al.get(j);
					qn = ptsProvider.findInternalNode(n2);
					qn = qn.getRepresentative();
					
					if ( pn.heap_sensitive_intersection( qn ) )
						cnt_hs_alias++;
					
					// We directly use the SPARK points-to sets
					if ( n1.getP2Set().hasNonEmptyIntersection(n2.getP2Set()) )
						cnt_hi_alias++;
				}
				
				cnt_all += al.size() - 1 - i;
			}
		}
		
		ptsProvider.ps.println();
		ptsProvider.ps.println( "--------> Alias Pairs Evaluation <---------" );
		ptsProvider.ps.println("All pointer pairs (app code) : " + cnt_all );
		ptsProvider.ps.println("Heap sensitive alias pairs (by Geom) : " + cnt_hs_alias
				+ ", Percentage = " + (double) cnt_hs_alias / cnt_all );
		ptsProvider.ps.println("Heap insensitive alias pairs (by SPARK) : " + cnt_hi_alias
				+ ", Percentage = " + (double) cnt_hi_alias / cnt_all );
		ptsProvider.ps.println();
	}
	
	/**
	 * Count how many static casts can be determined safe.
	 */
	public void checkCastsSafety() 
	{
		int total_casts = 0;
		int geom_solved_casts = 0, spark_solved_casts = 0;
		
		for ( SootMethod sm : ptsProvider.getAllReachableMethods() ) {
			if (sm.isJavaLibraryMethod())
				continue;
			if (!sm.isConcrete())
				continue;
			if (!sm.hasActiveBody()) {
				sm.retrieveActiveBody();
			}
			if ( !ptsProvider.isValidMethod(sm) )
				continue;
			
			// All the statements in the method
			for (Iterator stmts = sm.getActiveBody().getUnits().iterator(); stmts.hasNext();) {
				Stmt st = (Stmt) stmts.next();

				if (st instanceof AssignStmt) {
					Value rhs = ((AssignStmt) st).getRightOp();
					Value lhs = ((AssignStmt) st).getLeftOp();
					if (rhs instanceof CastExpr
							&& lhs.getType() instanceof RefLikeType) {

						
						Value v = ((CastExpr) rhs).getOp();
						VarNode node = ptsProvider.findLocalVarNode(v);
						if (node == null) continue;
						total_casts++;
						
						IVarAbstraction pn = ptsProvider.makeInternalNode(node).getRepresentative();
						final Type targetType = (RefLikeType) ((CastExpr) rhs).getCastType();
						
						// We first use the geometric points-to result to evaluate
						solved = true;
						Set<AllocNode> set = pn.get_all_points_to_objects();
						for ( AllocNode obj : set ) {
							solved = ptsProvider.castNeverFails( obj.getType(), targetType );
							if ( solved == false ) break;
						}
						
						if ( solved )
							geom_solved_casts++;
						
						// Second is the SPARK result
						solved = true;
						node.getP2Set().forall(new P2SetVisitor() {
							public void visit(Node arg0) {
								if ( solved == false ) return;
								solved = ptsProvider.castNeverFails(arg0.getType(), targetType);
							}
						});
						
						if (solved)
							spark_solved_casts++;
					}
				}
			}
		}

		ptsProvider.ps.println();
		ptsProvider.ps.println( "-----------> Static Casts Safety Evaluation <------------" );
		ptsProvider.ps.println( "Total casts (app code) : " + total_casts );
		ptsProvider.ps.println( "Safe casts: Geom = " + geom_solved_casts + ", SPARK = " + spark_solved_casts );
	}
	
	/**
	 * Estimate the size of the def-use graph for the heap memory.
	 * The graph is estimated without context information.
	 */
	public void estimateHeapDefuseGraph()
	{
		long ans_geom = 0, ans_spark = 0;
		final Map<IVarAbstraction, int[]> defUseCounterForGeom = new HashMap<IVarAbstraction, int[]>();
		final Map<AllocDotField, int[]> defUseCounterForSpark = new HashMap<AllocDotField, int[]>();
		
		for ( SootMethod sm : ptsProvider.getAllReachableMethods() ) {
//			if (sm.isJavaLibraryMethod())
//				continue;
			if (!sm.isConcrete())
				continue;
			if (!sm.hasActiveBody()) {
				sm.retrieveActiveBody();
			}
			if ( !ptsProvider.isValidMethod(sm) )
				continue;
			
			// We first gather all the memory access expressions
			for (Iterator stmts = sm.getActiveBody().getUnits().iterator(); stmts
					.hasNext();) {
				Stmt st = (Stmt) stmts.next();
				
				if ( !(st instanceof AssignStmt) ) continue;
				
				AssignStmt a = (AssignStmt) st;
				final Value lValue = a.getLeftOp();
				final Value rValue = a.getRightOp();
				
				InstanceFieldRef ifr = null;
				
				if (lValue instanceof InstanceFieldRef) {
					// Def statement
					ifr = (InstanceFieldRef)lValue;
				}
				else if ( rValue instanceof InstanceFieldRef ) {
					// Use statement
					ifr = (InstanceFieldRef)rValue;
				}
				
				if (ifr != null) {
					final SootField field = ifr.getField();
						
					LocalVarNode vn = ptsProvider.findLocalVarNode((Local) ifr.getBase());
					if ( vn == null ) continue;
					
					// Spark
					vn.getP2Set().forall(new P2SetVisitor() {
						
						@Override
						public void visit(Node n) {
							AllocDotField padf = ptsProvider.findAllocDotField( (AllocNode)n, field );
							int[] defUseUnit = defUseCounterForSpark.get(padf);
							if ( defUseUnit == null ) {
								defUseUnit = new int[2];
								defUseCounterForSpark.put(padf, defUseUnit);
							}
							
							if (lValue instanceof InstanceFieldRef) {
								defUseUnit[0]++;
							}
							else {
								defUseUnit[1]++;
							}
						}
					});
					
					// Geom
					IVarAbstraction pn = ptsProvider.findInternalNode(vn);
					if ( pn == null ) continue;
					pn = pn.getRepresentative();
					Set<AllocNode> objsSet = pn.get_all_points_to_objects();
					ans_geom += objsSet.size();
					
					for ( AllocNode obj : objsSet ) {
						/*
						 * We will create a lot of instance fields.
						 * Because in points-to analysis, we concern only the reference type.
						 * But here, we concern all the fields read write including the primitive type fields.
						 */
						IVarAbstraction padf = ptsProvider.findAndInsertInstanceField(obj, field);
						int[] defUseUnit = defUseCounterForGeom.get(padf);
						if ( defUseUnit == null ) {
							defUseUnit = new int[2];
							defUseCounterForGeom.put(padf, defUseUnit);
						}
						
						if (lValue instanceof InstanceFieldRef) {
							defUseUnit[0]++;
						}
						else {
							defUseUnit[1]++;
						}
					}
				}
			}
		}
		
		for ( int[] defUseUnit : defUseCounterForSpark.values() ) {
			ans_spark += ((long)defUseUnit[0]) * defUseUnit[1];
		}
		
		for ( int[] defUseUnit : defUseCounterForGeom.values() ) {
			ans_geom += ((long)defUseUnit[0]) * defUseUnit[1];
		}
		
		ptsProvider.ps.println();
		ptsProvider.ps.println( "-----------> Heap Def Use Graph Evaluation <------------" );
		ptsProvider.ps.println("The edges in the heap def-use graph is: " + ans_geom + "(" + ans_spark + ")" );
		ptsProvider.ps.println();
	}
}
