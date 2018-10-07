package soot.jimple.toolkits.annotation.arraycheck;

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

class IntContainer {
  static IntContainer[] pool = new IntContainer[100];
  static {
    for (int i = 0; i < 100; i++) {
      pool[i] = new IntContainer(i - 50);
    }
  }

  int value;

  public IntContainer(int v) {
    this.value = v;
  }

  public static IntContainer v(int v) {
    if ((v >= -50) && (v <= 49)) {
      return pool[v + 50];
    } else {
      return new IntContainer(v);
    }
  }

  public IntContainer dup() {
    return new IntContainer(value);
  }

  public int hashCode() {
    return value;
  }

  public boolean equals(Object other) {
    if (other instanceof IntContainer) {
      return ((IntContainer) other).value == this.value;
    }

    return false;
  }

  public String toString() {
    return "" + value;
  }

}
