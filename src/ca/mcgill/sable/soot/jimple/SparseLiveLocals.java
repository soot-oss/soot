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
 The reference version is: $SootVersion$

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

 - Modified on January 23, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Branched off from PackedLiveLocals;
   
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

public class SparseLiveLocals implements LiveLocals
{
    Map stmtToLocals;
    //Map stmtToLocalsBefore;

    public SparseLiveLocals(CompleteStmtGraph graph)
    {
        SparseLiveLocalsAnalysis analysis = new SparseLiveLocalsAnalysis(graph);

        if(Main.isProfilingOptimization)
                Main.livePostTimer.start();

        // Build stmtToLocals map
        {
            // long liveCount = 0;
            
            stmtToLocals = new HashMap(graph.size() * 2 + 1, 0.7f);

            Iterator stmtIt = graph.iterator();

            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
 
                FlowSet set = (FlowSet) analysis.getFlowBeforeStmt(s);
                //List localList = set.toList();
                
                //liveCount += localList.size();
                
                stmtToLocals.put(s, Collections.unmodifiableList(set.toList()));
            }
            
            // System.out.println((((double) liveCount) / graph.size()) + " live locals per stmt on avg" + (graph.size())); 
        }
        
        if(Main.isProfilingOptimization)
                Main.livePostTimer.end();
    }

    /*
    public List getLiveLocalsBefore(Stmt s)
    {
        FSet set = (FSet) analysis.getValueBeforeStmt(s);

        return set.toList();
    }
      */

    public List getLiveLocalsAfter(Stmt s)
    {
        return (List) stmtToLocals.get(s);
    }
}

class SparseLiveLocalsAnalysis extends BackwardFlowAnalysis
{
    FlowSet emptySet;
    Map stmtToGenerateSet;
    Map stmtToKillSet;

    SparseLiveLocalsAnalysis(StmtGraph g)
    {
        super(g);

        if(Main.isProfilingOptimization)
                Main.liveSetupTimer.start();

        emptySet = new ArraySparseSet();

        // Create Kill sets.
        {
            stmtToKillSet = new HashMap(g.size() * 2 + 1, 0.7f);

            Iterator stmtIt = g.iterator();

            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();

                FlowSet killSet = (FlowSet) emptySet.clone();

                Iterator boxIt = s.getDefBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                        killSet.add(box.getValue(), killSet);
                }

                stmtToKillSet.put(s, killSet);
            }
        }

        // Create generate sets
        {
            stmtToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);

            Iterator stmtIt = g.iterator();

            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();

                FlowSet genSet = (FlowSet) emptySet.clone();

                Iterator boxIt = s.getUseBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                        genSet.add(box.getValue(), genSet);
                }

                stmtToGenerateSet.put(s, genSet);
            }
        }

        if(Main.isProfilingOptimization)
            Main.liveSetupTimer.end();

        if(Main.isProfilingOptimization)
            Main.liveAnalysisTimer.start();

        doAnalysis();
        
        if(Main.isProfilingOptimization)
            Main.liveAnalysisTimer.end();

    }

    protected Object newInitialFlow()
    {
        return emptySet.clone();
    }

    protected void flowThrough(Object inValue, Stmt stmt, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        // Perform kill
            in.difference((FlowSet) stmtToKillSet.get(stmt), out);

        // Perform generation
            out.union((FlowSet) stmtToGenerateSet.get(stmt), out);
    }

    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;

        FlowSet outSet = (FlowSet) out;

        inSet1.union(inSet2, outSet);
    }
    
    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;
            
        sourceSet.copy(destSet);
    }
}
