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

import soot.toDex.LabelAssigner;

/**
 * Inspired by com.android.dx.dex.code.CodeAddress: pseudo instruction for use as jump target or start/end of an exception
 * handler range. It has size zero, so that its offset is the same as the following real instruction.
 */
public class AddressInsn extends AbstractInsn {

  private Object originalSource;

  public AddressInsn(Object originalSource) {
    super(Opcode.NOP);
    this.originalSource = originalSource;
  }

  public Object getOriginalSource() {
    return originalSource;
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    return null;
  }

  @Override
  public int getSize() {
    return 0;
  }

  @Override
  public String toString() {
    return "address instruction for " + originalSource;
  }
}
