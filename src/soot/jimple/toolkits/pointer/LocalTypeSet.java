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
}

