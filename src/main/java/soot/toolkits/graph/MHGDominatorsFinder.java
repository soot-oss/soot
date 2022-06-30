package soot.toolkits.graph;

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
import java.util.HashMap;
import java.util.HashSet;
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
  protected final Map<Integer, N> indexToNode;
  protected int lastIndex = 0;

  public MHGDominatorsFinder(DirectedGraph<N> graph) {
    this.graph = graph;
    this.heads = new HashSet<>(graph.getHeads());
    int size = graph.size() * 2 + 1;
    this.nodeToFlowSet = new HashMap<N, BitSet>(size, 0.7f);
    this.nodeToIndex = new HashMap<N, Integer>(size, 0.7f);
    this.indexToNode = new HashMap<Integer, N>(size, 0.7f);
    doAnalysis();
  }

  protected void doAnalysis() {
    final DirectedGraph<N> graph = this.graph;

    // build full set
    BitSet fullSet = new BitSet(graph.size());
    fullSet.flip(0, graph.size());// set all to true

    // set up domain for intersection: head nodes are only dominated by themselves,
    // other nodes are dominated by everything else
    for (N o : graph) {
      if (heads.contains(o)) {
        BitSet self = new BitSet();
        self.set(indexOf(o));
        nodeToFlowSet.put(o, self);
      } else {
        nodeToFlowSet.put(o, fullSet);
      }
    }

    boolean changed;
    do {
      changed = false;
      for (N o : graph) {
        if (heads.contains(o)) {
          continue;
        }

        // initialize to the "neutral element" for the intersection
        // this clone() is fast on BitSets (opposed to on HashSets)
        BitSet predsIntersect = (BitSet) fullSet.clone();

        // intersect over all predecessors
        for (N next : graph.getPredsOf(o)) {
          predsIntersect.and(getDominatorsBitSet(next));
        }

        BitSet oldSet = getDominatorsBitSet(o);
        // each node dominates itself
        predsIntersect.set(indexOf(o));
        if (!predsIntersect.equals(oldSet)) {
          nodeToFlowSet.put(o, predsIntersect);
          changed = true;
        }
      }
    } while (changed);
  }

  protected BitSet getDominatorsBitSet(N node) {
    BitSet bitSet = nodeToFlowSet.get(node);
    assert (bitSet != null) : "Node " + node + " is not in the graph!";
    return bitSet;
  }

  protected int indexOfAssert(N o) {
    Integer index = nodeToIndex.get(o);
    assert (index != null) : "Node " + o + " is not in the graph!";
    return index;
  }

  protected int indexOf(N o) {
    Integer index = nodeToIndex.get(o);
    if (index == null) {
      index = lastIndex;
      nodeToIndex.put(o, index);
      indexToNode.put(index, o);
      lastIndex++;
    }
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
    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
      result.add(indexToNode.get(i));
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
    doms.clear(indexOfAssert(node));

    for (int i = doms.nextSetBit(0); i >= 0; i = doms.nextSetBit(i + 1)) {
      N dominator = indexToNode.get(i);
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
    return getDominatorsBitSet(node).get(indexOfAssert(dominator));
  }

  @Override
  public boolean isDominatedByAll(N node, Collection<N> dominators) {
    BitSet s1 = getDominatorsBitSet(node);
    for (N n : dominators) {
      if (!s1.get(indexOfAssert(n))) {
        return false;
      }
    }
    return true;
  }
}
