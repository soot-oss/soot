package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrick Lam
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
 * This class implements an ArrayList where the equality and hashCode use object equality, not list equality. This is
 * important for putting Lists into HashMaps.
 *
 * The notation "Stationary" refers to the fact that the List stays "fixed" under list changes.
 */
public class StationaryArrayList<T> extends java.util.ArrayList<T> {
  public int hashCode() {
    return System.identityHashCode(this);
  }

  public boolean equals(Object other) {
    return this == other;
  }
}
