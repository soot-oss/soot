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
import org.jf.dexlib2.builder.instruction.BuilderInstruction31i;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;

/**
 * The "31i" instruction format: It needs three 16-bit code units, has one register and is used for a 32-bit literal (hence
 * the "i" for "integer").<br>
 * <br>
 * It is used by the opcodes "const" and "const-wide/32".
 */
public class Insn31i extends AbstractInsn implements OneRegInsn {

  private int litB;

  public Insn31i(Opcode opc, Register regA, int litB) {
    super(opc);
    regs.add(regA);
    this.litB = litB;
  }

  public Register getRegA() {
    return regs.get(REG_A_IDX);
  }

  public int getLitB() {
    return litB;
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    return new BuilderInstruction31i(opc, (short) getRegA().getNumber(), getLitB());
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
    return super.toString() + " lit: " + getLitB();
  }
}
