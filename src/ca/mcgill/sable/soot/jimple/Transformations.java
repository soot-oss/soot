/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
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

class Transformations
{
    public static void assignTypesToLocals(JimpleBody listBody)
    {
        if(Main.isVerbose)
            System.out.println("[" + listBody.getMethod().getName() + "] assigning types to locals...");

        // Jimple.printStmtListBody(listBody, System.out, false);

        if(!Main.oldTyping)
        {
            TypeResolver.assignTypesToLocals(listBody);
            return;
        }

        StmtList stmtList = listBody.getStmtList();

        // Set all local types to unknown.
        {
            Iterator localIt = listBody.getLocals().iterator();

            while(localIt.hasNext())
                ((Local) localIt.next()).setType(UnknownType.v());
        }

        // Perform iterations on code, changing the types of the locals.
        {
            boolean hasChanged = true;
            SootClassManager cm = listBody.getMethod().getDeclaringClass().getManager();

            while(hasChanged)
            {
                hasChanged = false;

                Iterator stmtIt = stmtList.iterator();

                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();

                    if(s instanceof DefinitionStmt)
                    {
                        DefinitionStmt def = (DefinitionStmt) s;

                        if(def.getLeftOp() instanceof Local)
                        {
                            Local local = (Local) def.getLeftOp();
                            Type previousType = local.getType();

                            Type newType = (Type.toMachineType(def.getRightOp().getType()))
                                .merge(previousType, cm);

                            if(!previousType.equals(newType))
                                 hasChanged = true;

                            local.setType(newType);
                        }
                    }
                }
            }
        }

        // Set all unknown locals to java.lang.Object
        {
            Iterator localIt = listBody.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local local = (Local) localIt.next();

                if(local.getType().equals(UnknownType.v()))
                    local.setType(RefType.v("java.lang.Object"));
            }
        }
    }

    public static void splitLocals(JimpleBody listBody)
    {
        StmtList stmtList = listBody.getStmtList();

        if(Main.isVerbose)
            System.out.println("[" + listBody.getMethod().getName() + "] Splitting locals...");

        Map boxToSet = new HashMap(stmtList.size() * 2 + 1, 0.7f);

        // Go through the definitions, building the boxToSet 
        {
            List code = stmtList;

            if(Main.isProfilingOptimization)
                Main.graphTimer.start();

            CompleteStmtGraph graph = new CompleteStmtGraph(stmtList);

            if(Main.isProfilingOptimization)
                Main.graphTimer.end();

            if(Main.isProfilingOptimization)
                Main.defsTimer.start();

            LocalDefs localDefs = new SimpleLocalDefs(graph);

            if(Main.isProfilingOptimization)
                Main.defsTimer.end();

            if(Main.isProfilingOptimization)
                Main.usesTimer.start();

            LocalUses localUses = new SimpleLocalUses(graph, localDefs);

            if(Main.isProfilingOptimization)
                Main.usesTimer.end();

            Iterator codeIt = stmtList.iterator();

            while(codeIt.hasNext())
            {
                Stmt s = (Stmt) codeIt.next();

                if(!(s instanceof DefinitionStmt))
                    continue;

                DefinitionStmt def = (DefinitionStmt) s;

                if(def.getLeftOp() instanceof Local && !boxToSet.containsKey(def.getLeftOpBox()))
                {
                    Set visitedBoxes = new ArraySet(); // set of uses
                    Set visitedDefs = new ArraySet();

                    LinkedList defsToVisit = new LinkedList();
                    LinkedList boxesToVisit = new LinkedList();

                    Map boxToStmt = new HashMap(stmtList.size() * 2 + 1, 0.7f);
                    Set equivClass = new ArraySet();

                    defsToVisit.add(def);

                    while(!boxesToVisit.isEmpty() || !defsToVisit.isEmpty())
                    {
                        if(!defsToVisit.isEmpty())
                        {
                            DefinitionStmt d = (DefinitionStmt) defsToVisit.removeFirst();

                            equivClass.add(d.getLeftOpBox());
                            boxToSet.put(d.getLeftOpBox(), equivClass);

                            visitedDefs.add(d);

                            List uses = localUses.getUsesOf(d);
                            Iterator useIt = uses.iterator();

                            while(useIt.hasNext())
                            {
                                StmtValueBoxPair use = (StmtValueBoxPair) useIt.next();

                                if(!visitedBoxes.contains(use.valueBox) &&
                                    !boxesToVisit.contains(use.valueBox))
                                {
                                    boxesToVisit.addLast(use.valueBox);
                                    boxToStmt.put(use.valueBox, use.stmt);
                                }
                            }
                        }
                        else {
                            ValueBox box = (ValueBox) boxesToVisit.removeFirst();

                            equivClass.add(box);
                            boxToSet.put(box, equivClass);
                            visitedBoxes.add(box);

                            List defs = localDefs.getDefsOfAt((Local) box.getValue(),
                                (Stmt) boxToStmt.get(box));
                            Iterator defIt = defs.iterator();

                            while(defIt.hasNext())
                            {
                                DefinitionStmt d = (DefinitionStmt) defIt.next();

                                if(!visitedDefs.contains(d) && !defsToVisit.contains(d))
                                    defsToVisit.addLast(d);
                            }
                        }
                    }
                }
            }
        }

        // Assign locals appropriately.
        {
            Map localToUseCount = new HashMap(listBody.getLocalCount() * 2 + 1, 0.7f);
            Set visitedSets = new HashSet();
            Iterator it = boxToSet.values().iterator();

            while(it.hasNext())
            {
                Set equivClass = (Set) it.next();

                if(visitedSets.contains(equivClass))
                    continue;
                else
                    visitedSets.add(equivClass);

                ValueBox rep = (ValueBox) equivClass.iterator().next();
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

                    Local local = new Local(desiredLocal.getName() + "$" + useCount, desiredLocal.getType());

                    listBody.addLocal(local);

                    // Change all boxes to point to this new local
                    {
                        Iterator j = equivClass.iterator();

                        while(j.hasNext())
                        {
                            ValueBox box = (ValueBox) j.next();

                            box.setValue(local);
                        }
                    }
                }
            }
        }
    }

    public static void removeUnusedLocals(JimpleBody listBody)
    {
        StmtList stmtList = listBody.getStmtList();
        Set unusedLocals = new HashSet();

        // Set all locals as unused
            unusedLocals.addAll(listBody.getLocals());

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

                        if(value instanceof Local && unusedLocals.contains(value))
                            unusedLocals.remove(value);
                    }
                }

                // Remove all locals in useBoxes from unusedLocals
                {
                    Iterator boxIt = s.getUseBoxes().iterator();

                    while(boxIt.hasNext())
                    {
                        Value value = ((ValueBox) boxIt.next()).getValue();

                        if(value instanceof Local && unusedLocals.contains(value))
                            unusedLocals.remove(value);
                    }
                }
            }

        }

        // Remove all locals in unusedLocals
        {
            Iterator it = unusedLocals.iterator();

            while(it.hasNext())
            {
                Local local = (Local) it.next();

                listBody.removeLocal(local);
            }
        }
    }

    public static void packLocals(JimpleBody body)
    {
        StmtList stmtList = body.getStmtList();

        if(Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() + "] Packing locals...");

        if(Main.isProfilingOptimization)
            Main.graphTimer.start();

        // Jimple.printStmtListBody_debug(body, new java.io.PrintWriter(System.out));

        CompleteStmtGraph stmtGraph = new CompleteStmtGraph(stmtList);

        if(Main.isProfilingOptimization)
            Main.graphTimer.end();

        if(Main.isProfilingOptimization)
            Main.liveTimer.start();

        LiveLocals liveLocals = new SimpleLiveLocals(stmtGraph);

        if(Main.isProfilingOptimization)
            Main.liveTimer.end();

        Set types;

        // Construct different types available
        {
            Iterator localIt = body.getLocals().iterator();

            types = new ArraySet();

            while(localIt.hasNext())
                types.add(((Local) localIt.next()).getType());
        }

        // Perform one packing per type
        {
            Iterator typeIt = types.iterator();

            while(typeIt.hasNext())
            {
                Type type = (Type) typeIt.next();

                InterferenceGraph originalGraph;
                InterferenceGraph workingGraph;
                LinkedList localQueue;
                Map localToColor = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
                Set usedColors = new HashSet();

                // Build graphs
                    originalGraph = new InterferenceGraph(body, type, liveLocals);
                    workingGraph = new InterferenceGraph(body, type, liveLocals);
                        // should really be a clone

                // Color parameter locals first
                {
                    Iterator codeIt = stmtList.iterator();

                    while(codeIt.hasNext())
                    {
                        Stmt s = (Stmt) codeIt.next();

                        if(s instanceof IdentityStmt &&
                            ((IdentityStmt) s).getLeftOp() instanceof Local)
                        {

                            Local l = (Local) ((IdentityStmt) s).getLeftOp();

                            if(l.getType().equals(type))
                            {
                                Local color = new Local("", type);

                                localToColor.put(l, color);
                                usedColors.add(color);

                                workingGraph.removeLocal(l);
                            }
                        }
                    }
                }

                // Construct queue
                    localQueue = new LinkedList();

                    // Put in queue in decreasing interference order
                    while(!workingGraph.isEmpty())
                        localQueue.addFirst(workingGraph.removeMostInterferingLocal());

                // Assign colors for each local in queue
                    while(!localQueue.isEmpty())
                    {
                        Local local = (Local) localQueue.removeFirst();
                        Set workingColors;

                        // Clone currentColors
                        {
                            workingColors = new HashSet();
                            Iterator colorIt = usedColors.iterator();

                            while(colorIt.hasNext())
                                workingColors.add(colorIt.next());
                        }

                        // Remove unavailable colors for this local
                        {
                            Local[] interferences = originalGraph.getInterferencesOf(local);

                            for(int i = 0; i < interferences.length; i++)
                            {
                                if(localToColor.containsKey(interferences[i]))
                                    workingColors.remove(localToColor.get(interferences[i]));
                            }
                        }

                        // Assign a color
                        {
                            Local assignedColor;

                            if(workingColors.isEmpty())
                            {
                                assignedColor = new Local("", type);
                                usedColors.add(assignedColor);
                            }
                            else
                                assignedColor = (Local) workingColors.iterator().next();

                            localToColor.put(local, assignedColor);
                        }
                    }

                // Perform changes on method
                {
                    Set originalLocals = new HashSet();

                    // Remove all locals with this type.
                    {
                        Iterator localIt = body.getLocals().iterator();

                        while(localIt.hasNext())
                        {
                            Local l = (Local) localIt.next();

                            if(l.getType().equals(type))
                            {
                                body.removeLocal(l);
                                originalLocals.add(l);
                            }
                        }
                    }

                    // Give names to the new locals
                    {
                        Iterator itr = originalLocals.iterator();

                        while(itr.hasNext())
                        {
                            Local original = (Local) itr.next();
                            Local color = (Local) localToColor.get(original);

                            if(color.getName().equals(""))
                                color.setName(original.getName());
                        }
                    }

                    // Add new locals to the method
                    {
                        Iterator itr = usedColors.iterator();

                        while(itr.hasNext())
                            body.addLocal((Local) itr.next());
                    }

                    // Go through all valueBoxes of this method and perform changes
                    {
                        Iterator codeIt = stmtList.iterator();

                        while(codeIt.hasNext())
                        {
                            Stmt s = (Stmt) codeIt.next();

                            Iterator boxIt = s.getUseAndDefBoxes().iterator();

                            while(boxIt.hasNext())
                            {
                                ValueBox box = (ValueBox) boxIt.next();

                                if(box.getValue() instanceof Local)
                                {
                                    Local l = (Local) box.getValue();

                                    if(l.getType().equals(type))
                                        box.setValue((Local) localToColor.get(l));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static void cleanupCode(JimpleBody stmtBody)
    {
        StmtList stmtList = stmtBody.getStmtList();
        int numPropagations = 0;
        int numIterations = 0;
        int numEliminations = 0;

        for(;;)
        {
            boolean hadPropagation = false;

            numIterations++;

            if(Main.isVerbose)
                System.out.println("[" + stmtList.getBody().getMethod().getName() + "] Cleanup Iteration " + numIterations);

            //System.out.println("Before optimization:");
            //Jimple.printStmtListBody_debug(stmtList.getBody(), new java.io.PrintWriter(System.out, true));

            if(Main.isVerbose)
             System.out.println("[" + stmtList.getBody().getMethod().getName() + "] Constructing StmtGraph...");

            if(Main.isProfilingOptimization)
                Main.graphTimer.start();

            CompleteStmtGraph graph = new CompleteStmtGraph(stmtList);

            if(Main.isProfilingOptimization)
                Main.graphTimer.end();

            if(Main.isVerbose)
                System.out.println("[" + stmtList.getBody().getMethod().getName() + "] Constructing LocalDefs...");


            if(Main.isProfilingOptimization)
                Main.defsTimer.start();

            LocalDefs localDefs = new SimpleLocalDefs(graph);

            if(Main.isProfilingOptimization)
                Main.defsTimer.end();

            if(Main.isVerbose)
                System.out.println("[" + stmtList.getBody().getMethod().getName() + "] Constructing LocalUses...");

            if(Main.isProfilingOptimization)
                Main.usesTimer.start();

            LocalUses localUses = new SimpleLocalUses(graph, localDefs);

            if(Main.isProfilingOptimization)
                Main.usesTimer.end();

            if(Main.isVerbose)
                System.out.println("[" + stmtList.getBody().getMethod().getName() + "] Constructing LocalCopies...");

            if(Main.isProfilingOptimization)
                Main.copiesTimer.start();

            LocalCopies localCopies = new SimpleLocalCopies(graph);

            if(Main.isProfilingOptimization)
                Main.copiesTimer.end();

            if(Main.isProfilingOptimization)
                Main.cleanupAlgorithmTimer.start();

            // Perform a dead code elimination pass.
            {
                Iterator stmtIt = stmtList.iterator();

                while(stmtIt.hasNext())
                {
                    Stmt stmt = (Stmt) stmtIt.next();

                    if(stmt instanceof NopStmt)
                    {
                        stmtIt.remove();
                        continue;
                    }

                    if(!(stmt instanceof DefinitionStmt))
                        continue;

                    DefinitionStmt def = (DefinitionStmt) stmt;

                    if(def.getLeftOp() instanceof Local && localUses.getUsesOf(def).size() == 0 &&
                        !(def instanceof IdentityStmt) && !(def.getRightOp() instanceof InvokeExpr))
                    {
                        // Not ever used and does not have a side effect.

                        stmtIt.remove();
                        numEliminations++;
                    }
                    else if(def.getLeftOp() instanceof Local && def.getRightOp() instanceof Local &&
                        def.getLeftOp() == def.getRightOp())
                    {
                        // This is an assignment of the form a = a, which is useless.

                        stmtIt.remove();
                        numEliminations++;
                    }
                }
            }

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
                                        stmt))
                                    {
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

            if(!hadPropagation)
                break;

            if(Main.isProfilingOptimization)
                Main.cleanupAlgorithmTimer.end();
        }

        //System.out.println("That's all folks:");
        //Jimple.printStmtListBody_debug(stmtList.getBody(), new java.io.PrintWriter(System.out, true));

    }

    public static void renameLocals(JimpleBody body)
    {
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

                if(l.getType().equals(IntType.v()))
                    l.setName("i" + intCount++);
                else if(l.getType().equals(LongType.v()))
                    l.setName("l" + longCount++);
                else if(l.getType().equals(DoubleType.v()))
                    l.setName("d" + doubleCount++);
                else if(l.getType().equals(FloatType.v()))
                    l.setName("f" + floatCount++);
                else if(l.getType().equals(StmtAddressType.v()))
                    l.setName("a" + addressCount++);
                else if(l.getType().equals(ErroneousType.v()) ||
                    l.getType().equals(UnknownType.v()))
                {
                    l.setName("e" + errorCount++);
                }
                else if(l.getType().equals(NullType.v()))
                    l.setName("n" + nullCount++);
                else
                    l.setName("r" + objectCount++);
            }
        }
    }

}
