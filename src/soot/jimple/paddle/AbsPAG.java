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
public abstract class AbsPAG implements DepItem
{ 
    protected Rsrcc_src_dstc_dst simple;
    protected Rsrcc_src_fld_dstc_dst load;
    protected Rsrcc_src_fld_dstc_dst store;
    protected Robjc_obj_varc_var alloc;

    AbsPAG( Rsrcc_src_dstc_dst simple, Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_fld_dstc_dst store, Robjc_obj_varc_var alloc ) {
        this.simple = simple;
        this.load = load;
        this.store = store;
        this.alloc = alloc;
    }
    public abstract boolean update();

    public abstract Iterator simpleSources();
    public abstract Iterator loadSources();
    public abstract Iterator storeSources();
    public abstract Iterator allocSources();
    public abstract Iterator simpleInvSources();
    public abstract Iterator loadInvSources();
    public abstract Iterator storeInvSources();
    public abstract Iterator allocInvSources();
    
    public Iterator simpleLookup( Context ctxt, VarNode key )
    { return simpleLookup( ContextVarNode.make(ctxt, key) ); }
    public Iterator loadLookup( Context ctxt, FieldRefNode key )
    { return loadLookup( ContextFieldRefNode.make(ctxt, key) ); }
    public Iterator storeLookup( Context ctxt, VarNode key )
    { return storeLookup( ContextVarNode.make(ctxt, key) ); }
    public Iterator allocLookup( Context ctxt, AllocNode key )
    { return allocLookup( ContextAllocNode.make(ctxt, key) ); }
    public Iterator simpleInvLookup( Context ctxt, VarNode key )
    { return simpleInvLookup( ContextVarNode.make(ctxt, key) ); }
    public Iterator loadInvLookup( Context ctxt, VarNode key )
    { return loadInvLookup( ContextVarNode.make(ctxt, key) ); }
    public Iterator storeInvLookup( Context ctxt, FieldRefNode key )
    { return storeInvLookup( ContextFieldRefNode.make(ctxt, key) ); }
    public Iterator allocInvLookup( Context ctxt, VarNode key )
    { return allocInvLookup( ContextVarNode.make(ctxt, key) ); }

    public Iterator simpleLookup( ContextVarNode key ) 
    { return simpleLookup( key.ctxt(), key.var() ); }
    public Iterator loadLookup( ContextFieldRefNode key )
    { return loadLookup( key.ctxt(), key.frn() ); }
    public Iterator storeLookup( ContextVarNode key )
    { return storeLookup( key.ctxt(), key.var() ); }
    public Iterator allocLookup( ContextAllocNode key )
    { return allocLookup( key.ctxt(), key.obj() ); }
    public Iterator simpleInvLookup( ContextVarNode key )
    { return simpleInvLookup( key.ctxt(), key.var() ); }
    public Iterator loadInvLookup( ContextVarNode key )
    { return loadInvLookup( key.ctxt(), key.var() ); }
    public Iterator storeInvLookup( ContextFieldRefNode key )
    { return storeInvLookup( key.ctxt(), key.frn() ); }
    public Iterator allocInvLookup( ContextVarNode key )
    { return allocInvLookup( key.ctxt(), key.var() ); }

    public abstract Rsrcc_src_dstc_dst allSimple();
    public abstract Rsrcc_src_fld_dstc_dst allLoad();
    public abstract Rsrcc_src_fld_dstc_dst allStore();
    public abstract Robjc_obj_varc_var allAlloc();
}

