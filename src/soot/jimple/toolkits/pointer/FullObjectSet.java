package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;

public class FullObjectSet extends Union {
    public FullObjectSet( Singletons.Global g ) {
        this( RefType.v( "java.lang.Object" ) );
    }
    public static FullObjectSet v() { return G.v().FullObjectSet(); }
    public static FullObjectSet v( RefType t ) { 
        if( t.getClassName().equals( "java.lang.Object" ) ) {
            return v();
        }
        return new FullObjectSet( t );
    }
    private final Set types;
    private FullObjectSet( RefType declaredType ) {
        AnySubType type = AnySubType.v( declaredType );
        types = Collections.singleton( type );
    }

    public AnySubType type() { return (AnySubType) types.iterator().next(); }

    /** Returns true if this set contains no run-time objects. */
    public boolean isEmpty() {
	return false;
    }
    /** Returns true if this set is a subset of other. */
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
	return other != null;
    }
    /** Set of all possible run-time types of objects in the set. */
    public Set possibleTypes() {
        return types;
    }

    /** Adds all objects in s into this union of sets, returning true if this
     * union was changed. */
    public boolean addAll( PointsToSet s ) {
	return false;
    }

    public Set possibleStringConstants() { return null; }
    public Set possibleClassConstants() { return null; }
}

