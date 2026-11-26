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
import soot.UByteType;
import soot.UIntType;
import soot.ULongType;
import soot.UShortType;
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
    ProtoIlInstructions.IlInstructionMsg.IlPrimitiveType targetType = instruction.getTargetType();

    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArgument(), dotnetBody, cilBlock);
    Value argument = cilExpr.jimplifyExpr(jb);

    Type convType = targetTypeToSootType(targetType);
    if (convType == null) {
      // ILSpy did not determine a primitive target, e.g. for a conversion that only sets the stack type.
      convType = stackTypeToSootType(instruction.getResultType());
    }
    if (convType == null) {
      convType = UnknownType.v();
    }
    argument = simplifyComplexExpression(jb, argument);
    if (convType instanceof UnknownType) {
      // Decompilation errors might happen. But even in this case, we do not
      // want to cast to unknown. In this case, let the type assigner figure something out.
      return argument;
    }

    if (argument.getType() == convType) {
      return argument;
    }
    if (instruction.getCheckForOverflow()) {
      return Jimple.v().newCheckedCastExpr(argument, convType);
    } else {
      return Jimple.v().newCastExpr(argument, convType);
    }
  }

  /**
   * Maps the primitive target of a conversion to the Jimple type to cast to, or {@code null} if there is none.
   */
  private static Type targetTypeToSootType(ProtoIlInstructions.IlInstructionMsg.IlPrimitiveType targetType) {
    switch (targetType) {
      case I1: // SByte
        return ByteType.v();
      case U1: // Byte
        return UByteType.v();
      case I2: // Int16
        return ShortType.v();
      case U2: // UInt16
        return UShortType.v();
      case U4: // UInt32
        return UIntType.v();
      case I4: // Int32
      case I: // System.IntPtr
      case U: // System.UIntPtr
      case Ref: // managed reference - always pointer as integer
        return IntType.v();
      case I8: // Int64
        return LongType.v();
      case U8: // UInt64
        return ULongType.v();
      case R4: // Single
        return FloatType.v();
      case R: // 254 - floating point type
      case R8: // Double
        return DoubleType.v();
      default:
        return null;
    }
  }

  /**
   * Maps a stack type to a Jimple type when no primitive target is available. Value types ({@code V}) and references
   * ({@code O}) carry no primitive type, so they stay {@code null} and the type assigner decides.
   */
  private static Type stackTypeToSootType(ProtoIlInstructions.IlInstructionMsg.IlStackType stackType) {
    switch (stackType) {
      case I4_STACK:
      case I_STACK:
        return IntType.v();
      case I8_STACK:
        return LongType.v();
      case F4:
        return FloatType.v();
      case F8:
        return DoubleType.v();
      case Ref_STACK: // managed pointer, treated as integer like the Ref target above
        return IntType.v();
      case V: // value type
      case O: // reference
      default:
        return null;
    }
  }

}
