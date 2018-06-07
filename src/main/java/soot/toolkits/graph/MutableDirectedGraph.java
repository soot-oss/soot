package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai, Patrick Lam
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

import java.util.List;

/**
 * Defines a DirectedGraph which is modifiable. Provides an interface to add/delete nodes and edges.
 */

public interface MutableDirectedGraph<N> extends DirectedGraph<N> {
  /**
   * Adds an edge to the graph between 2 nodes. If the edge is already present no change is made.
   *
   * @param from
   *          out node for the edge.
   * @param to
   *          in node for the edge.
   */
  public void addEdge(N from, N to);

  /**
   * Removes an edge between 2 nodes in the graph. If the edge is not present no change is made.
   *
   * @param from
   *          out node for the edge to remove.
   * @param to
   *          in node for the edge to remove.
   */
  public void removeEdge(N from, N to);

  /**
   * @return true if the graph contains an edge the 2 nodes false otherwise.
   */
  public boolean containsEdge(N from, N to);

  /** @return a list of the nodes that compose the graph. No ordering is implied. */
  public List<N> getNodes();

  /**
   * Adds a node to the graph. Initially the added node has no successors or predecessors. ; as a consequence it is
   * considered both a head and tail for the graph.
   *
   * @param node
   *          a node to add to the graph.
   * @see #getHeads
   * @see #getTails
   */
  public void addNode(N node);

  /**
   * Removes a node from the graph. If the node is not found in the graph, no change is made.
   *
   * @param node
   *          the node to be removed.
   */
  public void removeNode(N node);

  /**
   * @param node
   *          node that we want to know if the graph constains.
   * @return true if the graph contains the node. false otherwise.
   */
  public boolean containsNode(N node);
}
