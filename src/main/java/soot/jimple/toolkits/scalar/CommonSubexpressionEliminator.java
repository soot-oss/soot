package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrick Lam
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.EquivalentValue;
import soot.G;
import soot.Local;
import soot.PhaseOptions;
import soot.Scene;
import soot.SideEffectTester;
import soot.Singletons;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.NaiveSideEffectTester;
import soot.jimple.Stmt;
import soot.jimple.toolkits.pointer.PASideEffectTester;
import soot.options.Options;
import soot.tagkit.StringTag;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

/**
 * Runs an available expressions analysis on a body, then eliminates common subexpressions.
 *
 * This implementation is especially slow, as it does not run on basic blocks. A better implementation (which wouldn't catch
 * every single cse, but would get most) would use basic blocks instead.
 *
 * It is also slow because the flow universe is explicitly created; it need not be. A better implementation would implicitly
 * compute the kill sets at every node.
 */

public class CommonSubexpressionEliminator extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(CommonSubexpressionEliminator.class);

  public CommonSubexpressionEliminator(Singletons.Global g) {
  }

  public static CommonSubexpressionEliminator v() {
    return G.v().soot_jimple_toolkits_scalar_CommonSubexpressionEliminator();
  }

  /** Common subexpression eliminator. */
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    int counter = 0;

    // Sigh. check for name collisions.
    Iterator<Local> localsIt = b.getLocals().iterator();
    Set<String> localNames = new HashSet<String>(b.getLocals().size());
    while (localsIt.hasNext()) {
      localNames.add((localsIt.next()).getName());
    }

    SideEffectTester sideEffect;
    if (Scene.v().hasCallGraph() && !PhaseOptions.getBoolean(options, "naive-side-effect")) {
      sideEffect = new PASideEffectTester();
    } else {
      sideEffect = new NaiveSideEffectTester();
    }
    sideEffect.newMethod(b.getMethod());

    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     Eliminating common subexpressions "
          + (sideEffect instanceof NaiveSideEffectTester ? "(naively)" : "") + "...");
    }

    AvailableExpressions ae = // new SlowAvailableExpressions(b);
        new FastAvailableExpressions(b, sideEffect);

    Chain<Unit> units = b.getUnits();
    Iterator<Unit> unitsIt = units.snapshotIterator();
    while (unitsIt.hasNext()) {
      Stmt s = (Stmt) unitsIt.next();

      if (s instanceof AssignStmt) {
        Chain availExprs = ae.getAvailableEquivsBefore(s);
        // logger.debug("availExprs: "+availExprs);
        Value v = ((AssignStmt) s).getRightOp();
        EquivalentValue ev = new EquivalentValue(v);

        if (availExprs.contains(ev)) {
          // now we need to track down the containing stmt.
          List availPairs = ae.getAvailablePairsBefore(s);
          // logger.debug("availPairs: "+availPairs);
          Iterator availIt = availPairs.iterator();
          while (availIt.hasNext()) {
            UnitValueBoxPair up = (UnitValueBoxPair) availIt.next();
            if (up.getValueBox().getValue().equivTo(v)) {
              // create a local for temp storage.
              // (we could check to see that the def must-reach, I guess...)
              String newName = "$cseTmp" + counter;
              counter++;

              while (localNames.contains(newName)) {
                newName = "$cseTmp" + counter;
                counter++;
              }

              Local l = Jimple.v().newLocal(newName, Type.toMachineType(v.getType()));

              b.getLocals().add(l);

              // I hope it's always an AssignStmt -- Jimple should guarantee this.
              AssignStmt origCalc = (AssignStmt) up.getUnit();
              Value origLHS = origCalc.getLeftOp();

              origCalc.setLeftOp(l);

              Unit copier = Jimple.v().newAssignStmt(origLHS, l);
              units.insertAfter(copier, origCalc);

              ((AssignStmt) s).setRightOp(l);
              copier.addTag(new StringTag("Common sub-expression"));
              s.addTag(new StringTag("Common sub-expression"));
              // logger.debug("added tag to : "+copier);
              // logger.debug("added tag to : "+s);
            }
          }
        }
      }
    }
    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     Eliminating common subexpressions done!");
    }
  }
}
