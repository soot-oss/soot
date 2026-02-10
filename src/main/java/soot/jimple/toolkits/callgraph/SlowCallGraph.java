package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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
import soot.util.HashMultiMap;
import soot.util.MultiMap;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;

/**
 * Represents the edges in a call graph. This class is meant to act as only a container of edges; code for various call graph
 * builders should be kept out of it, as well as most code for accessing the edges.
 *
 * @author Ondrej Lhotak
 */
public class SlowCallGraph extends CallGraph {

  private final Set<Edge> edges = new HashSet<Edge>();
  private final MultiMap<Unit, Edge> unitMap = new HashMultiMap<Unit, Edge>();
  private final MultiMap<MethodOrMethodContext, Edge> srcMap = new HashMultiMap<MethodOrMethodContext, Edge>();
  private final MultiMap<MethodOrMethodContext, Edge> tgtMap = new HashMultiMap<MethodOrMethodContext, Edge>();
  private final ChunkedQueue<Edge> stream = new ChunkedQueue<Edge>();
  private final QueueReader<Edge> reader = stream.reader();

  /**
   * Used to add an edge to the call graph. Returns true iff the edge was not already present.
   */
  @Override
  public boolean addEdge(Edge e) {
    if (edges.add(e)) {
      stream.add(e);
      srcMap.put(e.getSrc(), e);
      tgtMap.put(e.getTgt(), e);
      unitMap.put(e.srcUnit(), e);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Removes the edge e from the call graph. Returns true iff the edge was originally present in the call graph.
   */
  @Override
  public boolean removeEdge(Edge e) {
    if (edges.remove(e)) {
      srcMap.remove(e.getSrc(), e);
      tgtMap.remove(e.getTgt(), e);
      unitMap.remove(e.srcUnit(), e);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns an iterator over all methods that are the sources of at least one edge.
   */
  @Override
  public Iterator<MethodOrMethodContext> sourceMethods() {
    return new ArrayList<MethodOrMethodContext>(srcMap.keySet()).iterator();
  }

  /**
   * Returns an iterator over all edges that have u as their source unit.
   */
  @Override
  public Iterator<Edge> edgesOutOf(Unit u) {
    return new ArrayList<Edge>(unitMap.get(u)).iterator();
  }

  /**
   * Returns an iterator over all edges that have m as their source method.
   */
  @Override
  public Iterator<Edge> edgesOutOf(MethodOrMethodContext m) {
    return new ArrayList<Edge>(srcMap.get(m)).iterator();
  }

  /**
   * Returns an iterator over all edges that have m as their target method.
   */
  @Override
  public Iterator<Edge> edgesInto(MethodOrMethodContext m) {
    return new ArrayList<Edge>(tgtMap.get(m)).iterator();
  }

  /**
   * Returns a QueueReader object containing all edges added so far, and which will be informed of any new edges that are
   * later added to the graph.
   */
  @Override
  public QueueReader<Edge> listener() {
    return (QueueReader<Edge>) reader.clone();
  }

  /**
   * Returns a QueueReader object which will contain ONLY NEW edges which will be added to the graph.
   */
  @Override
  public QueueReader<Edge> newListener() {
    return stream.reader();
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    for (QueueReader<Edge> rdr = listener(); rdr.hasNext();) {
      Edge e = rdr.next();
      if (e != null) {
        out.append(e.toString()).append('\n');
      }
    }
    return out.toString();
  }

  /**
   * Returns the number of edges in the call graph.
   */
  @Override
  public int size() {
    return edges.size();
  }
}
