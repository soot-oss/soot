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

/** Pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class PAG implements PointsToAnalysis {
    /** Returns the set of objects reaching variable l before stmt in method. */
    public PointsToSet reachingObjects( SootMethod method, Stmt stmt,
                            Local l ) {
        VarNode n = findVarNode( l );
        if( n == null ) return EmptyPointsToSet.v();
        return n.getP2Set();
    }

    /** Returns SparkOptions for this graph. */
    public SparkOptions getOpts() { return opts; }
    /** Finds the AllocNode for the new expression newExpr, or returns null. */
    public AllocNode findAllocNode( Object newExpr ) {
	return (AllocNode) valToAllocNode.get( newExpr );
    }
    /** Finds or creates the AllocNode for the new expression newExpr,
     * of type type. */
    public AllocNode makeAllocNode( Object newExpr, Type type ) {
        if( opts.typesForSites() ) newExpr = type;
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
	return (VarNode) valToVarNode.get( value );
    }
    /** Finds or creates the VarNode for the variable value, of type type. */
    public VarNode makeVarNode( Object value, Type type, SootMethod method ) {
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
        FastHierarchy fh = PointsToSetInternal.getFastHierarchy();
	boolean ret = false;
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
		ret = addToMap( store, from, (FieldRefNode) to ) | ret;
		ret = addToMap( storeInv, to, from ) | ret;
	    }
	} else if( from instanceof FieldRefNode ) {
            if( !( to instanceof VarNode ) ) {
                throw new RuntimeException( "Attempt to add edge from "+
                    from+" to "+to );
            }
	    ret = addToMap( load, from, to ) | ret;
	    ret = addToMap( loadInv, to, from ) | ret;
	} else {
            if( !( from instanceof AllocNode ) 
            || !( to instanceof VarNode ) ) {
                throw new RuntimeException( "Attempt to add edge from "+
                    from+" to "+to );
            }
            if( fh == null || to.getType() == null 
            || fh.canStoreType( to.getType(), from.getType() ) ) {
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
        if( !opts.ignoreTypesEntirely() ) {
            PointsToSetInternal.setFastHierarchy(
                    Scene.v().getOrMakeFastHierarchy() );
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

    public void setOnFlyCallGraph( OnFlyCallGraph ofcg ) { this.ofcg = ofcg; }
    public OnFlyCallGraph getOnFlyCallGraph() { return ofcg; }

    /* End of public methods. Nothing to see here; move along. */

    /** Node uses this to notify PAG that n2 has been merged into n1. */
    void mergedWith( Node n1, Node n2 ) {
        Map[] maps = { simple, alloc, store, load,
            simpleInv, allocInv, storeInv, loadInv };
        for( int i = 0; i < maps.length; i++ ) {
            Map m = maps[i];
            if( !m.keySet().contains( n2 ) ) continue;
            HashSet s = new HashSet();
            Object[] os = { m.get( n1 ), m.get( n2 ) };
            for( int j = 0; j < os.length; j++ ) {
                Object o = os[j];
                if( o == null ) continue;
                if( o instanceof Set ) {
                    s.addAll( (Set) o );
                } else {
                    Object[] ar = (Object[]) o;
                    for( int k = 0; k < ar.length; k++ ) {
                        s.add( ar[k] );
                    }
                }
            }
            m.put( n1, s );
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

    /* End of package methods. Nothing to see here; move along. */

    protected SparkOptions opts;

    protected Map simple = new HashMap();
    protected Map load = new HashMap();
    protected Map store = new HashMap();
    protected Map alloc = new HashMap();

    protected Map simpleInv = new HashMap();
    protected Map loadInv = new HashMap();
    protected Map storeInv = new HashMap();
    protected Map allocInv = new HashMap();

    protected boolean addToMap( Map m, Object key, Object value ) {
	boolean ret = false;
	Object valueList = m.get( key );

	if( valueList == null ) {
	    m.put( key, valueList = new HashSet(4) );
	} else if( !(valueList instanceof Set) ) {
	    Node[] ar = (Node[]) valueList;
	    m.put( key, valueList = new HashSet( ar.length ) );
	    for( int i = 0; i < ar.length; i++ ) ( (Set) valueList ).add( ar );

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
	    m.put( key, valueList = 
		    (Node[]) ( (Set) valueList ).toArray( EMPTY_NODE_ARRAY ) );
	}
	Node[] ret = (Node[]) valueList;
	for( int i = 0; i < ret.length; i++ ) {
	    Node reti = ret[i];
	    Node rep = reti.getReplacement();
	    if( rep != reti || rep == key ) {
		HashSet s = new HashSet( ret.length * 2 );
		for( int j = 0; j < i; j++ ) s.add( ret[j] );
		for( int j = i; j < ret.length; j++ ) {
                    rep = ret[j].getReplacement();
                    if( rep != key ) {
                        s.add( rep );
                    }
		}
		m.put( key, ret = (Node[]) s.toArray( EMPTY_NODE_ARRAY ) );
	    }
	}
	return ret;
    }
    protected Map valToVarNode = new HashMap(1000);
    protected Map valToAllocNode = new HashMap(1000);
    protected P2SetFactory setFactory;
    protected OnFlyCallGraph ofcg;
}

