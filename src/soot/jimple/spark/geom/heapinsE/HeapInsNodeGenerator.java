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
package soot.jimple.spark.geom.heapinsE;

import java.util.Iterator;

import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.DummyNode;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IEncodingBroker;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.PlainConstraint;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.SparkOptions;

public class HeapInsNodeGenerator extends IEncodingBroker 
{	
	private static final int full_convertor[] = { 
		GeomPointsTo.ONE_TO_ONE, GeomPointsTo.MANY_TO_MANY, 
		GeomPointsTo.MANY_TO_MANY, GeomPointsTo.MANY_TO_MANY 
	};
	
	@Override
	public IVarAbstraction generateNode(Node vNode ) 
	{
		IVarAbstraction ret = null;
		
		if ( vNode instanceof AllocNode ||
				vNode instanceof FieldRefNode ) {
			ret = new DummyNode(vNode);
		}
		else {
			ret = new HeapInsNode(vNode);
		}
		
		return ret;
	}

	@Override
	public int getEncodingType() {
		return SparkOptions.geom_encoding_HeapIns;
	}

	@Override
	public void initFlowGraph(GeomPointsTo ptAnalyzer) 
	{
		int k;
		int n_legal_cons;
		int nf1, nf2;
		int code;
		;
		CgEdge q;
		IVarAbstraction my_lhs, my_rhs;
		
		// Visit all the simple constraints
		n_legal_cons = 0;

		for (PlainConstraint cons : ptAnalyzer.constraints) {
			if (cons.isViable == false)
				continue;

			my_lhs = cons.expr.getO1().getRepresentative();
			my_rhs = cons.expr.getO2().getRepresentative();
			nf1 = ptAnalyzer.getMappedMethodID(my_lhs.getWrappedNode());
			nf2 = ptAnalyzer.getMappedMethodID(my_rhs.getWrappedNode());
			if ( nf1 == GeomPointsTo.UNKNOWN_FUNCTION || 
					nf2 == GeomPointsTo.UNKNOWN_FUNCTION ) {
				cons.isViable = false;
				continue;
			}
			
			// Test how many globals are in this constraint
			code = ((nf1==GeomPointsTo.SUPER_MAIN ? 1 : 0) << 1) |
						(nf2==GeomPointsTo.SUPER_MAIN ? 1 : 0);
			
			switch (cons.type) {
			case GeomPointsTo.NEW_CONS:
				// We directly add the objects to the points-to set
				
				my_rhs.add_points_to_3(
						(AllocNode)my_lhs.getWrappedNode(), 
						(code&1) == 1 ? 0 : 1,					// to decide if the receiver is a global or not 
						(code>>1) == 1 ? 0 : 1, 				// if the object is a global or not
						(code&1) == 1 ? ptAnalyzer.context_size[nf1] : ptAnalyzer.context_size[nf2] );
				
				// Enqueue to the worklist
				ptAnalyzer.getWorklist().push(my_rhs);
				break;

			case GeomPointsTo.ASSIGN_CONS:
				// The core part of any context sensitive algorithms
				if ( cons.interCallEdges != null ) {
					// Inter-procedural assignment
					for ( Iterator<Edge> it = cons.interCallEdges.iterator(); it.hasNext(); ) {
						Edge sEdge = it.next();
						q = ptAnalyzer.getInternalEdgeFromSootEdge( sEdge );
						if (q.is_obsoleted == true) {
							continue;
						}
						
						// Build the THIS ptr filter
//						if ( ((VarNode)rhs).is_this_ptr() ) {
//							if ( sEdge.isVirtual() ) {
//								// A virtual call has special treatment, but special call does not, :>
//								final SootMethod func = ((LocalVarNode)rhs).getMethod();
//								final SootClass defClass = func.getDeclaringClass();
//								
//								lhs.getP2Set().forall( new P2SetVisitor() {
//									public void visit(Node n) {
//										if ( !IntervalPointsTo.type_manager.castNeverFails(n.getType(), rhs.getType()) )
//											return;
//											
//										if ( n.getType() instanceof RefType ) {
//											SootClass sc = ((RefType)n.getType()).getSootClass();
//											if ( defClass != sc && 
//													Scene.v().getActiveHierarchy().resolveConcreteDispatch(sc, func) != func ) {
//												return;
//											}
//										}
//										
//										rhs.getP2Set().add(n);
//									}
//								});
//							}
//							else {
//								lhs.getP2Set().forall( new P2SetVisitor() {
//									public void visit(Node n) {
//										if ( IntervalPointsTo.type_manager.castNeverFails(n.getType(), rhs.getType()) )
//											rhs.getP2Set().add(n);
//									}
//								} );
//							}
//						}

						
						// Parameter passing
						if ( nf2 == q.t ) {
							/*
							 *  The receiver must be a local, while the sender is perhaps not (e.g. for handling reflection, see class PAG)
							 *  In that case, nf1 is 0.
							 */
							
							if ( nf1 == GeomPointsTo.SUPER_MAIN ) {
								my_lhs.add_simple_constraint_3( 
										my_rhs, 
										0,
										q.map_offset, 
										ptAnalyzer.max_context_size_block[q.s]);
							}
							else {
								// nf1 == q.s
								
								// We should treat the self recursive calls specially
								if ( q.s == q.t ) {
									my_lhs.add_simple_constraint_3(
											my_rhs, 
											1,
											1, 
											ptAnalyzer.context_size[nf1]);
								}
								else {
									for ( k = 0; k < ptAnalyzer.block_num[nf1]; ++k ) {
										my_lhs.add_simple_constraint_3(
												my_rhs, 
												k * ptAnalyzer.max_context_size_block[nf1] + 1,
												q.map_offset, 
												ptAnalyzer.max_context_size_block[nf1]);
									}
								}
							}
						} else {
							// nf2 == q.s
							// Return value
							// Both are locals
							
							if ( q.s == q.t ) {
								my_lhs.add_simple_constraint_3(
										my_rhs,
										1,
										1, 
										ptAnalyzer.context_size[nf2]);
							}
							else {
								for ( k = 0; k < ptAnalyzer.block_num[nf2]; ++k ) {
									my_lhs.add_simple_constraint_3(
											my_rhs,
											q.map_offset,
											k * ptAnalyzer.max_context_size_block[nf2] + 1, 
											ptAnalyzer.max_context_size_block[nf2]);
								}
							}
						}
					}
				}
				else {			
					// Intraprocedural
					// And, assignment involves global variable goes here. By
					// definition, global variables belong to SUPER_MAIN.
					// By the Jimple IR, not both sides are global variables

					my_lhs.add_simple_constraint_3(
							my_rhs,
							nf1 == GeomPointsTo.SUPER_MAIN ? 0 : 1,
							nf2 == GeomPointsTo.SUPER_MAIN ? 0 : 1,
							nf1 == GeomPointsTo.SUPER_MAIN ? ptAnalyzer.context_size[nf2] : 
								ptAnalyzer.context_size[nf1] );
				}
				break;

			case GeomPointsTo.LOAD_CONS:
				// lhs is always a local
				// rhs = lhs.f
				cons.code = full_convertor[code];
				cons.otherSide = my_rhs;
				my_lhs.put_complex_constraint( cons );
				break;

			case GeomPointsTo.STORE_CONS:
				// rhs is always a local
				// rhs.f = lhs
				cons.code = full_convertor[code];
				cons.otherSide = my_lhs;
				my_rhs.put_complex_constraint( cons );
				break;

			default:
				System.exit(-1);
				break;
			}

			++n_legal_cons;
		}

		ptAnalyzer.ps.println("We have " + n_legal_cons + " legal constraints at the beginning," +
				" occupies " + ((double)n_legal_cons/ptAnalyzer.constraints.size()) + " of the total.");
	}
}
