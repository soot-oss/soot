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
import java.util.List;

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
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.dotnet.members.AbstractDotnetMember;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.toolkits.scalar.Pair;

/**
 * Invoking methods
 */
public class CilCallVirtInstruction extends AbstractCilnstruction {

  private SootClass clazz;
  private DotnetMethod method;

  public CilCallVirtInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
    localsToCastForCall = new ArrayList<>();
  }

  @Override
  public void jimplify(Body jb) {
    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction, dotnetBody, cilBlock);
    Value value = cilExpr.jimplifyExpr(jb);
    InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(value);

    // cast for validation
    if (cilExpr instanceof CilCallVirtInstruction) {
      List<Pair<Local, Local>> locals = ((CilCallVirtInstruction) cilExpr).getLocalsToCastForCall();
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
    clazz = Scene.v().getSootClass(instruction.getMethod().getDeclaringType().getFullname());
    method = new DotnetMethod(instruction.getMethod(), clazz);

    // STATIC
    if (method.isStatic()) {
      checkMethodAvailable();
      List<Local> argsVariables = new ArrayList<>();
      List<Type> argsTypes = new ArrayList<>();

      // If System.Array.Empty
      Value rewriteField = AbstractDotnetMember.checkRewriteCilSpecificMember(clazz, method.getName());
      if (rewriteField != null) {
        return rewriteField;
      }

      // arguments which are passed to this function
      for (int z = 0; z < instruction.getArgumentsCount(); z++) {
        Value variableValue
            = CilInstructionFactory.fromInstructionMsg(instruction.getArguments(z), dotnetBody, cilBlock).jimplifyExpr(jb);
        checkVariabelIsLocal(variableValue, z, false);
        Local variable = (Local) variableValue;
        argsVariables.add(variable);
      }
      // method-parameters (signature)
      for (ProtoAssemblyAllTypes.ParameterDefinition parameterDefinition : method.getParameterDefinitions()) {
        argsTypes.add(DotnetTypeFactory.toSootType(parameterDefinition.getType()));
      }

      String methodName = method.getUniqueName();

      SootMethodRef methodRef = Scene.v().makeMethodRef(clazz, DotnetMethod.convertCtorName(methodName), argsTypes,
          DotnetTypeFactory.toSootType(method.getReturnType()), true);
      return Jimple.v().newStaticInvokeExpr(methodRef, argsVariables);
    }
    // INTERFACE
    else if (clazz.isInterface()) {
      MethodParams methodParams = getMethodCallParams(jb);
      return Jimple.v().newInterfaceInvokeExpr(methodParams.Base, methodParams.MethodRef, methodParams.ArgumentVariables);
    }
    // CONSTRUCTOR and PRIVATE METHOD CALLS
    else if (instruction.getMethod().getIsConstructor()
        || instruction.getMethod().getAccessibility().equals(ProtoAssemblyAllTypes.Accessibility.PRIVATE)) {
      MethodParams methodParams = getMethodCallParams(jb);
      return Jimple.v().newSpecialInvokeExpr(methodParams.Base, methodParams.MethodRef, methodParams.ArgumentVariables);
    }
    // DYNAMIC OBJECT METHOD
    else {
      MethodParams methodParams = getMethodCallParams(jb);
      return Jimple.v().newVirtualInvokeExpr(methodParams.Base, methodParams.MethodRef, methodParams.ArgumentVariables);
    }
  }

  public List<Pair<Local, Local>> getLocalsToCastForCall() {
    return localsToCastForCall;
  }

  private final List<Pair<Local, Local>> localsToCastForCall;

  private MethodParams getMethodCallParams(Body jb) {
    checkMethodAvailable();
    List<Local> argsVariables = new ArrayList<>();
    List<Type> methodParamTypes = new ArrayList<>();
    if (instruction.getArgumentsCount() == 0) {
      throw new RuntimeException("Opcode: " + instruction.getOpCode() + ": Given method " + method.getName()
          + " of declared type " + method.getDeclaringClass().getName()
          + " has no arguments! This means there is no base variable for the virtual invoke!");
    }

    Value baseValue
        = CilInstructionFactory.fromInstructionMsg(instruction.getArguments(0), dotnetBody, cilBlock).jimplifyExpr(jb);
    checkVariabelIsLocal(baseValue, 0, true);
    Local base = (Local) baseValue;

    if (instruction.getArgumentsCount() > 1) {
      for (int z = 1; z < instruction.getArgumentsCount(); z++) {
        Value variableValue
            = CilInstructionFactory.fromInstructionMsg(instruction.getArguments(z), dotnetBody, cilBlock).jimplifyExpr(jb);
        checkVariabelIsLocal(variableValue, z, false);
        Local variable = (Local) variableValue;
        argsVariables.add(variable);
      }
      for (ProtoAssemblyAllTypes.ParameterDefinition parameterDefinition : instruction.getMethod().getParameterList()) {
        methodParamTypes.add(DotnetTypeFactory.toSootType(parameterDefinition.getType()));
      }
    }

    // Check if cast is needed for correct validation, e.g.:
    // System.Object modifiers = null;
    // constructor = virtualinvoke type.<System.Type: ConstructorInfo
    // GetConstructor(System.Reflection.ParameterModifier[])>(modifiers);
    // FastHierarchy is not available at this point, check hierarchy would be easier
    for (int i = 0; i < argsVariables.size(); i++) {
      Local arg = argsVariables.get(i);
      Type methodParam = methodParamTypes.get(i);
      if (arg.getType().toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)
          && !methodParam.toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)) {
        Local castLocal = dotnetBody.variableManager.localGenerator.generateLocal(methodParam);
        localsToCastForCall.add(new Pair<>(arg, castLocal));
        argsVariables.set(i, castLocal);
      }
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

    SootMethodRef methodRef
        = Scene.v().makeMethodRef(clazz, DotnetMethod.convertCtorName(methodName), methodParamTypes, return_type, false);

    return new MethodParams(base, methodRef, argsVariables);
  }

  private void checkMethodAvailable() {
    if (method.getName().trim().isEmpty()) {
      throw new RuntimeException("Opcode: " + instruction.getOpCode() + ": Given method " + method.getName()
          + " of declared type " + method.getDeclaringClass().getName() + " has no method name!");
    }
  }

  private void checkVariabelIsLocal(Value var, int argPos, boolean isBase) {
    String err = "CALL: The given argument ";
    err += argPos;
    err += " ";
    if (isBase) {
      err += "(base variable)";
    }
    err += " of invoked method " + method.getName() + " declared in " + clazz.getName() + " is not a local! "
        + "The value is: " + var.toString() + " of type " + var.getType() + "! " + "The resolving method body is: "
        + dotnetBody.getDotnetMethodSig().getSootMethodSignature().getSignature();

    if (!(var instanceof Local)) {
      throw new RuntimeException(err);
    }
  }

  private static class MethodParams {
    public MethodParams(Local base, SootMethodRef methodRef, List<Local> argumentVariables) {
      Base = base;
      MethodRef = methodRef;
      ArgumentVariables = argumentVariables;
    }

    public Local Base;
    public SootMethodRef MethodRef;
    public List<Local> ArgumentVariables;
  }
}
