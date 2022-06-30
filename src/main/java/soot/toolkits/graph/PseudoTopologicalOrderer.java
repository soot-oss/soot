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

  private static class ReverseOrderBuilder<N> {

    private final DirectedGraph<N> graph;
    private final int graphSize;
    private final int[] indexStack;
    private final N[] stmtStack;
    private final Set<N> visited;
    private final N[] order;
    private int orderLength;

    /**
     * @param g
     *          a DirectedGraph instance we want to order the nodes for.
     */
    public ReverseOrderBuilder(DirectedGraph<N> g) {
      this.graph = g;
      final int n = g.size();
      this.graphSize = n;
      this.visited = Collections.newSetFromMap(new IdentityHashMap<N, Boolean>(n * 2 + 1));
      this.indexStack = new int[n];
      @SuppressWarnings("unchecked")
      N[] tempStmtStack = (N[]) new Object[n];
      this.stmtStack = tempStmtStack;
      @SuppressWarnings("unchecked")
      N[] tempOrder = (N[]) new Object[n];
      this.order = tempOrder;
      this.orderLength = 0;
    }

    /**
     * Orders in pseudo-topological order.
     * 
     * @param reverse
     *          specify if we want reverse pseudo-topological ordering, or not.
     * @return an ordered list of the graph's nodes.
     */
    public List<N> computeOrder(boolean reverse) {
      // Visit each node
      for (N s : graph) {
        if (visited.add(s)) {
          visitNode(s);
        }
        if (orderLength == graphSize) {
          break;
        }
      }

      if (reverse) {
        reverseArray(order);
      }
      return Arrays.asList(order);
    }

    // Unfortunately, the nice recursive solution fails
    // because of stack overflows
    // Fill in the 'order' list with a pseudo topological order
    // list of statements starting at s. Simulates recursion with a stack.
    private void visitNode(N startStmt) {
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
  }

  private boolean mIsReversed = false;

  public PseudoTopologicalOrderer() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<N> newList(DirectedGraph<N> g, boolean reverse) {
    this.mIsReversed = reverse;
    return (new ReverseOrderBuilder<N>(g)).computeOrder(!reverse);
  }

  // deprecated methods and constructors follow

  /**
   * @deprecated use {@link #PseudoTopologicalOrderer()} instead
   */
  @Deprecated
  public PseudoTopologicalOrderer(boolean isReversed) {
    this.mIsReversed = isReversed;
  }

  /**
   * @param g
   *          a DirectedGraph instance whose nodes we wish to order.
   * @return a pseudo-topologically ordered list of the graph's nodes.
   * @deprecated use {@link #newList(DirectedGraph, boolean))} instead
   */
  @Deprecated
  public List<N> newList(DirectedGraph<N> g) {
    return (new ReverseOrderBuilder<N>(g)).computeOrder(!mIsReversed);
  }

  /**
   * Set the ordering for the orderer.
   * 
   * @param isReversed
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
