package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai, Patrick Lam
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HashMap based implementation of a MutableEdgeLabelledDirectedGraph.
 *
 * @param <N>
 * @param <L>
 */
public class HashMutableEdgeLabelledDirectedGraph<N, L> implements MutableEdgeLabelledDirectedGraph<N, L> {
  private static final Logger logger = LoggerFactory.getLogger(HashMutableEdgeLabelledDirectedGraph.class);

  protected static class DGEdge<N> {

    final N from;
    final N to;

    public DGEdge(N from, N to) {
      this.from = from;
      this.to = to;
    }

    public N from() {
      return from;
    }

    public N to() {
      return to;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof DGEdge) {
        DGEdge<?> other = (DGEdge<?>) o;
        return this.from.equals(other.from) && this.to.equals(other.to);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(new Object[] { from, to });
    }
  }

  private static <T> List<T> getCopy(Collection<? extends T> c) {
    return Collections.unmodifiableList(new ArrayList<T>(c));
  }

  protected final Map<N, List<N>> nodeToPreds;
  protected final Map<N, List<N>> nodeToSuccs;

  protected final Map<DGEdge<N>, List<L>> edgeToLabels;
  protected final Map<L, List<DGEdge<N>>> labelToEdges;

  protected final Set<N> heads;
  protected final Set<N> tails;

  public HashMutableEdgeLabelledDirectedGraph() {
    this.nodeToPreds = new HashMap<N, List<N>>();
    this.nodeToSuccs = new HashMap<N, List<N>>();
    this.edgeToLabels = new HashMap<DGEdge<N>, List<L>>();
    this.labelToEdges = new HashMap<L, List<DGEdge<N>>>();
    this.heads = new HashSet<N>();
    this.tails = new HashSet<N>();
  }

  // copy constructor
  public HashMutableEdgeLabelledDirectedGraph(HashMutableEdgeLabelledDirectedGraph<N, L> orig) {
    this.nodeToPreds = deepCopy(orig.nodeToPreds);
    this.nodeToSuccs = deepCopy(orig.nodeToSuccs);
    this.edgeToLabels = deepCopy(orig.edgeToLabels);
    this.labelToEdges = deepCopy(orig.labelToEdges);
    this.heads = new HashSet<N>(orig.heads);
    this.tails = new HashSet<N>(orig.tails);
  }

  private static <A, B> Map<A, List<B>> deepCopy(Map<A, List<B>> in) {
    HashMap<A, List<B>> retVal = new HashMap<>(in);
    for (Map.Entry<A, List<B>> e : retVal.entrySet()) {
      e.setValue(new ArrayList<B>(e.getValue()));
    }
    return retVal;
  }

  @Override
  public HashMutableEdgeLabelledDirectedGraph<N, L> clone() {
    return new HashMutableEdgeLabelledDirectedGraph<>(this);
  }

  /**
   * Removes all nodes and edges.
   */
  public void clearAll() {
    this.nodeToPreds.clear();
    this.nodeToSuccs.clear();
    this.edgeToLabels.clear();
    this.labelToEdges.clear();
    this.heads.clear();
    this.tails.clear();
  }

  /* Returns an unbacked list of heads for this graph. */
  @Override
  public List<N> getHeads() {
    return getCopy(heads);
  }

  /* Returns an unbacked list of tails for this graph. */
  @Override
  public List<N> getTails() {
    return getCopy(tails);
  }

  @Override
  public List<N> getPredsOf(N s) {
    List<N> preds = nodeToPreds.get(s);
    if (preds != null) {
      return Collections.unmodifiableList(preds);
    }
    throw new RuntimeException(s + " not in graph!");
  }

  @Override
  public List<N> getSuccsOf(N s) {
    List<N> succs = nodeToSuccs.get(s);
    if (succs != null) {
      return Collections.unmodifiableList(succs);
    }
    throw new RuntimeException(s + " not in graph!");
  }

  @Override
  public int size() {
    return nodeToPreds.keySet().size();
  }

  @Override
  public Iterator<N> iterator() {
    return nodeToPreds.keySet().iterator();
  }

  @Override
  public void addEdge(N from, N to, L label) {
    if (from == null || to == null) {
      throw new RuntimeException("edge with null endpoint");
    }

    if (label == null) {
      throw new RuntimeException("edge with null label");
    }

    if (containsEdge(from, to, label)) {
      return;
    }

    List<N> succsList = nodeToSuccs.get(from);
    if (succsList == null) {
      throw new RuntimeException(from + " not in graph!");
    }

    List<N> predsList = nodeToPreds.get(to);
    if (predsList == null) {
      throw new RuntimeException(to + " not in graph!");
    }

    heads.remove(to);
    tails.remove(from);

    if (!succsList.contains(to)) {
      succsList.add(to);
    }
    if (!predsList.contains(from)) {
      predsList.add(from);
    }

    DGEdge<N> edge = new DGEdge<N>(from, to);
    List<L> labels = edgeToLabels.get(edge);
    if (labels == null) {
      edgeToLabels.put(edge, labels = new ArrayList<L>());
    }

    List<DGEdge<N>> edges = labelToEdges.get(label);
    if (edges == null) {
      labelToEdges.put(label, edges = new ArrayList<DGEdge<N>>());
    }

    // if(!labels.contains(label))
    labels.add(label);
    // if(!edges.contains(edge))
    edges.add(edge);
  }

  @Override
  public List<L> getLabelsForEdges(N from, N to) {
    DGEdge<N> edge = new DGEdge<N>(from, to);
    return edgeToLabels.get(edge);
  }

  @Override
  public MutableDirectedGraph<N> getEdgesForLabel(L label) {
    List<DGEdge<N>> edges = labelToEdges.get(label);
    MutableDirectedGraph<N> ret = new HashMutableDirectedGraph<N>();
    if (edges == null) {
      return ret;
    }
    for (DGEdge<N> edge : edges) {
      N from = edge.from();
      if (!ret.containsNode(from)) {
        ret.addNode(from);
      }
      N to = edge.to();
      if (!ret.containsNode(to)) {
        ret.addNode(to);
      }
      ret.addEdge(from, to);
    }
    return ret;
  }

  @Override
  public void removeEdge(N from, N to, L label) {
    DGEdge<N> edge = new DGEdge<N>(from, to);
    List<L> labels = edgeToLabels.get(edge);
    if (labels == null || !labels.contains(label)) {
      // i.e. containsEdge(from, to, label)==false
      return;
    }

    List<DGEdge<N>> edges = labelToEdges.get(label);
    if (edges == null) {
      // i.e. inconsistent data structures
      throw new RuntimeException("label " + label + " not in graph!");
    }

    labels.remove(label);
    edges.remove(edge);

    // if this edge has no more labels, then it's gone!
    if (labels.isEmpty()) {
      edgeToLabels.remove(edge);

      List<N> succsList = nodeToSuccs.get(from);
      if (succsList == null) {
        // i.e. inconsistent data structures
        throw new RuntimeException(from + " not in graph!");
      }

      List<N> predsList = nodeToPreds.get(to);
      if (predsList == null) {
        // i.e. inconsistent data structures
        throw new RuntimeException(to + " not in graph!");
      }

      succsList.remove(to);
      predsList.remove(from);

      if (succsList.isEmpty()) {
        tails.add(from);
      }

      if (predsList.isEmpty()) {
        heads.add(to);
      }
    }

    // if this label has no more edges, then who cares?
    if (edges.isEmpty()) {
      labelToEdges.remove(label);
    }
  }

  @Override
  public void removeAllEdges(N from, N to) {
    DGEdge<N> edge = new DGEdge<N>(from, to);
    List<L> labels = edgeToLabels.get(edge);
    if (labels == null || labels.isEmpty()) {
      // i.e. containsAnyEdge(from, to)==false
      return;
    }

    for (L label : getCopy(labels)) {
      removeEdge(from, to, label);
    }
  }

  @Override
  public void removeAllEdges(L label) {
    List<DGEdge<N>> edges = labelToEdges.get(label);
    if (edges == null || edges.isEmpty()) {
      // i.e. containsAnyEdge(label)==false
      return;
    }

    for (DGEdge<N> edge : getCopy(edges)) {
      removeEdge(edge.from(), edge.to(), label);
    }
  }

  @Override
  public boolean containsEdge(N from, N to, L label) {
    List<L> labels = edgeToLabels.get(new DGEdge<>(from, to));
    return labels != null && labels.contains(label);
  }

  @Override
  public boolean containsAnyEdge(N from, N to) {
    List<L> labels = edgeToLabels.get(new DGEdge<>(from, to));
    return labels != null && !labels.isEmpty();
  }

  @Override
  public boolean containsAnyEdge(L label) {
    List<DGEdge<N>> edges = labelToEdges.get(label);
    return edges != null && !edges.isEmpty();
  }

  @Override
  public boolean containsNode(N node) {
    return nodeToPreds.keySet().contains(node);
  }

  @Override
  public void addNode(N node) {
    if (containsNode(node)) {
      throw new RuntimeException("Node already in graph");
    }

    nodeToSuccs.put(node, new ArrayList<N>());
    nodeToPreds.put(node, new ArrayList<N>());
    heads.add(node);
    tails.add(node);
  }

  @Override
  public void removeNode(N node) {
    for (N n : new ArrayList<N>(nodeToSuccs.get(node))) {
      removeAllEdges(node, n);
    }
    nodeToSuccs.remove(node);

    for (N n : new ArrayList<N>(nodeToPreds.get(node))) {
      removeAllEdges(n, node);
    }
    nodeToPreds.remove(node);

    heads.remove(node);
    tails.remove(node);
  }

  public void printGraph() {
    for (N node : this) {
      logger.debug("Node = " + node);

      logger.debug("Preds:");
      for (N pred : getPredsOf(node)) {
        DGEdge<N> edge = new DGEdge<N>(pred, node);
        List<L> labels = edgeToLabels.get(edge);
        logger.debug("     " + pred + " [" + labels + "]");
      }

      logger.debug("Succs:");
      for (N succ : getSuccsOf(node)) {
        DGEdge<N> edge = new DGEdge<N>(node, succ);
        List<L> labels = edgeToLabels.get(edge);
        logger.debug("     " + succ + " [" + labels + "]");
      }
    }
  }
}
