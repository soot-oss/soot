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
 * Represents a dominator node in DominatorTree.  Usually you should
 * use DominatorTree or DominanceFrontier to obtain information on how
 * a node relates to other nodes instead of directly using any methods
 * provided here.
 *
 * @author Navindra Umanee
 **/
public class DominatorNode
{
    protected Object gode;
    protected DominatorNode parent;
    protected List children;

    protected DominatorNode(Object gode)
    {
        this.gode = gode;
        children = new ArrayList();
    }

    /**
     * Sets the parent of this node in the DominatorTree.  Usually
     * called internally.
     **/
    public void setParent(DominatorNode parent)
    {
        this.parent = parent;
    }

    /**
     * Adds a child to the internal list of children of this node in
     * tree.  Usually called internally.
     **/
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

    /**
     * Returns the node (from the original DirectedGraph) encapsulated
     * by this DominatorNode.
     **/
    public Object getGode()
    {
        return gode;
    }

    /**
     * Returns the parent of the node in the DominatorTree.
     **/
    public DominatorNode getParent()
    {
        return parent;
    }

    /**
     * Returns a backed list of the children of this node in the
     * DominatorTree.
     **/
    public List getChildren()
    {
        return children;
    }

    /**
     * Returns true if this node is the head of its DominatorTree.
     **/
    public boolean isHead()
    {
        if(parent == null)
            return true;
        else
            return false;
    }

    /**
     * Returns true if this node is a tail of its DominatorTree.
     **/
    public boolean isTail()
    {
        if(children.size() == 0)
            return true;
        else
            return false;
    }

    public String toString()
    {
        // *** FIXME: Print info about parent and children
        return gode.toString();
    }
}
