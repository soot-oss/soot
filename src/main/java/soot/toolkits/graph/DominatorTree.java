package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructs a dominator tree structure from the given DominatorsFinder. The nodes in DominatorTree are of type
 * DominatorNode.
 *
 * <p>
 *
 * Note: DominatorTree does not currently implement DirectedGraph since it provides 4 methods of navigating the nodes where
 * the meaning of getPredsOf and getSuccsOf diverge from the usual meaning in a DirectedGraph implementation.
 *
 * <p>
 *
 * If you need a DirectedGraph implementation, see DominatorTreeAdapter.
 *
 * @author Navindra Umanee
 */
public class DominatorTree<N> implements Iterable<DominatorNode<N>> {
  private static final Logger logger = LoggerFactory.getLogger(DominatorTree.class);

  protected final DominatorsFinder<N> dominators;
  protected final DirectedGraph<N> graph;
  protected final List<DominatorNode<N>> heads;
  protected final List<DominatorNode<N>> tails;
  /**
   * "gode" is a node in the original graph, "dode" is a node in the dominator tree.
   */
  protected final Map<N, DominatorNode<N>> godeToDode;

  public DominatorTree(DominatorsFinder<N> dominators) {
    // if(Options.v().verbose())
    // logger.debug("[" + graph.getBody().getMethod().getName() +
    // "] Constructing DominatorTree...");

    this.dominators = dominators;
    this.graph = dominators.getGraph();

    this.heads = new ArrayList<DominatorNode<N>>();
    this.tails = new ArrayList<DominatorNode<N>>();
    this.godeToDode = new HashMap<N, DominatorNode<N>>();

    buildTree();
  }

  /**
   * @return the original graph to which the DominatorTree pertains
   */
  public DirectedGraph<N> getGraph() {
    return dominators.getGraph();
  }

  /**
   * @return the root of the dominator tree.
   */
  public List<DominatorNode<N>> getHeads() {
    return Collections.unmodifiableList(heads);
  }

  /**
   * Gets the first head of the dominator tree. This function is implemented for single-headed trees and mainly for backwards
   * compatibility.
   *
   * @return The first head of the dominator tree
   */
  public DominatorNode<N> getHead() {
    return heads.isEmpty() ? null : heads.get(0);
  }

  /**
   * @return list of the tails of the dominator tree.
   */
  public List<DominatorNode<N>> getTails() {
    return Collections.unmodifiableList(tails);
  }

  /**
   * @return the parent of {@code node} in the tree, null if the node is at the root.
   */
  public DominatorNode<N> getParentOf(DominatorNode<N> node) {
    return node.getParent();
  }

  /**
   * @return the children of node in the tree.
   */
  public List<DominatorNode<N>> getChildrenOf(DominatorNode<N> node) {
    return Collections.unmodifiableList(node.getChildren());
  }

  /**
   * @return list of the DominatorNodes corresponding to the predecessors of {@code node} in the original DirectedGraph
   */
  public List<DominatorNode<N>> getPredsOf(DominatorNode<N> node) {
    List<DominatorNode<N>> predNodes = new ArrayList<DominatorNode<N>>();
    for (N pred : graph.getPredsOf(node.getGode())) {
      predNodes.add(getDode(pred));
    }
    return predNodes;
  }

  /**
   * @return list of the DominatorNodes corresponding to the successors of {@code node} in the original DirectedGraph
   */
  public List<DominatorNode<N>> getSuccsOf(DominatorNode<N> node) {
    List<DominatorNode<N>> succNodes = new ArrayList<DominatorNode<N>>();
    for (N succ : graph.getSuccsOf(node.getGode())) {
      succNodes.add(getDode(succ));
    }
    return succNodes;
  }

  /**
   * @return true if idom immediately dominates node.
   */
  public boolean isImmediateDominatorOf(DominatorNode<N> idom, DominatorNode<N> node) {
    // node.getParent() could be null
    return (node.getParent() == idom);
  }

  /**
   * @return true if dom dominates node.
   */
  public boolean isDominatorOf(DominatorNode<N> dom, DominatorNode<N> node) {
    return dominators.isDominatedBy(node.getGode(), dom.getGode());
  }

  /**
   * @return DominatorNode for a given node in the original DirectedGraph.
   */
  public DominatorNode<N> getDode(N gode) {
    DominatorNode<N> dode = godeToDode.get(gode);

    if (dode == null) {
      throw new RuntimeException(
          "Assertion failed: Dominator tree does not have a corresponding dode for gode (" + gode + ")");
    }

    return dode;
  }

  /**
   * @return iterator over the nodes in the tree. No ordering is implied.
   */
  @Override
  public Iterator<DominatorNode<N>> iterator() {
    return godeToDode.values().iterator();
  }

  /**
   * @return the number of nodes in the tree
   */
  public int size() {
    return godeToDode.size();
  }

  /**
   * Add all the necessary links between nodes to form a meaningful tree structure.
   */
  protected void buildTree() {
    // hook up children with parents and vice-versa
    for (N gode : graph) {
      DominatorNode<N> dode = fetchDode(gode);
      DominatorNode<N> parent = fetchParent(gode);

      if (parent == null) {
        heads.add(dode);
      } else {
        parent.addChild(dode);
        dode.setParent(parent);
      }
    }

    // identify the tail nodes
    for (DominatorNode<N> dode : this) {
      if (dode.isTail()) {
        tails.add(dode);
      }
    }
    if (heads instanceof ArrayList) {
      ((ArrayList<?>) heads).trimToSize(); // potentially a long-lived object
    }
    if (tails instanceof ArrayList) {
      ((ArrayList<?>) tails).trimToSize(); // potentially a long-lived object
    }
  }

  /**
   * Convenience method, ensures we don't create more than one DominatorNode for a given block.
   */
  protected DominatorNode<N> fetchDode(N gode) {
    DominatorNode<N> dode = godeToDode.get(gode);
    if (dode == null) {
      dode = new DominatorNode<N>(gode);
      godeToDode.put(gode, dode);
    }
    return dode;
  }

  protected DominatorNode<N> fetchParent(N gode) {
    N immediateDominator = dominators.getImmediateDominator(gode);
    return (immediateDominator == null) ? null : fetchDode(immediateDominator);
  }
}
