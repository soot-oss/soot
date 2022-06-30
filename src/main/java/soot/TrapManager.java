package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.util.Chain;

/** Utility methods for dealing with traps. */
public class TrapManager {

  /**
   * If exception e is caught at unit u in body b, return true; otherwise, return false.
   */
  public static boolean isExceptionCaughtAt(SootClass e, Unit u, Body b) {
    // Look through the traps t of b, checking to see if (1) caught exception is
    // e and, (2) unit lies between t.beginUnit and t.endUnit.
    final Hierarchy h = Scene.v().getActiveHierarchy();
    final Chain<Unit> units = b.getUnits();

    for (Trap t : b.getTraps()) {
      /* Ah ha, we might win. */
      if (h.isClassSubclassOfIncluding(e, t.getException())) {
        for (Iterator<Unit> it = units.iterator(t.getBeginUnit(), units.getPredOf(t.getEndUnit())); it.hasNext();) {
          if (u.equals(it.next())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /** Returns the list of traps caught at Unit u in Body b. */
  public static List<Trap> getTrapsAt(Unit unit, Body b) {
    final Chain<Unit> units = b.getUnits();
    List<Trap> trapsList = new ArrayList<Trap>();
    for (Trap t : b.getTraps()) {
      for (Iterator<Unit> it = units.iterator(t.getBeginUnit(), units.getPredOf(t.getEndUnit())); it.hasNext();) {
        if (unit.equals(it.next())) {
          trapsList.add(t);
        }
      }
    }
    return trapsList;
  }

  /** Returns a set of units which lie inside the range of any trap. */
  public static Set<Unit> getTrappedUnitsOf(Body b) {
    final Chain<Unit> units = b.getUnits();
    Set<Unit> trapsSet = new HashSet<Unit>();
    for (Trap t : b.getTraps()) {
      for (Iterator<Unit> it = units.iterator(t.getBeginUnit(), units.getPredOf(t.getEndUnit())); it.hasNext();) {
        trapsSet.add(it.next());
      }
    }
    return trapsSet;
  }

  /**
   * Splits all traps so that they do not cross the range rangeStart - rangeEnd. Note that rangeStart is inclusive, rangeEnd
   * is exclusive.
   */
  public static void splitTrapsAgainst(Body b, Unit rangeStart, Unit rangeEnd) {
    final Chain<Trap> traps = b.getTraps();
    final Chain<Unit> units = b.getUnits();

    for (Iterator<Trap> trapsIt = traps.snapshotIterator(); trapsIt.hasNext();) {
      Trap t = trapsIt.next();

      boolean insideRange = false;
      for (Iterator<Unit> unitIt = units.iterator(t.getBeginUnit(), t.getEndUnit()); unitIt.hasNext();) {
        Unit u = unitIt.next();
        if (rangeStart.equals(u)) {
          insideRange = true;
        }
        if (!unitIt.hasNext()) { // i.e. u.equals(t.getEndUnit())
          if (insideRange) {
            Trap newTrap = (Trap) t.clone();
            t.setBeginUnit(rangeStart);
            newTrap.setEndUnit(rangeStart);
            traps.insertAfter(newTrap, t);
          } else {
            break;
          }
        }
        if (rangeEnd.equals(u)) {
          // insideRange had better be true now.
          if (!insideRange) {
            throw new RuntimeException("inversed range?");
          }
          Trap firstTrap = (Trap) t.clone();
          Trap secondTrap = (Trap) t.clone();
          firstTrap.setEndUnit(rangeStart);
          secondTrap.setBeginUnit(rangeStart);
          secondTrap.setEndUnit(rangeEnd);
          t.setBeginUnit(rangeEnd);

          traps.insertAfter(firstTrap, t);
          traps.insertAfter(secondTrap, t);
        }
      }
    }
  }

  /**
   * Given a body and a unit handling an exception, returns the list of exception types possibly caught by the handler.
   */
  public static List<RefType> getExceptionTypesOf(Unit u, Body body) {
    final boolean module_mode = ModuleUtil.module_mode();

    List<RefType> possibleTypes = new ArrayList<RefType>();
    for (Trap trap : body.getTraps()) {
      if (trap.getHandlerUnit() == u) {
        RefType type;
        if (module_mode) {
          type = ModuleRefType.v(trap.getException().getName(), Optional.fromNullable(trap.getException().moduleName));
        } else {
          type = RefType.v(trap.getException().getName());
        }
        possibleTypes.add(type);
      }
    }

    return possibleTypes;
  }
}
