package soot.dotnet.instructions;

import java.util.ArrayList;

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
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoAssemblyAllTypes.IlType;
import soot.dotnet.proto.ProtoAssemblyAllTypes.ParameterDefinition;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.NewExpr;
import soot.jimple.StringConstant;

/**
 * Combi instruction with instantiating a new object and calling the constructor (no structs often) Call
 * resolveCallConstructorBody() afterwards in StLoc
 */
public class CilNewObjInstruction extends AbstractNewObjInstanceInstruction {
  public CilNewObjInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    if (!instruction.hasMethod()) {
      throw new RuntimeException("NewObj: There is no method information in the method definiton!");
    }
    SootClass clazz = RefType.v(instruction.getMethod().getDeclaringType().getFullname()).getSootClass();
    NewExpr newExpr = Jimple.v().newNewExpr(clazz.getType());

    ArrayList<Type> argsTypes = new ArrayList<>(instruction.getMethod().getParameterCount());
    for (ParameterDefinition p : instruction.getMethod().getParameterList()) {
      argsTypes.add(DotnetTypeFactory.toSootType(p.getType()));
    }
    ArrayList<Value> argsVariables = new ArrayList<>(instruction.getArgumentsList().size());
    int i = 0;
    for (ProtoIlInstructions.IlInstructionMsg a : instruction.getArgumentsList()) {

      if (a.hasVariable()) {
        argsVariables.add(dotnetBody.variableManager.addOrGetVariable(a.getVariable(), jb));
      } else {
        IlType t = a.getConstantType();
        if (t == null) {
          throw new RuntimeException("Not a local or constant: " + a);
        }
        Value argValue;
        switch (t) {
          case type_unknown:
          case UNRECOGNIZED:
            Value v = CilInstructionFactory.fromInstructionMsg(a, dotnetBody, cilBlock).jimplifyExpr(jb);
            argValue = createTempVar(jb, Jimple.v(), v);
            break;
          case type_double:
            argValue = DoubleConstant.v(a.getValueConstantDouble());
            break;
          case type_float:
            argValue = FloatConstant.v(a.getValueConstantFloat());
            break;
          case type_int32:
            argValue = IntConstant.v((int) a.getValueConstantInt64());
            break;
          case type_int64:
            argValue = LongConstant.v(a.getValueConstantInt64());
            break;
          case type_string:
            argValue = StringConstant.v(a.getValueConstantString());
            break;
          default:
            throw new RuntimeException("Unsupported: " + t);

        }
        Type argDestType = argsTypes.get(i);
        if (argDestType instanceof RefType && argValue.getType() instanceof PrimType) {
          // can happen when enums are expected
          argValue = createTempVar(jb, Jimple.v(), Jimple.v().newCastExpr(argValue, argDestType));
        }
        argsVariables.add(argValue);
        i++;
      }
    }

    // Constructor call expression
    methodRef = Scene.v().makeConstructorRef(clazz, argsTypes);
    listOfArgs = argsVariables;

    return newExpr;
  }

}
