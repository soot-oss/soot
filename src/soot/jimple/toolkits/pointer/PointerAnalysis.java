package soot.jimple.toolkits.pointer;
import soot.*;
import soot.jimple.*;
import soot.jimple.spark.PointsToAnalysis;
import soot.jimple.spark.PointsToSet;

/** A generic interface to any type of pointer analysis. */
public interface PointerAnalysis extends PointsToAnalysis {
//    public static final Integer THIS_NODE = new Integer( -1 );
    public static final Integer RETURN_NODE = new Integer( -2 );
    /*
    public static final Integer THROW_NODE = new Integer( -3 );
    public static final Integer ARRAY_ELEMENTS_NODE = new Integer( -4 );
    public static final Integer CAST_NODE = new Integer( -5 );
    public static final Integer STRING_ARRAY_NODE = new Integer( -6 );
    public static final Integer STRING_NODE = new Integer( -7 );
    public static final Integer STRING_NODE_LOCAL = new Integer( -8 );
    public static final Integer EXCEPTION_NODE = new Integer( -9 );
    public static final Integer RETURN_STRING_CONSTANT_NODE = new Integer( -10 );
    */

    /** Returns the set of objects reaching variable l before stmt in method. */
    public PointsToSet reachingObjects( Local l );
}

