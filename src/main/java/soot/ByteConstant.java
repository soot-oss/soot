package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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
import soot.jimple.IntConstant;

public class ByteConstant extends IntConstant {
  private static final long serialVersionUID = 0L;
  public static final ByteConstant ZERO = new ByteConstant(0);
  public static final ByteConstant ONE = new ByteConstant(1);

  private static final int MAX_CACHE = 128;
  private static final int MIN_CACHE = -127;
  private static final int ABS_MIN_CACHE = Math.abs(MIN_CACHE);
  private static final ByteConstant[] CACHED = new ByteConstant[1 + MAX_CACHE + ABS_MIN_CACHE];

  public ByteConstant(byte value) {
    super(value);
  }

  public ByteConstant(int value) {
    super((byte) value);
  }

  public static ByteConstant v(int value) {
    if (value >= MIN_CACHE && value <= MAX_CACHE) {
      int idx = value + ABS_MIN_CACHE;
      ByteConstant c = CACHED[idx];
      if (c != null) {
        return c;
      }
      c = new ByteConstant(value);
      CACHED[idx] = c;
      return c;
    }
    return new ByteConstant(value);
  }

  public byte getByte() {
    return (byte) value;
  }

  @Override
  public Number getNumericValue() {
    return (byte) value;
  }

  @Override
  public Type getType() {
    return ByteType.v();
  }
}
