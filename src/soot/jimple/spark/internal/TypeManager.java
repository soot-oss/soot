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
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.*;
import soot.util.*;
import java.util.Iterator;
import soot.util.queue.*;
import soot.Type;
import soot.options.SparkOptions;

/** A map of bit-vectors representing subtype relationships.
 * @author Ondrej Lhotak
 */
public final class TypeManager {
    public TypeManager( PAG pag ) {
        this.pag = pag;
    }
    public static boolean isUnresolved(Type type) {
        if( !(type instanceof RefType) ) return false;
        RefType rt = (RefType) type;
        if( !rt.hasSootClass() ) return true;
        SootClass cl = rt.getSootClass();
        return cl.resolvingLevel() < SootClass.HIERARCHY;
    }
    final public BitVector get( Type type ) {
        if( type == null ) return null;
        while(allocNodeListener.hasNext()) {
            AllocNode n = (AllocNode) allocNodeListener.next();
            for( Iterator tIt = Scene.v().getTypeNumberer().iterator(); tIt.hasNext(); ) {
                final Type t = (Type) tIt.next();
                if( !(t instanceof RefLikeType) ) continue;
                if( t instanceof AnySubType ) continue;
                if( isUnresolved(t) ) continue;
                if( castNeverFails( n.getType(), t ) ) {
                    BitVector mask = (BitVector) typeMask.get( t );
                    if( mask == null ) {
                        typeMask.put( t, mask = new BitVector() );
                        for( Iterator anIt = pag.getAllocNodeNumberer().iterator(); anIt.hasNext(); ) {
                            final AllocNode an = (AllocNode) anIt.next();
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
        if( ret == null && fh != null ) throw new RuntimeException( "oops"+type );
        return ret;
    }
    final public void clearTypeMask() {
        typeMask = null;
    }
    final public void makeTypeMask() {
        RefType.v( "java.lang.Class" );
        typeMask = new LargeNumberedMap( Scene.v().getTypeNumberer() );
        if( fh == null ) return;

        int numTypes = Scene.v().getTypeNumberer().size();
        if( pag.getOpts().verbose() )
            G.v().out.println( "Total types: "+numTypes );

        ArrayNumberer allocNodes = pag.getAllocNodeNumberer();
        for( Iterator tIt = Scene.v().getTypeNumberer().iterator(); tIt.hasNext(); ) {
            final Type t = (Type) tIt.next();
            if( !(t instanceof RefLikeType) ) continue;
            if( t instanceof AnySubType ) continue;
            if( isUnresolved(t) ) continue;
            BitVector mask = new BitVector( allocNodes.size() );
            for( Iterator nIt = allocNodes.iterator(); nIt.hasNext(); ) {
                final Node n = (Node) nIt.next();
                if( castNeverFails( n.getType(), t ) ) {
                    mask.set( n.getNumber() );
                }
            }
            typeMask.put( t, mask );
        }

        allocNodeListener = pag.allocNodeListener();
    }

    private LargeNumberedMap typeMask = null;
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
    protected QueueReader allocNodeListener = null;
}

