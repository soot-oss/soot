package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.*;

public class HybridRas extends Ras
{ 
    AllocNode n1 = null;
    AllocNode n2 = null;
    AllocNode n3 = null;
    AllocNode n4 = null;
    AllocNode n5 = null;
    AllocNode n6 = null;
    AllocNode n7 = null;
    AllocNode n8 = null;
    AllocNode n9 = null;
    AllocNode n10 = null;
    AllocNode n11 = null;
    AllocNode n12 = null;
    AllocNode n13 = null;
    AllocNode n14 = null;
    AllocNode n15 = null;
    AllocNode n16 = null;
    static int SIZE = 0;
    static AllocNode[] nodes = null;
    int[] bits = null;

    public boolean isEmpty() {
	return bits == null && n1 == null;
    }
    public boolean contains( AllocNode n ) {
	if( bits == null ) {
	    if( n1 == n ) return true;
	    if( n2 == n ) return true;
	    if( n3 == n ) return true;
	    if( n4 == n ) return true;
	    if( n5 == n ) return true;
	    if( n6 == n ) return true;
	    if( n7 == n ) return true;
	    if( n8 == n ) return true;
	    if( n9 == n ) return true;
	    if( n10 == n ) return true;
	    if( n11 == n ) return true;
	    if( n12 == n ) return true;
	    if( n13 == n ) return true;
	    if( n14 == n ) return true;
	    if( n15 == n ) return true;
	    if( n16 == n ) return true;
	    return false;
	} else {
	    int id = -n.getId();
	    return ( bits[ id/32 ] & 1<<(id%32 ) ) != 0;
	}
    }
    public void forall( RasVisitor v ) {
	if( bits == null ) {
	    if( n1 == null ) return; v.visit( n1 );
	    if( n2 == null ) return; v.visit( n2 );
	    if( n3 == null ) return; v.visit( n3 );
	    if( n4 == null ) return; v.visit( n4 );
	    if( n5 == null ) return; v.visit( n5 );
	    if( n6 == null ) return; v.visit( n6 );
	    if( n7 == null ) return; v.visit( n7 );
	    if( n8 == null ) return; v.visit( n8 );
	    if( n9 == null ) return; v.visit( n9 );
	    if( n10 == null ) return; v.visit( n10 );
	    if( n11 == null ) return; v.visit( n11 );
	    if( n12 == null ) return; v.visit( n12 );
	    if( n13 == null ) return; v.visit( n13 );
	    if( n14 == null ) return; v.visit( n14 );
	    if( n15 == null ) return; v.visit( n15 );
	    if( n16 == null ) return; v.visit( n16 );
	} else {
	    for( int i=0; i < SIZE; i++ ) {
		if( bits[i] != 0 ) for( int j=0; j<32; j++ ) {
		    if( ( bits[i] & (1<<j) ) != 0  ) {
			v.visit( nodes[i*32+j] );
		    }
		}
	    }
	}
    }
    public boolean add( AllocNode n ) {
	if( fh != null && type != null &&
	    !fh.canStoreType( n.getType(), type ) ) return false;
	return fastAdd( n );
    }
    public boolean fastAdd( AllocNode n ) {
	if( bits == null ) {
	    if( n1 == null ) { n1 = n; return true; } if( n1 == n ) return false;
	    if( n2 == null ) { n2 = n; return true; } if( n2 == n ) return false;
	    if( n3 == null ) { n3 = n; return true; } if( n3 == n ) return false;
	    if( n4 == null ) { n4 = n; return true; } if( n4 == n ) return false;
	    if( n5 == null ) { n5 = n; return true; } if( n5 == n ) return false;
	    if( n6 == null ) { n6 = n; return true; } if( n6 == n ) return false;
	    if( n7 == null ) { n7 = n; return true; } if( n7 == n ) return false;
	    if( n8 == null ) { n8 = n; return true; } if( n8 == n ) return false;
	    if( n9 == null ) { n9 = n; return true; } if( n9 == n ) return false;
	    if( n10 == null ) { n10 = n; return true; } if( n10 == n ) return false;
	    if( n11 == null ) { n11 = n; return true; } if( n11 == n ) return false;
	    if( n12 == null ) { n12 = n; return true; } if( n12 == n ) return false;
	    if( n13 == null ) { n13 = n; return true; } if( n13 == n ) return false;
	    if( n14 == null ) { n14 = n; return true; } if( n14 == n ) return false;
	    if( n15 == null ) { n15 = n; return true; } if( n15 == n ) return false;
	    if( n16 == null ) { n16 = n; return true; } if( n16 == n ) return false;
	    bits = new int[SIZE];
	    fastAdd( n1 );
	    fastAdd( n2 );
	    fastAdd( n3 );
	    fastAdd( n4 );
	    fastAdd( n5 );
	    fastAdd( n6 );
	    fastAdd( n7 );
	    fastAdd( n8 );
	    fastAdd( n9 );
	    fastAdd( n10 );
	    fastAdd( n11 );
	    fastAdd( n12 );
	    fastAdd( n13 );
	    fastAdd( n14 );
	    fastAdd( n15 );
	    fastAdd( n16 );
	}
	int id = -n.getId();
	if( nodes[id] == null ) nodes[id] = n;
	if( ( bits[id/32] & 1<<(id%32) ) != 0 ) return false;
	bits[id/32] |= 1<<(id%32);
	return true;
    }
    public boolean fastAddAll( Ras r ) {
	HybridRas hr = (HybridRas) r;
	boolean ret = false;
	if( hr.bits == null ) {
	    if( hr.n1 == null ) return ret; ret = ret | fastAdd( hr.n1 );
	    if( hr.n2 == null ) return ret; ret = ret | fastAdd( hr.n2 );
	    if( hr.n3 == null ) return ret; ret = ret | fastAdd( hr.n3 );
	    if( hr.n4 == null ) return ret; ret = ret | fastAdd( hr.n4 );
	    if( hr.n5 == null ) return ret; ret = ret | fastAdd( hr.n5 );
	    if( hr.n6 == null ) return ret; ret = ret | fastAdd( hr.n6 );
	    if( hr.n7 == null ) return ret; ret = ret | fastAdd( hr.n7 );
	    if( hr.n8 == null ) return ret; ret = ret | fastAdd( hr.n8 );
	    if( hr.n9 == null ) return ret; ret = ret | fastAdd( hr.n9 );
	    if( hr.n10 == null ) return ret; ret = ret | fastAdd( hr.n10 );
	    if( hr.n11 == null ) return ret; ret = ret | fastAdd( hr.n11 );
	    if( hr.n12 == null ) return ret; ret = ret | fastAdd( hr.n12 );
	    if( hr.n13 == null ) return ret; ret = ret | fastAdd( hr.n13 );
	    if( hr.n14 == null ) return ret; ret = ret | fastAdd( hr.n14 );
	    if( hr.n15 == null ) return ret; ret = ret | fastAdd( hr.n15 );
	    if( hr.n16 == null ) return ret; ret = ret | fastAdd( hr.n16 );
	} else {
	    if( bits == null ) {
		for( int i=0; i < SIZE; i++ ) {
		    if( bits == null ) {
			if( hr.bits[i] != 0 ) for( int j=0; j<32; j++ ) {
			    if( ( hr.bits[i] & (1<<j) ) != 0 ) ret = ret | fastAdd( nodes[i*32+j] );
			}
		    } else {
			ret = ret || ( (bits[i] | hr.bits[i]) != bits[i] );
			bits[i] |= hr.bits[i];
		    }
		}
	    } else {
		for( int i=0; i < SIZE; i++ ) {
		    ret = ret || ( (bits[i] | hr.bits[i]) != bits[i] );
		    bits[i] |= hr.bits[i];
		}
	    }
	}
	return ret;
    }
    public boolean addAll( Ras r ) {
	HybridRas hr = (HybridRas) r;
	boolean ret = false;
	if( hr.bits == null ) {
	    if( hr.n1 == null ) return ret; ret = ret | add( hr.n1 );
	    if( hr.n2 == null ) return ret; ret = ret | add( hr.n2 );
	    if( hr.n3 == null ) return ret; ret = ret | add( hr.n3 );
	    if( hr.n4 == null ) return ret; ret = ret | add( hr.n4 );
	    if( hr.n5 == null ) return ret; ret = ret | add( hr.n5 );
	    if( hr.n6 == null ) return ret; ret = ret | add( hr.n6 );
	    if( hr.n7 == null ) return ret; ret = ret | add( hr.n7 );
	    if( hr.n8 == null ) return ret; ret = ret | add( hr.n8 );
	    if( hr.n9 == null ) return ret; ret = ret | add( hr.n9 );
	    if( hr.n10 == null ) return ret; ret = ret | add( hr.n10 );
	    if( hr.n11 == null ) return ret; ret = ret | add( hr.n11 );
	    if( hr.n12 == null ) return ret; ret = ret | add( hr.n12 );
	    if( hr.n13 == null ) return ret; ret = ret | add( hr.n13 );
	    if( hr.n14 == null ) return ret; ret = ret | add( hr.n14 );
	    if( hr.n15 == null ) return ret; ret = ret | add( hr.n15 );
	    if( hr.n16 == null ) return ret; ret = ret | add( hr.n16 );
	} else {
	    for( int i=0; i < SIZE; i++ ) {
		if( hr.bits[i] != 0 ) for( int j=0; j<32; j++ ) {
		    if( ( hr.bits[i] & (1<<j) ) != 0 ) ret = ret | add( nodes[i*32+j] );
		}
	    }
	}
	return ret;
    }
    public int size() {
	if( bits == null ) {
	    if( n1 == null ) return 0;
	    if( n2 == null ) return 1;
	    if( n3 == null ) return 2;
	    if( n4 == null ) return 3;
	    if( n5 == null ) return 4;
	    if( n6 == null ) return 5;
	    if( n7 == null ) return 6;
	    if( n8 == null ) return 7;
	    if( n9 == null ) return 8;
	    if( n10 == null ) return 9;
	    if( n11 == null ) return 10;
	    if( n12 == null ) return 11;
	    if( n13 == null ) return 12;
	    if( n14 == null ) return 13;
	    if( n15 == null ) return 14;
	    if( n16 == null ) return 15;
	    return 16;
	} else {
	    int count = 0;
	    if( bits != null) for( int i=0; i<SIZE; i++ ) {
		if( bits[i] != 0 ) for( int j=1; j != 0; j <<= 1 ) {
		    if( ( bits[i] & j ) != 0 ) count++;
		}
	    }
	    return count;
	}
    }
    public boolean rasHasNonEmptyIntersection( Ras other ) {
	HybridRas o = (HybridRas) other;
	if( bits != null && o.bits != null ) {
	    for( int i=0; i < SIZE; i++ ) {
		if( ( o.bits[i] & bits[i] ) != 0 ) return true;
	    }
	}
	if( n1 != null && o.contains( n1 ) ) return true;
	if( n2 != null && o.contains( n2 ) ) return true;
	if( n3 != null && o.contains( n3 ) ) return true;
	if( n4 != null && o.contains( n4 ) ) return true;
	if( n5 != null && o.contains( n5 ) ) return true;
	if( n6 != null && o.contains( n6 ) ) return true;
	if( n7 != null && o.contains( n7 ) ) return true;
	if( n8 != null && o.contains( n8 ) ) return true;
	if( n9 != null && o.contains( n9 ) ) return true;
	if( n10 != null && o.contains( n10 ) ) return true;
	if( n11 != null && o.contains( n11 ) ) return true;
	if( n12 != null && o.contains( n12 ) ) return true;
	if( n13 != null && o.contains( n13 ) ) return true;
	if( n14 != null && o.contains( n14 ) ) return true;
	if( n15 != null && o.contains( n15 ) ) return true;
	if( n16 != null && o.contains( n16 ) ) return true;
	if( o.n1 != null && contains( o.n1 ) ) return true;
	if( o.n2 != null && contains( o.n2 ) ) return true;
	if( o.n3 != null && contains( o.n3 ) ) return true;
	if( o.n4 != null && contains( o.n4 ) ) return true;
	if( o.n5 != null && contains( o.n5 ) ) return true;
	if( o.n6 != null && contains( o.n6 ) ) return true;
	if( o.n7 != null && contains( o.n7 ) ) return true;
	if( o.n8 != null && contains( o.n8 ) ) return true;
	if( o.n9 != null && contains( o.n9 ) ) return true;
	if( o.n10 != null && contains( o.n10 ) ) return true;
	if( o.n11 != null && contains( o.n11 ) ) return true;
	if( o.n12 != null && contains( o.n12 ) ) return true;
	if( o.n13 != null && contains( o.n13 ) ) return true;
	if( o.n14 != null && contains( o.n14 ) ) return true;
	if( o.n15 != null && contains( o.n15 ) ) return true;
	if( o.n16 != null && contains( o.n16 ) ) return true;
	return false;
    }
    static int count = 0;
    public HybridRas( Type t ) {
	super(t);
	if( nodes == null ) {
	    SIZE = AllocNode.getNumNodes()/32+2;
	    nodes = new AllocNode[ SIZE * 32 ];
	    System.out.println( "There are "+AllocNode.getNumNodes()+" alloc sites" );
	}
	/*
	count++;
	if( (count % 1000) == 0 ) System.out.println( "Made "+count+"th HybridRas" );
	*/
    }
}


