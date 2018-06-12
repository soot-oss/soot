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

class IntValueContainer {
  private static final int BOT = 0;
  private static final int TOP = 1;
  private static final int INT = 2;

  private int type;
  private int value;

  public IntValueContainer() {
    this.type = BOT;
  }

  public IntValueContainer(int v) {
    this.type = INT;
    this.value = v;
  }

  public boolean isBottom() {
    return (this.type == BOT);
  }

  public boolean isTop() {
    return (this.type == TOP);
  }

  public boolean isInteger() {
    return this.type == INT;
  }

  public int getValue() {
    if (this.type != INT) {
      throw new RuntimeException("IntValueContainer: not integer type");
    }

    return this.value;
  }

  public void setTop() {
    this.type = TOP;
  }

  public void setValue(int v) {
    this.type = INT;
    this.value = v;
  }

  public void setBottom() {
    this.type = BOT;
  }

  public String toString() {
    if (type == BOT) {
      return "[B]";
    } else if (type == TOP) {
      return "[T]";
    } else {
      return "[" + value + "]";
    }
  }

  public boolean equals(Object other) {
    if (!(other instanceof IntValueContainer)) {
      return false;
    }

    IntValueContainer otherv = (IntValueContainer) other;

    if ((this.type == INT) && (otherv.type == INT)) {
      return (this.value == otherv.value);
    } else {
      return (this.type == otherv.type);
    }
  }

  public IntValueContainer dup() {
    IntValueContainer other = new IntValueContainer();
    other.type = this.type;
    other.value = this.value;

    return other;
  }
}
