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
package soot.jimple.spark.geom.geomE;

import java.util.Iterator;

import soot.jimple.spark.geom.dataRep.CgEdge;
import soot.jimple.spark.geom.dataRep.PlainConstraint;
import soot.jimple.spark.geom.geomPA.Constants;
import soot.jimple.spark.geom.geomPA.DummyNode;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IEncodingBroker;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * Build the initial encoding of the assignment graph in full geometric encoding.
 * 
 * @author xiao
 *
 */
public class FullSensitiveNodeGenerator extends IEncodingBroker 
{
	private static final int full_convertor[] = { 
		GeometricManager.ONE_TO_ONE, GeometricManager.MANY_TO_MANY, 
		GeometricManager.MANY_TO_MANY, GeometricManager.MANY_TO_MANY 
	};
	
	@Override
	public void initFlowGraph( GeomPointsTo ptAnalyzer ) 
	{
		int k;
		int n_legal_cons;
		int nf1, nf2;
		int code;
		IVarAbstraction my_lhs, my_rhs;
		
		// Visit all the simple constraints
		n_legal_cons = 0;

		for ( PlainConstraint cons : ptAnalyzer.constraints ) {
			if ( !cons.isActive ) continue;

			my_lhs = cons.getLHS().getRepresentative();
			my_rhs = cons.getRHS().getRepresentative();
			nf1 = ptAnalyzer.getMethodIDFromPtr(my_lhs);
			nf2 = ptAnalyzer.getMethodIDFromPtr(my_rhs);
			
			// Test how many globals are in this constraint
			code = ((nf1==Constants.SUPER_MAIN ? 1 : 0) << 1) |
						(nf2==Constants.SUPER_MAIN ? 1 : 0);
			
			switch (cons.type) {
			case Constants.NEW_CONS:
				// We directly add the objects to the points-to set
				
				if ( code == 0 ) {
					// the allocation result is assigned to a local variable
					my_rhs.add_points_to_3( 
							(AllocNode)my_lhs.getWrappedNode(),
							1,
							1, ptAnalyzer.context_size[nf1] );
				}
				else {
					// Assigned to a global or the object itself is a global
					my_rhs.add_points_to_4((AllocNode)my_lhs.getWrappedNode(), 
							1, 1,
							ptAnalyzer.context_size[nf2], 
							ptAnalyzer.context_size[nf1] );
				}
				
				// Enqueue to the worklist
				ptAnalyzer.getWorklist().push(my_rhs);
				break;

			case Constants.ASSIGN_CONS:
				// Assigning between two pointers
				if ( cons.interCallEdges != null ) {
					// Inter-procedural assignment (parameter passing, function return)
					for ( Iterator<Edge> it = cons.interCallEdges.iterator(); it.hasNext(); ) {
						Edge sEdge = it.next();
						CgEdge q = ptAnalyzer.getInternalEdgeFromSootEdge( sEdge );
						if (q.is_obsoleted == true) {
							continue;
						}
						
						// Parameter passing or not
						if ( nf2 == q.t ) {
							/*
							 *  The receiver must be a local, while the sender is perhaps not (e.g. for handling reflection, see class PAG)
							 */
							
							// Handle the special case first
							// In that case, nf1 is SUPER_MAIN.
							if ( nf1 == Constants.SUPER_MAIN ) {
								my_lhs.add_simple_constraint_4( 
										my_rhs,
										1,
										q.map_offset,
										1,
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
								// Self-recursive calls may fall here, we handle them properly
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
					// Intra-procedural assignment
					// And, the assignments involving the global variables go here. By our definition, the global variables belong to SUPER_MAIN.
					// And according to the Jimple IR, not both sides are global variables
					
					if ( code == 0 ) {
						// local to local assignment
						my_lhs.add_simple_constraint_3(
								my_rhs, 
								1, 1, ptAnalyzer.context_size[nf1] );
					}
					else {
						my_lhs.add_simple_constraint_4(
								my_rhs, 1, 1,
								ptAnalyzer.context_size[nf1],
								ptAnalyzer.context_size[nf2] );
					}
				}
				break;

			case Constants.LOAD_CONS:
				// lhs is always a local
				// rhs = lhs.f
				cons.code = full_convertor[code];
				cons.otherSide = my_rhs;
				my_lhs.put_complex_constraint( cons );
				break;

			case Constants.STORE_CONS:
				// rhs is always a local
				// rhs.f = lhs
				cons.code = full_convertor[code];
				cons.otherSide = my_lhs;
				my_rhs.put_complex_constraint( cons );
				break;

			default:
				throw new RuntimeException("Invalid type");
			}

			++n_legal_cons;
		}

		ptAnalyzer.ps.printf("Only %d (%.1f%%) constraints are needed for this run.\n",
				n_legal_cons, ((double)n_legal_cons/ptAnalyzer.n_init_constraints) * 100 );
	}

	@Override
	public String getSignature() {
		return Constants.geomE;
	}

	@Override
	public IVarAbstraction generateNode(Node vNode) 
	{
		IVarAbstraction ret;
		
		if ( vNode instanceof AllocNode ||
				vNode instanceof FieldRefNode ) {
			ret = new DummyNode(vNode);
		}
		else {
			ret = new FullSensitiveNode( vNode );
		}
		
		return ret;
	}
}
