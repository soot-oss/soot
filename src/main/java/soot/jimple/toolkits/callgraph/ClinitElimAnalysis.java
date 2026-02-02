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
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.toolkits.scalar.HashSparseSet;

public class ClinitElimAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<SootMethod>> {

  private final CallGraph cg = Scene.v().getCallGraph();
  private final UnitGraph g;

  private FlowSet<SootMethod> cachedFlowSet = null;

  public ClinitElimAnalysis(UnitGraph g) {
    super(g);
    this.g = g;

    doAnalysis();
  }

  @Override
  public void merge(FlowSet<SootMethod> in1, FlowSet<SootMethod> in2, FlowSet<SootMethod> out) {
    in1.intersection(in2, out);
  }

  @Override
  public void copy(FlowSet<SootMethod> src, FlowSet<SootMethod> dest) {
    src.copy(dest);
  }

  @Override
  protected void copyFreshToExisting(FlowSet<SootMethod> in, FlowSet<SootMethod> dest) {
    in.copyFreshToExisting(dest);
  }

  // out(s) = in(s) intersect { target methods of s where edge kind is clinit}
  @Override
  protected void flowThrough(FlowSet<SootMethod> inVal, Unit stmt, FlowSet<SootMethod> outVal) {
    inVal.copy(outVal);

    for (Iterator<Edge> edges = cg.edgesOutOf(stmt); edges.hasNext();) {
      Edge e = edges.next();
      if (e.isClinit()) {
        outVal.add(e.tgt());
      }
    }
  }

  @Override
  protected FlowSet<SootMethod> entryInitialFlow() {
    return new HashSparseSet<SootMethod>();
  }

  @Override
  protected FlowSet<SootMethod> newInitialFlow() {
    HashSparseSet<SootMethod> returnedFlowSet = new HashSparseSet<>();
    if (cachedFlowSet == null) {
      cachedFlowSet = calculateInitialFlow();
    }
    cachedFlowSet.copy(returnedFlowSet);
    return returnedFlowSet;
  }

  protected FlowSet<SootMethod> calculateInitialFlow() {
    HashSparseSet<SootMethod> newFlowSet = new HashSparseSet<>();
    for (Iterator<Edge> mIt = cg.edgesOutOf(g.getBody().getMethod()); mIt.hasNext();) {
      Edge edge = mIt.next();
      if (edge.isClinit()) {
        newFlowSet.add(edge.tgt());
      }
    }
    return newFlowSet;
  }

}
