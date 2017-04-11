package soot.toolkits.graph;

import java.util.List;

/**
 * A {@link DirectedGraph} with labels on the edges.
 *
 * @param <N> type of the nodes
 * @param <L> type of the labels
 */
public interface EdgeLabelledDirectedGraph<N, L> extends DirectedGraph<N> {

    /**
     * Returns a list of labels for which an edge exists between from and to
     *
     * @param from out node of the edges to get labels for
     * @param to   in node of the edges to get labels for
     *
     * @return
     */
    public List<L> getLabelsForEdges(N from, N to);

    /**
     * Returns a DirectedGraph consisting of all edges with the given label and
     * their nodes. Nodes without edges are not included in the new graph.
     *
     * @param label edge label to use as a filter in building the subgraph
     *
     * @return
     */
    public DirectedGraph<N> getEdgesForLabel(L label);

    /**
     * @param from
     * @param to
     * @param label
     *
     * @return true if the graph contains an edge between the 2 nodes with the
     *         given label, false otherwise
     */
    public boolean containsEdge(N from, N to, L label);

    /**
     * @param from out node for the edges
     * @param to   in node for the edges
     *
     * @return true if the graph contains any edges between the 2 nodes, false,
     *         otherwise
     */
    public boolean containsAnyEdge(N from, N to);

    /**
     * @param label label for the edges
     *
     * @return true if the graph contains any edges with the given label, false
     *         otherwise
     */
    public boolean containsAnyEdge(L label);

    /**
     * @param node node that we want to know if the graph contains
     *
     * @return true if the graph contains the node, false otherwise
     */
    public boolean containsNode(N node);
}
