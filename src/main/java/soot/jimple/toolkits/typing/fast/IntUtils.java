package soot.jimple.toolkits.typing.fast;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.IntegerType;
import soot.ShortType;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2008 Ben Bellamy
 *
 * All rights reserved.
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
 * Contains several utilities for integer typing.
 * 
 * @author Marc Miltenberger
 */
public class IntUtils {

  /**
   * Returns an appropriate integer for a given maximum element.
   * 
   * Throws an exception in case of an unsupported size.
   * 
   * @param maxValue
   *          the max value
   * @return the integer
   */
  public static IntegerType getTypeByWidth(int maxValue) {
    switch (maxValue) {
      case 1:
        return Integer1Type.v();
      case 127:
        return Integer127Type.v();
      case 32767:
        return Integer32767Type.v();
      case Integer.MAX_VALUE:
        return IntType.v();
      default:
        throw new RuntimeException("Unsupported width: " + maxValue);
    }
  }

  /**
   * Returns the maximum value an integer type can have.
   * 
   * @param t
   *          the integer type
   * @return the maximum value
   */
  public static int getMaxValue(IntegerType t) {
    if (t instanceof Integer1Type || t instanceof BooleanType) {
      return 1;
    }
    if (t instanceof Integer127Type || t instanceof ByteType) {
      return 127;
    }
    if (t instanceof Integer32767Type || t instanceof ShortType || t instanceof CharType) {
      return 32767;
    }
    if (t instanceof IntType) {
      return Integer.MAX_VALUE;
    }
    throw new RuntimeException("Unsupported type: " + t);
  }

}
