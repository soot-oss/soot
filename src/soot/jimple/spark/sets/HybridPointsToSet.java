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
import soot.jimple.spark.internal.*;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import java.util.*;
import soot.Type;

/** Hybrid implementation of points-to set, which uses an explicit array for
 * small sets, and a bit vector for large sets.
 * @author Ondrej Lhotak
 */
public final class HybridPointsToSet extends PointsToSetInternal {
    public HybridPointsToSet( Type type, PAG pag ) {
        super( type );
        this.pag = pag;
        if( nodes == null ) {
            SIZE = pag.getNumAllocNodes()/64+2;
            nodes = new Node[ SIZE * 64 ];
        }
    }
    /** Returns true if this set contains no run-time objects. */
    public final boolean isEmpty() {
        return empty;
    }
    /** Adds contents of other into this set, returns true if this set 
     * changed. */
    public final boolean addAll( final PointsToSetInternal other,
            final PointsToSetInternal exclude ) {
        boolean ret = false;
        long[] mask = null;
        TypeManager typeManager = pag.getTypeManager();
        if( !typeManager.castNeverFails( other.getType(), this.getType() ) ) {
            mask = (long[]) typeManager.get( this.getType() );
        }
        if( other instanceof HybridPointsToSet ) {
            HybridPointsToSet o = (HybridPointsToSet) other;
            if( o.bits != null ) {
                convertToBits();
                if( exclude == null || exclude.isEmpty() ) {
                    if( mask == null ) {
                        for( int i=0; i < SIZE; i++ ) {
                            long l = o.bits[i] & ~bits[i];
                            if( l != 0L ) {
                                ret = true;
                                empty = false;
                                bits[i] |= l;
                            }
                        }
                    } else {
                        for( int i=0; i < SIZE; i++ ) {
                            long l = o.bits[i] & ~bits[i] & mask[i];
                            if( l != 0L ) {
                                ret = true;
                                empty = false;
                                bits[i] |= l;
                            }
                        }
                    }
                    return ret;
                } else if( exclude instanceof HybridPointsToSet
                        && ((HybridPointsToSet)exclude).bits != null ) {
                    HybridPointsToSet e = (HybridPointsToSet) exclude;
                    if( mask == null ) {
                        for( int i=0; i < SIZE; i++ ) {
                            long l = o.bits[i] & ~bits[i] & ~e.bits[i];
                            if( l != 0L ) {
                                ret = true;
                                empty = false;
                                bits[i] |= l;
                            }
                        }
                    } else {
                        for( int i=0; i < SIZE; i++ ) {
                            long l = o.bits[i] & ~bits[i] & ~e.bits[i] & mask[i];
                            if( l != 0L ) {
                                ret = true;
                                empty = false;
                                bits[i] |= l;
                            }
                        }
                    }
                    return ret;
                } else {
                    if( mask == null ) {
                        for( int i=0; i < SIZE; i++ ) {
                            long l = o.bits[i] & ~bits[i];
                            if( l != 0L ) {
                                for( int j=0; j<64; j++ ) {
                                    if( ( l & (1L<<j) ) != 0  ) {
                                        Node n = nodes[i*64+j];
                                        if( exclude.contains( n ) ) {
                                            l &= ~(1L<<j);
                                        }
                                    }
                                }
                                if( l != 0L ) {
                                    ret = true;
                                    empty = false;
                                    bits[i] |= l;
                                }
                            }
                        }
                    } else {
                        for( int i=0; i < SIZE; i++ ) {
                            long l = o.bits[i] & ~bits[i] & mask[i];
                            if( l != 0L ) {
                                for( int j=0; j<64; j++ ) {
                                    if( ( l & (1L<<j) ) != 0  ) {
                                        Node n = nodes[i*64+j];
                                        if( exclude.contains( n ) ) {
                                            l &= ~(1L<<j);
                                        }
                                    }
                                }
                                if( l != 0L ) {
                                    ret = true;
                                    empty = false;
                                    bits[i] |= l;
                                }
                            }
                        }
                    }
                    return ret;
                }
            } else {
                if( o.n1 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n1 ) ) {
                    ret = add( o.n1 ) | ret;
                }
                if( o.n2 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n2 ) ) {
                    ret = add( o.n2 ) | ret;
                }
                if( o.n3 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n3 ) ) {
                    ret = add( o.n3 ) | ret;
                }
                if( o.n4 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n4 ) ) {
                    ret = add( o.n4 ) | ret;
                }
                if( o.n5 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n5 ) ) {
                    ret = add( o.n5 ) | ret;
                }
                if( o.n6 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n6 ) ) {
                    ret = add( o.n6 ) | ret;
                }
                if( o.n7 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n7 ) ) {
                    ret = add( o.n7 ) | ret;
                }
                if( o.n8 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n8 ) ) {
                    ret = add( o.n8 ) | ret;
                }
                if( o.n9 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n9 ) ) {
                    ret = add( o.n9 ) | ret;
                }
                if( o.n10 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n10 ) ) {
                    ret = add( o.n10 ) | ret;
                }
                if( o.n11 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n11 ) ) {
                    ret = add( o.n11 ) | ret;
                }
                if( o.n12 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n12 ) ) {
                    ret = add( o.n12 ) | ret;
                }
                if( o.n13 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n13 ) ) {
                    ret = add( o.n13 ) | ret;
                }
                if( o.n14 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n14 ) ) {
                    ret = add( o.n14 ) | ret;
                }
                if( o.n15 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n15 ) ) {
                    ret = add( o.n15 ) | ret;
                }
                if( o.n16 == null ) return ret;
                if( exclude == null || !exclude.contains( o.n16 ) ) {
                    ret = add( o.n16 ) | ret;
                }
                return ret;
            }
        }
        return super.addAll( other, exclude );
    }
    /** Calls v's visit method on all nodes in this set. */
    public final boolean forall( P2SetVisitor v ) {
       if( bits == null ) {
            if( n1 == null ) return v.getReturnValue(); v.visit( n1 );
            if( n2 == null ) return v.getReturnValue(); v.visit( n2 );
            if( n3 == null ) return v.getReturnValue(); v.visit( n3 );
            if( n4 == null ) return v.getReturnValue(); v.visit( n4 );
            if( n5 == null ) return v.getReturnValue(); v.visit( n5 );
            if( n6 == null ) return v.getReturnValue(); v.visit( n6 );
            if( n7 == null ) return v.getReturnValue(); v.visit( n7 );
            if( n8 == null ) return v.getReturnValue(); v.visit( n8 );
            if( n9 == null ) return v.getReturnValue(); v.visit( n9 );
            if( n10 == null ) return v.getReturnValue(); v.visit( n10 );
            if( n11 == null ) return v.getReturnValue(); v.visit( n11 );
            if( n12 == null ) return v.getReturnValue(); v.visit( n12 );
            if( n13 == null ) return v.getReturnValue(); v.visit( n13 );
            if( n14 == null ) return v.getReturnValue(); v.visit( n14 );
            if( n15 == null ) return v.getReturnValue(); v.visit( n15 );
            if( n16 == null ) return v.getReturnValue(); v.visit( n16 );
        } else {
            for( int i=0; i < SIZE; i++ ) {
                long bitsi = bits[i];
                if( bitsi != 0 ) for( int j=0; j<64; j++ ) {
                    if( ( bitsi & (1L<<j) ) != 0  ) {
                        v.visit( nodes[i*64+j] );
                    }
                }
            }
        }
        return v.getReturnValue();
    }
    /** Adds n to this set, returns true if n was not already in this set. */
    public final boolean add( Node n, long[] mask ) {
        if( mask != null ) {
            int id = -n.getId();
            if( ( mask[id/64] & (id%64) ) == 0 ) return false;
        }
        return fastAdd( n );
    }
    /** Adds n to this set, returns true if n was not already in this set. */
    public final boolean add( Node n ) {
        if( pag.getTypeManager().castNeverFails( n.getType(), type ) ) {
            return fastAdd( n );
        }
        return false;
    }
    /** Returns true iff the set contains n. */
    public final boolean contains( Node n ) {
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
            return ( bits[ id/64 ] & 1L<<(id%64 ) ) != 0;
        }
    }
    public final static P2SetFactory getFactory() {
        return new P2SetFactory() {
            public final PointsToSetInternal newSet( Type type, PAG pag ) {
                return new HybridPointsToSet( type, pag );
            }
        };
    }

    public final static void delete() {
        nodes = null;
    }

    /* End of public methods. */
    /* End of package methods. */

    protected final boolean fastAdd( Node n ) {
        if( bits == null ) {
            if( n1 == null ) { empty = false; n1 = n; return true; } if( n1 == n ) return false;
            if( n2 == null ) { empty = false; n2 = n; return true; } if( n2 == n ) return false;
            if( n3 == null ) { empty = false; n3 = n; return true; } if( n3 == n ) return false;
            if( n4 == null ) { empty = false; n4 = n; return true; } if( n4 == n ) return false;
            if( n5 == null ) { empty = false; n5 = n; return true; } if( n5 == n ) return false;
            if( n6 == null ) { empty = false; n6 = n; return true; } if( n6 == n ) return false;
            if( n7 == null ) { empty = false; n7 = n; return true; } if( n7 == n ) return false;
            if( n8 == null ) { empty = false; n8 = n; return true; } if( n8 == n ) return false;
            if( n9 == null ) { empty = false; n9 = n; return true; } if( n9 == n ) return false;
            if( n10 == null ) { empty = false; n10 = n; return true; } if( n10 == n ) return false;
            if( n11 == null ) { empty = false; n11 = n; return true; } if( n11 == n ) return false;
            if( n12 == null ) { empty = false; n12 = n; return true; } if( n12 == n ) return false;
            if( n13 == null ) { empty = false; n13 = n; return true; } if( n13 == n ) return false;
            if( n14 == null ) { empty = false; n14 = n; return true; } if( n14 == n ) return false;
            if( n15 == null ) { empty = false; n15 = n; return true; } if( n15 == n ) return false;
            if( n16 == null ) { empty = false; n16 = n; return true; } if( n16 == n ) return false;
            convertToBits();
        }
        int id = -n.getId();
        if( nodes[id] == null ) nodes[id] = n;
        if( ( bits[id/64] & 1L<<(id%64) ) != 0 ) return false;
        bits[id/64] |= 1L<<(id%64);
        empty = false;
        return true;
    }

    protected final void convertToBits() {
        if( bits != null ) return;
        bits = new long[SIZE];
        if( n1 != null ) fastAdd( n1 );
        if( n2 != null ) fastAdd( n2 );
        if( n3 != null ) fastAdd( n3 );
        if( n4 != null ) fastAdd( n4 );
        if( n5 != null ) fastAdd( n5 );
        if( n6 != null ) fastAdd( n6 );
        if( n7 != null ) fastAdd( n7 );
        if( n8 != null ) fastAdd( n8 );
        if( n9 != null ) fastAdd( n9 );
        if( n10 != null ) fastAdd( n10 );
        if( n11 != null ) fastAdd( n11 );
        if( n12 != null ) fastAdd( n12 );
        if( n13 != null ) fastAdd( n13 );
        if( n14 != null ) fastAdd( n14 );
        if( n15 != null ) fastAdd( n15 );
        if( n16 != null ) fastAdd( n16 );
    }

    private Node n1 = null;
    private Node n2 = null;
    private Node n3 = null;
    private Node n4 = null;
    private Node n5 = null;
    private Node n6 = null; 
    private Node n7 = null; 
    private Node n8 = null; 
    private Node n9 = null; 
    private Node n10 = null;
    private Node n11 = null;
    private Node n12 = null;
    private Node n13 = null;
    private Node n14 = null;
    private Node n15 = null;
    private Node n16 = null; 
    private static int SIZE = 0;
    private static Node[] nodes = null;
    private long[] bits = null;
    private PAG pag;
    private boolean empty = true;
}

