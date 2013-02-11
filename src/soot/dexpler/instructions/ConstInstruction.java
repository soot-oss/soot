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

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.LiteralInstruction;
import org.jf.dexlib.Code.SingleRegisterInstruction;

import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.internal.JAssignStmt;

public class ConstInstruction extends DexlibAbstractInstruction {

    AssignStmt assign = null;
  
    public ConstInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        int dest = ((SingleRegisterInstruction) instruction).getRegisterA();

        assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), getConstant(dest, body));
        setUnit(assign);
        tagWithLineNumber(assign);
        body.add(assign);
        
		}
		public void getConstraint(IDalvikTyper dalvikTyper) {
				if (IDalvikTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.opcode.value;
          dalvikTyper.captureAssign((JAssignStmt)assign, op);
        }
    }

    /**
     * Return the literal constant for this instruction.
     *
     * @param register the register number to fill
     * @param body the body containing the instruction
     */
    private Constant getConstant(int dest, DexBody body) {
        long literal = ((LiteralInstruction) instruction).getLiteral();
        boolean isFloatingPoint = false; // this is done later in DexBody by calling DexNumtransformer

        switch (instruction.opcode) {
        case CONST:
        case CONST_4:
        case CONST_16:
            if (isFloatingPoint)
                return FloatConstant.v(Float.intBitsToFloat((int) literal));
            return IntConstant.v((int) literal);

        case CONST_HIGH16:
            return IntConstant.v((int) literal << 16);

        case CONST_WIDE_HIGH16:
            return LongConstant.v(literal << 48);

        case CONST_WIDE:
        case CONST_WIDE_16:
        case CONST_WIDE_32:
            if (isFloatingPoint)
                return DoubleConstant.v(Double.longBitsToDouble(literal));
            else
                return LongConstant.v(literal);
        default:
            throw new IllegalArgumentException("Expected a const or a const-wide instruction, got neither.");
        }
    }

    @Override
    boolean overridesRegister(int register) {
        SingleRegisterInstruction i = (SingleRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }
}
