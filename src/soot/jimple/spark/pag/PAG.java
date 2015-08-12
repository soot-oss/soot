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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Context;
import soot.FastHierarchy;
import soot.G;
import soot.Kind;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.spark.builder.GlobalNodeFactory;
import soot.jimple.spark.builder.MethodNodeFactory;
import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.sets.BitPointsToSet;
import soot.jimple.spark.sets.DoublePointsToSet;
import soot.jimple.spark.sets.EmptyPointsToSet;
import soot.jimple.spark.sets.HashPointsToSet;
import soot.jimple.spark.sets.HybridPointsToSet;
import soot.jimple.spark.sets.P2SetFactory;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.spark.sets.SharedHybridSet;
import soot.jimple.spark.sets.SharedListSet;
import soot.jimple.spark.sets.SortedArraySet;
import soot.jimple.spark.solver.OnFlyCallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;
import soot.options.SparkOptions;
import soot.tagkit.LinkTag;
import soot.tagkit.StringTag;
import soot.tagkit.Tag;
import soot.toolkits.scalar.Pair;
import soot.util.ArrayNumberer;
import soot.util.HashMultiMap;
import soot.util.LargeNumberedMap;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;

/** Pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class PAG implements PointsToAnalysis {
    public PAG( final SparkOptions opts ) {
        this.opts = opts;
        if( opts.add_tags() ) {
            nodeToTag = new HashMap<Node, Tag>();
        }
        if (opts.rta() && opts.on_fly_cg()) {
        	throw new RuntimeException("Incompatible options rta:true and on-fly-cg:true for cg.spark. Use -p cg-"
        			+ ".spark on-fly-cg:false when using RTA.");
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
        runGeomPTA = opts.geom_pta();
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
        if( (getOpts()).propagator() == SparkOptions.propagator_alias ) {
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
    
    private <K extends Node> void lookupInMap(Map<K, Object> map) {
        for (K object : map.keySet()) {
            lookup( map, object );
        }
    }
    
    public void cleanUpMerges() {
        if( opts.verbose() ) {
            G.v().out.println( "Cleaning up graph for merged nodes" );
        }
       	lookupInMap(simple);
       	lookupInMap(alloc);
       	lookupInMap(store);
       	lookupInMap(load);
       	lookupInMap(simpleInv);
       	lookupInMap(allocInv);
       	lookupInMap(storeInv);
       	lookupInMap(loadInv);
       	
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
        for (Map<Node, Object> m : maps) {
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
                    for (Node element0 : ar) {
                        ( (HashSet<Node>) os[0] ).add( element0 );
                    }
                }
            } else if( os[1] instanceof HashSet ) {
                Node[] ar = (Node[]) os[0];
                for (Node element0 : ar) {
                    ((HashSet<Node>) os[1]).add( element0 );
                }
                m.put( n1, os[1] );
            } else if( size1*size2 < 1000 ) {
                Node[] a1 = (Node[]) os[0];
                Node[] a2 = (Node[]) os[1];
                Node[] ret = new Node[size1+size2];
                System.arraycopy( a1, 0, ret, 0, a1.length ); 
                int j = a1.length;
                outer: for (Node rep : a2) {
                    for( int k = 0; k < j; k++ )
                        if( rep == ret[k] ) continue outer;
                    ret[j++] = rep;
                }
                Node[] newArray = new Node[j];
                System.arraycopy( ret, 0, newArray, 0, j );
                m.put( n1, ret = newArray );
            } else {
                HashSet<Node> s = new HashSet<Node>( size1+size2 );
                for (Object o : os) {
                    if( o == null ) continue;
                    if( o instanceof Set ) {
                        s.addAll( (Set<Node>) o );
                    } else {
                        Node[] ar = (Node[]) o;
                        for (Node element1 : ar) {
                            s.add( element1 );
                        }
                    }
                }
                m.put( n1, s );
            }
            m.remove( n2 );
        }
    }
    protected final static Node[] EMPTY_NODE_ARRAY = new Node[0];
    protected <K extends Node> Node[] lookup( Map<K, Object> m, K key ) {
	Object valueList = m.get( key );
	if( valueList == null ) {
	    return EMPTY_NODE_ARRAY;
	}
	if( valueList instanceof Set ) {
            try {
	    m.put( key, valueList = 
		    ( (Set) valueList ).toArray( EMPTY_NODE_ARRAY ) );
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
                    Set<Node> s;
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
                        s = new HashSet<Node>( ret.length * 2 );
                        for( int j = 0; j < i; j++ ) s.add( ret[j] );
                        for( int j = i; j < ret.length; j++ ) {
                            rep = ret[j].getReplacement();
                            if( rep != key ) {
                                s.add( rep );
                            }
                        }
                        m.put( key, ret = s.toArray( EMPTY_NODE_ARRAY ) );
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
    public Set<VarNode> simpleSources() { return simple.keySet(); }
    public Set<AllocNode> allocSources() { return alloc.keySet(); }
    public Set<VarNode> storeSources() { return store.keySet(); }
    public Set<FieldRefNode> loadSources() { return load.keySet(); }
    public Set<VarNode> simpleInvSources() { return simpleInv.keySet(); }
    public Set<VarNode> allocInvSources() { return allocInv.keySet(); }
    public Set<FieldRefNode> storeInvSources() { return storeInv.keySet(); }
    public Set<VarNode> loadInvSources() { return loadInv.keySet(); }

    public Iterator<VarNode> simpleSourcesIterator() { return simple.keySet().iterator(); }
    public Iterator<AllocNode> allocSourcesIterator() { return alloc.keySet().iterator(); }
    public Iterator<VarNode> storeSourcesIterator() { return store.keySet().iterator(); }
    public Iterator<FieldRefNode> loadSourcesIterator() { return load.keySet().iterator(); }
    public Iterator<VarNode> simpleInvSourcesIterator() { return simpleInv.keySet().iterator(); }
    public Iterator<VarNode> allocInvSourcesIterator() { return allocInv.keySet().iterator(); }
    public Iterator<FieldRefNode> storeInvSourcesIterator() { return storeInv.keySet().iterator(); }
    public Iterator<VarNode> loadInvSourcesIterator() { return loadInv.keySet().iterator(); }

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
	AllocNode ret = valToAllocNode.get( newExpr );
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

    ChunkedQueue<AllocNode> newAllocNodes = new ChunkedQueue<AllocNode>();
    public QueueReader<AllocNode> allocNodeListener() { return newAllocNodes.reader(); }

    /** Finds the GlobalVarNode for the variable value, or returns null. */
    public GlobalVarNode findGlobalVarNode( Object value ) {
        if( opts.rta() ) {
            value = null;
        }
	return valToGlobalVarNode.get( value );
    }
    /** Finds the LocalVarNode for the variable value, or returns null. */
    public LocalVarNode findLocalVarNode( Object value ) {
        if( opts.rta() ) {
            value = null;
        } else if( value instanceof Local ) {
            return localToNodeMap.get( (Local) value );
        }
	return valToLocalVarNode.get( value );
    }
    /** Finds or creates the GlobalVarNode for the variable value, of type type. */
    public GlobalVarNode makeGlobalVarNode( Object value, Type type ) {
        if( opts.rta() ) {
            value = null;
            type = RefType.v("java.lang.Object");
        }
        GlobalVarNode ret = valToGlobalVarNode.get( value );
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
            LocalVarNode ret = localToNodeMap.get( val );
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
        LocalVarNode ret = valToLocalVarNode.get( value );
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
		if (from instanceof VarNode) {
			if (to instanceof VarNode) {
				return addSimpleEdge((VarNode) from, (VarNode) to);
			} else {
				return addStoreEdge((VarNode) from, (FieldRefNode) to);
			}
		} else if (from instanceof FieldRefNode) {
			return addLoadEdge((FieldRefNode) from, (VarNode) to);

		} else {
			return addAllocEdge((AllocNode) from, (VarNode) to);
		}
    }

    protected ChunkedQueue<Node> edgeQueue = new ChunkedQueue<Node>();
    public QueueReader<Node> edgeReader() { return edgeQueue.reader(); }

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
    public List<VarNode> getDereferences() {
        return dereferences;
    }

    public Map<Node, Tag> getNodeTags() {
        return nodeToTag;
    }

    private final ArrayNumberer<AllocNode> allocNodeNumberer = new ArrayNumberer<AllocNode>();
    public ArrayNumberer<AllocNode> getAllocNodeNumberer() { return allocNodeNumberer; }
    private final ArrayNumberer<VarNode> varNodeNumberer = new ArrayNumberer<VarNode>();
    public ArrayNumberer<VarNode> getVarNodeNumberer() { return varNodeNumberer; }
    private final ArrayNumberer<FieldRefNode> fieldRefNodeNumberer = new ArrayNumberer<FieldRefNode>();
    public ArrayNumberer<FieldRefNode> getFieldRefNodeNumberer() { return fieldRefNodeNumberer; }
    private final ArrayNumberer<AllocDotField> allocDotFieldNodeNumberer = new ArrayNumberer<AllocDotField>();
    public ArrayNumberer<AllocDotField> getAllocDotFieldNodeNumberer() { return allocDotFieldNodeNumberer; }


    /** Returns SparkOptions for this graph. */
    public SparkOptions getOpts() { return opts; }

    // Must be simple edges
	public Pair<Node, Node> addInterproceduralAssignment(Node from, Node to, Edge e) 
	{
		Pair<Node, Node> val = new Pair<Node, Node>(from, to);
		
		if ( runGeomPTA ) {
			Set<Edge> sets = assign2edges.get(val);
			if ( sets == null ) {
				sets = new HashSet<Edge>();
				assign2edges.put(val, sets);
			}
			sets.add(e);
		}
		
		return val;
	}
	
	public Set<Edge> lookupEdgesForAssignment(Pair<Node, Node> val)
	{
		return assign2edges.get(val);
	}
	
    final public void addCallTarget( Edge e ) {
        if( !e.passesParameters() ) return;
        MethodPAG srcmpag = MethodPAG.v( this, e.src() );
        MethodPAG tgtmpag = MethodPAG.v( this, e.tgt() );
        Pair<Node, Node> pval;
        
        if( e.isExplicit() || e.kind() == Kind.THREAD
        		 || e.kind() == Kind.ASYNCTASK ) {
            addCallTarget( srcmpag, tgtmpag, (Stmt) e.srcUnit(),
                           e.srcCtxt(), e.tgtCtxt(), e );
        }
        else if( e.kind() == Kind.EXECUTOR ) {
        	InvokeExpr ie = e.srcStmt().getInvokeExpr();
            boolean virtualCall = callAssigns.containsKey(ie);

        	Node parm = srcmpag.nodeFactory().getNode( ie.getArg(0) );
        	parm = srcmpag.parameterize( parm, e.srcCtxt() );
        	parm = parm.getReplacement();

        	Node thiz = tgtmpag.nodeFactory().caseThis();
        	thiz = tgtmpag.parameterize( thiz, e.tgtCtxt() );
        	thiz = thiz.getReplacement();

        	addEdge( parm, thiz );
        	pval = addInterproceduralAssignment(parm, thiz, e);
        	callAssigns.put(ie, pval);
        	callToMethod.put(ie, srcmpag.getMethod());

        	if (virtualCall && !virtualCallsToReceivers.containsKey(ie)) {
                virtualCallsToReceivers.put(ie, parm);
            }
        }
        else if( e.kind() == Kind.PRIVILEGED ) {
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
        	pval = addInterproceduralAssignment(parm, thiz, e);
        	callAssigns.put(ie, pval);
        	callToMethod.put(ie, srcmpag.getMethod());

        	if( e.srcUnit() instanceof AssignStmt ) {
        		AssignStmt as = (AssignStmt) e.srcUnit();

        		Node ret = tgtmpag.nodeFactory().caseRet();
        		ret = tgtmpag.parameterize( ret, e.tgtCtxt() );
        		ret = ret.getReplacement();

        		Node lhs = srcmpag.nodeFactory().getNode(as.getLeftOp());
        		lhs = srcmpag.parameterize( lhs, e.srcCtxt() );
        		lhs = lhs.getReplacement();

        		addEdge( ret, lhs );
        		pval = addInterproceduralAssignment(ret, lhs, e);
        		callAssigns.put(ie, pval);
        		callToMethod.put(ie, srcmpag.getMethod());
        	}
        }
        else if( e.kind() == Kind.FINALIZE ) {
        	Node srcThis = srcmpag.nodeFactory().caseThis();
        	srcThis = srcmpag.parameterize( srcThis, e.srcCtxt() );
        	srcThis = srcThis.getReplacement();

        	Node tgtThis = tgtmpag.nodeFactory().caseThis();
        	tgtThis = tgtmpag.parameterize( tgtThis, e.tgtCtxt() );
        	tgtThis = tgtThis.getReplacement();

        	addEdge( srcThis, tgtThis );
        	pval = addInterproceduralAssignment(srcThis, tgtThis, e);
        }
        else if( e.kind() == Kind.NEWINSTANCE ) {
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
                
        	pval = addInterproceduralAssignment(newObject, initThis, e);
        	callAssigns.put(s.getInvokeExpr(), pval);
        	callToMethod.put(s.getInvokeExpr(), srcmpag.getMethod());
        }
        else if( e.kind() == Kind.REFL_INVOKE ) {
        	// Flow (1) from first parameter of invoke(..) invocation
        	// to this of target, (2) from the contents of the second (array) parameter
        	// to all parameters of the target, and (3)  from return of target to the
        	// return of invoke(..)
            	
        	//(1)
        	InvokeExpr ie = e.srcStmt().getInvokeExpr();
        	Value arg0 = ie.getArg(0);
        	//if "null" is passed in, omit the edge
        	if(arg0!=NullConstant.v()) {
        		Node parm0 = srcmpag.nodeFactory().getNode( arg0 );
        		parm0 = srcmpag.parameterize( parm0, e.srcCtxt() );
        		parm0 = parm0.getReplacement();
	
        		Node thiz = tgtmpag.nodeFactory().caseThis();
        		thiz = tgtmpag.parameterize( thiz, e.tgtCtxt() );
        		thiz = thiz.getReplacement();
	
        		addEdge( parm0, thiz );
        		pval = addInterproceduralAssignment(parm0, thiz, e);
        		callAssigns.put(ie, pval);
        		callToMethod.put(ie, srcmpag.getMethod());
        	}

        	//(2)
        	Value arg1 = ie.getArg(1);
        	SootMethod tgt = e.getTgt().method();
        	//if "null" is passed in, or target has no parameters, omit the edge
        	if(arg1!=NullConstant.v() && tgt.getParameterCount()>0) {
        		Node parm1 = srcmpag.nodeFactory().getNode( arg1 );
        		parm1 = srcmpag.parameterize( parm1, e.srcCtxt() );
        		parm1 = parm1.getReplacement();
        		FieldRefNode parm1contents = makeFieldRefNode( (VarNode) parm1, ArrayElement.v() );
	                
        		for(int i=0;i<tgt.getParameterCount(); i++) {
        			//if no reference type, create no edge
        			if(!(tgt.getParameterType(i) instanceof RefLikeType)) continue;
	                	
        			Node tgtParmI = tgtmpag.nodeFactory().caseParm( i );
        			tgtParmI = tgtmpag.parameterize( tgtParmI, e.tgtCtxt() );
        			tgtParmI = tgtParmI.getReplacement();
	
        			addEdge( parm1contents, tgtParmI );
        			pval = addInterproceduralAssignment(parm1contents, tgtParmI, e);
        			callAssigns.put(ie, pval);
        		}
        	}

        	//(3)
        	//only create return edge if we are actually assigning the return value and
        	//the return type of the callee is actually a reference type
        	if( e.srcUnit() instanceof AssignStmt && (tgt.getReturnType() instanceof RefLikeType)) {
        		AssignStmt as = (AssignStmt) e.srcUnit();

        		Node ret = tgtmpag.nodeFactory().caseRet();
        		ret = tgtmpag.parameterize( ret, e.tgtCtxt() );
        		ret = ret.getReplacement();

        		Node lhs = srcmpag.nodeFactory().getNode(as.getLeftOp());
        		lhs = srcmpag.parameterize( lhs, e.srcCtxt() );
        		lhs = lhs.getReplacement();

        		addEdge( ret, lhs );
        		pval = addInterproceduralAssignment(ret, lhs, e);
        		callAssigns.put(ie, pval);
        	}
        }
        else if( e.kind() == Kind.REFL_CLASS_NEWINSTANCE || e.kind() == Kind.REFL_CONSTR_NEWINSTANCE) {
        	// (1) create a fresh node for the new object
        	// (2) create edge from this object to "this" of the constructor
        	// (3) if this is a call to Constructor.newInstance and not Class.newInstance,
        	//     create edges passing the contents of the arguments array of the call
        	//     to all possible parameters of the target
        	// (4) if we are inside an assign statement,
        	//     assign the fresh object from (1) to the LHS of the assign statement
            	
        	Stmt s = (Stmt) e.srcUnit();
        	InstanceInvokeExpr iie = (InstanceInvokeExpr) s.getInvokeExpr();

        	//(1)
        	Node cls = srcmpag.nodeFactory().getNode( iie.getBase() );
        	cls = srcmpag.parameterize( cls, e.srcCtxt() );
        	cls = cls.getReplacement();
        	if( cls instanceof ContextVarNode ) cls = findLocalVarNode( ((VarNode)cls).getVariable() );
                
        	VarNode newObject = makeGlobalVarNode( cls, RefType.v( "java.lang.Object" ) );
        	SootClass tgtClass = e.getTgt().method().getDeclaringClass();
        	RefType tgtType = tgtClass.getType();                
        	AllocNode site = makeAllocNode( new Pair<Node, SootClass>(cls, tgtClass), tgtType, null );
        	addEdge( site, newObject );

        	//(2)
        	Node initThis = tgtmpag.nodeFactory().caseThis();
        	initThis = tgtmpag.parameterize( initThis, e.tgtCtxt() );
        	initThis = initThis.getReplacement();
        	addEdge( newObject, initThis );
        	addInterproceduralAssignment(newObject, initThis, e);
                
        	//(3)
        	if(e.kind() == Kind.REFL_CONSTR_NEWINSTANCE) {
        		Value arg = iie.getArg(0);
        		SootMethod tgt = e.getTgt().method();
        		//if "null" is passed in, or target has no parameters, omit the edge
        		if(arg!=NullConstant.v() && tgt.getParameterCount()>0) {
        			Node parm0 = srcmpag.nodeFactory().getNode( arg );
        			parm0 = srcmpag.parameterize( parm0, e.srcCtxt() );
        			parm0 = parm0.getReplacement();
        			FieldRefNode parm1contents = makeFieldRefNode( (VarNode) parm0, ArrayElement.v() );
		                
        			for(int i=0;i<tgt.getParameterCount(); i++) {
        				//if no reference type, create no edge
        				if(!(tgt.getParameterType(i) instanceof RefLikeType)) continue;
		                	
        				Node tgtParmI = tgtmpag.nodeFactory().caseParm( i );
        				tgtParmI = tgtmpag.parameterize( tgtParmI, e.tgtCtxt() );
        				tgtParmI = tgtParmI.getReplacement();
		
        				addEdge( parm1contents, tgtParmI );
        				pval = addInterproceduralAssignment(parm1contents, tgtParmI, e);
        				callAssigns.put(iie, pval);
        			}
        		}
        	}
                
        	//(4)
        	if (s instanceof AssignStmt) {
        		AssignStmt as = (AssignStmt)s;
        		Node asLHS = srcmpag.nodeFactory().getNode(as.getLeftOp());
        		asLHS = srcmpag.parameterize( asLHS, e.srcCtxt());
        		asLHS = asLHS.getReplacement();
        		addEdge( newObject, asLHS);
        	}
                
        	pval = addInterproceduralAssignment(newObject, initThis, e);
        	callAssigns.put(s.getInvokeExpr(), pval);
        	callToMethod.put(s.getInvokeExpr(), srcmpag.getMethod());
        }
        else {
        	throw new RuntimeException( "Unhandled edge "+e );
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
                                     Context tgtContext,
                                     Edge e ) {
        MethodNodeFactory srcnf = srcmpag.nodeFactory();
        MethodNodeFactory tgtnf = tgtmpag.nodeFactory();
        InvokeExpr ie = s.getInvokeExpr();
        boolean virtualCall = callAssigns.containsKey(ie);
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
            Pair<Node, Node> pval = addInterproceduralAssignment(argNode, parm, e);
			callAssigns.put(ie, pval);
            callToMethod.put(ie, srcmpag.getMethod());
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
            Pair<Node, Node> pval = addInterproceduralAssignment(baseNode, thisRef, e);
			callAssigns.put(ie, pval);
            callToMethod.put(ie, srcmpag.getMethod());
            if (virtualCall && !virtualCallsToReceivers.containsKey(ie)) {
                virtualCallsToReceivers.put(ie, baseNode);
            }
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
                Pair<Node, Node> pval = addInterproceduralAssignment( retNode, destNode, e );
				callAssigns.put(ie, pval);
                callToMethod.put(ie, srcmpag.getMethod());
            }
        }
    }
    
    /**
     * Delete all the assignment edges.
     */
    public void cleanPAG()
    {
    	simple.clear();
    	load.clear();
    	store.clear();
    	alloc.clear();
    	simpleInv.clear();
    	loadInv.clear();
    	storeInv.clear();
    	allocInv.clear();
    }
    
    /* End of package methods. */

    protected SparkOptions opts;

    protected Map<VarNode, Object> simple = new HashMap<VarNode, Object>();
    protected Map<FieldRefNode, Object> load = new HashMap<FieldRefNode, Object>();
    protected Map<VarNode, Object> store = new HashMap<VarNode, Object>();
    protected Map<AllocNode, Object> alloc = new HashMap<AllocNode, Object>();

    protected Map<VarNode, Object> simpleInv = new HashMap<VarNode, Object>();
    protected Map<VarNode, Object> loadInv = new HashMap<VarNode, Object>();
    protected Map<FieldRefNode, Object> storeInv = new HashMap<FieldRefNode, Object>();
    protected Map<VarNode, Object> allocInv = new HashMap<VarNode, Object>();

    protected <K extends Node> boolean addToMap( Map<K, Object> m, K key, Node value ) {
	Object valueList = m.get( key );

	if( valueList == null ) {
	    m.put( key, valueList = new HashSet(4) );
	} else if( !(valueList instanceof Set) ) {
	    Node[] ar = (Node[]) valueList;
            HashSet<Node> vl = new HashSet<Node>(ar.length+4);
            m.put( key, vl );
            for (Node element : ar)
				vl.add( element );
            return vl.add( value );
	}
	return ((Set<Node>) valueList).add( value );
    }
	
    private boolean runGeomPTA = false;
    protected Map<Pair, Set<Edge>> assign2edges = new HashMap<Pair, Set<Edge>>();
    private final Map<Object, LocalVarNode> valToLocalVarNode = new HashMap<Object, LocalVarNode>(1000);
    private final Map<Object, GlobalVarNode> valToGlobalVarNode = new HashMap<Object, GlobalVarNode>(1000);
    private final Map<Object, AllocNode> valToAllocNode = new HashMap<Object, AllocNode>(1000);
    private OnFlyCallGraph ofcg;
    private final ArrayList<VarNode> dereferences = new ArrayList<VarNode>();
    protected TypeManager typeManager;
    private final LargeNumberedMap<Local, LocalVarNode> localToNodeMap =
    		new LargeNumberedMap<Local, LocalVarNode>( Scene.v().getLocalNumberer() );
    public int maxFinishNumber = 0;
    private Map<Node, Tag> nodeToTag;
    private final GlobalNodeFactory nodeFactory = new GlobalNodeFactory(this);
    public GlobalNodeFactory nodeFactory() { return nodeFactory; }
    public NativeMethodDriver nativeMethodDriver;

    public HashMultiMap<InvokeExpr, Pair<Node, Node>> callAssigns = new HashMultiMap<InvokeExpr, Pair<Node, Node>>();
    public Map<InvokeExpr, SootMethod> callToMethod = new HashMap<InvokeExpr, SootMethod>(); 
    public Map<InvokeExpr, Node> virtualCallsToReceivers = new HashMap<InvokeExpr, Node>();
    
}

