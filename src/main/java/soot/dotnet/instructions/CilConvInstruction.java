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

import soot.Body;
import soot.ByteType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.UnknownType;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;

/**
 * Convert opcode
 */
public class CilConvInstruction extends AbstractCilnstruction {
  public CilConvInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    ProtoIlInstructions.IlInstructionMsg.IlSign inputSign = instruction.getSign();
    ProtoIlInstructions.IlInstructionMsg.IlStackType inputType = instruction.getInputType();
    ProtoIlInstructions.IlInstructionMsg.IlPrimitiveType targetType = instruction.getTargetType();
    ProtoIlInstructions.IlInstructionMsg.IlStackType resultType = instruction.getResultType();

    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArgument(), dotnetBody, cilBlock);
    Value argument = cilExpr.jimplifyExpr(jb);

    Type convType;
    switch (targetType) {
      case I1: // SByte
      case U1: // Byte
        convType = ByteType.v();
        break;
      case I2: // Int16
      case U2: // UInt16
        convType = ShortType.v();
        break;
      case I4: // Int32
      case U4: // UInt32
      case I: // System.IntPtr
      case U: // System.UIntPtr
      case Ref: // managed reference - always pointer as integer
        convType = IntType.v();
        break;
      case I8: // Int64
      case U8: // UInt64
        convType = LongType.v();
        break;
      case R4: // Single
        convType = FloatType.v();
        break;
      case R: // 254 - floating point type
      case R8: // Double
        convType = DoubleType.v();
        break;
      default:
        convType = UnknownType.v();
        break;
    }

    return Jimple.v().newCastExpr(argument, convType);
  }
}
