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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Singletons;

/**
 * Provide the pseudo topological order of a graph's nodes. It has same functionality as PseudoTopologicalOrderer; however,
 * this class considers the order of successors. It runs slower but more precise. Currently it was only used by
 * ArrayBoundsCheckerAnalysis to reduce the iteration numbers.
 *
 * @see: PseudoTopologicalOrderer
 */
public class SlowPseudoTopologicalOrderer<N> implements Orderer<N> {

  public SlowPseudoTopologicalOrderer(Singletons.Global g) {
  }

  public static SlowPseudoTopologicalOrderer v() {
    return G.v().soot_toolkits_graph_SlowPseudoTopologicalOrderer();
  }

  private static abstract class AbstractOrderBuilder<N> {

    protected static enum Color { WHITE, GRAY, BLACK };

    protected final Map<N, Color> stmtToColor = new HashMap<N, Color>();
    protected final LinkedList<N> order = new LinkedList<N>();
    protected final DirectedGraph<N> graph;
    protected final boolean reverse;

    protected AbstractOrderBuilder(DirectedGraph<N> g, boolean reverse) {
      this.graph = g;
      this.reverse = reverse;
    }

    protected abstract void visitNode(N startStmt);

    /**
     * Orders in pseudo-topological order.
     * 
     * @return an ordered list of the graph's nodes.
     */
    public LinkedList<N> computeOrder() {
      // Color all nodes white
      for (N s : graph) {
        stmtToColor.put(s, Color.WHITE);
      }
      // Visit each node
      for (N s : graph) {
        if (stmtToColor.get(s) == Color.WHITE) {
          visitNode(s);
        }
      }

      return order;
    }
  }

  private static class ForwardOrderBuilder<N> extends AbstractOrderBuilder<N> {

    private final HashMap<N, List<N>> succsMap = new HashMap<N, List<N>>();
    private List<N> reverseOrder;

    /**
     * @param graph
     *          a DirectedGraph instance we want to order the nodes for.
     */
    public ForwardOrderBuilder(DirectedGraph<N> graph, boolean reverse) {
      super(graph, reverse);
    }

    /**
     * Orders in pseudo-topological order.
     * 
     * @return an ordered list of the graph's nodes.
     */
    public LinkedList<N> computeOrder() {
      reverseOrder = (new ReverseOrderBuilder<N>(graph)).computeOrder();
      return super.computeOrder();
    }

    // Unfortunately, the nice recursive solution fails because of stack
    // overflows. Fill in the 'order' list with a pseudo topological order
    // (possibly reversed) list of statements starting at s.
    // Simulates recursion with a stack.
    @Override
    protected void visitNode(N startStmt) {
      LinkedList<N> stmtStack = new LinkedList<N>();
      LinkedList<Integer> indexStack = new LinkedList<Integer>();

      stmtToColor.put(startStmt, Color.GRAY);

      stmtStack.addLast(startStmt);
      indexStack.addLast(-1);

      while (!stmtStack.isEmpty()) {
        int toVisitIndex = indexStack.removeLast();
        N toVisitNode = stmtStack.getLast();

        toVisitIndex++;

        indexStack.addLast(toVisitIndex);

        if (toVisitIndex >= graph.getSuccsOf(toVisitNode).size()) {
          // Visit this node now that we ran out of children
          if (reverse) {
            order.addLast(toVisitNode);
          } else {
            order.addFirst(toVisitNode);
          }

          stmtToColor.put(toVisitNode, Color.BLACK);

          // Pop this node off
          stmtStack.removeLast();
          indexStack.removeLast();
        } else {
          List<N> orderedSuccs = succsMap.get(toVisitNode);
          if (orderedSuccs == null) {
            orderedSuccs = new LinkedList<N>();
            succsMap.put(toVisitNode, orderedSuccs);
            /* make ordered succs */

            List<N> allsuccs = graph.getSuccsOf(toVisitNode);

            for (int i = 0; i < allsuccs.size(); i++) {
              N cur = allsuccs.get(i);
              int j = 0;
              for (; j < orderedSuccs.size(); j++) {
                N comp = orderedSuccs.get(j);
                if (reverseOrder.indexOf(cur) < reverseOrder.indexOf(comp)) {
                  break;
                }
              }
              orderedSuccs.add(j, cur);
            }
          }

          N childNode = orderedSuccs.get(toVisitIndex);

          // Visit this child next if not already visited (or on stack)
          if (stmtToColor.get(childNode) == Color.WHITE) {
            stmtToColor.put(childNode, Color.GRAY);

            stmtStack.addLast(childNode);
            indexStack.addLast(-1);
          }
        }
      }
    }
  }

  private static class ReverseOrderBuilder<N> extends AbstractOrderBuilder<N> {

    /**
     * @param graph
     *          a DirectedGraph instance we want to order the nodes for.
     */
    public ReverseOrderBuilder(DirectedGraph<N> graph) {
      super(graph, false);
    }

    @Override
    protected void visitNode(N startStmt) {
      LinkedList<N> stmtStack = new LinkedList<N>();
      LinkedList<Integer> indexStack = new LinkedList<Integer>();

      stmtToColor.put(startStmt, Color.GRAY);

      stmtStack.addLast(startStmt);
      indexStack.addLast(-1);

      while (!stmtStack.isEmpty()) {
        int toVisitIndex = indexStack.removeLast();
        N toVisitNode = stmtStack.getLast();

        toVisitIndex++;

        indexStack.addLast(toVisitIndex);

        if (toVisitIndex >= graph.getPredsOf(toVisitNode).size()) {
          // Visit this node now that we ran out of children
          if (reverse) {
            order.addLast(toVisitNode);
          } else {
            order.addFirst(toVisitNode);
          }

          stmtToColor.put(toVisitNode, Color.BLACK);

          // Pop this node off
          stmtStack.removeLast();
          indexStack.removeLast();
        } else {
          N childNode = graph.getPredsOf(toVisitNode).get(toVisitIndex);

          // Visit this child next if not already visited (or on stack)
          if (stmtToColor.get(childNode) == Color.WHITE) {
            stmtToColor.put(childNode, Color.GRAY);

            stmtStack.addLast(childNode);
            indexStack.addLast(-1);
          }
        }
      }
    }
  }

  private boolean mIsReversed = false;

  public SlowPseudoTopologicalOrderer() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<N> newList(DirectedGraph<N> g, boolean reverse) {
    this.mIsReversed = reverse;
    return (new ForwardOrderBuilder<>(g, reverse)).computeOrder();
  }

  // deprecated methods follow

  /**
   * @deprecated use {@link #SlowPseudoTopologicalOrderer()} instead
   */
  public SlowPseudoTopologicalOrderer(boolean isReversed) {
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
    return (new ForwardOrderBuilder<>(g, mIsReversed)).computeOrder();
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
