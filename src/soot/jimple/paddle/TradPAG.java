/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.paddle;
import soot.*;
import soot.jimple.paddle.queue.*;
import java.util.*;

/** Stores the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class TradPAG extends AbsPAG
{ 
    TradPAG( 
            Rsrc_dst simple,
            Rsrc_fld_dst load,
            Rsrc_fld_dst store,
            Robj_var alloc,
            Qsrc_dst simpleout,
            Qsrc_fld_dst loadout,
            Qsrc_fld_dst storeout,
            Qobj_var allocout
        ) {
        super( simple, load, store, alloc, simpleout, loadout, storeout, allocout );
    }
    public void update() {
        for( Iterator tIt = simple.iterator(); tIt.hasNext(); ) {
            final Rsrc_dst.Tuple t = (Rsrc_dst.Tuple) tIt.next();
            if( add( t.src(), t.dst(), simpleMap ) ) {
                simpleout.add( t.src(), t.dst() );
            }
        }
        for( Iterator tIt = load.iterator(); tIt.hasNext(); ) {
            final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
            if( add( t.src().dot( t.fld() ), t.dst(), loadMap ) ) {
                loadout.add( t.src(), t.fld(), t.dst() );
            }
        }
        for( Iterator tIt = store.iterator(); tIt.hasNext(); ) {
            final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
            if( add( t.src(), t.dst().dot( t.fld() ), storeMap ) ) {
                storeout.add( t.src(), t.fld(), t.dst() );
            }
        }
        for( Iterator tIt = alloc.iterator(); tIt.hasNext(); ) {
            final Robj_var.Tuple t = (Robj_var.Tuple) tIt.next();
            if( add( t.obj(), t.var(), allocMap ) ) {
                allocout.add( t.obj(), t.var() );
            }
        }
    }

    public Iterator simpleSources() {
        return simpleMap.fwd.keySet().iterator();
    }
    public Iterator loadSources() {
        return loadMap.fwd.keySet().iterator();
    }
    public Iterator storeSources() {
        return storeMap.fwd.keySet().iterator();
    }
    public Iterator allocSources() {
        return allocMap.fwd.keySet().iterator();
    }
    public Iterator simpleInvSources() {
        return simpleMap.inv.keySet().iterator();
    }
    public Iterator loadInvSources() {
        return loadMap.inv.keySet().iterator();
    }
    public Iterator storeInvSources() {
        return storeMap.inv.keySet().iterator();
    }
    public Iterator allocInvSources() {
        return allocMap.inv.keySet().iterator();
    }
    
    public Iterator simpleLookup( VarNode key ) {
        return simpleMap.fwd.iterator(key);
    }
    public Iterator loadLookup( FieldRefNode key ) {
        return loadMap.fwd.iterator(key);
    }
    public Iterator storeLookup( VarNode key ) {
        return storeMap.fwd.iterator(key);
    }
    public Iterator allocLookup( AllocNode key ) {
        return allocMap.fwd.iterator(key);
    }
    public Iterator simpleInvLookup( VarNode key ) {
        return simpleMap.inv.iterator(key);
    }
    public Iterator loadInvLookup( VarNode key ) {
        return loadMap.inv.iterator(key);
    }
    public Iterator storeInvLookup( FieldRefNode key ) {
        return storeMap.inv.iterator(key);
    }
    public Iterator allocInvLookup( VarNode key ) {
        return allocMap.inv.iterator(key);
    }

    public Rsrc_dst allSimple() {
        Qsrc_dst q = new Qsrc_dstTrad("allsimple");
        Rsrc_dst ret = q.reader("allsimple");
        for( Iterator srcIt = simpleSources(); srcIt.hasNext(); ) {
            final VarNode src = (VarNode) srcIt.next();
            Iterator dstIt = simpleLookup(src);
            while( dstIt.hasNext() ) {
                final VarNode dst = (VarNode) dstIt.next();
                q.add( src, dst );
            }
        }
        return ret;
    }
    public Rsrc_fld_dst allLoad() {
        Qsrc_fld_dst q = new Qsrc_fld_dstTrad("allload");
        Rsrc_fld_dst ret = q.reader("allload");
        for( Iterator srcIt = loadSources(); srcIt.hasNext(); ) {
            final FieldRefNode src = (FieldRefNode) srcIt.next();
            Iterator dstIt = loadLookup(src);
            while( dstIt.hasNext() ) {
                final VarNode dst = (VarNode) dstIt.next();
                q.add( src.getBase(), src.getField(), dst );
            }
        }
        return ret;
    }
    public Rsrc_fld_dst allStore() {
        Qsrc_fld_dst q = new Qsrc_fld_dstTrad("allstore");
        Rsrc_fld_dst ret = q.reader("allstore");
        for( Iterator srcIt = storeSources(); srcIt.hasNext(); ) {
            final VarNode src = (VarNode) srcIt.next();
            Iterator dstIt = storeLookup(src);
            while( dstIt.hasNext() ) {
                final FieldRefNode dst = (FieldRefNode) dstIt.next();
                q.add( src, dst.getField(), dst.getBase() );
            }
        }
        return ret;
    }
    public Robj_var allAlloc() {
        Qobj_var q = new Qobj_varTrad("allalloc");
        Robj_var ret = q.reader("allalloc");
        for( Iterator objIt = allocSources(); objIt.hasNext(); ) {
            final AllocNode obj = (AllocNode) objIt.next();
            Iterator varIt = allocLookup(obj);
            while( varIt.hasNext() ) {
                final VarNode var = (VarNode) varIt.next();
                q.add( obj, var );
            }
        }
        return ret;
    }

    private boolean add( Node src, Node dst, EdgePair map ) {
        if( !allEdges.add( new Pair( src, dst ) ) ) return false;
        map.fwd.add( src, dst );
        map.inv.add( dst, src );
        return true;
    }

    private Set allEdges = new HashSet();

    private static class Pair {
        public Pair( Node src, Node dst ) {
            this.src = src;
            this.dst = dst;
        }
        private Node src;
        private Node dst;
        public int hashCode() { return src.hashCode() + dst.hashCode(); }
        public boolean equals( Object o ) {
            if( !( o instanceof Pair ) ) return false;
            Pair p = (Pair) o;
            if( !src.equals(p.src) ) return false;
            if( !dst.equals(p.dst) ) return false;
            return true;
        }
    }
    private static class EdgeMap extends HashMap {
        public void add( Node key, Node val ) {
            ArrayList bucket = (ArrayList) get(key);
            if( bucket == null ) {
                bucket = new ArrayList();
                put( key, bucket );
            }
            bucket.add( val );
        }
        public Iterator iterator( Node key ) {
            Object o = get(key);
            if( o == null ) return new Iterator() {
                public boolean hasNext() { return false; }
                public Object next() { throw new NoSuchElementException(); }
                public void remove() { throw new UnsupportedOperationException(); }
            };
            ArrayList ar = (ArrayList) o;
            return ar.iterator();
        }
    }
    private static class EdgePair {
        public EdgeMap fwd = new EdgeMap();
        public EdgeMap inv = new EdgeMap();
    }
    private EdgePair simpleMap = new EdgePair();
    private EdgePair loadMap = new EdgePair();
    private EdgePair storeMap = new EdgePair();
    private EdgePair allocMap = new EdgePair();
}


