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
import soot.*;

/** A very naive pointer analysis that just reports that any points can point
 * to any object. */
public class DumbPointerAnalysis implements PointsToAnalysis {
    public DumbPointerAnalysis( Singletons.Global g ) {}
    public static DumbPointerAnalysis v() { return G.v().soot_jimple_toolkits_pointer_DumbPointerAnalysis(); }

    /** Returns the set of objects pointed to by variable l. */
    public PointsToSet reachingObjects( Local l ) {
        Type t = l.getType();
        if( t instanceof RefType ) return FullObjectSet.v((RefType) t);
	return FullObjectSet.v();
    }

    /** Returns the set of objects pointed to by variable l in context c. */
    public PointsToSet reachingObjects( Context c, Local l ) {
        return reachingObjects(null, l);
    }

    /** Returns the set of objects pointed to by static field f. */
    public PointsToSet reachingObjects( SootField f ) {
        Type t = f.getType();
        if( t instanceof RefType ) return FullObjectSet.v((RefType) t);
	return FullObjectSet.v();
    }

    /** Returns the set of objects pointed to by instance field f
     * of the objects in the PointsToSet s. */
    public PointsToSet reachingObjects( PointsToSet s, SootField f ) {
        return reachingObjects(f);
    }

    /** Returns the set of objects pointed to by instance field f
     * of the objects pointed to by l. */
    public PointsToSet reachingObjects( Local l, SootField f ) {
        return reachingObjects(f);
    }

    /** Returns the set of objects pointed to by instance field f
     * of the objects pointed to by l in context c. */
    public PointsToSet reachingObjects( Context c, Local l, SootField f ) {
        return reachingObjects(f);
    }

    /** Returns the set of objects pointed to by elements of the arrays
     * in the PointsToSet s. */
    public PointsToSet reachingObjectsOfArrayElement( PointsToSet s ) {
        return FullObjectSet.v();
    }
}

