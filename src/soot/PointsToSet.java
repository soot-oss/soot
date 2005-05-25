/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot;
import java.util.*;

/** A generic interface to some set of runtime objects computed by a
 * pointer analysis.
 * @author Ondrej Lhotak
 */
public interface PointsToSet {
    /** Returns true if this set contains no run-time objects. */
    public boolean isEmpty();
    /** Returns true if this set shares some objects with other. */
    public boolean hasNonEmptyIntersection( PointsToSet other );
    /** Set of all possible run-time types of objects in the set. */
    public Set possibleTypes();

    /** If this points-to set consists entirely of string constants,
     * returns a set of these constant strings.
     * If this point-to set may contain something other than constant
     * strings, returns null. */
    public Set possibleStringConstants();

    /** If this points-to set consists entirely of objects of
     * type java.lang.Class of a known class,
     * returns a set of ClassConstant's that are these classes.
     * If this point-to set may contain something else, returns null. */
    public Set possibleClassConstants();
}

