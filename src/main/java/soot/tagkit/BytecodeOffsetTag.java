package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2002 Raja Vallee-Rai
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

/**
 * This class represents a tag for byte-code offset of instructions that correspond to Jimple statements.
 *
 * @author Roman Manevich.
 * @since October 3 2002 Initial creation.
 */

public class BytecodeOffsetTag implements Tag {
  /**
   * The index of the last byte-code instruction.
   */
  protected int offset;

  /**
   * Constructs a tag from the index offset.
   */
  public BytecodeOffsetTag(int offset) {
    this.offset = offset;
  }

  /**
   * Returns the name of this tag.
   */
  public String getName() {
    return "BytecodeOffsetTag";
  }

  /**
   * Returns the offset in a four byte array.
   */
  public byte[] getValue() {
    byte[] v = new byte[4];
    v[0] = (byte) ((offset >> 24) % 256);
    ;
    v[1] = (byte) ((offset >> 16) % 256);
    ;
    v[2] = (byte) ((offset >> 8) % 256);
    ;
    v[3] = (byte) (offset % 256);
    return v;
  }

  /**
   * Returns the offset as an int.
   */
  public int getBytecodeOffset() {
    return offset;
  }

  /**
   * Returns the offset in a string.
   */
  public String toString() {
    return "" + offset;
  }
}
