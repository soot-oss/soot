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

 - Modified on March 13, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Split off from Transformations.java
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public class LocalSplitter
{

    public static void splitLocals(JimpleBody listBody)
    {
        StmtList stmtList = listBody.getStmtList();
        List webs = new ArrayList();
        
        if(Main.isVerbose)
            System.out.println("[" + listBody.getMethod().getName() + "] Splitting locals...");

        Map boxToSet = new HashMap(stmtList.size() * 2 + 1, 0.7f);

        if(Main.isProfilingOptimization)
                Main.splitPhase1Timer.start();

        // Go through the definitions, building the webs
        {
            List code = stmtList;

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
            
            if(Main.isProfilingOptimization)
                Main.splitPhase1Timer.end();
    
            if(Main.isProfilingOptimization)
                Main.splitPhase2Timer.start();

            Set markedBoxes = new HashSet();
            Map boxToStmt = new HashMap(stmtList.size() * 2 + 1, 0.7f);
            
            Iterator codeIt = stmtList.iterator();

            while(codeIt.hasNext())
            {
                Stmt s = (Stmt) codeIt.next();

                if(!(s instanceof DefinitionStmt))
                    continue;

                DefinitionStmt def = (DefinitionStmt) s;

                if(def.getLeftOp() instanceof Local && !markedBoxes.contains(def.getLeftOpBox()))
                {
                    LinkedList defsToVisit = new LinkedList();
                    LinkedList boxesToVisit = new LinkedList();

                    List web = new ArrayList();
                    webs.add(web);
                                        
                    defsToVisit.add(def);
                    markedBoxes.add(def.getLeftOpBox());
                    
                    while(!boxesToVisit.isEmpty() || !defsToVisit.isEmpty())
                    {
                        if(!defsToVisit.isEmpty())
                        {
                            DefinitionStmt d = (DefinitionStmt) defsToVisit.removeFirst();

                            web.add(d.getLeftOpBox());

                            // Add all the uses of this definition to the queue
                            {
                                List uses = localUses.getUsesOf(d);
                                Iterator useIt = uses.iterator();
    
                                while(useIt.hasNext())
                                {
                                    StmtValueBoxPair use = (StmtValueBoxPair) useIt.next();
    
                                    if(!markedBoxes.contains(use.valueBox))
                                    {
                                        markedBoxes.add(use.valueBox);
                                        boxesToVisit.addLast(use.valueBox);
                                        boxToStmt.put(use.valueBox, use.stmt);
                                    }
                                }
                            }
                        }
                        else {
                            ValueBox box = (ValueBox) boxesToVisit.removeFirst();

                            web.add(box);

                            // Add all the definitions of this use to the queue.
                            {               
                                List defs = localDefs.getDefsOfAt((Local) box.getValue(),
                                    (Stmt) boxToStmt.get(box));
                                Iterator defIt = defs.iterator();
    
                                while(defIt.hasNext())
                                {
                                    DefinitionStmt d = (DefinitionStmt) defIt.next();
    
                                    if(!markedBoxes.contains(d.getLeftOpBox()))
                                    {
                                        markedBoxes.add(d.getLeftOpBox());
                                        defsToVisit.addLast(d);
                                    }    
                                }
                            }
                        }
                    }
                }
            }
        }

        // Assign locals appropriately.
        {
            Map localToUseCount = new HashMap(listBody.getLocalCount() * 2 + 1, 0.7f);
            Iterator webIt = webs.iterator();

            while(webIt.hasNext())
            {
                List web = (List) webIt.next();

                ValueBox rep = (ValueBox) web.get(0);
                Local desiredLocal = (Local) rep.getValue();

                if(!localToUseCount.containsKey(desiredLocal))
                {
                    // claim this local for this set

                    localToUseCount.put(desiredLocal, new Integer(1));
                }
                else {
                    // generate a new local

                    int useCount = ((Integer) localToUseCount.get(desiredLocal)).intValue() + 1;
                    localToUseCount.put(desiredLocal, new Integer(useCount));
        
                    Local local = (Local) desiredLocal.clone();
                    local.setName(desiredLocal.getName() + "$" + useCount);
                    
                    listBody.addLocal(local);

                    // Change all boxes to point to this new local
                    {
                        Iterator j = web.iterator();

                        while(j.hasNext())
                        {
                            ValueBox box = (ValueBox) j.next();

                            box.setValue(local);
                        }
                    }
                }
            }
        }
        
        if(Main.isProfilingOptimization)
            Main.splitPhase2Timer.end();

    }   
}
