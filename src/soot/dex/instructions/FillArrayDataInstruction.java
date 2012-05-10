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

import java.util.Iterator;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.ArrayDataPseudoInstruction;
import org.jf.dexlib.Code.Format.ArrayDataPseudoInstruction.ArrayElement;
import org.jf.dexlib.Code.Format.Instruction31t;
import org.jf.dexlib.Util.ByteArray;

import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.dex.DexBody;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.NopStmt;
import soot.jimple.NumericConstant;

public class FillArrayDataInstruction extends DexlibAbstractInstruction {

    public FillArrayDataInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction31t))
            throw new IllegalArgumentException("Expected Instruction31t but got: "+instruction.getClass());

        Instruction31t fillArrayInstr = (Instruction31t)instruction;
        int destRegister = fillArrayInstr.getRegisterA();
        int offset = fillArrayInstr.getTargetAddressOffset();
        int targetAddress = codeAddress + offset;

        Instruction referenceTable = body.instructionAtAddress(targetAddress).instruction;

        if(!(referenceTable instanceof ArrayDataPseudoInstruction)) {
            throw new RuntimeException("Address " + targetAddress + "refers to an invalid PseudoInstruction.");
        }

        ArrayDataPseudoInstruction arrayTable = (ArrayDataPseudoInstruction)referenceTable;
        int numElements = arrayTable.getElementCount();

        NopStmt nopStmtBeginning = Jimple.v().newNopStmt();
        body.add(nopStmtBeginning);

        Local arrayReference = body.getRegisterLocal(destRegister);

        Iterator<ArrayElement> elements = arrayTable.getElements();
        for (int i = 0; i < numElements; i++) {
            ArrayRef arrayRef = Jimple.v().newArrayRef(arrayReference, IntConstant.v(i));
            NumericConstant element = getArrayElement(elements.next(),arrayRef.getType());
            AssignStmt assign = Jimple.v().newAssignStmt(arrayRef, element);
            tagWithLineNumber(assign);
            body.add(assign);
        }

        NopStmt nopStmtEnd = Jimple.v().newNopStmt();
        body.add(nopStmtEnd);

        defineBlock(nopStmtBeginning, nopStmtEnd);

    }

    private NumericConstant getArrayElement(ArrayElement element, Type arrayType) {
    	
        NumericConstant value;
        ByteArray byteArr = new ByteArray(element.buffer);
        if(arrayType instanceof ByteType) {
        	value = IntConstant.v(byteArr.getByte(element.bufferIndex));
        }
        else if(arrayType instanceof CharType || arrayType instanceof ShortType) {
        	value = IntConstant.v(byteArr.getShort(element.bufferIndex));
        }
        else if(arrayType instanceof DoubleType) {
        	value = DoubleConstant.v(byteArr.getLong(element.bufferIndex));
        }
        else if(arrayType instanceof FloatType) {
        	value = FloatConstant.v(byteArr.getInt(element.bufferIndex));
        }
        else if(arrayType instanceof IntType) {
        	value = IntConstant.v(byteArr.getInt(element.bufferIndex));
        }
        else if(arrayType instanceof LongType) {
        	value = LongConstant.v(byteArr.getLong(element.bufferIndex));
        }
        else {
        	throw new RuntimeException("Invalid Array Type occured in FillArrayDataInstruction");
        }
        return value;

    }
}
