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
import soot.*;

/** Implementation of points-to set that holds two sets: one for new
 * elements that have not yet been propagated, and the other for elements
 * that have already been propagated.
 * @author Ondrej Lhotak
 */
public class DoublePointsToSet extends PointsToSetInternal {
    public DoublePointsToSet( Type type ) {
        super( type );
        newSet = PaddleScene.v().newSetFactory.newSet( type );
        oldSet = PaddleScene.v().oldSetFactory.newSet( type );
    }
    /** Returns true if this set contains no run-time objects. */
    public boolean isEmpty() {
        return oldSet.isEmpty() && newSet.isEmpty();
    }
    /** Returns true if this set shares some objects with other. */
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
        return oldSet.hasNonEmptyIntersection( other );
    }
    /** Set of all possible run-time types of objects in the set. */
    public Set possibleTypes() {
        return oldSet.possibleTypes();
    }
    /** Adds contents of other into this set, returns true if this set 
     * changed. */
    public boolean addAll( PointsToSetReadOnly other,
            PointsToSetReadOnly exclude ) {
        if( exclude != null ) {
            throw new RuntimeException( "NYI" );
        }
        return newSet.addAll( other, oldSet );
    }
    /** Calls v's visit method on all nodes in this set. */
    public boolean forall( P2SetVisitor v ) {
        oldSet.forall( v );
        newSet.forall( v );
        return v.getReturnValue();
    }
    /** Adds n to this set, returns true if n was not already in this set. */
    public boolean add( ContextAllocNode n ) {
        if( oldSet.contains( n ) ) return false;
        return newSet.add( n );
    }
    /** Returns set of nodes already present before last call to flushNew. */
    public PointsToSetReadOnly getOldSet() { return oldSet; }
    /** Returns set of newly-added nodes since last call to flushNew. */
    public PointsToSetReadOnly getNewSet() { return newSet; }
    /** Sets all newly-added nodes to old nodes. */
    public void flushNew() {
        oldSet.addAll( newSet, null );
        newSet = PaddleScene.v().newSetFactory.newSet( type );
    }
    /** Sets all nodes to newly-added nodes. */
    public void unFlushNew() {
        newSet.addAll( oldSet, null );
        oldSet = PaddleScene.v().oldSetFactory.newSet( type );
    }
    /** Merges other into this set. */
    public void mergeWith( PointsToSetInternal other ) {
        if( !( other instanceof DoublePointsToSet ) ) {
            throw new RuntimeException( "NYI" );
        }
        final DoublePointsToSet o = (DoublePointsToSet) other;
        if( other.type != null && !( other.type.equals( type ) ) ) {
            throw new RuntimeException( "different types "+type+" and "+other.type );
        }
        if( other.type == null && type != null ) {
            throw new RuntimeException( "different types "+type+" and "+other.type );
        }
        final PointsToSetInternal newNewSet = PaddleScene.v().newSetFactory.newSet( type );
        final PointsToSetInternal newOldSet = PaddleScene.v().oldSetFactory.newSet( type );
        oldSet.forall( new P2SetVisitor() {
        public final void visit( ContextAllocNode n ) {
            if( o.oldSet.contains( n ) ) newOldSet.add( n );
        }} );
        newNewSet.addAll( this, newOldSet );
        newNewSet.addAll( o, newOldSet );
        newSet = newNewSet;
        oldSet = newOldSet;
    }
    /** Returns true iff the set contains n. */
    public boolean contains( ContextAllocNode n ) {
        return oldSet.contains( n ) || newSet.contains( n );
    }

    public static P2SetFactory getFactory( P2SetFactory newFactory,
            P2SetFactory oldFactory ) {
        PaddleScene.v().newSetFactory = newFactory;
        PaddleScene.v().oldSetFactory = oldFactory;
        return new P2SetFactory() {
            public PointsToSetInternal newSet( Type type ) {
                return new DoublePointsToSet( type );
            }
        };
    }

    /* End of public methods. */
    /* End of package methods. */

    protected PointsToSetInternal newSet;
    protected PointsToSetInternal oldSet;
}

