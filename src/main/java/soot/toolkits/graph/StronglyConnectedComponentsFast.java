package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2008 Eric Bodden
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Identifies and provides an interface to query the strongly-connected components of DirectedGraph instances.
 *
 * Uses Tarjan's algorithm.
 *
 * @see DirectedGraph
 * @author Eric Bodden
 *
 *         Changes: 2015/08/23 Steven Arzt, added an iterative version of Tarjan's algorithm for large graphs
 */

public class StronglyConnectedComponentsFast<N> {
  protected final List<List<N>> componentList = new ArrayList<List<N>>();
  protected final List<List<N>> trueComponentList = new ArrayList<List<N>>();

  protected int index = 0;

  protected Map<N, Integer> indexForNode, lowlinkForNode;

  protected Stack<N> s;

  protected DirectedGraph<N> g;

  /**
   * @param g
   *          a graph for which we want to compute the strongly connected components.
   * @see DirectedGraph
   */
  public StronglyConnectedComponentsFast(DirectedGraph<N> g) {
    this.g = g;
    s = new Stack<N>();

    indexForNode = new HashMap<N, Integer>();
    lowlinkForNode = new HashMap<N, Integer>();

    for (N node : g) {
      if (!indexForNode.containsKey(node)) {
        // If the graph is too big, we cannot use a recursive algorithm
        // because it will blow up our stack space. The cut-off value when
        // to switch is more or less random, though.
        if (g.size() > 1000) {
          iterate(node);
        } else {
          recurse(node);
        }
      }
    }

    // free memory
    indexForNode = null;
    lowlinkForNode = null;
    s = null;
    g = null;
  }

  protected void recurse(N v) {
    int lowLinkForNodeV;
    indexForNode.put(v, index);
    lowlinkForNode.put(v, lowLinkForNodeV = index);
    index++;
    s.push(v);

    for (N succ : g.getSuccsOf(v)) {
      Integer indexForNodeSucc = indexForNode.get(succ);
      if (indexForNodeSucc == null) {
        recurse(succ);
        lowlinkForNode.put(v, lowLinkForNodeV = Math.min(lowLinkForNodeV, lowlinkForNode.get(succ)));
      } else if (s.contains(succ)) {
        lowlinkForNode.put(v, lowLinkForNodeV = Math.min(lowLinkForNodeV, indexForNodeSucc));
      }
    }
    if (lowLinkForNodeV == indexForNode.get(v).intValue()) {
      List<N> scc = new ArrayList<N>();
      N v2;
      do {
        v2 = s.pop();
        scc.add(v2);
      } while (v != v2);
      componentList.add(scc);
      if (scc.size() > 1) {
        trueComponentList.add(scc);
      } else {
        N n = scc.get(0);
        if (g.getSuccsOf(n).contains(n)) {
          trueComponentList.add(scc);
        }
      }
    }
  }

  protected void iterate(N x) {
    List<N> workList = new ArrayList<N>();
    List<N> backtrackList = new ArrayList<N>();
    workList.add(x);
    while (!workList.isEmpty()) {
      N v = workList.remove(0);

      boolean hasChildren = false;
      boolean isForward = false;
      if (!indexForNode.containsKey(v)) {
        indexForNode.put(v, index);
        lowlinkForNode.put(v, index);
        index++;
        s.push(v);
        isForward = true;
      }

      for (N succ : g.getSuccsOf(v)) {
        Integer indexForNodeSucc = indexForNode.get(succ);
        if (indexForNodeSucc == null) {
          // Recursive call
          workList.add(0, succ);
          hasChildren = true;
          break;
        } else if (!isForward) {
          // Returned from recursive call
          int lowLinkForNodeV = lowlinkForNode.get(v);
          lowlinkForNode.put(v, Math.min(lowLinkForNodeV, lowlinkForNode.get(succ)));
        } else if (isForward && s.contains(succ)) {
          int lowLinkForNodeV = lowlinkForNode.get(v);
          lowlinkForNode.put(v, Math.min(lowLinkForNodeV, indexForNodeSucc));
        }
      }

      if (hasChildren) {
        backtrackList.add(0, v);
      } else {
        if (!backtrackList.isEmpty()) {
          workList.add(0, backtrackList.remove(0));
        }

        int lowLinkForNodeV = lowlinkForNode.get(v);
        if (lowLinkForNodeV == indexForNode.get(v).intValue()) {
          List<N> scc = new ArrayList<N>();
          N v2;
          do {
            v2 = s.pop();
            scc.add(v2);
          } while (v != v2);
          componentList.add(scc);
          if (scc.size() > 1) {
            trueComponentList.add(scc);
          } else {
            N n = scc.get(0);
            if (g.getSuccsOf(n).contains(n)) {
              trueComponentList.add(scc);
            }
          }
        }
      }
    }
  }

  /**
   * @return the list of the strongly-connected components
   */
  public List<List<N>> getComponents() {
    return componentList;
  }

  /**
   * @return the list of the strongly-connected components, but only those that are true components, i.e. components which
   *         have more than one element or consists of one node that has itself as a successor
   */
  public List<List<N>> getTrueComponents() {
    return trueComponentList;
  }
}
