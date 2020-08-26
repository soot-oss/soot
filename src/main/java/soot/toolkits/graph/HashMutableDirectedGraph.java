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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HashMap based implementation of a MutableBlockGraph.
 * 
 * @param <N>
 */
public class HashMutableDirectedGraph<N> implements MutableDirectedGraph<N> {
  private static final Logger logger = LoggerFactory.getLogger(HashMutableDirectedGraph.class);

  protected final Map<N, Set<N>> nodeToPreds;
  protected final Map<N, Set<N>> nodeToSuccs;

  protected final Set<N> heads;
  protected final Set<N> tails;

  private static <T> List<T> getCopy(Collection<? extends T> c) {
    return Collections.unmodifiableList(new ArrayList<T>(c));
  }

  private static <A, B> Map<A, Set<B>> deepCopy(Map<A, Set<B>> in) {
    HashMap<A, Set<B>> retVal = new HashMap<>(in);
    for (Map.Entry<A, Set<B>> e : retVal.entrySet()) {
      e.setValue(new LinkedHashSet<B>(e.getValue()));
    }
    return retVal;
  }

  public HashMutableDirectedGraph() {
    this.nodeToPreds = new HashMap<N, Set<N>>();
    this.nodeToSuccs = new HashMap<N, Set<N>>();
    this.heads = new HashSet<N>();
    this.tails = new HashSet<N>();
  }

  // copy constructor
  public HashMutableDirectedGraph(HashMutableDirectedGraph<N> orig) {
    this.nodeToPreds = deepCopy(orig.nodeToPreds);
    this.nodeToSuccs = deepCopy(orig.nodeToSuccs);
    this.heads = new HashSet<N>(orig.heads);
    this.tails = new HashSet<N>(orig.tails);
  }

  @Override
  public Object clone() {
    return new HashMutableDirectedGraph<N>(this);
  }

  /** Removes all nodes and edges. */
  public void clearAll() {
    nodeToPreds.clear();
    nodeToSuccs.clear();
    heads.clear();
    tails.clear();
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
    Set<N> preds = nodeToPreds.get(s);
    if (preds != null) {
      return getCopy(preds);
    }
    throw new RuntimeException(s + " not in graph!");
  }

  /**
   * Same as {@link #getPredsOf(Object)} but returns a set. This is faster than calling {@link #getPredsOf(Object)}. Also,
   * certain operations like {@link Collection#contains(Object)} execute faster on the set than on the list. The returned set
   * is unmodifiable.
   */
  public Set<N> getPredsOfAsSet(N s) {
    Set<N> preds = nodeToPreds.get(s);
    if (preds != null) {
      return Collections.unmodifiableSet(preds);
    }
    throw new RuntimeException(s + " not in graph!");
  }

  @Override
  public List<N> getSuccsOf(N s) {
    Set<N> succs = nodeToSuccs.get(s);
    if (succs != null) {
      return getCopy(succs);
    }
    throw new RuntimeException(s + " not in graph!");
  }

  /**
   * Same as {@link #getSuccsOf(Object)} but returns a set. This is faster than calling {@link #getSuccsOf(Object)}. Also,
   * certain operations like {@link Collection#contains(Object)} execute faster on the set than on the list. The returned set
   * is unmodifiable.
   */
  public Set<N> getSuccsOfAsSet(N s) {
    Set<N> succs = nodeToSuccs.get(s);
    if (succs != null) {
      return Collections.unmodifiableSet(succs);
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
  public void addEdge(N from, N to) {
    if (from == null || to == null) {
      throw new RuntimeException("edge with null endpoint");
    }

    if (containsEdge(from, to)) {
      return;
    }

    Set<N> succsList = nodeToSuccs.get(from);
    if (succsList == null) {
      throw new RuntimeException(from + " not in graph!");
    }

    Set<N> predsList = nodeToPreds.get(to);
    if (predsList == null) {
      throw new RuntimeException(to + " not in graph!");
    }

    heads.remove(to);
    tails.remove(from);

    succsList.add(to);
    predsList.add(from);
  }

  @Override
  public void removeEdge(N from, N to) {
    Set<N> succs = nodeToSuccs.get(from);
    if (succs == null || !succs.contains(to)) {
      // i.e. containsEdge(from, to)==false
      return;
    }

    Set<N> preds = nodeToPreds.get(to);
    if (preds == null) {
      // i.e. inconsistent data structures
      throw new RuntimeException(to + " not in graph!");
    }

    succs.remove(to);
    preds.remove(from);

    if (succs.isEmpty()) {
      tails.add(from);
    }

    if (preds.isEmpty()) {
      heads.add(to);
    }
  }

  @Override
  public boolean containsEdge(N from, N to) {
    Set<N> succs = nodeToSuccs.get(from);
    return succs != null && succs.contains(to);
  }

  @Override
  public boolean containsNode(N node) {
    return nodeToPreds.keySet().contains(node);
  }

  @Override
  public List<N> getNodes() {
    return getCopy(nodeToPreds.keySet());
  }

  @Override
  public void addNode(N node) {
    if (containsNode(node)) {
      throw new RuntimeException("Node already in graph");
    }

    nodeToSuccs.put(node, new LinkedHashSet<N>());
    nodeToPreds.put(node, new LinkedHashSet<N>());
    heads.add(node);
    tails.add(node);
  }

  @Override
  public void removeNode(N node) {
    for (N n : new ArrayList<N>(nodeToSuccs.get(node))) {
      removeEdge(node, n);
    }
    nodeToSuccs.remove(node);

    for (N n : new ArrayList<N>(nodeToPreds.get(node))) {
      removeEdge(n, node);
    }
    nodeToPreds.remove(node);

    heads.remove(node);
    tails.remove(node);
  }

  public void printGraph() {
    for (N node : this) {
      logger.debug("Node = " + node);
      logger.debug("Preds:");
      for (N p : getPredsOf(node)) {
        logger.debug("     ");
        logger.debug("" + p);
      }
      logger.debug("Succs:");
      for (N s : getSuccsOf(node)) {
        logger.debug("     ");
        logger.debug("" + s);
      }
    }
  }
}
