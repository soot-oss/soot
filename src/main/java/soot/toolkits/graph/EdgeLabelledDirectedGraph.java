package soot.toolkits.graph;

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

import java.util.List;

/**
 * A {@link DirectedGraph} with labels on the edges.
 *
 * @param <N>
 *          type of the nodes
 * @param <L>
 *          type of the labels
 */
public interface EdgeLabelledDirectedGraph<N, L> extends DirectedGraph<N> {

  /**
   * Returns a list of labels for which an edge exists between from and to
   *
   * @param from
   *          out node of the edges to get labels for
   * @param to
   *          in node of the edges to get labels for
   *
   * @return
   */
  public List<L> getLabelsForEdges(N from, N to);

  /**
   * Returns a DirectedGraph consisting of all edges with the given label and their nodes. Nodes without edges are not
   * included in the new graph.
   *
   * @param label
   *          edge label to use as a filter in building the subgraph
   *
   * @return
   */
  public DirectedGraph<N> getEdgesForLabel(L label);

  /**
   * @param from
   * @param to
   * @param label
   *
   * @return true if the graph contains an edge between the 2 nodes with the given label, false otherwise
   */
  public boolean containsEdge(N from, N to, L label);

  /**
   * @param from
   *          out node for the edges
   * @param to
   *          in node for the edges
   *
   * @return true if the graph contains any edges between the 2 nodes, false, otherwise
   */
  public boolean containsAnyEdge(N from, N to);

  /**
   * @param label
   *          label for the edges
   *
   * @return true if the graph contains any edges with the given label, false otherwise
   */
  public boolean containsAnyEdge(L label);

  /**
   * @param node
   *          node that we want to know if the graph contains
   *
   * @return true if the graph contains the node, false otherwise
   */
  public boolean containsNode(N node);
}
