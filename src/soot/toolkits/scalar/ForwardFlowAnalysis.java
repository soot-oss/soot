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

import soot.*;
import soot.toolkits.graph.*;
import java.util.*;

import soot.options.*;
import soot.toolkits.graph.interaction.*;

/**
 *   Abstract class that provides the fixed point iteration functionality
 *   required by all ForwardFlowAnalyses.
 *  
 */
public abstract class ForwardFlowAnalysis extends FlowAnalysis
{
    /** Construct the analysis from a DirectedGraph representation of a Body. */
    public ForwardFlowAnalysis(DirectedGraph graph)
    {
        super(graph);
    }

    protected boolean isForward()
    {
        return true;
    }

    protected void doAnalysis()
    {
        final Map numbers = new HashMap();
        Timers.v().orderComputation = new soot.Timer();
        Timers.v().orderComputation.start();
        List orderedUnits = constructOrderer().newList(graph,false);
        Timers.v().orderComputation.end();
        int i = 1;
        for( Iterator uIt = orderedUnits.iterator(); uIt.hasNext(); ) {
            final Object u = (Object) uIt.next();
            numbers.put(u, new Integer(i));
            i++;
        }

        Collection changedUnits = constructWorklist(numbers);

        List heads = graph.getHeads();
        int numNodes = graph.size();
        int numComputations = 0;
        
        // Set initial values and nodes to visit.
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

        // Feng Qian: March 07, 2002
        // Set initial values for entry points
        {
            Iterator it = heads.iterator();
            
            while (it.hasNext()) {
                Object s = it.next();
                // this is a forward flow analysis
                unitToBeforeFlow.put(s, entryInitialFlow());
            }
        }
        
        // Perform fixed point flow analysis
        {
            Object previousAfterFlow = newInitialFlow();

            while(!changedUnits.isEmpty())
            {
                Object beforeFlow;
                Object afterFlow;

                //get the first object
                Object s = changedUnits.iterator().next();
                changedUnits.remove(s);
                boolean isHead = heads.contains(s);

                copy(unitToAfterFlow.get(s), previousAfterFlow);

                // Compute and store beforeFlow
                {
                    List preds = graph.getPredsOf(s);

                    beforeFlow = unitToBeforeFlow.get(s);
                    
                    if(preds.size() == 1)
                        copy(unitToAfterFlow.get(preds.get(0)), beforeFlow);
                    else if(preds.size() != 0)
                    {
                        Iterator predIt = preds.iterator();

                        copy(unitToAfterFlow.get(predIt.next()), beforeFlow);

                        while(predIt.hasNext())
                        {
                            Object otherBranchFlow = unitToAfterFlow.get(predIt.next());
                            merge(beforeFlow, otherBranchFlow);
                        }
                    }

                    if(isHead && preds.size() != 0)
                    		merge(beforeFlow, entryInitialFlow());
                    	}
                    
                // Compute afterFlow and store it.
                {
                    afterFlow = unitToAfterFlow.get(s);
                    if (Options.v().interactive_mode()){
                        
                        Object savedInfo = newInitialFlow();
                        if (filterUnitToBeforeFlow != null){
                            savedInfo = filterUnitToBeforeFlow.get(s);
                            copy(filterUnitToBeforeFlow.get(s), savedInfo);
                        }
                        else {
                            copy(beforeFlow, savedInfo);
                        }
                        FlowInfo fi = new FlowInfo(savedInfo, s, true);
                        if (InteractionHandler.v().getStopUnitList() != null && InteractionHandler.v().getStopUnitList().contains(s)){
                            InteractionHandler.v().handleStopAtNodeEvent(s);
                        }
                        InteractionHandler.v().handleBeforeAnalysisEvent(fi);
                    }
                    flowThrough(beforeFlow, s, afterFlow);
                    if (Options.v().interactive_mode()){
                        Object aSavedInfo = newInitialFlow();
                        if (filterUnitToAfterFlow != null){
                            aSavedInfo = filterUnitToAfterFlow.get(s);
                            copy(filterUnitToAfterFlow.get(s), aSavedInfo);
                        }
                        else {
                            copy(afterFlow, aSavedInfo);
                        }
                        FlowInfo fi = new FlowInfo(aSavedInfo, s, false);
                        InteractionHandler.v().handleAfterAnalysisEvent(fi);
                    }
                    numComputations++;
                }

                // Update queue appropriately
                    if(!afterFlow.equals(previousAfterFlow))
                    {
                        Iterator succIt = graph.getSuccsOf(s).iterator();

                        while(succIt.hasNext())
                        {
                            Object succ = succIt.next();
                            
                            changedUnits.add(succ);
                        }
                    }
                }
}
        
        // G.v().out.println(graph.getBody().getMethod().getSignature() + " numNodes: " + numNodes + 
        //    " numComputations: " + numComputations + " avg: " + Main.truncatedOf((double) numComputations / numNodes, 2));
        
        Timers.v().totalFlowNodes += numNodes;
        Timers.v().totalFlowComputations += numComputations;
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


