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
  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    final Chain<Local> locals = b.getLocals();

    // Sigh. check for name collisions.
    Set<String> localNames = new HashSet<String>(locals.size());
    for (Local loc : locals) {
      localNames.add(loc.getName());
    }

    final SideEffectTester sideEffect;
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

    int counter = 0;
    final Chain<Unit> units = b.getUnits();
    for (Iterator<Unit> unitsIt = units.snapshotIterator(); unitsIt.hasNext();) {
      Unit u = unitsIt.next();
      if (u instanceof AssignStmt) {
        // logger.debug("availExprs: "+availExprs);
        Value v = ((AssignStmt) u).getRightOp();
        EquivalentValue ev = new EquivalentValue(v);

        if (ae.getAvailableEquivsBefore(u).contains(ev)) {
          // now we need to track down the containing stmt.
          for (UnitValueBoxPair up : ae.getAvailablePairsBefore(u)) {
            if (up.getValueBox().getValue().equivTo(v)) {
              // create a local for temp storage.
              // (we could check to see that the def must-reach, I guess...)
              String newName;
              do {
                newName = "$cseTmp" + counter;
                counter++;
              } while (localNames.contains(newName));

              Local l = Jimple.v().newLocal(newName, Type.toMachineType(v.getType()));
              locals.add(l);

              // I hope it's always an AssignStmt -- Jimple should guarantee this.
              AssignStmt origCalc = (AssignStmt) up.getUnit();
              Unit copier = Jimple.v().newAssignStmt(origCalc.getLeftOp(), l);

              origCalc.setLeftOp(l);

              units.insertAfter(copier, origCalc);

              ((AssignStmt) u).setRightOp(l);
              copier.addTag(new StringTag("Common sub-expression"));
              u.addTag(new StringTag("Common sub-expression"));
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
