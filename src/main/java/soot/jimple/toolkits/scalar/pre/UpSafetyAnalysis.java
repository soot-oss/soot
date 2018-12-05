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
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.CollectionFlowUniverse;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * Performs an UpSafe-analysis on the given graph. An expression is upsafe, if the computation already has been performed on
 * every path from START to the given program-point.
 */
public class UpSafetyAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<EquivalentValue>> {
  private SideEffectTester sideEffect;

  private Map<Unit, EquivalentValue> unitToGenerateMap;

  private BoundedFlowSet<EquivalentValue> set;

  /**
   * This constructor should not be used, and will throw a runtime-exception!
   */
  public UpSafetyAnalysis(DirectedGraph<Unit> dg) {
    /* we have to add super(dg). otherwise Javac complains. */
    super(dg);
    throw new RuntimeException("Don't use this Constructor!");
  }

  /**
   * This constructor automatically performs the UpSafety-analysis.<br>
   * the result of the analysis is as usual in FlowBefore (getFlowBefore()) and FlowAfter (getFlowAfter()).<br>
   *
   * @param dg
   *          a ExceptionalUnitGraph
   * @param unitToGen
   *          the EquivalentValue of each unit.
   * @param sideEffect
   *          the SideEffectTester that will be used to perform kills.
   */
  public UpSafetyAnalysis(DirectedGraph<Unit> dg, Map<Unit, EquivalentValue> unitToGen, SideEffectTester sideEffect) {
    this(dg, unitToGen, sideEffect,
        new ArrayPackedSet<EquivalentValue>(new CollectionFlowUniverse<EquivalentValue>(unitToGen.values())));
  }

  /**
   * This constructor automatically performs the UpSafety-analysis.<br>
   * the result of the analysis is as usual in FlowBefore (getFlowBefore()) and FlowAfter (getFlowAfter()).<br>
   * As usually flowset-operations are more efficient if shared, this allows to share sets over several analyses.
   *
   * @param dg
   *          a ExceptionalUnitGraph
   * @param unitToGen
   *          the EquivalentValue of each unit.
   * @param sideEffect
   *          the SideEffectTester that will be used to perform kills.
   * @param set
   *          a bounded flow-set.
   */
  public UpSafetyAnalysis(DirectedGraph<Unit> dg, Map<Unit, EquivalentValue> unitToGen, SideEffectTester sideEffect,
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

    // Perform generation
    EquivalentValue add = unitToGenerateMap.get(u);
    if (add != null) {
      out.add(add, out);
    }

    { /* Perform kill */
      // iterate over things (avail) in out set.
      for (Iterator<EquivalentValue> outIt = out.iterator(); outIt.hasNext();) {
        EquivalentValue equiVal = outIt.next();
        Value avail = equiVal.getValue();
        if (avail instanceof FieldRef) {
          if (sideEffect.unitCanWriteTo(u, avail)) {
            outIt.remove();
          }
        } else {
          // iterate over uses in each avail.
          for (ValueBox useBox : avail.getUseBoxes()) {
            Value use = useBox.getValue();
            if (sideEffect.unitCanWriteTo(u, use)) {
              outIt.remove();
              break;
            }
          }
        }
      }
    }
  }

  @Override
  protected void merge(FlowSet<EquivalentValue> inSet1, FlowSet<EquivalentValue> inSet2, FlowSet<EquivalentValue> outSet) {
    inSet1.intersection(inSet2, outSet);
  }

  @Override
  protected void copy(FlowSet<EquivalentValue> sourceSet, FlowSet<EquivalentValue> destSet) {
    sourceSet.copy(destSet);
  }
}
