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

import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.dexpler.Debug;
import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.tags.DoubleOpTag;
import soot.dexpler.tags.FloatOpTag;
import soot.dexpler.tags.IntOpTag;
import soot.dexpler.tags.LongOpTag;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Jimple;

public class CastInstruction extends TaggedInstruction {

    AssignStmt assign = null;
  
    public CastInstruction (Instruction instruction, int codeAddress) {
        super(instruction, codeAddress);
    }

    public void jimplify (DexBody body) {
        TwoRegisterInstruction i = (TwoRegisterInstruction)instruction;
        int dest = i.getRegisterA();
        int source = i.getRegisterB();
        Type targetType = getTargetType();
        CastExpr cast = Jimple.v().newCastExpr(body.getRegisterLocal(source), targetType);
        assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), cast);
        assign.addTag (getTag());
        setUnit(assign);
        addTags(assign);
        body.add(assign);
        
        if (IDalvikTyper.ENABLE_DVKTYPER) {
			Debug.printDbg(IDalvikTyper.DEBUG, "constraint cast: "+ assign +" castexpr type: "+ cast.getType()+" cast type: "+ cast.getCastType());
          int op = (int)instruction.getOpcode().value;
          DalvikTyper.v().setType(assign.getLeftOpBox(), cast.getType(), false);
          //DalvikTyper.v().captureAssign((JAssignStmt)assign, op);
        }
    }

    /**
     * Return the appropriate target type for the covered opcodes.
     * 
     * Note: the tag represents the original type before the cast. 
     * The cast type is not lost in Jimple and can be retrieved by 
     * calling the getCastType() method.
     */
    private Type getTargetType() {
        Opcode opcode = instruction.getOpcode();
        switch(opcode) {
        case INT_TO_BYTE:
            setTag (new IntOpTag());
            return ByteType.v();
        case INT_TO_CHAR:
          setTag (new IntOpTag());
            return CharType.v();
        case INT_TO_SHORT:
          setTag (new IntOpTag());
            return ShortType.v();
            
        case LONG_TO_INT:
          setTag (new LongOpTag());
          return IntType.v();
        case DOUBLE_TO_INT:
          setTag (new DoubleOpTag());
          return IntType.v();
        case FLOAT_TO_INT:
          setTag (new FloatOpTag());
            return IntType.v();
            
        case INT_TO_LONG:
          setTag (new IntOpTag());
          return LongType.v();
        case DOUBLE_TO_LONG:
          setTag (new DoubleOpTag());
          return LongType.v();
        case FLOAT_TO_LONG:
          setTag (new FloatOpTag());
            return LongType.v();
            
        case LONG_TO_FLOAT:
          setTag (new LongOpTag());
          return FloatType.v();
        case DOUBLE_TO_FLOAT:
          setTag (new DoubleOpTag());
          return FloatType.v();
        case INT_TO_FLOAT:
          setTag (new IntOpTag());
            return FloatType.v();
            
        case INT_TO_DOUBLE:
          setTag (new IntOpTag());
          return DoubleType.v();
        case FLOAT_TO_DOUBLE:
          setTag (new FloatOpTag());
          return DoubleType.v();
        case LONG_TO_DOUBLE:
          setTag (new LongOpTag());
            return DoubleType.v();

        default:
            throw new RuntimeException("Invalid Opcode: " + opcode);
        }
    }

    @Override
    boolean overridesRegister(int register) {
        TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }

}
