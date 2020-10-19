package soot.jimple;

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

public class GroupIntPair {
  public final Object group;
  public final int x;

  public GroupIntPair(Object group, int x) {
    this.group = group;
    this.x = x;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof GroupIntPair) {
      GroupIntPair cast = (GroupIntPair) other;
      return cast.group.equals(this.group) && cast.x == this.x;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return group.hashCode() + 1013 * x;
  }

  @Override
  public String toString() {
    return this.group + ": " + this.x;
  }
}
