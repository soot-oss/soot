package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.*;

public class ColourRas extends Ras
{ 
    HybridRas black;
    HybridRas gray;
    HybridRas white;

    public boolean isEmpty() {
	return black.isEmpty() && gray.isEmpty() && white.isEmpty();
    }
    public boolean contains( AllocNode n ) {
	return white.contains(n) || gray.contains(n) || black.contains(n);
    }
    public void forall( RasVisitor v ) {
	black.forall( v );
	gray.forall( v );
	white.forall( v );
    }
    public boolean add( AllocNode n ) {
	if( black.contains(n) ) return false;
	if( gray.contains(n) ) return false;
	return white.add(n);
    }
    public int size() {
	return black.size() + gray.size() + white.size();
    }
    public ColourRas( Type t ) {
	super(t);
	black = new HybridRas( t );
	gray = new HybridRas( t );
	white = new HybridRas( t );
    }
    public void nextIter() {
	black.fastAddAll( gray );
	gray = white;
	white = new HybridRas( type );
    }
    public Ras getNewSites() {
	return gray;
    }
    public boolean rasHasNonEmptyIntersection( Ras other ) {
	ColourRas o = (ColourRas) other;
	if( !o.gray.isEmpty() || !o.white.isEmpty() ||
	    !gray.isEmpty() || !white.isEmpty() ) {
	    throw new RuntimeException( "White or Gray sets not empty" );
	}
	return black.rasHasNonEmptyIntersection( o.black );
    }
    public Set possibleTypes() {
	if( !gray.isEmpty() || !white.isEmpty() ) {
	    throw new RuntimeException( "White or Gray sets not empty" );
	}
	return black.possibleTypes();
    }
}


