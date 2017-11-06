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
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;

import soot.Local;
import soot.Value;
import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.tags.DoubleOpTag;
import soot.dexpler.tags.FloatOpTag;
import soot.dexpler.tags.IntOpTag;
import soot.dexpler.tags.LongOpTag;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;

public class UnopInstruction extends TaggedInstruction {

    public UnopInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    @Override
	public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction12x))
            throw new IllegalArgumentException("Expected Instruction12x but got: "+instruction.getClass());

        Instruction12x cmpInstr = (Instruction12x)instruction;
        int dest = cmpInstr.getRegisterA();

        Local source = body.getRegisterLocal(cmpInstr.getRegisterB());
        Value expr = getExpression(source);

        AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), expr);
		assign.addTag(getTag());

        setUnit(assign);
        addTags(assign);
        body.add(assign);
        
		if (IDalvikTyper.ENABLE_DVKTYPER) {
			/*
          int op = (int)instruction.getOpcode().value;
          //DalvikTyper.v().captureAssign((JAssignStmt)assign, op);
          JAssignStmt jass = (JAssignStmt)assign;
          DalvikTyper.v().setType((expr instanceof JCastExpr) ? ((JCastExpr) expr).getOpBox() : ((UnopExpr) expr).getOpBox(), opUnType[op - 0x7b], true);
          DalvikTyper.v().setType(jass.leftBox, resUnType[op - 0x7b], false);
          */
        }
    }

    /**
     * Return the appropriate Jimple Expression according to the OpCode
     */
    private Value getExpression(Local source) {
        Opcode opcode = instruction.getOpcode();
        switch(opcode) {
        case NEG_INT:
			setTag(new IntOpTag());
			return Jimple.v().newNegExpr(source);
        case NEG_LONG:
			setTag(new LongOpTag());
			return Jimple.v().newNegExpr(source);
        case NEG_FLOAT:
			setTag(new FloatOpTag());
			return Jimple.v().newNegExpr(source);
        case NEG_DOUBLE:
			setTag(new DoubleOpTag());
            return Jimple.v().newNegExpr(source);
        case NOT_LONG:
			setTag(new LongOpTag());
            return getNotLongExpr(source);
        case NOT_INT:
			setTag(new IntOpTag());
            return getNotIntExpr(source);
        default:
            throw new RuntimeException("Invalid Opcode: " + opcode);
        }

    }
    /**
     * returns bitwise negation of an integer
     * @param source
     * @return
     */
    private Value getNotIntExpr(Local source) {
        return Jimple.v().newXorExpr(source, IntConstant.v(-1));

    }
    /**
     * returns bitwise negation of a long
     * @param source
     * @return
     */
    private Value getNotLongExpr(Local source) {
        return Jimple.v().newXorExpr(source, LongConstant.v(-1l));

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
        Opcode opcode = instruction.getOpcode();
        switch(opcode) {
        case NEG_FLOAT:
        case NEG_DOUBLE:
            return source == register;
        default:
            return false;
        }
    }
}
