package soot.jimple.toolkits.scalar.pre;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.EquivalentValue;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.SideEffectTester;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.NaiveSideEffectTester;
import soot.jimple.toolkits.graph.CriticalEdgeRemover;
import soot.jimple.toolkits.graph.LoopConditionUnroller;
import soot.jimple.toolkits.pointer.PASideEffectTester;
import soot.jimple.toolkits.scalar.LocalCreation;
import soot.options.LCMOptions;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArrayPackedSet;
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.CollectionFlowUniverse;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.FlowUniverse;
import soot.util.Chain;
import soot.util.UnitMap;

/**
 * Performs a partial redundancy elimination (= code motion). This is done, by introducing helper-vars, that store an already
 * computed value, or if a computation only arrives partially (not from all predecessors) inserts a new computation on these
 * paths afterwards).
 * <p>
 *
 * In order to catch every redundant expression, this transformation must be done on a graph without critical edges.
 * Therefore the first thing we do, is removing them. A subsequent pass can then easily remove the synthetic nodes we have
 * introduced.
 * <p>
 *
 * The term "lazy" refers to the fact, that we move computations only if necessary.
 *
 * @see soot.jimple.toolkits.graph.CriticalEdgeRemover
 */
public class LazyCodeMotion extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(LazyCodeMotion.class);

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

    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "] Performing Lazy Code Motion...");
    }

    if (options.unroll()) {
      new LoopConditionUnroller().transform(b, phaseName + ".lcu");
    }

    CriticalEdgeRemover.v().transform(b, phaseName + ".cer");

    UnitGraph graph = new BriefUnitGraph(b);

    /* map each unit to its RHS. only take binary expressions */
    Map<Unit, EquivalentValue> unitToEquivRhs = new UnitMap<EquivalentValue>(b, graph.size() + 1, 0.7f) {
      protected EquivalentValue mapTo(Unit unit) {
        Value tmp = SootFilter.noInvokeRhs(unit);
        Value tmp2 = SootFilter.binop(tmp);
        if (tmp2 == null) {
          tmp2 = SootFilter.concreteRef(tmp);
        }
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

    if (options.safety() == LCMOptions.safety_safe) {
      upSafe = new UpSafetyAnalysis(graph, unitToNoExceptionEquivRhs, sideEffect, set);
    } else {
      upSafe = new UpSafetyAnalysis(graph, unitToEquivRhs, sideEffect, set);
    }

    if (options.safety() == LCMOptions.safety_unsafe) {
      downSafe = new DownSafetyAnalysis(graph, unitToEquivRhs, sideEffect, set);
    } else {
      downSafe = new DownSafetyAnalysis(graph, unitToNoExceptionEquivRhs, sideEffect, set);
      /* we include the exception-throwing expressions at their uses */
      Iterator<Unit> unitIt = unitChain.iterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = unitIt.next();
        EquivalentValue rhs = unitToEquivRhs.get(currentUnit);
        if (rhs != null) {
          downSafe.getFlowBefore(currentUnit).add(rhs);
        }
      }
    }

    earliest = new EarliestnessComputation(graph, upSafe, downSafe, sideEffect, set);
    delay = new DelayabilityAnalysis(graph, earliest, unitToEquivRhs, set);
    latest = new LatestComputation(graph, delay, unitToEquivRhs, set);
    notIsolated = new NotIsolatedAnalysis(graph, latest, unitToEquivRhs, set);

    LocalCreation localCreation = new LocalCreation(b.getLocals(), PREFIX);

    /* debug */
    /*
     * { logger.debug("========" + b.getMethod().getName()); Iterator unitIt = unitChain.iterator(); while (unitIt.hasNext())
     * { Unit currentUnit = (Unit) unitIt.next(); Value equiVal = (Value)unitToEquivRhs.get(currentUnit); FlowSet latestSet =
     * (FlowSet)latest.getFlowBefore(currentUnit); FlowSet notIsolatedSet = (FlowSet)notIsolated.getFlowAfter(currentUnit);
     * FlowSet delaySet = (FlowSet)delay.getFlowBefore(currentUnit); FlowSet earlySet =
     * ((FlowSet)earliest.getFlowBefore(currentUnit)); FlowSet upSet = (FlowSet)upSafe.getFlowBefore(currentUnit); FlowSet
     * downSet = (FlowSet)downSafe.getFlowBefore(currentUnit); logger.debug(""+currentUnit); logger.debug(" rh: " + equiVal);
     * logger.debug(" up: " + upSet); logger.debug(" do: " + downSet); logger.debug(" is: " + notIsolatedSet);
     * logger.debug(" ea: " + earlySet); logger.debug(" db: " + delaySet); logger.debug(" la: " + latestSet); } }
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
              if (helper != null) {
                ((AssignStmt) currentUnit).setRightOp(helper);
              }
            } catch (RuntimeException e) {
              logger.debug("Error on " + b.getMethod().getName());
              logger.debug("" + currentUnit.toString());

              logger.debug("" + latestSet);

              logger.debug("" + notIsolatedSet);

              throw e;
            }
          }
        }
      }
    }
    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     Lazy Code Motion done.");
    }
  }
}
