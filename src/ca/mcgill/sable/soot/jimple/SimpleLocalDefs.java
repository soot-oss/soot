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

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Renamed the uses of Hashtable to HashMap.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

// FSet version

public class SimpleLocalDefs implements LocalDefs
{
    Map localStmtPairToDefs;

    public SimpleLocalDefs(CompleteStmtGraph g)
    {
        LocalDefsFlowAnalysis analysis = new LocalDefsFlowAnalysis(g);
        if(Main.isProfilingOptimization)
                Main.defsPostTimer.start();

        // Build localStmtPairToDefs map
        {
            Iterator stmtIt = g.iterator();

            localStmtPairToDefs = new HashMap(g.size() * 2 + 1, 0.7f);

            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();

                Iterator boxIt = s.getUseBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                    {
                        Local l = (Local) box.getValue();
                        LocalStmtPair pair = new LocalStmtPair(l, s);

                        if(!localStmtPairToDefs.containsKey(pair))
                        {
                            IntPair intPair = (IntPair) analysis.localToIntPair.get(l);
                            BoundedFlowSet value = (BoundedFlowSet) analysis.getFlowBeforeStmt(s);

                            List localDefs = value.toList(intPair.op1, intPair.op2);

                            localStmtPairToDefs.put(pair, Collections.unmodifiableList(localDefs));
                        }
                    }
                }
            }
        }

    }

    public List getDefsOfAt(Local l, Stmt s)
    {
        LocalStmtPair pair = new LocalStmtPair(l, s);

        return (List) localStmtPairToDefs.get(pair);
    }

    /*
    public List getDefsOfBefore(Local l, Stmt s)
    {
        IntPair pair = (IntPair) analysis.localToIntPair.get(l);
        FSet value = (FSet) analysis.getValueBeforeStmt(s);

        List localDefs = value.toList(pair.op1, pair.op2);

        return localDefs;
    }*/

/*
            Object[] elements = ((FSet) analysis.getValueBeforeStmt(s)).toArray();
            List listOfDefs = new LinkedList();

            // Extract those defs which correspond to this local
            {
                for(int i = 0; i < elements.length; i++)
                {
                    DefinitionStmt d = (DefinitionStmt) elements[i];

                    if(d.getLeftOp() == l)
                        listOfDefs.add(d);
                }
            }

            // Convert the array so that it's of an appropriate form
            {
                Object[] objects = listOfDefs.toArray();
                DefinitionStmt[] defs = new DefinitionStmt[objects.length];

                for(int i = 0; i < defs.length; i++)
                    defs[i] = (DefinitionStmt) objects[i];

                return defs;
            }

        }
    }
*/

    /*
    public DefinitionStmt[] getDefsOfAfter(Local l, Stmt s)
    {
       Object[] elements = ((FSet) analysis.getValueAfterStmt(s)).toArray();
           List listOfDefs = new LinkedList();

        // Extract those defs which correspond to this local
         {
             for(int i = 0; i < elements.length; i++)
            {
                   DefinitionStmt d = (DefinitionStmt) elements[i];

                if(d.getLeftOp() == l)
                    listOfDefs.add(d);
            }
           }

           // Convert the array so that it's of an appropriate form
           {
                Object[] objects = listOfDefs.toArray();
                DefinitionStmt[] defs = new DefinitionStmt[objects.length];

                for(int i = 0; i < defs.length; i++)
                    defs[i] = (DefinitionStmt) objects[i];

                return defs;
            }
    }

    public DefinitionStmt[] getDefsBefore(Stmt s)
    {
        Object[] elements = ((FSet) analysis.getValueBeforeStmt(s)).toArray();
        DefinitionStmt[] defs = new DefinitionStmt[elements.length];

        for(int i = 0; i < elements.length; i++)
            defs[i] = (DefinitionStmt) elements[i];

        return defs;
    }

    public DefinitionStmt[] getDefsAfter(Stmt s)
    {
        Object[] elements = ((FSet) analysis.getValueAfterStmt(s)).toArray();
        DefinitionStmt[] defs = new DefinitionStmt[elements.length];

        for(int i = 0; i < elements.length; i++)
            defs[i] = (DefinitionStmt) elements[i];

        return defs;
    }
    */
}

class IntPair
{
    int op1, op2;

    public IntPair(int op1, int op2)
    {
        this.op1 = op1;
        this.op2 = op2;
    }

}

class LocalDefsFlowAnalysis extends ForwardFlowAnalysis
{
    FlowSet emptySet;
    Map localToPreserveSet;
    Map localToIntPair;

    public LocalDefsFlowAnalysis(StmtGraph g)
    {
        super(g);

        Object[] defs;
        FlowUniverse defUniverse;

        if(Main.isProfilingOptimization)
                Main.defsSetupTimer.start();

        // Create a list of all the definitions and group defs of the same local together
        {
            Map localToDefList = new HashMap(g.getBody().getLocalCount() * 2 + 1, 0.7f);

            // Initialize the set of defs for each local to empty
            {
                Iterator localIt = g.getBody().getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local l = (Local) localIt.next();

                    localToDefList.put(l, new ArrayList());
                }
            }

            // Fill the sets up
            {
                Iterator it = g.iterator();

                while(it.hasNext())
                {
                    Stmt s = (Stmt) it.next();

                    if(s instanceof DefinitionStmt)
                    {
                        DefinitionStmt d = (DefinitionStmt) s;

                        if(d.getLeftOp() instanceof Local)
                            ((List) localToDefList.get(d.getLeftOp())).add(d);

                    }
                }
            }

            // Generate the list & localToIntPair
            {
                Iterator it = localToDefList.keySet().iterator();
                List defList = new LinkedList();

                int startPos = 0;

                localToIntPair = new HashMap(g.getBody().getLocalCount() * 2 + 1, 0.7f);

                // For every local, add all its defs
                {
                    while(it.hasNext())
                    {
                        Local l = (Local) it.next();
                        Iterator jt = ((List) localToDefList.get(l)).iterator();

                        int endPos = startPos - 1;

                        while(jt.hasNext())
                        {
                            defList.add(jt.next());
                            endPos++;
                        }

                        localToIntPair.put(l, new IntPair(startPos, endPos));

                        // System.out.println(startPos + ":" + endPos);

                        startPos = endPos + 1;
                    }
                }

                defs = defList.toArray();
                defUniverse = new FlowUniverse(defs);
            }
        }

        emptySet = new ArrayPackedSet(defUniverse);

        // Create the preserve sets for each local.
        {
            Map localToKillSet = new HashMap(g.getBody().getLocalCount() * 2 + 1, 0.7f);
            localToPreserveSet = new HashMap(g.getBody().getLocalCount() * 2 + 1, 0.7f);

            List locals = g.getBody().getLocals();

            // Initialize to empty set
            {
                Iterator localIt = locals.iterator();

                while(localIt.hasNext())
                {
                    Local l = (Local) localIt.next();

                    localToKillSet.put(l, emptySet.clone());
                }
            }

            // Add every definition of this local
                for(int i = 0; i < defs.length; i++)
                {
                    DefinitionStmt d = (DefinitionStmt) defs[i];

                    if(d.getLeftOp() instanceof Local)
                    {
                        BoundedFlowSet killSet = (BoundedFlowSet) localToKillSet.get(d.getLeftOp());

                        killSet.add(d, killSet);
                    }
                }

            // Store complement
            {
                Iterator localIt = locals.iterator();

                while(localIt.hasNext())
                {
                    Local l = (Local) localIt.next();

                    BoundedFlowSet killSet = (BoundedFlowSet) localToKillSet.get(l);

                    killSet.complement(killSet);

                    localToPreserveSet.put(l, killSet);
                }
            }
        }

        if(Main.isProfilingOptimization)
	  Main.defsSetupTimer.end();

        if(Main.isProfilingOptimization)
	  Main.defsAnalysisTimer.start();

        doAnalysis();
        
        if(Main.isProfilingOptimization)
	  Main.defsAnalysisTimer.end();
    }

    protected Object newInitialFlow()
    {
        return emptySet.clone();
    }

    protected void flowThrough(Object inValue, Stmt stmt, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        if(stmt instanceof DefinitionStmt)
        {
            DefinitionStmt d = (DefinitionStmt) stmt;

            if(!(d.getLeftOp() instanceof Local))
            {
                in.copy(out);
                return;
            }


            Local local = (Local) d.getLeftOp();

            // Perform kill on value
                in.intersection((FlowSet) localToPreserveSet.get(local), out);

            // Perform generation
                out.add(d, out);

        }
        else
            in.copy(out);
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;
            
        sourceSet.copy(destSet);
    }

    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;

        FlowSet outSet = (FlowSet) out;

        inSet1.union(inSet2, outSet);
    }
}
