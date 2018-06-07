package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.util.Switch;

/**
 * Soot representation of the Java type 'null'. Implemented as a singleton.
 */
@SuppressWarnings("serial")
public class NullType extends RefLikeType {
  public NullType(Singletons.Global g) {
  }

  public static NullType v() {
    return G.v().soot_NullType();
  }

  public int hashCode() {
    return 0x9891DFE1;
  }

  public boolean equals(Object t) {
    return this == t;
  }

  public String toString() {
    return "null_type";
  }

  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseNullType(this);
  }

  public Type getArrayElementType() {
    throw new RuntimeException("Attempt to get array base type of a non-array");
  }
}
