/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.spark.sets;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import java.util.*;
import soot.Type;

/** Implementation of points-to set using a bit vector.
 * @author Ondrej Lhotak
 */
public class BitPointsToSet extends PointsToSetInternal {
    public BitPointsToSet( Type type, PAG pag ) {
        super( type );
        if( nodes == null ) {
            SIZE = pag.getNumAllocNodes()/64+2;
            nodes = new Node[ SIZE * 64 ];
        }
        bits = new long[ SIZE ];
    }
    /** Returns true if this set contains no run-time objects. */
    public boolean isEmpty() {
        return empty;
    }
    /** Adds contents of other into this set, returns true if this set 
     * changed. */
    public boolean addAll( final PointsToSetInternal other,
            final PointsToSetInternal exclude ) {
        boolean ret = false;
        if( other instanceof BitPointsToSet ) {
            BitPointsToSet o = (BitPointsToSet) other;
            if( exclude == null ) {
                for( int i=0; i < SIZE; i++ ) {
                    long l = o.bits[i] & ~bits[i];
                    if( l != 0 ) for( int j=0; j<64; j++ ) {
                        if( ( l & (1L<<j) ) != 0  ) {
                            ret = add( nodes[i*64+j] ) | ret;
                        }
                    }
                }
                return ret;
            } else if( exclude instanceof BitPointsToSet ) {
                BitPointsToSet e = (BitPointsToSet) exclude;
                for( int i=0; i < SIZE; i++ ) {
                    long l = o.bits[i] & ~bits[i] & ~e.bits[i];
                    if( l != 0 ) for( int j=0; j<64; j++ ) {
                        if( ( l & (1L<<j) ) != 0  ) {
                            ret = add( nodes[i*64+j] ) | ret;
                        }
                    }
                }
                return ret;
            } else {
                for( int i=0; i < SIZE; i++ ) {
                    long l = o.bits[i] & ~bits[i];
                    if( l != 0 ) for( int j=0; j<64; j++ ) {
                        if( ( l & (1L<<j) ) != 0  ) {
                            Node n = nodes[i*64+j];
                            if( !exclude.contains( n ) ) {
                                ret = add( n ) | ret;
                            }
                        }
                    }
                }
                return ret;
            }
        }
        return super.addAll( other, exclude );
    }
    /** Calls v's visit method on all nodes in this set. */
    public boolean forall( P2SetVisitor v ) {
        for( int i=0; i < SIZE; i++ ) {
            long bitsi = bits[i];
            if( bitsi != 0 ) for( int j=0; j<64; j++ ) {
                if( ( bitsi & (1L<<j) ) != 0  ) {
                    v.visit( nodes[i*64+j] );
                }
            }
        }
        boolean ret = v.getReturnValue();
        if( ret ) empty = false;
        return ret;
    }
    /** Adds n to this set, returns true if n was not already in this set. */
    public boolean add( Node n ) {
        if( fh == null || type == null ||
            fh.canStoreType( n.getType(), type ) ) {

            return fastAdd( n );
        }
        return false;
    }
    /** Returns true iff the set contains n. */
    public boolean contains( Node n ) {
        int id = -n.getId();
        return ( bits[ id/64 ] & 1L<<(id%64 ) ) != 0;
    }
    public static P2SetFactory getFactory() {
        return new P2SetFactory() {
            public PointsToSetInternal newSet( Type type, PAG pag ) {
                return new BitPointsToSet( type, pag );
            }
        };
    }

    /* End of public methods. */
    /* End of package methods. */

    protected boolean fastAdd( Node n ) {
        int id = -n.getId();
        if( nodes[id] == null ) nodes[id] = n;
        if( ( bits[id/64] & 1L<<(id%64) ) != 0 ) return false;
        bits[id/64] |= 1L<<(id%64);
        empty = false;
        return true;
    }

    private static int SIZE = 0;
    private static Node[] nodes = null;
    private long[] bits = null;
    private boolean empty = true;
}

