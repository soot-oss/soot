package soot.dotnet.instructions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;

/**
 * Factory for creating IL Instruction objects
 */
public class CilInstructionFactory {
    private static final Logger logger = LoggerFactory.getLogger(CilInstructionFactory.class);

    public static CilInstruction fromInstructionMsg(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        if (instruction == null)
            throw new RuntimeException("Cannot instantiate null instruction!");


        switch (instruction.getOpCode()) {
            case CALL:
            case CALLVIRT:
                // e.g. System.Object..ctor call
                return new CilCallVirtInstruction(instruction, dotnetBody, cilBlock);
            case LEAVE:
                // return (void)
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
                return new CilLdElemaInstruction(instruction, dotnetBody, cilBlock);
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
            case LD_VIRT_FTN:
                return new CilLdFtnInstruction(instruction, dotnetBody, cilBlock);
            case MK_REF_ANY:
            case REF_ANY_VAL:
                return new CilRefAnyInstruction(instruction, dotnetBody, cilBlock);
            case REF_ANY_TYPE:
                return new CilRefTypeInstruction(instruction, dotnetBody, cilBlock);
            case SIZE_OF:
                return new CilSizeOfInstruction(instruction, dotnetBody, cilBlock);
            default:
                throw new IllegalArgumentException("Opcode " + instruction.getOpCode().name() + " is not implemented!");
        }
    }
}
