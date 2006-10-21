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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import soot.Timers;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.interaction.FlowInfo;
import soot.toolkits.graph.interaction.InteractionHandler;



/**
 *   Abstract class that provides the fixed point iteration functionality
 *   required by all BackwardFlowAnalyses.
 *  
 */
public abstract class BackwardFlowAnalysis extends FlowAnalysis
{
    /** Construct the analysis from a DirectedGraph representation of a Body. */
    public BackwardFlowAnalysis(DirectedGraph graph)
    {
        super(graph);
    }

    protected boolean isForward()
    {
        return false;
    }

    protected void doAnalysis()
    {
        final Map numbers = new HashMap();
        Timers.v().orderComputation = new soot.Timer();
        Timers.v().orderComputation.start();
        List orderedUnits = constructOrderer().newList(graph,false);
        Timers.v().orderComputation.end();
        new PseudoTopologicalOrderer().newList(graph,false);
        int i = 1;
        for( Iterator uIt = orderedUnits.iterator(); uIt.hasNext(); ) {
            final Object u = (Object) uIt.next();
            numbers.put(u, new Integer(i));
            i++;
        }

        Collection changedUnits = constructWorklist(numbers);


        // Set initial Flows and nodes to visit.
        {
            Iterator it = graph.iterator();

            while(it.hasNext())
            {
                Object s = it.next();

                changedUnits.add(s);

                unitToBeforeFlow.put(s, newInitialFlow());
                unitToAfterFlow.put(s, newInitialFlow());
            }
        }

        List tails = graph.getTails();
        
        // Feng Qian: March 07, 2002
        // init entry points
        {
            Iterator it = tails.iterator();
            
            while (it.hasNext()) {
                Object s = it.next();
                // this is a backward flow analysis
                unitToAfterFlow.put(s, entryInitialFlow());
            }
        }

        // Perform fixed point flow analysis
        {
            Object previousBeforeFlow = newInitialFlow();

            while(!changedUnits.isEmpty())
            {
                Object beforeFlow;
                Object afterFlow;

                //get the first object
                Object s = changedUnits.iterator().next();
                changedUnits.remove(s);
                boolean isTail = tails.contains(s);

                copy(unitToBeforeFlow.get(s), previousBeforeFlow);

                // Compute and store afterFlow
                {
                    List succs = graph.getSuccsOf(s);

                    afterFlow =  unitToAfterFlow.get(s);

                    if(succs.size() == 1)
                        copy(unitToBeforeFlow.get(succs.get(0)), afterFlow);
                    else if(succs.size() != 0)
                    {
                        Iterator succIt = succs.iterator();

                        copy(unitToBeforeFlow.get(succIt.next()), afterFlow);

                        while(succIt.hasNext())
                        {
                            Object otherBranchFlow = unitToBeforeFlow.get(succIt.next());
                            merge(afterFlow, otherBranchFlow);
                        }

                        if(isTail && succs.size() != 0)
                            merge(afterFlow, entryInitialFlow());
                    }
                }

                // Compute beforeFlow and store it.
                {
                    beforeFlow = unitToBeforeFlow.get(s);
                    if (Options.v().interactive_mode()){
                        Object savedFlow = newInitialFlow();
                        if (filterUnitToAfterFlow != null){
                            savedFlow = filterUnitToAfterFlow.get(s);
                            copy(filterUnitToAfterFlow.get(s), savedFlow);
                        }
                        else {
                            copy(afterFlow, savedFlow);
                        }
                        FlowInfo fi = new FlowInfo(savedFlow, s, false);
                        if (InteractionHandler.v().getStopUnitList() != null && InteractionHandler.v().getStopUnitList().contains(s)){
                            InteractionHandler.v().handleStopAtNodeEvent(s);
                        }
                        InteractionHandler.v().handleAfterAnalysisEvent(fi);
                    }
                    flowThrough(afterFlow, s, beforeFlow);
                    if (Options.v().interactive_mode()){
                        Object bSavedFlow = newInitialFlow();
                        if (filterUnitToBeforeFlow != null){
                            bSavedFlow = filterUnitToBeforeFlow.get(s);
                            copy(filterUnitToBeforeFlow.get(s), bSavedFlow);
                        }
                        else {
                            copy(beforeFlow, bSavedFlow);
                        }
                        FlowInfo fi = new FlowInfo(bSavedFlow, s, true);
                        InteractionHandler.v().handleBeforeAnalysisEvent(fi);
                    }
                }

                // Update queue appropriately
                    if(!beforeFlow.equals(previousBeforeFlow))
                    {
                        Iterator predIt = graph.getPredsOf(s).iterator();

                        while(predIt.hasNext())
                        {
                            Object pred = predIt.next();
                            
                            changedUnits.add(pred);
                        }
                    }
            }
        }
    }
    
	protected Collection constructWorklist(final Map numbers) {
		return new TreeSet( new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = (Integer) numbers.get(o1);
                Integer i2 = (Integer) numbers.get(o2);
                return (i1.intValue() - i2.intValue());
            }
        } );
	}
}



