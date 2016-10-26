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

package soot.jimple.spark.internal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.AnySubType;
import soot.ArrayType;
import soot.FastHierarchy;
import soot.G;
import soot.NullType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.TypeSwitch;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.util.ArrayNumberer;
import soot.util.BitVector;
import soot.util.LargeNumberedMap;
import soot.util.queue.QueueReader;

/** A map of bit-vectors representing subtype relationships.
 * @author Ondrej Lhotak
 * 
 *  @author Hamid A. Toussi (hamid2c@gmail.com):
 * Making TypeManager faster by making type masks during a
 * depth-first-traversal on the class hierarchy. First, type-masks of the
 * leaves of Class Hierarchy are created and then the type mask of each
 * type T is obtained by ORing type maks of Types sub-types and setting the
 * bit-numbers associated with Allocation Nodes of type T. The type-mask
 * of each interface is achieved by ORing the type-masks of its top-level
 * concrete implementers. In fact, Reference types are visited in
 * reversed-topological-order.
 */
public final class TypeManager {
    private Map<SootClass, List<AllocNode>> class2allocs = 
        new HashMap<SootClass, List<AllocNode>>(1024);
    private List<AllocNode> anySubtypeAllocs = new LinkedList<AllocNode>();

    public TypeManager( PAG pag ) {
        this.pag = pag;
    }
    public static boolean isUnresolved(Type type) {
    	if(type instanceof ArrayType) {
			ArrayType at = (ArrayType) type;
    		type = at.getArrayElementType();
    	}
        if( !(type instanceof RefType) ) return false;
        RefType rt = (RefType) type;
        if( !rt.hasSootClass() ) return true;
        SootClass cl = rt.getSootClass();
        return cl.resolvingLevel() < SootClass.HIERARCHY;
    }
    final public BitVector get( Type type ) {
        if( type == null ) return null;
        while(allocNodeListener.hasNext()) {
            AllocNode n = allocNodeListener.next();
            for( final Type t : Scene.v().getTypeNumberer()) {
                if( !(t instanceof RefLikeType) ) continue;
                if( t instanceof AnySubType ) continue;
                if( isUnresolved(t) ) continue;
                if( castNeverFails( n.getType(), t ) ) {
                    BitVector mask = typeMask.get( t );
                    if( mask == null ) {
                        typeMask.put( t, mask = new BitVector() );
                        for( final AllocNode an : pag.getAllocNodeNumberer()) {
                            if( castNeverFails( an.getType(), t ) ) {
                                mask.set( an.getNumber() );
                            }
                        }
                        continue;
                    }
                    mask.set( n.getNumber() );
                }
            }
        }
        BitVector ret = (BitVector) typeMask.get( type );
        if( ret == null && fh != null ) {
        	// If we have a phantom class and have no type mask, we assume that
        	// it is not cast-compatible to anything
    		SootClass curClass = ((RefType) type).getSootClass();
        	if (type instanceof RefType && curClass.isPhantom())
        		return new BitVector();
        	else {
        		// Scan through the hierarchy. We might have a phantom class higher up
        		while (curClass.hasSuperclass()) {
        			curClass = curClass.getSuperclass();
                	if (type instanceof RefType && curClass.isPhantom())
                		return new BitVector();
        		}
        		
        		throw new RuntimeException( "Type mask not found for type "+type );
        	}
        }
        return ret;
    }
    final public void clearTypeMask() {
        typeMask = null;
    }
    final public void makeTypeMask() {
        RefType.v( "java.lang.Class" );
        typeMask = new LargeNumberedMap<Type, BitVector>( Scene.v().getTypeNumberer() );
        if( fh == null ) return;

        int numTypes = Scene.v().getTypeNumberer().size();
        if( pag.getOpts().verbose() )
            G.v().out.println( "Total types: "+numTypes );
        // **
        initClass2allocs();
        makeClassTypeMask(Scene.v().getSootClass("java.lang.Object"));
        BitVector visitedTypes = new BitVector();
        {
        	Iterator<Type> it = typeMask.keyIterator();
        	while(it.hasNext()) {
        		Type t = it.next();
        		visitedTypes.set(t.getNumber());
        	}
        }
        // **
        ArrayNumberer<AllocNode> allocNodes = pag.getAllocNodeNumberer();
        for( Type t : Scene.v().getTypeNumberer()) {
            if( !(t instanceof RefLikeType) ) continue;
            if( t instanceof AnySubType ) continue;
            if( isUnresolved(t) ) continue;
            // **
            if (t instanceof RefType && !t.equals(RefType.v("java.lang.Object"))
                    && !t.equals(RefType.v("java.io.Serializable"))
                    && !t.equals(RefType.v("java.lang.Cloneable"))) {
                
                SootClass sc = ((RefType)t).getSootClass();
                if (sc.isInterface()) {
                    makeMaskOfInterface(sc);
                }
                if(!visitedTypes.get(t.getNumber()) && !((RefType)t).getSootClass().isPhantom()) {
                	makeClassTypeMask(((RefType)t).getSootClass());	
                }
                continue;
            }
            // **
            BitVector mask = new BitVector( allocNodes.size() );
            for( Node n : allocNodes) {
                if( castNeverFails( n.getType(), t ) ) {
                    mask.set( n.getNumber() );
                }
            }
            typeMask.put( t, mask );
        }

        allocNodeListener = pag.allocNodeListener();
    }

    private LargeNumberedMap<Type, BitVector> typeMask = null;
    final public boolean castNeverFails( Type src, Type dst ) {
        if( fh == null ) return true;
        if( dst == null ) return true;
        if( dst == src ) return true;
        if( src == null ) return false;
        if( dst.equals( src ) ) return true;
        if( src instanceof NullType ) return true;
        if( src instanceof AnySubType ) return true;
        if( dst instanceof NullType ) return false;
        if( dst instanceof AnySubType ) throw new RuntimeException( "oops src="+src+" dst="+dst );
        return fh.canStoreType( src, dst );
    }
    public void setFastHierarchy( FastHierarchy fh ) { this.fh = fh; }
    public FastHierarchy getFastHierarchy() { return fh; }

    protected FastHierarchy fh = null;
    protected PAG pag;
    protected QueueReader<AllocNode> allocNodeListener = null;
    // ** new methods
    private void initClass2allocs() {
        for ( AllocNode an : pag.getAllocNodeNumberer() ) {
            addAllocNode(an);
        }
    }
     
    final private void addAllocNode(final AllocNode alloc) {
        alloc.getType().apply(new TypeSwitch() {
            final public void caseRefType(RefType t) {              
                SootClass cl = t.getSootClass();
                List<AllocNode> list ;
                if ((list = class2allocs.get(cl)) == null) {
                    list = new LinkedList<AllocNode>();
                    class2allocs.put(cl, list);
                }
                list.add(alloc);
            }
            final public void caseAnySubType(AnySubType t) {
                anySubtypeAllocs.add(alloc);
            } 
        });
    }

    final private BitVector makeClassTypeMask(SootClass clazz) {
    	{
	    	BitVector cachedMask = typeMask.get(clazz.getType());
	    	if(cachedMask != null) {
	    		return cachedMask;
	    	}
    	}
    	
        int nBits = pag.getAllocNodeNumberer().size();
        final BitVector mask = new BitVector(nBits);
        
        List<AllocNode> allocs = null;
        if (clazz.isConcrete()) {
            allocs = class2allocs.get(clazz);
        }
        if (allocs != null){
            for (AllocNode an : allocs) {
                mask.set(an.getNumber());
            }
        }

        Collection<SootClass> subclasses = fh.getSubclassesOf(clazz);
        if (subclasses == Collections.EMPTY_LIST) {
            for (AllocNode an : anySubtypeAllocs) {
                mask.set(an.getNumber());
            }
            typeMask.put(clazz.getType(), mask);
            return mask;
        }
        
        for (SootClass subcl : subclasses) {
            mask.or(makeClassTypeMask(subcl));
        }
        
        typeMask.put(clazz.getType(), mask);
        return mask;
    }
    
    final private BitVector makeMaskOfInterface(SootClass interf) {
        if (!(interf.isInterface())) throw new RuntimeException();
        
        BitVector ret = new BitVector(pag.getAllocNodeNumberer().size());
        typeMask.put(interf.getType(), ret);
        Collection<SootClass> implementers = fh.getAllImplementersOfInterface(interf);
            
        for (SootClass impl : implementers) {
            BitVector other = (BitVector)typeMask.get(impl.getType());
            if (other == null) other = makeClassTypeMask(impl);
            ret.or(other);          
        }
        // I think, the following can be eliminated. It is added to make
        // type-masks exactly the same as the original type-masks
        if (implementers.size() == 0) {
            for (AllocNode an : anySubtypeAllocs) ret.set(an.getNumber());
        }
        return ret;
    }
    
}

