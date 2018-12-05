package soot.jimple;

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

import java.util.Iterator;

import soot.Local;
import soot.SideEffectTester;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;

/**
 * Provides naive side effect information. Relies on no context information; instead, does the least conservative thing
 * possible even in the possible presence of badness.
 *
 * Possible weakness of SideEffectTester: consider a Box. We don't have a name for "what-is-inside-the-box" and so we can't
 * ask questions about it. But perhaps we need only ask questions about the box itself; the side effect tester can deal with
 * that internally.
 */

// ArrayRef,
// CaughtExceptionRef,
// FieldRef,
// IdentityRef,
// InstanceFieldRef,
// InstanceInvokeExpr,
// Local,
// StaticFieldRef

public class NaiveSideEffectTester implements SideEffectTester {
  public void newMethod(SootMethod m) {
  }

  /**
   * Returns true if the unit can read from v. Does not deal with expressions; deals with Refs.
   */
  public boolean unitCanReadFrom(Unit u, Value v) {
    Stmt s = (Stmt) u;

    // This doesn't really make any sense, but we need to return something.
    if (v instanceof Constant) {
      return false;
    }

    if (v instanceof Expr) {
      throw new RuntimeException("can't deal with expr");
    }

    // If it's an invoke, then only locals are safe.
    if (s.containsInvokeExpr()) {
      if (!(v instanceof Local)) {
        return true;
      }
    }

    // otherwise, use boxes tell all.
    Iterator useIt = u.getUseBoxes().iterator();
    while (useIt.hasNext()) {
      Value use = (Value) useIt.next();

      if (use.equivTo(v)) {
        return true;
      }

      Iterator vUseIt = v.getUseBoxes().iterator();
      while (vUseIt.hasNext()) {
        if (use.equivTo(vUseIt.next())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean unitCanWriteTo(Unit u, Value v) {
    Stmt s = (Stmt) u;

    if (v instanceof Constant) {
      return false;
    }

    if (v instanceof Expr) {
      throw new RuntimeException("can't deal with expr");
    }

    // If it's an invoke, then only locals are safe.
    if (s.containsInvokeExpr()) {
      if (!(v instanceof Local)) {
        return true;
      }
    }

    // otherwise, def boxes tell all.
    Iterator defIt = u.getDefBoxes().iterator();
    while (defIt.hasNext()) {
      Value def = ((ValueBox) (defIt.next())).getValue();
      Iterator useIt = v.getUseBoxes().iterator();
      while (useIt.hasNext()) {
        Value use = ((ValueBox) useIt.next()).getValue();
        if (def.equivTo(use)) {
          return true;
        }
      }
      // also handle the container of all these useboxes!
      if (def.equivTo(v)) {
        return true;
      }

      // deal with aliasing - handle case where they
      // are a read to the same field, regardless of
      // base object.
      if (v instanceof InstanceFieldRef && def instanceof InstanceFieldRef) {
        if (((InstanceFieldRef) v).getField() == ((InstanceFieldRef) def).getField()) {
          return true;
        }
      }
    }
    return false;
  }
}
