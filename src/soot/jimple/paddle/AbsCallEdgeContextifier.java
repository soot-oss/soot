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
import soot.util.*;
import soot.jimple.paddle.queue.*;
import java.util.*;

/** Instantiates the pointer flow edges of method calls in specific contexts.
 * @author Ondrej Lhotak
 */
public abstract class AbsCallEdgeContextifier implements DepItem
{ 
    protected Rsrcm_stmt_kind_tgtm_src_dst parms;
    protected Rsrcm_stmt_kind_tgtm_src_dst rets;
    protected Rsrcc_srcm_stmt_kind_tgtc_tgtm calls;
    protected Qsrcc_src_dstc_dst csimple;
    public AbsCallEdgeContextifier(
        Rsrcm_stmt_kind_tgtm_src_dst parms,
        Rsrcm_stmt_kind_tgtm_src_dst rets,
        Rsrcc_srcm_stmt_kind_tgtc_tgtm calls,

        Qsrcc_src_dstc_dst csimple
        ) 
    {
        this.parms = parms;
        this.rets = rets;
        this.calls = calls;
        this.csimple = csimple;
    }

    public abstract boolean update();
}

