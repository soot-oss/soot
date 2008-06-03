/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2002 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-2002.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.tagkit;

/** This class represents a tag for byte-code offset of
 * instructions that correspond to Jimple statements.
 *
 * @author Roman Manevich.
 * @since October 3 2002 Initial creation.
 */

public class BytecodeOffsetTag implements Tag {
  /** The index of the last byte-code instruction.
   */
  private int offset;

  /** Constructs a tag from the index offset.
   */
  public BytecodeOffsetTag(int offset) {
    this.offset = offset;
  }
	
  /** Returns the name of this tag.
   */
  public String getName() {
    return "BytecodeOffsetTag";
  }

  /** Returns the offset in a four byte array.
   */
  public byte [] getValue() {
    byte [] v = new byte[4];
    v[0] = (byte) ((offset >> 24) % 256);;
    v[1] = (byte) ((offset >> 16) % 256);;
    v[2] = (byte) ((offset >> 8) % 256);;
    v[3] = (byte) (offset % 256);
    return v;
  }

  /** Returns the offset as an int.
   */
  public int getBytecodeOffset() {
    return offset;
  }

  /** Returns the offset in a string.
   */
  public String toString() {
    return "" + offset;
  }
}
