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

/** Pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class PAG implements PointsToAnalysis {
    /** Returns the set of objects reaching variable l before stmt in method. */
    public PointsToSet reachingObjects( SootMethod method, Stmt stmt,
                            Local l ) {
        VarNode n = findVarNode( l );
        if( n == null ) {
            return EmptyPointsToSet.v();
        }
        return n.getP2Set();
    }

    /** Returns SparkOptions for this graph. */
    public SparkOptions getOpts() { return opts; }
    /** Finds or creates the AllocNode for the new expression newExpr,
     * of type type. */
    public AllocNode makeAllocNode( Object newExpr, Type type ) {
        if( opts.typesForSites() || opts.VTA() ) newExpr = type;
	AllocNode ret = (AllocNode) valToAllocNode.get( newExpr );
	if( ret == null ) {
	    valToAllocNode.put( newExpr, ret = new AllocNode( this, newExpr, type ) );
	} else if( !( ret.getType().equals( type ) ) ) {
	    throw new RuntimeException( "NewExpr "+newExpr+" of type "+type+
		    " previously had type "+ret.getType() );
	}
	return ret;
    }
    /** Finds the VarNode for the variable value, or returns null. */
    public VarNode findVarNode( Object value ) {
        if( opts.RTA() ) {
            value = null;
        }
	return (VarNode) valToVarNode.get( value );
    }
    /** Finds or creates the VarNode for the variable value, of type type. */
    public VarNode makeVarNode( Object value, Type type, SootMethod method ) {
        if( opts.RTA() ) {
            value = null; type = RefType.v("java.lang.Object"); method = null;
        }
	VarNode ret = (VarNode) valToVarNode.get( value );
	if( ret == null ) {
	    valToVarNode.put( value, 
                    ret = new VarNode( this, value, type, method ) );
	} else if( !( ret.getType().equals( type ) ) ) {
	    throw new RuntimeException( "Value "+value+" of type "+type+
		    " previously had type "+ret.getType() );
	}
	return ret;
    }
    /** Finds the FieldRefNode for base variable value and field
     * field, or returns null. */
    public FieldRefNode findFieldRefNode( Object baseValue, SparkField field ) {
	VarNode base = findVarNode( baseValue );
	if( base == null ) return null;
	return base.dot( field );
    }
    /** Finds or creates the FieldRefNode for base variable baseValue and field
     * field, of type type. */
    public FieldRefNode makeFieldRefNode( Object baseValue, Type baseType,
	    SparkField field, SootMethod method ) {
	VarNode base = makeVarNode( baseValue, baseType, method );
        return makeFieldRefNode( base, field );
    }
    /** Finds or creates the FieldRefNode for base variable base and field
     * field, of type type. */
    public FieldRefNode makeFieldRefNode( VarNode base,
	    SparkField field ) {
	FieldRefNode ret = base.dot( field );
	if( ret == null ) {
	    ret = new FieldRefNode( this, base, field );
	}
	return ret;
    }
    /** Finds the AllocDotField for base AllocNode an and field
     * field, or returns null. */
    public AllocDotField findAllocDotField( AllocNode an, SparkField field ) {
	return an.dot( field );
    }
    /** Finds or creates the AllocDotField for base variable baseValue and field
     * field, of type t. */
    public AllocDotField makeAllocDotField( AllocNode an, SparkField field ) {
	AllocDotField ret = an.dot( field );
	if( ret == null ) {
	    ret = new AllocDotField( this, an, field );
	}
	return ret;
    }
    /** Adds an edge to the graph, returning false if it was already there. */
    public boolean addEdge( Node from, Node to ) {
        FastHierarchy fh = typeManager.getFastHierarchy();
	boolean ret = false;
        if( from.getReplacement() != from || to.getReplacement() != to )
            throw new RuntimeException( "Edge between merged nodes" );
	if( from instanceof VarNode ) {
	    if( to instanceof VarNode ) {
		ret = addToMap( simple, from, to ) | ret;
		ret = addToMap( simpleInv, to, from ) | ret;
                if( opts.simpleEdgesBidirectional() ) {
                    ret = addToMap( simple, to, from ) | ret;
                    ret = addToMap( simpleInv, from, to ) | ret;
                }
	    } else {
                if( !( to instanceof FieldRefNode ) ) {
                    throw new RuntimeException( "Attempt to add edge from "+
                        from+" to "+to );
                }
                if( !opts.RTA() ) {
                    ret = addToMap( store, from, (FieldRefNode) to ) | ret;
                    ret = addToMap( storeInv, to, from ) | ret;
                }
	    }
	} else if( from instanceof FieldRefNode ) {
            if( !opts.RTA() ) {
                if( !( to instanceof VarNode ) ) {
                    throw new RuntimeException( "Attempt to add edge from "+
                        from+" to "+to );
                }
                ret = addToMap( load, from, to ) | ret;
                ret = addToMap( loadInv, to, from ) | ret;
            }
	} else {
            if( !( from instanceof AllocNode ) 
            || !( to instanceof VarNode ) ) {
                throw new RuntimeException( "Attempt to add edge from "+
                    from+" to "+to );
            }
            if( fh == null || to.getType() == null 
            || fh.canStoreType( from.getType(), to.getType() ) ) {
                ret = addToMap( alloc, from, to ) | ret;
                ret = addToMap( allocInv, to, from ) | ret;
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
    public Set allVarNodes() { return new HashSet( valToVarNode.values() ); }

    public P2SetFactory getSetFactory() {
        return setFactory;
    }
    public PAG( final SparkOptions opts ) {
	this.opts = opts;
        typeManager = new TypeManager();
        if( !opts.ignoreTypesEntirely() ) {
            typeManager.setFastHierarchy( Scene.v().getOrMakeFastHierarchy() );
        }
        opts.setImpl( new SparkOptions.Switch_setImpl() {
            public void case_hash() 
            { setFactory = HashPointsToSet.getFactory(); }
            public void case_hybrid() 
            { setFactory = HybridPointsToSet.getFactory(); }
            public void case_array() 
            { setFactory = SortedArraySet.getFactory(); }
            public void case_bit() 
            { setFactory = BitPointsToSet.getFactory(); }
            public void case_double() {
                final P2SetFactory[] oldF = new P2SetFactory[1];
                final P2SetFactory[] newF = new P2SetFactory[1];
                opts.doubleSetOld( new SparkOptions.Switch_doubleSetOld() {
                    public void case_hash() 
                    { oldF[0] = HashPointsToSet.getFactory(); }
                    public void case_hybrid() 
                    { oldF[0] = HybridPointsToSet.getFactory(); }
                    public void case_array() 
                    { oldF[0] = SortedArraySet.getFactory(); }
                    public void case_bit() 
                    { oldF[0] = BitPointsToSet.getFactory(); }
                } );
                opts.doubleSetNew( new SparkOptions.Switch_doubleSetNew() {
                    public void case_hash() 
                    { newF[0] = HashPointsToSet.getFactory(); }
                    public void case_hybrid() 
                    { newF[0] = HybridPointsToSet.getFactory(); }
                    public void case_array() 
                    { newF[0] = SortedArraySet.getFactory(); }
                    public void case_bit() 
                    { newF[0] = BitPointsToSet.getFactory(); }
                } );
                setFactory = DoublePointsToSet.getFactory( newF[0], oldF[0] );
            }
        } );
    }
    public int getNumAllocNodes() {
        return (-nextAllocNodeId)-1;
    }
    public TypeManager getTypeManager() {
        return typeManager;
    }

    public void setOnFlyCallGraph( OnFlyCallGraph ofcg ) { this.ofcg = ofcg; }
    public OnFlyCallGraph getOnFlyCallGraph() { return ofcg; }
    public void cleanUpMerges() {
        if( opts.verbose() ) {
            System.out.println( "Cleaning up graph for merged nodes" );
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
            System.out.println( "Done cleaning up graph for merged nodes" );
        }
    }

    /** Adds the base of a dereference to the list of dereferenced 
     * variables. */
    public void addDereference( VarNode base ) {
        dereferences.add( base );
    }

    /** Returns list of dereferences variables. */
    public List getDereferences() {
        return dereferences;
    }

    /*
    public void dumpNumbersOfEdges() {
        Map[] maps = { simple, alloc, store, load,
            simpleInv, allocInv, storeInv, loadInv };
        String[] names = { "simple", "alloc", "store", "load",
            "simpleInv", "allocInv", "storeInv", "loadInv" };
        for( int i = 0; i < maps.length; i++ ) {
            Map m = maps[i];
            int size = 0;
            for( Iterator it = m.keySet().iterator(); it.hasNext(); ) {
                size += lookup( m, it.next() ).length;
            }
            System.out.println( ""+names[i]+" "+size );
        }
        System.out.println( "valToVarNodeSize: "+valToVarNode.size() );
    }
    */
    /* End of public methods. */

    static private int getSize( Object set ) {
        if( set instanceof Set ) return ((Set) set).size();
        else if( set == null ) return 0;
        else return ((Object[]) set).length;
    }
    /** Node uses this to notify PAG that n2 has been merged into n1. */
    void mergedWith( Node n1, Node n2 ) {
        if( n1.equals( n2 ) ) throw new RuntimeException( "oops" );

        somethingMerged = true;
        if( ofcg != null ) ofcg.mergedWith( n1, n2 );

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
    protected int nextNodeId = 1;
    int getNextNodeId() {
	return nextNodeId++;
    }
    protected int nextAllocNodeId = -1;
    int getNextAllocNodeId() {
	return nextAllocNodeId--;
    }

    /* End of package methods. */

    protected SparkOptions opts;

    protected Map simple = new HashMap();
    protected Map load = new HashMap();
    protected Map store = new HashMap();
    protected Map alloc = new HashMap();

    protected Map simpleInv = new HashMap();
    protected Map loadInv = new HashMap();
    protected Map storeInv = new HashMap();
    protected Map allocInv = new HashMap();

    protected boolean addToMap( Map m, Node key, Node value ) {
	boolean ret = false;
	Object valueList = m.get( key );

	if( valueList == null ) {
	    m.put( key, valueList = new HashSet(4) );
	} else if( !(valueList instanceof Set) ) {
	    Node[] ar = (Node[]) valueList;
            Node[] newar = new Node[ar.length+1];
            for( int i = 0; i < ar.length; i++ ) {
                Node n = ar[i];
                if( n == value ) return false;
                newar[i] = n;
            }
            newar[ar.length] = value;
            m.put( key, newar );
            return true;
	}
	return ((Set) valueList).add( value );
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
                    System.out.println( ""+it.next() );
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
    protected Map valToVarNode = new HashMap(1000);
    protected Map valToAllocNode = new HashMap(1000);
    protected P2SetFactory setFactory;
    protected OnFlyCallGraph ofcg;
    protected static boolean somethingMerged = false;
    protected ArrayList dereferences = new ArrayList();
    protected TypeManager typeManager;
}

