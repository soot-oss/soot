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
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.*;

/** Keeps track of call edges.
 * @author Ondrej Lhotak
 */
public class TradCallGraph extends AbsCallGraph
{ 
    private CallGraph cg;
    TradCallGraph( Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out ) {
        super(in, out);
        cg = new CallGraph();
    }
    public boolean update() {
        boolean ret = false;
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
            if( cg.addEdge( new Edge(
                        MethodContext.v( t.srcm(), t.srcc() ),
                        t.stmt(),
                        MethodContext.v( t.tgtm(), t.tgtc() ),
                        t.kind() ) ) ) {
                out.add( t.srcc(), t.srcm(), t.stmt(), t.kind(), t.tgtc(), t.tgtm() );
                ret = true;
            }
        }
        return ret;
    }
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf( Rctxt_method methods ) {
        Qsrcc_srcm_stmt_kind_tgtc_tgtm queue =
            new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
        Rsrcc_srcm_stmt_kind_tgtc_tgtm ret = queue.reader();
        for( Iterator tIt = methods.iterator(); tIt.hasNext(); ) {
            final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
            edgesOutOfHelper( MethodContext.v( t.method(), t.ctxt() ), queue );
        }
        return ret;
    }

    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf( MethodOrMethodContext method ) {
        Qsrcc_srcm_stmt_kind_tgtc_tgtm queue =
            new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
        Rsrcc_srcm_stmt_kind_tgtc_tgtm ret = queue.reader();
        edgesOutOfHelper( method, queue );
        return ret;
    }
    private void edgesOutOfHelper( MethodOrMethodContext method, Qsrcc_srcm_stmt_kind_tgtc_tgtm queue ) {
            edgesHelper( cg.edgesOutOf( method ), queue );
    }
    private void edgesHelper( Iterator it, Qsrcc_srcm_stmt_kind_tgtc_tgtm queue ) {
            while( it.hasNext() ) {
                Edge e = (Edge) it.next();

                queue.add( e.srcCtxt(), e.src(), e.srcUnit(), e.kind(),
                        e.tgtCtxt(), e.tgt() );
            }
    }
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edges() {
        Qsrcc_srcm_stmt_kind_tgtc_tgtm queue =
            new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad();
        Rsrcc_srcm_stmt_kind_tgtc_tgtm ret = queue.reader();
        edgesHelper( cg.listener(), queue );
        return ret;
    }
}

