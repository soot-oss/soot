package soot.dexpler.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.List;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.ArrayPayload;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.dexpler.DexBody;
import soot.dexpler.typing.UntypedIntOrFloatConstant;
import soot.dexpler.typing.UntypedLongOrDoubleConstant;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

/**
 * Converts <code>fill-array-data</code> instructions and associated data blocks into a series of assignment instructions
 * (one for each array index the data block contains a value).
 * 
 * As the data block contains untyped data, only the number of bytes per element is known. Recovering the array type at the
 * stage this class is used on would require a detailed analysis on the dex code. Therefore we save the data elements as
 * {@link UntypedConstant} and later use {@link DexFillArrayDataTransformer} to convert the values to their final type.
 */
public class FillArrayDataInstruction extends PseudoInstruction {
  private static final Logger logger = LoggerFactory.getLogger(FillArrayDataInstruction.class);

  public FillArrayDataInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  public void jimplify(DexBody body) {
    if (!(instruction instanceof Instruction31t)) {
      throw new IllegalArgumentException("Expected Instruction31t but got: " + instruction.getClass());
    }

    Instruction31t fillArrayInstr = (Instruction31t) instruction;
    int destRegister = fillArrayInstr.getRegisterA();
    int offset = fillArrayInstr.getCodeOffset();
    int targetAddress = codeAddress + offset;

    Instruction referenceTable = body.instructionAtAddress(targetAddress).instruction;

    if (!(referenceTable instanceof ArrayPayload)) {
      throw new RuntimeException("Address " + targetAddress + "refers to an invalid PseudoInstruction.");
    }

    ArrayPayload arrayTable = (ArrayPayload) referenceTable;

    // NopStmt nopStmtBeginning = Jimple.v().newNopStmt();
    // body.add(nopStmtBeginning);

    Local arrayReference = body.getRegisterLocal(destRegister);
    List<Number> elements = arrayTable.getArrayElements();
    int numElements = elements.size();

    int elementsWidth = arrayTable.getElementWidth();
    Stmt firstAssign = null;
    for (int i = 0; i < numElements; i++) {
      ArrayRef arrayRef = Jimple.v().newArrayRef(arrayReference, IntConstant.v(i));
      Constant element = getArrayElement(elements.get(i), elementsWidth);
      AssignStmt assign = Jimple.v().newAssignStmt(arrayRef, element);
      addTags(assign);
      body.add(assign);
      if (i == 0) {
        firstAssign = assign;
      }
    }
    if (firstAssign == null) { // if numElements == 0. Is it possible?
      logger.warn("No assign statements created for array at address 0x{} - empty array data section?",
          Integer.toHexString(targetAddress));
      firstAssign = Jimple.v().newNopStmt();
      body.add(firstAssign);
    }

    // NopStmt nopStmtEnd = Jimple.v().newNopStmt();
    // body.add(nopStmtEnd);

    // defineBlock(nopStmtBeginning, nopStmtEnd);
    setUnit(firstAssign);

  }

  private Constant getArrayElement(Number element, int elementsWidth) {
    if (elementsWidth == 2) {
      // For size = 2 the only possible array type is short[]
      return IntConstant.v(element.shortValue());
    }

    if (elementsWidth <= 4) {
      // can be array of int, char, boolean, float
      return UntypedIntOrFloatConstant.v(element.intValue());
    }

    // can be array of long or double
    return UntypedLongOrDoubleConstant.v(element.longValue());
  }

  @Override
  public void computeDataOffsets(DexBody body) {
    if (!(instruction instanceof Instruction31t)) {
      throw new IllegalArgumentException("Expected Instruction31t but got: " + instruction.getClass());
    }

    Instruction31t fillArrayInstr = (Instruction31t) instruction;
    int offset = fillArrayInstr.getCodeOffset();
    int targetAddress = codeAddress + offset;

    Instruction referenceTable = body.instructionAtAddress(targetAddress).instruction;

    if (!(referenceTable instanceof ArrayPayload)) {
      throw new RuntimeException("Address 0x" + Integer.toHexString(targetAddress)
          + " refers to an invalid PseudoInstruction (" + referenceTable.getClass() + ").");
    }

    ArrayPayload arrayTable = (ArrayPayload) referenceTable;
    int numElements = arrayTable.getArrayElements().size();
    int widthElement = arrayTable.getElementWidth();
    int size = (widthElement * numElements) / 2; // addresses are on 16bits

    // From org.jf.dexlib.Code.Format.ArrayDataPseudoInstruction we learn
    // that there are 6 bytes after the magic number that we have to jump.
    // 6 bytes to jump = address + 3
    //
    // out.writeByte(0x00); // magic
    // out.writeByte(0x03); // number
    // out.writeShort(elementWidth); // 2 bytes
    // out.writeInt(elementCount); // 4 bytes
    // out.write(encodedValues);
    //

    setDataFirstByte(targetAddress + 3); // address for 16 bits elements not 8 bits
    setDataLastByte(targetAddress + 3 + size);// - 1);
    setDataSize(size);

    // TODO: how to handle this with dexlib2 ?
    // ByteArrayAnnotatedOutput out = new ByteArrayAnnotatedOutput();
    // arrayTable.write(out, targetAddress);
    //
    // byte[] outa = out.getArray();
    // byte[] data = new byte[outa.length-6];
    // for (int i=6; i<outa.length; i++) {
    // data[i-6] = outa[i];
    // }
    // setData (data);
  }

}
