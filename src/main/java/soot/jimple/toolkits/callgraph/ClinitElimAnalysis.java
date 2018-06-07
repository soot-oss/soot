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

import soot.Scene;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class ClinitElimAnalysis extends ForwardFlowAnalysis {

  private UnitGraph g;

  public ClinitElimAnalysis(UnitGraph g) {
    super(g);
    this.g = g;

    doAnalysis();
  }

  public void merge(Object in1, Object in2, Object out) {

    FlowSet inSet1 = (FlowSet) in1;
    FlowSet inSet2 = (FlowSet) in2;
    FlowSet outSet = (FlowSet) out;

    inSet1.intersection(inSet2, outSet);
  }

  public void copy(Object src, Object dest) {

    FlowSet srcIn = (FlowSet) src;
    FlowSet destOut = (FlowSet) dest;

    srcIn.copy(destOut);
  }

  // out(s) = in(s) intersect { target methods of s where edge kind is clinit}
  protected void flowThrough(Object inVal, Object stmt, Object outVal) {
    FlowSet in = (FlowSet) inVal;
    FlowSet out = (FlowSet) outVal;
    Stmt s = (Stmt) stmt;

    in.copy(out);

    CallGraph cg = Scene.v().getCallGraph();

    Iterator edges = cg.edgesOutOf(s);

    while (edges.hasNext()) {
      Edge e = (Edge) edges.next();
      if (e.isClinit()) {
        out.add(e.tgt());
      }
    }
  }

  protected Object entryInitialFlow() {

    return new ArraySparseSet();

  }

  protected Object newInitialFlow() {
    ArraySparseSet set = new ArraySparseSet();
    CallGraph cg = Scene.v().getCallGraph();

    Iterator mIt = cg.edgesOutOf(g.getBody().getMethod());
    while (mIt.hasNext()) {
      Edge edge = (Edge) mIt.next();
      if (edge.isClinit()) {
        set.add(edge.tgt());
      }
    }

    return set;
  }
}
