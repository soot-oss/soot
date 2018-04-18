/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *       based on FastAvailableExpressionsAnalysis from Patrick Lam.
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
import soot.jimple.*;
import java.util.*;

/**
 * Performs an DownSafe-analysis on the given graph. An expression is downsafe,
 * if the computation will occur on every path from the current point down to
 * the END.
 */
public class DownSafetyAnalysis extends BackwardFlowAnalysis<Unit, FlowSet<EquivalentValue>> {
	private SideEffectTester sideEffect = null;

	private Map<Unit, EquivalentValue> unitToGenerateMap;

	private BoundedFlowSet<EquivalentValue> set;

	/**
	 * This constructor should not be used, and will throw a runtime-exception!
	 */
	public DownSafetyAnalysis(DirectedGraph<Unit> dg) {
		/* we have to add super(dg). otherwise Javac complains. */
		super(dg);
		throw new RuntimeException("Don't use this Constructor!");
	}

	/**
	 * This constructor automatically performs the DownSafety-analysis.<br>
	 * the result of the analysis is as usual in FlowBefore (getFlowBefore())
	 * and FlowAfter (getFlowAfter()).<br>
	 *
	 * @param dg
	 *            a ExceptionalUnitGraph.
	 * @param unitToGen
	 *            the equivalentValue of each unit.
	 * @param sideEffect
	 *            the SideEffectTester that performs kills.
	 */
	public DownSafetyAnalysis(DirectedGraph<Unit> dg, Map<Unit, EquivalentValue> unitToGen, SideEffectTester sideEffect) {
		this(dg, unitToGen, sideEffect, new ArrayPackedSet<EquivalentValue>(
				new CollectionFlowUniverse<EquivalentValue>(unitToGen.values())));
	}

	/**
	 * This constructor automatically performs the DownSafety-analysis.<br>
	 * the result of the analysis is as usual in FlowBefore (getFlowBefore())
	 * and FlowAfter (getFlowAfter()).<br>
	 * as sets-operations are usually more efficient, if the original set comes
	 * from the same source, this allows to share sets.
	 *
	 * @param dg
	 *            a ExceptionalUnitGraph.
	 * @param unitToGen
	 *            the equivalentValue of each unit.
	 * @param sideEffect
	 *            the SideEffectTester that performs kills.
	 * @param BoundedFlowSet
	 *            the shared set.
	 */
	public DownSafetyAnalysis(DirectedGraph<Unit> dg, Map<Unit, EquivalentValue> unitToGen,
			SideEffectTester sideEffect, BoundedFlowSet<EquivalentValue> set) {
		super(dg);
		this.sideEffect = sideEffect;
		this.set = set;
		this.unitToGenerateMap = unitToGen;
		doAnalysis();
	}

	@Override
	protected FlowSet<EquivalentValue> newInitialFlow() {
		return set.topSet();
	}

	@Override
	protected FlowSet<EquivalentValue> entryInitialFlow() {
		return set.emptySet();
	}

	@Override
	protected void flowThrough(FlowSet<EquivalentValue> in, Unit u,
			FlowSet<EquivalentValue> out) {
		in.copy(out);

		{ /* Perform kill */

			Iterator<EquivalentValue> outIt = out.iterator();
			// iterate over things (avail) in out set.
			while (outIt.hasNext()) {
				EquivalentValue equiVal = outIt.next();
				Value avail = equiVal.getValue();
				if (avail instanceof FieldRef) {
					if (sideEffect.unitCanWriteTo(u, avail))
						outIt.remove();
				} else {
					Iterator<ValueBox> usesIt = avail.getUseBoxes().iterator();

					// iterate over uses in each avail.
					while (usesIt.hasNext()) {
						Value use = usesIt.next().getValue();
						if (sideEffect.unitCanWriteTo(u, use)) {
							outIt.remove();
							break;
						}
					}
				}
			}
		}

		// Perform generation
		EquivalentValue add = unitToGenerateMap.get(u);
		if (add != null)
			out.add(add, out);
	}

	@Override
	protected void merge(FlowSet<EquivalentValue> in1,
			FlowSet<EquivalentValue> in2,
			FlowSet<EquivalentValue> out) {
		in1.intersection(in2, out);
	}

	@Override
	protected void copy(FlowSet<EquivalentValue> source,
			FlowSet<EquivalentValue> dest) {
		source.copy(dest);
	}
}
