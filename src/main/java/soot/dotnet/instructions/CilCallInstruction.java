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

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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
import soot.dotnet.members.AbstractDotnetMember;
import soot.dotnet.members.ByReferenceWrapperGenerator;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.dotnet.types.DotnetType;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.InvokeStmt;
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
    Value value = cilExpr.jimplifyExpr(jb);
    InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(value);

    // cast for validation
    if (cilExpr instanceof CilCallInstruction) {
      List<Pair<Local, Local>> locals = ((CilCallInstruction) cilExpr).getLocalsToCastForCall();
      if (locals.size() != 0) {
        for (Pair<Local, Local> pair : locals) {
          CastExpr castExpr = Jimple.v().newCastExpr(pair.getO1(), pair.getO2().getType());
          AssignStmt assignStmt = Jimple.v().newAssignStmt(pair.getO2(), castExpr);
          jb.getUnits().add(assignStmt);
        }
      }
    }

    jb.getUnits().add(invokeStmt);
  }

  /**
   * Call Expression
   *
   * @param jb
   * @return
   */
  @Override
  public Value jimplifyExpr(Body jb) {

    SootClass clazz = Scene.v().getSootClass(instruction.getMethod().getDeclaringType().getFullname());
    DotnetMethod method = new DotnetMethod(instruction.getMethod(), clazz);
    // STATIC
    if (method.isStatic()) {
      checkMethodAvailable(method);
      // If System.Array.Empty
      Value rewriteField = AbstractDotnetMember.checkRewriteCilSpecificMember(clazz, method.getName());
      if (rewriteField != null) {
        return rewriteField;
      }
      MethodParams methodParams = getMethodCallParams(method, false, jb);

      return Jimple.v().newStaticInvokeExpr(methodParams.methodRef, methodParams.argumentVariables);
    }
    // INTERFACE
    else if (clazz.isInterface()) {
      MethodParams methodParams = getMethodCallParams(method, true, jb);
      return Jimple.v().newInterfaceInvokeExpr(methodParams.base, methodParams.methodRef, methodParams.argumentVariables);
    }
    // CONSTRUCTOR and PRIVATE METHOD CALLS
    else if (instruction.getMethod().getIsConstructor()
        || instruction.getMethod().getAccessibility().equals(ProtoAssemblyAllTypes.Accessibility.PRIVATE)) {
      MethodParams methodParams = getMethodCallParams(method, true, jb);
      return Jimple.v().newSpecialInvokeExpr(methodParams.base, methodParams.methodRef, methodParams.argumentVariables);
    }
    // DYNAMIC OBJECT METHOD
    else {
      MethodParams methodParams = getMethodCallParams(method, true, jb);
      return Jimple.v().newVirtualInvokeExpr(methodParams.base, methodParams.methodRef, methodParams.argumentVariables);
    }
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
    if (instruction.getArgumentsCount() > startIdx) {
      for (int z = startIdx; z < instruction.getArgumentsCount(); z++) {
        Value argValue
            = CilInstructionFactory.fromInstructionMsg(instruction.getArguments(z), dotnetBody, cilBlock).jimplifyExpr(jb);
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
    final RefType valueType = RefType.v("System.ValueType");

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
      } else {
        if (modifiedArg instanceof Local) {
          if (argType.toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)
              && !methodParam.toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)) {
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
        && method.getReturnType().getFullname().equals(DotnetBasicTypes.SYSTEM_VOID)) {
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

  public void afterCall(Body jb, Local variableObject) {
    if (afterCallUnits != null) {
      jb.getUnits().addAll(afterCallUnits);
    }
  }

}
