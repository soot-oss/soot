package soot.jimple.toolkits.invoke;

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

import soot.Hierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;

/**
 * Methods for checking safety requirements for inlining.
 */
public class InlinerSafetyManager {

  private static final boolean PRINT_FAILURE_REASONS = true;

  // true if safe to inline
  public static boolean checkSpecialInlineRestrictions(SootMethod container, SootMethod target, String options) {
    // Check the body of the method to inline for specialinvokes
    final boolean accessors = "accessors".equals(options);
    for (Unit u : target.getActiveBody().getUnits()) {
      Stmt st = (Stmt) u;
      if (st.containsInvokeExpr()) {
        InvokeExpr ie1 = st.getInvokeExpr();
        if (ie1 instanceof SpecialInvokeExpr) {
          SootClass containerDeclaringClass = container.getDeclaringClass();
          if (specialInvokePerformsLookupIn(ie1, containerDeclaringClass)
              || specialInvokePerformsLookupIn(ie1, target.getDeclaringClass())) {
            return false;
          }

          SootMethod specialTarget = ie1.getMethod();
          if (specialTarget.isPrivate() && specialTarget.getDeclaringClass() != containerDeclaringClass) {
            // Do not inline a call which contains a specialinvoke call to a private method outside
            // the current class. This avoids a verifier error and we assume will not have a big
            // impact because we are inlining methods bottom-up, so such a call will be rare
            if (!accessors) {
              return false;
            }
          }
        }
      }
    }

    return true;
  }

  public static boolean checkAccessRestrictions(SootMethod container, SootMethod target, String modifierOptions) {
    // Check the body of the method to inline for method or field access restrictions
    for (Unit u : target.getActiveBody().getUnits()) {
      Stmt st = (Stmt) u;
      if (st.containsInvokeExpr()
          && !AccessManager.ensureAccess(container, st.getInvokeExpr().getMethod(), modifierOptions)) {
        return false;
      }

      if (st instanceof AssignStmt) {
        Value lhs = ((AssignStmt) st).getLeftOp();
        Value rhs = ((AssignStmt) st).getRightOp();

        if ((lhs instanceof FieldRef && !AccessManager.ensureAccess(container, ((FieldRef) lhs).getField(), modifierOptions))
            || (rhs instanceof FieldRef
                && !AccessManager.ensureAccess(container, ((FieldRef) rhs).getField(), modifierOptions))) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Returns true if this method can be inlined at the given site. Will try as hard as it can to change things to allow
   * inlining (modifierOptions controls what it's allowed to do: safe, unsafe and nochanges)
   *
   * Returns false otherwise.
   */
  public static boolean ensureInlinability(SootMethod target, Stmt toInline, SootMethod container, String modifierOptions) {
    if (!canSafelyInlineInto(target, toInline, container)) {
      if (PRINT_FAILURE_REASONS) {
        System.out.println("[InlinerSafetyManager] failed canSafelyInlineInto checks");
      }
      return false;
    } else if (!AccessManager.ensureAccess(container, target, modifierOptions)) {
      if (PRINT_FAILURE_REASONS) {
        System.out.println("[InlinerSafetyManager] failed AccessManager.ensureAccess checks");
      }
      return false;
    } else if (!checkSpecialInlineRestrictions(container, target, modifierOptions)) {
      if (PRINT_FAILURE_REASONS) {
        System.out.println("[InlinerSafetyManager] failed checkSpecialInlineRestrictions checks");
      }
      return false;
    } else if (!checkAccessRestrictions(container, target, modifierOptions)) {
      if (PRINT_FAILURE_REASONS) {
        System.out.println("[InlinerSafetyManager] failed checkAccessRestrictions checks");
      }
      return false;
    } else {
      return true;
    }
  }

  /**
   * Checks the safety criteria enumerated in section 3.1.4 (Safety Criteria for Method Inlining) of Vijay's thesis.
   */
  private static boolean canSafelyInlineInto(SootMethod inlinee, Stmt toInline, SootMethod container) {
    /* first, check the simple (one-line) safety criteria. */

    // Rule 0: Don't inline constructors.
    if ("<init>".equals(inlinee.getName())) {
      if (PRINT_FAILURE_REASONS) {
        System.out.println("[InlinerSafetyManager] cannot inline constructors");
      }
      return false;
    }

    // Rule 2: inlinee != container.
    if (inlinee.getSignature().equals(container.getSignature())) {
      if (PRINT_FAILURE_REASONS) {
        System.out.println("[InlinerSafetyManager] cannot inline method into itself");
      }
      return false;
    }

    // Rule 3: inlinee is neither native nor abstract.
    if (inlinee.isNative() || inlinee.isAbstract()) {
      if (PRINT_FAILURE_REASONS) {
        System.out.println("[InlinerSafetyManager] cannot inline native or abstract methods");
      }
      return false;
    }

    // Ok, that wraps up the simple criteria. Now for the more
    // complicated criteria.

    // Rule 4: Don't inline away IllegalAccessErrors of the original
    // source code (e.g. by moving a call to a private method
    // *from* a bad class *to* a good class) occuring in the
    // toInline statement.
    // Does not occur for static methods, because there is no base?
    InvokeExpr ie = toInline.getInvokeExpr();
    if (ie instanceof InstanceInvokeExpr) {
      Type baseTy = ((InstanceInvokeExpr) ie).getBase().getType();
      if (baseTy instanceof RefType && invokeThrowsAccessErrorIn(((RefType) baseTy).getSootClass(), inlinee, container)) {
        if (PRINT_FAILURE_REASONS) {
          System.out.println("[InlinerSafetyManager] cannot inline away IllegalAccessErrors");
        }
        return false;
      }
    }

    // Rule 5: Don't inline away any class, method or field access
    // (in inlinee) resulting in an IllegalAccess error.

    // Rule 6: Don't introduce a spurious IllegalAccessError from
    // inlining (by twiddling modifiers).

    // This is better handled by a pre-phase Scene transformation.
    // Inliner Safety should just report the absence of such
    // IllegalAccessErrors after the transformation (and, conversely,
    // their presence without the twiddling.)

    // Rule 7: Don't change semantics of program by moving
    // an invokespecial.
    if (ie instanceof SpecialInvokeExpr) {
      if (specialInvokePerformsLookupIn(ie, inlinee.getDeclaringClass())
          || specialInvokePerformsLookupIn(ie, container.getDeclaringClass())) {
        if (PRINT_FAILURE_REASONS) {
          System.out.println("[InlinerSafetyManager] cannot inline if changes semantics of invokespecial");
        }
        return false;
      }
    }

    return true;
  }

  /**
   * Returns true if any of the following cases holds: 1. inlinee is private, but container.declaringClass() !=
   * inlinee.declaringClass(); or, 2. inlinee is package-visible, and its package differs from that of container; or, 3.
   * inlinee is protected, and either: a. inlinee doesn't belong to container.declaringClass, or any superclass of container;
   * b. the class of the base is not a (non-strict) subclass of container's declaringClass. The base class may be null, in
   * which case 3b is omitted. (for instance, for a static method invocation.)
   */
  private static boolean invokeThrowsAccessErrorIn(SootClass base, SootMethod inlinee, SootMethod container) {
    SootClass inlineeClass = inlinee.getDeclaringClass();
    SootClass containerClass = container.getDeclaringClass();

    // Condition 1 above.
    if (inlinee.isPrivate() && !inlineeClass.getName().equals(containerClass.getName())) {
      return true;
    }

    // Condition 2. Check the package names.
    if (!inlinee.isPrivate() && !inlinee.isProtected() && !inlinee.isPublic()) {
      if (!inlineeClass.getPackageName().equals(containerClass.getPackageName())) {
        return true;
      }
    }

    // Condition 3.
    if (inlinee.isProtected()) {
      // protected means that you can be accessed by your children.
      // i.e. container must be in a child of inlinee.
      Hierarchy h = Scene.v().getActiveHierarchy();
      if (!h.isClassSuperclassOfIncluding(inlineeClass, containerClass)
          && ((base == null) || !h.isClassSuperclassOfIncluding(base, containerClass))) {
        return true;
      }
    }

    return false;
  }

  // m is the method being called; container is the class from which m is being called.
  static boolean specialInvokePerformsLookupIn(InvokeExpr ie, SootClass containerClass) {
    assert (ie instanceof SpecialInvokeExpr);

    // If all of the conditions are true, a lookup is performed.
    SootMethod m = ie.getMethod();
    if ("<init>".equals(m.getName()) || m.isPrivate()) {
      return false;
    }

    // ACC_SUPER must always be set, eh?
    Hierarchy h = Scene.v().getActiveHierarchy();
    return h.isClassSuperclassOf(m.getDeclaringClass(), containerClass);
  }

  private InlinerSafetyManager() {
  }
}
