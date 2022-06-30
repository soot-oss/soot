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
import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.SwitchLabelElement;
import org.jf.dexlib2.builder.instruction.BuilderSparseSwitchPayload;

import soot.Unit;
import soot.jimple.Stmt;
import soot.toDex.LabelAssigner;

/**
 * The payload for a sparse-switch instruction.
 * 
 * @see SwitchPayload
 */
public class SparseSwitchPayload extends SwitchPayload {

  private int[] keys;

  public SparseSwitchPayload(int[] keys, List<Unit> targets) {
    super(targets);
    this.keys = keys;
  }

  @Override
  public int getSize() {
    // size = (identFieldSize+sizeFieldSize) + numTargets * (keyFieldSize+targetFieldSize)
    return 2 + targets.size() * 4;
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    List<SwitchLabelElement> elements = new ArrayList<SwitchLabelElement>();
    for (int i = 0; i < keys.length; i++) {
      elements.add(new SwitchLabelElement(keys[i], assigner.getOrCreateLabel((Stmt) targets.get(i))));
    }
    return new BuilderSparseSwitchPayload(elements);
  }

}
