package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Archie L. Cobbs
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

import soot.jimple.LongConstant;

public class LongConstantValueTag extends ConstantValueTag {

  public static final String NAME = "LongConstantValueTag";

  private final LongConstant value;

  public LongConstantValueTag(long value) {
    super(new byte[] { (byte) ((value >> 56) & 0xff), (byte) ((value >> 48) & 0xff), (byte) ((value >> 40) & 0xff),
        (byte) ((value >> 32) & 0xff), (byte) ((value >> 24) & 0xff), (byte) ((value >> 16) & 0xff),
        (byte) ((value >> 8) & 0xff), (byte) ((value) & 0xff) });
    this.value = LongConstant.v(value);
  }

  public LongConstantValueTag(LongConstant value) {
    super(null);
    this.value = value;
  }

  public long getLongValue() {
    return value.value;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String toString() {
    return "ConstantValue: " + value.value;
  }

  @Override
  public LongConstant getConstant() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (value.value ^ (value.value >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || (this.getClass() != obj.getClass())) {
      return false;
    }
    LongConstantValueTag other = (LongConstantValueTag) obj;
    return this.value == other.value;
  }
}
