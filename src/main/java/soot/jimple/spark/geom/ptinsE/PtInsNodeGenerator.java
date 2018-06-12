package soot.jimple.spark.geom.ptinsE;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Richard Xiao
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.Iterator;

import soot.jimple.spark.geom.dataRep.CgEdge;
import soot.jimple.spark.geom.dataRep.PlainConstraint;
import soot.jimple.spark.geom.geomE.GeometricManager;
import soot.jimple.spark.geom.geomPA.Constants;
import soot.jimple.spark.geom.geomPA.DummyNode;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IEncodingBroker;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.heapinsE.HeapInsNode;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * Build the initial encoded pointer assignment graph with the PtIns encoding.
 *
 * @author xiao
 *
 */
public class PtInsNodeGenerator extends IEncodingBroker {
  private static final int full_convertor[] = { GeometricManager.ONE_TO_ONE, GeometricManager.MANY_TO_MANY,
      GeometricManager.MANY_TO_MANY, GeometricManager.MANY_TO_MANY };

  @Override
  public void initFlowGraph(GeomPointsTo ptAnalyzer) {
    int k;
    int n_legal_cons;
    int nf1, nf2;
    int code;
    CgEdge q;
    IVarAbstraction my_lhs, my_rhs;

    // Visit all the simple constraints
    n_legal_cons = 0;

    for (PlainConstraint cons : ptAnalyzer.constraints) {
      if (!cons.isActive) {
        continue;
      }

      my_lhs = cons.getLHS().getRepresentative();
      my_rhs = cons.getRHS().getRepresentative();
      nf1 = ptAnalyzer.getMethodIDFromPtr(my_lhs);
      nf2 = ptAnalyzer.getMethodIDFromPtr(my_rhs);

      // Test how many globals are in this constraint
      code = ((nf1 == Constants.SUPER_MAIN ? 1 : 0) << 1) | (nf2 == Constants.SUPER_MAIN ? 1 : 0);

      switch (cons.type) {
        case Constants.NEW_CONS:
          // We directly add the objects to the points-to set
          my_rhs.add_points_to_3((AllocNode) my_lhs.getWrappedNode(), nf2 == Constants.SUPER_MAIN ? 0 : 1,
              nf1 == Constants.SUPER_MAIN ? 0 : 1,
              nf2 == Constants.SUPER_MAIN ? ptAnalyzer.context_size[nf1] : ptAnalyzer.context_size[nf2]);

          // Enqueue to the worklist
          ptAnalyzer.getWorklist().push(my_rhs);
          break;

        case Constants.ASSIGN_CONS:
          // The core part of any context sensitive algorithms
          if (cons.interCallEdges != null) {
            // Inter-procedural assignment
            for (Iterator<Edge> it = cons.interCallEdges.iterator(); it.hasNext();) {
              Edge sEdge = it.next();
              q = ptAnalyzer.getInternalEdgeFromSootEdge(sEdge);
              if (q.is_obsoleted == true) {
                continue;
              }

              if (nf2 == q.t) {
                // Parameter passing
                // The receiver is a local, while the sender is perhaps not
                if (nf1 == Constants.SUPER_MAIN) {
                  my_lhs.add_simple_constraint_3(my_rhs, 0, q.map_offset, ptAnalyzer.max_context_size_block[q.s]);
                } else {
                  // nf1 == q.s

                  // We should treat the self recursive calls specially
                  if (q.s == q.t) {
                    my_lhs.add_simple_constraint_3(my_rhs, 1, 1, ptAnalyzer.context_size[nf1]);
                  } else {
                    for (k = 0; k < ptAnalyzer.block_num[nf1]; ++k) {
                      my_lhs.add_simple_constraint_3(my_rhs, k * ptAnalyzer.max_context_size_block[nf1] + 1, q.map_offset,
                          ptAnalyzer.max_context_size_block[nf1]);
                    }
                  }
                }
              } else {
                // nf2 == q.s
                // Return value
                // Both are locals

                if (q.s == q.t) {
                  my_lhs.add_simple_constraint_3(my_rhs, 1, 1, ptAnalyzer.context_size[nf2]);
                } else {
                  for (k = 0; k < ptAnalyzer.block_num[nf2]; ++k) {
                    my_lhs.add_simple_constraint_3(my_rhs, q.map_offset, k * ptAnalyzer.max_context_size_block[nf2] + 1,
                        ptAnalyzer.max_context_size_block[nf2]);
                  }
                }
              }
            }
          } else {
            // Intraprocedural
            // And, assignment involves global variable goes here. By
            // definition, global variables belong to SUPER_MAIN.
            // By the Jimple IR, not both sides are global variables

            my_lhs.add_simple_constraint_3(my_rhs, nf1 == Constants.SUPER_MAIN ? 0 : 1, nf2 == Constants.SUPER_MAIN ? 0 : 1,
                nf1 == Constants.SUPER_MAIN ? ptAnalyzer.context_size[nf2] : ptAnalyzer.context_size[nf1]);
          }
          break;

        case Constants.LOAD_CONS:
          // lhs is always a local
          // rhs = lhs.f
          cons.code = full_convertor[code];
          cons.otherSide = my_rhs;
          my_lhs.put_complex_constraint(cons);
          break;

        case Constants.STORE_CONS:
          // rhs is always a local
          // rhs.f = lhs
          cons.code = full_convertor[code];
          cons.otherSide = my_lhs;
          my_rhs.put_complex_constraint(cons);
          break;

        default:
          throw new RuntimeException("Invalid node type");
      }

      ++n_legal_cons;
    }

    ptAnalyzer.ps.printf("Only %d (%.1f%%) constraints are needed for this run.\n", n_legal_cons,
        ((double) n_legal_cons / ptAnalyzer.n_init_constraints) * 100);
  }

  @Override
  public IVarAbstraction generateNode(Node vNode) {
    IVarAbstraction ret;

    if (vNode instanceof AllocNode || vNode instanceof FieldRefNode) {
      ret = new DummyNode(vNode);
    } else {
      ret = new HeapInsNode(vNode);
    }

    return ret;
  }

  @Override
  public String getSignature() {
    return Constants.ptinsE;
  }
}
