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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.Hierarchy;
import soot.Local;
import soot.PhaseOptions;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.TrapManager;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.util.Chain;

/** Provides methods to inline a given invoke site. */
public class SiteInliner {
  public String getDefaultOptions() {
    return "insert-null-checks insert-redundant-casts";
  }

  public static void inlineSites(List sites) {
    inlineSites(sites, new HashMap());
  }

  /**
   * Iterates over a list of sites, inlining them in order. Each site is given as a 3-element list (inlinee, toInline,
   * container).
   */
  public static void inlineSites(List sites, Map options) {
    Iterator it = sites.iterator();
    while (it.hasNext()) {
      List l = (List) it.next();
      SootMethod inlinee = (SootMethod) l.get(0);
      Stmt toInline = (Stmt) l.get(1);
      SootMethod container = (SootMethod) l.get(2);

      inlineSite(inlinee, toInline, container, options);
    }
  }

  /**
   * Inlines the method <code>inlinee</code> into the <code>container</code> at the point <code>toInline</code>.
   */
  public static void inlineSite(SootMethod inlinee, Stmt toInline, SootMethod container) {
    inlineSite(inlinee, toInline, container, new HashMap());
  }

  /**
   * Inlines the given site. Note that this method does not actually check if it's safe (with respect to access modifiers and
   * special invokes) for it to be inlined. That functionality is handled by the InlinerSafetyManager.
   *
   */
  public static List inlineSite(SootMethod inlinee, Stmt toInline, SootMethod container, Map options) {

    boolean enableNullPointerCheckInsertion = PhaseOptions.getBoolean(options, "insert-null-checks");
    boolean enableRedundantCastInsertion = PhaseOptions.getBoolean(options, "insert-redundant-casts");

    Hierarchy hierarchy = Scene.v().getActiveHierarchy();

    JimpleBody containerB = (JimpleBody) container.getActiveBody();
    Chain<Unit> containerUnits = containerB.getUnits();

    if (!(inlinee.getDeclaringClass().isApplicationClass() || inlinee.getDeclaringClass().isLibraryClass())) {
      return null;
    }

    Body inlineeB = inlinee.getActiveBody();
    Chain<Unit> inlineeUnits = inlineeB.getUnits();

    InvokeExpr ie = toInline.getInvokeExpr();

    Value thisToAdd = null;
    if (ie instanceof InstanceInvokeExpr) {
      thisToAdd = ((InstanceInvokeExpr) ie).getBase();
    }

    // Insert casts to please the verifier.
    {
      boolean targetUsesThis = true;
      if (enableRedundantCastInsertion && ie instanceof InstanceInvokeExpr && targetUsesThis) {
        // The verifier will complain if targetUsesThis, and:
        // the argument passed to the method is not the same type.
        // For instance, Bottle.price_static takes a cost.
        // Cost is an interface implemented by Bottle.
        SootClass localType, parameterType;
        localType = ((RefType) ((InstanceInvokeExpr) ie).getBase().getType()).getSootClass();
        parameterType = inlinee.getDeclaringClass();

        if (localType.isInterface() || hierarchy.isClassSuperclassOf(localType, parameterType)) {
          Local castee = Jimple.v().newLocal("__castee", parameterType.getType());
          containerB.getLocals().add(castee);
          containerB.getUnits().insertBefore(Jimple.v().newAssignStmt(castee,
              Jimple.v().newCastExpr(((InstanceInvokeExpr) ie).getBase(), parameterType.getType())), toInline);
          thisToAdd = castee;
        }
      }
    }

    // (If enabled), add a null pointer check.
    {
      if (enableNullPointerCheckInsertion && ie instanceof InstanceInvokeExpr) {
        boolean caught = TrapManager.isExceptionCaughtAt(Scene.v().getSootClass("java.lang.NullPointerException"), toInline,
            containerB);

        /* Ah ha. Caught again! */
        if (caught) {
          /*
           * In this case, we don't use throwPoint; instead, put the code right there.
           */
          Stmt insertee
              = Jimple.v().newIfStmt(Jimple.v().newNeExpr(((InstanceInvokeExpr) ie).getBase(), NullConstant.v()), toInline);

          containerB.getUnits().insertBefore(insertee, toInline);

          // This sucks (but less than before).
          ((IfStmt) insertee).setTarget(toInline);

          ThrowManager.addThrowAfter(containerB, insertee);
        } else {
          Stmt throwPoint = ThrowManager.getNullPointerExceptionThrower(containerB);
          containerB.getUnits().insertBefore(
              Jimple.v().newIfStmt(Jimple.v().newEqExpr(((InstanceInvokeExpr) ie).getBase(), NullConstant.v()), throwPoint),
              toInline);
        }
      }
    }

    // Add synchronizing stuff.
    {
      if (inlinee.isSynchronized()) {
        // Need to get the class object if ie is a static invoke.
        if (ie instanceof InstanceInvokeExpr) {
          SynchronizerManager.v().synchronizeStmtOn(toInline, containerB, (Local) ((InstanceInvokeExpr) ie).getBase());
        } else {
          // If we're in an interface, we must be in a
          // <clinit> method, which surely needs no
          // synchronization.
          if (!container.getDeclaringClass().isInterface()) {
            // Whew!
            Local l = SynchronizerManager.v().addStmtsToFetchClassBefore(containerB, toInline);
            SynchronizerManager.v().synchronizeStmtOn(toInline, containerB, l);
          }
        }
      }
    }

    Stmt exitPoint = (Stmt) containerUnits.getSuccOf(toInline);

    // First, clone all of the inlinee's units & locals.
    HashMap<Local, Local> oldLocalsToNew = new HashMap<Local, Local>();
    HashMap<Stmt, Stmt> oldUnitsToNew = new HashMap<Stmt, Stmt>();
    {
      Stmt cursor = toInline;
      for (Iterator<Unit> currIt = inlineeUnits.iterator(); currIt.hasNext();) {
        final Stmt curr = (Stmt) currIt.next();
        Stmt currPrime = (Stmt) curr.clone();
        if (currPrime == null) {
          throw new RuntimeException("getting null from clone!");
        }
        currPrime.addAllTagsOf(curr);

        containerUnits.insertAfter(currPrime, cursor);
        cursor = currPrime;

        oldUnitsToNew.put(curr, currPrime);
      }

      for (Iterator<Local> lIt = inlineeB.getLocals().iterator(); lIt.hasNext();) {

        final Local l = lIt.next();
        Local lPrime = (Local) l.clone();
        if (lPrime == null) {
          throw new RuntimeException("getting null from local clone!");
        }

        containerB.getLocals().add(lPrime);
        oldLocalsToNew.put(l, lPrime);
      }
    }

    // Backpatch the newly-inserted units using newly-constructed maps.
    {
      Iterator<Unit> it = containerUnits.iterator(containerUnits.getSuccOf(toInline), containerUnits.getPredOf(exitPoint));

      while (it.hasNext()) {
        Stmt patchee = (Stmt) it.next();

        for (ValueBox box : patchee.getUseAndDefBoxes()) {
          if (!(box.getValue() instanceof Local)) {
            continue;
          }

          Local lPrime = oldLocalsToNew.get(box.getValue());
          if (lPrime != null) {
            box.setValue(lPrime);
          } else {
            throw new RuntimeException("local has no clone!");
          }
        }

        for (UnitBox box : patchee.getUnitBoxes()) {
          Unit uPrime = (oldUnitsToNew.get(box.getUnit()));
          if (uPrime != null) {
            box.setUnit(uPrime);
          } else {
            throw new RuntimeException("inlined stmt has no clone!");
          }
        }
      }
    }

    // Copy & backpatch the traps; preserve their same order.
    {
      Trap prevTrap = null;
      for (Trap t : inlineeB.getTraps()) {
        Stmt newBegin = oldUnitsToNew.get(t.getBeginUnit()), newEnd = oldUnitsToNew.get(t.getEndUnit()),
            newHandler = oldUnitsToNew.get(t.getHandlerUnit());

        if (newBegin == null || newEnd == null || newHandler == null) {
          throw new RuntimeException("couldn't map trap!");
        }

        Trap trap = Jimple.v().newTrap(t.getException(), newBegin, newEnd, newHandler);
        if (prevTrap == null) {
          containerB.getTraps().addFirst(trap);
        } else {
          containerB.getTraps().insertAfter(trap, prevTrap);
        }
        prevTrap = trap;
      }
    }

    // Handle identity stmt's and returns.
    {
      Iterator<Unit> it = containerUnits.iterator(containerUnits.getSuccOf(toInline), containerUnits.getPredOf(exitPoint));
      ArrayList<Unit> cuCopy = new ArrayList<Unit>();

      while (it.hasNext()) {
        cuCopy.add(it.next());
      }

      for (Unit u : cuCopy) {
        Stmt s = (Stmt) u;

        if (s instanceof IdentityStmt) {
          IdentityRef rhs = (IdentityRef) ((IdentityStmt) s).getRightOp();
          if (rhs instanceof CaughtExceptionRef) {
            continue;
          } else if (rhs instanceof ThisRef) {
            if (!(ie instanceof InstanceInvokeExpr)) {
              throw new RuntimeException("thisref with no receiver!");
            }

            containerUnits.swapWith(s, Jimple.v().newAssignStmt(((IdentityStmt) s).getLeftOp(), thisToAdd));
          } else if (rhs instanceof ParameterRef) {
            ParameterRef pref = (ParameterRef) rhs;
            containerUnits.swapWith(s, Jimple.v().newAssignStmt(((IdentityStmt) s).getLeftOp(), ie.getArg(pref.getIndex())));
          }
        } else if (s instanceof ReturnStmt) {
          if (toInline instanceof InvokeStmt) {
            // munch, munch.
            containerUnits.swapWith(s, Jimple.v().newGotoStmt(exitPoint));
            continue;
          }

          if (!(toInline instanceof AssignStmt)) {
            throw new RuntimeException("invoking stmt neither InvokeStmt nor AssignStmt!??!?!");
          }
          Value ro = ((ReturnStmt) s).getOp();
          Value lhs = ((AssignStmt) toInline).getLeftOp();
          AssignStmt as = Jimple.v().newAssignStmt(lhs, ro);
          containerUnits.insertBefore(as, s);
          containerUnits.swapWith(s, Jimple.v().newGotoStmt(exitPoint));
        } else if (s instanceof ReturnVoidStmt) {
          containerUnits.swapWith(s, Jimple.v().newGotoStmt(exitPoint));
        }
      }
    }

    List<Unit> newStmts = new ArrayList<Unit>();
    for (Iterator<Unit> i
        = containerUnits.iterator(containerUnits.getSuccOf(toInline), containerUnits.getPredOf(exitPoint)); i.hasNext();) {
      newStmts.add(i.next());
    }

    // Remove the original statement toInline.
    containerUnits.remove(toInline);

    // Resolve name collisions.
    LocalNameStandardizer.v().transform(containerB, "ji.lns");

    return newStmts;
  }
}
