package soot.jimple.toolkits.reflection;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2010 Eric Bodden
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.Local;
import soot.LocalGenerator;
import soot.Modifier;
import soot.PatchingChain;
import soot.PhaseOptions;
import soot.PrimType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.FieldRef;
import soot.jimple.GotoStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.toolkits.reflection.ReflectionTraceInfo.Kind;
import soot.jimple.toolkits.scalar.CopyPropagator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.NopEliminator;
import soot.options.CGOptions;
import soot.options.Options;
import soot.rtlib.tamiflex.DefaultHandler;
import soot.rtlib.tamiflex.IUnexpectedReflectiveCallHandler;
import soot.rtlib.tamiflex.OpaquePredicate;
import soot.rtlib.tamiflex.ReflectiveCalls;
import soot.rtlib.tamiflex.SootSig;
import soot.rtlib.tamiflex.UnexpectedReflectiveCall;
import soot.toolkits.scalar.UnusedLocalEliminator;
import soot.util.Chain;
import soot.util.HashChain;

public class ReflectiveCallsInliner extends SceneTransformer {

  private static final String ALREADY_CHECKED_FIELDNAME = "SOOT$Reflection$alreadyChecked";

  private static final List<String> fieldSets
      = Arrays.asList("set", "setBoolean", "setByte", "setChar", "setInt", "setLong", "setFloat", "setDouble", "setShort");

  private static final List<String> fieldGets
      = Arrays.asList("get", "getBoolean", "getByte", "getChar", "getInt", "getLong", "getFloat", "getDouble", "getShort");

  // caching currently does not work because it adds fields to Class, Method and Constructor,
  // but such fields cannot currently be added using the Instrumentation API
  private static final boolean useCaching = false;

  private ReflectionTraceInfo RTI;
  private SootMethodRef UNINTERPRETED_METHOD;
  private boolean initialized = false;
  private int callSiteId;
  private int callNum;
  private SootClass reflectiveCallsClass;

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (!this.initialized) {
      final CGOptions cgOptions = new CGOptions(PhaseOptions.v().getPhaseOptions("cg"));
      this.RTI = new ReflectionTraceInfo(cgOptions.reflection_log());
      final Scene scene = Scene.v();
      scene.getSootClass(SootSig.class.getName()).setApplicationClass();
      scene.getSootClass(UnexpectedReflectiveCall.class.getName()).setApplicationClass();
      scene.getSootClass(IUnexpectedReflectiveCallHandler.class.getName()).setApplicationClass();
      scene.getSootClass(DefaultHandler.class.getName()).setApplicationClass();
      scene.getSootClass(OpaquePredicate.class.getName()).setApplicationClass();
      scene.getSootClass(ReflectiveCalls.class.getName()).setApplicationClass();

      SootClass reflectiveCallsClass = new SootClass("soot.rtlib.tamiflex.ReflectiveCallsWrapper", Modifier.PUBLIC);
      scene.addClass(reflectiveCallsClass);
      reflectiveCallsClass.setApplicationClass();
      this.reflectiveCallsClass = reflectiveCallsClass;

      this.UNINTERPRETED_METHOD = scene.makeMethodRef(scene.getSootClass("soot.rtlib.tamiflex.OpaquePredicate"), "getFalse",
          Collections.emptyList(), BooleanType.v(), true);

      if (useCaching) {
        addCaching();
      }

      initializeReflectiveCallsTable();

      this.callSiteId = 0;
      this.callNum = 0;
      this.initialized = true;
    }

    final boolean validate = Options.v().validate();
    for (SootMethod m : RTI.methodsContainingReflectiveCalls()) {
      Body b = m.retrieveActiveBody();
      {
        Set<String> classForNameClassNames = RTI.classForNameClassNames(m);
        if (!classForNameClassNames.isEmpty()) {
          inlineRelectiveCalls(m, classForNameClassNames, ReflectionTraceInfo.Kind.ClassForName);
          if (validate) {
            b.validate();
          }
        }
      }
      {
        Set<String> classNewInstanceClassNames = RTI.classNewInstanceClassNames(m);
        if (!classNewInstanceClassNames.isEmpty()) {
          inlineRelectiveCalls(m, classNewInstanceClassNames, ReflectionTraceInfo.Kind.ClassNewInstance);
          if (validate) {
            b.validate();
          }
        }
      }
      {
        Set<String> constructorNewInstanceSignatures = RTI.constructorNewInstanceSignatures(m);
        if (!constructorNewInstanceSignatures.isEmpty()) {
          inlineRelectiveCalls(m, constructorNewInstanceSignatures, ReflectionTraceInfo.Kind.ConstructorNewInstance);
          if (validate) {
            b.validate();
          }
        }
      }
      {
        Set<String> methodInvokeSignatures = RTI.methodInvokeSignatures(m);
        if (!methodInvokeSignatures.isEmpty()) {
          inlineRelectiveCalls(m, methodInvokeSignatures, ReflectionTraceInfo.Kind.MethodInvoke);
          if (validate) {
            b.validate();
          }
        }
      }
      {
        Set<String> fieldSetSignatures = RTI.fieldSetSignatures(m);
        if (!fieldSetSignatures.isEmpty()) {
          inlineRelectiveCalls(m, fieldSetSignatures, ReflectionTraceInfo.Kind.FieldSet);
          if (validate) {
            b.validate();
          }
        }
      }
      {
        Set<String> fieldGetSignatures = RTI.fieldGetSignatures(m);
        if (!fieldGetSignatures.isEmpty()) {
          inlineRelectiveCalls(m, fieldGetSignatures, ReflectionTraceInfo.Kind.FieldGet);
          if (validate) {
            b.validate();
          }
        }
      }
      // clean up after us
      cleanup(b);
    }
  }

  private void cleanup(Body b) {
    CopyPropagator.v().transform(b);
    DeadAssignmentEliminator.v().transform(b);
    UnusedLocalEliminator.v().transform(b);
    NopEliminator.v().transform(b);
  }

  private void initializeReflectiveCallsTable() {
    final Jimple jimp = Jimple.v();
    final Scene scene = Scene.v();
    final SootClass reflCallsClass = scene.getSootClass("soot.rtlib.tamiflex.ReflectiveCalls");
    final Body body = reflCallsClass.getMethodByName(SootMethod.staticInitializerName).retrieveActiveBody();
    final LocalGenerator localGen = scene.createLocalGenerator(body);
    final Chain<Unit> newUnits = new HashChain<>();
    final SootClass sootClassSet = scene.getSootClass("java.util.Set");
    final RefType refTypeSet = sootClassSet.getType();
    final SootMethodRef addMethodRef = sootClassSet.getMethodByName("add").makeRef();

    int callSiteId = 0;
    for (SootMethod m : RTI.methodsContainingReflectiveCalls()) {
      if (!RTI.classForNameClassNames(m).isEmpty()) {
        SootFieldRef fieldRef = scene.makeFieldRef(reflCallsClass, "classForName", refTypeSet, true);
        Local setLocal = localGen.generateLocal(refTypeSet);
        newUnits.add(jimp.newAssignStmt(setLocal, jimp.newStaticFieldRef(fieldRef)));
        for (String className : RTI.classForNameClassNames(m)) {
          InterfaceInvokeExpr invokeExpr
              = jimp.newInterfaceInvokeExpr(setLocal, addMethodRef, StringConstant.v(callSiteId + className));
          newUnits.add(jimp.newInvokeStmt(invokeExpr));
        }
        callSiteId++;
      }
      if (!RTI.classNewInstanceClassNames(m).isEmpty()) {
        SootFieldRef fieldRef = scene.makeFieldRef(reflCallsClass, "classNewInstance", refTypeSet, true);
        Local setLocal = localGen.generateLocal(refTypeSet);
        newUnits.add(jimp.newAssignStmt(setLocal, jimp.newStaticFieldRef(fieldRef)));
        for (String className : RTI.classNewInstanceClassNames(m)) {
          InterfaceInvokeExpr invokeExpr
              = jimp.newInterfaceInvokeExpr(setLocal, addMethodRef, StringConstant.v(callSiteId + className));
          newUnits.add(jimp.newInvokeStmt(invokeExpr));
        }
        callSiteId++;
      }
      if (!RTI.constructorNewInstanceSignatures(m).isEmpty()) {
        SootFieldRef fieldRef = scene.makeFieldRef(reflCallsClass, "constructorNewInstance", refTypeSet, true);
        Local setLocal = localGen.generateLocal(refTypeSet);
        newUnits.add(jimp.newAssignStmt(setLocal, jimp.newStaticFieldRef(fieldRef)));
        for (String constrSig : RTI.constructorNewInstanceSignatures(m)) {
          InterfaceInvokeExpr invokeExpr
              = jimp.newInterfaceInvokeExpr(setLocal, addMethodRef, StringConstant.v(callSiteId + constrSig));
          newUnits.add(jimp.newInvokeStmt(invokeExpr));
        }
        callSiteId++;
      }
      if (!RTI.methodInvokeSignatures(m).isEmpty()) {
        SootFieldRef fieldRef = scene.makeFieldRef(reflCallsClass, "methodInvoke", refTypeSet, true);
        Local setLocal = localGen.generateLocal(refTypeSet);
        newUnits.add(jimp.newAssignStmt(setLocal, jimp.newStaticFieldRef(fieldRef)));
        for (String methodSig : RTI.methodInvokeSignatures(m)) {
          InterfaceInvokeExpr invokeExpr
              = jimp.newInterfaceInvokeExpr(setLocal, addMethodRef, StringConstant.v(callSiteId + methodSig));
          newUnits.add(jimp.newInvokeStmt(invokeExpr));
        }
        callSiteId++;
      }
    }

    PatchingChain<Unit> units = body.getUnits();
    units.insertAfter(newUnits, units.getPredOf(units.getLast()));
    if (Options.v().validate()) {
      body.validate();
    }
  }

  private void addCaching() {
    final Scene scene = Scene.v();
    final BooleanType bt = BooleanType.v();
    scene.getSootClass("java.lang.reflect.Method").addField(scene.makeSootField(ALREADY_CHECKED_FIELDNAME, bt));
    scene.getSootClass("java.lang.reflect.Constructor").addField(scene.makeSootField(ALREADY_CHECKED_FIELDNAME, bt));
    scene.getSootClass("java.lang.Class").addField(scene.makeSootField(ALREADY_CHECKED_FIELDNAME, bt));

    for (Kind k : Kind.values()) {
      addCaching(k);
    }
  }

  private void addCaching(Kind kind) {
    final Scene scene = Scene.v();
    SootClass c;
    String methodName;
    switch (kind) {
      case ClassNewInstance:
        c = scene.getSootClass("java.lang.Class");
        methodName = "knownClassNewInstance";
        break;
      case ConstructorNewInstance:
        c = scene.getSootClass("java.lang.reflect.Constructor");
        methodName = "knownConstructorNewInstance";
        break;
      case MethodInvoke:
        c = scene.getSootClass("java.lang.reflect.Method");
        methodName = "knownMethodInvoke";
        break;
      case ClassForName:
        // Cannot implement caching in this case because we can add no field
        // to the String argument
        return;
      default:
        throw new IllegalStateException("unknown kind: " + kind);
    }

    final SootMethod m = scene.getSootClass("soot.rtlib.tamiflex.ReflectiveCalls").getMethodByName(methodName);
    final JimpleBody body = (JimpleBody) m.retrieveActiveBody();
    final Chain<Unit> units = body.getUnits();
    final Unit firstStmt = units.getPredOf(body.getFirstNonIdentityStmt());

    final Chain<Unit> newUnits = new HashChain<>();
    final BooleanType bt = BooleanType.v();
    final Jimple jimp = Jimple.v();

    // alreadyCheckedLocal = m.alreadyChecked
    InstanceFieldRef fieldRef = jimp.newInstanceFieldRef(body.getParameterLocal(m.getParameterCount() - 1),
        scene.makeFieldRef(c, ALREADY_CHECKED_FIELDNAME, bt, false));
    LocalGenerator localGen = scene.createLocalGenerator(body);
    Local alreadyCheckedLocal = localGen.generateLocal(bt);
    newUnits.add(jimp.newAssignStmt(alreadyCheckedLocal, fieldRef));

    // if(!alreadyChecked) goto jumpTarget
    Stmt jumpTarget = jimp.newNopStmt();
    newUnits.add(jimp.newIfStmt(jimp.newEqExpr(alreadyCheckedLocal, IntConstant.v(0)), jumpTarget));

    // return
    newUnits.add(jimp.newReturnVoidStmt());

    // jumpTarget: nop
    newUnits.add(jumpTarget);

    // m.alreadyChecked = true
    newUnits.add(jimp.newAssignStmt(jimp.newInstanceFieldRef(body.getParameterLocal(m.getParameterCount() - 1),
        scene.makeFieldRef(c, ALREADY_CHECKED_FIELDNAME, bt, false)), IntConstant.v(1)));

    units.insertAfter(newUnits, firstStmt);

    if (Options.v().validate()) {
      body.validate();
    }
  }

  private void inlineRelectiveCalls(SootMethod m, Set<String> targets, Kind callKind) {
    final Body b = m.retrieveActiveBody();
    final Chain<Unit> units = b.getUnits();
    final Scene scene = Scene.v();
    final LocalGenerator localGen = scene.createLocalGenerator(b);
    final Jimple jimp = Jimple.v();

    // for all units
    for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
      Stmt s = (Stmt) iter.next();
      Chain<Unit> newUnits = new HashChain<>();

      // if we have an invoke expression, test to see if it is a
      // reflective invoke expression
      if (s.containsInvokeExpr()) {
        InvokeExpr ie = s.getInvokeExpr();
        boolean found = false;
        Type fieldSetGetType = null;

        if (callKind == Kind.ClassForName
            && ("<java.lang.Class: java.lang.Class forName(java.lang.String)>".equals(ie.getMethodRef().getSignature())
                || "<java.lang.Class: java.lang.Class forName(java.lang.String,boolean,java.lang.ClassLoader)>"
                    .equals(ie.getMethodRef().getSignature()))) {
          found = true;
          Value classNameValue = ie.getArg(0);
          newUnits.add(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(scene
              .getMethod("<soot.rtlib.tamiflex.ReflectiveCalls: void knownClassForName(int,java.lang.String)>").makeRef(),
              IntConstant.v(callSiteId), classNameValue)));
        } else if (callKind == Kind.ClassNewInstance
            && "<java.lang.Class: java.lang.Object newInstance()>".equals(ie.getMethodRef().getSignature())) {
          found = true;
          Local classLocal = (Local) ((InstanceInvokeExpr) ie).getBase();
          newUnits.add(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(scene
              .getMethod("<soot.rtlib.tamiflex.ReflectiveCalls: void knownClassNewInstance(int,java.lang.Class)>").makeRef(),
              IntConstant.v(callSiteId), classLocal)));
        } else if (callKind == Kind.ConstructorNewInstance
            && "<java.lang.reflect.Constructor: java.lang.Object newInstance(java.lang.Object[])>"
                .equals(ie.getMethodRef().getSignature())) {
          found = true;
          Local constrLocal = (Local) ((InstanceInvokeExpr) ie).getBase();
          newUnits.add(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(scene.getMethod(
              "<soot.rtlib.tamiflex.ReflectiveCalls: void knownConstructorNewInstance(int,java.lang.reflect.Constructor)>")
              .makeRef(), IntConstant.v(callSiteId), constrLocal)));
        } else if (callKind == Kind.MethodInvoke
            && "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>"
                .equals(ie.getMethodRef().getSignature())) {
          found = true;
          Local methodLocal = (Local) ((InstanceInvokeExpr) ie).getBase();
          Value recv = ie.getArg(0);
          newUnits.add(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(scene.getMethod(
              "<soot.rtlib.tamiflex.ReflectiveCalls: void knownMethodInvoke(int,java.lang.Object,java.lang.reflect.Method)>")
              .makeRef(), IntConstant.v(callSiteId), recv, methodLocal)));
        } else if (callKind == Kind.FieldSet) {
          SootMethod sootMethod = ie.getMethodRef().resolve();
          if ("java.lang.reflect.Field".equals(sootMethod.getDeclaringClass().getName())
              && fieldSets.contains(sootMethod.getName())) {
            found = true;
            // assign type of 2nd parameter (1st is receiver object)
            fieldSetGetType = sootMethod.getParameterType(1);
            Value recv = ie.getArg(0);
            Value field = ((InstanceInvokeExpr) ie).getBase();
            newUnits.add(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(scene.getMethod(
                "<soot.rtlib.tamiflex.ReflectiveCalls: void knownFieldSet(int,java.lang.Object,java.lang.reflect.Field)>")
                .makeRef(), IntConstant.v(callSiteId), recv, field)));
          }
        } else if (callKind == Kind.FieldGet) {
          SootMethod sootMethod = ie.getMethodRef().resolve();
          if ("java.lang.reflect.Field".equals(sootMethod.getDeclaringClass().getName())
              && fieldGets.contains(sootMethod.getName())) {
            found = true;
            // assign return type of get
            fieldSetGetType = sootMethod.getReturnType();
            Value recv = ie.getArg(0);
            Value field = ((InstanceInvokeExpr) ie).getBase();
            newUnits.add(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(scene.getMethod(
                "<soot.rtlib.tamiflex.ReflectiveCalls: void knownFieldSet(int,java.lang.Object,java.lang.reflect.Field)>")
                .makeRef(), IntConstant.v(callSiteId), recv, field)));
          }
        }

        if (!found) {
          continue;
        }

        final NopStmt endLabel = jimp.newNopStmt();

        // for all recorded targets
        for (String target : targets) {
          NopStmt jumpTarget = jimp.newNopStmt();

          // boolean predLocal = Opaque.getFalse();
          Local predLocal = localGen.generateLocal(BooleanType.v());
          newUnits.add(jimp.newAssignStmt(predLocal, jimp.newStaticInvokeExpr(UNINTERPRETED_METHOD)));
          // if predLocal == 0 goto <original reflective call>
          newUnits.add(jimp.newIfStmt(jimp.newEqExpr(IntConstant.v(0), predLocal), jumpTarget));

          SootMethod newMethod = createNewMethod(callKind, target, fieldSetGetType);

          List<Value> args = new LinkedList<>();
          switch (callKind) {
            case ClassForName:
            case ClassNewInstance:
              // no arguments
              break;
            case ConstructorNewInstance:
              // add Object[] argument
              args.add(ie.getArgs().get(0));
              break;
            case MethodInvoke:
              // add Object argument
              args.add(ie.getArgs().get(0));
              // add Object[] argument
              args.add(ie.getArgs().get(1));
              break;
            case FieldSet:
              // add Object argument
              args.add(ie.getArgs().get(0));
              // add value argument
              args.add(ie.getArgs().get(1));
              break;
            case FieldGet:
              // add Object argument
              args.add(ie.getArgs().get(0));
              break;
            default:
              throw new IllegalStateException();
          }

          Local retLocal = localGen.generateLocal(newMethod.getReturnType());
          newUnits.add(jimp.newAssignStmt(retLocal, jimp.newStaticInvokeExpr(newMethod.makeRef(), args)));

          if (s instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt) s;
            newUnits.add(jimp.newAssignStmt(assignStmt.getLeftOp(), retLocal));
          }

          GotoStmt gotoStmt = jimp.newGotoStmt(endLabel);
          newUnits.add(gotoStmt);
          newUnits.add(jumpTarget);
        }

        Unit end = newUnits.getLast();
        units.insertAfter(newUnits, s);
        units.remove(s);
        units.insertAfter(s, end);
        units.insertAfter(endLabel, s);
      }
    }
    callSiteId++;
  }

  private SootMethod createNewMethod(Kind callKind, String target, Type fieldSetGetType) {
    List<Type> parameterTypes = new LinkedList<>();
    Type returnType = null;
    switch (callKind) {
      case ClassForName:
        returnType = RefType.v("java.lang.Class");
        break;
      case ClassNewInstance:
        returnType = Scene.v().getObjectType();
        break;
      case ConstructorNewInstance:
        returnType = Scene.v().getObjectType();
        parameterTypes.add(ArrayType.v(returnType, 1));
        break;
      case MethodInvoke:
        returnType = Scene.v().getObjectType();
        parameterTypes.add(returnType);
        parameterTypes.add(ArrayType.v(returnType, 1));
        break;
      case FieldSet:
        returnType = VoidType.v();
        parameterTypes.add(Scene.v().getObjectType());
        parameterTypes.add(fieldSetGetType);
        break;
      case FieldGet:
        returnType = fieldSetGetType;
        parameterTypes.add(Scene.v().getObjectType());
        break;
      default:
        throw new IllegalStateException();
    }

    final Jimple jimp = Jimple.v();
    final Scene scene = Scene.v();
    final SootMethod newMethod = scene.makeSootMethod("reflectiveCall" + (callNum++), parameterTypes, returnType,
        Modifier.PUBLIC | Modifier.STATIC);
    final Body newBody = jimp.newBody(newMethod);
    newMethod.setActiveBody(newBody);
    reflectiveCallsClass.addMethod(newMethod);

    final PatchingChain<Unit> newUnits = newBody.getUnits();
    final LocalGenerator localGen = scene.createLocalGenerator(newBody);

    Local freshLocal;
    Value replacement = null;
    Local[] paramLocals = null;
    switch (callKind) {
      case ClassForName: {
        // replace by: <Class constant for <target>>
        freshLocal = localGen.generateLocal(RefType.v("java.lang.Class"));
        replacement = ClassConstant.v(target.replace('.', '/'));
        break;
      }
      case ClassNewInstance: {
        // replace by: new <target>
        RefType targetType = RefType.v(target);
        freshLocal = localGen.generateLocal(targetType);
        replacement = jimp.newNewExpr(targetType);
        break;
      }
      case ConstructorNewInstance: {
        /*
         * replace r=constr.newInstance(args) by: Object p0 = args[0]; ... Object pn = args[n]; T0 a0 = (T0)p0; ... Tn an =
         * (Tn)pn;
         */
        SootMethod constructor = scene.getMethod(target);
        paramLocals = new Local[constructor.getParameterCount()];
        if (constructor.getParameterCount() > 0) {
          // argArrayLocal = @parameter-0
          ArrayType arrayType = ArrayType.v(Scene.v().getObjectType(), 1);
          Local argArrayLocal = localGen.generateLocal(arrayType);
          newUnits.add(jimp.newIdentityStmt(argArrayLocal, jimp.newParameterRef(arrayType, 0)));
          int i = 0;
          for (Type paramType : constructor.getParameterTypes()) {
            paramLocals[i] = localGen.generateLocal(paramType);
            unboxParameter(argArrayLocal, i, paramLocals, paramType, newUnits, localGen);
            i++;
          }
        }
        RefType targetType = constructor.getDeclaringClass().getType();
        freshLocal = localGen.generateLocal(targetType);
        replacement = jimp.newNewExpr(targetType);
        break;
      }
      case MethodInvoke: {
        /*
         * replace r=m.invoke(obj,args) by: T recv = (T)obj; Object p0 = args[0]; ... Object pn = args[n]; T0 a0 = (T0)p0;
         * ... Tn an = (Tn)pn;
         */
        SootMethod method = scene.getMethod(target);
        // recvObject = @parameter-0
        RefType objectType = Scene.v().getObjectType();
        Local recvObject = localGen.generateLocal(objectType);
        newUnits.add(jimp.newIdentityStmt(recvObject, jimp.newParameterRef(objectType, 0)));
        paramLocals = new Local[method.getParameterCount()];
        if (method.getParameterCount() > 0) {
          // argArrayLocal = @parameter-1
          ArrayType arrayType = ArrayType.v(Scene.v().getObjectType(), 1);
          Local argArrayLocal = localGen.generateLocal(arrayType);
          newUnits.add(jimp.newIdentityStmt(argArrayLocal, jimp.newParameterRef(arrayType, 1)));
          int i = 0;
          for (Type paramType : ((Collection<Type>) method.getParameterTypes())) {
            paramLocals[i] = localGen.generateLocal(paramType);
            unboxParameter(argArrayLocal, i, paramLocals, paramType, newUnits, localGen);
            i++;
          }
        }
        RefType targetType = method.getDeclaringClass().getType();
        freshLocal = localGen.generateLocal(targetType);
        replacement = jimp.newCastExpr(recvObject, method.getDeclaringClass().getType());
        break;
      }
      case FieldSet:
      case FieldGet: {
        /*
         * replace f.set(o,v) by: Object obj = @parameter-0; T freshLocal = (T)obj;
         */
        RefType objectType = Scene.v().getObjectType();
        Local recvObject = localGen.generateLocal(objectType);
        newUnits.add(jimp.newIdentityStmt(recvObject, jimp.newParameterRef(objectType, 0)));

        RefType fieldClassType = scene.getField(target).getDeclaringClass().getType();
        freshLocal = localGen.generateLocal(fieldClassType);
        replacement = jimp.newCastExpr(recvObject, fieldClassType);
        break;
      }
      default:
        throw new InternalError("Unknown kind of reflective call " + callKind);
    }

    final AssignStmt replStmt = jimp.newAssignStmt(freshLocal, replacement);
    newUnits.add(replStmt);

    final Local retLocal = localGen.generateLocal(returnType);
    switch (callKind) {
      case ClassForName: {
        // add: retLocal = freshLocal;
        newUnits.add(jimp.newAssignStmt(retLocal, freshLocal));
        break;
      }
      case ClassNewInstance: {
        // add: freshLocal.<init>()
        newUnits.add(jimp.newInvokeStmt(jimp.newSpecialInvokeExpr(freshLocal, scene.makeMethodRef(scene.getSootClass(target),
            SootMethod.constructorName, Collections.emptyList(), VoidType.v(), false))));
        // add: retLocal = freshLocal
        newUnits.add(jimp.newAssignStmt(retLocal, freshLocal));
        break;
      }
      case ConstructorNewInstance: {
        // add: freshLocal.<target>(a0,...,an);
        newUnits.add(jimp.newInvokeStmt(
            jimp.newSpecialInvokeExpr(freshLocal, scene.getMethod(target).makeRef(), Arrays.asList(paramLocals))));
        // add: retLocal = freshLocal
        newUnits.add(jimp.newAssignStmt(retLocal, freshLocal));
        break;
      }
      case MethodInvoke: {
        // add: freshLocal=recv.<target>(a0,...,an);
        SootMethod method = scene.getMethod(target);
        InvokeExpr invokeExpr;
        if (method.isStatic()) {
          invokeExpr = jimp.newStaticInvokeExpr(method.makeRef(), Arrays.asList(paramLocals));
        } else {
          invokeExpr = jimp.newVirtualInvokeExpr(freshLocal, method.makeRef(), Arrays.asList(paramLocals));
        }
        if (VoidType.v().equals(method.getReturnType())) {
          // method returns null; simply invoke it and return null
          newUnits.add(jimp.newInvokeStmt(invokeExpr));
          newUnits.add(jimp.newAssignStmt(retLocal, NullConstant.v()));
        } else {
          newUnits.add(jimp.newAssignStmt(retLocal, invokeExpr));
        }
        break;
      }
      case FieldSet: {
        // add freshLocal.<f> = v;
        Local value = localGen.generateLocal(fieldSetGetType);
        newUnits.insertBeforeNoRedirect(jimp.newIdentityStmt(value, jimp.newParameterRef(fieldSetGetType, 1)), replStmt);
        SootField field = scene.getField(target);
        Local boxedOrCasted = localGen.generateLocal(field.getType());
        insertCastOrUnboxingCode(boxedOrCasted, value, newUnits);

        FieldRef fieldRef;
        if (field.isStatic()) {
          fieldRef = jimp.newStaticFieldRef(field.makeRef());
        } else {
          fieldRef = jimp.newInstanceFieldRef(freshLocal, field.makeRef());
        }
        newUnits.add(jimp.newAssignStmt(fieldRef, boxedOrCasted));
        break;
      }
      case FieldGet: {
        /*
         * add: T2 temp = recv.<f>; return temp;
         */
        SootField field = scene.getField(target);
        Local value = localGen.generateLocal(field.getType());

        FieldRef fieldRef;
        if (field.isStatic()) {
          fieldRef = jimp.newStaticFieldRef(field.makeRef());
        } else {
          fieldRef = jimp.newInstanceFieldRef(freshLocal, field.makeRef());
        }
        newUnits.add(jimp.newAssignStmt(value, fieldRef));
        insertCastOrBoxingCode(retLocal, value, newUnits);
        break;
      }
    }

    if (!VoidType.v().equals(returnType)) {
      newUnits.add(jimp.newReturnStmt(retLocal));
    }

    if (Options.v().validate()) {
      newBody.validate();
    }

    cleanup(newBody);

    return newMethod;
  }

  private void insertCastOrUnboxingCode(Local lhs, Local rhs, Chain<Unit> newUnits) {
    final Type lhsType = lhs.getType();
    // if assigning to a reference type then there's nothing to do
    if (lhsType instanceof PrimType) {
      final Type rhsType = rhs.getType();
      if (rhsType instanceof PrimType) {
        // insert cast
        newUnits.add(Jimple.v().newAssignStmt(lhs, Jimple.v().newCastExpr(rhs, lhsType)));
      } else {
        // reference type in rhs; insert unboxing code
        RefType boxedType = (RefType) rhsType;
        SootMethodRef ref = Scene.v().makeMethodRef(boxedType.getSootClass(), lhsType.toString() + "Value",
            Collections.emptyList(), lhsType, false);
        newUnits.add(Jimple.v().newAssignStmt(lhs, Jimple.v().newVirtualInvokeExpr(rhs, ref)));
      }
    }
  }

  private void insertCastOrBoxingCode(Local lhs, Local rhs, Chain<Unit> newUnits) {
    final Type lhsType = lhs.getType();
    // if assigning to a primitive type then there's nothing to do
    if (lhsType instanceof RefLikeType) {
      final Type rhsType = rhs.getType();
      if (rhsType instanceof RefLikeType) {
        // insert cast
        newUnits.add(Jimple.v().newAssignStmt(lhs, Jimple.v().newCastExpr(rhs, lhsType)));
      } else {
        // primitive type in rhs; insert boxing code
        RefType boxedType = ((PrimType) rhsType).boxedType();
        SootMethodRef ref = Scene.v().makeMethodRef(boxedType.getSootClass(), "valueOf", Collections.singletonList(rhsType),
            boxedType, true);
        newUnits.add(Jimple.v().newAssignStmt(lhs, Jimple.v().newStaticInvokeExpr(ref, rhs)));
      }
    }
  }

  /**
   * Auto-unboxes an argument array.
   *
   * @param argsArrayLocal
   *          a local holding the argument Object[] array
   * @param paramIndex
   *          the index of the parameter to unbox
   * @param paramType
   *          the (target) type of the parameter
   * @param newUnits
   *          the Unit chain to which the unboxing code will be appended
   * @param localGen
   *          a {@link DefaultLocalGenerator} for the body holding the units
   */
  private void unboxParameter(Local argsArrayLocal, int paramIndex, Local[] paramLocals, Type paramType,
      Chain<Unit> newUnits, LocalGenerator localGen) {
    final Jimple jimp = Jimple.v();
    if (paramType instanceof PrimType) {
      // Unbox the value if needed
      RefType boxedType = ((PrimType) paramType).boxedType();
      Local boxedLocal = localGen.generateLocal(Scene.v().getObjectType());
      newUnits.add(jimp.newAssignStmt(boxedLocal, jimp.newArrayRef(argsArrayLocal, IntConstant.v(paramIndex))));
      Local castedLocal = localGen.generateLocal(boxedType);
      newUnits.add(jimp.newAssignStmt(castedLocal, jimp.newCastExpr(boxedLocal, boxedType)));
      newUnits.add(jimp.newAssignStmt(paramLocals[paramIndex], jimp.newVirtualInvokeExpr(castedLocal, Scene.v()
          .makeMethodRef(boxedType.getSootClass(), paramType + "Value", Collections.emptyList(), paramType, false))));
    } else {
      Local boxedLocal = localGen.generateLocal(Scene.v().getObjectType());
      newUnits.add(jimp.newAssignStmt(boxedLocal, jimp.newArrayRef(argsArrayLocal, IntConstant.v(paramIndex))));
      Local castedLocal = localGen.generateLocal(paramType);
      newUnits.add(jimp.newAssignStmt(castedLocal, jimp.newCastExpr(boxedLocal, paramType)));
      newUnits.add(jimp.newAssignStmt(paramLocals[paramIndex], castedLocal));
    }
  }
}
