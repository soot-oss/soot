package soot.jimple.toolkits.thread.mhp;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Kind;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * Assembles a list of target methods for a given unit and call graph, filtering out static initializers and optionally
 * native methods. Can optionally throw a runtime exception if the list is null.
 */
public class TargetMethodsFinder {

  public List<SootMethod> find(Unit unit, CallGraph cg, boolean canBeNullList, boolean canBeNative) {
    List<SootMethod> target = new ArrayList<SootMethod>();
    Iterator<Edge> it = cg.edgesOutOf(unit);
    while (it.hasNext()) {
      Edge edge = it.next();
      SootMethod targetMethod = edge.tgt();
      if (targetMethod.isNative() && !canBeNative) {
        continue;
      }
      if (edge.kind() == Kind.CLINIT) {
        continue;
      }
      target.add(targetMethod);
    }
    if (target.size() < 1 && !canBeNullList) {
      throw new RuntimeException("No target method for: " + unit);
    }
    return target;
  }
}
