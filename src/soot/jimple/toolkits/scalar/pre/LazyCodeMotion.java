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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.scalar.pre;

import soot.options.*;
import soot.jimple.toolkits.graph.*;
import soot.jimple.toolkits.scalar.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.jimple.toolkits.pointer.PASideEffectTester;
import soot.options.LCMOptions;

/**
 * Performs a partial redundancy elimination (= code motion). This is done, by
 * introducing helper-vars, that store an already computed value, or if a
 * computation only arrives partially (not from all predecessors) inserts a new
 * computation on these paths afterwards).
 * <p>
 *
 * In order to catch every redundant expression, this transformation must be
 * done on a graph without critical edges. Therefore the first thing we do, is
 * removing them. A subsequent pass can then easily remove the synthetic nodes
 * we have introduced.
 * <p>
 *
 * The term "lazy" refers to the fact, that we move computations only if
 * necessary.
 *
 * @see soot.jimple.toolkits.graph.CriticalEdgeRemover
 */
public class LazyCodeMotion extends BodyTransformer {
	public LazyCodeMotion(Singletons.Global g) {
	}

	public static LazyCodeMotion v() {
		return G.v().soot_jimple_toolkits_scalar_pre_LazyCodeMotion();
	}

	private static final String PREFIX = "$lcm";

	/**
	 * performs the lazy code motion.
	 */
	protected void internalTransform(Body b, String phaseName, Map<String, String> opts) {
		LCMOptions options = new LCMOptions(opts);
		HashMap<EquivalentValue, Local> expToHelper = new HashMap<EquivalentValue, Local>();
		Chain<Unit> unitChain = b.getUnits();

		if (Options.v().verbose())
			G.v().out.println("[" + b.getMethod().getName() + "] Performing Lazy Code Motion...");

		if (options.unroll())
			new LoopConditionUnroller().transform(b, phaseName + ".lcu");

		CriticalEdgeRemover.v().transform(b, phaseName + ".cer");

		UnitGraph graph = new BriefUnitGraph(b);

		/* map each unit to its RHS. only take binary expressions */
		Map<Unit, EquivalentValue> unitToEquivRhs = new UnitMap<EquivalentValue>(b, graph.size() + 1, 0.7f) {
			protected EquivalentValue mapTo(Unit unit) {
				Value tmp = SootFilter.noInvokeRhs(unit);
				Value tmp2 = SootFilter.binop(tmp);
				if (tmp2 == null)
					tmp2 = SootFilter.concreteRef(tmp);
				return SootFilter.equiVal(tmp2);
			}
		};

		/* same as before, but without exception-throwing expressions */
		Map<Unit, EquivalentValue> unitToNoExceptionEquivRhs = new UnitMap<EquivalentValue>(b, graph.size() + 1, 0.7f) {
			protected EquivalentValue mapTo(Unit unit) {
				Value tmp = SootFilter.binopRhs(unit);
				tmp = SootFilter.noExceptionThrowing(tmp);
				return SootFilter.equiVal(tmp);
			}
		};

		FlowUniverse<EquivalentValue> universe = new CollectionFlowUniverse<EquivalentValue>(unitToEquivRhs.values());
		BoundedFlowSet<EquivalentValue> set = new ArrayPackedSet<EquivalentValue>(universe);

		/* if a more precise sideeffect-tester comes out, please change it here! */
		SideEffectTester sideEffect;
		if (Scene.v().hasCallGraph() && !options.naive_side_effect()) {
			sideEffect = new PASideEffectTester();
		} else {
			sideEffect = new NaiveSideEffectTester();
		}
		sideEffect.newMethod(b.getMethod());
		UpSafetyAnalysis upSafe;
		DownSafetyAnalysis downSafe;
		EarliestnessComputation earliest;
		DelayabilityAnalysis delay;
		NotIsolatedAnalysis notIsolated;
		LatestComputation latest;

		if (options.safety() == LCMOptions.safety_safe)
			upSafe = new UpSafetyAnalysis(graph, unitToNoExceptionEquivRhs, sideEffect, set);
		else
			upSafe = new UpSafetyAnalysis(graph, unitToEquivRhs, sideEffect, set);

		if (options.safety() == LCMOptions.safety_unsafe)
			downSafe = new DownSafetyAnalysis(graph, unitToEquivRhs, sideEffect, set);
		else {
			downSafe = new DownSafetyAnalysis(graph, unitToNoExceptionEquivRhs, sideEffect, set);
			/* we include the exception-throwing expressions at their uses */
			Iterator<Unit> unitIt = unitChain.iterator();
			while (unitIt.hasNext()) {
				Unit currentUnit = unitIt.next();
				EquivalentValue rhs = unitToEquivRhs.get(currentUnit);
				if (rhs != null)
					downSafe.getFlowBefore(currentUnit).add(rhs);
			}
		}

		earliest = new EarliestnessComputation(graph, upSafe, downSafe, sideEffect, set);
		delay = new DelayabilityAnalysis(graph, earliest, unitToEquivRhs, set);
		latest = new LatestComputation(graph, delay, unitToEquivRhs, set);
		notIsolated = new NotIsolatedAnalysis(graph, latest, unitToEquivRhs, set);

		LocalCreation localCreation = new LocalCreation(b.getLocals(), PREFIX);

		/* debug */
		/*
		 * { G.v().out.println("========" + b.getMethod().getName()); Iterator
		 * unitIt = unitChain.iterator(); while (unitIt.hasNext()) { Unit
		 * currentUnit = (Unit) unitIt.next(); Value equiVal =
		 * (Value)unitToEquivRhs.get(currentUnit); FlowSet latestSet =
		 * (FlowSet)latest.getFlowBefore(currentUnit); FlowSet notIsolatedSet =
		 * (FlowSet)notIsolated.getFlowAfter(currentUnit); FlowSet delaySet =
		 * (FlowSet)delay.getFlowBefore(currentUnit); FlowSet earlySet =
		 * ((FlowSet)earliest.getFlowBefore(currentUnit)); FlowSet upSet =
		 * (FlowSet)upSafe.getFlowBefore(currentUnit); FlowSet downSet =
		 * (FlowSet)downSafe.getFlowBefore(currentUnit);
		 * G.v().out.println(currentUnit); G.v().out.println(" rh: " + equiVal);
		 * G.v().out.println(" up: " + upSet); G.v().out.println(" do: " +
		 * downSet); G.v().out.println(" is: " + notIsolatedSet);
		 * G.v().out.println(" ea: " + earlySet); G.v().out.println(" db: " +
		 * delaySet); G.v().out.println(" la: " + latestSet); } }
		 */

		{ /* insert the computations */
			for (Iterator<Unit> unitIt = unitChain.snapshotIterator(); unitIt.hasNext();) {
				Unit currentUnit = unitIt.next();
				FlowSet<EquivalentValue> latestSet = latest.getFlowBefore(currentUnit);
				FlowSet<EquivalentValue> notIsolatedSet = notIsolated.getFlowAfter(currentUnit);
				FlowSet<EquivalentValue> insertHere = latestSet.clone();
				insertHere.intersection(notIsolatedSet, insertHere);

				for (EquivalentValue equiVal : insertHere) {
					/* get the unic helper-name for this expression */
					Local helper = expToHelper.get(equiVal);
					if (helper == null) {
						helper = localCreation.newLocal(equiVal.getType());
						expToHelper.put(equiVal, helper);
					}

					/* insert a new Assignment-stmt before the currentUnit */
					Value insertValue = Jimple.cloneIfNecessary(equiVal.getValue());
					Unit firstComp = Jimple.v().newAssignStmt(helper, insertValue);
					unitChain.insertBefore(firstComp, currentUnit);
				}
			}
		}

		{ /* replace old computations by the helper-vars */
			Iterator<Unit> unitIt = unitChain.iterator();
			while (unitIt.hasNext()) {
				Unit currentUnit = (Unit) unitIt.next();
				EquivalentValue rhs = (EquivalentValue) unitToEquivRhs.get(currentUnit);
				if (rhs != null) {
					FlowSet<EquivalentValue> latestSet = latest.getFlowBefore(currentUnit);
					FlowSet<EquivalentValue> notIsolatedSet = notIsolated.getFlowAfter(currentUnit);
					if (!latestSet.contains(rhs) && notIsolatedSet.contains(rhs)) {
						Local helper = expToHelper.get(rhs);

						try {
							if (helper != null)
								((AssignStmt) currentUnit).setRightOp(helper);
						} catch (RuntimeException e) {
							G.v().out.println("Error on " + b.getMethod().getName());
							G.v().out.println(currentUnit.toString());

							G.v().out.println(latestSet);

							G.v().out.println(notIsolatedSet);

							throw e;
						}
					}
				}
			}
		}
		if (Options.v().verbose())
			G.v().out.println("[" + b.getMethod().getName() + "]     Lazy Code Motion done.");
	}
}
