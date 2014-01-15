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

import soot.Timers;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;

/**
 * Abstract class that provides the fixed point iteration functionality required
 * by all ForwardFlowAnalyses.
 */
public abstract class ForwardFlowAnalysis<N, A> extends FlowAnalysis<N, A> {
	/**
	 * Construct the analysis from a DirectedGraph representation of a Body.
	 */
	public ForwardFlowAnalysis(DirectedGraph<N> graph) {
		super(graph);
	}

	protected boolean isForward() {
		return true;
	}

	protected void doAnalysis() {
		final boolean interactiveMode = Options.v().interactive_mode();

		List<N> orderedUnits = constructOrderer().newList(graph, false);

		final int n = orderedUnits.size();
		BitSet head = new BitSet();
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
		// Set initial values for entry points
		for (N s : graph.getHeads()) {
			head.set(index.get(s));

			// this is a forward flow analysis
			unitToBeforeFlow.put(s, entryInitialFlow());
		}

		int numComputations = 0;

		// Perform fixed point flow analysis
		{
			A previousFlow = newInitialFlow();

			for (int i = work.nextSetBit(0); i >= 0; i = work.nextSetBit(i + 1)) {
				work.clear(i);
				N s = orderedUnits.get(i);

				A beforeFlow = unitToBeforeFlow.get(s);

				// Compute and store beforeFlow
				{
					final Iterator<N> it = graph.getPredsOf(s).iterator();

					if (it.hasNext()) {
						copy(unitToAfterFlow.get(it.next()), beforeFlow);

						while (it.hasNext()) {
							mergeInto(s, beforeFlow, unitToAfterFlow.get(it.next()));
						}

						if (head.get(i)) {
							mergeInto(s, beforeFlow, entryInitialFlow());
						}
					}
				}

				A afterFlow = unitToAfterFlow.get(s);
				copy(afterFlow, previousFlow);
				
				// Compute afterFlow and store it.
				if (interactiveMode) {
					beforeFlowThrough(s, beforeFlow, true);
					flowThrough(beforeFlow, s, afterFlow);
					afterFlowThrough(s, afterFlow, false);
				} else {
					flowThrough(beforeFlow, s, afterFlow);
				}

				boolean hasChanged = !previousFlow.equals(afterFlow);
				
				// Update queue appropriately
				if ( hasChanged ) {
					for (N v : graph.getSuccsOf(s)) {
						int j = index.get(v);
						work.set(j);
						i = Math.min(i, j-1);
					}
				}

				numComputations++;
			}
		}

		Timers.v().totalFlowNodes += n;
		Timers.v().totalFlowComputations += numComputations;
	}
}

