package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.PointsToSet;
import soot.jimple.ClassConstant;

/** A generic interface to some set of runtime objects computed by a pointer analysis. */
public abstract class Union implements PointsToSet {
  /**
   * Adds all objects in s into this union of sets, returning true if this union was changed.
   */
  public abstract boolean addAll(PointsToSet s);

  public static boolean hasNonEmptyIntersection(PointsToSet s1, PointsToSet s2) {
    if (s1 == null) {
      return false;
    }
    if (s1 instanceof Union) {
      return s1.hasNonEmptyIntersection(s2);
    }
    if (s2 == null) {
      return false;
    }
    return s2.hasNonEmptyIntersection(s1);
  }

  public Set<String> possibleStringConstants() {
    return null;
  }

  public Set<ClassConstant> possibleClassConstants() {
    return null;
  }

}
