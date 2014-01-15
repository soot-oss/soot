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

import java.util.IdentityHashMap;
import java.util.Map;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.Orderer;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.interaction.FlowInfo;
import soot.toolkits.graph.interaction.InteractionHandler;

/** 
 * An abstract class providing a framework for carrying out dataflow analysis.
 * Subclassing either BackwardFlowAnalysis or ForwardFlowAnalysis and providing
 * implementations for the abstract methods will allow Soot to compute the
 * corresponding flow analysis. 
 */
public abstract class FlowAnalysis<N,A> extends AbstractFlowAnalysis<N,A>
{
    /** Maps graph nodes to OUT sets. */
    protected Map<N, A> unitToAfterFlow;

    /** Filtered: Maps graph nodes to OUT sets. */
    protected Map<N, A> filterUnitToAfterFlow;

    /** Constructs a flow analysis on the given <code>DirectedGraph</code>. */
    public FlowAnalysis(DirectedGraph<N> graph)
    {
        super(graph);
        unitToAfterFlow = new IdentityHashMap<N, A>(graph.size() * 2 + 1);
    }

    /** 
     * Given the merge of the <code>out</code> sets, compute the <code>in</code> set for 
     * <code>s</code> (or in to out, depending on direction).
     *
     * This function often causes confusion, because the same interface
     * is used for both forward and backward flow analyses. The first
     * parameter is always the argument to the flow function (i.e. it
     * is the "in" set in a forward analysis and the "out" set in a
     * backward analysis), and the third parameter is always the result
     * of the flow function (i.e. it is the "out" set in a forward
     * analysis and the "in" set in a backward analysis).
     * */
    protected abstract void flowThrough(A in, N d, A out);

    /** Accessor function returning value of OUT set for s. */
    public A getFlowAfter(N s)
    {
        return unitToAfterFlow.get(s);
    }
    
	/**
	 * Default implementation constructing a PseudoTopologicalOrderer. 
	 * @return an Orderer to order the nodes for the fixed-point iteration 
	 */
	protected Orderer<N> constructOrderer() {
		return new PseudoTopologicalOrderer<N>();
	}
	
	
	protected FlowInfo getFlowInfo(N s, A flow, Map<?,A> filterFlow, boolean b) {
		A savedFlow;
		if (filterFlow != null) {
			savedFlow = filterFlow.get(s);
			copy(filterFlow.get(s), savedFlow);
		} else {
			savedFlow = newInitialFlow();
			copy(flow, savedFlow);
		}
		return new FlowInfo(savedFlow, s, b);
	}	

	protected void afterFlowThrough(N s, A flow, boolean handleStop) {
		final InteractionHandler h = InteractionHandler.v();

		FlowInfo fi = getFlowInfo(s, flow, filterUnitToAfterFlow, false);

		if (handleStop && h.getStopUnitList() != null && h.getStopUnitList().contains(s)) {
			h.handleStopAtNodeEvent(s);
		}
		h.handleAfterAnalysisEvent(fi);
	}

	protected void beforeFlowThrough(N s, A flow, boolean handleStop) {
    	final InteractionHandler h = InteractionHandler.v();
    	
		FlowInfo fi = getFlowInfo(s, flow, filterUnitToBeforeFlow, true);
    	
        if (handleStop && h.getStopUnitList() != null && h.getStopUnitList().contains(s)){
            h.handleStopAtNodeEvent(s);
        }
        h.handleBeforeAnalysisEvent(fi);
    }
}
