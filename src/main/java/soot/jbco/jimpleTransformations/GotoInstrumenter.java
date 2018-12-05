package soot.jbco.jimpleTransformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.Rand;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.util.Chain;

/**
 * Changes the sequence of statements in which they appear in methods, preserving the sequence they are executed using
 * {@code goto} commands and {@code labels}. Also if possible adds {@code try-catch} block in random position.
 *
 * @author Michael Batchelder
 * @since 15-Feb-2006
 */
public class GotoInstrumenter extends BodyTransformer implements IJbcoTransform {

  private static final Logger logger = LoggerFactory.getLogger(GotoInstrumenter.class);

  public static final String name = "jtp.jbco_gia";
  public static final String dependencies[] = new String[] { GotoInstrumenter.name };

  private int trapsAdded = 0;
  private int gotosInstrumented = 0;

  private static final UnitBox[] EMPTY_UNIT_BOX_ARRAY = new UnitBox[0];

  private static final int MAX_TRIES_TO_GET_REORDER_COUNT = 10;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String[] getDependencies() {
    return Arrays.copyOf(dependencies, dependencies.length);
  }

  @Override
  public void outputSummary() {
    logger.info("Instrumented {} GOTOs, added {} traps.", gotosInstrumented, trapsAdded);
  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    if (SootMethod.constructorName.equals(body.getMethod().getName())
        || SootMethod.staticInitializerName.equals(body.getMethod().getName())) {
      if (isVerbose()) {
        logger.info("Skipping {} method GOTO instrumentation as it is constructor/initializer.",
            body.getMethod().getSignature());
      }
      return;
    }

    if (soot.jbco.Main.getWeight(phaseName, body.getMethod().getSignature()) == 0) {
      return;
    }

    final PatchingChain<Unit> units = body.getUnits();

    int precedingFirstNotIdentityIndex = 0;
    Unit precedingFirstNotIdentity = null;
    for (Unit unit : units) {
      if (unit instanceof IdentityStmt) {
        precedingFirstNotIdentity = unit;
        precedingFirstNotIdentityIndex++;
        continue;
      }

      break;
    }

    final int unitsLeft = units.size() - precedingFirstNotIdentityIndex;
    if (unitsLeft < 8) {
      if (isVerbose()) {
        logger.info("Skipping {} method GOTO instrumentation as it is too small.", body.getMethod().getSignature());
      }
      return;
    }

    int tries = 0;
    int unitsQuantityToReorder = 0;
    while (tries < MAX_TRIES_TO_GET_REORDER_COUNT) {
      // value must be in (0; unitsLeft - 1]
      // - greater than 0 to avoid modifying the precedingFirstNotIdentity
      // - less than unitsLeft - 1 to avoid getting out of bounds
      unitsQuantityToReorder = Rand.getInt(unitsLeft - 2) + 1;

      tries++;

      final Unit selectedUnit = Iterables.get(units, precedingFirstNotIdentityIndex + unitsQuantityToReorder);
      if (isExceptionCaught(selectedUnit, units, body.getTraps())) {
        continue;
      }

      break;
    }

    // if 10 tries, we give up
    if (tries >= MAX_TRIES_TO_GET_REORDER_COUNT) {
      return;
    }

    if (isVerbose()) {
      logger.info("Adding GOTOs to \"{}\".", body.getMethod().getName());
    }

    final Unit first = precedingFirstNotIdentity == null ? units.getFirst() : precedingFirstNotIdentity;
    final Unit firstReorderingUnit = units.getSuccOf(first);

    // move random-size chunk at beginning to end
    Unit reorderingUnit = firstReorderingUnit;
    for (int reorder = 0; reorder < unitsQuantityToReorder; reorder++) {
      // create separate array to avoid coming modifications
      final UnitBox pointingToReorderingUnit[] = reorderingUnit.getBoxesPointingToThis().toArray(EMPTY_UNIT_BOX_ARRAY);
      for (UnitBox element : pointingToReorderingUnit) {
        reorderingUnit.removeBoxPointingToThis(element);
      }

      // unit box targets stay with a unit even if the unit is removed.
      final Unit nextReorderingUnit = units.getSuccOf(reorderingUnit);
      units.remove(reorderingUnit);
      units.add(reorderingUnit);

      for (UnitBox element : pointingToReorderingUnit) {
        reorderingUnit.addBoxPointingToThis(element);
      }

      reorderingUnit = nextReorderingUnit;
    }

    // add goto as FIRST unit to point to new chunk location
    final Unit firstReorderingNotGotoStmt
        = first instanceof GotoStmt ? ((GotoStmt) first).getTargetBox().getUnit() : firstReorderingUnit;
    final GotoStmt gotoFirstReorderingNotGotoStmt = Jimple.v().newGotoStmt(firstReorderingNotGotoStmt);
    units.insertBeforeNoRedirect(gotoFirstReorderingNotGotoStmt, reorderingUnit);

    // add goto as LAST unit to point to new position of second chunk
    if (units.getLast().fallsThrough()) {
      final Stmt gotoStmt = (reorderingUnit instanceof GotoStmt)
          ? Jimple.v().newGotoStmt(((GotoStmt) reorderingUnit).getTargetBox().getUnit())
          : Jimple.v().newGotoStmt(reorderingUnit);

      units.add(gotoStmt);
    }

    gotosInstrumented++;

    Unit secondReorderedUnit = units.getSuccOf(firstReorderingNotGotoStmt);
    if (secondReorderedUnit == null
        || (secondReorderedUnit.equals(units.getLast()) && secondReorderedUnit instanceof IdentityStmt)) {

      if (firstReorderingNotGotoStmt instanceof IdentityStmt) {
        if (isVerbose()) {
          logger.info("Skipping adding try-catch block at \"{}\".", body.getMethod().getSignature());
        }
        return;
      }

      secondReorderedUnit = firstReorderingNotGotoStmt;
    }

    final RefType throwable = Scene.v().getRefType("java.lang.Throwable");
    final Local caughtExceptionLocal = Jimple.v().newLocal("jbco_gi_caughtExceptionLocal", throwable);
    body.getLocals().add(caughtExceptionLocal);

    final Unit caughtExceptionHandler = Jimple.v().newIdentityStmt(caughtExceptionLocal, Jimple.v().newCaughtExceptionRef());
    units.add(caughtExceptionHandler);
    units.add(Jimple.v().newThrowStmt(caughtExceptionLocal));

    final Iterator<Unit> reorderedUnitsIterator
        = units.iterator(secondReorderedUnit, units.getPredOf(caughtExceptionHandler));
    Unit trapEndUnit = reorderedUnitsIterator.next();
    while (trapEndUnit instanceof IdentityStmt && reorderedUnitsIterator.hasNext()) {
      trapEndUnit = reorderedUnitsIterator.next();
    }
    trapEndUnit = units.getSuccOf(trapEndUnit);

    body.getTraps().add(Jimple.v().newTrap(throwable.getSootClass(), units.getPredOf(firstReorderingNotGotoStmt),
        trapEndUnit, caughtExceptionHandler));

    trapsAdded++;
  }

  private static boolean isExceptionCaught(Unit unit, Chain<Unit> units, Chain<Trap> traps) {
    for (Trap trap : traps) {
      final Unit end = trap.getEndUnit();
      if (end.equals(unit)) {
        return true;
      }

      final Iterator<Unit> unitsInTryIterator = units.iterator(trap.getBeginUnit(), units.getPredOf(end));
      if (Iterators.contains(unitsInTryIterator, unit)) {
        return true;
      }
    }

    return false;
  }

}
