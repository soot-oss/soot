/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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
import soot.jimple.toolkits.scalar.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** 
 * Performs a Delayability-analysis on the given graph.<br>
 * This analysis is the third analysis in the PRE (lazy code motion) and has
 * little (no?) sense if used alone. Basicly it tries to push the computations
 * we would insert in the Busy Code Motion as far down as possible, to decrease
 * life-time ranges (clearly this is not true, if the computation "uses" two
 * variables and produces only one temporary).
 */
public class DelayabilityAnalysis extends ForwardFlowAnalysis {
  private HashMap unitToGenerateSet;
  private Map unitToKillValue;
  private FlowSet emptySet;

  /**
   * this constructor should not be used, and will throw a runtime-exception!
   */
  public DelayabilityAnalysis(DirectedGraph dg) {
    /* we have to add super(dg). otherwise Javac complains. */
    super(dg);
    throw new RuntimeException("Don't use this Constructor!");
  }

  /**
   * automaticly performs the Delayability-analysis on the graph
   * <code>dg</code> and the Earliest-computation <code>earliest</code>.<br>
   * the <code>equivRhsMap</code> is only here to avoid doing these things
   * again...
   *
   * @param dg a CompleteUnitGraph
   * @param earliest the earliest-computation of the <b>same</b> graph.
   * @param equivRhsMap the rhs of each unit (if assignment-stmt).
   */
  public DelayabilityAnalysis(DirectedGraph dg, EarliestnessComputation
      earliest, Map equivRhsMap) {
    super(dg);
    UnitGraph g = (UnitGraph)dg;
    emptySet = new ToppedSet(new ArraySparseSet());
    unitToKillValue = equivRhsMap;

    /* Create generate sets */
    {
      unitToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);

      Iterator unitIt = g.iterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        FlowSet genSet = (FlowSet)emptySet.clone();
        Iterator genIt = earliest.getEarliestBefore(currentUnit).iterator();
        while (genIt.hasNext())
          genSet.add(genIt.next(), genSet);
        unitToGenerateSet.put(currentUnit, genSet);
      }
    }
    doAnalysis();
    { // finally add the genSet to each BeforeFlow
      Iterator unitIt = g.iterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        FlowSet beforeSet = (FlowSet)getFlowBefore(currentUnit);
        beforeSet.union((FlowSet)unitToGenerateSet.get(currentUnit), beforeSet);
      }
    }
  }

  protected Object newInitialFlow() {
    Object newSet = emptySet.clone();
    ((ToppedSet)newSet).setTop(true);
    return newSet;
  }

  protected void customizeInitialFlowGraph() {
    // Initialize heads to {}
    Iterator headIt = graph.getHeads().iterator();
    while (headIt.hasNext()) {
      Object newSet = unitToBeforeFlow.get(headIt.next());
      ((ToppedSet)newSet).setTop(false);
    }
  }

  protected void flowThrough(Object inValue, Object unit, Object outValue) {
    FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

    in.copy(out);
    if (((ToppedSet)in).isTop())
      return;

    // Perform generation
    out.union((FlowSet) unitToGenerateSet.get(unit), out);

    /* should not be possible */
    if (((ToppedSet)out).isTop()) {
      throw new RuntimeException("trying to kill on topped set!");
    }

    { /* Perform kill */
      Unit u = (Unit)unit;
      EquivalentValue equiVal = (EquivalentValue)unitToKillValue.get(u);
      if (equiVal != null)
        out.remove(equiVal, out);
    }
  }

  protected void merge(Object in1, Object in2, Object out) {
    FlowSet inSet1 = (FlowSet) in1;
    FlowSet inSet2 = (FlowSet) in2;

    FlowSet outSet = (FlowSet) out;

    inSet1.intersection(inSet2, outSet);
  }

  protected void copy(Object source, Object dest) {
    FlowSet sourceSet = (FlowSet) source;
    FlowSet destSet = (FlowSet) dest;

    sourceSet.copy(destSet);
  }
}

