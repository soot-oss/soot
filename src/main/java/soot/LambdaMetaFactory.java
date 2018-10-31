/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2014 Raja Vallee-Rai and others
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.javaToJimple.LocalGenerator;
import soot.jimple.ClassConstant;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.MethodHandle;
import soot.jimple.MethodType;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;

public final class LambdaMetaFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(LambdaMetaFactory.class);
  // FIXME: Move to 'G'?
  private static int uniq;

  public static SootMethodRef makeLambdaHelper(List<? extends Value> bootstrapArgs, int tag, String name, Type[] types) {
    if (bootstrapArgs.size() < 3 || !(bootstrapArgs.get(0) instanceof MethodType)
        || !(bootstrapArgs.get(1) instanceof MethodHandle) || !(bootstrapArgs.get(2) instanceof MethodType)
        || (bootstrapArgs.size() > 3 && !(bootstrapArgs.get(3) instanceof IntConstant))) {
      LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactor.metaFactory: {}", bootstrapArgs);
      return null;
    }

    MethodType samMethodType = ((MethodType) bootstrapArgs.get(0));
    SootMethodRef implMethod = ((MethodHandle) bootstrapArgs.get(1)).getMethodRef();
    MethodType instantiatedMethodType = ((MethodType) bootstrapArgs.get(2));

    int flags = 0;
    if (bootstrapArgs.size() > 3) {
      flags = ((IntConstant) bootstrapArgs.get(3)).value;
    }

    boolean serializable = (flags & 1 /* FLAGS_SERIALIZABLE */) != 0;
    List<ClassConstant> markerInterfaces = new ArrayList<ClassConstant>();
    List<MethodType> bridges = new ArrayList<MethodType>();

    int va = 4;
    if ((flags & 2 /* FLAG_MARKERS */) != 0) {
      if (va == bootstrapArgs.size() || !(bootstrapArgs.get(va) instanceof IntConstant)) {
        LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
        return null;
      }
      int count = ((IntConstant) bootstrapArgs.get(va++)).value;
      for (int i = 0; i < count; i++) {
        if (va >= bootstrapArgs.size()) {
          LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
          return null;
        }
        Value v = bootstrapArgs.get(va++);
        if (!(v instanceof ClassConstant)) {
          LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
          return null;
        }
        markerInterfaces.add((ClassConstant) v);
      }
    }

    if ((flags & 4 /* FLAG_BRIDGES */) != 0) {
      if (va == bootstrapArgs.size() || !(bootstrapArgs.get(va) instanceof IntConstant)) {
        LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
        return null;
      }
      int count = ((IntConstant) bootstrapArgs.get(va++)).value;
      for (int i = 0; i < count; i++) {
        if (va >= bootstrapArgs.size()) {
          LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
          return null;
        }
        Value v = bootstrapArgs.get(va++);
        if (!(v instanceof MethodType)) {
          LOGGER.warn("LambdaMetaFactory: unexpected arguments for LambdaMetaFactory.altMetaFactory");
          return null;
        }
        bridges.add((MethodType) v);
      }
    }

    List<Type> capTypes = Arrays.asList(types).subList(0, types.length - 1);
    if (!(types[types.length - 1] instanceof RefType)) {
      LOGGER.warn("unexpected interface type: " + types[types.length - 1]);
      return null;
    }
    SootClass iface = ((RefType) types[types.length - 1]).getSootClass();

    // Our thunk class implements the functional interface
    String className = "soot.dummy." + implMethod.name() + "$" + uniqSupply();
    SootClass tclass = new SootClass(className);
    tclass.addInterface(iface);
    if (serializable)
      tclass.addInterface(RefType.v("java.io.Serializable").getSootClass());
    for (int i = 0; i < markerInterfaces.size(); i++)
      tclass.addInterface(RefType.v(markerInterfaces.get(i).getValue()).getSootClass());

    // It contains fields for all the captures in the lambda
    List<SootField> capFields = new ArrayList<SootField>(capTypes.size());
    for (int i = 0; i < capTypes.size(); i++) {
      SootField f = new SootField("cap" + i, capTypes.get(i), 0);
      capFields.add(f);
      tclass.addField(f);
    }

    List<Type> typedParameterTypes = instantiatedMethodType.getParameterTypes();
    Type retType = instantiatedMethodType.getReturnType();
    ThunkMethodSource ms = new ThunkMethodSource(capFields, typedParameterTypes, retType, implMethod);

    // create a method with the concrete, non-erased types first
    SootMethod apply = new SootMethod(name, typedParameterTypes, retType);
    tclass.addMethod(apply);
    apply.setSource(ms);

    // Bootstrap method creates a new instance of this class
    SootMethod tboot = new SootMethod("bootstrap$", capTypes, iface.getType(), Modifier.STATIC);
    tclass.addMethod(tboot);
    tboot.setSource(ms);

    // Constructor just copies the captures
    SootMethod tctor = new SootMethod("<init>", capTypes, VoidType.v());
    tclass.addMethod(tctor);
    tctor.setSource(ms);

    boolean isGeneric = !samMethodType.equals(instantiatedMethodType);
    if (isGeneric) {
      // if the functional interface is generic in it' return-/parameter-types, we have to create bridging method which can
      // work with plain objects and re-routes the call to the actual thunk method
      addBridge(name, tclass, samMethodType, instantiatedMethodType, apply);
    }

    for (int i = 0; i < bridges.size(); i++) {
      final MethodType bridgeType = bridges.get(i);
      addBridge(name, tclass, bridgeType, instantiatedMethodType, apply);
    }

    Scene.v().addClass(tclass);

    // FIXME remove debug stuff
    System.out.println(tboot);

    java.io.PrintWriter pw = new java.io.PrintWriter(System.out);
    Printer.v().printTo(tclass, pw);
    pw.close();

    return tboot.makeRef();
  }

  private static void addBridge(String name, SootClass tclass, MethodType bridgeType, MethodType targetType,
      SootMethod apply) {
    final List<Type> bridgeParams = bridgeType.getParameterTypes();
    final Type bridgeReturn = bridgeType.getReturnType();
    SootMethod genericBridge = new SootMethod(name, bridgeParams, bridgeReturn);
    tclass.addMethod(genericBridge);
    genericBridge.setSource(new BridgeMethodSource(bridgeParams, targetType.getParameterTypes(), bridgeReturn,
        targetType.getReturnType(), apply.makeRef()));
  }

  private static synchronized long uniqSupply() {
    return ++uniq;
  }

  private static class ThunkMethodSource implements MethodSource {
    private List<SootField> capFields;
    private List<Type> paramTypes;
    private Type retType;
    private SootMethodRef implMethod;

    public ThunkMethodSource(List<SootField> capFields, List<Type> paramTypes, Type retType, SootMethodRef implMethod) {
      this.capFields = capFields;
      this.paramTypes = paramTypes;
      this.retType = retType;
      this.implMethod = implMethod;
    }

    public Body getBody(SootMethod m, String phaseName) {
      if (!phaseName.equals("jb"))
        throw new Error("unsupported body type: " + phaseName);

      SootClass tclass = m.getDeclaringClass();
      JimpleBody jb = Jimple.v().newBody(m);
      PatchingChain<Unit> us = jb.getUnits();
      LocalGenerator lc = new LocalGenerator(jb);

      if (m.getName().equals("<init>")) {
        Local l = lc.generateLocal(tclass.getType());
        us.add(Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef(tclass.getType())));
        us.add(Jimple.v()
            .newInvokeStmt(Jimple.v().newSpecialInvokeExpr(l,
                Scene.v().makeConstructorRef(Scene.v().getObjectType().getSootClass(), Collections.<Type>emptyList()),
                Collections.<Value>emptyList())));
        for (SootField f : capFields) {
          int i = us.size() - 2;
          Local l2 = lc.generateLocal(f.getType());
          us.add(Jimple.v().newIdentityStmt(l2, Jimple.v().newParameterRef(f.getType(), i)));
          us.add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(l, f.makeRef()), l2));
        }
        us.add(Jimple.v().newReturnVoidStmt());
      } else if (m.getName().equals("bootstrap$")) {
        Local l = lc.generateLocal(tclass.getType());
        Value val = Jimple.v().newNewExpr(tclass.getType());
        us.add(Jimple.v().newAssignStmt(l, val));
        us.add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(l,
            Scene.v().makeConstructorRef(tclass, Collections.<Type>emptyList()), Collections.<Value>emptyList())));
        us.add(Jimple.v().newReturnStmt(l));
      } else {
        Local this_ = lc.generateLocal(tclass.getType());
        us.add(Jimple.v().newIdentityStmt(this_, Jimple.v().newThisRef(tclass.getType())));

        List<Local> args = new ArrayList<Local>();

        for (SootField f : capFields) {
          int i = args.size();
          Local l = lc.generateLocal(f.getType());
          us.add(Jimple.v().newAssignStmt(l, Jimple.v().newInstanceFieldRef(this_, f.makeRef())));
          args.add(l);
        }

        for (Type ty : paramTypes) {
          int i = args.size();
          Local l = lc.generateLocal(ty);
          us.add(Jimple.v().newIdentityStmt(l, Jimple.v().newParameterRef(ty, i)));
          args.add(l);
        }

        final StaticInvokeExpr invoke = Jimple.v().newStaticInvokeExpr(implMethod, args);

        if (retType == VoidType.v()) {
          us.add(Jimple.v().newInvokeStmt(invoke));
          us.add(Jimple.v().newReturnVoidStmt());
        } else {
          Local ret = lc.generateLocal(retType);
          us.add(Jimple.v().newAssignStmt(ret, invoke));
          us.add(Jimple.v().newReturnStmt(ret));
        }
      }
      return jb;
    }
  }

  private static class BridgeMethodSource implements MethodSource {
    private final List<Type> paramTypes;
    private final List<Type> targetTypes;
    private final Type retType;
    private final SootMethodRef apply;
    private final Type targetRetType;

    public BridgeMethodSource(List<Type> paramTypes, List<Type> targetTypes, Type retType, Type targetRetType,
        SootMethodRef apply) {
      Preconditions.checkArgument(paramTypes.size() == targetTypes.size());
      this.paramTypes = paramTypes;
      this.targetTypes = targetTypes;
      this.retType = retType;
      this.targetRetType = targetRetType;
      this.apply = apply;
    }

    @Override
    public Body getBody(SootMethod m, String phaseName) {
      if (!phaseName.equals("jb"))
        throw new Error("unsupported body type: " + phaseName);

      SootClass tclass = m.getDeclaringClass();
      JimpleBody jb = Jimple.v().newBody(m);
      PatchingChain<Unit> us = jb.getUnits();
      LocalGenerator lc = new LocalGenerator(jb);

      Local this_ = lc.generateLocal(tclass.getType());
      final IdentityStmt thisIdentity = Jimple.v().newIdentityStmt(this_, Jimple.v().newThisRef(tclass.getType()));
      us.addFirst(thisIdentity);

      List<Local> args = new ArrayList<Local>();

      for (int i = 0; i < paramTypes.size(); i++) {
        final Type paramType = paramTypes.get(i);
        final Type targetType = targetTypes.get(i);

        Local paramLocal = lc.generateLocal(paramType);
        us.insertAfter(Jimple.v().newIdentityStmt(paramLocal, Jimple.v().newParameterRef(paramType, i)), thisIdentity);

        final Local targetLocal = lc.generateLocal(targetType);
        us.add(Jimple.v().newAssignStmt(targetLocal, Jimple.v().newCastExpr(paramLocal, targetType)));

        args.add(targetLocal);
      }

      final VirtualInvokeExpr invoke = Jimple.v().newVirtualInvokeExpr(this_, apply, args);

      if (retType == VoidType.v()) {
        us.add(Jimple.v().newInvokeStmt(invoke));
        us.add(Jimple.v().newReturnVoidStmt());
      } else {
        final Local nonCastRet = lc.generateLocal(targetRetType);
        us.add(Jimple.v().newAssignStmt(nonCastRet, invoke));

        final Local ret = lc.generateLocal(retType);
        us.add(Jimple.v().newAssignStmt(ret, Jimple.v().newCastExpr(nonCastRet, retType)));
        us.add(Jimple.v().newReturnStmt(ret));
      }

      return jb;
    }
  }
}
