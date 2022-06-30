package soot.jimple.toolkits.annotation.callgraph;

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
import soot.G;
import soot.MethodOrMethodContext;
import soot.MethodToContexts;
import soot.Scene;
import soot.Singletons;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.Host;
import soot.tagkit.LinkTag;

public class CallGraphTagger extends BodyTransformer {

  public CallGraphTagger(Singletons.Global g) {
  }

  public static CallGraphTagger v() {
    return G.v().soot_jimple_toolkits_annotation_callgraph_CallGraphTagger();
  }

  private MethodToContexts methodToContexts;

  protected void internalTransform(Body b, String phaseName, Map options) {

    CallGraph cg = Scene.v().getCallGraph();
    if (methodToContexts == null) {
      methodToContexts = new MethodToContexts(Scene.v().getReachableMethods().listener());
    }

    Iterator stmtIt = b.getUnits().iterator();

    while (stmtIt.hasNext()) {

      Stmt s = (Stmt) stmtIt.next();

      Iterator edges = cg.edgesOutOf(s);

      while (edges.hasNext()) {
        Edge e = (Edge) edges.next();
        SootMethod m = e.tgt();
        s.addTag(new LinkTag("CallGraph: Type: " + e.kind() + " Target Method/Context: " + e.getTgt().toString(), m,
            m.getDeclaringClass().getName(), "Call Graph"));

      }
    }

    SootMethod m = b.getMethod();
    for (Iterator momcIt = methodToContexts.get(m).iterator(); momcIt.hasNext();) {
      final MethodOrMethodContext momc = (MethodOrMethodContext) momcIt.next();
      Iterator callerEdges = cg.edgesInto(momc);
      while (callerEdges.hasNext()) {
        Edge callEdge = (Edge) callerEdges.next();
        SootMethod methodCaller = callEdge.src();
        Host src = methodCaller;
        if (callEdge.srcUnit() != null) {
          src = callEdge.srcUnit();
        }
        m.addTag(new LinkTag(
            "CallGraph: Source Type: " + callEdge.kind() + " Source Method/Context: " + callEdge.getSrc().toString(), src,
            methodCaller.getDeclaringClass().getName(), "Call Graph"));
      }
    }
  }

}
