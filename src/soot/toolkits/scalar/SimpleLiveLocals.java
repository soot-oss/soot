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
import java.util.*;

import soot.toolkits.graph.*;


/**
 *   Analysis that provides an implementation of the LiveLocals  interface.
 */
public class SimpleLiveLocals implements LiveLocals
{
    Map<Unit, List<Local>> unitToLocalsAfter;
    Map<Unit, List<Local>> unitToLocalsBefore;



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
            unitToLocalsAfter = new HashMap<Unit, List<Local>>(graph.size() * 2 + 1, 0.7f);
            unitToLocalsBefore = new HashMap<Unit, List<Local>>(graph.size() * 2 + 1, 0.7f);

            for (Unit s : graph) {
                FlowSet set = analysis.getFlowBefore(s);
                unitToLocalsBefore.put(s, Collections.unmodifiableList(set.toList()));
                
                set = analysis.getFlowAfter(s);
                unitToLocalsAfter.put(s, Collections.unmodifiableList(set.toList()));
            }            
        }
        
        if(Options.v().time())
            Timers.v().livePostTimer.end();
        
        if(Options.v().time())
            Timers.v().liveTimer.end();
    }

    public List<Local> getLiveLocalsAfter(Unit s)
    {
        return unitToLocalsAfter.get(s);
    }
    
    public List<Local> getLiveLocalsBefore(Unit s)
    {
        return unitToLocalsBefore.get(s);
    }
}

class SimpleLiveLocalsAnalysis extends BackwardFlowAnalysis<Unit, FlowSet>
{
    FlowSet emptySet;
    Map<Unit, FlowSet> unitToGenerateSet;
    Map<Unit, FlowSet> unitToKillSet;

    SimpleLiveLocalsAnalysis(UnitGraph g)
    {
        super(g);

        if(Options.v().time())
            Timers.v().liveSetupTimer.start();

        emptySet = new ArraySparseSet();

        // Create kill sets.
        {
            unitToKillSet = new HashMap<Unit, FlowSet>(g.size() * 2 + 1, 0.7f);

            for (Unit s : g) {
                FlowSet killSet = emptySet.clone();

                for (ValueBox box : s.getDefBoxes()) {
                    if(box.getValue() instanceof Local)
                        killSet.add(box.getValue(), killSet);
                }

                unitToKillSet.put(s, killSet);
            }
        }

        // Create generate sets
        {
            unitToGenerateSet = new HashMap<Unit, FlowSet>(g.size() * 2 + 1, 0.7f);

            for (Unit s : g) {
                FlowSet genSet = emptySet.clone();

                for (ValueBox box : s.getUseBoxes()) {
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

    @Override
    protected FlowSet newInitialFlow()
    {
        return emptySet.clone();
    }

    @Override
    protected FlowSet entryInitialFlow()
    {
        return emptySet.clone();
    }
        
    @Override
    protected void flowThrough(FlowSet inValue, Unit unit, FlowSet outValue)
    {
        // Perform kill
    	inValue.difference(unitToKillSet.get(unit), outValue);

        // Perform generation
    	outValue.union(unitToGenerateSet.get(unit), outValue);
    }

    @Override
    protected void merge(FlowSet in1, FlowSet in2, FlowSet out)
    {
        in1.union(in2, out);
    }
    
    @Override
    protected void copy(FlowSet source, FlowSet dest)
    {
        source.copy(dest);
    }
}
