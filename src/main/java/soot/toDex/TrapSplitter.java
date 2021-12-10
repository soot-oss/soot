package soot.toDex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;

/**
 * Transformer that splits nested traps for Dalvik which does not support hierarchies of traps. If we have a trap (1-3) with
 * handler A and a trap (2) with handler B, we transform them into three new traps: (1) and (3) with A, (2) with A+B.
 *
 * @author Steven Arzt
 */
public class TrapSplitter extends BodyTransformer {

  public TrapSplitter(Singletons.Global g) {
  }

  public static TrapSplitter v() {
    return soot.G.v().soot_toDex_TrapSplitter();
  }

  private class TrapOverlap {
    private Trap t1;
    private Trap t2;
    private Unit t2Start;

    public TrapOverlap(Trap t1, Trap t2, Unit t2Start) {
      this.t1 = t1;
      this.t2 = t2;
      this.t2Start = t2Start;
    }
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    // If we have less then two traps, there's nothing to do here
    if (b.getTraps().size() < 2) {
      return;
    }

    Set<Unit> potentiallyUselessTrapHandlers = null;

    // Look for overlapping traps
    TrapOverlap to;
    while ((to = getNextOverlap(b)) != null) {
      // If one of the two traps is empty, we remove it
      if (to.t1.getBeginUnit() == to.t1.getEndUnit()) {
        b.getTraps().remove(to.t1);
        if (potentiallyUselessTrapHandlers == null) {
          potentiallyUselessTrapHandlers = new HashSet<>();
        }
        potentiallyUselessTrapHandlers.add(to.t1.getHandlerUnit());
        continue;
      }
      if (to.t2.getBeginUnit() == to.t2.getEndUnit()) {
        b.getTraps().remove(to.t2);
        if (potentiallyUselessTrapHandlers == null) {
          potentiallyUselessTrapHandlers = new HashSet<>();
        }
        potentiallyUselessTrapHandlers.add(to.t2.getHandlerUnit());
        continue;
      }

      // t1start..t2start -> t1'start...t1'end,t2start...
      if (to.t1.getBeginUnit() != to.t2Start) {
        // We need to split off t1.start - predOf(t2.splitUnit). If both traps
        // start at the same statement, this range is empty, so we have checked
        // that.
        Trap newTrap = Jimple.v().newTrap(to.t1.getException(), to.t1.getBeginUnit(), to.t2Start, to.t1.getHandlerUnit());
        safeAddTrap(b, newTrap, to.t1);
        to.t1.setBeginUnit(to.t2Start);
      }
      // (t1start, t2start) ... t1end ... t2end
      else if (to.t1.getBeginUnit() == to.t2.getBeginUnit()) {
        Unit firstEndUnit = to.t1.getBeginUnit();
        while (firstEndUnit != to.t1.getEndUnit() && firstEndUnit != to.t2.getEndUnit()) {
          firstEndUnit = b.getUnits().getSuccOf(firstEndUnit);
        }

        if (firstEndUnit == to.t1.getEndUnit()) {
          if (to.t1.getException() != to.t2.getException()) {
            Trap newTrap
                = Jimple.v().newTrap(to.t2.getException(), to.t1.getBeginUnit(), firstEndUnit, to.t2.getHandlerUnit());
            safeAddTrap(b, newTrap, to.t2);
          } else if (to.t1.getHandlerUnit() != to.t2.getHandlerUnit()) {
            // Traps t1 and t2 catch the same exception, but have different handlers
            //
            // The JVM specification (2.10 Exceptions) says:
            // "At run time, when an exception is thrown, the Java
            // Virtual Machine searches the exception handlers of the current method in the order
            // that they appear in the corresponding exception handler table in the class file,
            // starting from the beginning of that table. Note that the Java Virtual Machine does
            // not enforce nesting of or any ordering of the exception table entries of a method.
            // The exception handling semantics of the Java programming language are implemented
            // only through cooperation with the compiler (3.12)."
            //
            // 3.12
            // "The nesting of catch clauses is represented only in the exception table. The Java
            // Virtual Machine does not enforce nesting of or any ordering of the exception table
            // entries (2.10). However, because try-catch constructs are structured, a compiler
            // can always order the entries of the exception handler table such that, for any thrown
            // exception and any program counter value in that method, the first exception handler
            // that matches the thrown exception corresponds to the innermost matching catch clause."
            //
            // t1 is first, so it stays the same.
            // t2 is reduced
            Trap newTrap
                = Jimple.v().newTrap(to.t1.getException(), to.t1.getBeginUnit(), firstEndUnit, to.t1.getHandlerUnit());
            safeAddTrap(b, newTrap, to.t1);
          }
          to.t2.setBeginUnit(firstEndUnit);
        } else if (firstEndUnit == to.t2.getEndUnit()) {
          if (to.t1.getException() != to.t2.getException()) {
            Trap newTrap2
                = Jimple.v().newTrap(to.t1.getException(), to.t1.getBeginUnit(), firstEndUnit, to.t1.getHandlerUnit());
            safeAddTrap(b, newTrap2, to.t1);
            to.t1.setBeginUnit(firstEndUnit);
          } else if (to.t1.getHandlerUnit() != to.t2.getHandlerUnit()) {
            // If t2 ends first, t2 is useless.
            b.getTraps().remove(to.t2);
            if (potentiallyUselessTrapHandlers == null) {
              potentiallyUselessTrapHandlers = new HashSet<>();
            }
            potentiallyUselessTrapHandlers.add(to.t2.getHandlerUnit());
          } else {
            to.t1.setBeginUnit(firstEndUnit);
          }
        }
      }
    }

    removePotentiallyUselassTraps(b, potentiallyUselessTrapHandlers);
  }

  /**
   * Changes the given body so that trap handlers, which are contained in the given set, are removed in case they are not
   * referenced by any trap. The list is changed so that it contains the unreferenced trap handlers.
   * 
   * @param b
   *          the body
   * @param potentiallyUselessTrapHandlers
   *          potentially useless trap handlers
   */
  public static void removePotentiallyUselassTraps(Body b, Set<Unit> potentiallyUselessTrapHandlers) {
    if (potentiallyUselessTrapHandlers == null) {
      return;
    }

    for (Trap t : b.getTraps()) {
      // Trap is used by another trap handler, so it is not useless
      potentiallyUselessTrapHandlers.remove(t.getHandlerUnit());
    }
    boolean removedUselessTrap = false;
    for (Unit uselessTrapHandler : potentiallyUselessTrapHandlers) {
      if (uselessTrapHandler instanceof IdentityStmt) {
        IdentityStmt assign = (IdentityStmt) uselessTrapHandler;
        if (assign.getRightOp() instanceof CaughtExceptionRef) {
          // Make sure that the useless trap handler, which is not used
          // anywhere else still gets a valid value.
          Unit newStmt = Jimple.v().newAssignStmt(assign.getLeftOp(), NullConstant.v());
          b.getUnits().swapWith(assign, newStmt);
          removedUselessTrap = true;
        }
      }
    }
    if (removedUselessTrap) {
      // We cleaned up the useless trap, it hopefully is unreachable
      UnreachableCodeEliminator.v().transform(b);
    }
  }

  /**
   * Adds a new trap to the given body only if the given trap is not empty
   *
   * @param b
   *          The body to which to add the trap
   * @param newTrap
   *          The trap to add
   * @param position
   *          The position after which to insert the trap
   */
  private void safeAddTrap(Body b, Trap newTrap, Trap position) {
    // Do not create any empty traps
    if (newTrap.getBeginUnit() != newTrap.getEndUnit()) {
      if (position != null) {
        b.getTraps().insertAfter(newTrap, position);
      } else {
        b.getTraps().add(newTrap);
      }
    }
  }

  /**
   * Gets two arbitrary overlapping traps t1, t2 in the given method body. The begin unit of the t2 should be equal to or
   * occurring after the begin unit of t1.
   *
   * @param b
   *          The body in which to look for overlapping traps
   * @return Two overlapping traps if they exist, otherwise null
   */
  private TrapOverlap getNextOverlap(Body b) {
    Map<Unit, LinkedHashSet<Trap>> trapsContainingThisUnit = new HashMap<>();
    for (Trap t1 : b.getTraps()) {
      // Look whether one of our trapped statements is the begin
      // statement of another trap
      for (Unit splitUnit = t1.getBeginUnit(); splitUnit != t1.getEndUnit() && splitUnit != null; splitUnit
          = b.getUnits().getSuccOf(splitUnit)) {
        LinkedHashSet<Trap> otherTrapsContainingUnit = trapsContainingThisUnit.get(splitUnit);
        if (otherTrapsContainingUnit != null) {
          for (Trap t2 : otherTrapsContainingUnit) {
            // if we got here, this unit is already contained inside another trap.
            // the other traps were added earlier
            if (t2.getEndUnit() != t1.getEndUnit() || t2.getException() == t1.getException()) {

              // this restores the old function behavior
              if (splitUnit == t1.getBeginUnit()) {
                return new TrapOverlap(t2, t1, splitUnit);
              } else {
                return new TrapOverlap(t1, t2, splitUnit);
              }
            }
          }
          otherTrapsContainingUnit.add(t1);
        } else {
          LinkedHashSet<Trap> newSet = new LinkedHashSet<>();
          newSet.add(t1);
          trapsContainingThisUnit.put(splitUnit, newSet);
        }

      }
    }

    return null;
  }

}
