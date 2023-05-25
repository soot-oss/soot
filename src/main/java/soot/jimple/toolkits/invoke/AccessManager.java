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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.Body;
import soot.ClassMember;
import soot.Hierarchy;
import soot.Local;
import soot.LocalGenerator;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.util.Chain;

/**
 * Methods for checking Java scope and visibility requirements.
 */
public class AccessManager {

  /**
   * Returns true iff target is legally accessible from container. Illegal access occurs when any of the following cases
   * holds: (1) target is private, but container.declaringClass() != target.declaringClass(); or, (2) target is
   * package-visible (i.e. default), and its package differs from that of container; or, (3) target is protected and the
   * package of container differs from the package of target and the container doesn't belong to target.declaringClass or any
   * subclass.
   */
  public static boolean isAccessLegal(final SootMethod container, final ClassMember target) {
    final SootClass targetClass = target.getDeclaringClass();
    if (!isAccessLegal(container, targetClass)) {
      return false;
    }

    final SootClass containerClass = container.getDeclaringClass();
    // Condition 1 above.
    if (target.isPrivate() && !targetClass.getName().equals(containerClass.getName())) {
      return false;
    }

    // Condition 2. Check the package names.
    if (!target.isPrivate() && !target.isProtected() && !target.isPublic()) { // i.e. default
      if (!targetClass.getPackageName().equals(containerClass.getPackageName())) {
        return false;
      }
    }

    // Condition 3.
    if (target.isProtected()) {
      // protected means that you can be accessed by your children (i.e.
      // container is in a child of target) and classes in the same package.
      if (!targetClass.getPackageName().equals(containerClass.getPackageName())) {
        Hierarchy h = Scene.v().getActiveHierarchy();
        return h.isClassSuperclassOfIncluding(targetClass, containerClass);
      }
    }

    return true;
  }

  /**
   * Returns true if an access to <code>target</code> is legal from code in <code>container</code>.
   */
  public static boolean isAccessLegal(final SootMethod container, final SootClass target) {
    return target.isPublic() || container.getDeclaringClass().getPackageName().equals(target.getPackageName());
  }

  /**
   * Returns true if the statement <code>stmt</code> contains an illegal access to a field or method, assuming the statement
   * is in method <code>container</code>
   *
   * @param container
   * @param stmt
   * @return
   */
  public static boolean isAccessLegal(final SootMethod container, final Stmt stmt) {
    if (stmt.containsInvokeExpr()) {
      return AccessManager.isAccessLegal(container, stmt.getInvokeExpr().getMethod());
    } else if (stmt instanceof AssignStmt) {
      AssignStmt as = (AssignStmt) stmt;
      Value rightOp = as.getRightOp();
      if (rightOp instanceof FieldRef) {
        return AccessManager.isAccessLegal(container, ((FieldRef) rightOp).getField());
      }
      Value leftOp = as.getLeftOp();
      if (leftOp instanceof FieldRef) {
        return AccessManager.isAccessLegal(container, ((FieldRef) leftOp).getField());
      }
    }
    return true;
  }

  /**
   * Resolves illegal accesses in the interval ]before,after[ by creating accessor methods. <code>before</code> and
   * <code>after</code> can be null to indicate beginning/end respectively.
   *
   * @param body
   * @param before
   * @param after
   */
  public static void createAccessorMethods(final Body body, final Stmt before, final Stmt after) {
    final Chain<Unit> units = body.getUnits();

    if ((before != null && !units.contains(before)) || (after != null && !units.contains(after))) {
      throw new RuntimeException();
    }

    boolean bInside = (before == null);
    for (Unit unit : new ArrayList<Unit>(units)) {
      Stmt s = (Stmt) unit;

      if (bInside) {
        if (s == after) {
          return;
        }
        SootMethod m = body.getMethod();
        if (!isAccessLegal(m, s)) {
          createAccessorMethod(m, s);
        }
      } else if (s == before) {
        bInside = true;
      }
    }
  }

  /**
   * Creates a name for an accessor method.
   *
   * @param member
   * @param setter
   * @return
   */
  public static String createAccessorName(final ClassMember member, final boolean setter) {
    StringBuilder name = new StringBuilder("access$");
    if (member instanceof SootField) {
      name.append(setter ? "set$" : "get$");
      SootField f = (SootField) member;
      name.append(f.getName());
    } else {
      SootMethod m = (SootMethod) member;
      name.append(m.getName()).append('$');
      for (Type type : m.getParameterTypes()) {
        name.append(type.toString().replaceAll("\\.", "\\$\\$")).append('$');
      }
    }
    return name.toString();
  }

  /**
   * Turns a field access or method call into a call to an accessor method. Reuses existing accessors based on name mangling
   * (see createAccessorName)
   *
   * @param container
   * @param stmt
   */
  public static void createAccessorMethod(final SootMethod container, final Stmt stmt) {
    if (!container.getActiveBody().getUnits().contains(stmt)) {
      throw new RuntimeException();
    }

    if (stmt.containsInvokeExpr()) {
      createInvokeAccessor(container, stmt);
    } else if (stmt instanceof AssignStmt) {
      AssignStmt as = (AssignStmt) stmt;
      Value leftOp = as.getLeftOp();
      if (leftOp instanceof FieldRef) {
        // set
        createSetAccessor(container, as, (FieldRef) leftOp);
      } else {
        Value rightOp = as.getRightOp();
        if (rightOp instanceof FieldRef) {
          // get
          createGetAccessor(container, as, (FieldRef) rightOp);
        } else {
          throw new RuntimeException("Expected class member access");
        }
      }
    } else {
      throw new RuntimeException("Expected class member access");
    }
  }

  private static void createGetAccessor(final SootMethod container, final AssignStmt as, final FieldRef ref) {
    final Jimple jimp = Jimple.v();
    final SootClass target = ref.getField().getDeclaringClass();
    String name = createAccessorName(ref.getField(), false);
    SootMethod accessor = target.getMethodByNameUnsafe(name);
    if (accessor == null) {
      final JimpleBody accessorBody = jimp.newBody();
      final Chain<Unit> accStmts = accessorBody.getUnits();
      final LocalGenerator lg = Scene.v().createLocalGenerator(accessorBody);

      List<Type> parameterTypes;
      final Type targetType = target.getType();
      final Local thisLocal = lg.generateLocal(targetType);
      if (ref instanceof InstanceFieldRef) {
        accStmts.addFirst(jimp.newIdentityStmt(thisLocal, jimp.newParameterRef(targetType, 0)));
        parameterTypes = Collections.singletonList(targetType);
      } else {
        parameterTypes = Collections.emptyList();
      }
      final Type refFieldType = ref.getField().getType();
      Local l = lg.generateLocal(refFieldType);
      accStmts.add(
          jimp.newAssignStmt(l, (ref instanceof InstanceFieldRef) ? jimp.newInstanceFieldRef(thisLocal, ref.getFieldRef())
              : jimp.newStaticFieldRef(ref.getFieldRef())));
      accStmts.add(jimp.newReturnStmt(l));

      accessor = Scene.v().makeSootMethod(name, parameterTypes, refFieldType, Modifier.PUBLIC | Modifier.STATIC,
          Collections.emptyList());

      accessorBody.setMethod(accessor);
      accessor.setActiveBody(accessorBody);
      target.addMethod(accessor);
    }

    List<Value> args = (ref instanceof InstanceFieldRef) ? Collections.singletonList(((InstanceFieldRef) ref).getBase())
        : Collections.emptyList();
    as.setRightOp(jimp.newStaticInvokeExpr(accessor.makeRef(), args));
  }

  private static void createSetAccessor(final SootMethod container, final AssignStmt as, final FieldRef ref) {
    final Jimple jimp = Jimple.v();
    final SootClass target = ref.getField().getDeclaringClass();
    final String name = createAccessorName(ref.getField(), true);
    SootMethod accessor = target.getMethodByNameUnsafe(name);
    if (accessor == null) {
      final JimpleBody accessorBody = jimp.newBody();
      final Chain<Unit> accStmts = accessorBody.getUnits();
      final LocalGenerator lg = Scene.v().createLocalGenerator(accessorBody);
      Local thisLocal = lg.generateLocal(target.getType());
      List<Type> parameterTypes = new ArrayList<Type>(2);
      int paramID = 0;
      if (ref instanceof InstanceFieldRef) {
        accStmts.add(jimp.newIdentityStmt(thisLocal, jimp.newParameterRef(target.getType(), paramID)));
        parameterTypes.add(target.getType());
        paramID++;
      }
      parameterTypes.add(ref.getField().getType());
      Local l = lg.generateLocal(ref.getField().getType());
      accStmts.add(jimp.newIdentityStmt(l, jimp.newParameterRef(ref.getField().getType(), paramID)));
      paramID++;
      if (ref instanceof InstanceFieldRef) {
        accStmts.add(jimp.newAssignStmt(jimp.newInstanceFieldRef(thisLocal, ref.getFieldRef()), l));
      } else {
        accStmts.add(jimp.newAssignStmt(jimp.newStaticFieldRef(ref.getFieldRef()), l));
      }
      accStmts.addLast(jimp.newReturnVoidStmt());

      accessor = Scene.v().makeSootMethod(name, parameterTypes, VoidType.v(), Modifier.PUBLIC | Modifier.STATIC,
          Collections.emptyList());
      accessorBody.setMethod(accessor);
      accessor.setActiveBody(accessorBody);
      target.addMethod(accessor);
    }
    {
      ArrayList<Value> args = new ArrayList<Value>(2);
      if (ref instanceof InstanceFieldRef) {
        args.add(((InstanceFieldRef) ref).getBase());
      }
      args.add(as.getRightOp());

      Chain<Unit> containerStmts = container.getActiveBody().getUnits();
      containerStmts.insertAfter(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(accessor.makeRef(), args)), as);
      containerStmts.remove(as);
    }
  }

  private static void createInvokeAccessor(final SootMethod container, final Stmt stmt) {
    final Jimple jimp = Jimple.v();
    final InvokeExpr expr = stmt.getInvokeExpr();
    final SootMethod method = expr.getMethod();
    final SootClass target = method.getDeclaringClass();
    final String name = createAccessorName(method, true);
    SootMethod accessor = target.getMethodByNameUnsafe(name);
    if (accessor == null) {
      final JimpleBody accessorBody = jimp.newBody();
      final Chain<Unit> accStmts = accessorBody.getUnits();
      final LocalGenerator lg = Scene.v().createLocalGenerator(accessorBody);

      List<Type> parameterTypes = new ArrayList<Type>();
      if (expr instanceof InstanceInvokeExpr) {
        parameterTypes.add(target.getType());
      }
      parameterTypes.addAll(method.getParameterTypes());

      List<Local> arguments = new ArrayList<Local>();
      int paramID = 0;
      for (Type type : parameterTypes) {
        Local l = lg.generateLocal(type);
        accStmts.add(jimp.newIdentityStmt(l, jimp.newParameterRef(type, paramID)));
        arguments.add(l);
        paramID++;
      }

      final InvokeExpr accExpr;
      if (expr instanceof StaticInvokeExpr) {
        accExpr = jimp.newStaticInvokeExpr(method.makeRef(), arguments);
      } else if (expr instanceof VirtualInvokeExpr) {
        Local thisLocal = (Local) arguments.get(0);
        arguments.remove(0);
        accExpr = jimp.newVirtualInvokeExpr(thisLocal, method.makeRef(), arguments);
      } else if (expr instanceof SpecialInvokeExpr) {
        Local thisLocal = (Local) arguments.get(0);
        arguments.remove(0);
        accExpr = jimp.newSpecialInvokeExpr(thisLocal, method.makeRef(), arguments);
      } else {
        throw new RuntimeException();
      }

      final Stmt s;
      final Type returnType = method.getReturnType();
      if (returnType instanceof VoidType) {
        s = jimp.newInvokeStmt(accExpr);
        accStmts.add(s);
        accStmts.add(jimp.newReturnVoidStmt());
      } else {
        Local resultLocal = lg.generateLocal(returnType);
        s = jimp.newAssignStmt(resultLocal, accExpr);
        accStmts.add(s);
        accStmts.add(jimp.newReturnStmt(resultLocal));
      }

      accessor = Scene.v().makeSootMethod(name, parameterTypes, returnType, Modifier.PUBLIC | Modifier.STATIC,
          method.getExceptions());
      accessorBody.setMethod(accessor);
      accessor.setActiveBody(accessorBody);
      target.addMethod(accessor);
    }

    List<Value> args = new ArrayList<Value>();
    if (expr instanceof InstanceInvokeExpr) {
      args.add(((InstanceInvokeExpr) expr).getBase());
    }
    args.addAll(expr.getArgs());
    stmt.getInvokeExprBox().setValue(jimp.newStaticInvokeExpr(accessor.makeRef(), args));
  }

  /**
   * Modifies code so that an access to <code>target</code> is legal from code in <code>container</code>.
   *
   * The "accessors" option assumes suitable accessor methods will be created after checking.
   */
  public static boolean ensureAccess(final SootMethod container, final ClassMember target, final String options) {
    final SootClass targetClass = target.getDeclaringClass();
    if (!ensureAccess(container, targetClass, options)) {
      return false;
    }
    if (isAccessLegal(container, target)) {
      return true;
    }
    if (!targetClass.isApplicationClass()) {
      return false;
    }

    if (options != null) {
      switch (options) {
        case "none":
          return false;
        case "accessors":
          return true;
        case "unsafe":
          target.setModifiers(target.getModifiers() | Modifier.PUBLIC);
          return true;
      }
    }
    throw new RuntimeException("Not implemented yet!");
  }

  /**
   * Modifies code so that an access to <code>target</code> is legal from code in <code>container</code>.
   */
  public static boolean ensureAccess(final SootMethod container, final SootClass target, final String options) {
    if (isAccessLegal(container, target)) {
      return true;
    }
    if (options != null) {
      switch (options) {
        case "accessors":
          return false;
        case "none":
          return false;
        case "unsafe":
          if (target.isApplicationClass()) {
            target.setModifiers(target.getModifiers() | Modifier.PUBLIC);
            return true;
          } else {
            return false;
          }
      }
    }
    throw new RuntimeException("Not implemented yet!");
  }

  private AccessManager() {
  }
}
