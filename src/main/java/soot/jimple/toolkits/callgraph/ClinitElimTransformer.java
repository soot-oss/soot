package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jennifer Lhotak
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
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.jimple.Stmt;
import soot.toolkits.graph.BriefUnitGraph;

public class ClinitElimTransformer extends BodyTransformer {

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    ClinitElimAnalysis a = new ClinitElimAnalysis(new BriefUnitGraph(b));

    CallGraph cg = Scene.v().getCallGraph();
    for (Iterator<Edge> edgeIt = cg.edgesOutOf(b.getMethod()); edgeIt.hasNext();) {
      Edge e = edgeIt.next();
      if (e.isClinit()) {
        Stmt srcStmt = e.srcStmt();
        if (srcStmt != null) {
          if (a.getFlowBefore(srcStmt).contains(e.tgt())) {
            cg.removeEdge(e);
          }
        }
      }
    }
  }
}
