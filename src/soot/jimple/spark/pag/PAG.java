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
public class PAG implements PointsToAnalysis {
    public PAG( final SparkOptions opts ) {
	this.opts = opts;
        typeManager = new TypeManager();
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
        if( opts.add_tags() ) {
            nodeToTag = new HashMap();
        }
    }

    /** Returns the set of objects pointed to by variable l. */
    public PointsToSet reachingObjects( Local l ) {
        VarNode n = findVarNode( l );
        if( n == null ) {
            return EmptyPointsToSet.v();
        }
        return n.getP2Set();
    }

    /** Returns the set of objects pointed to by static field f. */
    public PointsToSet reachingObjects( SootField f ) {
        if( !f.isStatic() )
            throw new RuntimeException( "The parameter f must be a *static* field." );
        VarNode n = findVarNode( f );
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
            VarNode n = findVarNode( f );
            if( n == null ) {
                return EmptyPointsToSet.v();
            }
            return n.getP2Set();
        }
        if( getOpts().propagator() == SparkOptions.propagator_alias ) {
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

    /** Returns the set of objects pointed to by instance field f
     * of the objects pointed to by l. */
    public PointsToSet reachingObjects( Local l, SootField f ) {
        return reachingObjects( reachingObjects(l), f );
    }

    /** Returns SparkOptions for this graph. */
    public SparkOptions getOpts() { return opts; }
    /** Finds or creates the AllocNode for the new expression newExpr,
     * of type type. */
    private void addNodeTag( Node node, SootMethod m ) {
        if( nodeToTag != null ) {
            Tag tag;
            if( m == null ) {
                tag = new StringTag( node.toString() );
            } else {
                tag = new LinkTag( node.toString(), m, m.getDeclaringClass().getName() );
            }
            nodeToTag.put( node, tag );
        }
    }
    public AllocNode makeAllocNode( Object newExpr, Type type, SootMethod m ) {
        if( opts.types_for_sites() || opts.vta() ) newExpr = type;
	AllocNode ret = (AllocNode) valToAllocNode.get( newExpr );
	if( ret == null ) {
	    valToAllocNode.put( newExpr, ret = new AllocNode( this, newExpr, type, m ) );
            newAllocNodes.add( ret );
            addNodeTag( ret, m );
	} else if( !( ret.getType().equals( type ) ) ) {
	    throw new RuntimeException( "NewExpr "+newExpr+" of type "+type+
		    " previously had type "+ret.getType() );
	}
	return ret;
    }
    public AllocNode makeStringConstantNode( String s ) {
        if( opts.types_for_sites() || opts.vta() )
            return makeAllocNode( RefType.v( "java.lang.String" ),
                    RefType.v( "java.lang.String" ), null );
        StringConstantNode ret = (StringConstantNode) valToAllocNode.get( s );
	if( ret == null ) {
	    valToAllocNode.put( s, ret = new StringConstantNode( this, s ) );
            newAllocNodes.add( ret );
            addNodeTag( ret, null );
	}
	return ret;
    }
    public AllocNode makeClassConstantNode( String s ) {
        if( opts.types_for_sites() || opts.vta() )
            return makeAllocNode( RefType.v( "java.lang.Class" ),
                    RefType.v( "java.lang.Class" ), null );
        ClassConstantNode ret = (ClassConstantNode) valToAllocNode.get( "$$"+s );
	if( ret == null ) {
	    valToAllocNode.put( "$$"+s, ret = new ClassConstantNode( this, s ) );
            newAllocNodes.add( ret );
            addNodeTag( ret, null );
	}
	return ret;
    }

    ChunkedQueue newAllocNodes = new ChunkedQueue();
    public QueueReader allocNodeListener() { return newAllocNodes.reader(); }

    /** Finds the VarNode for the variable value, or returns null. */
    public VarNode findVarNode( Object value ) {
        if( opts.rta() ) {
            value = null;
        } else if( value instanceof Local ) {
            return (VarNode) localToNodeMap.get( (Local) value );
        }
	return (VarNode) valToVarNode.get( value );
    }
    /** Finds or creates the VarNode for the variable value, of type type. */
    public VarNode makeVarNode( Object value, Type type, SootMethod method ) {
        if( opts.rta() ) {
            value = null;
            type = RefType.v("java.lang.Object");
            method = null;
        } else if( value instanceof Local ) {
            Local val = (Local) value;
            if( val.getNumber() == 0 ) Scene.v().getLocalNumberer().add(val);
            VarNode ret = (VarNode) localToNodeMap.get( val );
            if( ret == null ) {
                localToNodeMap.put( (Local) value,
                    ret = new VarNode( this, value, type, method ) );
                addNodeTag( ret, method );
            } else if( !( ret.getType().equals( type ) ) ) {
                throw new RuntimeException( "Value "+value+" of type "+type+
                        " previously had type "+ret.getType() );
            }
            return ret;
        }
        VarNode ret = (VarNode) valToVarNode.get( value );
        if( ret == null ) {
            valToVarNode.put( value, 
                    ret = new VarNode( this, value, type, method ) );
            addNodeTag( ret, method );
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
            addNodeTag( ret, base.getMethod() );
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
        /*
        if( from.getReplacement() != from || to.getReplacement() != to ) {
            G.v().out.println( "from    is "+from );
            G.v().out.println( "fromrep is "+from.getReplacement() );
            G.v().out.println( "to      is "+to );
            G.v().out.println( "torep   is "+to.getReplacement() );
            throw new RuntimeException( "Edge between merged nodes" );
        }
        */
	if( from instanceof VarNode ) {
	    if( to instanceof VarNode ) {
		boolean ret1 = addToMap( simple, from, to );
		ret1 = addToMap( simpleInv, to, from ) | ret1;
                if( ret1 ) {
                    edgeQueue.add( from );
                    edgeQueue.add( to );
                    ret = true;
                }
                if( opts.simple_edges_bidirectional() ) {
                    boolean ret2 = addToMap( simple, to, from );
                    ret2 = addToMap( simpleInv, from, to ) | ret2;
                    if( ret2 ) {
                        edgeQueue.add( to );
                        edgeQueue.add( from );
                        ret = true;
                    }
                }
	    } else {
                if( !( to instanceof FieldRefNode ) ) {
                    throw new RuntimeException( "Attempt to add edge from "+
                        from+" to "+to );
                }
                if( !opts.rta() ) {
                    ret = addToMap( store, from, (FieldRefNode) to ) | ret;
                    ret = addToMap( storeInv, to, from ) | ret;
                    if( ret ) {
                        edgeQueue.add( from );
                        edgeQueue.add( to );
                    }
                }
	    }
	} else if( from instanceof FieldRefNode ) {
            if( !opts.rta() ) {
                if( !( to instanceof VarNode ) ) {
                    throw new RuntimeException( "Attempt to add edge from "+
                        from+" to "+to );
                }
                ret = addToMap( load, from, to ) | ret;
                ret = addToMap( loadInv, to, from ) | ret;
                if( ret ) {
                    edgeQueue.add( from );
                    edgeQueue.add( to );
                }
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
                if( ret ) {
                    edgeQueue.add( from );
                    edgeQueue.add( to );
                }
            }
	}
	return ret;
    }
    private ChunkedQueue edgeQueue = new ChunkedQueue();
    public QueueReader edgeReader() { return edgeQueue.reader(); }

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

    public P2SetFactory getSetFactory() {
        return setFactory;
    }
    public int getNumAllocNodes() {
        return allocNodeNumberer.size();
    }
    public TypeManager getTypeManager() {
        return typeManager;
    }

    public void setOnFlyCallGraph( OnFlyCallGraph ofcg ) { this.ofcg = ofcg; }
    public OnFlyCallGraph getOnFlyCallGraph() { return ofcg; }
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

    /** Adds the base of a dereference to the list of dereferenced 
     * variables. */
    public void addDereference( VarNode base ) {
        dereferences.add( base );
    }

    /** Returns list of dereferences variables. */
    public List getDereferences() {
        return dereferences;
    }

    public Map getNodeTags() {
        return nodeToTag;
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
            G.v().out.println( ""+names[i]+" "+size );
        }
        G.v().out.println( "valToVarNodeSize: "+valToVarNode.size() );
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
    private Numberer allocNodeNumberer = new Numberer();
    public Numberer getAllocNodeNumberer() { return allocNodeNumberer; }
    private Numberer varNodeNumberer = new Numberer();
    public Numberer getVarNodeNumberer() { return varNodeNumberer; }
    private Numberer fieldRefNodeNumberer = new Numberer();
    public Numberer getFieldRefNodeNumberer() { return fieldRefNodeNumberer; }
    private Numberer allocDotFieldNodeNumberer = new Numberer();
    public Numberer getAllocDotFieldNodeNumberer() { return allocDotFieldNodeNumberer; }

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
    private Map valToVarNode = new HashMap(1000);
    private Map valToAllocNode = new HashMap(1000);
    private P2SetFactory setFactory;
    private OnFlyCallGraph ofcg;
    private boolean somethingMerged = false;
    private ArrayList dereferences = new ArrayList();
    private TypeManager typeManager;
    private LargeNumberedMap localToNodeMap = new LargeNumberedMap( Scene.v().getLocalNumberer() );
    public int maxFinishNumber = 0;
    private Map nodeToTag;
}

