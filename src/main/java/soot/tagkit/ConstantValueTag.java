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

import java.util.Arrays;

import soot.jimple.Constant;

public abstract class ConstantValueTag implements Tag {
  protected byte[] bytes; // encoded constant

  protected ConstantValueTag() {
  }

  public String getName() {
    String className = getClass().getName();
    return className.substring(className.lastIndexOf('.') + 1);
  }

  public byte[] getValue() {
    return bytes;
  }

  public abstract Constant getConstant();

  public abstract String toString();

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(bytes);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConstantValueTag other = (ConstantValueTag) obj;
    if (!Arrays.equals(bytes, other.bytes)) {
      return false;
    }
    return true;
  }

}
