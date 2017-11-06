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

    private boolean superAddAll( PointsToSetInternal other, PointsToSetInternal exclude ) {
        boolean ret = super.addAll( other, exclude );
        if( ret ) empty = false;
        return ret;
    }

    private boolean nativeAddAll( HybridPointsToSet other, HybridPointsToSet exclude ) {
        boolean ret = false;
        TypeManager typeManager = pag.getTypeManager();
        if( other.bits != null ) {
            convertToBits();
            if( exclude != null ) {
                exclude.convertToBits();
            }
            BitVector mask = null;
            if( !typeManager.castNeverFails( other.getType(), this.getType() ) ) {
                mask = typeManager.get( this.getType() );
            }

            BitVector ebits = ( exclude==null ? null : exclude.bits );
            ret = bits.orAndAndNot( other.bits, mask, ebits );
        } else {
           	for (int i = 0; i < nodes.length; i++) {
           		if( other.nodes[i] == null ) break;
                	if( exclude == null || !exclude.contains( other.nodes[i] ) ) {
                		ret = add( other.nodes[i] ) | ret;
                	}
           	}
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
            for (Node node : nodes) {
                if (node == null)
                    return v.getReturnValue();
                v.visit(node);
            }
        } else {
            for( BitSetIterator it = bits.iterator(); it.hasNext(); ) {
                v.visit(pag.getAllocNodeNumberer().get( it.next() ) );
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
            for (Node node : nodes) {
                if (node == n)
                    return true;
                if (node == null) {
                    break;
                }
            }
            return false;
        } else {
            return bits.get( n.getNumber() );
        }
    }
    public static P2SetFactory getFactory() {
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
    		for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] == null) {
                    empty = false;
                    nodes[i] = n;
                    return true;
                } else if (nodes[i] == n) {
                    return false;
                }
            }
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
        for (Node node : nodes) {
            if (node != null) {
                fastAdd(node);
            }
        }
    }

//	public static int numBitVectors = 0;
    private Node[] nodes = new Node[16];
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

