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

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction30t;

import soot.toDex.LabelAssigner;

/**
 * The "30t" instruction format: It needs three 16-bit code units, does not have any registers and is used for jump targets
 * (hence the "t").<br>
 * <br>
 * It is used by the "goto/32" opcode for jumps to a 32-bit wide offset.
 */
public class Insn30t extends InsnWithOffset {

  public Insn30t(Opcode opc) {
    super(opc);
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    return new BuilderInstruction30t(opc, assigner.getOrCreateLabel(target));
  }

  @Override
  public int getMaxJumpOffset() {
    return Integer.MAX_VALUE;
  }

}
