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
 * Implementation of the Tag interface for array bounds checks.
 */
public class ArrayCheckTag implements OneByteCodeTag {

  public static final String NAME = "ArrayCheckTag";

  private final boolean lowerCheck;
  private final boolean upperCheck;

  /**
   * A tag represents two bounds checks of an array reference. The value 'true' indicates check needed.
   */
  public ArrayCheckTag(boolean lower, boolean upper) {
    lowerCheck = lower;
    upperCheck = upper;
  }

  /**
   * Returns back the check information in binary form, which will be written into the class file.
   */
  @Override
  public byte[] getValue() {
    byte b = 0;
    if (lowerCheck) {
      b |= 0x01;
    }
    if (upperCheck) {
      b |= 0x02;
    }
    return new byte[] { b };
  }

  /**
   * Needs upper bound check?
   */
  public boolean isCheckUpper() {
    return upperCheck;
  }

  /**
   * Needs lower bound check?
   */
  public boolean isCheckLower() {
    return lowerCheck;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String toString() {
    return (lowerCheck ? "[potentially unsafe lower bound]" : "[safe lower bound]") + ""
        + (upperCheck ? "[potentially unsafe upper bound]" : "[safe upper bound]");
  }
}
