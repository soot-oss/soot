package soot.jimple.toolkits.invoke;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
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
import java.util.Set;

import soot.Body;
import soot.Hierarchy;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Trap;
import soot.TrapManager;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.shimple.ShimpleBody;
import soot.util.Chain;

/** Utility methods for dealing with traps. */
public class ThrowManager {

  /**
   * Iterate through the statements in b (starting at the end), returning the last instance of the following pattern:
   *
   * <code>
   * r928 = new java.lang.NullPointerException; specialinvoke r928."<init>"(); throw r928;
   * </code>
   *
   * Creates if necessary.
   */
  public static Stmt getNullPointerExceptionThrower(JimpleBody b) {
    return getNullPointerExceptionThrower((Body) b);
  }

  /**
   * Iterate through the statements in b (starting at the end), returning the last instance of the following pattern:
   *
   * <code>
   * r928 = new java.lang.NullPointerException; specialinvoke r928."<init>"(); throw r928;
   * </code>
   *
   * Creates if necessary.
   */
  public static Stmt getNullPointerExceptionThrower(ShimpleBody b) {
    return getNullPointerExceptionThrower((Body) b);
  }

  static Stmt getNullPointerExceptionThrower(Body b) {
    assert (b instanceof JimpleBody || b instanceof ShimpleBody);
    final Set<Unit> trappedUnits = TrapManager.getTrappedUnitsOf(b);
    final Chain<Unit> units = b.getUnits();
    final Unit first = units.getFirst();
    final Stmt last = (Stmt) units.getLast();
    for (Stmt s = last; s != first; s = (Stmt) units.getPredOf(s)) {
      if (!trappedUnits.contains(s) && s instanceof ThrowStmt) {
        Value throwee = ((ThrowStmt) s).getOp();
        if (throwee instanceof Constant) {
          continue;
        }

        if (s == first) {
          break;
        }
        Stmt prosInvoke = (Stmt) units.getPredOf(s);
        if (!(prosInvoke instanceof InvokeStmt)) {
          continue;
        }

        if (prosInvoke == first) {
          break;
        }

        Stmt prosNew = (Stmt) units.getPredOf(prosInvoke);
        if (!(prosNew instanceof AssignStmt)) {
          continue;
        }

        InvokeExpr ie = ((InvokeStmt) prosInvoke).getInvokeExpr();
        if (!(ie instanceof SpecialInvokeExpr) || ((SpecialInvokeExpr) ie).getBase() != throwee
            || !"<init>".equals(ie.getMethodRef().name())) {
          continue;
        }

        Value ro = ((AssignStmt) prosNew).getRightOp();
        if (((AssignStmt) prosNew).getLeftOp() != throwee || !(ro instanceof NewExpr)
            || !((NewExpr) ro).getBaseType().equals(RefType.v("java.lang.NullPointerException"))) {
          continue;
        }

        // Whew!
        return prosNew;
      }
    }

    // Create.
    return addThrowAfter(b.getLocals(), units, last);
  }

  static Stmt addThrowAfter(JimpleBody b, Stmt target) {
    return addThrowAfter(b.getLocals(), b.getUnits(), target);
  }

  static Stmt addThrowAfter(ShimpleBody b, Stmt target) {
    return addThrowAfter(b.getLocals(), b.getUnits(), target);
  }

  static Stmt addThrowAfter(Chain<Local> locals, Chain<Unit> units, Stmt target) {
    int i = 0;
    boolean canAddI;
    do {
      canAddI = true;
      String name = "__throwee" + i;
      for (Local l : locals) {
        if (name.equals(l.getName())) {
          canAddI = false;
        }
      }
      if (!canAddI) {
        i++;
      }
    } while (!canAddI);

    final Jimple jimp = Jimple.v();
    Local l = jimp.newLocal("__throwee" + i, RefType.v("java.lang.NullPointerException"));
    locals.add(l);

    Stmt newStmt = jimp.newAssignStmt(l, jimp.newNewExpr(RefType.v("java.lang.NullPointerException")));

    Stmt invStmt = jimp.newInvokeStmt(
        jimp.newSpecialInvokeExpr(l, Scene.v().getMethod("<java.lang.NullPointerException: void <init>()>").makeRef()));

    Stmt throwStmt = jimp.newThrowStmt(l);

    units.insertAfter(newStmt, target);
    units.insertAfter(invStmt, newStmt);
    units.insertAfter(throwStmt, invStmt);
    return newStmt;
  }

  /**
   * If exception e is caught at stmt s in body b, return the handler; otherwise, return null.
   */
  static boolean isExceptionCaughtAt(SootClass e, Stmt stmt, Body b) {
    // Look through the traps t of b, checking to see if (1) caught exception
    // is e and (2) stmt lies between t.beginUnit and t.endUnit
    Hierarchy h = new Hierarchy();
    for (Trap t : b.getTraps()) {
      /* Ah ha, we might win. */
      if (h.isClassSubclassOfIncluding(e, t.getException())) {
        for (Iterator<Unit> it = b.getUnits().iterator(t.getBeginUnit(), t.getEndUnit()); it.hasNext();) {
          if (stmt.equals(it.next())) {
            return true;
          }
        }
      }
    }

    return false;
  }
}
