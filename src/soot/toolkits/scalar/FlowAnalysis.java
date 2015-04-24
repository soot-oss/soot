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
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.Orderer;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.interaction.FlowInfo;
import soot.toolkits.graph.interaction.InteractionHandler;
import soot.util.PriorityQueue;

/** 
 * An abstract class providing a framework for carrying out dataflow analysis.
 * Subclassing either BackwardFlowAnalysis or ForwardFlowAnalysis and providing
 * implementations for the abstract methods will allow Soot to compute the
 * corresponding flow analysis. 
 */
public abstract class FlowAnalysis<N,A> extends AbstractFlowAnalysis<N,A>
{
	enum InteractionFlowHandler {
		NONE,
		FORWARD {
			@Override
			public <A,N> void handleFlowIn(FlowAnalysis<N,A> a, N s) {
				A inFlow = a.getInFlow(s);
				FlowInfo<A, N> fi = a.newFlowInfo(s, inFlow, a.filterUnitToBeforeFlow, true);
				handleStop(s).handleBeforeAnalysisEvent(fi);	
			}
			
			@Override
			public <A,N> void handleFlowOut(FlowAnalysis<N,A> a, N s) {
				A outFlow = a.getOutFlow(s);
				FlowInfo<A, N> fi = a.newFlowInfo(s, outFlow, a.filterUnitToAfterFlow, false);
				InteractionHandler.v().handleAfterAnalysisEvent(fi);				
			}
		},
		BACKWARD {
			@Override
			public <A,N> void handleFlowIn(FlowAnalysis<N,A> a, N s) {
				A inFlow = a.getInFlow(s);
				FlowInfo<A, N> fi = a.newFlowInfo(s, inFlow, a.filterUnitToAfterFlow, false);
				handleStop(s).handleAfterAnalysisEvent(fi);
			}
			
			@Override
			public <A,N> void handleFlowOut(FlowAnalysis<N,A> a, N s) {
				A outFlow = a.getOutFlow(s);
				FlowInfo<A, N> fi = a.newFlowInfo(s, outFlow, a.filterUnitToBeforeFlow, true);			
				InteractionHandler.v().handleBeforeAnalysisEvent(fi);
			}
		};

		InteractionHandler handleStop(Object s) {
			InteractionHandler h = InteractionHandler.v();
			List<?> stopList = h.getStopUnitList();
			if (stopList != null && stopList.contains(s)) {
				h.handleStopAtNodeEvent(s);
			}
			return h;
		}
				
		public <A,N> void handleFlowIn(FlowAnalysis<N,A> a, N s) {}
		public <A,N> void handleFlowOut(FlowAnalysis<N,A> a, N s) {}
	}
	
	enum GraphView {
		BACKWARD {
			@Override
			<N> List<N> getEntries(DirectedGraph<N> g) {
				return g.getTails();
			}

			@Override
			<N> List<N> getExits(DirectedGraph<N> g) {
				return g.getHeads();
			}

			@Override
			<N> List<N> getIn(DirectedGraph<N> g, N s) {
				return g.getSuccsOf(s);
			}

			@Override
			<N> List<N> getOut(DirectedGraph<N> g, N s) {
				return g.getPredsOf(s);
			}

			@Override
			<N> List<N> newList(DirectedGraph<N> g, Orderer<N> o) {
				return o.newList(g, false);
			}		
		},
		FORWARD {
			@Override
			<N> List<N> getEntries(DirectedGraph<N> g) {
				return g.getHeads();
			}

			@Override
			<N> List<N> getExits(DirectedGraph<N> g) {
				return g.getTails();
			}

			@Override
			<N> List<N> getIn(DirectedGraph<N> g, N s) {
				return g.getPredsOf(s);
			}

			@Override
			<N> List<N> getOut(DirectedGraph<N> g, N s) {
				return g.getSuccsOf(s);
			}

			@Override
			<N> List<N> newList(DirectedGraph<N> g, Orderer<N> o) {
				return o.newList(g, true);
			}		
		};

		abstract <N> List<N> getEntries(DirectedGraph<N> g);
		abstract <N> List<N> getExits(DirectedGraph<N> g);
		
		abstract <N> List<N> getIn(DirectedGraph<N> g, N s);
		abstract <N> List<N> getOut(DirectedGraph<N> g, N s);

		abstract <N> List<N> newList(DirectedGraph<N> g, Orderer<N> o);
	}
	
	final GraphView graphView;
	
    final InteractionFlowHandler interactiveFlow;
    
    int numComputations = 0;
    
    /** Maps graph nodes to OUT sets. */
    protected Map<N, A> unitToAfterFlow;

    /** Filtered: Maps graph nodes to OUT sets. */
    protected Map<N, A> filterUnitToAfterFlow;

    /** Constructs a flow analysis on the given <code>DirectedGraph</code>. */
    public FlowAnalysis(DirectedGraph<N> graph, GraphView graphView, InteractionFlowHandler interactionFlowHandler)
    {
        super(graph);
    	assert graphView != null;
    	assert interactionFlowHandler != null;
        this.graphView = graphView;
        this.interactiveFlow = Options.v().interactive_mode() ? interactionFlowHandler : InteractionFlowHandler.NONE;
        this.unitToAfterFlow = new IdentityHashMap<N, A>(graph.size() * 2 + 1);
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
	
	
	protected FlowInfo<A, N> newFlowInfo(N s, A flow, Map<N,A> filterFlow, boolean b) {
		A savedFlow = null;
		if (filterFlow != null) 
			savedFlow = filterFlow.get(s);
		if (savedFlow == null)
			savedFlow = newInitialFlow();
		copy(flow, savedFlow);
		return new FlowInfo<A, N>(savedFlow, s, b);
	}	
	

	abstract A getInFlow(N s);
	abstract A getOutFlow(N s);
	
	@Override
    protected A entryInitialFlow() {
    	return null;
    }
	
	@Override
	protected void doAnalysis() {
		numComputations = 0;
				
		for (N s : graph) {
			// Set initial Flows
			unitToBeforeFlow.put(s, newInitialFlow());
			unitToAfterFlow.put(s, newInitialFlow());
		}
				
		// Feng Qian: March 07, 2002
		// init entry points
		final A e = entryInitialFlow();
		final Collection<N> entries;
		if (e == null) {
			entries = Collections.emptySet();
		} else {
			entries = new HashSet<N>(graphView.getEntries(graph));
			for (N s : entries) {	
				copy(e, getInFlow(s));
			}
		}
		
		// int numComputations = 0;

		// Perform fixed point flow analysis
		for (Queue<N> q = PriorityQueue.of(graphView.newList(graph, constructOrderer()));;) {
			N s = q.poll();
			if (s == null)
				return;

			A inFlow = getInFlow(s);				
			
			// Compute and store afterFlow
			boolean isFirst = true;
			for (N in : graphView.getIn(graph, s)) {
				if (isFirst) {
					isFirst = false;
					copy(getOutFlow(in), inFlow);
					
					// optional entry initial flow
					if (entries.contains(s)) {
						mergeInto(s, inFlow, e);
					}
					continue;
				}

				mergeInto(s, inFlow, getOutFlow(in));
			}
							
			// Compute beforeFlow and store it.
			interactiveFlow.handleFlowIn(this, s);
			boolean hasChanged = flow(inFlow, s, getOutFlow(s));
			interactiveFlow.handleFlowOut(this, s);			

			// Update queue appropriately
			if ( hasChanged ) {
				q.addAll(graphView.getOut(graph, s));
			}

			numComputations++;
		}
	}
	
	protected boolean flow(A in, N d, A out) {
		A previous = newInitialFlow();
		copy(out, previous);
		flowThrough(in, d, out);
		return !previous.equals(out);
	}
	
}
