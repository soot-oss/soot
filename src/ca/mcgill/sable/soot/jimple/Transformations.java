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
import ca.mcgill.sable.util.*;
import java.util.*;

public class Transformations
{
    public static void assignTypesToLocals(JimpleBody listBody)
    {
        TypeResolver.assignTypesToLocals(listBody);
        
    }

    public static void removeUnusedLocals(StmtBody listBody)
    {
        StmtList stmtList = listBody.getStmtList();
        Set usedLocals = new HashSet();

        // Traverse statements noting all the uses
        {
            Iterator stmtIt = stmtList.iterator();

            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();

                // Remove all locals in defBoxes from unusedLocals
                {
                    Iterator boxIt = s.getDefBoxes().iterator();

                    while(boxIt.hasNext())
                    {
                        Value value = ((ValueBox) boxIt.next()).getValue();

                        if(value instanceof Local && !usedLocals.contains(value))
                            usedLocals.add(value);
                    }
                }

                // Remove all locals in useBoxes from unusedLocals
                {
                    Iterator boxIt = s.getUseBoxes().iterator();

                    while(boxIt.hasNext())
                    {
                        Value value = ((ValueBox) boxIt.next()).getValue();

                        if(value instanceof Local && !usedLocals.contains(value))
                            usedLocals.add(value);
                    }
                }
            }

        }

        // Remove all locals that are unused.
        {
            Iterator it = listBody.getLocals().iterator();
            
            while(it.hasNext())
            {
                Local local = (Local) it.next();

                if(!usedLocals.contains(local))
                    it.remove();
            }
        }
    }

    /**
        Cleans up the code of the method by performing copy/constant propagation and dead code elimination.
        
        Right now it must only be called on JimpleBody's (as opposed to GrimpBody's) because 
        it checks for the different forms on the rhs such as fieldref, etc to determine if a statement
        has a side effect.  (FieldRef can throw a null pointer exception)
        
        A better way to handle this would be to have a method which returns whether the statement
        has a side effect.
     */
     
    public static void cleanupCode(JimpleBody stmtBody)
    {
        ConstantAndCopyPropagator.propagateConstantsAndCopies(stmtBody);
        DeadCodeEliminator.eliminateDeadCode(stmtBody);
        
        //stmtBody.printDebugTo(new java.io.PrintWriter(System.out, true));
    }

    public static void standardizeStackLocalNames(StmtBody body)
    {
        boolean saveStackName = true;
        StmtList stmtList = body.getStmtList();

        // Change the names to the standard forms now.
        {
            int objectCount = 0;
            int intCount = 0;
            int longCount = 0;
            int floatCount = 0;
            int doubleCount = 0;
            int addressCount = 0;
            int errorCount = 0;
            int nullCount = 0;

            Iterator localIt = body.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();
                String prefix = "";
                
                if(l.getName().startsWith("$"))
                    prefix = "$";
                else
                    continue;
                    
                if(l.getType().equals(IntType.v()))
                    l.setName(prefix + "i" + intCount++);
                else if(l.getType().equals(LongType.v()))
                    l.setName(prefix + "l" + longCount++);
                else if(l.getType().equals(DoubleType.v()))
                    l.setName(prefix + "d" + doubleCount++);
                else if(l.getType().equals(FloatType.v()))
                    l.setName(prefix + "f" + floatCount++);
                else if(l.getType().equals(StmtAddressType.v()))
                    l.setName(prefix + "a" + addressCount++);
                else if(l.getType().equals(ErroneousType.v()) ||
                    l.getType().equals(UnknownType.v()))
                {
                    l.setName(prefix + "e" + errorCount++);
                }
                else if(l.getType().equals(NullType.v()))
                    l.setName(prefix + "n" + nullCount++);
                else
                    l.setName(prefix + "r" + objectCount++);
            }
        }
    }
    
    public static void standardizeLocalNames(StmtBody body)
    {
        boolean saveStackName = true;
        StmtList stmtList = body.getStmtList();

        // Change the names to the standard forms now.
        {
            int objectCount = 0;
            int intCount = 0;
            int longCount = 0;
            int floatCount = 0;
            int doubleCount = 0;
            int addressCount = 0;
            int errorCount = 0;
            int nullCount = 0;

            Iterator localIt = body.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();
                String prefix = "";
                
                if(l.getName().startsWith("$"))
                    prefix = "$";
                
                if(l.getType().equals(IntType.v()))
                    l.setName(prefix + "i" + intCount++);
                else if(l.getType().equals(LongType.v()))
                    l.setName(prefix + "l" + longCount++);
                else if(l.getType().equals(DoubleType.v()))
                    l.setName(prefix + "d" + doubleCount++);
                else if(l.getType().equals(FloatType.v()))
                    l.setName(prefix + "f" + floatCount++);
                else if(l.getType().equals(StmtAddressType.v()))
                    l.setName(prefix + "a" + addressCount++);
                else if(l.getType().equals(ErroneousType.v()) ||
                    l.getType().equals(UnknownType.v()))
                {
                    l.setName(prefix + "e" + errorCount++);
                }
                else if(l.getType().equals(NullType.v()))
                    l.setName(prefix + "n" + nullCount++);
                else
                    l.setName(prefix + "r" + objectCount++);
            }
        }
    }        
}



