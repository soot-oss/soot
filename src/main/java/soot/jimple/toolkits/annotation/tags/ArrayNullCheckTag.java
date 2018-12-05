package soot.jimple.toolkits.annotation.tags;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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
 * ArrayNullCheckTag combines ArrayCheckTag and NullCheckTag into one tag. It uses bits of one byte value to represent the
 * check information. The right-most two bits stand for the array bounds checks, and the right third bit represents the null
 * check.
 * <p>
 *
 * For array references, the right three bits are meaningful; for other object refrences, only null check bit should be used.
 *
 * @see ArrayCheckTag
 * @see NullCheckTag
 */
public class ArrayNullCheckTag implements OneByteCodeTag {
  private final static String NAME = "ArrayNullCheckTag";

  private byte value = 0;

  public ArrayNullCheckTag() {
  }

  public ArrayNullCheckTag(byte v) {
    value = v;
  }

  public String getName() {
    return NAME;
  }

  public byte[] getValue() {
    byte[] bv = new byte[1];
    bv[0] = value;
    return bv;
  }

  public String toString() {
    return Byte.toString(value);
  }

  /** Accumulates another byte value by OR. */
  public byte accumulate(byte other) {
    byte oldv = value;
    value |= other;
    return oldv;
  }
}
