/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
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
 * Modified by the Sable Research Group and others 1997-2002.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

/**
 * Performs a Not-Isolated-analysis on the given graph, which is basically the
 * same as an Isolated-analysis (we just return the complement, as it's easier
 * to calculate it). A computation is isolated, if it can only be used at the
 * current computation-point. In other words: if the result of the computation
 * will not be used later on the computation is isolated.<br>
 * The Latest-analysis helps us in finding isolated computations, as they show
 * us points, where a precedent computation can't be used anymore. In completely
 * other words: we search the interval "latest"-"computation". a computation in
 * this interval would not be isolated.
 */
public class NotIsolatedAnalysis extends BackwardFlowAnalysis<Unit, FlowSet<EquivalentValue>> {
	private LatestComputation unitToLatest;
	private Map<Unit, EquivalentValue> unitToGen;
	private FlowSet<EquivalentValue> set;
	
	/**
	 * Automatically performs the Isolation-analysis on the graph
	 * <code>dg</code> using the Latest-computation <code>latest</code>.<br>
	 * the <code>equivRhsMap</code> is only here to avoid doing these things
	 * again...
	 *
	 * @param dg
	 *            a ExceptionalUnitGraph
	 * @param latest
	 *            the latest-computation of the same graph.
	 * @param equivRhsMap
	 *            the rhs of each unit (if assignment-stmt).
	 */
	public NotIsolatedAnalysis(DirectedGraph<Unit> dg,
			LatestComputation latest,
			Map<Unit, EquivalentValue> equivRhsMap) {
		this(dg, latest, equivRhsMap, new ArrayPackedSet<EquivalentValue>(
				new CollectionFlowUniverse<EquivalentValue>(equivRhsMap.values())));
	}

	/**
	 * Automatically performs the Isolation-analysis on the graph
	 * <code>dg</code> using the Latest-computation <code>latest</code>.<br>
	 * the <code>equivRhsMap</code> is only here to avoid doing these things
	 * again...<br>
	 * the shared set allows more efficient set-operations, when this analysis
	 * is joined with other analyses/computations.
	 *
	 * @param dg
	 *            a ExceptionalUnitGraph
	 * @param latest
	 *            the latest-computation of the same graph.
	 * @param equivRhsMap
	 *            the rhs of each unit (if assignment-stmt).
	 * @param set
	 *            the shared set.
	 */
	public NotIsolatedAnalysis(DirectedGraph<Unit> dg, LatestComputation latest,
			Map<Unit, EquivalentValue> equivRhsMap,
			BoundedFlowSet<EquivalentValue> set) {
		super(dg);
		this.set = set;
		unitToGen = equivRhsMap;
		unitToLatest = latest;
		doAnalysis();
	}

	@Override
	protected FlowSet<EquivalentValue> newInitialFlow() {
		return set.emptySet();
	}

	@Override
	protected FlowSet<EquivalentValue> entryInitialFlow() {
		return newInitialFlow();
	}

	@Override
	protected void flowThrough(FlowSet<EquivalentValue> in, Unit unit,
			FlowSet<EquivalentValue> out) {
		in.copy(out);

		// Perform generation
		EquivalentValue rhs = (EquivalentValue) unitToGen.get(unit);
		if (rhs != null)
			out.add(rhs);

		// perform kill
		FlowSet<EquivalentValue> latest = unitToLatest.getFlowBefore(unit);
		out.difference(latest);
	}

	@Override
	protected void merge(FlowSet<EquivalentValue> inSet1,
			FlowSet<EquivalentValue> inSet2,
			FlowSet<EquivalentValue> outSet) {
		inSet1.union(inSet2, outSet);
	}

	@Override
	protected void copy(FlowSet<EquivalentValue> sourceSet,
			FlowSet<EquivalentValue> destSet) {
		sourceSet.copy(destSet);
	}
}
