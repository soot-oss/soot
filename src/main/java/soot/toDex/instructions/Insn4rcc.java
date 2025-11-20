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

package soot.toDex.instructions;

import com.android.tools.smali.dexlib2.Opcode;
import com.android.tools.smali.dexlib2.builder.BuilderInstruction;
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction4rcc;
import com.android.tools.smali.dexlib2.iface.reference.Reference;

import java.util.BitSet;
import java.util.List;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

/**
 * The "4rcc" instruction format <br>
 * It is used by the "invoke-polymorphic/range" opcode
 */
public class Insn4rcc extends AbstractInsn {
  private short regCount;

  private Reference referencedItem;

  private Reference referencedItem2;

  public Insn4rcc(Opcode opc, List<Register> regs, short regCount, Reference referencedItem, Reference referencedItem2) {
    super(opc);
    this.regs = regs;
    this.regCount = regCount;
    this.referencedItem = referencedItem;
    this.referencedItem2 = referencedItem2;
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    Register startReg = regs.get(0);
    return new BuilderInstruction4rcc(opc, startReg.getNumber(), regCount, referencedItem, referencedItem2);
  }

  @Override
  public BitSet getIncompatibleRegs() {
    // if there is one problem -> all regs are incompatible (this could be optimized in reg allocation, probably)
    int regCount = SootToDexUtils.getRealRegCount(regs);
    if (hasHoleInRange()) {
      return getAllIncompatible(regCount);
    }
    for (Register r : regs) {
      if (!r.fitsUnconstrained()) {
        return getAllIncompatible(regCount);
      }
      if (r.isWide()) {
        boolean secondWideHalfFits = Register.fitsUnconstrained(r.getNumber() + 1, false);
        if (!secondWideHalfFits) {
          return getAllIncompatible(regCount);
        }
      }
    }
    return new BitSet(regCount);
  }

  private static BitSet getAllIncompatible(int regCount) {
    BitSet incompatRegs = new BitSet(regCount);
    incompatRegs.flip(0, regCount);
    return incompatRegs;
  }

  private boolean hasHoleInRange() {
    // the only "hole" that is allowed: if regN is wide -> regN+1 must not be there
    Register startReg = regs.get(0);
    int nextExpectedRegNum = startReg.getNumber() + 1;
    if (startReg.isWide()) {
      nextExpectedRegNum++;
    }
    // loop starts at 1, since the first reg alone cannot have a hole
    for (int i = 1; i < regs.size(); i++) {
      Register r = regs.get(i);
      int regNum = r.getNumber();
      if (regNum != nextExpectedRegNum) {
        return true;
      }
      nextExpectedRegNum++;
      if (r.isWide()) {
        nextExpectedRegNum++;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return super.toString() + " ref: " + referencedItem;
  }
}
