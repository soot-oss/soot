/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai, Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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

public class PseudoTopologicalOrderer
{
    public static final boolean REVERSE = true;
    public PseudoTopologicalOrderer() {}
    public PseudoTopologicalOrderer(boolean isReversed) { mIsReversed = isReversed;}

    private Map stmtToColor;
    private static final int 
        WHITE = 0,
        GRAY = 1,
        BLACK = 2;

    private LinkedList order;
    private boolean mIsReversed = false;
    private DirectedGraph graph;

        
    /**
     *  @param g a DirectedGraph instance whose nodes we which to order.
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
        stmtToColor = new HashMap();
            
        order = new LinkedList();
        graph = g;
        
        // Color all nodes white
        {
            

            Iterator stmtIt = g.iterator();
            while(stmtIt.hasNext())
            {
                Object s = stmtIt.next();
                
                stmtToColor.put(s, new Integer(WHITE));
            }
        }
        
        // Visit each node
        {
            Iterator stmtIt = g.iterator();
            
            while(stmtIt.hasNext())
            {
                Object s = stmtIt.next();
               
                if(((Integer) stmtToColor.get(s)).intValue() == WHITE)
                    visitNode(s); 
            }
        }
        
        return order;
    }

    // Unfortunately, the nice recursive solution fails
    // because of stack overflows
    
    // Fill in the 'order' list with a pseudo topological order (possibly reversed)
    // list of statements starting at s.  Simulates recursion with a stack.
    
    
    private void visitNode(Object startStmt)
    {
        LinkedList stmtStack = new LinkedList();
        LinkedList indexStack = new LinkedList();
        
        stmtToColor.put(startStmt, new Integer(GRAY));
        
        stmtStack.addLast(startStmt);
        indexStack.addLast(new Integer(-1));
        
        while(!stmtStack.isEmpty())
        {
            int toVisitIndex = ((Integer) indexStack.removeLast()).intValue();
            Object toVisitNode = stmtStack.getLast();
            
            toVisitIndex++;
            
            indexStack.addLast(new Integer(toVisitIndex));
            
            if(toVisitIndex >= graph.getSuccsOf(toVisitNode).size())
            {
                // Visit this node now that we ran out of children 
                    if(mIsReversed)
                        order.addLast(toVisitNode);
                    else
                        order.addFirst(toVisitNode);
                           
                    stmtToColor.put(toVisitNode, new Integer(BLACK));                
                
                // Pop this node off
                    stmtStack.removeLast();
                    indexStack.removeLast();
            }
            else
            {
                Object childNode = graph.getSuccsOf(toVisitNode).get(toVisitIndex);
                
                // Visit this child next if not already visited (or on stack)
                    if(((Integer) stmtToColor.get(childNode)).intValue() == WHITE)
                    {
                        stmtToColor.put(childNode, new Integer(GRAY));
                        
                        stmtStack.addLast(childNode);
                        indexStack.addLast(new Integer(-1));
                    }
            }
        }
    }
    
}
