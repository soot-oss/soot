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
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.NaiveSideEffectTester;
import soot.jimple.toolkits.graph.CriticalEdgeRemover;
import soot.jimple.toolkits.pointer.PASideEffectTester;
import soot.jimple.toolkits.scalar.LocalCreation;
import soot.options.BCMOptions;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import soot.util.UnitMap;

/**
 * Performs a partial redundancy elimination (= code motion). This is done, by moving <b>every</b>computation as high as
 * possible (it is easy to show, that they are computationally optimal), and then replacing the original computation by a
 * reference to this new high computation. This implies, that we introduce <b>many</b> new helper-variables (that can easily
 * be eliminated afterwards).<br>
 * In order to catch every redundant expression, this transformation must be done on a graph without critical edges.
 * Therefore the first thing we do, is removing them. A subsequent pass can then easily remove the synthetic nodes we have
 * introduced.<br>
 * The term "busy" refers to the fact, that we <b>always</b> move computations as high as possible. Even, if this is not
 * necessary.
 *
 * @see soot.jimple.toolkits.graph.CriticalEdgeRemover
 */
public class BusyCodeMotion extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(BusyCodeMotion.class);

  private static final String PREFIX = "$bcm";

  public BusyCodeMotion(Singletons.Global g) {
  }

  public static BusyCodeMotion v() {
    return G.v().soot_jimple_toolkits_scalar_pre_BusyCodeMotion();
  }

  /**
   * performs the busy code motion.
   */
  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> opts) {
    final BCMOptions options = new BCMOptions(opts);

    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     performing Busy Code Motion...");
    }

    CriticalEdgeRemover.v().transform(b, phaseName + ".cer");

    UnitGraph graph = new BriefUnitGraph(b);

    /* Map each unit to its RHS. Take only BinopExpr and ConcreteRef */
    Map<Unit, EquivalentValue> unitToEquivRhs = new UnitMap<EquivalentValue>(b, graph.size() + 1, 0.7f) {
      @Override
      protected EquivalentValue mapTo(Unit unit) {
        Value tmp = SootFilter.noInvokeRhs(unit);
        Value tmp2 = SootFilter.binop(tmp);
        if (tmp2 == null) {
          tmp2 = SootFilter.concreteRef(tmp);
        }
        return SootFilter.equiVal(tmp2);
      }
    };

    /* Same as before, but without exception-throwing expressions */
    Map<Unit, EquivalentValue> unitToNoExceptionEquivRhs = new UnitMap<EquivalentValue>(b, graph.size() + 1, 0.7f) {
      @Override
      protected EquivalentValue mapTo(Unit unit) {
        return SootFilter.equiVal(SootFilter.noExceptionThrowing(SootFilter.binopRhs(unit)));
      }
    };

    final Scene sc = Scene.v();
    /* if a more precise sideeffect-tester comes out, please change it here! */
    final SideEffectTester sideEffect;
    if (sc.hasCallGraph() && !options.naive_side_effect()) {
      sideEffect = new PASideEffectTester();
    } else {
      sideEffect = new NaiveSideEffectTester();
    }
    sideEffect.newMethod(b.getMethod());

    final UpSafetyAnalysis upSafe = new UpSafetyAnalysis(graph, unitToEquivRhs, sideEffect);
    final DownSafetyAnalysis downSafe = new DownSafetyAnalysis(graph, unitToNoExceptionEquivRhs, sideEffect);
    final EarliestnessComputation earliest = new EarliestnessComputation(graph, upSafe, downSafe, sideEffect);
    final LocalCreation localCreation = sc.createLocalCreation(b.getLocals(), PREFIX);
    final HashMap<EquivalentValue, Local> expToHelper = new HashMap<EquivalentValue, Local>();

    Chain<Unit> unitChain = b.getUnits();

    /* insert the computations at the earliest positions */
    for (Iterator<Unit> unitIt = unitChain.snapshotIterator(); unitIt.hasNext();) {
      Unit currentUnit = unitIt.next();
      for (EquivalentValue equiVal : earliest.getFlowBefore(currentUnit)) {
        /* get the unic helper-name for this expression */
        Local helper = expToHelper.get(equiVal);
        if (helper == null) {
          helper = localCreation.newLocal(equiVal.getType());
          expToHelper.put(equiVal, helper);
        }

        // Make sure not to place any stuff inside the identity block at
        // the beginning of the method
        if (currentUnit instanceof IdentityStmt) {
          currentUnit = getFirstNonIdentityStmt(b);
        }

        /* insert a new Assignment-stmt before the currentUnit */
        Value insertValue = Jimple.cloneIfNecessary(equiVal.getValue());
        Unit firstComp = Jimple.v().newAssignStmt(helper, insertValue);
        unitChain.insertBefore(firstComp, currentUnit);
      }
    }

    /* replace old computations by the helper-vars */
    for (Unit currentUnit : unitChain) {
      EquivalentValue rhs = unitToEquivRhs.get(currentUnit);
      if (rhs != null) {
        Local helper = expToHelper.get(rhs);
        if (helper != null) {
          ((AssignStmt) currentUnit).setRightOp(helper);
        }
      }
    }

    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     Busy Code Motion done!");
    }
  }

  private Unit getFirstNonIdentityStmt(Body b) {
    for (Unit u : b.getUnits()) {
      if (!(u instanceof IdentityStmt)) {
        return u;
      }
    }
    return null;
  }
}
