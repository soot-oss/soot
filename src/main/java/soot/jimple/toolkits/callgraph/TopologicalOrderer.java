package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.util.NumberedSet;

public class TopologicalOrderer {
  CallGraph cg;
  List<SootMethod> order = new ArrayList<SootMethod>();
  NumberedSet<SootMethod> visited = new NumberedSet<SootMethod>(Scene.v().getMethodNumberer());

  public TopologicalOrderer(CallGraph cg) {
    this.cg = cg;
  }

  public void go() {
    Iterator<MethodOrMethodContext> methods = cg.sourceMethods();
    while (methods.hasNext()) {
      SootMethod m = (SootMethod) methods.next();
      dfsVisit(m);
    }
  }

  private void dfsVisit(SootMethod m) {
    if (visited.contains(m)) {
      return;
    }
    visited.add(m);
    Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(m));
    while (targets.hasNext()) {
      SootMethod target = (SootMethod) targets.next();
      dfsVisit(target);
    }
    order.add(m);
  }

  public List<SootMethod> order() {
    return order;
  }
}
