package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.jimple.spark.PointsToSet;

public class ReallyCheapRasUnion extends Union {

    public boolean isEmpty() {
	return false;
    }
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
	return true;
    }
    public boolean addAll( PointsToSet s ) {
	return false;
    }
    public Object clone() {
	return this;
    }
    public Set possibleTypes() {
	throw new RuntimeException( "Not implemented" );
    }
    static int count = 0;
    public ReallyCheapRasUnion() {
	count++;
	if( ( count % 1000 ) == 0 ) System.out.println( "Made "+count+"th ReallyCheapRasUnion" );
    }
}
