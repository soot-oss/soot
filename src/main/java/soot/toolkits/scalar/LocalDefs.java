package soot.toolkits.scalar;

import com.google.common.collect.Iterators;

import java.util.Iterator;

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

import java.util.List;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.shimple.PhiExpr;

/**
 * Provides an interface for querying for the definitions of a Local at a given Unit in a method.
 */
public interface LocalDefs {

  /**
   * Returns the definition sites for a Local at a certain point (Unit) in a method.
   *
   * You can assume this method never returns {@code null}.
   *
   * @param l
   *          the Local in question.
   * @param s
   *          a unit that specifies the method context (location) to query for the definitions of the Local.
   * @return a list of Units where the local is defined in the current method context. If there are no uses an empty list
   *         will returned.
   */
  public List<Unit> getDefsOfAt(Local l, Unit s);

  /**
   * Returns the definition sites for a Local at a certain point (Unit) in a method.
   *
   * You can assume this method never returns {@code null}.
   * 
   * Potentially more efficient than getDefsOfAt.
   *
   * @param l
   *          the Local in question.
   * @param s
   *          a unit that specifies the method context (location) to query for the definitions of the Local.
   * @return an iterator iterating over all Units where the local is defined in the current method context. If there are no
   *         uses an empty list will returned.
   */
  public default Iterator<Unit> getDefsOfAtIterator(Local l, Unit s) {
    return getDefsOfAt(l, s).iterator();
  }

  /**
   * Returns true when there are definition sites for a Local at a certain point (Unit) in a method.
   *
   * @param l
   *          the Local in question.
   * @param s
   *          a unit that specifies the method context (location) to query for the definitions of the Local.
   * @return true when there are definitions
   */
  public default boolean hasDefsOfAt(Local l, Unit s) {
    return !getDefsOfAt(l, s).isEmpty();
  }

  /**
   * Checks whether the definitions of <code>l</code> agree at units <code>a</code> and <code>b</code>
   * @param l the local to check
   * @param a the first usage site
   * @param b the second usage site
   * @return whether the set of definition statements agree
   */
  public default boolean doDefsAgreeAt(Local l, Unit a, Unit b) {
    Iterator<Unit> la = getDefsOfAtIterator(l, a);
    Iterator<Unit> lb = getDefsOfAtIterator(l, b);
    
    //Check whether the iterator elements are equal
    //Is slightly faster than using Iterator.equals since we only
    //check the identity here
    while (la.hasNext()) {
      Unit ua = la.next();
      if (!lb.hasNext()) {
        return false;
      }
      Unit ub = lb.next();
      if (ua != ub) {
        return false;
      }
    }
    return !lb.hasNext();
  }

  /**
   * Returns the definition sites for a Local merged over all points in a method.
   *
   * You can assume this method never returns {@code null}.
   *
   * @param l
   *          the Local in question.
   * @return a list of Units where the local is defined in the current method context. If there are no uses an empty list
   *         will returned.
   */
  public List<Unit> getDefsOf(Local l);
}
