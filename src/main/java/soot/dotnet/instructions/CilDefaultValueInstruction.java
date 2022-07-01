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
import soot.Scene;
import soot.SootClass;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;

/**
 * Structs (System.ValueType) instantiation opcode
 */
public class CilDefaultValueInstruction extends AbstractNewObjInstanceInstruction {
  public CilDefaultValueInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    // RefType refType = RefType.v(i.getType().getFullname());
    // return Jimple.v().newNewExpr(refType);

    SootClass clazz = Scene.v().getSootClass(instruction.getType().getFullname());
    NewExpr newExpr = Jimple.v().newNewExpr(clazz.getType());

    methodRef = Scene.v().makeConstructorRef(clazz, new ArrayList<>());
    listOfArgs = new ArrayList<>();

    return newExpr;
  }

}
