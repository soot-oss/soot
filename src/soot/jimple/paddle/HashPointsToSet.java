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

package soot.jimple.paddle;
import java.util.*;
import soot.Type;

/** HashSet implementation of points-to set.
 * @author Ondrej Lhotak
 */
public final class HashPointsToSet extends PointsToSetInternal {
    public HashPointsToSet( Type type ) {
        super( type );
    }
    /** Returns true if this set contains no run-time objects. */
    public final boolean isEmpty() {
        return s.isEmpty();
    }
    /** Adds contents of other into this set, returns true if this set 
     * changed. */
    public final boolean addAll( final PointsToSetReadOnly other,
            final PointsToSetReadOnly exclude ) {
        if( other instanceof HashPointsToSet
        && exclude == null
        && ( PaddleScene.v().tm == null ||
            type == null || type.equals( other.type ) ) ) {
            return s.addAll( ((HashPointsToSet) other).s );
        } else {
            return super.addAll( other, exclude );
        }
    }
    /** Calls v's visit method on all nodes in this set. */
    public final boolean forall( P2SetVisitor v ) {
        for( Iterator it = new ArrayList(s).iterator(); it.hasNext(); ) {
            v.visit( (ContextAllocNode) it.next() );
        }
        return v.getReturnValue();
    }
    /** Adds n to this set, returns true if n was not already in this set. */
    public final boolean add( ContextAllocNode n ) {
        if( PaddleScene.v().tm.castNeverFails( n.getType(), type ) ) {

            return s.add( n );
        }
        return false;
    }
    /** Returns true iff the set contains n. */
    public final boolean contains( ContextAllocNode n ) {
        return s.contains( n );
    }
    public static P2SetFactory getFactory() {
        return new P2SetFactory() {
            public PointsToSetInternal newSet( Type type ) {
                return new HashPointsToSet( type );
            }
        };
    }

    /* End of public methods. */
    /* End of package methods. */

    private HashSet s = new HashSet(4);
}

