package soot.jimple.toolkits.pointer;
import soot.*;

/** A very naive pointer analysis that just reports that any points can point
 * to any object. */
public class DumbPointerAnalysis implements PointsToAnalysis {
    public DumbPointerAnalysis( Singletons.Global g ) {}
    public static DumbPointerAnalysis v() { return G.v().DumbPointerAnalysis(); }

    /** Returns the set of objects reaching variable l before stmt in method. */
    public PointsToSet reachingObjects( Local l ) {
        Type t = l.getType();
        if( t instanceof RefType ) return FullObjectSet.v((RefType) t);
	return FullObjectSet.v();
    }
}

