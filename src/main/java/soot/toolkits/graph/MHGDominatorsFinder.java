package soot.toolkits.graph;

import java.util.ArrayDeque;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Navindra Umanee <navindra@cs.mcgill.ca>
 * Copyright (C) 2007 Eric Bodden
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
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculate dominators for basic blocks.
 * <p>
 * Uses the algorithm contained in Dragon book, pg. 670-1.
 *
 * <pre>
 *       D(n0) := { n0 }
 *       for n in N - { n0 } do D(n) := N;
 *       while changes to any D(n) occur do
 *         for n in N - {n0} do
 *             D(n) := {n} U (intersect of D(p) over all predecessors p of n)
 * </pre>
 *
 * 2007/07/03 - updated to use {@link BitSet}s instead of {@link HashSet}s, as the most expensive operation in this algorithm
 * used to be cloning of the fullSet, which is very cheap for {@link BitSet}s.
 *
 * @author Navindra Umanee
 * @author Eric Bodden
 **/
public class MHGDominatorsFinder<N> implements DominatorsFinder<N> {

  protected final DirectedGraph<N> graph;
  protected final Set<N> heads;
  protected final Map<N, BitSet> nodeToFlowSet;
  protected final Map<N, Integer> nodeToIndex;
  protected final N[] indexToNode;

  public MHGDominatorsFinder(DirectedGraph<N> graph) {
    this.graph = graph;
    this.heads = new HashSet<>(graph.getHeads());
    Set<N> allReachableNodes = getAllReachableNodes(graph);
    int size = allReachableNodes.size() * 2 + 1;
    
    
    this.nodeToFlowSet = new HashMap<N, BitSet>(size, 0.7f);
    this.nodeToIndex = new HashMap<N, Integer>(size, 0.7f);
    this.indexToNode = (N[]) new Object[allReachableNodes.size()];

    int lastIndex = 0;
    for (N n : allReachableNodes) {
      nodeToIndex.put(n, lastIndex);
      indexToNode[lastIndex] = n;
      lastIndex++;
    }
    doAnalysis(allReachableNodes);
  }

  private Set<N> getAllReachableNodes(DirectedGraph<N> graph) {
    Deque<N> queue = new ArrayDeque<>(graph.getHeads());
    Set<N> seen = new LinkedHashSet<>();
    while (true) {
      N p = queue.poll();
      if (p == null) {
        break;
      }
      if (seen.add(p)) {
        queue.addAll(graph.getSuccsOf(p));
      }
      
    }
    return seen;
  }

  protected void doAnalysis(Set<N> allReachableNodes) {
    final DirectedGraph<N> graph = this.graph;

    // build full set
    int graphsize = nodeToIndex.size();
    BitSet fullSet = new BitSet(graphsize);
    fullSet.flip(0, graphsize);// set all to true

    // set up domain for intersection: head nodes are only dominated by themselves,
    // other nodes are dominated by everything else
    for (N o : allReachableNodes) {
      if (heads.contains(o)) {
        BitSet self = new BitSet();
        self.set(indexOfAssert(o));
        nodeToFlowSet.put(o, self);
      } else if (graph.getPredsOf(o).isEmpty()) {
        BitSet empty = new BitSet();
        nodeToFlowSet.put(o, empty);
      } else {
        nodeToFlowSet.put(o, fullSet);
      }
    }

    boolean changed;
    do {
      changed = false;
      for (N o : allReachableNodes) {
        if (heads.contains(o)) {
          continue;
        }

        // initialize to the "neutral element" for the intersection
        // this clone() is fast on BitSets (opposed to on HashSets)
        BitSet predsIntersect = null;

        // intersect over all predecessors
        for (N next : graph.getPredsOf(o)) {
          BitSet s = getDominatorsBitSet(next);
          if (s == null) {
            continue;
          }
          if (predsIntersect == null) {
            predsIntersect = (BitSet) s.clone();
          } else {
            predsIntersect.and(s);
          }
        }

        BitSet oldSet = getDominatorsBitSet(o);
        // each node dominates itself
        if (predsIntersect != null) {
          predsIntersect.set(indexOfAssert(o));
        } else {
          predsIntersect = fullSet;
        }
        if (!predsIntersect.equals(oldSet)) {
          nodeToFlowSet.put(o, predsIntersect);
          changed = true;
        }
      }
    } while (changed);
  }

  protected BitSet getDominatorsBitSet(N node) {
    BitSet bitSet = nodeToFlowSet.get(node);
    return bitSet;
  }

  protected int indexOfAssert(N o) {
    Integer index = nodeToIndex.get(o);
    assert (index != null) : "Node " + o + " is not in the graph!";
    return index;
  }


  @Override
  public DirectedGraph<N> getGraph() {
    return graph;
  }

  @Override
  public List<N> getDominators(N node) {
    // reconstruct list of dominators from bitset
    List<N> result = new ArrayList<N>();
    BitSet bitSet = getDominatorsBitSet(node);
    if (bitSet == null) {
      return Collections.emptyList();
    }
    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
      N n = indexToNode[i];
      result.add(n);
      if (i == Integer.MAX_VALUE) {
        break; // or (i+1) would overflow
      }
    }
    return result;
  }

  @Override
  public N getImmediateDominator(N node) {
    // root node
    if (heads.contains(node)) {
      return null;
    }

    BitSet doms = (BitSet) getDominatorsBitSet(node).clone();
    if (doms == null) {
      return null;
    }
    doms.clear(indexOfAssert(node));

    for (int i = doms.nextSetBit(0); i >= 0; i = doms.nextSetBit(i + 1)) {
      N dominator = indexToNode[i];
      if (isDominatedByAll(dominator, doms)) {
        if (dominator != null) {
          return dominator;
        }
      }
      if (i == Integer.MAX_VALUE) {
        break; // or (i+1) would overflow
      }
    }
    return null;
  }

  private boolean isDominatedByAll(N node, BitSet doms) {
    BitSet s1 = getDominatorsBitSet(node);
    if (s1 == null) {
      return doms.isEmpty();
    }
    for (int i = doms.nextSetBit(0); i >= 0; i = doms.nextSetBit(i + 1)) {
      if (!s1.get(i)) {
        return false;
      }
      if (i == Integer.MAX_VALUE) {
        break; // or (i+1) would overflow
      }
    }
    return true;
  }

  @Override
  public boolean isDominatedBy(N node, N dominator) {
    Integer idx = nodeToIndex.get(dominator);
    if (idx == null) {
      return false;
    }
    return getDominatorsBitSet(node).get(idx);
  }

  @Override
  public boolean isDominatedByAll(N node, Collection<N> dominators) {
    BitSet s1 = getDominatorsBitSet(node);
    if (s1 == null) {
      return dominators.isEmpty();
    }
    for (N n : dominators) {
      if (!s1.get(indexOfAssert(n))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isDominatingAllGiven(N node, Collection<N> given) {
    Integer c = nodeToIndex.get(node);
    if (c == null) {
      return given.isEmpty();
    }
    for (N n : given) {
      BitSet s1 = getDominatorsBitSet(n);
      if (!s1.get(c)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isDominatedByAny(N node, Collection<N> dominators) {
    BitSet s1 = getDominatorsBitSet(node);
    if (s1 == null) {
      return false;
    }
    for (N n : dominators) {
      if (s1.get(indexOfAssert(n))) {
        return true;
      }
    }
    return false;
  }
}
