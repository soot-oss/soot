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
import soot.Scene;
import soot.Type;
import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.util.BitSetIterator;
import soot.util.BitVector;

/** Hybrid implementation of points-to set, which uses an explicit array for
 * small sets, and a bit vector for large sets.
 * @author Ondrej Lhotak
 */
public final class HybridPointsToSet extends PointsToSetInternal {
    public HybridPointsToSet( Type type, PAG pag ) {
        super( type );
        this.pag = pag;
    }
    /** Returns true if this set contains no run-time objects. */
    public final boolean isEmpty() {
        return empty;
    }

    private final boolean superAddAll( PointsToSetInternal other, PointsToSetInternal exclude ) {
        boolean ret = super.addAll( other, exclude );
        if( ret ) empty = false;
        return ret;
    }

    private final boolean nativeAddAll( HybridPointsToSet other, HybridPointsToSet exclude ) {
        boolean ret = false;
        BitVector mask = null;
        TypeManager typeManager = pag.getTypeManager();
        if( !typeManager.castNeverFails( other.getType(), this.getType() ) ) {
            mask = typeManager.get( this.getType() );
        }
        if( other.bits != null ) {
            convertToBits();
            if( exclude != null ) {
                exclude.convertToBits();
            }
            BitVector ebits = ( exclude==null ? null : exclude.bits );
            ret = bits.orAndAndNot( other.bits, mask, ebits );
        } else {
            do {
                if( other.n1 == null ) break;
                if( exclude == null || !exclude.contains( other.n1 ) ) {
                    ret = add( other.n1 ) | ret;
                }
                if( other.n2 == null ) break;
                if( exclude == null || !exclude.contains( other.n2 ) ) {
                    ret = add( other.n2 ) | ret;
                }
                if( other.n3 == null ) break;
                if( exclude == null || !exclude.contains( other.n3 ) ) {
                    ret = add( other.n3 ) | ret;
                }
                if( other.n4 == null ) break;
                if( exclude == null || !exclude.contains( other.n4 ) ) {
                    ret = add( other.n4 ) | ret;
                }
                if( other.n5 == null ) break;
                if( exclude == null || !exclude.contains( other.n5 ) ) {
                    ret = add( other.n5 ) | ret;
                }
                if( other.n6 == null ) break;
                if( exclude == null || !exclude.contains( other.n6 ) ) {
                    ret = add( other.n6 ) | ret;
                }
                if( other.n7 == null ) break;
                if( exclude == null || !exclude.contains( other.n7 ) ) {
                    ret = add( other.n7 ) | ret;
                }
                if( other.n8 == null ) break;
                if( exclude == null || !exclude.contains( other.n8 ) ) {
                    ret = add( other.n8 ) | ret;
                }
                if( other.n9 == null ) break;
                if( exclude == null || !exclude.contains( other.n9 ) ) {
                    ret = add( other.n9 ) | ret;
                }
                if( other.n10 == null ) break;
                if( exclude == null || !exclude.contains( other.n10 ) ) {
                    ret = add( other.n10 ) | ret;
                }
                if( other.n11 == null ) break;
                if( exclude == null || !exclude.contains( other.n11 ) ) {
                    ret = add( other.n11 ) | ret;
                }
                if( other.n12 == null ) break;
                if( exclude == null || !exclude.contains( other.n12 ) ) {
                    ret = add( other.n12 ) | ret;
                }
                if( other.n13 == null ) break;
                if( exclude == null || !exclude.contains( other.n13 ) ) {
                    ret = add( other.n13 ) | ret;
                }
                if( other.n14 == null ) break;
                if( exclude == null || !exclude.contains( other.n14 ) ) {
                    ret = add( other.n14 ) | ret;
                }
                if( other.n15 == null ) break;
                if( exclude == null || !exclude.contains( other.n15 ) ) {
                    ret = add( other.n15 ) | ret;
                }
                if( other.n16 == null ) break;
                if( exclude == null || !exclude.contains( other.n16 ) ) {
                    ret = add( other.n16 ) | ret;
                }
            } while( false );
        }
        if( ret ) empty = false;
        return ret;
    }
 
    /** Adds contents of other into this set, returns true if this set 
     * changed. */
    public final boolean addAll( final PointsToSetInternal other,
            final PointsToSetInternal exclude ) {
        if( other != null && !(other instanceof HybridPointsToSet) )
            return superAddAll( other, exclude );
        if( exclude != null && !(exclude instanceof HybridPointsToSet) )
            return superAddAll( other, exclude );
        return nativeAddAll( (HybridPointsToSet) other, (HybridPointsToSet) exclude );
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
            for( BitSetIterator it = bits.iterator(); it.hasNext(); ) {
                v.visit( (Node) pag.getAllocNodeNumberer().get( it.next() ) );
            }
        }
        return v.getReturnValue();
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
            return bits.get( n.getNumber() );
        }
    }
    public final static P2SetFactory getFactory() {
        return new P2SetFactory() {
            public final PointsToSetInternal newSet( Type type, PAG pag ) {
                return new HybridPointsToSet( type, pag );
            }
        };
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
        boolean ret = bits.set( n.getNumber() );
        if( ret ) empty = false;
        return ret;
    }

    protected final void convertToBits() {
        if( bits != null ) return;
//		++numBitVectors;
        bits = new BitVector( pag.getAllocNodeNumberer().size() );
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

//	public static int numBitVectors = 0;
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
    private BitVector bits = null;
    private PAG pag;
    private boolean empty = true;

    public static HybridPointsToSet intersection(final HybridPointsToSet set1,
        final HybridPointsToSet set2, PAG pag) {
    final HybridPointsToSet ret = new HybridPointsToSet(Scene.v().getObjectType(), pag);
    BitVector s1Bits = set1.bits;
    BitVector s2Bits = set2.bits;
    if (s1Bits == null || s2Bits == null) {
        if (s1Bits != null) {
            // set2 is smaller
            set2.forall(new P2SetVisitor() {
                @Override
                public void visit(Node n) {
                    if (set1.contains(n))
                        ret.add(n);
                }
            });                
        } else {
            // set1 smaller, or both small
            set1.forall(new P2SetVisitor() {
                @Override
                public void visit(Node n) {
                    if (set2.contains(n))
                        ret.add(n);
                }
            });                                
        }
    } else {
        // both big; do bit-vector operation
        // potential issue: if intersection is small, might
        // use inefficient bit-vector operations later
        ret.bits = BitVector.and(s1Bits, s2Bits);
        ret.empty = false;
    }
    return ret;
}
    
}

