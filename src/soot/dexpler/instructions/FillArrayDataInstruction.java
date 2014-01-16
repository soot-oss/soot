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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.ArrayPayload;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;
import org.jf.dexlib2.iface.reference.TypeReference;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.dexpler.Debug;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.NumericConstant;
import soot.jimple.Stmt;

public class FillArrayDataInstruction extends PseudoInstruction {

  public FillArrayDataInstruction (Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  public void jimplify (DexBody body) {
    if(!(instruction instanceof Instruction31t))
      throw new IllegalArgumentException("Expected Instruction31t but got: "+instruction.getClass());

    Instruction31t fillArrayInstr = (Instruction31t)instruction;
    int destRegister = fillArrayInstr.getRegisterA();
    int offset = fillArrayInstr.getCodeOffset();
    int targetAddress = codeAddress + offset;

    Instruction referenceTable = body.instructionAtAddress(targetAddress).instruction;

    if(!(referenceTable instanceof ArrayPayload)) {
      throw new RuntimeException("Address " + targetAddress + "refers to an invalid PseudoInstruction.");
    }

    ArrayPayload arrayTable = (ArrayPayload)referenceTable;

    //        NopStmt nopStmtBeginning = Jimple.v().newNopStmt();
    //        body.add(nopStmtBeginning);

    Local arrayReference = body.getRegisterLocal(destRegister);
    List<Number> elements = arrayTable.getArrayElements();
    int numElements = elements.size();

    Stmt firstAssign = null;
    for (int i = 0; i < numElements; i++) {
      ArrayRef arrayRef = Jimple.v().newArrayRef(arrayReference, IntConstant.v(i));
      NumericConstant element = getArrayElement(elements.get(i),body,destRegister);
      if (element == null) //array was not defined -> element type can not be found (obfuscated bytecode?)
        break;
      AssignStmt assign = Jimple.v().newAssignStmt(arrayRef, element);
      addTags(assign);
      body.add(assign);
      if (i == 0) {
        firstAssign = assign;
      }
    }
    if (firstAssign == null) { // if numElements == 0. Is it possible?
        firstAssign = Jimple.v().newNopStmt();
        body.add (firstAssign);
    }

    //        NopStmt nopStmtEnd = Jimple.v().newNopStmt();
    //        body.add(nopStmtEnd);

    //        defineBlock(nopStmtBeginning, nopStmtEnd);
    setUnit (firstAssign);

  }

  private NumericConstant getArrayElement(Number element, DexBody body, int arrayRegister) {

    List<DexlibAbstractInstruction> instructions = body.instructionsBefore(this);
    Set<Integer> usedRegisters = new HashSet<Integer>();
    usedRegisters.add(arrayRegister);

    Type elementType = null;
    Outer:
      for(DexlibAbstractInstruction i : instructions) {
        if (usedRegisters.isEmpty())
          break;

        for (int reg : usedRegisters)
          if (i instanceof NewArrayInstruction) {
            NewArrayInstruction newArrayInstruction = (NewArrayInstruction) i;
            Instruction22c instruction22c = (Instruction22c)newArrayInstruction.instruction;
            if (instruction22c.getRegisterA()==reg) {
              ArrayType arrayType = (ArrayType) DexType.toSoot((TypeReference) instruction22c.getReference());
              elementType = arrayType.getElementType();
              break Outer;
            }
          }

//        // look for obsolete registers
//        for (int reg : usedRegisters) {
//          if (i.overridesRegister(reg)) {
//            usedRegisters.remove(reg);
//            break;      // there can't be more than one obsolete
//          }
//        }

        // look for new registers
        for (int reg : usedRegisters) {
          int newRegister = i.movesToRegister(reg);
          if (newRegister != -1) {
            usedRegisters.add(newRegister);
            usedRegisters.remove(reg);
            break;      // there can't be more than one new
          }
        }
      }

    if(elementType==null) {
      //throw new InternalError("Unable to find array type to type array elements!");
      G.v().out.println("Warning: Unable to find array type to type array elements! Array was not defined! (obfuscated bytecode?)");
      return null;
    }

    NumericConstant value;

    if (elementType instanceof BooleanType) {
      value = IntConstant.v(element.intValue());
      IntConstant ic = (IntConstant)value;
      if (!(ic.value == 0 || ic.value == 1)) {
        throw new RuntimeException("ERROR: Invalid value for boolean: "+ value);
      }
    } else if(elementType instanceof ByteType) {
      value = IntConstant.v(element.byteValue());
    } else if(elementType instanceof CharType || elementType instanceof ShortType) {
      value = IntConstant.v(element.shortValue());
    } else if(elementType instanceof DoubleType) {
      value = DoubleConstant.v(Double.longBitsToDouble(element.longValue()));
    } else if(elementType instanceof FloatType) {
      value = FloatConstant.v(Float.intBitsToFloat(element.intValue()));
    } else if(elementType instanceof IntType) {
      value = IntConstant.v(element.intValue());
    } else if(elementType instanceof LongType) {
      value = LongConstant.v(element.longValue());
    } else {
      throw new RuntimeException("Invalid Array Type occured in FillArrayDataInstruction: "+ elementType);
    }
    Debug.printDbg("array element: ", value);
    return value;

  }

  @Override
  public void computeDataOffsets(DexBody body) {
    Debug.printDbg("compute data offset");
    if(!(instruction instanceof Instruction31t))
      throw new IllegalArgumentException("Expected Instruction31t but got: "+instruction.getClass());

    Instruction31t fillArrayInstr = (Instruction31t)instruction;
    int offset = fillArrayInstr.getCodeOffset();
    int targetAddress = codeAddress + offset;

    Instruction referenceTable = body.instructionAtAddress(targetAddress).instruction;

    if(!(referenceTable instanceof ArrayPayload)) {
      throw new RuntimeException("Address 0x" + Integer.toHexString(targetAddress) + " refers to an invalid PseudoInstruction ("+ referenceTable.getClass() +").");
    }

    ArrayPayload arrayTable = (ArrayPayload)referenceTable;
    int numElements = arrayTable.getArrayElements().size();
    int widthElement = arrayTable.getElementWidth();
    int size = (widthElement * numElements) / 2; // addresses are on 16bits

    // From org.jf.dexlib.Code.Format.ArrayDataPseudoInstruction we learn
    // that there are 6 bytes after the magic number that we have to jump.
    // 6 bytes to jump = address + 3
    //
    //    out.writeByte(0x00); // magic
    //    out.writeByte(0x03); // number
    //    out.writeShort(elementWidth); // 2 bytes
    //    out.writeInt(elementCount); // 4 bytes
    //    out.write(encodedValues);
    //

    setDataFirstByte (targetAddress + 3); // address for 16 bits elements not 8 bits
    setDataLastByte (targetAddress + 3 + size);// - 1);
    setDataSize (size);

// TODO: how to handle this with dexlib2 ?
//    ByteArrayAnnotatedOutput out = new ByteArrayAnnotatedOutput();
//    arrayTable.write(out, targetAddress);
//
//    byte[] outa = out.getArray();
//    byte[] data = new byte[outa.length-6];
//    for (int i=6; i<outa.length; i++) {
//      data[i-6] = outa[i];
//    }
//    setData (data);
  }



}
