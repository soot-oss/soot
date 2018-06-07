package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Richard L. Halpert
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

/**
 * Defines a DirectedGraph which is modifiable and associates a label object with every edge. Provides an interface to
 * add/delete nodes and edges.
 *
 * @param <N>
 * @param <L>
 */
public interface MutableEdgeLabelledDirectedGraph<N, L> extends EdgeLabelledDirectedGraph<N, L> {

  /**
   * Adds an edge to the graph between 2 nodes. If the edge is already present no change is made.
   *
   * @param from
   *          out node for the edge.
   * @param to
   *          in node for the edge.
   * @param label
   *          label for the edge.
   */
  public void addEdge(N from, N to, L label);

  /**
   * Removes an edge between 2 nodes in the graph. If the edge is not present no change is made.
   *
   * @param from
   *          out node for the edges to remove.
   * @param to
   *          in node for the edges to remove.
   * @param label
   *          label for the edge to remove.
   */
  public void removeEdge(N from, N to, L label);

  /**
   * Removes all edges between 2 nodes in the graph. If no edges are present, no change is made.
   *
   * @param from
   *          out node for the edges to remove.
   * @param to
   *          in node for the edges to remove.
   */
  public void removeAllEdges(N from, N to);

  /**
   * Removes all edges with the given label in the graph. If no edges are present, no change is made.
   *
   * @param label
   *          label for the edge to remove.
   */
  public void removeAllEdges(L label);

  /**
   * Adds a node to the graph. Initially the added node has no successors or predecessors. ; as a consequence it is
   * considered both a head and tail for the graph.
   *
   * @param node
   *          a node to add to the graph.
   *
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
}
