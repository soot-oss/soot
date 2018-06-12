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
 * Soot representation used for not-yet-typed objects. Implemented as a singleton.
 */
@SuppressWarnings("serial")
public class UnknownType extends Type {
  public UnknownType(Singletons.Global g) {
  }

  public static UnknownType v() {
    return G.v().soot_UnknownType();
  }

  public int hashCode() {
    return 0x5CAE5357;
  }

  public boolean equals(Object t) {
    return this == t;
  }

  public String toString() {
    return "unknown";
  }

  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseUnknownType(this);
  }

  /** Returns the least common superclass of this type and other. */
  public Type merge(Type other, Scene cm) {
    if (other instanceof RefType) {
      return other;
    }
    throw new RuntimeException("illegal type merge: " + this + " and " + other);
  }
}
