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
import soot.util.*;
import java.util.*;

/** Represents a set of (local,type) pairs using a bit-vector. */
class LocalTypeSet extends java.util.BitSet {
    protected List locals;
    protected List types;

    /** Constructs a new empty set given a list of all locals and types that may
     * ever be in the set. */
    public LocalTypeSet( List locals, List types ) {
	super( locals.size() * types.size() );
	this.locals = locals;
	this.types = types;
	if( !Scene.v().hasFastHierarchy() ) {
	    Scene.v().setFastHierarchy( new FastHierarchy() );
	}
    }
    /** Returns the number of the bit corresponding to the pair (l,t). */
    protected int indexOf( Local l, RefType t ) {
	if( locals.indexOf( l ) == -1 || types.indexOf( t ) == -1 ) {
	    throw new RuntimeException( "Invalid local or type in LocalTypeSet" );
	}
	return locals.indexOf( l ) * types.size() + types.indexOf( t );
    }
    /** Removes all pairs corresponding to local l from the set. */
    public void killLocal( Local l ) {
	int base = types.size() * locals.indexOf( l );
	for( int i = 0; i < types.size(); i++ ) {
	    clear( i + base );
	}
    }
    /** For each pair (from,t), adds a pair (to,t). */
    public void localCopy( Local to, Local from ) {
	int baseTo = types.size() * locals.indexOf( to );
	int baseFrom = types.size() * locals.indexOf( from );
	for( int i = 0; i < types.size(); i++ ) {
	    if( get( i+baseFrom ) ) {
		set( i+baseTo );
	    } else {
		clear( i+baseTo );
	    }
	}
    }
    /** Empties the set. */
    public void clearAllBits() {
	for( int i = 0; i < types.size() * locals.size(); i++ ) {
	    clear( i );
	}
    }
    /** Fills the set to contain all possible (local,type) pairs. */
    public void setAllBits() {
	for( int i = 0; i < types.size() * locals.size(); i++ ) {
	    set( i );
	}
    }
    /** Adds to the set all pairs (l,type) where type is any supertype of t. */
    public void localMustBeSubtypeOf( Local l, RefType t ) {
	FastHierarchy fh = Scene.v().getFastHierarchy();
	for( Iterator it = types.iterator(); it.hasNext(); ) {
	    RefType supertype = (RefType) it.next();
	    if( fh.canStoreType( t, supertype ) ) {
		set( indexOf( l, supertype ) );
	    }
	}
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        Iterator localsIt = locals.iterator();
        while (localsIt.hasNext()){
            Local l = (Local)localsIt.next();
            Iterator typesIt = types.iterator();
            while (typesIt.hasNext()){
                RefType t = (RefType)typesIt.next();
                int index = indexOf(l, t);
                //G.v().out.println("for: "+l+" and type: "+t+" at: "+index);
                if (get(index)) {
                    sb.append("(("+l+","+t+") -> elim cast check) ");
                }
            }
            
        }
        return sb.toString(); 
    }
}

