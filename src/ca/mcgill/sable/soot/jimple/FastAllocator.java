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

 - Modified on February 2, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca). (*)
   Improved the interference graph builder.

 - Modified on January 20, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca). (*)
   Extracted the interference graph and local packer and put in this file for
   increased pluggability. 
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public class FastAllocator
{   
    public static void packLocals(JimpleBody body)
    {
        new FastAllocator(body);
    } 
    
    public FastAllocator(JimpleBody body)
    {
        StmtList stmtList = body.getStmtList();

        if(Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() + "] Packing locals...");

        if(Main.isProfilingOptimization)
            Main.graphTimer.start();

        // Jimple.printStmtListBody_debug(body, new java.io.PrintWriter(System.out));

        if(Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() + "](Packing locals)    Building stmt graph...");

        CompleteStmtGraph stmtGraph = new CompleteStmtGraph(stmtList);

        if(Main.isProfilingOptimization)
            Main.graphTimer.end();

        if(Main.isProfilingOptimization)
            Main.liveTimer.start();

            
        LiveLocals liveLocals;

        if(Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() + "](Packing locals)    Building live locals..");
        
        if(Main.usePackedLive)
            liveLocals = new SimpleLiveLocals(stmtGraph);
        else 
            liveLocals = new SparseLiveLocals(stmtGraph);

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

                if(Main.isVerbose)
                    System.out.println("[" + body.getMethod().getName() + "](Packing locals)    Packing type " +
                        type.toString() + "...");

                InterferenceGraph originalGraph;
                InterferenceGraph workingGraph;
                LinkedList localQueue;
                Map localToColor = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
                Set usedColors = new HashSet();

                // Build graphs
                    if(Main.isVerbose)
                        System.out.println("[" + body.getMethod().getName() + "](Packing locals)    " + 
                            "Building interference graph...");

                    originalGraph = new InterferenceGraph(body, type, liveLocals);
                    // workingGraph = new InterferenceGraph(body, type, liveLocals);
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

                                //workingGraph.removeLocal(l);
                            }
                        }
                        else {
                            // At the end of the IdentityStmt's
                            break;
                        }
                    }
                }

                // Construct queue
                    localQueue = new LinkedList();

                    // Put locals in queue in any order
                        localQueue.addAll(originalGraph.getLocals());
                    
                // Assign colors for each local in queue
                {
                    if(Main.isVerbose) 
                    {
                        System.out.println("[" + body.getMethod().getName() + "](Packing locals)    " +
                            "Coloring each local...");
                    }
                    
                    while(!localQueue.isEmpty())
                    {
                        Local local = (Local) localQueue.removeFirst();
                        Set workingColors;

                        if(localToColor.containsKey(local))
                        {
                            // Already assigned, probably a parameter
                            continue;
                        }
                        
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

                        /*
                        // Remove unavailable colors for this local
                        {
                            Iterator interferences = originalGraph.getInterferencesOf(local).iterator();

                            while(interferences.hasNext())
                            {
                                Local l = (Local) interferences.next();
                            
                                if(localToColor.containsKey(l));
                                    workingColors.remove(localToColor.get(l));
                            }
                        }
*/

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
    
    public class InterferenceGraph
    {
        Map localToLocals;
    
        private InterferenceGraph()
        {
        }
    
        public Set getLocals()
        {
            return localToLocals.keySet();
        }
        
        public InterferenceGraph(JimpleBody body, Type type, LiveLocals liveLocals)
        {
            StmtList stmtList = body.getStmtList();
    
            // Initialize localToLocals
            {
                localToLocals = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
    
                Iterator localIt = body.getLocals().iterator();
    
                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();
    
                    if(local.getType().equals(type))
                        localToLocals.put(local, new ArraySet());
                }
            }
    
            // Go through code, noting interferences
            {
                Iterator codeIt = stmtList.iterator();
    
                while(codeIt.hasNext())
                {
                    Stmt stmt = (Stmt) codeIt.next();
    
                    List liveLocalsAtStmt = liveLocals.getLiveLocalsAfter(stmt);
                    
                    // Note interferences if this statement is a definition
                    {
                        if(stmt instanceof DefinitionStmt)
                        {
                            DefinitionStmt def = (DefinitionStmt) stmt;
    
                            if(def.getLeftOp() instanceof Local)
                            {
                                Local defLocal = (Local) def.getLeftOp();
                  
                                if(defLocal.getType().equals(type))
                                {   
                                    Iterator localIt = liveLocalsAtStmt.iterator();
                                    
                                    while(localIt.hasNext())
                                    {
                                        Local otherLocal = (Local) localIt.next();
                                        
                                        if(otherLocal.getType().equals(type))
                                            setInterference(defLocal, otherLocal);
                                    }
                                }
                            }    
                        }
                    }                    
                }
            }
        }
    
        public boolean localsInterfere(Local l1, Local l2)
        {
            return ((Set) localToLocals.get(l1)).contains(l2);
        }
    
        public void setInterference(Local l1, Local l2)
        {
            ((Set) localToLocals.get(l1)).add(l2);
            ((Set) localToLocals.get(l2)).add(l1);
        }
    
        public boolean isEmpty()
        {
            return localToLocals.isEmpty();
        }
    
        /*
        public void removeLocal(Local local)
        {
            Object[] locals = ((Set) localToLocals.get(local)).toArray();
    
            // Handle all inverse edges
                for(int i = 0; i < locals.length; i++)
                    ((Set) localToLocals.get(locals[i])).remove(local);
    
            // Handle all outgoing edges
                localToLocals.remove(local);
        }
    
        public Local removeMostInterferingLocal()
        {
            if(isEmpty())
                throw new RuntimeException("graph is empty");
    
    
            Iterator it = localToLocals.entries().iterator();
            Local top = (Local) ((Map.Entry) it.next()).getKey();
    
            while(it.hasNext())
            {
                Local other = (Local) ((Map.Entry) it.next()).getKey();
    
                if(((Set) localToLocals.get(other)).size() > ((Set) localToLocals.get(top)).size())
                    top = other;
            }
    
    
            removeLocal(top);
    
            return top;
        }
    */
    /*
        Set getInterferencesOf(Local l)
        {
            return localToLocals.get(l);
        }
      */
        
        Local[] getInterferencesOf(Local l)
        {
            Object[] objects = ((Set) localToLocals.get(l)).toArray();
            Local[] locals = new Local[objects.length];
    
            for(int i = 0; i < objects.length; i++)
                locals[i] = (Local) objects[i];
    
            return locals; 
        }
    
        /*
        protected Object clone()
        {
            InterferenceGraph newGraph = InterferenceGraph();
    
            // Clone all the elements
            {
                Iterator it = localToLocals.entries().iterator();
    
    
                while(it.hasNext())
                {
                    Local local = (Local) ((Map.Entry) it.next()).getValue();
    
                    newGraph.put(local, localToLocals.get(local).clone());
                }
            }
        } */
    }
    
}
    
    
    
    
    
    
