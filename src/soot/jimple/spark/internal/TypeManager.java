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

/** A map of bit-vectors representing subtype relationships.
 * @author Ondrej Lhotak
 */
public final class TypeManager {
    final public BitSet get( Type type ) {
        if( type == null ) return null;
        while(true) {
            AllocNode n = (AllocNode) allocNodeListener.next();
            if( n == null ) break;
            for( Iterator tIt = Scene.v().getTypeNumberer().iterator(); tIt.hasNext(); ) {
                final Type t = (Type) tIt.next();
                if( !(t instanceof RefLikeType) ) continue;
                if( t instanceof AnySubType ) continue;
                if( castNeverFails( n.getType(), t ) ) {
                    BitSet mask = (BitSet) typeMask.get( t );
                    if( mask == null ) {
                        typeMask.put( t, mask = new BitSet() );
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
        BitSet ret = (BitSet) typeMask.get( type );
        if( ret == null && fh != null ) throw new RuntimeException( "oops"+type );
        return ret;
    }
    final public void clearTypeMask() {
        typeMask = null;
    }
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
    final public void makeTypeMask( PAG pag ) {
        RefType.v( "java.lang.Class" );
        this.pag = pag;
        typeMask = new LargeNumberedMap( Scene.v().getTypeNumberer() );
        if( fh == null ) return;

        int numTypes = Scene.v().getTypeNumberer().size();
        if( pag.getOpts().verbose() )
            System.out.println( "Total types: "+numTypes );

        Numberer allocNodes = pag.getAllocNodeNumberer();
        for( Iterator tIt = Scene.v().getTypeNumberer().iterator(); tIt.hasNext(); ) {
            final Type t = (Type) tIt.next();
            if( !(t instanceof RefLikeType) ) continue;
            if( t instanceof AnySubType ) continue;
            BitSet mask = new BitSet( allocNodes.size() );
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

    public void setFastHierarchy( FastHierarchy fh ) { this.fh = fh; }
    public FastHierarchy getFastHierarchy() { return fh; }

    private LargeNumberedMap typeMask = null;
    private FastHierarchy fh = null;
    private QueueReader allocNodeListener = null;
    private PAG pag;
}

