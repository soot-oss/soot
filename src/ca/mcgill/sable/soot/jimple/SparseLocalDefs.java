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

 - Modified on March 13, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Re-organized the timers.

 - Modified on January 24, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Branched off from SimpleLocalDefs.
   
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

public class SparseLocalDefs implements LocalDefs
{
    Map localStmtPairToDefs;
    LiveLocals liveLocals;
    
    public SparseLocalDefs(CompleteStmtGraph g, LiveLocals liveLocals)
    {
        if(Main.isProfilingOptimization)
            Main.defsTimer.start();
    
        if(Main.isVerbose)
            System.out.println("[" + g.getBody().getMethod().getName() +
                "]     Constructing SparseLocalDefs...");
        
        SparseLocalDefsFlowAnalysis analysis = new SparseLocalDefsFlowAnalysis(g, liveLocals);

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
                            FlowSet value = (FlowSet) analysis.getFlowBeforeStmt(s);

                            List allLocalDefs = value.toList();
                            
                            // Pick all definitions pertaining to this local
                            {
                                List localDefs = new ArrayList();
                                Iterator defIt = allLocalDefs.iterator();
                                
                                while(defIt.hasNext())
                                {
                                    DefinitionStmt d = (DefinitionStmt) defIt.next();
                                    
                                    if(d.getLeftOp() == l)
                                        localDefs.add(d);
                                }
                                
                                localStmtPairToDefs.put(pair, Collections.unmodifiableList(localDefs));
                            }
                        }
                    }
                }
            }
        }

        if(Main.isProfilingOptimization)
                Main.defsPostTimer.end();

        if(Main.isProfilingOptimization)
            Main.defsTimer.end();
    }

    public List getDefsOfAt(Local l, Stmt s)
    {
        LocalStmtPair pair = new LocalStmtPair(l, s);

        return (List) localStmtPairToDefs.get(pair);
    }
    
    class LocalStmtPair
    {
        Local local;
        Stmt stmt;
    
        LocalStmtPair(Local local, Stmt stmt)
        {
            this.local = local;
            this.stmt = stmt;
        }
    
        public boolean equals(Object other)
        {
            if(other instanceof LocalStmtPair &&
                ((LocalStmtPair) other).local == this.local &&
                ((LocalStmtPair) other).stmt == this.stmt)
            {
                return true;
            }
            else
                return false;
        }
    
        public int hashCode()
        {
            return local.hashCode() * 101 + stmt.hashCode() + 17;
        }
    }
}

class SparseLocalDefsFlowAnalysis extends ForwardFlowAnalysis
{
    FlowSet emptySet;
    Map localToPreserveSet;
    FlowSet workingSet;
    LiveLocals liveLocals;
    
    public SparseLocalDefsFlowAnalysis(StmtGraph g, LiveLocals liveLocals)
    {
        super(g);

        this.liveLocals = liveLocals;
        if(Main.isProfilingOptimization)
                Main.defsSetupTimer.start();

        emptySet = new ArraySparseSet();
        workingSet = (FlowSet) emptySet.clone();
        
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
            }
            else {

                Local local = (Local) d.getLeftOp();
    
                // Kill all other definitions of this local
                {
                    workingSet.clear();
                
                    Iterator defIt = in.toList().iterator();
                    
                    while(defIt.hasNext())
                    {
                        DefinitionStmt def = (DefinitionStmt) defIt.next();
                        
                        if(def.getLeftOp() == local)
                            workingSet.add(def, workingSet);
                    }
                    
                    in.difference(workingSet, out);
                }
                
                    
                // Perform generation
                    out.add(d, out);
            }

        }
        else
            in.copy(out);
            
        // Kill all definitions whose locals are no longer live
        {
            Iterator useBoxIt = stmt.getUseBoxes().iterator();
            List liveLocalsAfter = liveLocals.getLiveLocalsAfter(stmt);
            
            workingSet.clear();
            
            while(useBoxIt.hasNext())
            {
                ValueBox useBox = (ValueBox) useBoxIt.next();
                
                if(useBox.getValue() instanceof Local)
                {
                    Local l = (Local) useBox.getValue();
                    
                    if(!liveLocalsAfter.contains(l))
                    {
                        // Kill all definitions of l
                        
                        Iterator defIt = out.toList().iterator();
                    
                        while(defIt.hasNext())
                        {
                            DefinitionStmt def = (DefinitionStmt) defIt.next();
                            
                            if(def.getLeftOp() == l)
                                workingSet.add(def, workingSet);
                        }
                    }
                }
            }
            
            out.difference(workingSet, out);
        }
        
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
