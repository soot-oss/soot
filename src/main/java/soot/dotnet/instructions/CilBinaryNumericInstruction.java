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
    left = simplifyComplexExpression(jb, left);
    Value right = CilInstructionFactory.fromInstructionMsg(instruction.getRight(), dotnetBody, cilBlock).jimplifyExpr(jb);
    right = inlineCastExpr(right);
    right = simplifyComplexExpression(jb, right);
    Jimple jimple = Jimple.v();
    switch (instruction.getOperator()) {
      case Add:
        if (instruction.getCheckForOverflow()) {
          return jimple.newCheckedAddExpr(left, right);
        } else {
          return jimple.newAddExpr(left, right);
        }
      case Sub:
        if (instruction.getCheckForOverflow()) {
          return jimple.newCheckedSubExpr(left, right);
        } else {
          return jimple.newSubExpr(left, right);
        }
      case Mul:
        if (instruction.getCheckForOverflow()) {
          return jimple.newCheckedMulExpr(left, right);
        } else {
          return jimple.newMulExpr(left, right);
        }
      case Div:
        return jimple.newDivExpr(left, right);
      case Rem:
        return jimple.newRemExpr(left, right);
      case BitAnd:
        return jimple.newAndExpr(left, right);
      case BitOr:
        return jimple.newOrExpr(left, right);
      case BitXor:
        return jimple.newXorExpr(left, right);
      case ShiftLeft:
        return jimple.newShlExpr(left, right);
      case ShiftRight:
        if (instruction.getSign().equals(ProtoIlInstructions.IlInstructionMsg.IlSign.Signed)) {
          return jimple.newShrExpr(left, right);
        }
        return jimple.newUshrExpr(left, right);
      default:
        return null;
    }
  }
}
