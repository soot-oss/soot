package soot.jimple.toolkits.pointer;
import soot.jimple.spark.PointsToSet;
import java.util.*;
import soot.*;

public class FullObjectSet extends Union implements PointsToSet {
    public FullObjectSet( Singletons.Global g ) {}
    public static FullObjectSet v() { return G.v().FullObjectSet(); }

    /** Returns true if this set contains no run-time objects. */
    public boolean isEmpty() {
	return false;
    }
    /** Returns true if this set is a subset of other. */
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
	return other != null;
    }
    private final Set types = 
        Collections.singleton( AnySubType.v( RefType.v( "java.lang.Object" ) ) );
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

