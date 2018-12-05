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

import java.util.Collection;
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
import soot.Type;
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
import soot.util.Chain;

/** Utility methods for dealing with traps. */
public class ThrowManager {
  /**
   * Iterate through the statements in b (starting at the end), returning the last instance of the following pattern:
   *
   * r928 = new java.lang.NullPointerException; specialinvoke r928."<init>"(); throw r928;
   *
   * Creates if necessary.
   */

  public static Stmt getNullPointerExceptionThrower(JimpleBody b) {
    Chain<Unit> units = b.getUnits();
    Set<Unit> trappedUnits = TrapManager.getTrappedUnitsOf(b);

    for (Stmt s = (Stmt) units.getLast(); s != units.getFirst(); s = (Stmt) units.getPredOf(s)) {
      if (trappedUnits.contains(s)) {
        continue;
      }
      if (s instanceof ThrowStmt) {
        Value throwee = ((ThrowStmt) s).getOp();
        if (throwee instanceof Constant) {
          continue;
        }

        if (s == units.getFirst()) {
          break;
        }
        Stmt prosInvoke = (Stmt) units.getPredOf(s);

        if (!(prosInvoke instanceof InvokeStmt)) {
          continue;
        }

        if (prosInvoke == units.getFirst()) {
          break;
        }
        Stmt prosNew = (Stmt) units.getPredOf(prosInvoke);

        if (!(prosNew instanceof AssignStmt)) {
          continue;
        }

        InvokeExpr ie = ((InvokeStmt) prosInvoke).getInvokeExpr();
        if (!(ie instanceof SpecialInvokeExpr)) {
          continue;
        }

        if (((SpecialInvokeExpr) ie).getBase() != throwee || !ie.getMethodRef().name().equals("<init>")) {
          continue;
        }

        Value lo = ((AssignStmt) prosNew).getLeftOp();
        Value ro = ((AssignStmt) prosNew).getRightOp();
        if (lo != throwee || !(ro instanceof NewExpr)) {
          continue;
        }

        Type newType = ((NewExpr) ro).getBaseType();
        if (!newType.equals(RefType.v("java.lang.NullPointerException"))) {
          continue;
        }

        // Whew!
        return prosNew;
      }
    }

    // Create.
    Stmt last = (Stmt) units.getLast();

    return addThrowAfter(b, last);
  }

  static Stmt addThrowAfter(JimpleBody b, Stmt target) {
    Chain<Unit> units = b.getUnits();
    Collection<Local> locals = b.getLocals();
    int i = 0;

    // Bah!
    boolean canAddI = false;
    do {
      canAddI = true;
      Iterator<Local> localIt = locals.iterator();
      while (localIt.hasNext()) {
        Local l = (Local) localIt.next();
        if (l.getName().equals("__throwee" + i)) {
          canAddI = false;
        }
      }
      if (!canAddI) {
        i++;
      }
    } while (!canAddI);

    Local l = Jimple.v().newLocal("__throwee" + i, RefType.v("java.lang.NullPointerException"));
    b.getLocals().add(l);

    Stmt newStmt = Jimple.v().newAssignStmt(l, Jimple.v().newNewExpr(RefType.v("java.lang.NullPointerException")));

    Stmt invStmt = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(l,
        Scene.v().getMethod("<java.lang.NullPointerException: void <init>()>").makeRef()));

    Stmt throwStmt = Jimple.v().newThrowStmt(l);

    units.insertAfter(newStmt, target);
    units.insertAfter(invStmt, newStmt);
    units.insertAfter(throwStmt, invStmt);
    return newStmt;
  }

  /**
   * If exception e is caught at stmt s in body b, return the handler; otherwise, return null.
   */
  static boolean isExceptionCaughtAt(SootClass e, Stmt stmt, Body b) {
    /*
     * Look through the traps t of b, checking to see if: - caught exception is e; - and, stmt lies between t.beginUnit and
     * t.endUnit
     */

    Hierarchy h = new Hierarchy();

    Iterator<Trap> trapsIt = b.getTraps().iterator();

    while (trapsIt.hasNext()) {
      Trap t = trapsIt.next();

      /* Ah ha, we might win. */
      if (h.isClassSubclassOfIncluding(e, t.getException())) {
        Iterator<Unit> it = b.getUnits().iterator(t.getBeginUnit(), t.getEndUnit());
        while (it.hasNext()) {
          if (stmt.equals(it.next())) {
            return true;
          }
        }
      }
    }

    return false;
  }
}
