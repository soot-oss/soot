package soot.toDex.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.BitSet;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.iface.reference.Reference;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;

/**
 * The "21c" instruction format: It needs two 16-bit code units, has one register and is used for class/string/type items
 * (hence the "c" for "constant pool").<br>
 * <br>
 * It is used e.g. by the opcodes "check-cast", "new-instance" and "const-string".
 */
public class Insn21c extends AbstractInsn implements OneRegInsn {

  private Reference referencedItem;

  public Insn21c(Opcode opc, Register regA, Reference referencedItem) {
    super(opc);
    regs.add(regA);
    this.referencedItem = referencedItem;
  }

  public Register getRegA() {
    return regs.get(REG_A_IDX);
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    return new BuilderInstruction21c(opc, (short) getRegA().getNumber(), referencedItem);
  }

  @Override
  public BitSet getIncompatibleRegs() {
    BitSet incompatRegs = new BitSet(1);
    if (!getRegA().fitsShort()) {
      incompatRegs.set(REG_A_IDX);
    }
    return incompatRegs;
  }

  @Override
  public String toString() {
    return super.toString() + " ref: " + referencedItem;
  }
}
