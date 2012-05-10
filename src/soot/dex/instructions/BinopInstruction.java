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
import org.jf.dexlib.Code.ThreeRegisterInstruction;
import org.jf.dexlib.Code.Format.Instruction23x;

import soot.Local;
import soot.Value;
import soot.dex.DexBody;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

public class BinopInstruction extends DexlibAbstractInstruction {

    public BinopInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction23x))
            throw new IllegalArgumentException("Expected Instruction23x but got: "+instruction.getClass());

        Instruction23x binOpInstr = (Instruction23x)instruction;
        int dest = binOpInstr.getRegisterA();

        Local source1 = body.getRegisterLocal(binOpInstr.getRegisterB());
        Local source2 = body.getRegisterLocal(binOpInstr.getRegisterC());

        Value expr = getExpression(source1, source2);

        AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), expr);

        defineBlock(assign);
        tagWithLineNumber(assign);
        body.add(assign);
    }

    private Value getExpression(Local source1, Local source2) {
        switch(instruction.opcode) {
        case ADD_LONG:
        case ADD_FLOAT:
        case ADD_DOUBLE:
        case ADD_INT:
            return Jimple.v().newAddExpr(source1, source2);

        case SUB_LONG:
        case SUB_FLOAT:
        case SUB_DOUBLE:
        case SUB_INT:
            return Jimple.v().newSubExpr(source1, source2);

        case MUL_LONG:
        case MUL_FLOAT:
        case MUL_DOUBLE:
        case MUL_INT:
            return Jimple.v().newMulExpr(source1, source2);

        case DIV_LONG:
        case DIV_FLOAT:
        case DIV_DOUBLE:
        case DIV_INT:
            return Jimple.v().newDivExpr(source1, source2);

        case REM_LONG:
        case REM_FLOAT:
        case REM_DOUBLE:
        case REM_INT:
            return Jimple.v().newRemExpr(source1, source2);

        case AND_LONG:
        case AND_INT:
            return Jimple.v().newAndExpr(source1, source2);

        case OR_LONG:
        case OR_INT:
            return Jimple.v().newOrExpr(source1, source2);

        case XOR_LONG:
        case XOR_INT:
            return Jimple.v().newXorExpr(source1, source2);

        case SHL_LONG:
        case SHL_INT:
            return Jimple.v().newShlExpr(source1, source2);

        case SHR_LONG:
        case SHR_INT:
            return Jimple.v().newShrExpr(source1, source2);

        case USHR_LONG:
        case USHR_INT:
            return Jimple.v().newUshrExpr(source1, source2);

        default :
            throw new RuntimeException("Invalid Opcode: " + instruction.opcode);
        }
    }

    @Override
    boolean overridesRegister(int register) {
        ThreeRegisterInstruction i = (ThreeRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }

    @Override
    boolean isUsedAsFloatingPoint(DexBody body, int register) {
        ThreeRegisterInstruction i = (ThreeRegisterInstruction) instruction;
        int b = i.getRegisterB();
        int c = i.getRegisterC();
        switch(instruction.opcode) {
        case ADD_FLOAT:
        case ADD_DOUBLE:
        case SUB_FLOAT:
        case SUB_DOUBLE:
        case MUL_FLOAT:
        case MUL_DOUBLE:
        case DIV_FLOAT:
        case DIV_DOUBLE:
        case REM_FLOAT:
        case REM_DOUBLE:
            return b == register || c == register;
        default:
            return false;
        }
    }
}
