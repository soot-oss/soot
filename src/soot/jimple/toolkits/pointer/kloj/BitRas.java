package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.*;

public class BitRas extends Ras
{ 
    static int SIZE = 0;
    static AllocNode[] nodes = null;

    int[] bits = new int[SIZE];
    int[] typeBits;
    boolean empty = true;

    public boolean contains( AllocNode n ) {
	int id = -n.getId();
	return ( bits[ id/32 ] & 1<<(id%32 ) ) != 0;
    }
    public void forall( RasVisitor v ) {
	for( int i = 0; i < SIZE; i++ ) {
	    for( int j = 0; j < 32; j++ ) {
		if( ( bits[i] & (1<<j) ) != 0  ) {
		    v.visit( nodes[i*32+j] );
		}
	    }
	}
    }
    public boolean isEmpty() {
	return empty;
    }
    public boolean fastAdd( AllocNode n ) {
	int id = -n.getId();
	if( contains( n ) ) return false;
	if( nodes[id] == null ) {
	    nodes[id] = n;
	}
	bits[id/32] |= 1<<(id%32) ;
	empty = false;
	return true;
    }
    public boolean add( AllocNode n ) {
	int id = -n.getId();
	if( contains( n ) ) return false;
	if( fh != null && type != null &&
		!fh.canStoreType( n.getType(), type ) ) return false;
	if( nodes[id] == null ) {
	    nodes[id] = n;
	}
	bits[id/32] |= 1<<(id%32) ;
	empty = false;
	return true;
    }
    protected void makeTypeBits() {
	typeBits = new int[SIZE];
	if( fh == null || type == null ) {
	    for( int i = 0; i < SIZE; i++ ) {
		typeBits[i] = 0xffffffff;
	    }
	    return;
	}
	for( int id = 0; id < SIZE*32; id++ ) {
	    AllocNode node = nodes[id];
	    if( node == null ) continue;
	    if( fh.canStoreType( node.getType(), type ) ) {
		typeBits[id/32] |= 1<<(id%32);
	    }
	}
    }
    public boolean fastAddAll( Ras r ) {
	BitRas br = (BitRas) r;
	boolean changed = false;
	for( int i = 0; i < SIZE; i++ ) {
	    int updated = br.bits[i];
	    if( updated == 0 ) continue;
	    updated = updated | bits[i];
	    if( updated != bits[i] ) changed = true;
	    bits[i] = updated;
	}
	if( changed ) empty = false;
	return changed;
    }
    public boolean addAll( Ras r ) {
	if( typeBits == null ) makeTypeBits();
	BitRas br = (BitRas) r;
	boolean changed = false;
	for( int i = 0; i < SIZE; i++ ) {
	    int updated = br.bits[i] & typeBits[i];
	    if( updated == 0 ) continue;
	    updated = updated | bits[i];
	    if( updated != bits[i] ) changed = true;
	    bits[i] = updated;
	}
	if( changed ) empty = false;
	return changed;
    }
    public int size() {
	int count = 0;
	for( int i = 0; i < SIZE; i++ ) {
	    for( int j = 0; j < 32; j++ ) {
		if( ( bits[i] & (1<<j) ) != 0 ) {
		    count++;
		}
	    }
	}
	return count;
    }

    public boolean rasHasNonEmptyIntersection( Ras other ) {
	BitRas o = (BitRas) other;
	for( int i = 0; i < SIZE; i++ ) {
	    if( (bits[i] & o.bits[i]) != 0 ) return true;
	}
	return false;
    }

    public BitRas( Type t ) {
	super(t);
	if( nodes == null ) {
	    SIZE = AllocNode.getNumNodes()/32+2;
	    nodes = new AllocNode[ SIZE * 32 ];
	    System.out.println( "There are "+AllocNode.getNumNodes()+" alloc sites" );
	}
    }
}


