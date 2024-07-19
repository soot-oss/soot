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
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg.IlType;
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
    SootClass clazz = Scene.v().getSootClass(instruction.getMethod().getDeclaringType().getFullname());
    NewExpr newExpr = Jimple.v().newNewExpr(clazz.getType());

    ArrayList<Value> argsVariables = new ArrayList<>();
    ArrayList<Type> argsTypes = new ArrayList<>();
    for (ProtoIlInstructions.IlInstructionMsg a : instruction.getArgumentsList()) {
      addArguments(jb, argsVariables, argsTypes, a);
    }

    // Constructor call expression
    methodRef = Scene.v().makeConstructorRef(clazz, argsTypes);
    listOfArgs = argsVariables;

    return newExpr;
  }

  public void addArguments(Body jb, ArrayList<Value> argsVariables, ArrayList<Type> argsTypes,
      ProtoIlInstructions.IlInstructionMsg a) {
    if (a.hasVariable()) {
      argsVariables.add(dotnetBody.variableManager.addOrGetVariable(a.getVariable(), jb));
      argsTypes.add(DotnetTypeFactory.toSootType(a.getVariable().getType().getFullname()));
    } else {
      IlType t = a.getConstantType();
      if (t == null) {
        throw new RuntimeException("Not a local or constant: " + a);
      }
      switch (t) {
        case type_unknown:
        case UNRECOGNIZED:
          throw new RuntimeException("Not a local or constant: " + a);
        case type_double:
          argsVariables.add(DoubleConstant.v(a.getValueConstantDouble()));
          argsTypes.add(DoubleType.v());
          break;
        case type_float:
          argsVariables.add(FloatConstant.v(a.getValueConstantFloat()));
          argsTypes.add(FloatType.v());
          break;
        case type_int32:
          argsVariables.add(IntConstant.v(a.getValueConstantInt32()));
          argsTypes.add(IntType.v());
          break;
        case type_int64:
          argsVariables.add(LongConstant.v(a.getValueConstantInt64()));
          argsTypes.add(LongType.v());
          break;
        case type_string:
          argsVariables.add(StringConstant.v(a.getValueConstantString()));
          argsTypes.add(RefType.v("java.lang.String"));
          break;
        default:
          throw new RuntimeException("Unsupported: " + t);

      }
    }
  }
}
