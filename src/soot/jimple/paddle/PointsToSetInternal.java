/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003 Ondrej Lhotak
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
import soot.*;
import java.util.*;

/** Abstract base class for implementations of points-to sets.
 * @author Ondrej Lhotak
 */
public abstract class PointsToSetInternal extends PointsToSetReadOnly {
    /** Adds contents of other minus the contents of exclude into this set;
     * returns true if this set changed. */
    public boolean addAll( PointsToSetReadOnly other,
            final PointsToSetReadOnly exclude ) {
        if( other instanceof DoublePointsToSet ) {
            return addAll( other.getNewSet(), exclude )
                | addAll( other.getOldSet(), exclude );
        } else if( other instanceof EmptyPointsToSet ) {
            return false;
        } else if( exclude instanceof EmptyPointsToSet ) { 
            return addAll( other, null );
        }
        if( !G.v().PointsToSetInternal_warnedAlready ) {
            G.v().out.println( "Warning: using default implementation of addAll. You should implement a faster specialized implementation." );
            G.v().out.println( "this is of type "+getClass().getName() );
            G.v().out.println( "other is of type "+other.getClass().getName() );
            if( exclude == null ) {
                G.v().out.println( "exclude is null" );
            } else {
                G.v().out.println( "exclude is of type "+
                        exclude.getClass().getName() );
            }
            G.v().PointsToSetInternal_warnedAlready = true;
        }
        return other.forall( new P2SetVisitor() {
        public final void visit( Node n ) {
                if( exclude == null || !exclude.contains( n ) )
                    returnValue = add( n ) | returnValue;
            }
        } );
    }
    /** Adds n to this set, returns true if n was not already in this set. */
    public abstract boolean add( Node n );
    /** Sets all newly-added nodes to old nodes. */
    public void flushNew() {}
    /** Sets all nodes to newly-added nodes. */
    public void unFlushNew() {}
    /** Merges other into this set. */
    public void mergeWith( PointsToSetInternal other ) 
    { addAll( other, null ); }

    public PointsToSetInternal( Type type ) { super(type); }

    /* End of public methods. */
    /* End of package methods. */
}

