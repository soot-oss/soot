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
import java.util.*;
import soot.util.*;

/**
 * Class representing a dominator.  Has standard methods to find
 * children, parents, and it can also store the dominance frontier for
 * the node.
 *
 * <p> Fairly self-documenting.
 *
 * @author Navindra Umanee
 **/
public class DominatorNode
{
    private Block block;
    private DominatorNode parent;
    private List children;

    private DominatorTree dominatorTree;
    
    private boolean frontierKnown;
    private List dominanceFrontier;
    
    public DominatorNode(Block block)
    {
        this.block = block;
        children = new ArrayList();

        frontierKnown = false;
        dominanceFrontier = new ArrayList();
    }

    public void setParent(DominatorNode parent)
    {
        this.parent = parent;
    }

    public boolean addChild(DominatorNode child)
    {
        if(children.contains(child)){
            return false;
        }
        else{
            children.add(child);
            return true;
        }
    }

    public void setDominatorTree(DominatorTree dominatorTree)
    {
        this.dominatorTree = dominatorTree;
    }
    
    public boolean addToDominanceFrontier(DominatorNode frontierNode)
    {

        if(dominanceFrontier.contains(frontierNode)){
            return false;
        }
        else{
            dominanceFrontier.add(frontierNode);
            return true;
        }
    }

    public void setFrontierKnown()
    {
        frontierKnown = true;
    }

    public Block getBlock()
    {
        return block;
    }

    public DominatorNode getParent()
    {
        return parent;
    }

    public boolean isImmediateDominator(DominatorNode node)
    {
        return (parent == node);
    }
    
    public List getChildren()
    {
        return children;
    }

    public List getPreds()
    {
        Iterator predsIt = block.getPreds().iterator();

        List predNodes = new ArrayList();

        while(predsIt.hasNext()){
            Block pred = (Block) predsIt.next();
            predNodes.add(dominatorTree.fetchNode(pred));
        }
        
        return predNodes;
    }

    public List getSuccs()
    {
        Iterator succsIt = block.getSuccs().iterator();

        List succNodes = new ArrayList();

        while(succsIt.hasNext()){
            Block succ = (Block) succsIt.next();
            succNodes.add(dominatorTree.fetchNode(succ));
        }
        
        return succNodes;
    }
    
    public boolean isFrontierKnown()
    {
        return frontierKnown;
    }

    public List getDominanceFrontier()
    {
        if(!frontierKnown)
            throw new RuntimeException("DominatorNode: Frontier not properly defined.");
            
        return dominanceFrontier;
    }

    public boolean isHead()
    {
        if(parent == null){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean isTail()
    {
        if(children.size() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    public String toString()
    {
        return block.toString();
    }
}
