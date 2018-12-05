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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

/**
 * Abstract implementation of an {@link Insn}.
 */
public abstract class AbstractInsn implements Insn {

  protected Opcode opc;

  protected List<Register> regs;

  public AbstractInsn(Opcode opc) {
    if (opc == null) {
      throw new IllegalArgumentException("opcode must not be null");
    }
    this.opc = opc;
    regs = new ArrayList<Register>();
  }

  public Opcode getOpcode() {
    return opc;
  }

  public List<Register> getRegs() {
    return regs;
  }

  public BitSet getIncompatibleRegs() {
    return new BitSet(0);
  }

  public boolean hasIncompatibleRegs() {
    return getIncompatibleRegs().cardinality() > 0;
  }

  public int getMinimumRegsNeeded() {
    BitSet incompatRegs = getIncompatibleRegs();
    int resultNeed = 0;
    int miscRegsNeed = 0;
    boolean hasResult = opc.setsRegister();
    if (hasResult && incompatRegs.get(0)) {
      resultNeed = SootToDexUtils.getDexWords(regs.get(0).getType());
    }
    for (int i = hasResult ? 1 : 0; i < regs.size(); i++) {
      if (incompatRegs.get(i)) {
        miscRegsNeed += SootToDexUtils.getDexWords(regs.get(i).getType());
      }
    }

    // The /2addr instruction format takes two operands and overwrites the
    // first operand register with the result. The result register is thus
    // not free to overlap as we still need to provide input data in it.
    // add-long/2addr r0 r0 -> 2 registers
    // add-int r0 r0 r2 -> 2 registers, re-use result register
    if (opc.name.endsWith("/2addr")) {
      return resultNeed + miscRegsNeed;
    } else {
      return Math.max(resultNeed, miscRegsNeed);
    }
  }

  @Override
  public BuilderInstruction getRealInsn(LabelAssigner assigner) {
    if (hasIncompatibleRegs()) {
      throw new RuntimeException("the instruction still has incompatible registers: " + getIncompatibleRegs());
    }
    return getRealInsn0(assigner);
  }

  protected abstract BuilderInstruction getRealInsn0(LabelAssigner assigner);

  @Override
  public String toString() {
    return opc.toString() + " " + regs;
  }

  public int getSize() {
    return opc.format.size / 2; // the format size is in byte count, we need word count
  }
}
