/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $JimpleVersion: 0.5 $

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:
 
 - Modified on September 22, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Added support for exception edge inclusion.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Many changes.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;

public class StmtGraph
{
    List heads;
    List tails;
        
    Map stmtToSuccs;        // Stmt to List
    Map stmtToPreds;        // Stmt to List
    Method method;
    List stmts;
    int size;
    StmtGraphBody body;
    
    StmtGraph(StmtGraphBody body, StmtListBody listBody, boolean
        addExceptionEdges)
    {
        StmtList stmtList = listBody.getStmtList();
        
        this.body = body;    
        this.method = listBody.getMethod();

        // Build stmts (for iterator)
        {
            stmts = new LinkedList();
            
            stmts.addAll(stmtList);
            size = stmtList.size();
        }
        
        // Build successors
        {
            Map classToHandler = new HashMap(); // list of exceptions being caught, and their handlers
        
            stmtToSuccs = new HashMap(size * 2 + 1, 0.7f);
            stmtToPreds = new HashMap(size * 2 + 1, 0.7f);
            

            // Add regular successors                
            {
                ListIterator stmtIt = stmtList.listIterator();

                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
    
                    List successors = new ArrayList();
                    boolean addNext = true;
                    
                    if(s instanceof GotoStmt)
                    {
                        successors.add(((GotoStmt) s).getTarget());
                        addNext = false;
                    }
                    else if(s instanceof IfStmt)
                    {
                        successors.add(((IfStmt) s).getTarget());
                    }
                    else if(s instanceof ReturnStmt || s instanceof ReturnVoidStmt)
                    {
                        addNext = false;
                    }
                    else if(s instanceof RetStmt)
                    {
                        // Add all statements which get their address taken
                        
                        ListIterator it = stmtList.listIterator();
                        
                        while(it.hasNext())
                        {
                            Stmt stmt = (Stmt) it.next();
                            
                            if(stmt instanceof AssignStmt)
                            {
                                AssignStmt as = (AssignStmt) stmt;
                                
                                if(as.getRightOp() instanceof NextNextStmtAddress)
                                {
                                    Iterator succIt = stmtList.listIterator(it.nextIndex());
                                    
                                    if(succIt.hasNext())
                                    {
                                        succIt.next();
                                        
                                        if(succIt.hasNext())
                                            successors.add(succIt.next());
                                    }
                                }
                            }
                        }
                    
                        addNext = false;
                    }
                    else if(s instanceof ThrowStmt)
                    {
                        addNext = false;
                    }
                    else if(s instanceof LookupSwitchStmt)
                    {
                        LookupSwitchStmt l = (LookupSwitchStmt) s;
                        
                        successors.add(l.getDefaultTarget());
                        
                        Iterator targetIt = l.getTargets().iterator();
                        
                        while(targetIt.hasNext())
                            successors.add(targetIt.next());
                            
                        addNext = false;
                    }
                    else if(s instanceof TableSwitchStmt)
                    {
                        TableSwitchStmt t = (TableSwitchStmt) s;
                        
                        successors.add(t.getDefaultTarget());
                        
                        Iterator targetIt = t.getTargets().iterator();
                        
                        while(targetIt.hasNext())
                            successors.add(targetIt.next());
                            
                        
                        addNext = false;
                    }
                    
                    // Put the next statement as the successor
                        if(addNext)
                        {
                            successors.add(stmtList.get(stmtIt.nextIndex()));
                        }
                       
                                     
                    // Store away successors
                        stmtToSuccs.put(s, successors);
                }
            }
            
            // Add exception based successors            
                if(addExceptionEdges)
                {
                    Iterator trapIt = listBody.getTrapTable().getTraps().
                        iterator();
                        
                    while(trapIt.hasNext())
                    {
                        StmtTrap trap = (StmtTrap) trapIt.next();
                        
                        Stmt beginStmt = trap.getBeginStmt();
                        Stmt handlerStmt = trap.getHandlerStmt();
                        Stmt endStmt = trap.getEndStmt();
                        Iterator stmtIt = stmtList.listIterator(stmtList.indexOf(beginStmt));
                        
                        for(;;)
                        {
                            Stmt s = (Stmt) stmtIt.next();
                            
                            ((List) stmtToSuccs.get(s)).add(handlerStmt);
                            
                            if(s == endStmt)
                                break;
                        } 
                    }
                }
                
            // Make successors unmodifiable
            {
                ListIterator stmtIt = stmtList.listIterator();

                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
                    stmtToSuccs.put(s, Collections.unmodifiableList((List) stmtToSuccs.get(s)));
                }
            }
        }

                
        // Build predecessors
        {
            Map stmtToPredList = new HashMap(size * 2 + 1, 0.7f);
            
            // initialize the pred sets to empty
            {
                Iterator stmtIt = stmtList.iterator();
                
                while(stmtIt.hasNext())
                {
                    stmtToPredList.put(stmtIt.next(), new ArrayList());
                }
            }
               
            // Modify preds set for each successor for this statement
            {
                Iterator stmtIt = stmtList.iterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
                    Iterator succIt = ((List) stmtToSuccs.get(s)).iterator();
                    
                    while(succIt.hasNext())
                    {
                        List predList = (List) stmtToPredList.get(succIt.next());
                        predList.add(s);
                    }
                }
            }
            
                                
            // Convert pred lists to arrays
            {
                Iterator stmtIt = stmtList.iterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
                    
                    List predList = (List) stmtToPredList.get(s);
                    stmtToPreds.put(s, Collections.unmodifiableList(predList));
                }   
            }
            
        }

        // Build tails
        {
            List tailList = new ArrayList();
            
            // Build the set
            {
                Iterator stmtIt = stmtList.iterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
                    
                    List succs = (List) stmtToSuccs.get(s);
                    
                    if(succs.size() == 0)
                        tailList.add(s);
                }
            }
               
            tails = Collections.unmodifiableList(tailList);
        }
        
        // Build heads
        {
            List headList = new ArrayList();
            
            // Build the set
            {
                Iterator stmtIt = stmtList.iterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
                    List preds = (List) stmtToPreds.get(s);
                    
                    if(preds.size() == 0)
                        headList.add(s);
                }
            }
         
            heads = Collections.unmodifiableList(headList);
        }
        
    }        
        
    public List getHeads()
    {
        return heads;
    }
    
    public List getTails()
    {
        return tails;
    }
    
    public List getPredsOf(Stmt s)
    {
        if(!stmtToPreds.containsKey(s))
            throw new RuntimeException("Invalid stmt" + s);
            
        return (List) stmtToPreds.get(s);
    }
    
    public List getSuccsOf(Stmt s)
    {
        if(!stmtToSuccs.containsKey(s))
            throw new RuntimeException("Invalid stmt" + s);
            
        return (List) stmtToSuccs.get(s);
    }

    public Iterator iterator()
    {
        return new GraphIterator();
    }

    public int size()
    {
        return size;
    }
    
    private class GraphIterator implements Iterator
    {
        Iterator iterator;
        
        public GraphIterator()
        {
            iterator = stmts.iterator();
        }
        
        public boolean hasNext()
        {
            return iterator.hasNext();
        }
        
        public Object next()
        {
            return iterator.next();
        }
        
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException(); 
        }
    }

}







