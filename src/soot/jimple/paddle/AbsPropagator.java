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

/** Propagates points-to sets along the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public abstract class AbsPropagator
{ 
    protected Rsrc_dst newSimple;
    protected Rsrc_fld_dst newLoad;
    protected Rsrc_fld_dst newStore;
    protected Robj_var newAlloc;
    protected Qvar_obj ptout;
    protected AbsPAG pag;

    AbsPropagator( Rsrc_dst simple,
            Rsrc_fld_dst load,
            Rsrc_fld_dst store,
            Robj_var alloc,
            Qvar_obj ptout,
            AbsPAG pag
        ) {
        this.newSimple = simple;
        this.newLoad = load;
        this.newStore = store;
        this.newAlloc = alloc;
        this.ptout = ptout;
        this.pag = pag;
    }
    public abstract void update();
}


