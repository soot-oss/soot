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
 * NullCheckTag contains the null pointer check information. The right third bit of a byte is used to represent whether the
 * null check is needed.
 */

public class NullCheckTag implements OneByteCodeTag {
  private final static String NAME = "NullCheckTag";

  private byte value = 0;

  public NullCheckTag(boolean needCheck) {
    if (needCheck) {
      value = 0x04;
    }
  }

  public String getName() {
    return NAME;
  }

  public byte[] getValue() {
    byte[] bv = new byte[1];
    bv[0] = value;
    return bv;
  }

  public boolean needCheck() {
    if (value == 0) {
      return false;
    } else {
      return true;
    }
  }

  public String toString() {
    return ((value == 0) ? "[not null]" : "[unknown]");
  }
}
