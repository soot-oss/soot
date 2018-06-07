
package soot.jimple.toolkits.infoflow;

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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import soot.SootMethod;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * CallChain written by Richard L. Halpert 2007-03-07 Stores a list of edges, and has a "next pointer" to a continuation of
 * the list
 */

public class CallChain {
  // List edges;
  Edge edge;
  CallChain next;

  public CallChain(Edge edge, CallChain next) {
    this.edge = edge;
    if (next != null && next.edge == null && next.next == null) {
      this.next = null;
    } else {
      this.next = next;
    }
  }

  // reconstructs the whole chain
  public List<Edge> getEdges() {
    List<Edge> ret = new LinkedList<Edge>();
    if (edge != null) {
      ret.add(edge);
    }
    CallChain current = next;
    while (current != null) {
      ret.add(current.edge);
      current = current.next;
    }
    return ret;
  }

  public int size() {
    return 1 + (next == null ? 0 : next.size());
  }

  public Iterator<Edge> iterator() {
    return getEdges().iterator();
  }

  public boolean contains(Edge e) {
    return (edge == e) || (next != null && next.contains(e));
  }

  public boolean containsMethod(SootMethod sm) {
    return (edge != null && edge.tgt() == sm) || (next != null && next.containsMethod(sm));
  }

  // returns a shallow clone of this list...
  // which requires a deep clone of the CallChain objects in it
  public CallChain cloneAndExtend(CallChain extension) {
    if (next == null) {
      return new CallChain(edge, extension);
    }

    return new CallChain(edge, next.cloneAndExtend(extension));
  }

  public Object clone() {
    if (next == null) {
      return new CallChain(edge, null);
    }

    return new CallChain(edge, (CallChain) next.clone());
  }

  public boolean equals(Object o) {
    if (o instanceof CallChain) {
      CallChain other = (CallChain) o;
      if (edge == other.edge
          && ((next == null && other.next == null) || (next != null && other.next != null && next.equals(other.next)))) {
        return true;
      }
    }
    return false;
  }
}
