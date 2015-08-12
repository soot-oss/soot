/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.toolkits.graph;

import java.util.*;

/**
 * Constructs a dominator tree structure from the given
 * DominatorsFinder.  The nodes in DominatorTree are of type
 * DominatorNode.
 *
 * <p>
 *
 * Note: DominatorTree does not currently implement DirectedGraph
 * since it provides 4 methods of navigating the nodes where the
 * meaning of getPredsOf and getSuccsOf diverge from the usual meaning
 * in a DirectedGraph implementation.
 *
 * <p>
 *
 * If you need a DirectedGraph implementation, see DominatorTreeAdapter.
 *
 * @author Navindra Umanee
 **/
public class DominatorTree<N> implements Iterable<DominatorNode<N>>{

    protected DominatorsFinder<N> dominators;
    protected DirectedGraph<N> graph;
    protected List<DominatorNode<N>> heads;
    protected List<DominatorNode<N>> tails;
    
    /**
     * "gode" is a node in the original graph, "dode" is a node in the
     * dominator tree.
     **/
    protected Map<N, DominatorNode<N>> godeToDode;

    public DominatorTree(DominatorsFinder dominators) {
        // if(Options.v().verbose())
        // G.v().out.println("[" + graph.getBody().getMethod().getName() +
        // "]     Constructing DominatorTree...");

        this.dominators = dominators;
        this.graph = dominators.getGraph();

        heads = new ArrayList<DominatorNode<N>>();
        tails = new ArrayList<DominatorNode<N>>();
        godeToDode = new HashMap<N, DominatorNode<N>>();

        buildTree();
    }

    /**
     * @return  the original graph to which the DominatorTree pertains
     **/
    public DirectedGraph<N> getGraph() {
        return dominators.getGraph();
    }

    /**
     * @return  the root of the dominator tree.
     **/
    public List<DominatorNode<N>> getHeads() {
        return new ArrayList<DominatorNode<N>>(heads);
    }
    
    /**
     * Gets the first head of the dominator tree. This function is implemented 
     * for single-headed trees and mainly for backwards compatibility.
     * @return  The first head of the dominator tree
     */
    public DominatorNode<N> getHead() {
        return heads.isEmpty() ? null : heads.get(0);
    }

    /**
     * @return  list of the tails of the dominator tree.
     **/
    public List<DominatorNode<N>> getTails() {
        return new ArrayList<DominatorNode<N>>(tails);
    }

    /**
     * @return  the parent of {@code node} in the tree, null if the node is at
     * the root.
     **/
    public DominatorNode<N> getParentOf(DominatorNode<N> node) {
        return node.getParent();
    }

    /**
     * @return  the children of node in the tree.
     **/
    public List<DominatorNode<N>> getChildrenOf(DominatorNode<N> node) {
        return new ArrayList<DominatorNode<N>>(node.getChildren());
    }

    /**
     * @return  list of the DominatorNodes corresponding to the predecessors
     * of {@code node} in the original DirectedGraph
     **/
    public List<DominatorNode<N>> getPredsOf(DominatorNode<N> node) {
        List<N> preds = graph.getPredsOf(node.getGode());
        List<DominatorNode<N>> predNodes = new ArrayList<DominatorNode<N>>();
        for (N pred : preds) {
            predNodes.add(getDode(pred));
        }
        return predNodes;
    }

    /**
     * @return  list of the DominatorNodes corresponding to the successors
     * of {@code node} in the original DirectedGraph
     **/
    public List<DominatorNode<N>> getSuccsOf(DominatorNode<N> node) {
        List<N> succs = graph.getSuccsOf(node.getGode());
        List<DominatorNode<N>> succNodes = new ArrayList<DominatorNode<N>>();
        for (N succ : succs) {
            succNodes.add(getDode(succ));
        }
        return succNodes;
    }

    /**
     * @return  true if idom immediately dominates node.
     **/
    public boolean isImmediateDominatorOf(DominatorNode<N> idom, DominatorNode<N> node) {
        // node.getParent() could be null
        return (node.getParent() == idom);
    }

    /**
     * @return  true if dom dominates node.
     **/
    public boolean isDominatorOf(DominatorNode<N> dom, DominatorNode<N> node) {
        return dominators.isDominatedBy(node.getGode(), dom.getGode());
    }

    /**
     * @return  DominatorNode for a given node in the original DirectedGraph.
     **/
    public DominatorNode<N> getDode(N gode) {
        DominatorNode<N> dode = godeToDode.get(gode);

        if (dode == null) {
            throw new RuntimeException("Assertion failed: Dominator tree does not have a corresponding dode for gode (" + gode + ")");
        }

        return dode;
    }

    /**
     * @return  iterator over the nodes in the tree.  No ordering is implied.
     **/
    public Iterator<DominatorNode<N>> iterator() {
        return godeToDode.values().iterator();
    }

    /**
     * @return  the number of nodes in the tree
     **/
    public int size() {
        return godeToDode.size();
    }

    /**
     * Add all the necessary links between nodes to form a meaningful
     * tree structure.
     **/
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
        for (DominatorNode dode : this) {
            if(dode.isTail()) {
                tails.add(dode);
            }
        }
    }

    /**
     * Convenience method, ensures we don't create more than one
     * DominatorNode for a given block.
     **/
    protected DominatorNode<N> fetchDode(N gode) {
        DominatorNode<N> dode;

        if (godeToDode.containsKey(gode)) {
            dode = godeToDode.get(gode);
        } else {
            dode = new DominatorNode(gode);
            godeToDode.put(gode, dode);
        }

        return dode;
    }

    protected DominatorNode<N> fetchParent(N gode) {
        N immediateDominator = dominators.getImmediateDominator(gode);

        if (immediateDominator == null) {
            return null;
        }

        return fetchDode(immediateDominator);
    }
}
