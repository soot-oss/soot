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
import org.jf.dexlib2.builder.instruction.BuilderInstruction10t;

import soot.toDex.LabelAssigner;

/**
 * The "10t" instruction format: It needs one 16-bit code unit, does not have any registers and is used for jump targets
 * (hence the "t").<br>
 * <br>
 * It is used by the "goto" opcode for jumps to offsets up to 8 bits away.
 */
public class Insn10t extends InsnWithOffset {

  public Insn10t(Opcode opc) {
    super(opc);
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    if (target == null) {
      throw new RuntimeException("Cannot jump to a NULL target");
    }
    return new BuilderInstruction10t(opc, assigner.getOrCreateLabel(target));
  }

  @Override
  public int getMaxJumpOffset() {
    return Byte.MAX_VALUE;
  }

}
