package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 Clark Verbrugge
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

import java.util.Enumeration;

/**
 * An enumeration of a Utf8 allows one to run through the characters in a unicode string; used primarily for comparing
 * unicode strings. Note that unlike regular enumerations, and to be efficient (ie to avoid allocating an object in each call
 * to nextElement), the return value is accessed by this object's 'c' field, and nextElement merely returns this.
 *
 * @see CONSTANT_Utf8_info
 * @see Utf8_Enumeration#c
 * @see Utf8_Enumeration#nextElement
 * @author Clark Verbrugge
 */
public class Utf8_Enumeration implements Enumeration {

  /**
   * The latest character, as determined by nextElement.
   *
   * @see Utf8_Enumeration#nextElement
   */
  public int c; // latest character

  private short curindex;
  private short length;
  private byte bytes[];

  /**
   * For creating an empty enumeration; you must use reset() after this to initialize the enumeration.
   *
   * @see Utf8_Enumeration#reset
   */
  public Utf8_Enumeration() {
  }

  /**
   * For creating a normal enumeration of the given Utf8 string.
   *
   * @param b
   *          array of bytes in Utf8 format.
   */
  public Utf8_Enumeration(byte b[]) {
    bytes = b;
    curindex = (short) 2;
    length = (short) (((((bytes[0])) & 0xff) << 8) + (((bytes[1])) & 0xff) + 2);
  }

  /**
   * Resets this object to be an enumeration of the given Utf8 string.
   *
   * @param b
   *          array of bytes in Utf8 format.
   */
  public void reset(byte b[]) {
    bytes = b;
    curindex = (short) 2;
    length = (short) (((((bytes[0])) & 0xff) << 8) + (((bytes[1])) & 0xff) + 2);
  }

  /** <i>true</i> if the entire string hasn't been enumerated yet. */
  public boolean hasMoreElements() {
    if (curindex < length) {
      return true;
    }
    return false;
  }

  /**
   * Determines the next Utf8 character, and stores it in c.
   *
   * @return <i>this</i>
   * @see Utf8_Enumeration#c
   */
  public Object nextElement() {
    byte b;
    b = bytes[curindex++];
    if ((b & ((byte) 0x80)) == 0) { // one-byte character
      c = b;
    } else if ((b & ((byte) 0xe0)) == 0xc0) { // two-byte character
      c = ((b & ((byte) 0x1f))) << 6;
      b = bytes[curindex++];
      c |= (b & ((byte) 0x3f));
    } else { // three-byte character
      c = ((b & ((byte) 0x0f))) << 12;
      b = bytes[curindex++];
      c |= ((b & ((byte) 0x3f))) << 6;
      b = bytes[curindex++];
      c |= (b & ((byte) 0x3f));
    }
    return this;
  }
}
