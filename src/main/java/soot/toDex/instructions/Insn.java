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
import java.util.List;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;

/**
 * Interface for the dalvik instruction formats.
 */
public interface Insn extends Cloneable {

  Opcode getOpcode();

  List<Register> getRegs();

  BitSet getIncompatibleRegs();

  boolean hasIncompatibleRegs();

  int getMinimumRegsNeeded();

  BuilderInstruction getRealInsn(LabelAssigner assigner);

  int getSize();
}
