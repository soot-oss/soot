/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai, Patrick Lam
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.graph;

import soot.*;
import soot.util.*;
import java.util.*;


/**
 *  Orders in pseudo-topological order, 
 *  the nodes of a DirectedGraph instance.
 */

/* Updated By Marc Berndl May 13 */

public class PseudoTopologicalOrderer
{
    public static final boolean REVERSE = true;
    public PseudoTopologicalOrderer() {}
    public PseudoTopologicalOrderer(boolean isReversed) { mIsReversed = isReversed;}

    private Map stmtToColor;
    private static final Object GRAY = new Object();
    private LinkedList order;
    private boolean mIsReversed = false;
    private DirectedGraph graph;
    private int[] indexStack;
    private Object[] stmtStack;
    private int last;
        
    /**
     *  @param g a DirectedGraph instance whose nodes we wish to order.
     *  @return a pseudo-topologically ordered list of the graph's nodes.
     */
    public List newList(DirectedGraph g)
    {        
        return computeOrder(g);
    }

    /**
     *   Set the ordering for the orderer.
     *   @param isReverse specify if we want reverse pseudo-topological ordering, or not.
     */
    public void setReverseOrder(boolean isReversed)
    {
        mIsReversed = isReversed;
    }

    /**
     *   Check the ordering for the orderer.
     *   @return true if we have reverse pseudo-topological ordering, false otherwise.
     */
    public boolean isReverseOrder()
    {
        return mIsReversed;
    }

    /**
     *  Orders in pseudo-topological order.
     *  @param g a DirectedGraph instance we want to order the nodes for.
     *  @return an ordered list of the graph's nodes.
     */
    LinkedList computeOrder(DirectedGraph g)
    {
        stmtToColor = new HashMap((3*g.size())/2,0.7f);
        indexStack = new int[g.size()];
        stmtStack = new Object[g.size()];
        order = new LinkedList();
        graph = g;
        
        // Visit each node
        {
            Iterator stmtIt = g.iterator();            
            while(stmtIt.hasNext())
            {
                Object s = stmtIt.next();               
                if(stmtToColor.get(s) == null)
                    visitNode(s); 
            }
        }
        indexStack = null;
        stmtStack = null;
        stmtToColor = null;
        return order;
    }

    // Unfortunately, the nice recursive solution fails
    // because of stack overflows
    
    // Fill in the 'order' list with a pseudo topological order (possibly reversed)
    // list of statements starting at s.  Simulates recursion with a stack.
    
    
    private void visitNode(Object startStmt)
    {
        last = 0;
        
        stmtToColor.put(startStmt, GRAY);
        
        stmtStack[last] = startStmt;
        indexStack[last++]= -1;
        while(last > 0)
	{
            int toVisitIndex = ++indexStack[last-1];
            Object toVisitNode = stmtStack[last-1];
            
            if(toVisitIndex >= graph.getSuccsOf(toVisitNode).size())
            {
                // Visit this node now that we ran out of children 
                    if(mIsReversed)
                        order.addLast(toVisitNode);
                    else
                        order.addFirst(toVisitNode);
                           
		    last--;
            }
            else
            {
                Object childNode = graph.getSuccsOf(toVisitNode).get(toVisitIndex);
                
                    if(stmtToColor.get(childNode) == null)
                    {
                        stmtToColor.put(childNode, GRAY);                        
                        stmtStack[last]=childNode;
			indexStack[last++]=-1;
		    }
            }
        }
    }
    
}
