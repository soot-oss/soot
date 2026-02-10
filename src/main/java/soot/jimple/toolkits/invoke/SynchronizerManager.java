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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.G;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.Singletons;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.util.Chain;

/** Utility methods for dealing with synchronization. */
public class SynchronizerManager {

  /** Maps classes to class$ fields. Don't trust default. */
  public HashMap<SootClass, SootField> classToClassField = new HashMap<SootClass, SootField>();

  public SynchronizerManager(Singletons.Global g) {
  }

  public static SynchronizerManager v() {
    return G.v().soot_jimple_toolkits_invoke_SynchronizerManager();
  }

  /**
   * Adds code to fetch the static Class object to the given JimpleBody before the target Stmt.
   *
   * Uses our custom classToClassField field to cache the results.
   *
   * The code will look like this:
   *
   * <pre>
   * $r3 = <quack: java.lang.Class class$quack>;
   * .if $r3 != .null .goto label2;
   *
   * $r3 = .staticinvoke <quack: java.lang.Class class$(java.lang.String)>("quack");
   * <quack: java.lang.Class class$quack> = $r3;
   *
   * label2:
   * </pre>
   */
  public Local addStmtsToFetchClassBefore(JimpleBody b, Stmt target) {
    return addStmtsToFetchClassBefore(b, target, false);
  }

  /**
   * Adds code to fetch the static Class object to the given JimpleBody before the target Stmt.
   *
   * Uses our custom classToClassField field to cache the results.
   *
   * The code will look like this:
   *
   * <pre>
   * $r3 = <quack: java.lang.Class class$quack>;
   * .if $r3 != .null .goto label2;
   *
   * $r3 = .staticinvoke <quack: java.lang.Class class$(java.lang.String)>("quack");
   * <quack: java.lang.Class class$quack> = $r3;
   *
   * label2:
   * </pre>
   */
  public Local addStmtsToFetchClassBefore(ShimpleBody b, Stmt target) {
    return addStmtsToFetchClassBefore(b, target, true);
  }

  /**
   * Adds code to fetch the static Class object to the given JimpleBody before the target Stmt.
   *
   * Uses our custom classToClassField field to cache the results.
   *
   * The code will look like this:
   *
   * <pre>
   * $r3 = <quack: java.lang.Class class$quack>;
   * .if $r3 != .null .goto label2;
   *
   * $r3 = .staticinvoke <quack: java.lang.Class class$(java.lang.String)>("quack");
   * <quack: java.lang.Class class$quack> = $r3;
   *
   * label2:
   * </pre>
   */
  Local addStmtsToFetchClassBefore(Body b, Stmt target) {
    assert (b instanceof JimpleBody || b instanceof ShimpleBody);
    return addStmtsToFetchClassBefore(b, target, b instanceof ShimpleBody);
  }

  private Local addStmtsToFetchClassBefore(Body b, Stmt target, boolean createNewAsShimple) {
    SootClass sc = b.getMethod().getDeclaringClass();
    SootField classCacher = classToClassField.get(sc);
    if (classCacher == null) {
      // Add a unique field named [__]class$name
      String n = "class$" + sc.getName().replace('.', '$');
      while (sc.declaresFieldByName(n)) {
        n = '_' + n;
      }

      classCacher = Scene.v().makeSootField(n, RefType.v("java.lang.Class"), Modifier.STATIC);
      sc.addField(classCacher);
      classToClassField.put(sc, classCacher);
    }

    final Chain<Local> locals = b.getLocals();

    // Find unique name. Not strictly necessary unless we parse Jimple code.
    String lName = "$uniqueClass";
    while (true) {
      boolean oops = false;
      for (Local jbLocal : locals) {
        if (lName.equals(jbLocal.getName())) {
          oops = true;
        }
      }
      if (!oops) {
        break;
      }
      lName = '_' + lName;
    }

    final Jimple jimp = Jimple.v();
    Local l = jimp.newLocal(lName, RefType.v("java.lang.Class"));
    locals.add(l);

    final Chain<Unit> units = b.getUnits();
    units.insertBefore(jimp.newAssignStmt(l, jimp.newStaticFieldRef(classCacher.makeRef())), target);

    units.insertBefore(jimp.newIfStmt(jimp.newNeExpr(l, NullConstant.v()), target), target);

    units.insertBefore(jimp.newAssignStmt(l, jimp.newStaticInvokeExpr(getClassFetcherFor(sc, createNewAsShimple).makeRef(),
        Collections.singletonList(StringConstant.v(sc.getName())))), target);
    units.insertBefore(jimp.newAssignStmt(jimp.newStaticFieldRef(classCacher.makeRef()), l), target);

    return l;
  }

  /**
   * @see #getClassFetcherFor(soot.SootClass, boolean)
   */
  public SootMethod getClassFetcherFor(SootClass c) {
    return getClassFetcherFor(c, false);
  }

  /**
   * Finds a method which calls java.lang.Class.forName(String). Searches for names class$, _class$, __class$, etc. If no
   * such method is found, creates one and returns it.
   *
   * Uses dumb matching to do search. Not worth doing symbolic analysis for this!
   */
  public SootMethod getClassFetcherFor(final SootClass c, boolean createNewAsShimple) {
    final String prefix = '<' + c.getName().replace('.', '$') + ": java.lang.Class ";
    for (String methodName = "class$"; true; methodName = '_' + methodName) {
      SootMethod m = c.getMethodByNameUnsafe(methodName);
      if (m == null) {
        return createClassFetcherFor(c, methodName, createNewAsShimple);
      }

      // Check signature.
      if (!(prefix + methodName + "(java.lang.String)>").equals(m.getSignature())) {
        continue;
      }

      /* we now look for the following fragment: */
      /*
       * r0 := @parameter0: java.lang.String; $r2 = .staticinvoke <java.lang.Class: java.lang.Class
       * forName(java.lang.String)>(r0); .return $r2;
       *
       * Ignore the catching code; this is enough.
       */
      final Iterator<Unit> unitsIt = m.retrieveActiveBody().getUnits().iterator();
      if (!unitsIt.hasNext()) {
        continue;
      }
      Stmt s = (Stmt) unitsIt.next();
      if (!(s instanceof IdentityStmt)) {
        continue;
      }

      final IdentityStmt is = (IdentityStmt) s;
      final Value ro = is.getRightOp();
      if (!(ro instanceof ParameterRef) || (((ParameterRef) ro).getIndex() != 0) || !unitsIt.hasNext()) {
        continue;
      }
      s = (Stmt) unitsIt.next();
      if (!(s instanceof AssignStmt)) {
        continue;
      }

      final AssignStmt as = (AssignStmt) s;
      if (!(".staticinvoke <java.lang.Class: java.lang.Class forName(java.lang.String)>(" + is.getLeftOp() + ")")
          .equals(as.getRightOp().toString()) || !unitsIt.hasNext()) {
        continue;
      }
      s = (Stmt) unitsIt.next();
      if (!(s instanceof ReturnStmt)) {
        continue;
      }

      if (!((ReturnStmt) s).getOp().equivTo(as.getLeftOp())) {
        continue;
      }

      // don't care about rest. we have sufficient code.
      // in particular, it certainly returns Class.forName(arg).
      return m;
    }
  }

  /**
   * @see #createClassFetcherFor(soot.SootClass, java.lang.String, boolean)
   */
  public SootMethod createClassFetcherFor(SootClass c, String methodName) {
    return createClassFetcherFor(c, methodName, false);
  }

  /**
   * Creates a method which calls java.lang.Class.forName(String).
   *
   * The method should look like the following:
   *
   * <code><pre>
           .static java.lang.Class class$(java.lang.String)
           {
               java.lang.String r0, $r5;
               java.lang.ClassNotFoundException r1, $r3;
               java.lang.Class $r2;
               java.lang.NoClassDefFoundError $r4;
  
               r0 := @parameter0: java.lang.String;
  
           label0:
               $r2 = .staticinvoke <java.lang.Class: java.lang.Class forName(java.lang.String)>(r0);
               .return $r2;
  
           label1:
               $r3 := @caughtexception;
               r1 = $r3;
               $r4 = .new java.lang.NoClassDefFoundError;
               $r5 = .virtualinvoke r1.<java.lang.Throwable: java.lang.String getMessage()>();
               .specialinvoke $r4.<java.lang.NoClassDefFoundError: .void <init>(java.lang.String)>($r5);
               .throw $r4;
  
               .catch java.lang.ClassNotFoundException .from label0 .to label1 .with label1;
           }
   * </pre></code>
   */
  public SootMethod createClassFetcherFor(SootClass c, String methodName, boolean createNewAsShimple) {
    final RefType refTyString = RefType.v("java.lang.String");
    final RefType refTypeClass = RefType.v("java.lang.Class");
    final Scene scene = Scene.v();

    // Create the method
    SootMethod method
        = scene.makeSootMethod(methodName, Collections.singletonList(refTyString), refTypeClass, Modifier.STATIC);
    c.addMethod(method);

    // Create the method body
    {
      final Jimple jimp = Jimple.v();
      Body body = jimp.newBody(method);

      // Add some locals
      Local l_r0, l_r1, l_r2, l_r3, l_r4, l_r5;
      final RefType refTypeClassNotFoundException = RefType.v("java.lang.ClassNotFoundException");
      final RefType refTypeNoClassDefFoundError = RefType.v("java.lang.NoClassDefFoundError");
      final Chain<Local> locals = body.getLocals();
      locals.add(l_r0 = jimp.newLocal("r0", refTyString));
      locals.add(l_r1 = jimp.newLocal("r1", refTypeClassNotFoundException));
      locals.add(l_r2 = jimp.newLocal("$r2", refTypeClass));
      locals.add(l_r3 = jimp.newLocal("$r3", refTypeClassNotFoundException));
      locals.add(l_r4 = jimp.newLocal("$r4", refTypeNoClassDefFoundError));
      locals.add(l_r5 = jimp.newLocal("$r5", refTyString));

      final Chain<Unit> units = body.getUnits();

      // add "r0 := @parameter0: java.lang.String"
      units.add(jimp.newIdentityStmt(l_r0, jimp.newParameterRef(refTyString, 0)));

      // add "$r2 = .staticinvoke <java.lang.Class: java.lang.Class
      // forName(java.lang.String)>(r0);
      AssignStmt asi = jimp.newAssignStmt(l_r2,
          jimp.newStaticInvokeExpr(scene.getMethod("<java.lang.Class: java.lang.Class forName(java.lang.String)>").makeRef(),
              Collections.singletonList(l_r0)));
      units.add(asi);

      // insert "return $r2;"
      units.add(jimp.newReturnStmt(l_r2));

      // add "r3 := @caughtexception;"
      Stmt handlerStart = jimp.newIdentityStmt(l_r3, jimp.newCaughtExceptionRef());
      units.add(handlerStart);

      // add "r1 = r3;"
      units.add(jimp.newAssignStmt(l_r1, l_r3));

      // add "$r4 = .new java.lang.NoClassDefFoundError;"
      units.add(jimp.newAssignStmt(l_r4, jimp.newNewExpr(refTypeNoClassDefFoundError)));

      // add "$r5 = virtualinvoke r1.<java.lang.Throwable:
      // java.lang.String getMessage()>();"
      units.add(jimp.newAssignStmt(l_r5, jimp.newVirtualInvokeExpr(l_r1,
          scene.getMethod("<java.lang.Throwable: java.lang.String getMessage()>").makeRef(), Collections.emptyList())));

      // add .specialinvoke $r4.<java.lang.NoClassDefFoundError: .void
      // <init>(java.lang.String)>($r5);
      units.add(jimp.newInvokeStmt(jimp.newSpecialInvokeExpr(l_r4,
          scene.getMethod("<java.lang.NoClassDefFoundError: void <init>(java.lang.String)>").makeRef(),
          Collections.singletonList(l_r5))));

      // add .throw $r4;
      units.add(jimp.newThrowStmt(l_r4));

      body.getTraps().add(jimp.newTrap(refTypeClassNotFoundException.getSootClass(), asi, handlerStart, handlerStart));

      // Convert to Shimple if requested and then store it to the method
      if (createNewAsShimple) {
        body = Shimple.v().newBody(body);
      }
      method.setActiveBody(body);
    }

    return method;
  }

  /**
   * Wraps stmt around a monitor associated with local lock. When inlining or static method binding, this is the former base
   * of the invoke expression.
   */
  public void synchronizeStmtOn(Stmt stmt, JimpleBody b, Local lock) {
    synchronizeStmtOn(stmt, (Body) b, lock);
  }

  /**
   * Wraps stmt around a monitor associated with local lock. When inlining or static method binding, this is the former base
   * of the invoke expression.
   */
  public void synchronizeStmtOn(Stmt stmt, ShimpleBody b, Local lock) {
    synchronizeStmtOn(stmt, (Body) b, lock);
  }

  /**
   * Wraps stmt around a monitor associated with local lock. When inlining or static method binding, this is the former base
   * of the invoke expression.
   */
  void synchronizeStmtOn(Stmt stmt, Body b, Local lock) {
    assert (b instanceof JimpleBody || b instanceof ShimpleBody);

    final Jimple jimp = Jimple.v();
    final Chain<Unit> units = b.getUnits();

    // TrapManager.splitTrapsAgainst(b, stmt, units.getSuccOf(stmt));
    units.insertBefore(jimp.newEnterMonitorStmt(lock), stmt);

    Stmt exitMon = jimp.newExitMonitorStmt(lock);
    units.insertAfter(exitMon, stmt);

    // Ok. That was the easy part.
    // We must also add a catch Throwable exception block in the appropriate place.
    {
      Stmt newGoto = jimp.newGotoStmt(units.getSuccOf(exitMon));
      units.insertAfter(newGoto, exitMon);

      Local eRef = jimp.newLocal("__exception", Scene.v().getBaseExceptionType());
      b.getLocals().add(eRef);

      List<Unit> l = new ArrayList<Unit>();
      Stmt handlerStmt = jimp.newIdentityStmt(eRef, jimp.newCaughtExceptionRef());
      l.add(handlerStmt);
      l.add((Unit) exitMon.clone());
      l.add(jimp.newThrowStmt(eRef));
      units.insertAfter(l, newGoto);

      b.getTraps().addFirst(jimp.newTrap(Scene.v().getSootClass(Scene.v().getBaseExceptionType().toString()), stmt,
          units.getSuccOf(stmt), handlerStmt));
    }
  }
}
