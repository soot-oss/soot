package soot.jimple.toolkits.pointer.kloj;
import java.util.*;

public class LimitedSet extends HashSet
{ 
    boolean full = false;
    int threshold;
    public LimitedSet( int threshold ) {
	super(4);
	this.threshold = threshold;
    }
    public boolean add(Object o) {
	if( full ) return false;
	else {
	    boolean ret = super.add(o);
	    if( super.size() > threshold ) {
		full = true;
	    }
	    return ret;
	}
    }
    public void clear() {
	full = false;
	super.clear();
    }
    public boolean contains( Object o ) {
	if( full ) return true;
	else return super.contains( o );
    }
    public boolean isEmpty() {
	return !full && super.isEmpty();
    }
    public Iterator iterator() {
	if( full ) {
	    throw new RuntimeException( "No, I won't let you iterate over me!" );
	} else return super.iterator();
    }
    public boolean remove( Object o ) {
	if( full ) return true;
	else return super.remove( o );
    }
    public int size() {
	if( full ) return Integer.MAX_VALUE;
	else return super.size();
    }
    public boolean addAll( Collection c ) {
	if( c instanceof LimitedSet ) {
	    LimitedSet l = (LimitedSet) c;
	    boolean ret = !full;
	    if( l.full || full ) {
		full = true;
		return ret;
	    }
	}
	return super.addAll( c );
    }
}


