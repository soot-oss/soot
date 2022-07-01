package soot.dotnet.instructions;

import java.util.ArrayList;
import java.util.List;

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
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.SootResolver;
import soot.Type;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.MethodHandle;
import soot.jimple.StringConstant;

/**
 * ldtoken was split up by ILspy: LdMemberToken for Method and Field handles and LdTypeToken Load a method/field handle
 * token (e.g. reflection)
 */
public class CilLdMemberTokenInstruction extends AbstractCilnstruction {
  public CilLdMemberTokenInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    if (instruction.hasField()) {
      ProtoAssemblyAllTypes.FieldDefinition field = instruction.getField();
      SootClass declaringClass = SootResolver.v().makeClassRef(field.getDeclaringType().getFullname());

      SootFieldRef sootFieldRef = Scene.v().makeFieldRef(declaringClass, field.getName(),
          DotnetTypeFactory.toSootType(field.getType()), field.getIsStatic());

      int kind = field.getIsStatic() ? MethodHandle.Kind.REF_GET_FIELD_STATIC.getValue()
          : MethodHandle.Kind.REF_GET_FIELD.getValue();
      return MethodHandle.v(sootFieldRef, kind);
    } else if (instruction.hasMethod()) {
      final DotnetMethod method = new DotnetMethod(instruction.getMethod());
      SootClass declaringClass = method.getDeclaringClass();

      if (method.getName().trim().isEmpty()) {
        throw new RuntimeException("Opcode: " + instruction.getOpCode() + ": Given method " + method.getName()
            + " of declared type " + method.getDeclaringClass().getName() + " has no method name!");
      }
      String methodName = method.getUniqueName();

      List<Type> paramTypes = new ArrayList<>();
      for (ProtoAssemblyAllTypes.ParameterDefinition parameterDefinition : method.getParameterDefinitions()) {
        paramTypes.add(DotnetTypeFactory.toSootType(parameterDefinition.getType()));
      }

      SootMethodRef methodRef = Scene.v().makeMethodRef(declaringClass, DotnetMethod.convertCtorName(methodName),
          paramTypes, DotnetTypeFactory.toSootType(method.getReturnType()), method.isStatic());

      int kind;
      if (method.isConstructor()) {
        kind = MethodHandle.Kind.REF_INVOKE_CONSTRUCTOR.getValue();
      } else if (method.isStatic()) {
        kind = MethodHandle.Kind.REF_INVOKE_STATIC.getValue();
      } else if (declaringClass.isInterface()) {
        kind = MethodHandle.Kind.REF_INVOKE_INTERFACE.getValue();
      } else {
        kind = MethodHandle.Kind.REF_INVOKE_VIRTUAL.getValue();
      }
      return MethodHandle.v(methodRef, kind);
    } else {
      return StringConstant.v(instruction.getValueConstantString());
    }
  }
}
