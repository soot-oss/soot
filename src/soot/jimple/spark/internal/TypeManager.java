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
import java.util.*;
import soot.Type;

/** A map of bit-vectors representing subtype relationships.
 * @author Ondrej Lhotak
 */
public final class TypeManager {
    final public long[] get( Type type ) {
        return (long[]) typeMask.get( type );
    }
    final public void clearTypeMask() {
        typeMask = null;
        typeToNum = null;
        typeCache = null;
    }
    private void addType( Type t ) {
        if( t == null ) return;
        Integer i = (Integer) typeToNum.get( t );
        if( i == null ) {
            t.typeNum = nextTypeNum; 
            i = new Integer( nextTypeNum++ );
            typeToNum.put( t, i );
        } else {
            t.typeNum = i.intValue();
        }
    }
    final public boolean castNeverFails( Type src, Type dst ) {
        if( typeCache == null ) return true;
        if( dst == null ) return true;
        if( dst == src ) return true;
        if( src == null ) return false;
        if( dst.equals( src ) ) return true;
        return 
            (typeCache[src.typeNum][dst.typeNum/32] & (1<<(dst.typeNum%32))) != 0;
    }
    final public void makeTypeMask( PAG pag ) {
        if( typeToNum != null ) throw new RuntimeException( "oops" );
        typeToNum = new HashMap();
        nextTypeNum = 1;
        HashSet declaredTypes = new HashSet();
        for( Iterator nIt = pag.allVarNodes().iterator(); nIt.hasNext(); ) {
            final VarNode n = (VarNode) nIt.next();
            addType( n.getType() );
            declaredTypes.add( n.getType() );
        }
        for( Iterator nIt = pag.allocSources().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            addType( n.getType() );
        }
        
        SIZE = pag.getNumAllocNodes()/64+2;
        nodes = new Node[ SIZE * 64 ];

        typeMask = new HashMap();
        if( fh == null ) return;

        if( pag.getOpts().verbose() )
            System.out.println( "Total types: "+nextTypeNum );
        typeCache = new int[nextTypeNum+1][nextTypeNum/32+2];

        for( Iterator childIt = typeToNum.keySet().iterator(); childIt.hasNext(); ) {

            final Type child = (Type) childIt.next();
            for( Iterator parentIt = typeToNum.keySet().iterator(); parentIt.hasNext(); ) {
                final Type parent = (Type) parentIt.next();
                if( (child instanceof NullType || child instanceof AnyType ) ||
                        ( !(parent instanceof NullType || parent instanceof AnyType ) 
                          &&  fh.canStoreType( child, parent ) ) ) {
                    typeCache[child.typeNum][parent.typeNum/32] 
                        |= (1<<(parent.typeNum%32));
                }
            }
        }

        for( Iterator tIt = declaredTypes.iterator(); tIt.hasNext(); ) {

            final Type t = (Type) tIt.next();
            long[] mask = new long[SIZE];
            for( Iterator nIt = pag.allocSources().iterator(); nIt.hasNext(); ) {
                final Node n = (Node) nIt.next();
                if( castNeverFails( n.getType(), t ) ) {
                    int id = -n.getId();
                    mask[id/64] |= 1L<<(id%64);
                }
            }
            typeMask.put( t, mask );
        }
    }

    public void setFastHierarchy( FastHierarchy fh ) { this.fh = fh; }
    public FastHierarchy getFastHierarchy() { return fh; }

    private int SIZE = 0;
    private Node[] nodes = null;
    private HashMap typeMask = null;
    private HashMap typeToNum = null;
    private int nextTypeNum = 1;
    private int[][] typeCache = null;
    private FastHierarchy fh = null;
}

