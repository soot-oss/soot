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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dotnet.instructions.CilBlockContainer.BlockContainerKind;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg.IlOpCode;

/**
 * Factory for creating IL Instruction objects
 */
public class CilInstructionFactory {
  private static final Logger logger = LoggerFactory.getLogger(CilInstructionFactory.class);

  public static CilInstruction fromInstructionMsg(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    if (instruction == null) {
      throw new RuntimeException("Cannot instantiate null instruction!");
    }

    switch (instruction.getOpCode()) {
      case CALL:
        return new CilCallInstruction(instruction, dotnetBody, cilBlock) {
          @Override
          protected boolean isNonVirtualCall() {
            return true;
          }
        };
      case CALLVIRT:
        // e.g. System.Object..ctor call
        return new CilCallInstruction(instruction, dotnetBody, cilBlock);
      case LEAVE:
        // return
        return new CilLeaveInstruction(instruction, dotnetBody, cilBlock);
      case STLOC:
        return new CilStLocInstruction(instruction, dotnetBody, cilBlock);
      case STOBJ:
        return new CilStObjInstruction(instruction, dotnetBody, cilBlock);
      case NOP:
        return new CilNopInstruction(instruction, dotnetBody, cilBlock);
      case BRANCH:
        return new CilBranchInstruction(instruction, dotnetBody, cilBlock);
      case IF_INSTRUCTION:
        return new CilIfInstruction(instruction, dotnetBody, cilBlock);
      case TRY_CATCH:
        return new CilTryCatchInstruction(instruction, dotnetBody, cilBlock);
      case TRY_FINALLY:
        return new CilTryFinallyInstruction(instruction, dotnetBody, cilBlock);
      case TRY_FAULT:
        return new CilTryFaultInstruction(instruction, dotnetBody, cilBlock);
      case RETHROW:
        return new CilRethrowInstruction(instruction, dotnetBody, cilBlock);
      case THROW:
        return new CilThrowInstruction(instruction, dotnetBody, cilBlock);
      case DEBUG_BREAK:
        return new CilDebugBreakInstruction(instruction, dotnetBody, cilBlock);
      case SWITCH:
        return new CilSwitchInstruction(instruction, dotnetBody, cilBlock);
      case CK_FINITE:
        return new CilCkFiniteInstruction(instruction, dotnetBody, cilBlock);
      case LDLOCA:
      case LDLOC:
        return new CilLdLocInstruction(instruction, dotnetBody, cilBlock);
      case LDC_DECIMAL:
        return new CilLdcDecimalInstruction(instruction, dotnetBody, cilBlock);
      case LDC_I4:
        return new CilLdcI4Instruction(instruction, dotnetBody, cilBlock);
      case LDC_I8:
        return new CilLdcI8Instruction(instruction, dotnetBody, cilBlock);
      case LDC_R4:
        return new CilLdcR4Instruction(instruction, dotnetBody, cilBlock);
      case LDC_R8:
        return new CilLdcR8Instruction(instruction, dotnetBody, cilBlock);
      case LDSTR:
        return new CilLdStrInstruction(instruction, dotnetBody, cilBlock);
      case LDSFLDA:
        return new CilLdsFldaInstruction(instruction, dotnetBody, cilBlock);
      case LDFLDA:
        return new CilLdFldaInstruction(instruction, dotnetBody, cilBlock);
      case LDOBJ:
        if (instruction.getTarget().getOpCode() == IlOpCode.LDELEMA) {
          //we want to load the *value* of an array reference
          //ilspy resolves the ldelem opcode to storeobj(ldelema)
          return new CilLdElemInstruction(instruction.getTarget(), dotnetBody, cilBlock);
        }
        return fromInstructionMsg(instruction.getTarget(), dotnetBody, cilBlock);
      // return new CilLdObjInstruction(instruction, dotnetBody);
      case NEWOBJ:
        return new CilNewObjInstruction(instruction, dotnetBody, cilBlock);
      case BINARY_NUMERIC_INSTRUCTION:
        return new CilBinaryNumericInstruction(instruction, dotnetBody, cilBlock);
      case COMP:
        return new CilCompInstruction(instruction, dotnetBody, cilBlock);
      case LDNULL:
        return new CilLdNullInstruction(instruction, dotnetBody, cilBlock);
      case LDLEN:
        return new CilLdLenInstruction(instruction, dotnetBody, cilBlock);
      case CONV:
        return new CilConvInstruction(instruction, dotnetBody, cilBlock);
      case NEWARR:
        return new CilNewArrInstruction(instruction, dotnetBody, cilBlock);
      case LDELEMA:
        return new CilLdElemInstruction(instruction, dotnetBody, cilBlock);
      case ISINST:
        return new CilIsInstInstruction(instruction, dotnetBody, cilBlock);
      case CASTCLASS:
      case BOX:
      case UNBOX:
      case UNBOXANY:
        return new CilCastClassUnBoxInstruction(instruction, dotnetBody, cilBlock);
      case NOT:
        return new CilNotInstruction(instruction, dotnetBody, cilBlock);
      case DEFAULT_VALUE:
        return new CilDefaultValueInstruction(instruction, dotnetBody, cilBlock);
      case LD_MEMBER_TOKEN:
        return new CilLdMemberTokenInstruction(instruction, dotnetBody, cilBlock);
      case LD_TYPE_TOKEN:
        return new CilLdTypeTokenInstruction(instruction, dotnetBody, cilBlock);
      case LOC_ALLOC:
        return new CilLocAllocInstruction(instruction, dotnetBody, cilBlock);
      case LD_FTN:
        return new CilLdFtnInstruction(instruction, dotnetBody, cilBlock);
      case LD_VIRT_FTN:
        return new CilLdVirtualFtnInstruction(instruction, dotnetBody, cilBlock);
      case MK_REF_ANY:
      case REF_ANY_VAL:
        return new CilRefAnyInstruction(instruction, dotnetBody, cilBlock);
      case REF_ANY_TYPE:
        return new CilRefTypeInstruction(instruction, dotnetBody, cilBlock);
      case SIZE_OF:
        return new CilSizeOfInstruction(instruction, dotnetBody, cilBlock);
      case NONE_OP:
        throw new IllegalArgumentException("Check the native wrapper; some instruction is not handled in "
            + dotnetBody.getDotnetMethodSig().getSootMethodSignature());
      case MATCH_INSTRUCTION:
        return new CilMatchInstruction(instruction, dotnetBody, cilBlock);
      case BLOCK:
        return new CILBlockContainerInstr(instruction.getTryBlock(), dotnetBody, BlockContainerKind.INSTR_BLOCK);

      default:
        throw new IllegalArgumentException("Opcode " + instruction.getOpCode().name() + " is not implemented!");
    }
  }
}
