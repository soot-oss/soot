package soot.jimple.toolkits.pointer;
import soot.*;

/** A very naive pointer analysis that just reports that any points can point
 * to any object. */
public class DumbPointerAnalysis implements PointsToAnalysis {
    public DumbPointerAnalysis( Singletons.Global g ) {}
    public static DumbPointerAnalysis v() { return G.v().DumbPointerAnalysis(); }

    /** Returns the set of objects pointed to by variable l. */
    public PointsToSet reachingObjects( Local l ) {
        Type t = l.getType();
        if( t instanceof RefType ) return FullObjectSet.v((RefType) t);
	return FullObjectSet.v();
    }

    /** Returns the set of objects pointed to by static field f. */
    public PointsToSet reachingObjects( SootField f ) {
        Type t = f.getType();
        if( t instanceof RefType ) return FullObjectSet.v((RefType) t);
	return FullObjectSet.v();
    }

    /** Returns the set of objects pointed to by instance field f
     * of the objects in the PointsToSet s. */
    public PointsToSet reachingObjects( PointsToSet s, SootField f ) {
        return reachingObjects(f);
    }

    /** Returns the set of objects pointed to by instance field f
     * of the objects pointed to by l. */
    public PointsToSet reachingObjects( Local l, SootField f ) {
        return reachingObjects(f);
    }
}

