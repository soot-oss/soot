package soot.jimple.toolkits.thread.mhp.findobject;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import soot.PointsToAnalysis;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.NewExpr;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.PAG;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class AllocNodesFinder {

  private final Set<AllocNode> allocNodes;
  private final Set<AllocNode> multiRunAllocNodes;
  private final Set<SootMethod> multiCalledMethods;
  PAG pag;

  public AllocNodesFinder(PegCallGraph pcg, CallGraph cg, PAG pag) {
    // System.out.println("===inside AllocNodesFinder===");
    this.pag = pag;
    allocNodes = new HashSet<AllocNode>();
    multiRunAllocNodes = new HashSet<AllocNode>();
    multiCalledMethods = new HashSet<SootMethod>();
    MultiCalledMethods mcm = new MultiCalledMethods(pcg, multiCalledMethods);

    find(mcm.getMultiCalledMethods(), pcg, cg);
  }

  private void find(Set<SootMethod> multiCalledMethods, PegCallGraph pcg, CallGraph callGraph) {
    Set clinitMethods = pcg.getClinitMethods();
    Iterator it = pcg.iterator();
    while (it.hasNext()) {
      SootMethod sm = (SootMethod) it.next();
      UnitGraph graph = new CompleteUnitGraph(sm.getActiveBody());
      Iterator iterator = graph.iterator();
      if (multiCalledMethods.contains(sm)) {
        while (iterator.hasNext()) {
          Unit unit = (Unit) iterator.next();
          // System.out.println("unit: "+unit);
          if (clinitMethods.contains(sm) && unit instanceof AssignStmt) {
            // Value rightOp = ((AssignStmt)unit).getRightOp();

            // Type type = ((NewExpr)rightOp).getType();
            AllocNode allocNode = pag.makeAllocNode(PointsToAnalysis.STRING_NODE, RefType.v("java.lang.String"), null);
            // AllocNode allocNode = pag.makeAllocNode((NewExpr)rightOp, type, sm);
            // System.out.println("make alloc node: "+allocNode);
            allocNodes.add(allocNode);
            multiRunAllocNodes.add(allocNode);

          }

          else if (unit instanceof DefinitionStmt) {
            Value rightOp = ((DefinitionStmt) unit).getRightOp();
            if (rightOp instanceof NewExpr) {
              Type type = ((NewExpr) rightOp).getType();
              AllocNode allocNode = pag.makeAllocNode(rightOp, type, sm);
              // System.out.println("make alloc node: "+allocNode);
              allocNodes.add(allocNode);
              multiRunAllocNodes.add(allocNode);
            }
          }
        }
      }

      else {
        // MultiRunStatementsFinder finder = new MultiRunStatementsFinder(graph, sm);
        MultiRunStatementsFinder finder = new MultiRunStatementsFinder(graph, sm, multiCalledMethods, callGraph);
        FlowSet fs = finder.getMultiRunStatements();
        // methodsToMultiObjsSites.put(sm, fs);
        // PatchingChain pc = sm.getActiveBody().getUnits();

        while (iterator.hasNext()) {
          Unit unit = (Unit) iterator.next();
          // System.out.println("unit: "+unit);

          if (clinitMethods.contains(sm) && unit instanceof AssignStmt) {
            AllocNode allocNode = pag.makeAllocNode(PointsToAnalysis.STRING_NODE, RefType.v("java.lang.String"), null);
            // AllocNode allocNode = pag.makeAllocNode((NewExpr)rightOp, type, sm);
            // System.out.println("make alloc node: "+allocNode);
            allocNodes.add(allocNode);
            /*
             * if (fs.contains(unit)){ multiRunAllocNodes.add(unit); }
             */
          } else if (unit instanceof DefinitionStmt) {

            Value rightOp = ((DefinitionStmt) unit).getRightOp();
            if (rightOp instanceof NewExpr) {
              Type type = ((NewExpr) rightOp).getType();
              AllocNode allocNode = pag.makeAllocNode(rightOp, type, sm);
              // System.out.println("make alloc node: "+allocNode);
              allocNodes.add(allocNode);
              if (fs.contains(unit)) {
                // System.out.println("fs contains: "+unit);
                multiRunAllocNodes.add(allocNode);
              }
            }
          }
        }
      }
    }
  }

  public Set<AllocNode> getAllocNodes() {
    return allocNodes;
  }

  public Set<AllocNode> getMultiRunAllocNodes() {
    return multiRunAllocNodes;
  }

  public Set<SootMethod> getMultiCalledMethods() {
    return multiCalledMethods;
  }
}
