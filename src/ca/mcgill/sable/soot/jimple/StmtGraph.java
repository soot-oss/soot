/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
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

 - Modified on March 15, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Added a pseudo topological order iterator (and its reverse).
   Moved in Patrick's getPath code.
   
 - Modified on March 13, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Re-organized the timers.

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on September 22, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Added support for exception edge inclusion.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Many changes.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public class StmtGraph
{
    List heads;
    List tails;

    Map stmtToSuccs;        // Stmt to List
    Map stmtToPreds;        // Stmt to List
    SootMethod method;
    List stmts;
    int size;
    StmtList stmtList;

    public StmtBody getBody()
    {
        return stmtList.getBody();
    }

    StmtGraph(StmtList stmtList, boolean addExceptionEdges)
    {
        this.stmtList = stmtList;
        this.method = getBody().getMethod();
        
        if(Main.isVerbose)
            System.out.println("[" + method.getName() + 
            "]     Constructing StmtGraph...");
      
        if(Main.isProfilingOptimization)
            Main.graphTimer.start();
      
        // Build stmts (for iterator)
        {
            stmts = new LinkedList();

            stmts.addAll(stmtList);
            stmts = Collections.unmodifiableList(stmts);
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

                                if(as.getRightOp() instanceof NextNextStmtRef)
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
                    Iterator trapIt = getBody().getTraps().
                        iterator();

                    while(trapIt.hasNext())
                    {
                        Trap trap = (Trap) trapIt.next();

                        Stmt beginStmt = (Stmt) trap.getBeginUnit();
                        Stmt handlerStmt = (Stmt) trap.getHandlerUnit();
                        Stmt endStmt = (Stmt) trap.getEndUnit();
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

        if(Main.isProfilingOptimization)
            Main.graphTimer.end();
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
        return stmts.iterator();
    }

    public int size()
    {
        return size;
    }

    private boolean isPseudoTopologicalOrderReady;
    private List topOrder;
              
    public Iterator pseudoTopologicalOrderIterator()
    {
        if(!isPseudoTopologicalOrderReady)
        {
            topOrder = Collections.unmodifiableList(computeOrder(false));
            isPseudoTopologicalOrderReady = true;
        }
        
        return topOrder.iterator();
    }   
    
    private boolean isReversePseudoTopologicalOrderReady;
    private List reverseTopOrder;
              
    public Iterator reversePseudoTopologicalOrderIterator()
    {
        if(!isReversePseudoTopologicalOrderReady)
        {
            reverseTopOrder = Collections.unmodifiableList(computeOrder(false));
            isReversePseudoTopologicalOrderReady = true;
        }
                
        return reverseTopOrder.iterator();
    }   
    
    private Map stmtToColor;
    private final int WHITE = 0,
              GRAY = 1,
              BLACK = 2;

    private LinkedList order;
    private boolean isReversed;
    
    private LinkedList computeOrder(boolean isReversed)
    {
        stmtToColor = new HashMap();
    
        this.isReversed = isReversed;
        order = new LinkedList();
        
        // Color all statements white
        {
            Iterator stmtIt = iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                stmtToColor.put(s, new Integer(WHITE));
            }
        }
        
        // Visit each statement 
        {
            Iterator stmtIt = iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
               
                if(((Integer) stmtToColor.get(s)).intValue() == WHITE)
                    visitStmt(s); 
            }
        }
        
        return order;
    }
    
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
    }

  /** Look for a path, in g, from def to use. 
   * This path has to lie inside an extended basic block 
   * (and this property implies uniqueness.) */
  /* This path does not include the to
     returns null if there is no such path */
  
  public List getExtendedBasicBlockPathBetween(Stmt from, Stmt to)
    {
        StmtGraph g = this;
        
      // if this holds, we're doomed to failure!!!
      if (g.getPredsOf(to).size() > 1)
        return null;

      // pathStack := list of succs lists
      // pathStackIndex := last visited index in pathStack
      LinkedList pathStack = new LinkedList();
      LinkedList pathStackIndex = new LinkedList();

      pathStack.add(from);
      pathStackIndex.add(new Integer(0));

      int psiMax = (g.getSuccsOf((Stmt)pathStack.get(0))).size();
      int level = 0;
      while (((Integer)pathStackIndex.get(0)).intValue() != psiMax)
        {
          int p = ((Integer)(pathStackIndex.get(level))).intValue();

          List succs = g.getSuccsOf((Stmt)(pathStack.get(level)));
          if (p >= succs.size())
            {
              // no more succs - backtrack to previous level.

              pathStack.remove(level);
              pathStackIndex.remove(level);

              level--;
              int q = ((Integer)pathStackIndex.get(level)).intValue();
              pathStackIndex.set(level, new Integer(q+1));
              continue;
            }

          Stmt betweenStmt = (Stmt)(succs.get(p));

          // we win!
          if (betweenStmt == to)
            {
              return pathStack;
            }

          // check preds of betweenStmt to see if we should visit its kids.
          if (g.getPredsOf(betweenStmt).size() > 1)
            {
              pathStackIndex.set(level, new Integer(p+1));
              continue;
            }

          // visit kids of betweenStmt.
          level++;
          pathStackIndex.add(new Integer(0));
          pathStack.add(betweenStmt);
        }
      return null;
    }  
     
}
























