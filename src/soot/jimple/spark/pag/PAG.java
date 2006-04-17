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
import soot.jimple.spark.builder.*;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;
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
        if( opts.add_tags() ) {
            nodeToTag = new HashMap();
        }
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
            case SparkOptions.set_impl_heintze:
            	setFactory = SharedHybridSet.getFactory();
            	break;
            case SparkOptions.set_impl_sharedlist:
            	setFactory = SharedListSet.getFactory();
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
                    case SparkOptions.double_set_old_heintze:
                    	oldF = SharedHybridSet.getFactory();
                    	break;
                    case SparkOptions.double_set_old_sharedlist:
                    	oldF = SharedListSet.getFactory();
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
                    case SparkOptions.double_set_new_heintze:
                    	newF = SharedHybridSet.getFactory();
                    	break;
                    case SparkOptions.double_set_new_sharedlist:
                    	newF = SharedListSet.getFactory();
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
    
    /** Returns the set of objects pointed to by variable l in context c. */
    public PointsToSet reachingObjects( Context c, Local l ) {
        VarNode n = findContextVarNode( l, c );
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

        return reachingObjectsInternal( s, f );
    }

    /** Returns the set of objects pointed to by elements of the arrays
     * in the PointsToSet s. */
    public PointsToSet reachingObjectsOfArrayElement( PointsToSet s ) {
        return reachingObjectsInternal( s, ArrayElement.v() );
    }

    private PointsToSet reachingObjectsInternal( PointsToSet s, final SparkField f ) {
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
        final PointsToSetInternal ret = setFactory.newSet( 
                (f instanceof SootField) ? ((SootField)f).getType() : null, this );
        bases.forall( new P2SetVisitor() {
        public final void visit( Node n ) {
            Node nDotF = ((AllocNode) n).dot( f );
            if(nDotF != null) ret.addAll( nDotF.getP2Set(), null );
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

    /** Returns the set of objects pointed to by instance field f
     * of the objects pointed to by l. */
    public PointsToSet reachingObjects( Local l, SootField f ) {
        return reachingObjects( reachingObjects(l), f );
    }

    /** Returns the set of objects pointed to by instance field f
     * of the objects pointed to by l in context c. */
    public PointsToSet reachingObjects( Context c, Local l, SootField f ) {
        return reachingObjects( reachingObjects(c, l), f );
    }

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
    public AllocNode makeClassConstantNode( ClassConstant cc ) {
        if( opts.types_for_sites() || opts.vta() )
            return makeAllocNode( RefType.v( "java.lang.Class" ),
                    RefType.v( "java.lang.Class" ), null );
        ClassConstantNode ret = (ClassConstantNode) valToAllocNode.get(cc);
	if( ret == null ) {
	    valToAllocNode.put(cc, ret = new ClassConstantNode(this, cc));
            newAllocNodes.add( ret );
            addNodeTag( ret, null );
	}
	return ret;
    }

    ChunkedQueue newAllocNodes = new ChunkedQueue();
    public QueueReader allocNodeListener() { return newAllocNodes.reader(); }

    /** Finds the GlobalVarNode for the variable value, or returns null. */
    public GlobalVarNode findGlobalVarNode( Object value ) {
        if( opts.rta() ) {
            value = null;
        }
	return (GlobalVarNode) valToGlobalVarNode.get( value );
    }
    /** Finds the LocalVarNode for the variable value, or returns null. */
    public LocalVarNode findLocalVarNode( Object value ) {
        if( opts.rta() ) {
            value = null;
        } else if( value instanceof Local ) {
            return (LocalVarNode) localToNodeMap.get( (Local) value );
        }
	return (LocalVarNode) valToLocalVarNode.get( value );
    }
    /** Finds or creates the GlobalVarNode for the variable value, of type type. */
    public GlobalVarNode makeGlobalVarNode( Object value, Type type ) {
        if( opts.rta() ) {
            value = null;
            type = RefType.v("java.lang.Object");
        }
        GlobalVarNode ret = (GlobalVarNode) valToGlobalVarNode.get( value );
        if( ret == null ) {
            valToGlobalVarNode.put( value, 
                    ret = new GlobalVarNode( this, value, type ) );
            addNodeTag( ret, null );
        } else if( !( ret.getType().equals( type ) ) ) {
            throw new RuntimeException( "Value "+value+" of type "+type+
                    " previously had type "+ret.getType() );
        }
	return ret;
    }
    /** Finds or creates the LocalVarNode for the variable value, of type type. */
    public LocalVarNode makeLocalVarNode( Object value, Type type, SootMethod method ) {
        if( opts.rta() ) {
            value = null;
            type = RefType.v("java.lang.Object");
            method = null;
        } else if( value instanceof Local ) {
            Local val = (Local) value;
            if( val.getNumber() == 0 ) Scene.v().getLocalNumberer().add(val);
            LocalVarNode ret = (LocalVarNode) localToNodeMap.get( val );
            if( ret == null ) {
                localToNodeMap.put( (Local) value,
                    ret = new LocalVarNode( this, value, type, method ) );
                addNodeTag( ret, method );
            } else if( !( ret.getType().equals( type ) ) ) {
                throw new RuntimeException( "Value "+value+" of type "+type+
                        " previously had type "+ret.getType() );
            }
            return ret;
        }
        LocalVarNode ret = (LocalVarNode) valToLocalVarNode.get( value );
        if( ret == null ) {
            valToLocalVarNode.put( value, 
                    ret = new LocalVarNode( this, value, type, method ) );
            addNodeTag( ret, method );
        } else if( !( ret.getType().equals( type ) ) ) {
            throw new RuntimeException( "Value "+value+" of type "+type+
                    " previously had type "+ret.getType() );
        }
	return ret;
    }
    /** Finds the ContextVarNode for base variable value and context
     * context, or returns null. */
    public ContextVarNode findContextVarNode( Object baseValue, Context context ) {
	LocalVarNode base = findLocalVarNode( baseValue );
	if( base == null ) return null;
	return base.context( context );
    }
    /** Finds or creates the ContextVarNode for base variable baseValue and context
     * context, of type type. */
    public ContextVarNode makeContextVarNode( Object baseValue, Type baseType,
	    Context context, SootMethod method ) {
	LocalVarNode base = makeLocalVarNode( baseValue, baseType, method );
        return makeContextVarNode( base, context );
    }
    /** Finds or creates the ContextVarNode for base variable base and context
     * context, of type type. */
    public ContextVarNode makeContextVarNode( LocalVarNode base, Context context ) {
	ContextVarNode ret = base.context( context );
	if( ret == null ) {
	    ret = new ContextVarNode( this, base, context );
            addNodeTag( ret, base.getMethod() );
	}
	return ret;
    }
    /** Finds the FieldRefNode for base variable value and field
     * field, or returns null. */
    public FieldRefNode findLocalFieldRefNode( Object baseValue, SparkField field ) {
	VarNode base = findLocalVarNode( baseValue );
	if( base == null ) return null;
	return base.dot( field );
    }
    /** Finds the FieldRefNode for base variable value and field
     * field, or returns null. */
    public FieldRefNode findGlobalFieldRefNode( Object baseValue, SparkField field ) {
	VarNode base = findGlobalVarNode( baseValue );
	if( base == null ) return null;
	return base.dot( field );
    }
    /** Finds or creates the FieldRefNode for base variable baseValue and field
     * field, of type type. */
    public FieldRefNode makeLocalFieldRefNode( Object baseValue, Type baseType,
	    SparkField field, SootMethod method ) {
	VarNode base = makeLocalVarNode( baseValue, baseType, method );
        return makeFieldRefNode( base, field );
    }
    /** Finds or creates the FieldRefNode for base variable baseValue and field
     * field, of type type. */
    public FieldRefNode makeGlobalFieldRefNode( Object baseValue, Type baseType,
	    SparkField field ) {
	VarNode base = makeGlobalVarNode( baseValue, baseType );
        return makeFieldRefNode( base, field );
    }
    /** Finds or creates the FieldRefNode for base variable base and field
     * field, of type type. */
    public FieldRefNode makeFieldRefNode( VarNode base, SparkField field ) {
	FieldRefNode ret = base.dot( field );
	if( ret == null ) {
	    ret = new FieldRefNode( this, base, field );
	    if( base instanceof LocalVarNode ) {
	    	addNodeTag( ret, ((LocalVarNode) base).getMethod() );
	    } else {
	    	addNodeTag( ret, null );
	    }
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

    public boolean addSimpleEdge( VarNode from, VarNode to ) {
	boolean ret = false;
        if( doAddSimpleEdge( from, to ) ) {
            edgeQueue.add( from );
            edgeQueue.add( to );
            ret = true;
        }
        if( opts.simple_edges_bidirectional() ) {
            if( doAddSimpleEdge( to, from ) ) {
                edgeQueue.add( to );
                edgeQueue.add( from );
                ret = true;
            }
        }
        return ret;
    }

    public boolean addStoreEdge( VarNode from, FieldRefNode to ) {
        if( !opts.rta() ) {
            if( doAddStoreEdge( from, to ) ) {
                edgeQueue.add( from );
                edgeQueue.add( to );
                return true;
            }
        }
        return false;
    }

    public boolean addLoadEdge( FieldRefNode from, VarNode to ) {
        if( !opts.rta() ) {
            if( doAddLoadEdge( from, to ) ) {
                edgeQueue.add( from );
                edgeQueue.add( to );
                return true;
            }
        }
        return false;
    }

    public boolean addAllocEdge( AllocNode from, VarNode to ) {
        FastHierarchy fh = typeManager.getFastHierarchy();
        if( fh == null || to.getType() == null 
        || fh.canStoreType( from.getType(), to.getType() ) ) {
            if( doAddAllocEdge( from, to ) ) {
                edgeQueue.add( from );
                edgeQueue.add( to );
                return true;
            }
        }
        return false;
    }

    /** Adds an edge to the graph, returning false if it was already there. */
    public final boolean addEdge( Node from, Node to ) {
        from = from.getReplacement();
        to = to.getReplacement();
	if( from instanceof VarNode ) {
	    if( to instanceof VarNode ) {
                return addSimpleEdge( (VarNode) from, (VarNode) to );
	    } else {
                return addStoreEdge( (VarNode) from, (FieldRefNode) to );
	    }
	} else if( from instanceof FieldRefNode ) {
            return addLoadEdge( (FieldRefNode) from, (VarNode) to );

	} else {
            return addAllocEdge( (AllocNode) from, (VarNode) to );
	}
    }

    protected ChunkedQueue edgeQueue = new ChunkedQueue();
    public QueueReader edgeReader() { return edgeQueue.reader(); }

    public int getNumAllocNodes() {
        return allocNodeNumberer.size();
    }
    public TypeManager getTypeManager() {
        return typeManager;
    }

    public void setOnFlyCallGraph( OnFlyCallGraph ofcg ) { this.ofcg = ofcg; }
    public OnFlyCallGraph getOnFlyCallGraph() { return ofcg; }
    public OnFlyCallGraph ofcg() { return ofcg; }
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

    private ArrayNumberer allocNodeNumberer = new ArrayNumberer();
    public ArrayNumberer getAllocNodeNumberer() { return allocNodeNumberer; }
    private ArrayNumberer varNodeNumberer = new ArrayNumberer();
    public ArrayNumberer getVarNodeNumberer() { return varNodeNumberer; }
    private ArrayNumberer fieldRefNodeNumberer = new ArrayNumberer();
    public ArrayNumberer getFieldRefNodeNumberer() { return fieldRefNodeNumberer; }
    private ArrayNumberer allocDotFieldNodeNumberer = new ArrayNumberer();
    public ArrayNumberer getAllocDotFieldNodeNumberer() { return allocDotFieldNodeNumberer; }


    /** Returns SparkOptions for this graph. */
    public SparkOptions getOpts() { return opts; }

    final public void addCallTarget( Edge e ) {
        if( !e.passesParameters() ) return;
        MethodPAG srcmpag = MethodPAG.v( this, e.src() );
        MethodPAG tgtmpag = MethodPAG.v( this, e.tgt() );
        if( e.isExplicit() || e.kind() == Kind.THREAD ) {
            addCallTarget( srcmpag, tgtmpag, (Stmt) e.srcUnit(),
                           e.srcCtxt(), e.tgtCtxt() );
        } else {
            if( e.kind() == Kind.PRIVILEGED ) {
                // Flow from first parameter of doPrivileged() invocation
                // to this of target, and from return of target to the
                // return of doPrivileged()

                InvokeExpr ie = e.srcStmt().getInvokeExpr();

                Node parm = srcmpag.nodeFactory().getNode( ie.getArg(0) );
                parm = srcmpag.parameterize( parm, e.srcCtxt() );
                parm = parm.getReplacement();

                Node thiz = tgtmpag.nodeFactory().caseThis();
                thiz = tgtmpag.parameterize( thiz, e.tgtCtxt() );
                thiz = thiz.getReplacement();

                addEdge( parm, thiz );

                if( e.srcUnit() instanceof AssignStmt ) {
                    AssignStmt as = (AssignStmt) e.srcUnit();

                    Node ret = tgtmpag.nodeFactory().caseRet();
                    ret = tgtmpag.parameterize( ret, e.tgtCtxt() );
                    ret = ret.getReplacement();

                    Node lhs = srcmpag.nodeFactory().getNode(as.getLeftOp());
                    lhs = srcmpag.parameterize( lhs, e.srcCtxt() );
                    lhs = lhs.getReplacement();

                    addEdge( ret, lhs );
                }
            } else if( e.kind() == Kind.FINALIZE ) {
                Node srcThis = srcmpag.nodeFactory().caseThis();
                srcThis = srcmpag.parameterize( srcThis, e.srcCtxt() );
                srcThis = srcThis.getReplacement();

                Node tgtThis = tgtmpag.nodeFactory().caseThis();
                tgtThis = tgtmpag.parameterize( tgtThis, e.tgtCtxt() );
                tgtThis = tgtThis.getReplacement();

                addEdge( srcThis, tgtThis );
            } else if( e.kind() == Kind.NEWINSTANCE ) {
                Stmt s = (Stmt) e.srcUnit();
                InstanceInvokeExpr iie = (InstanceInvokeExpr) s.getInvokeExpr();

                Node cls = srcmpag.nodeFactory().getNode( iie.getBase() );
                cls = srcmpag.parameterize( cls, e.srcCtxt() );
                cls = cls.getReplacement();
                Node newObject = nodeFactory.caseNewInstance( (VarNode) cls );

                Node initThis = tgtmpag.nodeFactory().caseThis();
                initThis = tgtmpag.parameterize( initThis, e.tgtCtxt() );
                initThis = initThis.getReplacement();

                addEdge( newObject, initThis );
                if (s instanceof AssignStmt) {
                    AssignStmt as = (AssignStmt)s;
                    Node asLHS = srcmpag.nodeFactory().getNode(as.getLeftOp());
                    asLHS = srcmpag.parameterize( asLHS, e.srcCtxt());
                    asLHS = asLHS.getReplacement();
                    addEdge( newObject, asLHS);
                }
            } else {
                throw new RuntimeException( "Unhandled edge "+e );
            }
        }
    }

    
    /** Adds method target as a possible target of the invoke expression in s.
     * If target is null, only creates the nodes for the call site,
     * without actually connecting them to any target method.
     **/
    final public void addCallTarget( MethodPAG srcmpag,
                                     MethodPAG tgtmpag,
                                     Stmt s,
                                     Context srcContext,
                                     Context tgtContext ) {
        MethodNodeFactory srcnf = srcmpag.nodeFactory();
        MethodNodeFactory tgtnf = tgtmpag.nodeFactory();
        InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
        int numArgs = ie.getArgCount();
        for( int i = 0; i < numArgs; i++ ) {
            Value arg = ie.getArg( i );
            if( !( arg.getType() instanceof RefLikeType ) ) continue;
            if( arg instanceof NullConstant ) continue;

            Node argNode = srcnf.getNode( arg );
            argNode = srcmpag.parameterize( argNode, srcContext );
            argNode = argNode.getReplacement();

            Node parm = tgtnf.caseParm( i );
            parm = tgtmpag.parameterize( parm, tgtContext );
            parm = parm.getReplacement();

            addEdge( argNode, parm );
        }
        if( ie instanceof InstanceInvokeExpr ) {
            InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;

            Node baseNode = srcnf.getNode( iie.getBase() );
            baseNode = srcmpag.parameterize( baseNode, srcContext );
            baseNode = baseNode.getReplacement();

            Node thisRef = tgtnf.caseThis();
            thisRef = tgtmpag.parameterize( thisRef, tgtContext );
            thisRef = thisRef.getReplacement();
            addEdge( baseNode, thisRef );
        }
        if( s instanceof AssignStmt ) {
            Value dest = ( (AssignStmt) s ).getLeftOp();
            if( dest.getType() instanceof RefLikeType && !(dest instanceof NullConstant) ) {

                Node destNode = srcnf.getNode( dest );
                destNode = srcmpag.parameterize( destNode, srcContext );
                destNode = destNode.getReplacement();

                Node retNode = tgtnf.caseRet();
                retNode = tgtmpag.parameterize( retNode, tgtContext );
                retNode = retNode.getReplacement();

                addEdge( retNode, destNode );
            }
        }
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
	Object valueList = m.get( key );

	if( valueList == null ) {
	    m.put( key, valueList = new HashSet(4) );
	} else if( !(valueList instanceof Set) ) {
	    Node[] ar = (Node[]) valueList;
            HashSet vl = new HashSet(ar.length+4);
            m.put( key, vl );
            for( int i = 0; i < ar.length; i++ ) vl.add( ar[i] );
            return vl.add( value );
            /*
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
            */
	}
	return ((Set) valueList).add( value );
    }
    private Map valToLocalVarNode = new HashMap(1000);
    private Map valToGlobalVarNode = new HashMap(1000);
    private Map valToAllocNode = new HashMap(1000);
    private OnFlyCallGraph ofcg;
    private ArrayList dereferences = new ArrayList();
    protected TypeManager typeManager;
    private LargeNumberedMap localToNodeMap = new LargeNumberedMap( Scene.v().getLocalNumberer() );
    public int maxFinishNumber = 0;
    private Map nodeToTag;
    private GlobalNodeFactory nodeFactory = new GlobalNodeFactory(this);
    public GlobalNodeFactory nodeFactory() { return nodeFactory; }
    public NativeMethodDriver nativeMethodDriver;

}

