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
import org.jf.dexlib.Code.Format.Instruction12x;

import soot.Local;
import soot.Value;
import soot.dex.DexBody;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

public class Binop2addrInstruction extends DexlibAbstractInstruction {

    public Binop2addrInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction12x))
            throw new IllegalArgumentException("Expected Instruction12x but got: "+instruction.getClass());

        Instruction12x binOp2AddrInstr = (Instruction12x)instruction;
        int dest = binOp2AddrInstr.getRegisterA();

        Local source1 = body.getRegisterLocal(binOp2AddrInstr.getRegisterA());
        Local source2 = body.getRegisterLocal(binOp2AddrInstr.getRegisterB());

        Value expr = getExpression(source1, source2);

        AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), expr);

        defineBlock(assign);
        tagWithLineNumber(assign);
        body.add(assign);
    }

    private Value getExpression(Local source1, Local source2) {
        switch(instruction.opcode) {
        case ADD_LONG_2ADDR:
        case ADD_FLOAT_2ADDR:
        case ADD_DOUBLE_2ADDR:
        case ADD_INT_2ADDR:
            return Jimple.v().newAddExpr(source1, source2);

        case SUB_LONG_2ADDR:
        case SUB_FLOAT_2ADDR:
        case SUB_DOUBLE_2ADDR:
        case SUB_INT_2ADDR:
            return Jimple.v().newSubExpr(source1, source2);

        case MUL_LONG_2ADDR:
        case MUL_FLOAT_2ADDR:
        case MUL_DOUBLE_2ADDR:
        case MUL_INT_2ADDR:
            return Jimple.v().newMulExpr(source1, source2);

        case DIV_LONG_2ADDR:
        case DIV_FLOAT_2ADDR:
        case DIV_DOUBLE_2ADDR:
        case DIV_INT_2ADDR:
            return Jimple.v().newDivExpr(source1, source2);

        case REM_LONG_2ADDR:
        case REM_FLOAT_2ADDR:
        case REM_DOUBLE_2ADDR:
        case REM_INT_2ADDR:
            return Jimple.v().newRemExpr(source1, source2);

        case AND_LONG_2ADDR:
        case AND_INT_2ADDR:
            return Jimple.v().newAndExpr(source1, source2);

        case OR_LONG_2ADDR:
        case OR_INT_2ADDR:
            return Jimple.v().newOrExpr(source1, source2);

        case XOR_LONG_2ADDR:
        case XOR_INT_2ADDR:
            return Jimple.v().newXorExpr(source1, source2);

        case SHL_LONG_2ADDR:
        case SHL_INT_2ADDR:
            return Jimple.v().newShlExpr(source1, source2);

        case SHR_LONG_2ADDR:
        case SHR_INT_2ADDR:
            return Jimple.v().newShrExpr(source1, source2);

        case USHR_LONG_2ADDR:
        case USHR_INT_2ADDR:
            return Jimple.v().newUshrExpr(source1, source2);

        default :
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
        TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;
        int a = i.getRegisterA();
        int b = i.getRegisterB();

        switch(instruction.opcode) {
        case ADD_FLOAT_2ADDR:
        case ADD_DOUBLE_2ADDR:
        case SUB_FLOAT_2ADDR:
        case SUB_DOUBLE_2ADDR:
        case DIV_FLOAT_2ADDR:
        case DIV_DOUBLE_2ADDR:
        case MUL_FLOAT_2ADDR:
        case MUL_DOUBLE_2ADDR:
        case REM_FLOAT_2ADDR:
        case REM_DOUBLE_2ADDR:
            return register == a || register == b;
        default:
            return false;
        }
    }
}
