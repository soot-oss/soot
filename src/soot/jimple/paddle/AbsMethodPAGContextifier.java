/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

/** Instantiates the pointer flow edges of methods in specific contexts.
 * @author Ondrej Lhotak
 */
public abstract class AbsMethodPAGContextifier implements DepItem
{ 
    protected Rsrc_dst simple;
    protected Rsrc_fld_dst load;
    protected Rsrc_fld_dst store;
    protected Robj_var alloc;

    protected Rvar_method_type locals;
    protected Rvar_type globals;
    protected Robj_method_type localallocs;
    protected Robj_type globalallocs;

    protected Rctxt_method rcout;
    protected Rsrcm_stmt_kind_tgtm_src_dst parms;
    protected Rsrcm_stmt_kind_tgtm_src_dst rets;
    protected Rsrcc_srcm_stmt_kind_tgtc_tgtm calls;

    protected Qsrcc_src_dstc_dst csimple;
    protected Qsrcc_src_fld_dstc_dst cload;
    protected Qsrcc_src_fld_dstc_dst cstore;
    protected Qobjc_obj_varc_var calloc;

    public AbsMethodPAGContextifier(
        Rsrc_dst simple,
        Rsrc_fld_dst load,
        Rsrc_fld_dst store,
        Robj_var alloc,

        Rvar_method_type locals,
        Rvar_type globals,
        Robj_method_type localallocs,
        Robj_type globalallocs,

        Rctxt_method rcout,
        Rsrcm_stmt_kind_tgtm_src_dst parms,
        Rsrcm_stmt_kind_tgtm_src_dst rets,
        Rsrcc_srcm_stmt_kind_tgtc_tgtm calls,

        Qsrcc_src_dstc_dst csimple,
        Qsrcc_src_fld_dstc_dst cload,
        Qsrcc_src_fld_dstc_dst cstore,
        Qobjc_obj_varc_var calloc ) 
    {
        this.simple = simple;
        this.load = load;
        this.store = store;
        this.alloc = alloc;

        this.locals = locals;
        this.globals = globals;
        this.localallocs = localallocs;
        this.globalallocs = globalallocs;

        this.rcout = rcout;
        this.parms = parms;
        this.rets = rets;
        this.calls = calls;

        this.csimple = csimple;
        this.cload = cload;
        this.cstore = cstore;
        this.calloc = calloc;
    }
    public abstract boolean update();
}

