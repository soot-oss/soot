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

/** Creates inter-procedural pointer assignment edges.
 * @author Ondrej Lhotak
 */
public abstract class AbsPAGBuilder implements DepItem
{ 
    protected Rsrcc_srcm_stmt_kind_tgtc_tgtm in;
    protected Qsrcc_src_dstc_dst simple;
    protected Qsrcc_src_fld_dstc_dst load;
    protected Qsrcc_src_dstc_dst_fld store;
    protected Qobjc_obj_varc_var alloc;

    AbsPAGBuilder( 
        Rsrcc_srcm_stmt_kind_tgtc_tgtm in,
        Qsrcc_src_dstc_dst simple,
        Qsrcc_src_fld_dstc_dst load,
        Qsrcc_src_dstc_dst_fld store,
        Qobjc_obj_varc_var alloc ) {
        this.in = in;
        this.simple = simple;
        this.load = load;
        this.store = store;
        this.alloc = alloc;
    }
    public abstract boolean update();
}


