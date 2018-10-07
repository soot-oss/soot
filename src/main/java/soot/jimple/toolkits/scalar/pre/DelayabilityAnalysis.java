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

import java.util.Map;

import soot.EquivalentValue;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArrayPackedSet;
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.CollectionFlowUniverse;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * Performs a Delayability-analysis on the given graph. This analysis is the third analysis in the PRE (lazy code motion) and
 * has little (no?) sense if used alone. Basically it tries to push the computations we would insert in the Busy Code Motion
 * as far down as possible, to decrease life-time ranges (clearly this is not true, if the computation "uses" two variables
 * and produces only one temporary).
 */
public class DelayabilityAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<EquivalentValue>> {
  private EarliestnessComputation earliest;
  private Map<Unit, EquivalentValue> unitToKillValue;
  private BoundedFlowSet<EquivalentValue> set;

  /**
   * this constructor should not be used, and will throw a runtime-exception!
   */
  public DelayabilityAnalysis(DirectedGraph<Unit> dg) {
    /* we have to add super(dg). otherwise Javac complains. */
    super(dg);
    throw new RuntimeException("Don't use this Constructor!");
  }

  /**
   * Automatically performs the Delayability-analysis on the graph <code>dg</code> and the Earliest-computation
   * <code>earliest</code>.<br>
   * the <code>equivRhsMap</code> is only here to avoid doing these things again...
   *
   * @param dg
   *          a ExceptionalUnitGraph
   * @param earliest
   *          the earliest-computation of the <b>same</b> graph.
   * @param equivRhsMap
   *          the rhs of each unit (if assignment-stmt).
   */
  public DelayabilityAnalysis(DirectedGraph<Unit> dg, EarliestnessComputation earliest,
      Map<Unit, EquivalentValue> equivRhsMap) {
    this(dg, earliest, equivRhsMap,
        new ArrayPackedSet<EquivalentValue>(new CollectionFlowUniverse<EquivalentValue>(equivRhsMap.values())));
  }

  /**
   * Automatically performs the Delayability-analysis on the graph <code>dg</code> and the Earliest-computation
   * <code>earliest</code>.<br>
   * the <code>equivRhsMap</code> is only here to avoid doing these things again...<br>
   * as set-operations are usually more efficient, if the sets come from one source, sets should be shared around analyses,
   * if the analyses are to be combined.
   *
   * @param dg
   *          a ExceptionalUnitGraph
   * @param earliest
   *          the earliest-computation of the <b>same</b> graph.
   * @param equivRhsMap
   *          the rhs of each unit (if assignment-stmt).
   * @param set
   *          the shared set.
   */
  public DelayabilityAnalysis(DirectedGraph<Unit> dg, EarliestnessComputation earliest,
      Map<Unit, EquivalentValue> equivRhsMap, BoundedFlowSet<EquivalentValue> set) {
    super(dg);
    UnitGraph g = (UnitGraph) dg;
    this.set = set;
    unitToKillValue = equivRhsMap;
    this.earliest = earliest;

    doAnalysis();
    { // finally add the genSet to each BeforeFlow
      for (Unit currentUnit : g) {
        FlowSet<EquivalentValue> beforeSet = getFlowBefore(currentUnit);
        beforeSet.union(earliest.getFlowBefore(currentUnit));
      }
    }
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
  protected void flowThrough(FlowSet<EquivalentValue> in, Unit u, FlowSet<EquivalentValue> out) {
    in.copy(out);

    // Perform generation
    out.union(earliest.getFlowBefore(u));

    { /* Perform kill */
      EquivalentValue equiVal = (EquivalentValue) unitToKillValue.get(u);
      if (equiVal != null) {
        out.remove(equiVal);
      }
    }
  }

  @Override
  protected void merge(FlowSet<EquivalentValue> inSet1, FlowSet<EquivalentValue> inSet2, FlowSet<EquivalentValue> outSet) {
    inSet1.intersection(inSet2, outSet);
  }

  protected void copy(FlowSet<EquivalentValue> sourceSet, FlowSet<EquivalentValue> destSet) {
    sourceSet.copy(destSet);
  }
}
