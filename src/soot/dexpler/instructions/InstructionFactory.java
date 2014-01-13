/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dexpler.instructions;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.instruction.Instruction;

/**
 * Factory that returns an appropriate Instruction instances for given dexlib
 * instructions and opcodes.
 *
 */
public class InstructionFactory {

    /**
     * Resolve an Instruction from a dexlib instruction.
     *
     * @param instruction the corresponding dexlib instruction
     * @param codeAddress the byte code address of the instruction
     */
    public static DexlibAbstractInstruction fromInstruction(Instruction instruction, int codeAddress) {
        return fromOpcode(instruction.getOpcode(), instruction, codeAddress);
    }

    /**
     * Resolve an Instruction from an dex opcode.
     *
     * @param op the opcode of the instruction
     * @param instruction the corresponding dexlib instruction
     * @param codeAddress the byte code address of the instruction
     */
    public static DexlibAbstractInstruction fromOpcode(Opcode op, Instruction instruction, int codeAddress) {
        switch (op) {

        case SPARSE_SWITCH_PAYLOAD:
        case PACKED_SWITCH_PAYLOAD:
        case ARRAY_PAYLOAD:
        case NOP:               // also includes
                                // PackedSwitchDataPseudoInstruction,
                                // SparseSwitchDataPseudoInstruction and
                                // ArrayDataPseudoInstruction
            return new NopInstruction(instruction, codeAddress);

        case MOVE:
        case MOVE_FROM16:
        case MOVE_16:
        case MOVE_OBJECT:
        case MOVE_OBJECT_FROM16:
        case MOVE_OBJECT_16:
        case MOVE_WIDE:
        case MOVE_WIDE_FROM16:
        case MOVE_WIDE_16:
            return new MoveInstruction(instruction, codeAddress);

        case MOVE_RESULT:
        case MOVE_RESULT_OBJECT:
        case MOVE_RESULT_WIDE:
            return new MoveResultInstruction(instruction, codeAddress);


        case MOVE_EXCEPTION:
            return new MoveExceptionInstruction(instruction, codeAddress);


        case RETURN_VOID:
            return new ReturnVoidInstruction(instruction, codeAddress);
        case RETURN:
        case RETURN_OBJECT:
        case RETURN_WIDE:
            return new ReturnInstruction(instruction, codeAddress);

        case CONST:
        case CONST_4:
        case CONST_16:
        case CONST_HIGH16:
        case CONST_WIDE:
        case CONST_WIDE_16:
        case CONST_WIDE_32:
        case CONST_WIDE_HIGH16:
            return new ConstInstruction(instruction, codeAddress);

        case CONST_STRING:
        case CONST_STRING_JUMBO:
            return new ConstStringInstruction(instruction, codeAddress);

        case CONST_CLASS:
            return new ConstClassInstruction(instruction, codeAddress);

        case MONITOR_ENTER:
            return new MonitorEnterInstruction(instruction, codeAddress);

        case MONITOR_EXIT:
            return new MonitorExitInstruction(instruction, codeAddress);

        case CHECK_CAST:
            return new CheckCastInstruction(instruction, codeAddress);

        case INSTANCE_OF:
            return new InstanceOfInstruction(instruction, codeAddress);

        case ARRAY_LENGTH:
            return new ArrayLengthInstruction(instruction, codeAddress);

        case NEW_INSTANCE:
            return new NewInstanceInstruction(instruction, codeAddress);

        case NEW_ARRAY:
            return new NewArrayInstruction(instruction, codeAddress);

        case FILLED_NEW_ARRAY:
            return new FilledNewArrayInstruction(instruction, codeAddress);

        case FILLED_NEW_ARRAY_RANGE:
            return new FilledNewArrayRangeInstruction(instruction, codeAddress);

        case FILL_ARRAY_DATA:
            return new FillArrayDataInstruction(instruction, codeAddress);

        case THROW:
            return new ThrowInstruction(instruction, codeAddress);

        case GOTO:
        case GOTO_16:
        case GOTO_32:
            return new GotoInstruction(instruction, codeAddress);

        case PACKED_SWITCH:
        //case PACKED_SWITCH_PAYLOAD:
            return new PackedSwitchInstruction(instruction, codeAddress);
        case SPARSE_SWITCH:
        //case SPARSE_SWITCH_PAYLOAD:
            return new SparseSwitchInstruction(instruction, codeAddress);

        case CMPL_FLOAT:
        case CMPG_FLOAT:
        case CMPL_DOUBLE:
        case CMPG_DOUBLE:
        case CMP_LONG:
            return new CmpInstruction(instruction, codeAddress);

        case IF_EQ:
        case IF_NE:
        case IF_LT:
        case IF_GE:
        case IF_GT:
        case IF_LE:
            return new IfTestInstruction(instruction, codeAddress);

        case IF_EQZ:
        case IF_NEZ:
        case IF_LTZ:
        case IF_GEZ:
        case IF_GTZ:
        case IF_LEZ:
            return new IfTestzInstruction(instruction, codeAddress);

        case AGET:
        case AGET_OBJECT:
        case AGET_BOOLEAN:
        case AGET_BYTE:
        case AGET_CHAR:
        case AGET_SHORT:
        case AGET_WIDE:
            return new AgetInstruction(instruction, codeAddress);

        case APUT:
        case APUT_OBJECT:
        case APUT_BOOLEAN:
        case APUT_BYTE:
        case APUT_CHAR:
        case APUT_SHORT:
        case APUT_WIDE:
            return new AputInstruction(instruction, codeAddress);


        case IGET:
        case IGET_OBJECT:
        case IGET_BOOLEAN:
        case IGET_BYTE:
        case IGET_CHAR:
        case IGET_SHORT:
        case IGET_WIDE:
            return new IgetInstruction(instruction, codeAddress);
        case IPUT:
        case IPUT_OBJECT:
        case IPUT_BOOLEAN:
        case IPUT_BYTE:
        case IPUT_CHAR:
        case IPUT_SHORT:
        case IPUT_WIDE:
            return new IputInstruction(instruction, codeAddress);

        case SGET:
        case SGET_OBJECT:
        case SGET_BOOLEAN:
        case SGET_BYTE:
        case SGET_CHAR:
        case SGET_SHORT:
        case SGET_WIDE:
            return new SgetInstruction(instruction, codeAddress);
        case SPUT:
        case SPUT_OBJECT:
        case SPUT_BOOLEAN:
        case SPUT_BYTE:
        case SPUT_CHAR:
        case SPUT_SHORT:
        case SPUT_WIDE:
            return new SputInstruction(instruction, codeAddress);

        case INVOKE_VIRTUAL:
        case INVOKE_VIRTUAL_RANGE:
            return new InvokeVirtualInstruction(instruction, codeAddress);

        case INVOKE_INTERFACE:
        case INVOKE_INTERFACE_RANGE:
            return new InvokeInterfaceInstruction(instruction, codeAddress);

        case INVOKE_DIRECT:
        case INVOKE_DIRECT_RANGE:
        case INVOKE_SUPER:
        case INVOKE_SUPER_RANGE:
            return new InvokeSpecialInstruction(instruction, codeAddress);

        case INVOKE_STATIC:
        case INVOKE_STATIC_RANGE:
            return new InvokeStaticInstruction(instruction, codeAddress);

        case NEG_INT:
        case NOT_INT:
        case NEG_FLOAT:
        case NEG_LONG:
        case NOT_LONG:
        case NEG_DOUBLE:
            return new UnopInstruction(instruction, codeAddress);

        case INT_TO_LONG:
        case INT_TO_DOUBLE:
        case FLOAT_TO_LONG:
        case FLOAT_TO_DOUBLE:
        case LONG_TO_INT:
        case LONG_TO_FLOAT:
        case DOUBLE_TO_INT:
        case DOUBLE_TO_FLOAT:
        case LONG_TO_DOUBLE:
        case DOUBLE_TO_LONG:
        case INT_TO_FLOAT:
        case FLOAT_TO_INT:
        case INT_TO_BYTE:
        case INT_TO_CHAR:
        case INT_TO_SHORT:
            return new CastInstruction(instruction, codeAddress);

        case ADD_INT:
        case SUB_INT:
        case MUL_INT:
        case DIV_INT:
        case REM_INT:
        case AND_INT:
        case OR_INT:
        case XOR_INT:
        case SHL_INT:
        case SHR_INT:
        case USHR_INT:
        case ADD_FLOAT:
        case SUB_FLOAT:
        case MUL_FLOAT:
        case DIV_FLOAT:
        case REM_FLOAT:
        case ADD_LONG:
        case SUB_LONG:
        case MUL_LONG:
        case DIV_LONG:
        case REM_LONG:
        case AND_LONG:
        case OR_LONG:
        case XOR_LONG:
        case SHL_LONG:
        case SHR_LONG:
        case USHR_LONG:
        case ADD_DOUBLE:
        case SUB_DOUBLE:
        case MUL_DOUBLE:
        case DIV_DOUBLE:
        case REM_DOUBLE:
            return new BinopInstruction(instruction, codeAddress);

        case ADD_INT_2ADDR:
        case SUB_INT_2ADDR:
        case MUL_INT_2ADDR:
        case DIV_INT_2ADDR:
        case REM_INT_2ADDR:
        case AND_INT_2ADDR:
        case OR_INT_2ADDR:
        case XOR_INT_2ADDR:
        case SHL_INT_2ADDR:
        case SHR_INT_2ADDR:
        case USHR_INT_2ADDR:
        case ADD_FLOAT_2ADDR:
        case SUB_FLOAT_2ADDR:
        case MUL_FLOAT_2ADDR:
        case DIV_FLOAT_2ADDR:
        case REM_FLOAT_2ADDR:
        case ADD_LONG_2ADDR:
        case SUB_LONG_2ADDR:
        case MUL_LONG_2ADDR:
        case DIV_LONG_2ADDR:
        case REM_LONG_2ADDR:
        case AND_LONG_2ADDR:
        case OR_LONG_2ADDR:
        case XOR_LONG_2ADDR:
        case SHL_LONG_2ADDR:
        case SHR_LONG_2ADDR:
        case USHR_LONG_2ADDR:
        case ADD_DOUBLE_2ADDR:
        case SUB_DOUBLE_2ADDR:
        case MUL_DOUBLE_2ADDR:
        case DIV_DOUBLE_2ADDR:
        case REM_DOUBLE_2ADDR:
            return new Binop2addrInstruction(instruction, codeAddress);

        case ADD_INT_LIT16:
        case RSUB_INT:
        case MUL_INT_LIT16:
        case DIV_INT_LIT16:
        case REM_INT_LIT16:
        case AND_INT_LIT16:
        case OR_INT_LIT16:
        case XOR_INT_LIT16:
        case ADD_INT_LIT8:
        case RSUB_INT_LIT8:
        case MUL_INT_LIT8:
        case DIV_INT_LIT8:
        case REM_INT_LIT8:
        case AND_INT_LIT8:
        case OR_INT_LIT8:
        case XOR_INT_LIT8:
        case SHL_INT_LIT8:
        case SHR_INT_LIT8:
        case USHR_INT_LIT8:
            return new BinopLitInstruction(instruction, codeAddress);


        default:
            throw new IllegalArgumentException("Opcode: "+ op +" @ 0x"+ Integer.toHexString(codeAddress));
        }
    }
}
