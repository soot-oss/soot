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
import soot.jimple.paddle.queue.*;
import java.util.*;
import soot.*;

/** Assigns a target context to virtual call edges depending on the source
 * context.
 * @author Ondrej Lhotak
 */
public class TradKCFAVirtualContextManager extends AbsVirtualContextManager
{ 
    TradKCFAVirtualContextManager( Rctxt_local_obj_srcm_stmt_kind_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out, int k ) {
        super( in, out );
        this.k = k;
    }
    private int k;
    public void update() {
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple t = (Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple) tIt.next();
            ContextString cs = (ContextString) t.ctxt();
            if( cs == null ) cs = new ContextString(k);
            out.add( t.ctxt(),
                    t.srcm(),
                    t.stmt(),
                    t.kind(),
                    cs.push(t.stmt()),
                    t.tgtm() );
        }
    }
}
