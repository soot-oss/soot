/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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



public class PseudoTopologicalOrderer
{
    private static boolean isPseudoTopologicalOrderReady;
    private static List topOrder;

    private static Map stmtToColor;
    private static final int 
        WHITE = 0,
        GRAY = 1,
        BLACK = 2;

    private static LinkedList order;
    private static boolean mIsReversed;
  
    private static DirectedGraph graph;
        
    public static Iterator iteratorOf(DirectedGraph g)
    {        
        if(!isPseudoTopologicalOrderReady){
            topOrder = Collections.unmodifiableList(computeOrder(false, g));
            isPseudoTopologicalOrderReady = true;
        }
        
        return topOrder.iterator();
    }

    private static LinkedList computeOrder(boolean isReversed, DirectedGraph g)
    {
        stmtToColor = new HashMap();
    
        mIsReversed = isReversed;
        order = new LinkedList();
        graph = g;
        
        // Color all statements white
        {
            

            Iterator stmtIt = g.iterator();
            while(stmtIt.hasNext())
            {
                Unit s = (Unit) stmtIt.next();
                
                stmtToColor.put(s, new Integer(WHITE));
            }
        }
        
        // Visit each statement 
        {
            Iterator stmtIt = g.iterator();
            
            while(stmtIt.hasNext())
            {
                Unit s = (Unit) stmtIt.next();
               
                if(((Integer) stmtToColor.get(s)).intValue() == WHITE)
                    visitStmt(s); 
            }
        }
        
        return order;
    }

    // Unfortunately, the nice recursive solution fails
    // because of stack overflows
    /*
    private void visitStmt(Stmt s)
    {
        stmtToColor.put(s, new Integer(GRAY));
         
        Iterator succIt = getSuccsOf(s).iterator();
        
        while(succIt.hasNext())
        {
            Stmt succ = (Stmt) succIt.next();
            
            if(((Integer) stmtToColor.get(succ)).intValue() == WHITE)
                visitStmt(succ);
        }
        
        stmtToColor.put(s, new Integer(BLACK));
         
        if(isReversed)
            order.addLast(s);
        else
            order.addFirst(s); 
    }*/
    
    // Fill in the 'order' list with a pseudo topological order (possibly reversed)
    // list of statements starting at s.  Simulates recursion with a stack.
    
    
    private static void visitStmt(Unit startStmt)
    {
        LinkedList stmtStack = new LinkedList();
        LinkedList indexStack = new LinkedList();
        
        stmtToColor.put(startStmt, new Integer(GRAY));
        
        stmtStack.addLast(startStmt);
        indexStack.addLast(new Integer(-1));
        
        while(!stmtStack.isEmpty())
        {
            int toVisitIndex = ((Integer) indexStack.removeLast()).intValue();
            Unit toVisitStmt = (Unit) stmtStack.getLast();
            
            toVisitIndex++;
            
            indexStack.addLast(new Integer(toVisitIndex));
            
            if(toVisitIndex >= graph.getSuccsOf(toVisitStmt).size())
            {
                // Visit this node now that we ran out of children 
                    if(mIsReversed)
                        order.addLast(toVisitStmt);
                    else
                        order.addFirst(toVisitStmt);
                           
                    stmtToColor.put(toVisitStmt, new Integer(BLACK));                
                
                // Pop this node off
                    stmtStack.removeLast();
                    indexStack.removeLast();
            }
            else
            {
                Unit childStmt = (Unit) graph.getSuccsOf(toVisitStmt).get(toVisitIndex);
                
                // Visit this child next if not already visited (or on stack)
                    if(((Integer) stmtToColor.get(childStmt)).intValue() == WHITE)
                    {
                        stmtToColor.put(childStmt, new Integer(GRAY));
                        
                        stmtStack.addLast(childStmt);
                        indexStack.addLast(new Integer(-1));
                    }
            }
        }
    }
    



}
