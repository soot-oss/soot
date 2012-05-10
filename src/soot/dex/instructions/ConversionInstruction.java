/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
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

package soot.dex.instructions;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.TwoRegisterInstruction;

import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.dex.DexBody;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Jimple;

public class ConversionInstruction extends DexlibAbstractInstruction {

    public ConversionInstruction (Instruction instruction, int codeAddress) {
        super(instruction, codeAddress);
    }

    public void jimplify (DexBody body) {
        TwoRegisterInstruction i = (TwoRegisterInstruction)instruction;
        int dest = i.getRegisterA();
        int source = i.getRegisterB();
        Type targetType = getTargetType();
        CastExpr cast = Jimple.v().newCastExpr(body.getRegisterLocal(source), targetType);
        AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), cast);
        defineBlock(assign);
        tagWithLineNumber(assign);
        body.add(assign);
    }

    /**
     * Return the appropriate target type for the covered opcodes.
     */
    private Type getTargetType() {
        switch(instruction.opcode) {
        case INT_TO_BYTE:
            return ByteType.v();
        case INT_TO_CHAR:
            return CharType.v();
        case INT_TO_SHORT:
            return ShortType.v();
        case LONG_TO_INT:
        case DOUBLE_TO_INT:
        case FLOAT_TO_INT:
            return IntType.v();
        case INT_TO_LONG:
        case DOUBLE_TO_LONG:
        case FLOAT_TO_LONG:
            return LongType.v();
        case LONG_TO_FLOAT:
        case DOUBLE_TO_FLOAT:
        case INT_TO_FLOAT:
            return FloatType.v();
        case INT_TO_DOUBLE:
        case FLOAT_TO_DOUBLE:
        case LONG_TO_DOUBLE:
            return DoubleType.v();

        default:
            throw new RuntimeException("Invalid Opcode: " + instruction.opcode);
        }
    }

    @Override
    boolean overridesRegister(int register) {
        TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }

    @Override
    boolean isUsedAsFloatingPoint(DexBody body, int register) {
        int source = ((TwoRegisterInstruction) instruction).getRegisterB();
        switch(instruction.opcode) {
        case DOUBLE_TO_INT:
        case FLOAT_TO_INT:
        case DOUBLE_TO_LONG:
        case FLOAT_TO_LONG:
        case DOUBLE_TO_FLOAT:
        case FLOAT_TO_DOUBLE:
            return source == register;
        default:
            return false;
        }
    }
}
