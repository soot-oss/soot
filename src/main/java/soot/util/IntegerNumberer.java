package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Ondrej Lhotak
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
 * A numberer that associates each number with the corresponding Long object.
 */
public class IntegerNumberer implements Numberer<Long> {
  /** Tells the numberer that a new object needs to be assigned a number. */
  public void add(Long o) {
  }

  /**
   * Should return the number that was assigned to object o that was previously passed as an argument to add().
   */
  public long get(Long o) {
    if (o == null) {
      return 0;
    }
    return o.longValue();
  }

  /** Should return the object that was assigned the number number. */
  public Long get(long number) {
    if (number == 0) {
      return null;
    }
    return new Long(number);
  }

  /** Should return the number of objects that have been assigned numbers. */
  public int size() {
    throw new RuntimeException("IntegerNumberer does not implement the size() method.");
  }
}
