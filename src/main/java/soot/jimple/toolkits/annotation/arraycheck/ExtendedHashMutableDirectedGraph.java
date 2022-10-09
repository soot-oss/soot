package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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

import soot.toolkits.graph.HashMutableDirectedGraph;

/**
 * add skipNode method to direct all predecessor edges to successors.
 * 
 * override 'addEdge' to add node if the node was not in the graph
 * 
 * @param <N>
 */
public class ExtendedHashMutableDirectedGraph<N> extends HashMutableDirectedGraph<N> {

  public ExtendedHashMutableDirectedGraph() {
  }

  /**
   * If nodes are not in the graph, add them into graph first.
   */
  @Override
  public void addEdge(N from, N to) {
    if (!super.containsNode(from)) {
      super.addNode(from);
    }

    if (!super.containsNode(to)) {
      super.addNode(to);
    }

    super.addEdge(from, to);
  }

  /**
   * Add mutual edge to the graph. It should be optimized in the future.
   */
  public void addMutualEdge(N from, N to) {
    if (!super.containsNode(from)) {
      super.addNode(from);
    }

    if (!super.containsNode(to)) {
      super.addNode(to);
    }

    super.addEdge(from, to);
    super.addEdge(to, from);
  }

  /**
   * Bypass the in edge to out edge. Not delete the node
   */
  public void skipNode(N node) {
    if (!super.containsNode(node)) {
      return;
    }

    ArrayList<N> origPreds = new ArrayList<N>(getPredsOf(node));
    ArrayList<N> origSuccs = new ArrayList<N>(getSuccsOf(node));

    for (N p : origPreds) {
      for (N s : origSuccs) {
        if (p != s) {
          super.addEdge(p, s);
        }
      }
    }

    for (N element : origPreds) {
      super.removeEdge(element, node);
    }

    for (N element : origSuccs) {
      super.removeEdge(node, element);
    }

    super.removeNode(node);
  }

  public <T extends N> void mergeWith(ExtendedHashMutableDirectedGraph<T> other) {
    for (T node : other.getNodes()) {
      for (T succ : other.getSuccsOf(node)) {
        this.addEdge(node, succ);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Graph:\n");
    for (N node : super.getNodes()) {
      for (N succ : super.getSuccsOf(node)) {
        sb.append(node).append("\t --- \t").append(succ).append('\n');
      }
    }
    return sb.toString();
  }
}
