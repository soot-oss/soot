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

import static soot.dotnet.members.method.DotnetBody.inlineCastExpr;

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
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;

/**
 * ILSpy opcode BinaryNumericInstruction
 */
public class CilBinaryNumericInstruction extends AbstractCilnstruction {
  public CilBinaryNumericInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    Value left = CilInstructionFactory.fromInstructionMsg(instruction.getLeft(), dotnetBody, cilBlock).jimplifyExpr(jb);
    left = inlineCastExpr(left);
    Value right = CilInstructionFactory.fromInstructionMsg(instruction.getRight(), dotnetBody, cilBlock).jimplifyExpr(jb);
    right = inlineCastExpr(right);
    switch (instruction.getOperator()) {
      case Add:
        return Jimple.v().newAddExpr(left, right);
      case Sub:
        return Jimple.v().newSubExpr(left, right);
      case Mul:
        return Jimple.v().newMulExpr(left, right);
      case Div:
        return Jimple.v().newDivExpr(left, right);
      case Rem:
        return Jimple.v().newRemExpr(left, right);
      case BitAnd:
        return Jimple.v().newAndExpr(left, right);
      case BitOr:
        return Jimple.v().newOrExpr(left, right);
      case BitXor:
        return Jimple.v().newXorExpr(left, right);
      case ShiftLeft:
        return Jimple.v().newShlExpr(left, right);
      case ShiftRight:
        if (instruction.getSign().equals(ProtoIlInstructions.IlInstructionMsg.IlSign.Signed)) {
          return Jimple.v().newShrExpr(left, right);
        }
        return Jimple.v().newUshrExpr(left, right);
      default:
        return null;
    }
  }
}
