package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai, Patrick Lam
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

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * Orders in pseudo-topological order, the nodes of a DirectedGraph instance.
 *
 * @author Steven Lambeth
 * @author Marc Berndl
 */
public class PseudoTopologicalOrderer<N> implements Orderer<N> {
  public static final boolean REVERSE = true;

  private Set<N> visited;

  private int[] indexStack;

  private N[] stmtStack;
  private N[] order;
  private int orderLength;

  private boolean mIsReversed = false;

  private DirectedGraph<N> graph;

  public PseudoTopologicalOrderer() {
  }

  /**
   * Reverses the order of the elements in the specified array.
   * 
   * @param array
   */
  private static <T> void reverseArray(T[] array) {
    final int max = array.length >> 1;
    for (int i = 0, j = array.length - 1; i < max; i++, j--) {
      T temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<N> newList(DirectedGraph<N> g, boolean reverse) {
    this.mIsReversed = reverse;
    return computeOrder(g, !mIsReversed);
  }

  /**
   * Orders in pseudo-topological order.
   * 
   * @param g
   *          a DirectedGraph instance we want to order the nodes for.
   * @return an ordered list of the graph's nodes.
   */

  @SuppressWarnings("unchecked")
  protected final List<N> computeOrder(DirectedGraph<N> g, boolean reverse) {
    final int n = g.size();
    visited = Collections.newSetFromMap(new IdentityHashMap<N, Boolean>(n * 2 + 1));// new HashMap((3 * g.size()) / 2, 0.7f);
    indexStack = new int[n];
    stmtStack = (N[]) new Object[n];
    order = (N[]) new Object[n];
    graph = g;
    orderLength = 0;

    // Visit each node
    for (N s : g) {
      if (visited.add(s)) {
        visitNode(s);
      }

      if (orderLength == n) {
        break;
      }
    }

    if (reverse) {
      reverseArray(order);
    }

    List<N> o = Arrays.asList(order);

    indexStack = null;
    stmtStack = null;
    visited = null;
    order = null;

    return o;
  }

  // Unfortunately, the nice recursive solution fails
  // because of stack overflows

  // Fill in the 'order' list with a pseudo topological order
  // list of statements starting at s. Simulates recursion with a stack.

  protected final void visitNode(N startStmt) {
    int last = 0;

    stmtStack[last] = startStmt;
    indexStack[last++] = -1;
    while (last > 0) {
      int toVisitIndex = ++indexStack[last - 1];
      N toVisitNode = stmtStack[last - 1];

      List<N> succs = graph.getSuccsOf(toVisitNode);
      if (toVisitIndex >= succs.size()) {
        // Visit this node now that we ran out of children
        order[orderLength++] = toVisitNode;

        last--;
      } else {
        N childNode = succs.get(toVisitIndex);

        if (visited.add(childNode)) {
          stmtStack[last] = childNode;
          indexStack[last++] = -1;
        }
      }
    }
  }

  // deprecated methods and constructors follow

  /**
   * @deprecated use {@link #PseudoTopologicalOrderer()} instead
   */
  @Deprecated
  public PseudoTopologicalOrderer(boolean isReversed) {
    mIsReversed = isReversed;
  }

  /**
   * @param g
   *          a DirectedGraph instance whose nodes we wish to order.
   * @return a pseudo-topologically ordered list of the graph's nodes.
   * @deprecated use {@link #newList(DirectedGraph, boolean))} instead
   */
  @Deprecated
  public List<N> newList(DirectedGraph<N> g) {
    return computeOrder(g, !mIsReversed);
  }

  /**
   * Set the ordering for the orderer.
   * 
   * @param isReverse
   *          specify if we want reverse pseudo-topological ordering, or not.
   * @deprecated use {@link #newList(DirectedGraph, boolean))} instead
   */
  @Deprecated
  public void setReverseOrder(boolean isReversed) {
    mIsReversed = isReversed;
  }

  /**
   * Check the ordering for the orderer.
   * 
   * @return true if we have reverse pseudo-topological ordering, false otherwise.
   * @deprecated use {@link #newList(DirectedGraph, boolean))} instead
   */
  @Deprecated
  public boolean isReverseOrder() {
    return mIsReversed;
  }

}
