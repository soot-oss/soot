package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.*;

public class RasMap extends HashMap
{ 
    int count = 0;
    int varnodecount = 0;
    protected Ras findOrAddRas( VarNode key ) {
	Ras r = (Ras) get( key );
	if( r == null ) {
	    r = Ras.factory.newRas( key.getType() );
	    put( key, r );
	    varnodecount++;
	    if( varnodecount % 1000 == 0 ) {
		System.out.println( "Created "+varnodecount+"th varnode ras" );
	    }
	}
	return r;
    }
    protected Ras findOrAddRas( SiteDotField key ) {
	Ras r = (Ras) get( key );
	if( r == null ) {
	    r = Ras.factory.newRas( key.getType() );
	    put( key, r );
	    count++;
	    if( count % 1000 == 0 ) {
		System.out.println( "Created "+count+"th adotf ras" );
		AllocNode n = key.o1;
		System.out.println( n.getSootClass() );
		System.out.println( key.o2 );
	    }
	}
	return r;
    }
    public boolean put( SiteDotField key, AllocNode n ) {
	Ras r = findOrAddRas( key );
	return r.add( n );
    }
    public boolean put( VarNode key, AllocNode n ) {
	Ras r = findOrAddRas( key );
	return r.add( n );
    }
    public boolean putAll( SiteDotField key, Ras n ) {
	Ras r = findOrAddRas( key );
	return r.addAll( n );
    }
    public boolean putAll( VarNode key, Ras n ) {
	Ras r = findOrAddRas( key );
	return r.addAll( n );
    }
    public Ras lookup( SiteDotField key ) {
	return findOrAddRas( key );
    }
    public Ras lookup( VarNode key ) {
	return findOrAddRas( key );
    }
    public void nextIter() {
    }
}


