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

package soot.jimple.spark.pag;
import java.util.*;
import soot.jimple.*;
import soot.jimple.spark.*;
import soot.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.solver.OnFlyCallGraph;
import soot.jimple.spark.internal.*;
import soot.util.*;
import soot.util.queue.*;
import soot.options.SparkOptions;
import soot.tagkit.*;

/** Pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class PAG extends AbstractPAG {
    public PAG( final SparkOptions opts ) {
        super( opts );
        typeManager = new TypeManager(this);
        if( !opts.ignore_types() ) {
            typeManager.setFastHierarchy( Scene.v().getOrMakeFastHierarchy() );
        }
        switch( opts.set_impl() ) {
            case SparkOptions.set_impl_hash:
                setFactory = HashPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_hybrid:
                setFactory = HybridPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_shared:
                setFactory = SharedPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_array:
                setFactory = SortedArraySet.getFactory();
                break;
            case SparkOptions.set_impl_bit:
                setFactory = BitPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_double:
                P2SetFactory oldF;
                P2SetFactory newF;
                switch( opts.double_set_old() ) {
                    case SparkOptions.double_set_old_hash:
                        oldF = HashPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_hybrid:
                        oldF = HybridPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_shared:
                        oldF = SharedPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_array:
                        oldF = SortedArraySet.getFactory();
                        break;
                    case SparkOptions.double_set_old_bit:
                        oldF = BitPointsToSet.getFactory();
                        break;
                    default:
                        throw new RuntimeException();
                }
                switch( opts.double_set_new() ) {
                    case SparkOptions.double_set_new_hash:
                        newF = HashPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_hybrid:
                        newF = HybridPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_shared:
                        newF = SharedPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_array:
                        newF = SortedArraySet.getFactory();
                        break;
                    case SparkOptions.double_set_new_bit:
                        newF = BitPointsToSet.getFactory();
                        break;
                    default:
                        throw new RuntimeException();
                }
                setFactory = DoublePointsToSet.getFactory( newF, oldF );
                break;
            default:
                throw new RuntimeException();
        }
    }


    /** Returns the set of objects pointed to by variable l. */
    public PointsToSet reachingObjects( Local l ) {
        VarNode n = findLocalVarNode( l );
        if( n == null ) {
            return EmptyPointsToSet.v();
        }
        return n.getP2Set();
    }

    /** Returns the set of objects pointed to by static field f. */
    public PointsToSet reachingObjects( SootField f ) {
        if( !f.isStatic() )
            throw new RuntimeException( "The parameter f must be a *static* field." );
        VarNode n = findGlobalVarNode( f );
        if( n == null ) {
            return EmptyPointsToSet.v();
        }
        return n.getP2Set();
    }

    /** Returns the set of objects pointed to by instance field f
     * of the objects in the PointsToSet s. */
    public PointsToSet reachingObjects( PointsToSet s, final SootField f ) {
        if( f.isStatic() )
            throw new RuntimeException( "The parameter f must be an *instance* field." );
        if( getOpts().field_based() || getOpts().vta() ) {
            VarNode n = findGlobalVarNode( f );
            if( n == null ) {
                return EmptyPointsToSet.v();
            }
            return n.getP2Set();
        }
        if( ((SparkOptions)getOpts()).propagator() == SparkOptions.propagator_alias ) {
            throw new RuntimeException( "The alias edge propagator does not compute points-to information for instance fields! Use a different propagator." );
        }
        PointsToSetInternal bases = (PointsToSetInternal) s;
        final PointsToSetInternal ret = setFactory.newSet( f.getType(), this );
        bases.forall( new P2SetVisitor() {
        public final void visit( Node n ) {
            ret.addAll( ((AllocNode) n).dot( f ).getP2Set(), null );
        }} );
        return ret;
    }

    public P2SetFactory getSetFactory() {
        return setFactory;
    }
    public void cleanUpMerges() {
        if( opts.verbose() ) {
            G.v().out.println( "Cleaning up graph for merged nodes" );
        }
        Map[] maps = { simple, alloc, store, load,
            simpleInv, allocInv, storeInv, loadInv };
        for( int i = 0; i < maps.length; i++ ) {
            Map m = maps[i];
            for( Iterator it = m.keySet().iterator(); it.hasNext(); ) {
                lookup( m, it.next() );
            }
        }
        somethingMerged = false;
        if( opts.verbose() ) {
            G.v().out.println( "Done cleaning up graph for merged nodes" );
        }
    }
    public boolean doAddSimpleEdge( VarNode from, VarNode to ) {
        return addToMap( simple, from, to ) | addToMap( simpleInv, to, from );
    }

    public boolean doAddStoreEdge( VarNode from, FieldRefNode to ) {
        return addToMap( store, from, to ) | addToMap( storeInv, to, from );
    }

    public boolean doAddLoadEdge( FieldRefNode from, VarNode to ) {
        return addToMap( load, from, to ) | addToMap( loadInv, to, from );
    }

    public boolean doAddAllocEdge( AllocNode from, VarNode to ) {
        return addToMap( alloc, from, to ) | addToMap( allocInv, to, from );
    }

    /** Node uses this to notify PAG that n2 has been merged into n1. */
    void mergedWith( Node n1, Node n2 ) {
        if( n1.equals( n2 ) ) throw new RuntimeException( "oops" );

        somethingMerged = true;
        if( ofcg() != null ) ofcg().mergedWith( n1, n2 );

        Map[] maps = { simple, alloc, store, load,
            simpleInv, allocInv, storeInv, loadInv };
        for( int mapi = 0; mapi < maps.length; mapi++ ) {
            Map m = maps[mapi];

            if( !m.keySet().contains( n2 ) ) continue;

            Object[] os = { m.get( n1 ), m.get( n2 ) };
            int size1 = getSize(os[0]); int size2 = getSize(os[1]);
            if( size1 == 0 ) {
                if( os[1] != null ) m.put( n1, os[1] );
            } else if( size2 == 0 ) {
                // nothing needed
            } else if( os[0] instanceof HashSet ) {
                if( os[1] instanceof HashSet ) {
                    ((HashSet) os[0]).addAll( (HashSet) os[1] );
                } else {
                    Node[] ar = (Node[]) os[1];
                    for( int j = 0; j < ar.length; j++ ) {
                        ( (HashSet) os[0] ).add( ar[j] );
                    }
                }
            } else if( os[1] instanceof HashSet ) {
                Node[] ar = (Node[]) os[0];
                for( int j = 0; j < ar.length; j++ ) {
                    ((HashSet) os[1]).add( ar[j] );
                }
                m.put( n1, os[1] );
            } else if( size1*size2 < 1000 ) {
                Node[] a1 = (Node[]) os[0];
                Node[] a2 = (Node[]) os[1];
                Node[] ret = new Node[size1+size2];
                System.arraycopy( a1, 0, ret, 0, a1.length ); 
                int j = a1.length;
                outer: for( int i = 0; i < a2.length; i++ ) {
                    Node rep = a2[i];
                    for( int k = 0; k < j; k++ )
                        if( rep == ret[k] ) continue outer;
                    ret[j++] = rep;
                }
                Node[] newArray = new Node[j];
                System.arraycopy( ret, 0, newArray, 0, j );
                m.put( n1, ret = newArray );
            } else {
                HashSet s = new HashSet( size1+size2 );
                for( int j = 0; j < os.length; j++ ) {
                    Object o = os[j];
                    if( o == null ) continue;
                    if( o instanceof Set ) {
                        s.addAll( (Set) o );
                    } else {
                        Node[] ar = (Node[]) o;
                        for( int k = 0; k < ar.length; k++ ) {
                            s.add( ar[k] );
                        }
                    }
                }
                m.put( n1, s );
            }
            m.remove( n2 );
        }
    }
    protected final static Node[] EMPTY_NODE_ARRAY = new Node[0];
    protected Node[] lookup( Map m, Object key ) {
	Object valueList = m.get( key );
	if( valueList == null ) {
	    return EMPTY_NODE_ARRAY;
	}
	if( valueList instanceof Set ) {
            try {
	    m.put( key, valueList = 
		    (Node[]) ( (Set) valueList ).toArray( EMPTY_NODE_ARRAY ) );
            } catch( Exception e ) {
                for( Iterator it = ((Set)valueList).iterator(); it.hasNext(); ) {
                    G.v().out.println( ""+it.next() );
                }
                throw new RuntimeException( ""+valueList+e );
            }
	}
	Node[] ret = (Node[]) valueList;
        if( somethingMerged ) {
            for( int i = 0; i < ret.length; i++ ) {
                Node reti = ret[i];
                Node rep = reti.getReplacement();
                if( rep != reti || rep == key ) {
                    Set s;
                    if( ret.length <= 75 ) {
                        int j = i;
                        outer: for( ; i < ret.length; i++ ) {
                            reti = ret[i];
                            rep = reti.getReplacement();
                            if( rep == key ) continue;
                            for( int k = 0; k < j; k++ )
                                if( rep == ret[k] ) continue outer;
                            ret[j++] = rep;
                        }
                        Node[] newArray = new Node[j];
                        System.arraycopy( ret, 0, newArray, 0, j );
                        m.put( key, ret = newArray );
                    } else {
                        s = new HashSet( ret.length * 2 );
                        for( int j = 0; j < i; j++ ) s.add( ret[j] );
                        for( int j = i; j < ret.length; j++ ) {
                            rep = ret[j].getReplacement();
                            if( rep != key ) {
                                s.add( rep );
                            }
                        }
                        m.put( key, ret = (Node[]) s.toArray( EMPTY_NODE_ARRAY ) );
                    }
                    break;
                }
            }
        }
	return ret;
    }

    public Node[] simpleLookup( VarNode key ) 
    { return lookup( simple, key ); }
    public Node[] simpleInvLookup( VarNode key ) 
    { return lookup( simpleInv, key ); }
    public Node[] loadLookup( FieldRefNode key ) 
    { return lookup( load, key ); }
    public Node[] loadInvLookup( VarNode key ) 
    { return lookup( loadInv, key ); }
    public Node[] storeLookup( VarNode key ) 
    { return lookup( store, key ); }
    public Node[] storeInvLookup( FieldRefNode key ) 
    { return lookup( storeInv, key ); }
    public Node[] allocLookup( AllocNode key ) 
    { return lookup( alloc, key ); }
    public Node[] allocInvLookup( VarNode key ) 
    { return lookup( allocInv, key ); }
    public Set simpleSources() { return simple.keySet(); }
    public Set allocSources() { return alloc.keySet(); }
    public Set storeSources() { return store.keySet(); }
    public Set loadSources() { return load.keySet(); }
    public Set simpleInvSources() { return simpleInv.keySet(); }
    public Set allocInvSources() { return allocInv.keySet(); }
    public Set storeInvSources() { return storeInv.keySet(); }
    public Set loadInvSources() { return loadInv.keySet(); }

    public Iterator simpleSourcesIterator() { return simple.keySet().iterator(); }
    public Iterator allocSourcesIterator() { return alloc.keySet().iterator(); }
    public Iterator storeSourcesIterator() { return store.keySet().iterator(); }
    public Iterator loadSourcesIterator() { return load.keySet().iterator(); }
    public Iterator simpleInvSourcesIterator() { return simpleInv.keySet().iterator(); }
    public Iterator allocInvSourcesIterator() { return allocInv.keySet().iterator(); }
    public Iterator storeInvSourcesIterator() { return storeInv.keySet().iterator(); }
    public Iterator loadInvSourcesIterator() { return loadInv.keySet().iterator(); }

    static private int getSize( Object set ) {
        if( set instanceof Set ) return ((Set) set).size();
        else if( set == null ) return 0;
        else return ((Object[]) set).length;
    }


    protected P2SetFactory setFactory;
    protected boolean somethingMerged = false;
}

