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

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

import soot.IntType;
import soot.Local;
import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.InvalidDalvikBytecodeException;
import soot.dexpler.tags.BooleanOpTag;
import soot.dexpler.tags.ByteOpTag;
import soot.dexpler.tags.CharOpTag;
import soot.dexpler.tags.IntOrFloatOpTag;
import soot.dexpler.tags.LongOrDoubleOpTag;
import soot.dexpler.tags.ObjectOpTag;
import soot.dexpler.tags.ShortOpTag;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

public class AgetInstruction extends DexlibAbstractInstruction {

  public AgetInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  public void jimplify(DexBody body) throws InvalidDalvikBytecodeException {
    if (!(instruction instanceof Instruction23x)) {
      throw new IllegalArgumentException("Expected Instruction23x but got: " + instruction.getClass());
    }

    Instruction23x aGetInstr = (Instruction23x) instruction;
    int dest = aGetInstr.getRegisterA();

    Local arrayBase = body.getRegisterLocal(aGetInstr.getRegisterB());
    Local index = body.getRegisterLocal(aGetInstr.getRegisterC());

    ArrayRef arrayRef = Jimple.v().newArrayRef(arrayBase, index);
    Local l = body.getRegisterLocal(dest);

    AssignStmt assign = Jimple.v().newAssignStmt(l, arrayRef);
    switch (aGetInstr.getOpcode()) {
      case AGET_OBJECT:
        assign.addTag(new ObjectOpTag());
        break;
      case AGET:
        assign.addTag(new IntOrFloatOpTag());
        break;
      case AGET_WIDE:
        assign.addTag(new LongOrDoubleOpTag());
        break;
      case AGET_BYTE:
        assign.addTag(new ByteOpTag());
        break;
      case AGET_CHAR:
        assign.addTag(new CharOpTag());
        break;
      case AGET_SHORT:
        assign.addTag(new ShortOpTag());
        break;
      case AGET_BOOLEAN:
        assign.addTag(new BooleanOpTag());
        break;
    }

    setUnit(assign);
    addTags(assign);
    body.add(assign);

    if (IDalvikTyper.ENABLE_DVKTYPER) {
      DalvikTyper.v().addConstraint(assign.getLeftOpBox(), assign.getRightOpBox());
      DalvikTyper.v().setType(arrayRef.getIndexBox(), IntType.v(), true);
    }
  }

  @Override
  boolean overridesRegister(int register) {
    OneRegisterInstruction i = (OneRegisterInstruction) instruction;
    int dest = i.getRegisterA();
    return register == dest;
  }
}
