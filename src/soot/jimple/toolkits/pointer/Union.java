package soot.jimple.toolkits.pointer;
import soot.jimple.spark.PointsToSet;

/** A generic interface to some set of runtime objects computed by a pointer analysis. */
public abstract class Union implements PointsToSet {
    /** Adds all objects in s into this union of sets, returning true if this
     * union was changed. */
    public abstract boolean addAll( PointsToSet s );

    public static boolean hasNonEmptyIntersection( PointsToSet s1, PointsToSet s2 ) {
        if( s1 == null ) return false;
        if( s1 instanceof  Union ) return s1.hasNonEmptyIntersection( s2 );
        if( s2 == null ) return false;
        return s2.hasNonEmptyIntersection( s1 );
    }

    public static UnionFactory factory = null;
}

