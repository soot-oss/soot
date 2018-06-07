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
import org.jf.dexlib2.builder.instruction.BuilderInstruction22b;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;

/**
 * The "22b" instruction format: It needs two 16-bit code units, has two registers and is used for a 8-bit literal (hence the
 * "b" for "byte").<br>
 * <br>
 * It is used by the "/lit8" opcodes for binary operations.
 */
public class Insn22b extends AbstractInsn implements TwoRegInsn {

  private byte litC;

  public Insn22b(Opcode opc, Register regA, Register regB, byte litC) {
    super(opc);
    regs.add(regA);
    regs.add(regB);
    this.litC = litC;
  }

  public Register getRegA() {
    return regs.get(REG_A_IDX);
  }

  public Register getRegB() {
    return regs.get(REG_B_IDX);
  }

  public byte getLitC() {
    return litC;
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    return new BuilderInstruction22b(opc, (short) getRegA().getNumber(), (short) getRegB().getNumber(), getLitC());
  }

  @Override
  public BitSet getIncompatibleRegs() {
    BitSet incompatRegs = new BitSet(2);
    if (!getRegA().fitsShort()) {
      incompatRegs.set(REG_A_IDX);
    }
    if (!getRegB().fitsShort()) {
      incompatRegs.set(REG_B_IDX);
    }
    return incompatRegs;
  }

  @Override
  public String toString() {
    return super.toString() + " lit: " + getLitC();
  }
}
