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
public abstract class ForwardFlowAnalysis<N,A> extends FlowAnalysis<N,A>
{
    /** Construct the analysis from a DirectedGraph representation of a Body.
     */
    public ForwardFlowAnalysis(DirectedGraph<N> graph)
    {
        super(graph);
    }

    protected boolean isForward()
    {
        return true;
    }

    protected void doAnalysis()
    {
        List<N> orderedUnits = constructOrderer().newList(graph, false);
        
        final int n = orderedUnits.size();
        
        final Map<N, Integer> numbers = new IdentityHashMap<N, Integer>(n*2+1);
        int i = 0;
                    
        for ( N s :orderedUnits ) {
        	numbers.put(u, i++);        

        	// Set initial Flows
            unitToBeforeFlow.put(s, newInitialFlow());
            unitToAfterFlow.put(s, newInitialFlow());
        }
    

        Collection<N> changedUnits = constructWorklist(numbers);
        changedUnits.addAll(orderedUnits);


        List<N> heads = graph.getHeads();
        
        // Feng Qian: March 07, 2002
        // Set initial values for entry points
        for ( N s : heads ) {
			// this is a forward flow analysis	
        	unitToBeforeFlow.put(s, entryInitialFlow());
        }
        
                
        int numComputations = 0;
        
        // Perform fixed point flow analysis
        {
            A previousFlow = newInitialFlow();

            while(!changedUnits.isEmpty())
            {
                //get the first object
                N s = changedUnits.iterator().next();
                changedUnits.remove(s);
                
                A beforeFlow = unitToBeforeFlow.get(s);
                A afterFlow = unitToAfterFlow.get(s);                

                copy(unitToAfterFlow.get(s), previousFlow);

                // Compute and store beforeFlow
                {
                    final Iterator<N> it = graph.getPredsOf(s).iterator();

					if ( it.hasNext() ) {
                    	copy(unitToAfterFlow.get(it.next()), beforeFlow);
                    	
		                while ( it.hasNext() ) {
		                    mergeInto(s, beforeFlow, unitToAfterFlow.get(it.next()));
		                }
		                
		                if ( heads.contains(s) ) {
		                    mergeInto(s, beforeFlow, entryInitialFlow());
	                    }
                	}
                }                
            
                // Compute afterFlow and store it.
                if (Options.v().interactive_mode()){
                    InteractionHandler h = InteractionHandler.v();
                    
                    A savedInfo;
                    if ( filterUnitToBeforeFlow != null ) {
                        savedInfo = filterUnitToBeforeFlow.get(s);
                        copy(filterUnitToBeforeFlow.get(s), savedInfo);
                    }
                    else {
                    	savedInfo = newInitialFlow();
                        copy(beforeFlow, savedInfo);
                    }
                    FlowInfo fi = new FlowInfo(savedInfo, s, true);
                    if ( h.getStopUnitList() != null && h.getStopUnitList().contains(s) ) {
                        h.handleStopAtNodeEvent(s);
                    }
                    h.handleBeforeAnalysisEvent(fi);
                }
                flowThrough(beforeFlow, s, afterFlow);
                if (Options.v().interactive_mode()){
                    A aSavedInfo;
                    if ( filterUnitToAfterFlow != null ) {
                        aSavedInfo = filterUnitToAfterFlow.get(s);
                        copy(filterUnitToAfterFlow.get(s), aSavedInfo);
                    }
                    else {
                    	aSavedInfo = newInitialFlow();
                        copy(afterFlow, aSavedInfo);
                    }
                    FlowInfo fi = new FlowInfo(aSavedInfo, s, false);
                    InteractionHandler.v().handleAfterAnalysisEvent(fi);
                }
                
                // Update queue appropriately
                if ( !previousFlow.equals(afterFlow) ) {
                	for ( N v : graph.getSuccsOf(s) ) {
                		changedUnits.add(v);
                	}
                }
                
                numComputations++;
            }
        }
        
        // G.v().out.println(graph.getBody().getMethod().getSignature() + " n: " + n + 
        //    " numComputations: " + numComputations + " avg: " + Main.truncatedOf((double) numComputations / n, 2));
        
        Timers.v().totalFlowNodes += n;
        Timers.v().totalFlowComputations += numComputations;
    }
    
	protected Collection<N> constructWorklist(final Map<N, Integer> numbers) {
		return new TreeSet<N>( new Comparator<N>() {
            public int compare(N o1, N o2) {
                Integer i1 = numbers.get(o1);
                Integer i2 = numbers.get(o2);
                return (i1.intValue() - i2.intValue());
            }
        } );
	}

}


