/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
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

 - Modified on June 2, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*) 
   Fixed a problem with moving invoke's past field reads.
   
 - Modified on May 24, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*) 
   Added support for zone checking.
   
 - Modified on April 20, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*) 
   Split off the aggregate method into its own classfile.
   Split off packLocals() method into the class LocalPacker Transformations.java
   Added a standardizeStackLocalNames().
   
   
 - Modified on March 25, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   1. Changed the aggregator to proceed in pseudo topological order.  Failure
   to do this introduces possible bugs.
   2. Added some checks to the aggregator when aggregating array refs.  Was previously
   doing some unsafe things.
   
 - Modified on March 23, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Modified removeUnusedLocals to not use an iterator for HashSet (which is non-deterministic).
 
 - Modified on March 17, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Corrected some aggregation bugs.
   Moved the local splitting code into its own file.
   Eliminated the multi-pass dead code elimination.  Calls a
   cascaded dead code eliminator.

 - Modified on March 5, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Changed aggregate to be iterative.  No longer returns a value.
   
 - Modified on March 3, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Fixed a bug with dead-code elimination concerning field/array references.  
   (they can throw null-pointer exceptions so they should not be eliminated) 
   Dead-code elimination is now done iteratively until nothing changes. (not sure why
   it wasn't done before) 
   
 - Modified on March 3, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Improved the aggregator to move field accesses past some types of writes.
      
 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on January 25, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca). (*)
   Made transformations class public.
    
 - Modified on January 20, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca). (*)
   Moved packLocals method to ChaitinAllocator.java
    
 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on July 29,1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Changed assignTypesToLocals. It now uses Etienne's type inference
   algorithm.
   Changed renameLocals. Gives a different name to address and error
   variables.

 - Modified on 23-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Changed Hashtable to HashMap.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.toolkit.scalar.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class Aggregator
{
   public static int nodeCount = 0;
   public static int aggrCount = 0;

   /**
      Only aggregate stack variables (those which start with $)
    */
    
    public static void aggregateStackVariables(StmtBody body)
    {
        aggregate_facade(body, true);
    } 
  
    /** Traverse the statements in the given body, looking for
      *  aggregation possibilities; that is, given a def d and a use u,
      *  d has no other uses, u has no other defs, collapse d and u. */
    
    public static void aggregate(StmtBody body)
    {
        aggregate_facade(body, false);
    } 
    
   
    private static void aggregate_facade(StmtBody body, boolean isConservative)
    {
        int aggregateCount = 1;

        if(Main.isProfilingOptimization)
            Main.aggregationTimer.start();
         boolean changed = false;

        Map boxToZone = new HashMap(body.getUnits().size() * 2 + 1, 0.7f);

        // Determine the zone of every box
        {
            Zonation zonation = new Zonation(body);
            
            Iterator unitIt = body.getUnits().iterator();
            
            while(unitIt.hasNext())
            {
                Unit u = (Unit) unitIt.next();
                Zone zone = (Zone) zonation.getZoneOf(u);
                
                
                Iterator boxIt = u.getUseAndDefBoxes().iterator();
                           
                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();                    
                    boxToZone.put(box, zone);
                }   
            }
        }        
        
                     
        do {
            if(Main.isVerbose)
                System.out.println("[" + body.getMethod().getName() + "] Aggregating iteration " + aggregateCount + "...");
        
            // body.printTo(new java.io.PrintWriter(System.out, true));
            
            changed = internalAggregate(body, boxToZone, isConservative);
            
            aggregateCount++;
        } while(changed);
        
        if(Main.isProfilingOptimization)
            Main.aggregationTimer.end();
            
    }
  
  private static boolean internalAggregate(StmtBody body, Map boxToZone, boolean isConservative)
    {
      Iterator stmtIt;
      UnitLocalUses localUses;
      UnitLocalDefs localDefs;
      CompleteUnitGraph graph;
      boolean hadAggregation = false;
      Chain units = body.getUnits();
      
      graph = new CompleteUnitGraph(body);
      localDefs = new SimpleUnitLocalDefs(graph);
      localUses = new SimpleUnitLocalUses(graph, localDefs);
          
      stmtIt = graph.pseudoTopologicalOrderIterator();
      
      while (stmtIt.hasNext())
        {
          Stmt s = (Stmt)(stmtIt.next());
              
          if (!(s instanceof AssignStmt))
            continue;
          
          Value lhs = ((AssignStmt)s).getLeftOp();
          if (!(lhs instanceof Local))
            continue;
    
          if(isConservative && !((Local) lhs).getName().startsWith("$"))
            continue;
            
          List lu = localUses.getUsesOf((AssignStmt)s);
          if (lu.size() != 1)
            continue;
            
          UnitValueBoxPair usepair = (UnitValueBoxPair)lu.get(0);
          Unit use = usepair.unit;
          ValueBox useBox = usepair.valueBox;
              
          List ld = localDefs.getDefsOfAt((Local)lhs, use);
          if (ld.size() != 1)
            continue;
   
          // Check to make sure aggregation pair in the same zone
            if(boxToZone.get(((AssignStmt) s).getRightOpBox()) != boxToZone.get(usepair.valueBox))
            {
                continue;
            }  
             
          /* we need to check the path between def and use */
          /* to see if there are any intervening re-defs of RHS */
          /* in fact, we should check that this path is unique. */
          /* if the RHS uses only locals, then we know what
             to do; if RHS has a method invocation f(a, b,
             c) or field access, we must ban field writes, other method
             calls and (as usual) writes to a, b, c. */
          
          boolean cantAggr = false;
          boolean propagatingInvokeExpr = false;
          boolean propagatingFieldRef = false;
          boolean propagatingArrayRef = false;
          FieldRef fieldRef = null;
      
          Value rhs = ((AssignStmt)s).getRightOp();
          LinkedList localsUsed = new LinkedList();
          for (Iterator useIt = (s.getUseBoxes()).iterator();
               useIt.hasNext(); )
            {
              Value v = ((ValueBox)(useIt.next())).getValue();
                if (v instanceof Local)
                    localsUsed.add(v);
                else if (v instanceof InvokeExpr)
                    propagatingInvokeExpr = true;
                else if(v instanceof ArrayRef)
                    propagatingArrayRef = true;
                else if(v instanceof FieldRef)
                {
                    propagatingFieldRef = true;
                    fieldRef = (FieldRef) v;
                }
            }
          
          // look for a path from s to use in graph.
          // only look in an extended basic block, though.

          List path = graph.getExtendedBasicBlockPathBetween(s, use);
      
          if (path == null)
            continue;

          Iterator pathIt = path.iterator();

          // skip s.
          if (pathIt.hasNext())
            pathIt.next();

          while (pathIt.hasNext() && !cantAggr)
          {
              Stmt between = (Stmt)(pathIt.next());
          
              if(between != use)    
              {
                // Check for killing definitions
                
                for (Iterator it = between.getDefBoxes().iterator();
                       it.hasNext(); )
                  {
                      Value v = ((ValueBox)(it.next())).getValue();
                      if (localsUsed.contains(v))
                      { 
                            cantAggr = true; 
                            break; 
                      }
                      
                      if (propagatingInvokeExpr || propagatingFieldRef || propagatingArrayRef)
                      {
                          if (v instanceof FieldRef)
                          {
                              if(propagatingInvokeExpr)
                              {
                                  cantAggr = true; 
                                  break;
                              }
                              else if(propagatingFieldRef)
                              {
                                  // Can't aggregate a field access if passing a definition of a field 
                                  // with the same name, because they might be aliased
                            
                                  if(((FieldRef) v).getField() == fieldRef.getField())
                                  {
                                      cantAggr = true;
                                      break;
                                  } 
                              } 
                           }
                           else if(v instanceof ArrayRef)
                           {
                                if(propagatingInvokeExpr)
                                {   
                                    // Cannot aggregate an invoke expr past an array write
                                    cantAggr = true;
                                    break;
                                }
                                else if(propagatingArrayRef)
                                {
                                    // cannot aggregate an array read past a write
                                    // this is somewhat conservative
                                    // (if types differ they may not be aliased)
                                    
                                    cantAggr = true;
                                    break;
                                }
                           }
                      }
                  }
                  
                  // Make sure not propagating past a {enter,exit}Monitor
                    if(propagatingInvokeExpr && between instanceof MonitorStmt)
                        cantAggr = true;
              }  
                            
              // Check for intervening side effects due to method calls
                if(propagatingInvokeExpr || propagatingFieldRef || propagatingArrayRef)
                    {
                      for (Iterator useIt = (between.getUseBoxes()).iterator();
                           useIt.hasNext(); )
                        {
                          ValueBox box = (ValueBox) useIt.next();
                          
                          if(between == use && box == useBox)
                          {
                                // Reached use point, stop looking for
                                // side effects
                                break;
                          }
                          
                          Value v = box.getValue();
                          
                            if (v instanceof InvokeExpr || 
                                (propagatingInvokeExpr && (v instanceof FieldRef || v instanceof ArrayRef)))
                            {
                                cantAggr = true;
                                break;
                            }
                            
                        }
                    }
            }

          // we give up: can't aggregate.
          if (cantAggr)
          {
            continue;
          }
          /* assuming that the d-u chains are correct, */
          /* we need not check the actual contents of ld */
          
          Value aggregatee = ((AssignStmt)s).getRightOp();
          
          if (usepair.valueBox.canContainValue(aggregatee))
            {
              usepair.valueBox.setValue(aggregatee);
              units.remove(s);
              hadAggregation = true;
              aggrCount++;
            }
          else
            {/*
            if(Main.isVerbose)
            {
                System.out.println("[debug] failed aggregation");
                  System.out.println("[debug] tried to put "+aggregatee+
                                 " into "+usepair.stmt + 
                                 ": in particular, "+usepair.valueBox);
                  System.out.println("[debug] aggregatee instanceof Expr: "
                                 +(aggregatee instanceof Expr));
            }*/
            }
        }
      return hadAggregation;
    }
        
}

