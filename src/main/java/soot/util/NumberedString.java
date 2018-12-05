package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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
 * A class that assigns integers to java.lang.Strings.
 *
 * @author Ondrej Lhotak
 */

public final class NumberedString implements Numberable {
  public NumberedString(String s) {
    this.s = s;
  }

  public final String toString() {
    return getString();
  }

  public final String getString() {
    if (number == 0) {
      throw new RuntimeException("oops");
    }
    return s;
  }

  public final void setNumber(int number) {
    this.number = number;
  }

  public final int getNumber() {
    return number;
  }

  final private String s;
  private volatile int number;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + number;
    result = prime * result + ((s == null) ? 0 : s.hashCode());
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
    NumberedString other = (NumberedString) obj;
    if (number != other.number) {
      return false;
    }
    if (s == null) {
      if (other.s != null) {
        return false;
      }
    } else if (!s.equals(other.s)) {
      return false;
    }
    return true;
  }
}
