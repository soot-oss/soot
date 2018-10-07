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

class BoolValue {
  private boolean isRectangular;

  private static final BoolValue trueValue = new BoolValue(true);
  private static final BoolValue falseValue = new BoolValue(false);

  public BoolValue(boolean v) {
    isRectangular = v;
  }

  public static BoolValue v(boolean v) {
    if (v) {
      return trueValue;
    } else {
      return falseValue;
    }
  }

  public boolean getValue() {
    return isRectangular;
  }

  public boolean or(BoolValue other) {
    if (other.getValue()) {
      isRectangular = true;
    }

    return isRectangular;
  }

  public boolean or(boolean other) {
    if (other) {
      isRectangular = true;
    }
    return isRectangular;
  }

  public boolean and(BoolValue other) {
    if (!other.getValue()) {
      isRectangular = false;
    }

    return isRectangular;
  }

  public boolean and(boolean other) {
    if (!other) {
      isRectangular = false;
    }

    return isRectangular;
  }

  public int hashCode() {
    if (isRectangular) {
      return 1;
    } else {
      return 0;
    }
  }

  public boolean equals(Object other) {
    if (other instanceof BoolValue) {
      return isRectangular == ((BoolValue) other).getValue();
    }

    return false;
  }

  public String toString() {
    return "[" + isRectangular + "]";
  }
}
