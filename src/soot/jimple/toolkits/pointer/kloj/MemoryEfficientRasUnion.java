package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.jimple.spark.PointsToSet;

public class MemoryEfficientRasUnion extends Union {
    HashSet subsets;

    public boolean isEmpty() {
	if( subsets == null ) return true;
	for( Iterator it = subsets.iterator(); it.hasNext(); ) {
	    PointsToSet subset = (PointsToSet) it.next();
	    if( !subset.isEmpty() ) return false;
	}
	return true;
    }
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
	if( subsets == null ) return true;
	for( Iterator it = subsets.iterator(); it.hasNext(); ) {
	    PointsToSet subset = (PointsToSet) it.next();
	    if( other instanceof Union ) {
		if( other.hasNonEmptyIntersection( subset ) ) return true;
	    } else {
		if( subset.hasNonEmptyIntersection( other ) ) return true;
	    }
	}
	return false;
    }
    public boolean addAll( PointsToSet s ) {
	boolean ret = false;
	if( subsets == null ) subsets = new HashSet();
	if( s instanceof Union ) {
	    MemoryEfficientRasUnion meru = (MemoryEfficientRasUnion) s;
	    if( meru.subsets == null || subsets.containsAll( meru.subsets ) ) {
		return false;
	    }
	    return subsets.addAll( meru.subsets );
	} else {
	    Ras r = (Ras) s;
	    return subsets.add( s );
	}
    }
    public Object clone() {
	MemoryEfficientRasUnion ret = new MemoryEfficientRasUnion();
	ret.addAll( this );
	return ret;
    }
    public Set possibleTypes() {
	if( subsets == null ) {
	    return Collections.EMPTY_SET;
	}
	HashSet ret = new HashSet();
	for( Iterator it = subsets.iterator(); it.hasNext(); ) {
	    PointsToSet subset = (PointsToSet) it.next();
	    ret.addAll( subset.possibleTypes() );
	}
	return ret;
    }
    static int count = 0;
    public MemoryEfficientRasUnion() {
	count++;
	if( ( count % 1000 ) == 0 ) System.out.println( "Made "+count+"th MemoryEffcientRasunion" );
    }
}
