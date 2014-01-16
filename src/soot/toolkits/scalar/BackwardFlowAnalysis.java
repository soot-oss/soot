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

import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;

/**
 * Abstract class that provides the fixed point iteration functionality required
 * by all BackwardFlowAnalyses.
 */
public abstract class BackwardFlowAnalysis<N, A> extends FlowAnalysis<N, A> {
	/**
	 * Construct the analysis from a DirectedGraph representation of a Body.
	 */
	public BackwardFlowAnalysis(DirectedGraph<N> graph) {
		super(graph);
	}

	protected boolean isForward() {
		return false;
	}

	protected void doAnalysis() {
		final boolean interactiveMode = Options.v().interactive_mode();

		List<N> orderedUnits = constructOrderer().newList(graph, true);

		final int n = orderedUnits.size();

		BitSet tail = new BitSet();
		BitSet work = new BitSet(n);
		work.set(0, n);

		final Map<N, Integer> index = new IdentityHashMap<N, Integer>(n * 2 + 1);
		{
			int i = 0;
			for (N s : orderedUnits) {
				index.put(s, i++);

				// Set initial Flows
				unitToBeforeFlow.put(s, newInitialFlow());
				unitToAfterFlow.put(s, newInitialFlow());
			}
		}

		// Feng Qian: March 07, 2002
		// init entry points
		for (N s : graph.getTails()) {
			tail.set(index.get(s));

			// this is a backward flow analysis
			unitToAfterFlow.put(s, entryInitialFlow());
		}

		// int numComputations = 0;

		// Perform fixed point flow analysis
		{
			A previousFlow = newInitialFlow();

			for (int i = work.nextSetBit(0); i >= 0; i = work.nextSetBit(i + 1)) {
				work.clear(i);
				N s = orderedUnits.get(i);

				A afterFlow = unitToAfterFlow.get(s);				

				// Compute and store afterFlow
				{
					final Iterator<N> it = graph.getSuccsOf(s).iterator();

					if (it.hasNext()) {
						copy(unitToBeforeFlow.get(it.next()), afterFlow);

						while (it.hasNext()) {
							mergeInto(s, afterFlow, unitToBeforeFlow.get(it.next()));
						}

						if (tail.get(i)) {
							mergeInto(s, afterFlow, entryInitialFlow());
						}
					}
				}
				
				A beforeFlow = unitToBeforeFlow.get(s);
				copy(beforeFlow, previousFlow);
				
				// Compute beforeFlow and store it.
				if (interactiveMode) {
					afterFlowThrough(s, afterFlow, true);
					flowThrough(afterFlow, s, beforeFlow);
					beforeFlowThrough(s, beforeFlow, false);
				} else {
					flowThrough(afterFlow, s, beforeFlow);
				}
				
				boolean hasChanged = !previousFlow.equals(beforeFlow);

				// Update queue appropriately
				if ( hasChanged ) {
					for (N v : graph.getPredsOf(s)) {
						int j = index.get(v);
						work.set(j);
						i = Math.min(i, j-1);
					}
				}

				// numComputations++;
			}
		}

		// Timers.v().totalFlowNodes += n;
		// Timers.v().totalFlowComputations += numComputations;
	}
}

