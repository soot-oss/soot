package soot;

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

import java.util.Set;

import soot.jimple.ClassConstant;

/**
 * A generic interface to some set of runtime objects computed by a pointer analysis.
 *
 * @author Ondrej Lhotak
 */
public interface PointsToSet {
  /** Returns true if this set contains no run-time objects. */
  public boolean isEmpty();

  /** Returns true if this set shares some objects with other. */
  public boolean hasNonEmptyIntersection(PointsToSet other);

  /** Set of all possible run-time types of objects in the set. */
  public Set<Type> possibleTypes();

  /**
   * If this points-to set consists entirely of string constants, returns a set of these constant strings. If this point-to
   * set may contain something other than constant strings, returns null.
   */
  public Set<String> possibleStringConstants();

  /**
   * If this points-to set consists entirely of objects of type java.lang.Class of a known class, returns a set of
   * ClassConstant's that are these classes. If this point-to set may contain something else, returns null.
   */
  public Set<ClassConstant> possibleClassConstants();
}
