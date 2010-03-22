/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999-2010 Hossein Sadat-Mohtasham
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
package soot.toolkits.graph.pdg;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.DominatorTree;
import soot.toolkits.graph.DominatorsFinder;

/**
 * Constructs a multi-headed dominator tree. This is mostly the same as the DominatorTree
 * but the buildTree method is changed to allow mutilple heads. This can be used for 
 * graphs that are multi-headed and cannot be augmented to become single-headed.
 *
 * @author Hossein Sadat-Mohtasham
 * March 2009
 * 
 **/
@SuppressWarnings("unchecked")
public class MHGDominatorTree extends DominatorTree
{
   
	protected ArrayList<DominatorNode> heads;

 
	public MHGDominatorTree(DominatorsFinder dominators)
    {
		super(dominators);
    }

 
    /**
     * Returns the root(s)!!! of the dominator tree.
     **/
	public List<DominatorNode> getHeads()
    {
        return (List<DominatorNode>) heads.clone();
    }

    /**
     * This overrides the parent buildTree to allow multiple heads.
     * Mostly copied from the super class and modified.
     * 
     **/
    protected void buildTree()
    {
        // hook up children with parents and vice-versa
    	this.heads = null;
    	
        for(Iterator godesIt = graph.iterator(); godesIt.hasNext();)
        {
        	
            Object gode = godesIt.next();

            DominatorNode dode = fetchDode(gode);
            DominatorNode parent = fetchParent(gode);

            if(parent == null){
               
            	//make sure the array is created!
            	if(heads == null)
                	heads = new ArrayList();
            	
                heads.add(dode);
            }
            else{
                parent.addChild(dode);
                dode.setParent(parent);
            }
        }
      
        head = (DominatorNode) heads.get(0);
        // identify the tail nodes
        
        for(Iterator dodesIt = this.iterator(); dodesIt.hasNext();)
        {
            DominatorNode dode = (DominatorNode) dodesIt.next();

            if(dode.isTail())
                tails.add(dode);
        }
    
    }

 
}
