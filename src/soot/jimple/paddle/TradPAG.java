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
    TradPAG( Rsrcc_src_dstc_dst simple, Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_fld_dstc_dst store, Robjc_obj_varc_var alloc ) {
        super( simple, load, store, alloc);
    }
    public boolean update() {
        boolean ret = false;
        for( Iterator tIt = simple.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_dstc_dst.Tuple t = (Rsrcc_src_dstc_dst.Tuple) tIt.next();
            if( add( t.srcc(), t.src(), t.dstc(), t.dst(), simpleMap ) ) ret = true;
        }
        for( Iterator tIt = load.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_fld_dstc_dst.Tuple t = (Rsrcc_src_fld_dstc_dst.Tuple) tIt.next();
            if( add( t.srcc(), t.src().dot( t.fld() ), t.dstc(), t.dst(), loadMap ) ) ret = true;
        }
        for( Iterator tIt = store.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_fld_dstc_dst.Tuple t = (Rsrcc_src_fld_dstc_dst.Tuple) tIt.next();
            if( add( t.srcc(), t.src(), t.dstc(), t.dst().dot( t.fld() ), storeMap ) ) ret = true;
        }
        for( Iterator tIt = alloc.iterator(); tIt.hasNext(); ) {
            final Robjc_obj_varc_var.Tuple t = (Robjc_obj_varc_var.Tuple) tIt.next();
            if( add( t.objc(), t.obj(), t.varc(), t.var(), allocMap ) ) ret = true;
        }
        return ret;
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
    
    public Iterator simpleLookup( ContextVarNode key ) {
        return simpleMap.fwd.iterator(key);
    }
    public Iterator loadLookup( ContextFieldRefNode key ) {
        return loadMap.fwd.iterator(key);
    }
    public Iterator storeLookup( ContextVarNode key ) {
        return storeMap.fwd.iterator(key);
    }
    public Iterator allocLookup( ContextAllocNode key ) {
        return allocMap.fwd.iterator(key);
    }
    public Iterator simpleInvLookup( ContextVarNode key ) {
        return simpleMap.inv.iterator(key);
    }
    public Iterator loadInvLookup( ContextVarNode key ) {
        return loadMap.inv.iterator(key);
    }
    public Iterator storeInvLookup( ContextFieldRefNode key ) {
        return storeMap.inv.iterator(key);
    }
    public Iterator allocInvLookup( ContextVarNode key ) {
        return allocMap.inv.iterator(key);
    }

    public Rsrcc_src_dstc_dst allSimple() {
        Qsrcc_src_dstc_dst q = new Qsrcc_src_dstc_dstTrad("allsimple");
        Rsrcc_src_dstc_dst ret = q.reader("allsimple");
        for( Iterator srcIt = simpleSources(); srcIt.hasNext(); ) {
            final ContextVarNode src = (ContextVarNode) srcIt.next();
            Iterator dstIt = simpleLookup(src);
            while( dstIt.hasNext() ) {
                final ContextVarNode dst = (ContextVarNode) dstIt.next();
                q.add( src.ctxt(), src.var(), dst.ctxt(), dst.var() );
            }
        }
        return ret;
    }
    public Rsrcc_src_fld_dstc_dst allLoad() {
        Qsrcc_src_fld_dstc_dst q = new Qsrcc_src_fld_dstc_dstTrad("allload");
        Rsrcc_src_fld_dstc_dst ret = q.reader("allload");
        for( Iterator srcIt = loadSources(); srcIt.hasNext(); ) {
            final ContextFieldRefNode src = (ContextFieldRefNode) srcIt.next();
            Iterator dstIt = loadLookup(src);
            while( dstIt.hasNext() ) {
                final ContextVarNode dst = (ContextVarNode) dstIt.next();
                q.add( src.ctxt(), src.base().var(), src.field(), dst.ctxt(), dst.var() );
            }
        }
        return ret;
    }
    public Rsrcc_src_fld_dstc_dst allStore() {
        Qsrcc_src_fld_dstc_dst q = new Qsrcc_src_fld_dstc_dstTrad("allstore");
        Rsrcc_src_fld_dstc_dst ret = q.reader("allstore");
        for( Iterator srcIt = storeSources(); srcIt.hasNext(); ) {
            final ContextVarNode src = (ContextVarNode) srcIt.next();
            Iterator dstIt = storeLookup(src);
            while( dstIt.hasNext() ) {
                final ContextFieldRefNode dst = (ContextFieldRefNode) dstIt.next();
                q.add( src.ctxt(), src.var(), dst.field(), dst.ctxt(), dst.base().var() );
            }
        }
        return ret;
    }
    public Robjc_obj_varc_var allAlloc() {
        Qobjc_obj_varc_var q = new Qobjc_obj_varc_varTrad("allalloc");
        Robjc_obj_varc_var ret = q.reader("allalloc");
        for( Iterator objIt = allocSources(); objIt.hasNext(); ) {
            final ContextAllocNode obj = (ContextAllocNode) objIt.next();
            Iterator varIt = allocLookup(obj);
            while( varIt.hasNext() ) {
                final ContextVarNode var = (ContextVarNode) varIt.next();
                q.add( obj.ctxt(), obj.obj(), var.ctxt(), var.var() );
            }
        }
        return ret;
    }

    private boolean add( Context srcc, Node src, Context dstc, Node dst, EdgePair map ) {
        ContextNode srccn = ContextNode.make(srcc, src);
        ContextNode dstcn = ContextNode.make(dstc, dst);
        if( !allEdges.add( new Pair( srccn, dstcn ) ) ) return false;
        map.fwd.add( srccn, dstcn );
        map.inv.add( dstcn, srccn );
        return true;
    }

    private Set allEdges = new HashSet();

    private static class Pair {
        public Pair( ContextNode src, ContextNode dst ) {
            this.src = src;
            this.dst = dst;
        }
        private ContextNode src;
        private ContextNode dst;
        public int hashCode() { 
            return src.hashCode() + dst.hashCode();
        }
        public boolean equals( Object o ) {
            if( !( o instanceof Pair ) ) return false;
            Pair p = (Pair) o;
            if( !src.equals(p.src) ) return false;
            if( !dst.equals(p.dst) ) return false;
            return true;
        }
    }
    private static class EdgeMap extends HashMap {
        public void add( ContextNode key, ContextNode val ) {
            ArrayList bucket = (ArrayList) get(key);
            if( bucket == null ) {
                bucket = new ArrayList();
                put( key, bucket );
            }
            bucket.add( val );
        }
        public Iterator iterator( ContextNode key ) {
            Object o = get(key);
            if( o == null ) {
                return new Iterator() {
                    public boolean hasNext() { return false; }
                    public Object next() { throw new NoSuchElementException(); }
                    public void remove() { throw new UnsupportedOperationException(); }
                };
            }
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


