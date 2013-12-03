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
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
public abstract class BackwardFlowAnalysis<N,A> extends FlowAnalysis<N,A>
{
    /** Construct the analysis from a DirectedGraph representation of a Body. */
    public BackwardFlowAnalysis(DirectedGraph<N> graph)
    {
        super(graph);
    }

    protected boolean isForward()
    {
        return false;
    }

    protected void doAnalysis()
    {
        List<N> orderedUnits = constructOrderer().newList(graph, true);
        
        final int n = orderedUnits.size();
        
        final Map<N, Integer> numbers = new IdentityHashMap<N, Integer>(n*2+1);
        int i = 0;
                    
        for ( N s :orderedUnits ) {
        	numbers.put(u, i++);        

        	// Set initial Flows.
            unitToBeforeFlow.put(s, newInitialFlow());
            unitToAfterFlow.put(s, newInitialFlow());
        }
        
        Collection<N> changedUnits = constructWorklist(numbers);
        changedUnits.addAll(orderedUnits);
        

        List<N> tails = graph.getTails();
        
        // Feng Qian: March 07, 2002
        // init entry points
        for ( N s : tails ) {
			// this is a backward flow analysis	
        	unitToAfterFlow.put(s, entryInitialFlow());
        }
        
        
        //int numComputations = 0;
        
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
                
                copy(unitToBeforeFlow.get(s), previousFlow);

                // Compute and store afterFlow
                {
                    final Iterator<N> it = graph.getSuccsOf(s).iterator();

					if ( it.hasNext() ) {
                    	copy(unitToBeforeFlow.get(it.next()), afterFlow);
                    	
		                while ( it.hasNext() ) {
		                    mergeInto(s, afterFlow, unitToBeforeFlow.get(it.next()));
		                }
                    
		                if ( tails.contains(s) ) {
		                    mergeInto(s, afterFlow, entryInitialFlow());
	                    }
                	}
                }

                // Compute beforeFlow and store it.
                if ( Options.v().interactive_mode() ) {
					InteractionHandler h = InteractionHandler.v();
					
                    A savedFlow;
                    if ( filterUnitToAfterFlow != null ) {
                        savedFlow = filterUnitToAfterFlow.get(s);
                        copy(filterUnitToAfterFlow.get(s), savedFlow);
                    }
                    else {
                    	savedFlow = newInitialFlow();
                        copy(afterFlow, savedFlow);
                    }
                    FlowInfo fi = new FlowInfo(savedFlow, s, false);
                    if ( h.getStopUnitList() != null && h.getStopUnitList().contains(s) ) {
                        h.handleStopAtNodeEvent(s);
                    }
                    h.handleAfterAnalysisEvent(fi);
                }
                flowThrough(afterFlow, s, beforeFlow);
                if ( Options.v().interactive_mode() ) {
                    A bSavedFlow;
                    if ( filterUnitToBeforeFlow != null ) {
                        bSavedFlow = filterUnitToBeforeFlow.get(s);
                        copy(filterUnitToBeforeFlow.get(s), bSavedFlow);
                    }
                    else {
                    	bSavedFlow = newInitialFlow();
                        copy(beforeFlow, bSavedFlow);
                    }
                    FlowInfo fi = new FlowInfo(bSavedFlow, s, true);
                    InteractionHandler.v().handleBeforeAnalysisEvent(fi);
                }
            

                // Update queue appropriately
                if ( !previousFlow.equals(beforeFlow) ) {
                	for ( N v : graph.getPredsOf(s) ) {
                		changedUnits.add(v);
                	}
                }
                
                //numComputations++;
            }
        }        
        
        //Timers.v().totalFlowNodes += n;
        //Timers.v().totalFlowComputations += numComputations;
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



