/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1999 Raja Vallee-Rai (kor@sable.mcgill.ca)          *
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

 - Modified on March 14, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   First release.
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public class ConstantAndCopyPropagator
{
    /** Propagates constants and copies using a cascaded transformation technique. */
    
    public static void propagateConstantsAndCopies(StmtBody stmtBody)
    {
        if(Main.isVerbose)
            System.out.println("[" + stmtBody.getMethod().getName() +
                "] Propagating constants and copies ...");

        if(Main.isProfilingOptimization)
            Main.propagatorTimer.start();                
                
        StmtList stmtList = stmtBody.getStmtList();
        int numPropagations = 0;
        int numIterations = 0;
        int numEliminations = 0;
        
        for(;;)
        {
            boolean hadPropagation = false;
            boolean hadElimination = false;
            
            ((JimpleBody) stmtBody).printDebugTo(new java.io.PrintWriter(System.out, true));
            
            numIterations++;

            if(Main.isVerbose)
                System.out.println("[" + stmtList.getBody().getMethod().getName() + 
                    "] Propagation iteration " + numIterations);

            //System.out.println("Before optimization:");
            CompleteStmtGraph graph = new CompleteStmtGraph(stmtList);

            LocalDefs localDefs;
            
            if(Main.usePackedDefs) 
            {
                localDefs = new SimpleLocalDefs(graph);
            }
            else {
                LiveLocals liveLocals;
            
                if(Main.usePackedLive) 
                    liveLocals = new SimpleLiveLocals(graph);
                else
                    liveLocals = new SparseLiveLocals(graph);    

                localDefs = new SparseLocalDefs(graph, liveLocals);                
            }           


            LocalUses localUses = new SimpleLocalUses(graph, localDefs);
            LocalCopies localCopies = new SimpleLocalCopies(graph);

            if(Main.isProfilingOptimization)
                Main.cleanupAlgorithmTimer.start();

            // Perform a constant/local propagation pass.
            {
                Iterator stmtIt = stmtList.iterator();

                while(stmtIt.hasNext())
                {
                    Stmt stmt = (Stmt) stmtIt.next();
                    Iterator useBoxIt = stmt.getUseBoxes().iterator();

                    while(useBoxIt.hasNext())
                    {
                        ValueBox useBox = (ValueBox) useBoxIt.next();

                        if(useBox.getValue() instanceof Local)
                        {
                            Local l = (Local) useBox.getValue();

                            List defsOfUse = localDefs.getDefsOfAt(l, stmt);

                            if(defsOfUse.size() == 1)
                            {
                                DefinitionStmt def = (DefinitionStmt) defsOfUse.get(0);

                                if(def.getRightOp() instanceof Constant)
                                {
                                    if(useBox.canContainValue(def.getRightOp()))
                                    {
                                        // Check to see if this box can actually contain
                                        // a constant.  (bases can't)

                                        useBox.setValue(def.getRightOp());
                                        numPropagations++;
                                        hadPropagation = true;
                                    }
                                }
                                else if(def.getRightOp() instanceof Local)
                                {
                                    Local m = (Local) def.getRightOp();

                                    if(localCopies.isLocalCopyOfBefore((Local) l, (Local) m,
                                        stmt) && l != m)
                                    {
                                        // Check to see if the local copy is available
                                        // and that we're not propagated l == m
                                        
                                        useBox.setValue(m);
                                        numPropagations++;
                                        hadPropagation = true;
                                    }
                                }
                            }
                        }

                    }
                }
            }

            //JimpleBody.printStmtList_debug((StmtBody) stmtBody, new java.io.PrintWriter(System.out, true));

            if(Main.isProfilingOptimization)
                Main.cleanupAlgorithmTimer.end();
                
            if(!hadPropagation && !hadElimination)
                break;
        }
        
        if(Main.isProfilingOptimization)
            Main.propagatorTimer.end();
    
    }
    
}






























