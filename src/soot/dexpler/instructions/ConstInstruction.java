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
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction;

import soot.dexpler.Debug;
import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.dexpler.typing.UntypedConstant;
import soot.dexpler.typing.UntypedIntOrFloatConstant;
import soot.dexpler.typing.UntypedLongOrDoubleConstant;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;

public class ConstInstruction extends DexlibAbstractInstruction {

    AssignStmt assign = null;
  
    public ConstInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        int dest = ((OneRegisterInstruction) instruction).getRegisterA();

        Constant cst = getConstant(dest, body);
        assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), cst);
        setUnit(assign);
        addTags(assign);
        body.add(assign);

        if (IDalvikTyper.ENABLE_DVKTYPER) {
            Debug.printDbg(IDalvikTyper.DEBUG, "constraint: "+ assign);
            int op = (int)instruction.getOpcode().value;
            if (cst instanceof UntypedConstant) {
                DalvikTyper.v().addConstraint(assign.getLeftOpBox(), assign.getRightOpBox());
            } else {
                DalvikTyper.v().setType(assign.getLeftOpBox(), cst.getType(), false);
            }
        }
    }

    /**
     * Return the literal constant for this instruction.
     *
     * @param register the register number to fill
     * @param body the body containing the instruction
     */
    private Constant getConstant(int dest, DexBody body) {

        long literal = 0;

        if (instruction instanceof WideLiteralInstruction) {
            literal = ((WideLiteralInstruction)instruction).getWideLiteral();
        } else if (instruction instanceof NarrowLiteralInstruction) {
            literal = ((NarrowLiteralInstruction)instruction).getNarrowLiteral();
        } else {
            throw new RuntimeException("literal error: expected narrow or wide literal.");
        }
        

        boolean isFloatingPoint = false; // this is done later in DexBody by calling DexNumtransformer
        Opcode opcode = instruction.getOpcode();
        switch (opcode) {
        case CONST:
        case CONST_4:
        case CONST_16:
            if (IDalvikTyper.ENABLE_DVKTYPER) {
                return UntypedIntOrFloatConstant.v((int)literal);
            } else {
                if (isFloatingPoint)
                    return FloatConstant.v(Float.intBitsToFloat((int) literal));
                return IntConstant.v((int) literal);
            }

        case CONST_HIGH16:
            if (IDalvikTyper.ENABLE_DVKTYPER) {
                //
                //return UntypedIntOrFloatConstant.v((int)literal<<16).toFloatConstant();
                // seems that dexlib correctly puts the 16bits into the topmost bits.
                //
                return UntypedIntOrFloatConstant.v((int)literal);//.toFloatConstant();
            } else {
                return IntConstant.v((int) literal);
            }

        case CONST_WIDE_HIGH16:
            if (IDalvikTyper.ENABLE_DVKTYPER) {
                //return UntypedLongOrDoubleConstant.v((long)literal<<48).toDoubleConstant();
                // seems that dexlib correctly puts the 16bits into the topmost bits.
                //
                return UntypedLongOrDoubleConstant.v((long)literal);//.toDoubleConstant();
            } else {
                if (isFloatingPoint)
                    return DoubleConstant.v(Double.longBitsToDouble(literal));
                else
                    return LongConstant.v(literal);
            }

        case CONST_WIDE:
        case CONST_WIDE_16:
        case CONST_WIDE_32:
            if (IDalvikTyper.ENABLE_DVKTYPER) {
                return UntypedLongOrDoubleConstant.v(literal);
            } else {
                if (isFloatingPoint)
                    return DoubleConstant.v(Double.longBitsToDouble(literal));
                else
                    return LongConstant.v(literal);
            }
        default:
            throw new IllegalArgumentException("Expected a const or a const-wide instruction, got neither.");
        }
    }

    @Override
    boolean overridesRegister(int register) {
        OneRegisterInstruction i = (OneRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }
}
