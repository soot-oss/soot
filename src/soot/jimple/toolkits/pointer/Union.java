/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.toolkits.pointer;
import java.util.Set;

import soot.PointsToSet;

/** A generic interface to some set of runtime objects computed by a pointer analysis. */
public abstract class Union implements PointsToSet {
    /** Adds all objects in s into this union of sets, returning true if this
     * union was changed. */
    public abstract boolean addAll( PointsToSet s );

    public static boolean hasNonEmptyIntersection( PointsToSet s1, PointsToSet s2 ) {
        if( s1 == null ) return false;
        if( s1 instanceof  Union ) return s1.hasNonEmptyIntersection( s2 );
        if( s2 == null ) return false;
        return s2.hasNonEmptyIntersection( s1 );
    }
    public Set possibleStringConstants() { return null; }
    public Set possibleClassConstants() { return null; }

}

