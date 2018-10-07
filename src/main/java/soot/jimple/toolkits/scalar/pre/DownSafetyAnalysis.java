package soot.jimple.toolkits.scalar.pre;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
 *       based on FastAvailableExpressionsAnalysis from Patrick Lam.
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

import java.util.Iterator;
import java.util.Map;

import soot.EquivalentValue;
import soot.SideEffectTester;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArrayPackedSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.CollectionFlowUniverse;
import soot.toolkits.scalar.FlowSet;

/**
 * Performs an DownSafe-analysis on the given graph. An expression is downsafe, if the computation will occur on every path
 * from the current point down to the END.
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
   * the result of the analysis is as usual in FlowBefore (getFlowBefore()) and FlowAfter (getFlowAfter()).<br>
   *
   * @param dg
   *          a ExceptionalUnitGraph.
   * @param unitToGen
   *          the equivalentValue of each unit.
   * @param sideEffect
   *          the SideEffectTester that performs kills.
   */
  public DownSafetyAnalysis(DirectedGraph<Unit> dg, Map<Unit, EquivalentValue> unitToGen, SideEffectTester sideEffect) {
    this(dg, unitToGen, sideEffect,
        new ArrayPackedSet<EquivalentValue>(new CollectionFlowUniverse<EquivalentValue>(unitToGen.values())));
  }

  /**
   * This constructor automatically performs the DownSafety-analysis.<br>
   * the result of the analysis is as usual in FlowBefore (getFlowBefore()) and FlowAfter (getFlowAfter()).<br>
   * as sets-operations are usually more efficient, if the original set comes from the same source, this allows to share
   * sets.
   *
   * @param dg
   *          a ExceptionalUnitGraph.
   * @param unitToGen
   *          the equivalentValue of each unit.
   * @param sideEffect
   *          the SideEffectTester that performs kills.
   * @param BoundedFlowSet
   *          the shared set.
   */
  public DownSafetyAnalysis(DirectedGraph<Unit> dg, Map<Unit, EquivalentValue> unitToGen, SideEffectTester sideEffect,
      BoundedFlowSet<EquivalentValue> set) {
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
  protected void flowThrough(FlowSet<EquivalentValue> in, Unit u, FlowSet<EquivalentValue> out) {
    in.copy(out);

    { /* Perform kill */

      Iterator<EquivalentValue> outIt = out.iterator();
      // iterate over things (avail) in out set.
      while (outIt.hasNext()) {
        EquivalentValue equiVal = outIt.next();
        Value avail = equiVal.getValue();
        if (avail instanceof FieldRef) {
          if (sideEffect.unitCanWriteTo(u, avail)) {
            outIt.remove();
          }
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
    if (add != null) {
      out.add(add, out);
    }
  }

  @Override
  protected void merge(FlowSet<EquivalentValue> in1, FlowSet<EquivalentValue> in2, FlowSet<EquivalentValue> out) {
    in1.intersection(in2, out);
  }

  @Override
  protected void copy(FlowSet<EquivalentValue> source, FlowSet<EquivalentValue> dest) {
    source.copy(dest);
  }
}
