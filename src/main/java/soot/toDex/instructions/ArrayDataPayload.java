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

import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderArrayPayload;

import soot.toDex.LabelAssigner;

/**
 * Payload for the fill-array-data instructions in dex
 * 
 * @author Steven Arzt
 *
 */
public class ArrayDataPayload extends AbstractPayload {

  private final int elementWidth;
  private final List<Number> arrayElements;

  public ArrayDataPayload(int elementWidth, List<Number> arrayElements) {
    super();
    this.elementWidth = elementWidth;
    this.arrayElements = arrayElements;
  }

  @Override
  public int getSize() {
    // size = (identFieldSize+sizeFieldSize) + numValues * (valueSize)
    return 4 + (arrayElements.size() * elementWidth + 1) / 2;
  }

  @Override
  public int getMaxJumpOffset() {
    return Short.MAX_VALUE;
  }

  @Override
  protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
    return new BuilderArrayPayload(elementWidth, arrayElements);
  }

}
