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
 * to calculate it).
 * A computation is isolated, if it can only be used at the current
 * computation-point. In other words: if the result of the computation will not
 * be used later on the computation is isolated.<br>
 * The Latest-analysis helps us in finding isolated computations, as they
 * show us points, where a precedent computation can't be used anymore.
 * In completely other words: we search the interval "latest"-"computation". a
 * computation in this interval would not be isolated.
 */
public class NotIsolatedAnalysis extends BackwardFlowAnalysis {
  private LatestComputation unitToLatest;
  private Map unitToGen;
  private FlowSet set;

  /**
   * This constructor should not be used, and will throw a runtime-exception!
   */
  public NotIsolatedAnalysis(DirectedGraph dg) {
    /* we have to add super(dg). otherwise Javac complains. */
    super(dg);
    throw new RuntimeException("Don't use this Constructor!");
  }

  /**
   * Automatically performs the Isolation-analysis on the graph
   * <code>dg</code> using the Latest-computation <code>latest</code>.<br>
   * the <code>equivRhsMap</code> is only here to avoid doing these things
   * again...
   *
   * @param dg a ExceptionalUnitGraph
   * @param latest the latest-computation of the same graph.
   * @param equivRhsMap the rhs of each unit (if assignment-stmt).
   */
  public NotIsolatedAnalysis(DirectedGraph dg, LatestComputation latest,
      Map equivRhsMap) {
    this(dg, latest, equivRhsMap, new
      ArrayPackedSet(new CollectionFlowUniverse(equivRhsMap.values())));
  }

  /**
   * Automatically performs the Isolation-analysis on the graph
   * <code>dg</code> using the Latest-computation <code>latest</code>.<br>
   * the <code>equivRhsMap</code> is only here to avoid doing these things
   * again...<br>
   * the shared set allows more efficient set-operations, when this analysis is
   * joined with other analyses/computations.
   *
   * @param dg a ExceptionalUnitGraph
   * @param latest the latest-computation of the same graph.
   * @param equivRhsMap the rhs of each unit (if assignment-stmt).
   * @param set the shared set.
   */
  public NotIsolatedAnalysis(DirectedGraph dg, LatestComputation latest,
      Map equivRhsMap, BoundedFlowSet set) {
    super(dg);
    this.set = set;
    unitToGen = equivRhsMap;
    unitToLatest = latest;
    doAnalysis();
  }

  protected Object newInitialFlow() {
    return set.emptySet();
  }

  protected Object entryInitialFlow() {
    return newInitialFlow();
  }

  protected void flowThrough(Object inValue, Object unit, Object outValue) {

    FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

    in.copy(out);

    // Perform generation
    EquivalentValue rhs = (EquivalentValue)unitToGen.get(unit);
    if (rhs != null)
      out.add(rhs);

    // perform kill
    FlowSet latest = (FlowSet)unitToLatest.getFlowBefore(unit);
    out.difference(latest);
  }

  protected void merge(Object in1, Object in2, Object out) {
    FlowSet inSet1 = (FlowSet) in1;
    FlowSet inSet2 = (FlowSet) in2;

    FlowSet outSet = (FlowSet) out;

    inSet1.union(inSet2, outSet);
  }

  protected void copy(Object source, Object dest) {
    FlowSet sourceSet = (FlowSet) source;
    FlowSet destSet = (FlowSet) dest;

    sourceSet.copy(destSet);
  }
}
