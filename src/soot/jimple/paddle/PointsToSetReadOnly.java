/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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
public abstract class PointsToSetReadOnly implements PointsToSet {
    /** Calls v's visit method on all nodes in this set. */
    public abstract boolean forall( P2SetVisitor v );
    /** Returns set of newly-added nodes since last call to flushNew. */
    public PointsToSetReadOnly getNewSet() { return this; }
    /** Returns set of nodes already present before last call to flushNew. */
    public PointsToSetReadOnly getOldSet() { return EmptyPointsToSet.v(); }
    /** Returns true iff the set contains n. */
    public abstract boolean contains( Node n );

    public PointsToSetReadOnly( Type type ) { this.type = type; }

    public boolean hasNonEmptyIntersection( PointsToSet other ) {
        final PointsToSetReadOnly o = (PointsToSetReadOnly) other;
        return forall( new P2SetVisitor() {
            public void visit( Node n ) {
                if( o.contains( n ) ) returnValue = true;
            }
        } );
    }
    public Set possibleTypes() {
        final HashSet ret = new HashSet();
        forall( new P2SetVisitor() {
            public void visit( Node n ) {
                Type t = n.getType();
                if( t instanceof RefType ) {
                    RefType rt = (RefType) t;
                    if( rt.getSootClass().isAbstract() ) return;
                }
                ret.add( t );
            }
        } );
        return ret;
    }
    public Type getType() {
        return type;
    }
    public void setType( Type type ) {
        this.type = type;
    }
    public int size() {
        final int[] ret = new int[1];
        forall( new P2SetVisitor() {
            public void visit( Node n ) {
                ret[0]++;
            }
        } );
        return ret[0];
    }
    public String toString() {
        final StringBuffer ret = new StringBuffer();
        this.forall( new P2SetVisitor() {
        public final void visit( Node n ) {
            ret.append( ""+n+"," );
        }} );
        return ret.toString();
    }

    public Set possibleStringConstants() { 
        final HashSet ret = new HashSet();
        return this.forall( new P2SetVisitor() {
        public final void visit( Node n ) {
            if( n instanceof StringConstantNode ) {
                ret.add( ((StringConstantNode)n).getString() );
            } else {
                returnValue = true;
            }
        }} ) ? null : ret;
    }
    public Set possibleClassConstants() {
    	return Collections.EMPTY_SET;
    }

    /* End of public methods. */
    /* End of package methods. */

    protected Type type;
}

