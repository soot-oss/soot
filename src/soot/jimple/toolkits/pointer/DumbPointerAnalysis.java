package soot.jimple.toolkits.pointer;
import soot.*;
import soot.jimple.*;
import soot.jimple.spark.PointsToSet;

/** A very naive pointer analysis that just reports that any points can point
 * to any object. */
public class DumbPointerAnalysis implements PointerAnalysis {
    public DumbPointerAnalysis( Singletons.Global g ) {}
    public static DumbPointerAnalysis v() { return G.v().DumbPointerAnalysis(); }

    /** Returns the set of objects reaching variable l before stmt in method. */
    public PointsToSet reachingObjects( SootMethod method, Stmt stmt,
	    Local l ) {
	return FullObjectSet.v();
    }
}

