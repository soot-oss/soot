package soot.jimple.toolkits.base;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Trap;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.util.Chain;

public class ThisInliner extends BodyTransformer {

  public void internalTransform(Body b, String phaseName, Map options) {

    // assure body is a constructor
    if (!b.getMethod().getName().equals("<init>")) {
      return;
    }

    // if the first invoke is a this() and not a super() inline the this()
    InvokeStmt invokeStmt = getFirstSpecialInvoke(b);
    if (invokeStmt == null) {
      return;
    }
    SpecialInvokeExpr specInvokeExpr = (SpecialInvokeExpr) invokeStmt.getInvokeExpr();
    if (specInvokeExpr.getMethod().getDeclaringClass().equals(b.getMethod().getDeclaringClass())) {

      // put locals from inlinee into container
      if (!specInvokeExpr.getMethod().hasActiveBody()) {
        specInvokeExpr.getMethod().retrieveActiveBody();
      }

      HashMap<Local, Local> oldLocalsToNew = new HashMap<Local, Local>();

      for (Local l : specInvokeExpr.getMethod().getActiveBody().getLocals()) {
        Local newLocal = (Local) l.clone();
        b.getLocals().add(newLocal);
        oldLocalsToNew.put(l, newLocal);
      }

      // find identity stmt of original method
      IdentityStmt origIdStmt = findIdentityStmt(b);

      HashMap<Stmt, Stmt> oldStmtsToNew = new HashMap<Stmt, Stmt>();

      // System.out.println("locals: "+b.getLocals());
      Chain<Unit> containerUnits = b.getUnits();
      for (Unit u : specInvokeExpr.getMethod().getActiveBody().getUnits()) {
        Stmt inlineeStmt = (Stmt) u;

        // handle identity stmts
        if (inlineeStmt instanceof IdentityStmt) {
          IdentityStmt idStmt = (IdentityStmt) inlineeStmt;

          if (idStmt.getRightOp() instanceof ThisRef) {
            Stmt newThis = Jimple.v().newAssignStmt((Local) oldLocalsToNew.get(idStmt.getLeftOp()), origIdStmt.getLeftOp());
            containerUnits.insertBefore(newThis, invokeStmt);
            oldStmtsToNew.put(inlineeStmt, newThis);
          }

          else if (idStmt.getRightOp() instanceof CaughtExceptionRef) {
            Stmt newInlinee = (Stmt) inlineeStmt.clone();
            for (ValueBox next : newInlinee.getUseAndDefBoxes()) {
              if (next.getValue() instanceof Local) {
                next.setValue((Local) oldLocalsToNew.get(next.getValue()));
              }
            }

            containerUnits.insertBefore(newInlinee, invokeStmt);
            oldStmtsToNew.put(inlineeStmt, newInlinee);
          } else if (idStmt.getRightOp() instanceof ParameterRef) {
            Stmt newParam = Jimple.v().newAssignStmt((Local) oldLocalsToNew.get(idStmt.getLeftOp()),
                specInvokeExpr.getArg(((ParameterRef) idStmt.getRightOp()).getIndex()));
            containerUnits.insertBefore(newParam, invokeStmt);
            oldStmtsToNew.put(inlineeStmt, newParam);
          }
        }

        // handle return void stmts (cannot return anything else
        // from a constructor)
        else if (inlineeStmt instanceof ReturnVoidStmt) {
          Stmt newRet = Jimple.v().newGotoStmt((Stmt) containerUnits.getSuccOf(invokeStmt));
          containerUnits.insertBefore(newRet, invokeStmt);
          System.out.println("adding to stmt map: " + inlineeStmt + " and " + newRet);
          oldStmtsToNew.put(inlineeStmt, newRet);
        }

        else {
          Stmt newInlinee = (Stmt) inlineeStmt.clone();
          for (ValueBox next : newInlinee.getUseAndDefBoxes()) {
            if (next.getValue() instanceof Local) {
              next.setValue((Local) oldLocalsToNew.get(next.getValue()));
            }
          }

          containerUnits.insertBefore(newInlinee, invokeStmt);
          oldStmtsToNew.put(inlineeStmt, newInlinee);
        }

      }

      // handleTraps
      for (Trap t : specInvokeExpr.getMethod().getActiveBody().getTraps()) {
        System.out.println("begin: " + t.getBeginUnit());
        Stmt newBegin = oldStmtsToNew.get(t.getBeginUnit());
        System.out.println("end: " + t.getEndUnit());
        Stmt newEnd = oldStmtsToNew.get(t.getEndUnit());
        System.out.println("handler: " + t.getHandlerUnit());
        Stmt newHandler = oldStmtsToNew.get(t.getHandlerUnit());

        if (newBegin == null || newEnd == null || newHandler == null) {
          throw new RuntimeException("couldn't map trap!");
        }

        b.getTraps().add(Jimple.v().newTrap(t.getException(), newBegin, newEnd, newHandler));
      }

      // patch gotos
      for (Unit u : specInvokeExpr.getMethod().getActiveBody().getUnits()) {
        Stmt inlineeStmt = (Stmt) u;
        if (inlineeStmt instanceof GotoStmt) {
          System.out.println("inlinee goto target: " + ((GotoStmt) inlineeStmt).getTarget());
          ((GotoStmt) oldStmtsToNew.get(inlineeStmt)).setTarget(oldStmtsToNew.get(((GotoStmt) inlineeStmt).getTarget()));
        }

      }

      // remove original invoke
      containerUnits.remove(invokeStmt);

      // resolve name collisions
      LocalNameStandardizer.v().transform(b, "ji.lns");

    }
    // System.out.println("locals: "+b.getLocals());
    // System.out.println("units: "+b.getUnits());
  }

  private InvokeStmt getFirstSpecialInvoke(Body b) {
    for (Unit u : b.getUnits()) {
      Stmt s = (Stmt) u;
      if (!(s instanceof InvokeStmt)) {
        continue;
      }

      InvokeExpr invokeExpr = ((InvokeStmt) s).getInvokeExpr();
      if (!(invokeExpr instanceof SpecialInvokeExpr)) {
        continue;
      }

      return (InvokeStmt) s;
    }
    // but there will always be either a call to this() or to super()
    // from the constructor
    return null;
  }

  private IdentityStmt findIdentityStmt(Body b) {
    for (Unit u : b.getUnits()) {
      Stmt s = (Stmt) u;
      if ((s instanceof IdentityStmt) && (((IdentityStmt) s).getRightOp() instanceof ThisRef)) {
        return (IdentityStmt) s;
      }
    }
    return null;
  }
}
