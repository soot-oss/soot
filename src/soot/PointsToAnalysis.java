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

/** A generic interface to any type of pointer analysis.
 * @author Ondrej Lhotak
 */

public interface PointsToAnalysis {
    /** Returns the set of objects pointed to by variable l. */
    public PointsToSet reachingObjects( Local l );

    /** Returns the set of objects pointed to by static field f. */
    public PointsToSet reachingObjects( SootField f );

    /** Returns the set of objects pointed to by instance field f
     * of the objects in the PointsToSet s. */
    public PointsToSet reachingObjects( PointsToSet s, SootField f );

    /** Returns the set of objects pointed to by instance field f
     * of the objects pointed to by l. */
    public PointsToSet reachingObjects( Local l, SootField f );

    /** Returns the set of objects pointed to by elements of the arrays
     * in the PointsToSet s. */
    public PointsToSet reachingObjectsOfArrayElement( PointsToSet s );

    public static final Integer THIS_NODE = new Integer( -1 );
    public static final int RETURN_NODE = -2;
    public static final Integer THROW_NODE = new Integer( -3 );
    public static final Integer ARRAY_ELEMENTS_NODE = new Integer( -4 );
    public static final Integer CAST_NODE = new Integer( -5 );
    public static final Integer STRING_ARRAY_NODE = new Integer( -6 );
    public static final Integer STRING_NODE = new Integer( -7 );
    public static final Integer STRING_NODE_LOCAL = new Integer( -8 );
    public static final Integer EXCEPTION_NODE = new Integer( -9 );
    public static final Integer RETURN_STRING_CONSTANT_NODE = new Integer( -10 );
    public static final Integer STRING_ARRAY_NODE_LOCAL = new Integer( -11 );
    public static final Integer MAIN_THREAD_NODE = new Integer( -12 );
    public static final Integer MAIN_THREAD_NODE_LOCAL = new Integer( -13 );
    public static final Integer MAIN_THREAD_GROUP_NODE = new Integer( -14 );
    public static final Integer MAIN_THREAD_GROUP_NODE_LOCAL = new Integer( -15 );
    public static final Integer MAIN_CLASS_NAME_STRING = new Integer( -16 );
    public static final Integer MAIN_CLASS_NAME_STRING_LOCAL = new Integer( -17 );
    public static final Integer DEFAULT_CLASS_LOADER = new Integer( -18 );
    public static final Integer DEFAULT_CLASS_LOADER_LOCAL = new Integer( -19 );

}

