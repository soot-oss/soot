/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */






package soot.toolkits.scalar;
import soot.options.*;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;


/**
 *   Analysis that provides an implementation of the LiveLocals  interface.
 */
public class SimpleLiveLocals implements LiveLocals
{
    Map unitToLocalsAfter;
    Map unitToLocalsBefore;



    /**
     *   Computes the analysis given a UnitGraph computed from a
     *   method body.  It is recommended that a ExceptionalUnitGraph (or
     *   similar) be provided for correct results in the case of
     *   exceptional control flow.
     *
     *   @param g a graph on which to compute the analysis.
     *   
     *   @see ExceptionalUnitGraph
     */
    public SimpleLiveLocals(UnitGraph graph)
    {
        if(Options.v().time())
            Timers.v().liveTimer.start();
        
        if(Options.v().verbose())
            G.v().out.println("[" + graph.getBody().getMethod().getName() +
                "]     Constructing SimpleLiveLocals...");

                        
        SimpleLiveLocalsAnalysis analysis = new SimpleLiveLocalsAnalysis(graph);

        if(Options.v().time())
                Timers.v().livePostTimer.start();

        // Build unitToLocals map
        {
            unitToLocalsAfter = new HashMap(graph.size() * 2 + 1, 0.7f);
            unitToLocalsBefore = new HashMap(graph.size() * 2 + 1, 0.7f);

            Iterator unitIt = graph.iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();
 
                FlowSet set = (FlowSet) analysis.getFlowBefore(s);
                unitToLocalsBefore.put(s, Collections.unmodifiableList(set.toList()));
                
                set = (FlowSet) analysis.getFlowAfter(s);
                unitToLocalsAfter.put(s, Collections.unmodifiableList(set.toList()));
            }            
        }
        
        if(Options.v().time())
            Timers.v().livePostTimer.end();
        
        if(Options.v().time())
            Timers.v().liveTimer.end();
    }

    public List getLiveLocalsAfter(Unit s)
    {
        return (List) unitToLocalsAfter.get(s);
    }
    
    public List getLiveLocalsBefore(Unit s)
    {
        return (List) unitToLocalsBefore.get(s);
    }
}

class SimpleLiveLocalsAnalysis extends BackwardFlowAnalysis
{
    FlowSet emptySet;
    Map unitToGenerateSet;
    Map unitToKillSet;

    SimpleLiveLocalsAnalysis(UnitGraph g)
    {
        super(g);

        if(Options.v().time())
            Timers.v().liveSetupTimer.start();

        emptySet = new ArraySparseSet();

        // Create kill sets.
        {
            unitToKillSet = new HashMap(g.size() * 2 + 1, 0.7f);

            Iterator unitIt = g.iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                FlowSet killSet = (FlowSet) emptySet.clone();

                Iterator boxIt = s.getDefBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                        killSet.add(box.getValue(), killSet);
                }

                    unitToKillSet.put(s, killSet);
            }
        }

        // Create generate sets
        {
            unitToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);

            Iterator unitIt = g.iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                FlowSet genSet = (FlowSet) emptySet.clone();

                Iterator boxIt = s.getUseBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                        genSet.add(box.getValue(), genSet);
                }

                unitToGenerateSet.put(s, genSet);
            }
        }

        if(Options.v().time())
            Timers.v().liveSetupTimer.end();

        if(Options.v().time())
            Timers.v().liveAnalysisTimer.start();

        doAnalysis();
        
        if(Options.v().time())
            Timers.v().liveAnalysisTimer.end();

    }

    protected Object newInitialFlow()
    {
        return emptySet.clone();
    }

    protected Object entryInitialFlow()
    {
        return emptySet.clone();
    }
        
    protected void flowThrough(Object inValue, Object unit, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        // Perform kill
            in.difference((FlowSet) unitToKillSet.get(unit), out);

        // Perform generation
            out.union((FlowSet) unitToGenerateSet.get(unit), out);
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
