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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.interaction.FlowInfo;
import soot.toolkits.graph.interaction.InteractionHandler;
import soot.util.Numberable;
import soot.util.PriorityQueue;
import static soot.toolkits.scalar.FlowAnalysis.StronglyConnectedComponents.newUniverse;


/** 
 * An abstract class providing a framework for carrying out dataflow analysis.
 * Subclassing either BackwardFlowAnalysis or ForwardFlowAnalysis and providing
 * implementations for the abstract methods will allow Soot to compute the
 * corresponding flow analysis. 
 */
public abstract class FlowAnalysis<N,A> extends AbstractFlowAnalysis<N,A>
{
	static class Entry<D,F> implements Numberable {
		final D data;
		int number;
		int min;
		boolean isStronglyConnected;
		Entry<D,F>[] in;
		Entry<D,F>[] out;
		F inFlow;
		F outFlow;
		
		@SuppressWarnings("unchecked")
		Entry(D u, Entry<D,F> pred) {
			in = (Entry<D,F>[]) new Entry[] {pred};
			data = u;
			min = Integer.MIN_VALUE;
			isStronglyConnected = false;
		}		
		
		@Override
		public String toString() {
			return data.toString();
		}

		@Override
		public void setNumber(int n) {
			this.number = n;			
		}

		@Override
		public int getNumber() {
			return number;
		}
	}

	static class StronglyConnectedComponents {			
		private StronglyConnectedComponents() {}
		static <D,F> List<Entry<D,F>> newUniverse(DirectedGraph<D> g, GraphView gv, F entryFlow) {
			final int n = g.size();
			Deque<Entry<D,F>> s = new ArrayDeque<Entry<D,F>>(n);
			Deque<Entry<D,F>> q = new ArrayDeque<Entry<D,F>>(n);
			List<Entry<D,F>> universe = new ArrayList<Entry<D,F>>(n);
			Map<D, Entry<D,F>> visited = new HashMap<D, Entry<D,F>>((n*3)/2+7);
			
			Entry<D,F>superEntry = new Entry<D,F>(null, null);
			superEntry.outFlow = entryFlow;		
			
			q.addAll(Arrays.asList(visitEntry(visited, superEntry, gv.getEntries(g))));
			
			for (;;) {
				if (q.isEmpty()) {
					Collections.reverse(universe);
					return universe;
				}
				
				Entry<D,F> v = q.peekLast();		
				
				// already finished
				if (v.min == Integer.MAX_VALUE) {
					q.removeLast();
					continue;
				}
				
				if (sccPush(s, v)) {					
					boolean foundNew = false;
					for (Entry<D,F> e : visitEntry(visited, v, gv.getOut(g, v.data))) {
						if (e.min == Integer.MIN_VALUE) {
							q.addLast(e);
							foundNew = true;
						}						
					}
					if (foundNew)
						continue;
				}

				universe.add(v);
				sccPop(s, v);
				q.removeLast();
			}
		}

		@SuppressWarnings("unchecked")
		private static <D,F> Entry<D,F>[] visitEntry(Map<D, Entry<D,F>> visited, Entry<D,F> v, List<D> out) {		
			int n = out.size();
			v.out = new Entry[n];
			
			// reverse output-order for a better visit-order!
			for (int i = 0; i < n; i++) {
				v.out[n-1-i] = getEntryOf(visited, out.get(i), v);
			}
			
			return v.out;
		}
		
		private static <D,F> Entry<D,F> getEntryOf(Map<D, Entry<D,F>> visited, D n, Entry<D,F> head) {
			Entry<D,F> me = visited.get(n);
			if (me == null) {
				visited.put(n, me = new Entry<D,F>(n, head));	
			} else {
				// adding self ref (at least strongly connected with itself)
				if (me == head)
					me.isStronglyConnected = true;
				
				// merge nodes are uncommon, so this is ok
				int l = me.in.length;
				me.in = Arrays.copyOf(me.in, l+1);
				me.in[l] = head;					
			}
			return me;
		}

		private static <D,F> boolean sccPush(Deque<Entry<D,F>> stack, Entry<D,F> e) {
			// push node to stack, if it hasn't been visited 
			if (e.min == Integer.MIN_VALUE) {
				e.min = stack.size();
				stack.push(e);			
				return true;
			}
			return false;
		}
		
		private static <D,F> void sccPop(Deque<Entry<D,F>> stack, Entry<D,F> v) {
			int min = v.min;
			
			for (Entry<D,F> e : v.out) {
				min = Math.min(min, e.min);
			}
			
			// not our SCC 
			if (min < v.min) {
				v.min = min;
				return;
			}
			
			// we only want real SCCs
			
			Entry<D,F> w = stack.pop();
			w.min = Integer.MAX_VALUE;		
			if (w == v) {	
				return;		
			}
			w.isStronglyConnected = true; 
			for (;;) {
				w = stack.pop();
				w.isStronglyConnected = true; 
				w.min = Integer.MAX_VALUE;
				if (w == v) {	
					return;		
				}
			}	
		}
	}
	
	
	enum InteractionFlowHandler {
		NONE,
		FORWARD {
			@Override
			public <A,N> void handleFlowIn(FlowAnalysis<N,A> a, N s) {
				beforeEvent(stop(s), a, s);
			}
			
			@Override
			public <A,N> void handleFlowOut(FlowAnalysis<N,A> a, N s) {
				afterEvent(InteractionHandler.v(), a, s);
			}
		},
		BACKWARD {
			@Override
			public <A,N> void handleFlowIn(FlowAnalysis<N,A> a, N s) {
				afterEvent(stop(s), a, s);
			}
			
			@Override
			public <A,N> void handleFlowOut(FlowAnalysis<N,A> a, N s) {
				beforeEvent(InteractionHandler.v(), a, s);
			}
		};
		
		<A,N> void beforeEvent(InteractionHandler i, FlowAnalysis<N,A> a, N s) {
			A savedFlow = a.filterUnitToBeforeFlow.get(s);
			if (savedFlow == null)
				savedFlow = a.newInitialFlow();
			a.copy(a.unitToBeforeFlow.get(s), savedFlow);
			i.handleBeforeAnalysisEvent(new FlowInfo<A, N>(savedFlow, s, true));
		}	
		
		<A,N> void afterEvent(InteractionHandler i, FlowAnalysis<N,A> a, N s) {
			A savedFlow = a.filterUnitToAfterFlow.get(s);
			if (savedFlow == null)
				savedFlow = a.newInitialFlow();
			a.copy(a.unitToAfterFlow.get(s), savedFlow);
			i.handleAfterAnalysisEvent(new FlowInfo<A, N>(savedFlow, s, false));
		}
	
		InteractionHandler stop(Object s) {
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
			<N> List<N> getOut(DirectedGraph<N> g, N s) {
				return g.getPredsOf(s);
			}
		},
		FORWARD {
			@Override
			<N> List<N> getEntries(DirectedGraph<N> g) {
				return g.getHeads();
			}

			@Override
			<N> List<N> getOut(DirectedGraph<N> g, N s) {
				return g.getSuccsOf(s);
			}
		};

		abstract <N> List<N> getEntries(DirectedGraph<N> g);
		abstract <N> List<N> getOut(DirectedGraph<N> g, N s);
	}		    
    
    /** Maps graph nodes to OUT sets. */
    protected Map<N, A> unitToAfterFlow;

    /** Filtered: Maps graph nodes to OUT sets. */
    protected Map<N, A> filterUnitToAfterFlow = Collections.emptyMap();

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
     *
     * @param in the input flow
     * @param d the current node
     * @param out the returned flow
     **/
    protected abstract void flowThrough(A in, N d, A out);

    /** Accessor function returning value of OUT set for s. */
    
    public A getFlowAfter(N s)
    {
        A a = unitToAfterFlow.get(s);
        return a == null ? newInitialFlow() : a;
    }
    
    @Override
    public A getFlowBefore(N s)
    {
        A a = unitToBeforeFlow.get(s);
        return a == null ? newInitialFlow() : a;
    }
		
	private void initFlow(Iterable<Entry<N,A>> universe, Map<N, A> in, Map<N, A> out) {	
    	assert universe != null;	
    	assert in != null;
    	assert out != null;	
		
		for (Entry<N, A> n : universe) {
			boolean omitable = omissible(n.data);
			if (n.in.length > 1) {
				// make sure no loop-entry will omitted
				if (omitable && n.isStronglyConnected) {
					for (Entry<N, A> p : n.in) {
						// this node has at least one back-edge 
						if (p.outFlow == null) {
							omitable = false;
							break;
						}
					}
				}
				
				n.inFlow = newInitialFlow();
			} else {
				assert n.in.length == 1 : "missing superhead";
				n.inFlow = n.in[0].outFlow;	
				assert n.inFlow != null : "topological order is broken";
			}
			
			if (omitable) {
				n.outFlow = n.inFlow;		
			} else {
				n.outFlow = newInitialFlow();
			}

			// for legacy api
			in.put(n.data, n.inFlow);
			out.put(n.data, n.outFlow);
		}
	}
	
	/**
	 * If a flow node can be omitted return <code>true</code>, otherwise <code>false</code>.
	 * There is no guarantee an node will be omitted.
	 * 
	 * If you are unsure, dont't overwrite this method 
	 * 
	 * @param n the node to check
	 * @return <code>false</code>
	 */
	protected boolean omissible(N n) {
		return false;
	}
	
	
	final int doAnalysis(GraphView graphView, InteractionFlowHandler interactiveHandler, Map<N, A> inFlow, Map<N, A> outFlow) {
    	assert graphView != null;
    	assert interactiveHandler != null;
    	
    	interactiveHandler = Options.v().interactive_mode() 
    			? interactiveHandler 
    			: InteractionFlowHandler.NONE
    			;
    	
    	final List<Entry<N,A>> universe = newUniverse(graph, graphView, entryInitialFlow());
		
    	initFlow(universe, inFlow, outFlow);
    	
		Queue<Entry<N,A>> q = PriorityQueue.of(universe, true);

	    int numComputations = 0;
		// Perform fixed point flow analysis
		for (numComputations = 0;;numComputations++) {
			Entry<N,A> e = q.poll();
			if (e == null)
				break;
			
			Entry<N,A>[] in = e.in;
			if (in.length > 1) {
				copy(in[0].outFlow, e.inFlow);			
				for (int i = 1; i < in.length; i++) {
					mergeInto(e.data, e.inFlow, in[i].outFlow);
				}		
			}
			
			// Compute beforeFlow and store it.
			interactiveHandler.handleFlowIn(this, e.data);
			boolean hasChanged = flowThrough(e);
			interactiveHandler.handleFlowOut(this, e.data);			

			// Update queue appropriately
			if ( hasChanged ) {
				q.addAll(Arrays.asList(e.out));
			}
		}
		return numComputations;
	}
	
	private boolean flowThrough(Entry<N,A> d) {
		if (d.isStronglyConnected) {
			A out = newInitialFlow();
			flowThrough(d.inFlow, d.data, out);
			if (out.equals(d.outFlow)) {
				return false;
			}
			// copy back the result, as it has changed
			copy(out, d.outFlow);
			return true;
		}			
		
		flowThrough(d.inFlow, d.data, d.outFlow);
		return true;
	}
	
}
