/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee
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

package soot.shimple.internal.analysis;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import soot.options.*;
import java.util.*;
import soot.util.*;

/**
 * Dominator tree that implements the Soot DirectedGraph interface
 * amongst other conveniences.
 *
 * <p> DominatorTree invokes DominatorsFinder to identify the list of
 * dominators for each node, and then proceeds to build a tree.
 *
 * @author Navindra Umanee
 **/
public class DominatorTree implements DirectedGraph
{
    private BlockGraph graph;
    private DominatorsFinder dominators;

    private ArrayList heads;
    private ArrayList tails;

    private HashMap blockToNode;
    
    public DominatorTree(BlockGraph graph)
    {
        this(graph, false);
    }

    public DominatorTree(BlockGraph graph, boolean constructFrontier)
    {
        if(Options.v().verbose())
            G.v().out.println("[" + graph.getBody().getMethod().getName() +
                               "]     Constructing DominatorTree...");

        this.graph = graph;
        
        dominators = new DominatorsFinder(graph);

        heads = new ArrayList();
        tails = new ArrayList();

        blockToNode = new HashMap();
        
        buildTree();

        if(constructFrontier)
            buildFrontier();
    }

    public BlockGraph getGraph()
    {
        return graph;
    }
    
    public List getHeads()
    {
        return (List) heads.clone();
    }

    public List getTails()
    {
        return (List) tails.clone();
    }
    
    public List getPredsOf(Object node)
    {
        List parent = new ArrayList();
        parent.add(((DominatorNode) node).getParent());

        return parent;
    }

    public List getSuccsOf(Object node)
    {
        return ((DominatorNode) node).getChildren();
    }

    public Iterator iterator()
    {
        return blockToNode.values().iterator();
    }

    public int size()
    {
        return blockToNode.size();
    }

    /**
     * Add all the necessary links between nodes to form a meaningful
     * tree structure.
     **/
    public void buildTree()
    {
        // hook up children with parents and vice-versa
        {
            Iterator blocksIt = graph.iterator();
            
            // *** TODO: getTails() hasn't been implemented in BlockGraph

            while(blocksIt.hasNext()){
                Block block = (Block) blocksIt.next();

                DominatorNode node = fetchNode(block);
                DominatorNode parent = fetchParent(block);

                if(parent == null){
                    heads.add(node);
                }
                else{
                    parent.addChild(node);
                    node.setParent(parent);
                }
            }
        }
        
        // identify the tail nodes
        {
            Iterator nodesIt = blockToNode.values().iterator();

            while(nodesIt.hasNext()){
                DominatorNode node = (DominatorNode) nodesIt.next();

                node.setDominatorTree(this);
                
                if(node.isTail()){
                    tails.add(node);
                }
            }
        }
    }

    /**
     * Clever convenience method to make sure we don't create more than one
     * DominatorNode for a given block.
     *
     * <p> Warning: Because fetchNode creates a new Node, if necessary,
     * it is not 100% suitable for outside public use.  We need it anyway,
     * hence the interface is not protected.
     **/
    public DominatorNode fetchNode(Block block)
    {
        DominatorNode node;
        
        if(blockToNode.containsKey(block)){
            node = (DominatorNode) blockToNode.get(block);
        }
        else{
            node = new DominatorNode(block);
            blockToNode.put(block, node);
        }

        return node;
    }

    /**
     * Has most of the intelligence necessary to build the tree.
     *
     * <p> In fact, it's not that smart and can be improved.  In
     * short, we try to identify the most dominated dominator :-) and
     * deduce that that dominator is our parent.
     **/
    protected DominatorNode fetchParent(Block block)
    {
        // identify all the root nodes
        if(graph.getHeads().contains(block))
            return null;

        List dominatorsList = dominators.getDominators(block);
        Iterator dominatorsIt = dominatorsList.iterator();
        DominatorNode immediateDominator = null;
        
        while((immediateDominator == null) && dominatorsIt.hasNext()){
            Block dominator = (Block) dominatorsIt.next();

            // we want a tree, we're not interested in self domination
            if(dominator == block)
                continue;
            
            List dominatorsListClone = dominators.getDominators(block);
            dominatorsListClone.remove(block);

            if(dominators.isDominatedByAll(dominator, dominatorsListClone))
                immediateDominator = fetchNode(dominator);
        }

        if(immediateDominator == null)
            throw new RuntimeException("Assertion failed.");
        
        return immediateDominator;
    }

    public void buildFrontier()
    {
        new DominanceFrontier(heads);
    }
}
