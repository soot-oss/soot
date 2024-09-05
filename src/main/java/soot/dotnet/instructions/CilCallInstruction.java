package soot.dotnet.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.FastHierarchy;
import soot.Local;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.dotnet.members.AbstractDotnetMember;
import soot.dotnet.members.ByReferenceWrapperGenerator;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg.IlOpCode;
import soot.dotnet.types.DotNetBasicTypes;
import soot.dotnet.types.DotnetType;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.toolkits.scalar.Pair;

/**
 * Invoking methods
 */
public class CilCallInstruction extends AbstractCilnstruction {

  public CilCallInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
    localsToCastForCall = new ArrayList<>();
  }

  @Override
  public void jimplify(Body jb) {
    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction, dotnetBody, cilBlock);
    cilExpr.jimplifyExpr(jb);
  }

  /**
   * Call Expression
   *
   * @param jb
   * @return
   */
  @Override
  public Value jimplifyExpr(Body jb) {
    String clzname = instruction.getMethod().getDeclaringType().getFullname();
    SootClass clazz = Scene.v().getSootClassUnsafe(clzname);
    if (clazz == null) {
      clazz = Scene.v().forceResolve(clzname, SootClass.SIGNATURES);
    }
    DotnetMethod method = new DotnetMethod(instruction.getMethod(), clazz);
    InvokeExpr invokeEx;
    MethodParams methodParams;
    // STATIC
    if (method.isStatic()) {
      checkMethodAvailable(method);
      // If System.Array.Empty
      Value rewriteField = AbstractDotnetMember.checkRewriteCilSpecificMember(clazz, method.getName());
      if (rewriteField != null) {
        return rewriteField;
      }
      methodParams = getMethodCallParams(method, false, jb);

      invokeEx = Jimple.v().newStaticInvokeExpr(methodParams.methodRef, methodParams.argumentVariables);
    } else {

      methodParams = getMethodCallParams(method, hasBaseObj(), jb);

      invokeEx = createInvokeExpr(jb, clazz, method, methodParams);
    }

    // cast for validation
    List<Pair<Local, Local>> locals = getLocalsToCastForCall();
    if (locals.size() != 0) {
      for (Pair<Local, Local> pair : locals) {
        CastExpr castExpr = Jimple.v().newCastExpr(pair.getO1(), pair.getO2().getType());
        AssignStmt assignStmt = Jimple.v().newAssignStmt(pair.getO2(), castExpr);
        jb.getUnits().add(assignStmt);
      }
    }

    Jimple j = Jimple.v();
    Value ret = null;
    if (methodParams.methodRef.getReturnType() instanceof VoidType) {
      jb.getUnits().add(j.newInvokeStmt(invokeEx));
    } else {
      ret = createTempVar(jb, j, invokeEx);
    }
    if (afterCallUnits != null) {
      for (Unit i : afterCallUnits) {
        jb.getUnits().add(i);
      }
    }

    return ret;

  }

  protected boolean hasBaseObj() {
    return true;
  }

  protected InvokeExpr createInvokeExpr(Body jb, SootClass clazz, DotnetMethod method, MethodParams methodParams) {
    Jimple j = Jimple.v();
    // CONSTRUCTOR and PRIVATE METHOD CALLS
    if (instruction.getMethod().getIsConstructor()
        || instruction.getMethod().getAccessibility().equals(ProtoAssemblyAllTypes.Accessibility.PRIVATE)) {
      return j.newSpecialInvokeExpr(methodParams.base, methodParams.methodRef, methodParams.argumentVariables);
    }

    if (isNonVirtualCall()) {
      return j.newSpecialInvokeExpr(methodParams.base, methodParams.methodRef, methodParams.argumentVariables);
    }

    // INTERFACE
    if (clazz.isInterface()) {
      return j.newInterfaceInvokeExpr(methodParams.base, methodParams.methodRef, methodParams.argumentVariables);
    }
    // DYNAMIC OBJECT METHOD
    else {
      return j.newVirtualInvokeExpr(methodParams.base, methodParams.methodRef, methodParams.argumentVariables);
    }
  }

  protected boolean isNonVirtualCall() {
    return false;
  }

  public List<Pair<Local, Local>> getLocalsToCastForCall() {
    return localsToCastForCall;
  }

  private final List<Pair<Local, Local>> localsToCastForCall;

  protected MethodParams getMethodCallParams(boolean hasBase, Body jb) {
    SootClass clazz = Scene.v().getSootClass(instruction.getMethod().getDeclaringType().getFullname());
    DotnetMethod method = new DotnetMethod(instruction.getMethod(), clazz);
    return getMethodCallParams(method, hasBase, jb);
  }

  protected MethodParams getMethodCallParams(DotnetMethod method, boolean hasBase, Body jb) {

    checkMethodAvailable(method);
    List<Value> argsVariables = new ArrayList<>();
    List<Type> methodParamTypes = new ArrayList<>();
    if (hasBase && instruction.getArgumentsCount() == 0) {
      throw new RuntimeException("Opcode: " + instruction.getOpCode() + ": Given method " + method.getName()
          + " of declared type " + method.getDeclaringClass().getName()
          + " has no arguments! This means there is no base variable for the virtual invoke!");
    }

    int startIdx = 0;
    Local base = null;
    if (hasBase) {
      Value baseValue
          = CilInstructionFactory.fromInstructionMsg(instruction.getArguments(0), dotnetBody, cilBlock).jimplifyExpr(jb);
      baseValue = simplifyComplexExpression(jb, baseValue);
      if (baseValue instanceof Constant) {
        baseValue = createTempVar(jb, Jimple.v(), baseValue);
      }
      base = (Local) baseValue;
      startIdx = 1;
    }

    Map<Integer, SootClass> byRefWrappers = new HashMap<>();
    FieldRef[] byRefField = new FieldRef[instruction.getArgumentsCount()];
    if (instruction.getArgumentsCount() > startIdx) {
      for (int z = startIdx; z < instruction.getArgumentsCount(); z++) {
        IlInstructionMsg arg = instruction.getArguments(z);
        CilInstruction inst = CilInstructionFactory.fromInstructionMsg(arg, dotnetBody, cilBlock);
        Value argValue = inst.jimplifyExpr(jb);
        if (arg.getOpCode() == IlOpCode.LDFLDA || arg.getOpCode() == IlOpCode.LDSFLDA) {
          //by reference a field
          if (!ByReferenceWrapperGenerator.needsWrapper(instruction.getMethod().getParameterList().get(z))) {
            throw new IllegalStateException(
                "We load an address of a field, but apparently this is not a by-reference paramter?");
          }
          if (!(argValue instanceof FieldRef)) {
            throw new IllegalStateException("Expected a field reference");
          }
          byRefField[z - startIdx] = (FieldRef) argValue;
        }
        argValue = simplifyComplexExpression(jb, argValue);
        argsVariables.add(argValue);
      }
      int p = 0;
      for (ProtoAssemblyAllTypes.ParameterDefinition parameterDefinition : instruction.getMethod().getParameterList()) {
        Type t = DotnetTypeFactory.toSootType(parameterDefinition.getType());
        if (ByReferenceWrapperGenerator.needsWrapper(parameterDefinition)) {
          SootClass sc = ByReferenceWrapperGenerator.getWrapperClass(t);
          t = sc.getType();
          byRefWrappers.put(p, sc);
        }
        methodParamTypes.add(t);
        p++;
      }
    }

    final FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
    final RefType valueType = RefType.v(DotNetBasicTypes.SYSTEM_VALUETYPE);

    // Check if cast is needed for correct validation, e.g.:
    // System.Object modifiers = null;
    // constructor = virtualinvoke type.<System.Type: ConstructorInfo
    // GetConstructor(System.Reflection.ParameterModifier[])>(modifiers);
    for (int i = 0; i < argsVariables.size(); i++) {
      final Value originalArg = argsVariables.get(i);
      Value modifiedArg = originalArg;
      Type methodParam = methodParamTypes.get(i);
      Type argType = modifiedArg.getType();
      SootClass wrapped = byRefWrappers.get(i);
      if (wrapped != null) {
        modifiedArg = ByReferenceWrapperGenerator.insertWrapperCall(jb, wrapped, modifiedArg);
        if (afterCallUnits == null) {
          afterCallUnits = new ArrayList<>();
        }
        afterCallUnits.add(ByReferenceWrapperGenerator.getUnwrapCall(wrapped, modifiedArg, originalArg));
        FieldRef fieldRef = byRefField[i];
        if (fieldRef != null) {
          //by field reference
          afterCallUnits.add(Jimple.v().newAssignStmt(fieldRef, originalArg));
        }
      } else {
        if (modifiedArg instanceof Local) {
          if (argType.toString().equals(DotNetBasicTypes.SYSTEM_OBJECT)
              && !methodParam.toString().equals(DotNetBasicTypes.SYSTEM_OBJECT)) {
            Local castLocal = dotnetBody.variableManager.localGenerator.generateLocal(methodParam);
            localsToCastForCall.add(new Pair<>((Local) modifiedArg, castLocal));
            modifiedArg = castLocal;
          }
        }
        if (methodParam instanceof RefType && argType instanceof PrimType) {
          // can happen when enums are expected
          modifiedArg = createTempVar(jb, Jimple.v(), Jimple.v().newCastExpr(modifiedArg, methodParam));
        }
        Type modType = modifiedArg.getType();
        if (fh.canStoreType(modType, valueType) && modifiedArg instanceof Local) {
          // we need to copy structs!
          RefType rt = (RefType) modType;
          SootMethod cm = DotnetType.getCopyMethod(rt.getSootClass());
          if (cm != null) {
            Jimple j = Jimple.v();
            modifiedArg = createTempVar(jb, j, j.newSpecialInvokeExpr((Local) modifiedArg, cm.makeRef()));
          }
        }
      }
      argsVariables.set(i, modifiedArg);
    }

    String methodName = method.getUniqueName();

    // return type of the method
    // due to unsafe methods with "void*" rewrite this
    Type return_type;
    if (method.getReturnType().getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.POINTER)
        && method.getReturnType().getFullname().equals(DotNetBasicTypes.SYSTEM_VOID)) {
      return_type = DotnetTypeFactory.toSootType(method.getProtoMessage().getDeclaringType());
    } else {
      return_type = DotnetTypeFactory.toSootType(method.getProtoMessage().getReturnType());
    }

    SootMethodRef methodRef = Scene.v().makeMethodRef(method.getDeclaringClass(), DotnetMethod.convertCtorName(methodName),
        methodParamTypes, return_type, !hasBase);

    return new MethodParams(base, methodRef, argsVariables);
  }

  private void checkMethodAvailable(DotnetMethod method) {
    if (method.getName().trim().isEmpty()) {
      throw new RuntimeException("Opcode: " + instruction.getOpCode() + ": Given method " + method.getName()
          + " of declared type " + method.getDeclaringClass().getName() + " has no method name!");
    }
  }

  private List<Unit> afterCallUnits;

  static class MethodParams {
    public MethodParams(Local base, SootMethodRef methodRef, List<Value> argsVariables) {
      this.base = base;
      this.methodRef = methodRef;
      this.argumentVariables = argsVariables;
    }

    public Local base;
    public SootMethodRef methodRef;
    public List<Value> argumentVariables;
  }

}
