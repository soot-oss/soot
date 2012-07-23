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
import org.jf.dexlib.Code.Format.Instruction21t;

import soot.DoubleType;
import soot.IntType;
import soot.dex.DexBody;
import soot.dex.DvkTyper;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.internal.JIfStmt;

public class IfTestzInstruction extends ConditionalJumpInstruction {

    public IfTestzInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    protected IfStmt ifStatement(DexBody body) {
        Instruction21t i = (Instruction21t) instruction;
        BinopExpr condition = getComparisonExpr(body, i.getRegisterA());
        JIfStmt jif = (JIfStmt) Jimple.v().newIfStmt(condition,
                                    targetInstruction.getUnit());
        
        if (DvkTyper.ENABLE_DVKTYPER) {
           int op = instruction.opcode.value;
           switch (op) {
           case 0x38:
           case 0x39:
             body.dvkTyper.setConstraint(condition.getOp1Box(), condition.getOp2Box());
             break;
           case 0x3a:
           case 0x3b:
           case 0x3c:
           case 0x3d:
             body.dvkTyper.setType(condition.getOp1Box(), IntType.v());
             break;
           default:
             throw new RuntimeException("error: unknown op: 0x"+ Integer.toHexString(op));
           }
        }
        return jif;
    }
}
