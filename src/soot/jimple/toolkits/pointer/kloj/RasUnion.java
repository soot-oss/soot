package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.jimple.spark.PointsToSet;

public class RasUnion extends Union {
    Ras r;
    boolean rIsUnmodifiable = false;

    public boolean isEmpty() {
	return r == null || r.isEmpty();
    }
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
	boolean ret;
	
	if( r == null ) {
	    return false;
	}
	if( other instanceof RasUnion ) {
	    RasUnion o = (RasUnion) other;
	    if( o.r == null ) {
		return false;
	    }
	    ret = r.hasNonEmptyIntersection( o.r );
	} else {
	    ret = r.hasNonEmptyIntersection( other );
	}
	return ret;
    }
    public boolean addAll( PointsToSet s ) {
	if( s == null || s.isEmpty() ) return false;
	if( r == null ) {
	    if( s instanceof Ras ) {
		r = (Ras) s;
		rIsUnmodifiable = true;
		return !r.isEmpty();
	    }
	    r = Ras.factory.newRas( null );
	    rIsUnmodifiable = false;
	} else if( rIsUnmodifiable ) {
	    Ras newR = Ras.factory.newRas( null );
	    rIsUnmodifiable = false;
	    newR.addAll( r );
	    r = newR;
	}
	if( s instanceof RasUnion ) {
	    return r.addAll( ((RasUnion) s).r ); 
	}
	return r.addAll( (Ras) s );
    }
    public Object clone() {
	RasUnion ret = new RasUnion();
	ret.r = r;
	rIsUnmodifiable = true;
	ret.rIsUnmodifiable = true;
	return ret;
    }
    public Set possibleTypes() {
	if( r == null ) {
	    return Collections.EMPTY_SET;
	}
	return r.possibleTypes();
    }
    static int count = 0;
    public RasUnion() {
	count++;
	if( ( count % 1000 ) == 0 ) System.out.println( "Made "+count+"th RasUnion" );
    }
}
