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

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import soot.options.*;
import java.util.*;
import soot.util.*;

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
public class DominatorTree
{
    protected DominatorsFinder dominators;
    protected DirectedGraph graph;
    protected DominatorNode head;
    protected ArrayList tails;

    /**
     * "gode" is a node in the original graph, "dode" is a node in the
     * dominator tree.
     **/
    protected HashMap godeToDode;
    
    public DominatorTree(DominatorsFinder dominators)
    {
        // if(Options.v().verbose())
        // G.v().out.println("[" + graph.getBody().getMethod().getName() +
        // "]     Constructing DominatorTree...");

        this.dominators = dominators;
        this.graph = dominators.getGraph();
        
        head = null;
        tails = new ArrayList();
        godeToDode = new HashMap();
        
        buildTree();
    }

    /**
     * Returns the original graph to which the Dominator tree
     * pertains.
     **/
    public DirectedGraph getGraph()
    {
        return dominators.getGraph();
    }
    
    /**
     * Returns the root of the dominator tree.
     **/
    public DominatorNode getHead()
    {
        return head;
    }

    /**
     * Returns a list of the tails of the dominator tree.
     **/
    public List getTails()
    {
        return (List) tails.clone();
    }

    /**
     * Returns the parent of node in the tree, null if the node is at
     * the root.
     **/
    public DominatorNode getParentOf(DominatorNode node)
    {
        return node.getParent();
    }

    /**
     * Returns the children of node in the tree.
     **/
    public List getChildrenOf(DominatorNode node)
    {
        return (List)((ArrayList)node.getChildren()).clone();
    }

    /**
     * Finds all the predecessors of node in the original
     * DirectedGraph and returns a list of the corresponding
     * DominatorNodes.
     **/
    public List getPredsOf(DominatorNode node)
    {
        List preds = graph.getPredsOf(node.getGode());

        List predNodes = new ArrayList();
        
        for(Iterator predsIt = preds.iterator(); predsIt.hasNext();){
            Object pred = predsIt.next();
            predNodes.add(getDode(pred));
        }

        return predNodes;
    }

    /**
     * Finds all the successors of node in the original DirectedGraph
     * and returns a list of the corresponding DominatorNodes.
     **/
    public List getSuccsOf(DominatorNode node)
    {
        List succs = graph.getSuccsOf(node.getGode());

        List succNodes = new ArrayList();
        
        for(Iterator succsIt = succs.iterator(); succsIt.hasNext();){
            Object succ = succsIt.next();
            succNodes.add(getDode(succ));
        }

        return succNodes;
    }

    /**
     * Returns true if idom immediately dominates node.
     **/
    public boolean isImmediateDominatorOf(DominatorNode idom, DominatorNode node)
    {
        // node.getParent() could be null
        return (node.getParent() == idom);
    }

    /**
     * Returns true if dom dominates node.
     **/
    public boolean isDominatorOf(DominatorNode dom, DominatorNode node)
    {
        return dominators.isDominatedBy(node.getGode(), dom.getGode());
    }

    /**
     * Returns the DominatorNode for a given node in the original
     * DirectedGraph.
     **/
    public DominatorNode getDode(Object gode)
    {
        DominatorNode dode = (DominatorNode) godeToDode.get(gode);

        if(dode == null)
            throw new RuntimeException("Assertion failed: Dominator tree does not have a corresponding dode for gode (" + gode + ")");

        return dode;
    }

    /**
     * Returns an iterator over the nodes in the tree.  No ordering is
     * implied.
     **/
    public Iterator iterator()
    {
        return godeToDode.values().iterator();
    }

    /**
     * Returns the number of nodes in the tree.
     **/
    public int size()
    {
        return godeToDode.size();
    }

    /**
     * Add all the necessary links between nodes to form a meaningful
     * tree structure.
     **/
    protected void buildTree()
    {
        // hook up children with parents and vice-versa
        {
            for(Iterator godesIt = graph.iterator(); godesIt.hasNext();){
                Object gode = godesIt.next();

                DominatorNode dode = fetchDode(gode);
                DominatorNode parent = fetchParent(gode);

                if(parent == null){
                    if(head != null)
                        throw new RuntimeException("Assertion failed.");
                    
                    head = dode;
                }
                else{
                    parent.addChild(dode);
                    dode.setParent(parent);
                }
            }
        }
        
        // identify the tail nodes
        {
            for(Iterator dodesIt = this.iterator(); dodesIt.hasNext();){
                DominatorNode dode = (DominatorNode) dodesIt.next();

                if(dode.isTail())
                    tails.add(dode);
            }
        }
    }

    /**
     * Convenience method, ensures we don't create more than one
     * DominatorNode for a given block.
     **/
    protected DominatorNode fetchDode(Object gode)
    {
        DominatorNode dode;
        
        if(godeToDode.containsKey(gode)){
            dode = (DominatorNode) godeToDode.get(gode);
        }
        else{
            dode = new DominatorNode(gode);
            godeToDode.put(gode, dode);
        }

        return dode;
    }

    protected DominatorNode fetchParent(Object gode)
    {
        Object immediateDominator = dominators.getImmediateDominator(gode);

        if(immediateDominator == null)
            return null;
        
        return fetchDode(immediateDominator);
    }
}
