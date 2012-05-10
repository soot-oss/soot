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
import soot.dex.DexBody;
import soot.jimple.AssignStmt;
import soot.jimple.Expr;
import soot.jimple.Jimple;

public class CmpInstruction extends DexlibAbstractInstruction {

    public CmpInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction23x))
            throw new IllegalArgumentException("Expected Instruction23x but got: "+instruction.getClass());

        Instruction23x cmpInstr = (Instruction23x)instruction;
        int dest = cmpInstr.getRegisterA();

        Local first = body.getRegisterLocal(cmpInstr.getRegisterB());
        Local second = body.getRegisterLocal(cmpInstr.getRegisterC());

        Expr cmpExpr;
        switch (instruction.opcode) {
        case CMPL_DOUBLE:
        case CMPL_FLOAT:
            cmpExpr = Jimple.v().newCmplExpr(first, second);
            break;
        case CMPG_DOUBLE:
        case CMPG_FLOAT:
            cmpExpr = Jimple.v().newCmpgExpr(first, second);
            break;
        default:
            cmpExpr = Jimple.v().newCmpExpr(first, second);
        }

        AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), cmpExpr);

        defineBlock(assign);
        tagWithLineNumber(assign);
        body.add(assign);
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
        if (i.getRegisterB() == register || i.getRegisterC() == register)
            switch (instruction.opcode) {
            case CMPL_FLOAT:
            case CMPG_FLOAT:
            case CMPL_DOUBLE:
            case CMPG_DOUBLE:
                return true;
            default:
                return false;
            }
        return false;
    }
}
