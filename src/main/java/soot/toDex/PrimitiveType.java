package soot.toDex;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.Locale;

/**
 * An enumeration for the primitive types the Dalvik VM can handle.
 */
public enum PrimitiveType {

  // NOTE: the order is relevant for cast code generation, so do not change it
  BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE;

  public String getName() {
    // return lower case name that is locale-insensitive
    return this.name().toLowerCase(Locale.ENGLISH);
  }

  public static PrimitiveType getByName(String name) {
    for (PrimitiveType p : values()) {
      if (p.getName().equals(name)) {
        return p;
      }
    }
    throw new RuntimeException("not found: " + name);
  }
}
