package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;
import soot.jimple.spark.PointsToSet;

public class FullRWSet extends RWSet {
    public boolean getCallsNative() {
	return true;
    }
    public boolean setCallsNative() {
	throw new RuntimeException( "Unsupported" );
    }

    /** Returns an iterator over any globals read/written. */
    public Set getGlobals() {
	throw new RuntimeException( "Unsupported" );
    }
    public Set getFields() {
	throw new RuntimeException( "Unsupported" );
    }
    public PointsToSet getBaseForField( Object f ) {
	throw new RuntimeException( "Unsupported" );
    }
    public boolean hasNonEmptyIntersection( RWSet other ) {
	if( other == null ) return false;
	return true;
    }
    /** Adds the RWSet other into this set. */
    public boolean union( RWSet other ) {
	throw new RuntimeException( "Unsupported" );
    }
    public boolean addGlobal( SootField global ) {
	throw new RuntimeException( "Unsupported" );
    }
    public boolean addFieldRef( PointsToSet otherBase, Object field ) {
	throw new RuntimeException( "Unsupported" );
    }
    public boolean isEquivTo( RWSet other ) {
	if( other instanceof FullRWSet ) return true;
	return false;
    }
}
