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
public abstract class AbsPAG
{ 
    protected Rsrc_dst simple;
    protected Rsrc_fld_dst load;
    protected Rsrc_fld_dst store;
    protected Robj_var alloc;
    protected Qsrc_dst simpleout;
    protected Qsrc_fld_dst loadout;
    protected Qsrc_fld_dst storeout;
    protected Qobj_var allocout;

    AbsPAG( 
            Rsrc_dst simple,
            Rsrc_fld_dst load,
            Rsrc_fld_dst store,
            Robj_var alloc,
            Qsrc_dst simpleout,
            Qsrc_fld_dst loadout,
            Qsrc_fld_dst storeout,
            Qobj_var allocout
        ) {
        this.simple = simple;
        this.load = load;
        this.store = store;
        this.alloc = alloc;
        this.simpleout = simpleout;
        this.loadout = loadout;
        this.storeout = storeout;
        this.allocout = allocout;
    }
    public abstract void update();

    public abstract Iterator simpleSources();
    public abstract Iterator loadSources();
    public abstract Iterator storeSources();
    public abstract Iterator allocSources();
    public abstract Iterator simpleInvSources();
    public abstract Iterator loadInvSources();
    public abstract Iterator storeInvSources();
    public abstract Iterator allocInvSources();
    
    public abstract Iterator simpleLookup( VarNode key );
    public abstract Iterator loadLookup( FieldRefNode key );
    public abstract Iterator storeLookup( VarNode key );
    public abstract Iterator allocLookup( AllocNode key );
    public abstract Iterator simpleInvLookup( VarNode key );
    public abstract Iterator loadInvLookup( VarNode key );
    public abstract Iterator storeInvLookup( FieldRefNode key );
    public abstract Iterator allocInvLookup( VarNode key );

    public abstract Rsrc_dst allSimple();
    public abstract Rsrc_fld_dst allLoad();
    public abstract Rsrc_fld_dst allStore();
    public abstract Robj_var allAlloc();
}

