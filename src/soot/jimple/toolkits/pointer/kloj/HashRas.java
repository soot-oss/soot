package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.*;

public class HashRas extends Ras
{ 
    Set s = new HashSet(4);
    public boolean isEmpty() {
	return s.isEmpty();
    }
    public boolean contains( AllocNode n ) {
	return s.contains( n );
    }
    public void forall( RasVisitor v ) {
	for( Iterator nIt = new LinkedList( s ).iterator();
		nIt.hasNext(); ) {
	    AllocNode n = (AllocNode) nIt.next();

	    v.visit( n );
	}
    }
    public boolean add( AllocNode n ) {
	if( fh == null || type == null || fh.canStoreType( n.getType(), type ) ) {
	    return s.add( n );
	} else return false;
    }
    public boolean fastAdd( AllocNode n ) {
	return s.add( n );
    }
    public int size() {
	return s.size();
    }
    public boolean rasHasNonEmptyIntersection( Ras other ) {
	if( size() < other.size() ) return other.rasHasNonEmptyIntersection( this );
	HashRas o = (HashRas) other;
	for( Iterator it = o.s.iterator(); it.hasNext(); ) {
	    if( s.contains( it.next() ) ) return true;
	}
	return false;
    }
    public Set possibleTypes() {
	HashSet ret = new HashSet();
	for( Iterator nIt = new LinkedList( s ).iterator();
		nIt.hasNext(); ) {
	    AllocNode n = (AllocNode) nIt.next();

	    ret.add( n.getType() );
	}
	return ret;
    }
    public HashRas( Type t ) {
	super(t);
    }
}

