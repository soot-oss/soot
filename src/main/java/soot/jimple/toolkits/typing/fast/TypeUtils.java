package soot.jimple.toolkits.typing.fast;

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

import soot.BooleanType;
import soot.ByteType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.Type;

public class TypeUtils {

  /**
   * Returns the bit size of a given primitive type. Note that it returns 1 for boolean albeit not being possible on a real
   * machine.
   * 
   * @param type
   * @return the size
   */
  public static int getValueBitSize(Type type) {
    if (type instanceof BooleanType) {
      return 1;
    }
    if (type instanceof ByteType) {
      return 8;
    }
    if (type instanceof ShortType) {
      return 16;
    }
    if (type instanceof IntType) {
      return 32;
    }
    if (type instanceof LongType) {
      return 64;
    }
    if (type instanceof FloatType) {
      return 32;
    }
    if (type instanceof DoubleType) {
      return 64;
    }
    if (type instanceof Integer127Type) {
      return 8;
    }
    if (type instanceof Integer32767Type) {
      return 16;
    }
    if (type instanceof Integer1Type) {
      return 1;
    }
    if (type instanceof RefType) {
      return 64; // not valid for 32 bit Java VMs, but I don't see a different way to handle this in a static analysis
    }
    throw new IllegalArgumentException(type + " not supported.");
  }

}
