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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
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
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.tagkit.Host;
import soot.util.Chain;

/** Provides methods to inline a given invoke site. */
public class SiteInliner {

  public String getDefaultOptions() {
    return "insert-null-checks insert-redundant-casts";
  }

  /**
   * Iterates over a list of sites, inlining them in order. Each site is given as a 3-element list (inlinee, toInline,
   * container).
   */
  public static void inlineSites(List<List<Host>> sites) {
    inlineSites(sites, Collections.emptyMap());
  }

  /**
   * Iterates over a list of sites, inlining them in order. Each site is given as a 3-element list (inlinee, toInline,
   * container).
   */
  public static void inlineSites(List<List<Host>> sites, Map<String, String> options) {
    for (List<Host> l : sites) {
      assert (l.size() == 3);
      SootMethod inlinee = (SootMethod) l.get(0);
      Stmt toInline = (Stmt) l.get(1);
      SootMethod container = (SootMethod) l.get(2);
      inlineSite(inlinee, toInline, container, options);
    }
  }

  /**
   * Inlines the method <code>inlinee</code> into the <code>container</code> at the point <code>toInline</code>.
   */
  public static List<Unit> inlineSite(SootMethod inlinee, Stmt toInline, SootMethod container) {
    return inlineSite(inlinee, toInline, container, Collections.emptyMap());
  }

  /**
   * Inlines the given site. Note that this method does not actually check if it's safe (with respect to access modifiers and
   * special invokes) for it to be inlined. That functionality is handled by the InlinerSafetyManager.
   */
  public static List<Unit> inlineSite(SootMethod inlinee, Stmt toInline, SootMethod container, Map<String, String> options) {
    final SootClass declaringClass = inlinee.getDeclaringClass();
    if (!declaringClass.isApplicationClass() && !declaringClass.isLibraryClass()) {
      return null;
    }

    final Body containerB = container.getActiveBody();
    final Chain<Unit> containerUnits = containerB.getUnits();
    assert (containerUnits.contains(toInline)) : toInline + " is not in body " + containerB;
    final InvokeExpr ie = toInline.getInvokeExpr();
    Value thisToAdd = (ie instanceof InstanceInvokeExpr) ? ((InstanceInvokeExpr) ie).getBase() : null;
    if (ie instanceof InstanceInvokeExpr) {
      // Insert casts to please the verifier.
      if (PhaseOptions.getBoolean(options, "insert-redundant-casts")) {
        // The verifier will complain if the argument passed to the method is not the correct type.
        // For instance, Bottle.price_static takes a cost.
        // Cost is an interface implemented by Bottle.
        Value base = ((InstanceInvokeExpr) ie).getBase();
        SootClass localType = ((RefType) base.getType()).getSootClass();
        if (localType.isInterface() || Scene.v().getActiveHierarchy().isClassSuperclassOf(localType, declaringClass)) {
          final Jimple jimp = Jimple.v();
          RefType type = declaringClass.getType();
          Local castee = jimp.newLocal("__castee", type);
          containerB.getLocals().add(castee);
          containerUnits.insertBefore(jimp.newAssignStmt(castee, jimp.newCastExpr(base, type)), toInline);
          thisToAdd = castee;
        }
      }

      // (If enabled), add a null pointer check.
      if (PhaseOptions.getBoolean(options, "insert-null-checks")) {
        final Jimple jimp = Jimple.v();
        /* Ah ha. Caught again! */
        if (TrapManager.isExceptionCaughtAt(Scene.v().getSootClass("java.lang.NullPointerException"), toInline,
            containerB)) {
          // In this case, we don't use throwPoint. Instead, put the code right there.
          IfStmt insertee = jimp.newIfStmt(jimp.newNeExpr(((InstanceInvokeExpr) ie).getBase(), NullConstant.v()), toInline);

          containerUnits.insertBefore(insertee, toInline);

          // This sucks (but less than before).
          insertee.setTarget(toInline);

          ThrowManager.addThrowAfter(containerB.getLocals(), containerUnits, insertee);
        } else {
          containerUnits.insertBefore(jimp.newIfStmt(jimp.newEqExpr(((InstanceInvokeExpr) ie).getBase(), NullConstant.v()),
              ThrowManager.getNullPointerExceptionThrower(containerB)), toInline);
        }
      }
    }

    // Add synchronizing stuff.
    if (inlinee.isSynchronized()) {
      // Need to get the class object if ie is a static invoke.
      if (ie instanceof InstanceInvokeExpr) {
        Local base = (Local) ((InstanceInvokeExpr) ie).getBase();
        SynchronizerManager.v().synchronizeStmtOn(toInline, containerB, base);
      } else if (!container.getDeclaringClass().isInterface()) {
        // If we're in an interface, we must be in a <clinit> method,
        // which surely needs no synchronization.
        final SynchronizerManager mgr = SynchronizerManager.v();
        mgr.synchronizeStmtOn(toInline, containerB, mgr.addStmtsToFetchClassBefore(containerB, toInline));
      }
    }

    final Body inlineeB = inlinee.getActiveBody();
    final Chain<Unit> inlineeUnits = inlineeB.getUnits();
    final Unit exitPoint = containerUnits.getSuccOf(toInline);

    // First, clone all of the inlinee's units & locals.
    HashMap<Local, Local> oldLocalsToNew = new HashMap<Local, Local>();
    HashMap<Unit, Unit> oldUnitsToNew = new HashMap<Unit, Unit>();
    {
      Unit cursor = toInline;
      for (Unit u : inlineeUnits) {
        Unit currPrime = (Unit) u.clone();
        if (currPrime == null) {
          throw new RuntimeException("getting null from clone!");
        }
        currPrime.addAllTagsOf(u);
        containerUnits.insertAfter(currPrime, cursor);
        oldUnitsToNew.put(u, currPrime);

        cursor = currPrime;
      }

      for (Local l : inlineeB.getLocals()) {
        Local lPrime = (Local) l.clone();
        if (lPrime == null) {
          throw new RuntimeException("getting null from local clone!");
        }
        containerB.getLocals().add(lPrime);
        oldLocalsToNew.put(l, lPrime);
      }
    }

    // Backpatch the newly-inserted units using newly-constructed maps.
    for (Iterator<Unit> it
        = containerUnits.iterator(containerUnits.getSuccOf(toInline), containerUnits.getPredOf(exitPoint)); it.hasNext();) {
      Unit patchee = it.next();

      for (ValueBox box : patchee.getUseAndDefBoxes()) {
        Value value = box.getValue();
        if (value instanceof Local) {
          Local lPrime = oldLocalsToNew.get((Local) value);
          if (lPrime == null) {
            throw new RuntimeException("local has no clone!");
          }
          box.setValue(lPrime);
        }
      }
      for (UnitBox box : patchee.getUnitBoxes()) {
        Unit uPrime = oldUnitsToNew.get(box.getUnit());
        if (uPrime == null) {
          throw new RuntimeException("inlined stmt has no clone!");
        }
        box.setUnit(uPrime);
      }
    }

    // Copy & backpatch the traps; preserve their same order.
    {
      final Chain<Trap> traps = containerB.getTraps();
      Trap prevTrap = null;
      for (Trap t : inlineeB.getTraps()) {
        Unit newBegin = oldUnitsToNew.get(t.getBeginUnit());
        Unit newEnd = oldUnitsToNew.get(t.getEndUnit());
        Unit newHandler = oldUnitsToNew.get(t.getHandlerUnit());

        if (newBegin == null || newEnd == null || newHandler == null) {
          throw new RuntimeException("couldn't map trap!");
        }

        Trap trap = Jimple.v().newTrap(t.getException(), newBegin, newEnd, newHandler);
        if (prevTrap == null) {
          traps.addFirst(trap);
        } else {
          traps.insertAfter(trap, prevTrap);
        }
        prevTrap = trap;
      }
    }

    // Handle identity stmt's and returns.
    {
      ArrayList<Unit> cuCopy = new ArrayList<Unit>();
      for (Iterator<Unit> it
          = containerUnits.iterator(containerUnits.getSuccOf(toInline), containerUnits.getPredOf(exitPoint)); it
              .hasNext();) {
        cuCopy.add(it.next());
      }
      for (Unit u : cuCopy) {
        if (u instanceof IdentityStmt) {
          IdentityStmt idStmt = (IdentityStmt) u;
          IdentityRef rhs = (IdentityRef) idStmt.getRightOp();
          if (rhs instanceof CaughtExceptionRef) {
            continue;
          } else if (rhs instanceof ThisRef) {
            if (!(ie instanceof InstanceInvokeExpr)) {
              throw new RuntimeException("thisref with no receiver!");
            }
            containerUnits.swapWith(u, Jimple.v().newAssignStmt(idStmt.getLeftOp(), thisToAdd));
          } else if (rhs instanceof ParameterRef) {
            ParameterRef pref = (ParameterRef) rhs;
            containerUnits.swapWith(u, Jimple.v().newAssignStmt(idStmt.getLeftOp(), ie.getArg(pref.getIndex())));
          }
        } else if (u instanceof ReturnStmt) {
          if (toInline instanceof InvokeStmt) {
            // munch, munch.
            containerUnits.swapWith(u, Jimple.v().newGotoStmt(exitPoint));
          } else if (toInline instanceof AssignStmt) {
            final Jimple jimp = Jimple.v();
            AssignStmt as = jimp.newAssignStmt(((AssignStmt) toInline).getLeftOp(), ((ReturnStmt) u).getOp());
            containerUnits.insertBefore(as, u);
            containerUnits.swapWith(u, jimp.newGotoStmt(exitPoint));
          } else {
            throw new RuntimeException("invoking stmt neither InvokeStmt nor AssignStmt!??!?!");
          }
        } else if (u instanceof ReturnVoidStmt) {
          containerUnits.swapWith(u, Jimple.v().newGotoStmt(exitPoint));
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
