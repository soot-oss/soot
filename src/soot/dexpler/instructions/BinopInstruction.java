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
import org.jf.dexlib2.iface.instruction.ThreeRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

import soot.Local;
import soot.Value;
import soot.dexpler.DexBody;
import soot.dexpler.tags.DoubleOpTag;
import soot.dexpler.tags.FloatOpTag;
import soot.dexpler.tags.IntOpTag;
import soot.dexpler.tags.LongOpTag;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

public class BinopInstruction extends TaggedInstruction {
  
    public BinopInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    @Override
	public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction23x))
            throw new IllegalArgumentException("Expected Instruction23x but got: "+instruction.getClass());

        Instruction23x binOpInstr = (Instruction23x)instruction;
        int dest = binOpInstr.getRegisterA();

        Local source1 = body.getRegisterLocal(binOpInstr.getRegisterB());
        Local source2 = body.getRegisterLocal(binOpInstr.getRegisterC());

        Value expr = getExpression(source1, source2);

        AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), expr);
        assign.addTag(getTag());
        
        setUnit(assign);
        addTags(assign);
        body.add(assign);
        
		/*
        if (IDalvikTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.getOpcode().value;
          BinopExpr bexpr = (BinopExpr)expr;
          JAssignStmt jassign = (JAssignStmt)assign;
          DalvikTyper.v().setType(bexpr.getOp1Box(), op1BinType[op-0x90], true);
          DalvikTyper.v().setType(bexpr.getOp2Box(), op2BinType[op-0x90], true);
          DalvikTyper.v().setType(jassign.leftBox, resBinType[op-0x90], false);
        }
          */
    }

    private Value getExpression(Local source1, Local source2) {
        Opcode opcode = instruction.getOpcode();
        switch(opcode) {
        case ADD_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newAddExpr(source1, source2);
        case ADD_FLOAT:
          setTag (new FloatOpTag());
          return Jimple.v().newAddExpr(source1, source2);
        case ADD_DOUBLE:
          setTag (new DoubleOpTag());
          return Jimple.v().newAddExpr(source1, source2);
        case ADD_INT:
          setTag (new IntOpTag());
            return Jimple.v().newAddExpr(source1, source2);

        case SUB_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newSubExpr(source1, source2);
        case SUB_FLOAT:
          setTag (new FloatOpTag());
          return Jimple.v().newSubExpr(source1, source2);
        case SUB_DOUBLE:
          setTag (new DoubleOpTag());
          return Jimple.v().newSubExpr(source1, source2);
        case SUB_INT:
          setTag (new IntOpTag());
            return Jimple.v().newSubExpr(source1, source2);

        case MUL_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newMulExpr(source1, source2);
        case MUL_FLOAT:
          setTag (new FloatOpTag());
          return Jimple.v().newMulExpr(source1, source2);
        case MUL_DOUBLE:
          setTag (new DoubleOpTag());
          return Jimple.v().newMulExpr(source1, source2);
        case MUL_INT:
          setTag (new IntOpTag());
            return Jimple.v().newMulExpr(source1, source2);

        case DIV_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newDivExpr(source1, source2);
        case DIV_FLOAT:
          setTag (new FloatOpTag());
          return Jimple.v().newDivExpr(source1, source2);
        case DIV_DOUBLE:
          setTag (new DoubleOpTag());
          return Jimple.v().newDivExpr(source1, source2);
        case DIV_INT:
          setTag (new IntOpTag());
            return Jimple.v().newDivExpr(source1, source2);

        case REM_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newRemExpr(source1, source2);
        case REM_FLOAT:
          setTag (new FloatOpTag());
          return Jimple.v().newRemExpr(source1, source2);
        case REM_DOUBLE:
          setTag (new DoubleOpTag());
          return Jimple.v().newRemExpr(source1, source2);
        case REM_INT:
          setTag (new IntOpTag());
            return Jimple.v().newRemExpr(source1, source2);

        case AND_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newAndExpr(source1, source2);
        case AND_INT:
          setTag (new IntOpTag());
            return Jimple.v().newAndExpr(source1, source2);

        case OR_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newOrExpr(source1, source2);
        case OR_INT:
          setTag (new IntOpTag());
            return Jimple.v().newOrExpr(source1, source2);

        case XOR_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newXorExpr(source1, source2);
        case XOR_INT:
          setTag (new IntOpTag());
            return Jimple.v().newXorExpr(source1, source2);

        case SHL_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newShlExpr(source1, source2);
        case SHL_INT:
          setTag (new IntOpTag());
            return Jimple.v().newShlExpr(source1, source2);

        case SHR_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newShrExpr(source1, source2);
        case SHR_INT:
          setTag (new IntOpTag());
            return Jimple.v().newShrExpr(source1, source2);

        case USHR_LONG:
          setTag (new LongOpTag());
          return Jimple.v().newUshrExpr(source1, source2);
        case USHR_INT:
          setTag (new IntOpTag());
            return Jimple.v().newUshrExpr(source1, source2);

        default :
            throw new RuntimeException("Invalid Opcode: " + opcode);
        }
    }

    @Override
    boolean overridesRegister(int register) {
        ThreeRegisterInstruction i = (ThreeRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }

}
