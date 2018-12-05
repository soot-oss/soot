package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 - 2004 Ondrej Lhotak
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import soot.MethodOrMethodContext;
import soot.Unit;

/**
 * Extends a TargetsOfMethod or TargetsOfUnit to include edges transitively reachable from any target methods.
 * 
 * @author Ondrej Lhotak
 */
public class TransitiveTargets {
  private CallGraph cg;
  private Filter filter;

  public TransitiveTargets(CallGraph cg) {
    this.cg = cg;
  }

  public TransitiveTargets(CallGraph cg, Filter filter) {
    this.cg = cg;
    this.filter = filter;
  }

  public Iterator<MethodOrMethodContext> iterator(Unit u) {
    ArrayList<MethodOrMethodContext> methods = new ArrayList<MethodOrMethodContext>();
    Iterator<Edge> it = cg.edgesOutOf(u);
    if (filter != null) {
      it = filter.wrap(it);
    }
    while (it.hasNext()) {
      Edge e = (Edge) it.next();
      methods.add(e.getTgt());
    }
    return iterator(methods.iterator());
  }

  public Iterator<MethodOrMethodContext> iterator(MethodOrMethodContext momc) {
    ArrayList<MethodOrMethodContext> methods = new ArrayList<MethodOrMethodContext>();
    Iterator<Edge> it = cg.edgesOutOf(momc);
    if (filter != null) {
      it = filter.wrap(it);
    }
    while (it.hasNext()) {
      Edge e = (Edge) it.next();
      methods.add(e.getTgt());
    }
    return iterator(methods.iterator());
  }

  public Iterator<MethodOrMethodContext> iterator(Iterator<? extends MethodOrMethodContext> methods) {
    Set<MethodOrMethodContext> s = new HashSet<MethodOrMethodContext>();
    ArrayList<MethodOrMethodContext> worklist = new ArrayList<MethodOrMethodContext>();
    while (methods.hasNext()) {
      MethodOrMethodContext method = methods.next();
      if (s.add(method)) {
        worklist.add(method);
      }
    }
    return iterator(s, worklist);
  }

  private Iterator<MethodOrMethodContext> iterator(Set<MethodOrMethodContext> s, ArrayList<MethodOrMethodContext> worklist) {
    for (int i = 0; i < worklist.size(); i++) {
      MethodOrMethodContext method = worklist.get(i);
      Iterator<Edge> it = cg.edgesOutOf(method);
      if (filter != null) {
        it = filter.wrap(it);
      }
      while (it.hasNext()) {
        Edge e = (Edge) it.next();
        if (s.add(e.getTgt())) {
          worklist.add(e.getTgt());
        }
      }
    }
    return worklist.iterator();
  }
}
