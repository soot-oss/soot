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
import soot.jimple.spark.internal.*;
import soot.util.*;
import soot.Type;

/** Implementation of points-to set using a sorted array.
 * @author Ondrej Lhotak
 */
public final class SortedArraySet extends PointsToSetInternal {
    public SortedArraySet( Type type, PAG pag ) {
        super( type );
        this.pag = pag;
    }
    /** Returns true if this set contains no run-time objects. */
    public final boolean isEmpty() {
        return size == 0;
    }
    /** Adds contents of other into this set, returns true if this set 
     * changed. */
    public final boolean addAll( final PointsToSetInternal other,
            final PointsToSetInternal exclude ) {
        boolean ret = false;
        BitVector typeMask = ((TypeManager)pag.getTypeManager()).get( type );
        if( other instanceof SortedArraySet ) {
            SortedArraySet o = (SortedArraySet) other;
            Node[] mya = nodes;
            Node[] oa = o.nodes;
            int osize = o.size;
            Node[] newa = new Node[ size + osize ];
            int myi = 0;
            int oi = 0;
            int newi = 0;
            for( ;; ) {
                if( myi < size ) {
                    if( oi < osize ) {
                        int myhc = mya[myi].getNumber();
                        int ohc = oa[oi].getNumber();
                        if( myhc < ohc ) {
                            newa[ newi++ ] = mya[ myi++ ];
                        } else if( myhc > ohc ) {
                            if( ( type == null || typeMask == null ||
                                        typeMask.get(ohc) )
                            && ( exclude == null || !exclude.contains( oa[oi] ) ) ) {
                                newa[ newi++ ] = oa[ oi ];
                                ret = true;
                            }
                            oi++;
                        } else {
                            newa[ newi++ ] = mya[ myi++ ];
                            oi++;
                        }
                    } else { // oi >= osize
                        newa[ newi++ ] = mya[ myi++ ];
                    }
                } else { // myi >= size
                    if( oi < osize ) {
                        int ohc = oa[oi].getNumber();
                        if( ( type == null || typeMask == null ||
                                    typeMask.get(ohc) )
                        && ( exclude == null || !exclude.contains( oa[oi] ) ) ) {
                            newa[ newi++ ] = oa[ oi ];
                            ret = true;
                        }
                        oi++;
                    } else {
                        break;
                    }
                }
            }
            nodes = newa;
            size = newi;
            return ret;
        }
        return super.addAll( other, exclude );
    }
    /** Calls v's visit method on all nodes in this set. */
    public final boolean forall( P2SetVisitor v ) {
        for( int i = 0; i < size; i++ ) {
            v.visit( nodes[i] );
        }
        return v.getReturnValue();
    }
    /** Adds n to this set, returns true if n was not already in this set. */
    public final boolean add( Node n ) {
        if( pag.getTypeManager().castNeverFails( n.getType(), type ) ) {
            if( contains(n) ) return false;
            int left = 0;
            int right = size;
            int mid;
            int hc = n.getNumber();
            while( left < right ) {
                mid = (left + right)/2;
                int midhc = nodes[mid].getNumber();
                if( midhc < hc ) {
                    left = mid+1;
                } else if( midhc > hc ) {
                    right = mid;
                } else break;
            }
            if( nodes == null ) {
                nodes = new Node[size+4];
            } else if( size == nodes.length ) {
                Node[] newNodes = new Node[size+4];
                System.arraycopy( nodes, 0, newNodes, 0, nodes.length );
                nodes = newNodes;
            }
            System.arraycopy( nodes, left, nodes, left+1, size-left );
            nodes[left] = n;
            size++;
            return true;
        }
        return false;
    }
    /** Returns true iff the set contains n. */
    public final boolean contains( Node n ) {
        int left = 0;
        int right = size;
        int hc = n.getNumber();
        while( left < right ) {
            int mid = (left + right)/2;
            int midhc = nodes[mid].getNumber();
            if( midhc < hc ) {
                left = mid+1;
            } else if( midhc > hc ) {
                right = mid;
            } else return true;
        }
        return false;
    }
    public final static P2SetFactory getFactory() {
        return new P2SetFactory() {
            public final PointsToSetInternal newSet( Type type, PAG pag ) {
                return new SortedArraySet( type, pag );
            }
        };
    }

    /* End of public methods. */
    /* End of package methods. */

    private Node[] nodes = null;
    private int size = 0;
    private PAG pag = null;
}

