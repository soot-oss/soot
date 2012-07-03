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
import soot.dex.tags.DoubleOpTag;
import soot.dex.tags.FloatOpTag;
import soot.dex.tags.IntOpTag;
import soot.dex.tags.LongOpTag;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Jimple;

public class CastInstruction extends TaggedInstruction {

    public CastInstruction (Instruction instruction, int codeAddress) {
        super(instruction, codeAddress);
    }

    public void jimplify (DexBody body) {
        TwoRegisterInstruction i = (TwoRegisterInstruction)instruction;
        int dest = i.getRegisterA();
        int source = i.getRegisterB();
        Type targetType = getTargetType();
        CastExpr cast = Jimple.v().newCastExpr(body.getRegisterLocal(source), targetType);
        AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), cast);
        assign.addTag (getTag());
        defineBlock(assign);
        tagWithLineNumber(assign);
        body.add(assign);
    }

    /**
     * Return the appropriate target type for the covered opcodes.
     * 
     * Note: the tag represents the original type before the cast. 
     * The cast type is not lost in Jimple and can be retrieved by 
     * calling the getCastType() method.
     */
    private Type getTargetType() {
        switch(instruction.opcode) {
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
          setTag (new LongOpTag());
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
          setTag (new DoubleOpTag());
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

}
