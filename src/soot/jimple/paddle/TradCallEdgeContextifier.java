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
public class TradCallEdgeContextifier extends AbsCallEdgeContextifier
{ 
    private TradNodeInfo ni;
    public TradCallEdgeContextifier(
        TradNodeInfo ni,
        Rsrcm_stmt_kind_tgtm_src_dst parms,
        Rsrcm_stmt_kind_tgtm_src_dst rets,
        Rsrcc_srcm_stmt_kind_tgtc_tgtm calls,

        Qsrcc_src_dstc_dst csimple
        ) 
    {
        super( parms, rets, calls, csimple );
        this.ni = ni;
    }

    public boolean update() {
    	boolean change = false;
        if( !PaddleScene.v().depMan.checkPrec(this) ) throw new RuntimeException();
        for( Iterator tIt = parms.iterator(); tIt.hasNext(); ) {
            final Rsrcm_stmt_kind_tgtm_src_dst.Tuple t = (Rsrcm_stmt_kind_tgtm_src_dst.Tuple) tIt.next();
            Cons edge = new Cons(new Cons(t.srcm(), t.stmt()),
                                 new Cons(t.kind(), t.tgtm()));
            Collection parmColl = (Collection) parmMap.get(edge);
            if( parmColl == null ) parmMap.put(edge, parmColl = new ArrayList());
            parmColl.add( new Cons(t.src(), t.dst()));
        }
        for( Iterator tIt = rets.iterator(); tIt.hasNext(); ) {
            final Rsrcm_stmt_kind_tgtm_src_dst.Tuple t = (Rsrcm_stmt_kind_tgtm_src_dst.Tuple) tIt.next();
            Cons edge = new Cons(new Cons(t.srcm(), t.stmt()),
                                 new Cons(t.kind(), t.tgtm()));
            Collection retColl = (Collection) retMap.get(edge);
            if( retColl == null ) retMap.put(edge, retColl = new ArrayList());
            retColl.add( new Cons(t.src(), t.dst()));
        }
        for( Iterator tIt = calls.iterator(); tIt.hasNext(); ) {
            final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
            Cons edge = new Cons(new Cons(t.srcm(), t.stmt()),
                                 new Cons(t.kind(), t.tgtm()));
            Collection parmColl = (Collection) parmMap.get(edge);
            if( parmColl != null ) {
                for( Iterator callIt = parmColl.iterator(); callIt.hasNext(); ) {
                    final Cons call = (Cons) callIt.next();
                    addSimple( t.srcc(), (VarNode)call.car(),
                                 t.tgtc(), (VarNode)call.cdr() );
                    change = true;
                }
            }
            Collection retColl = (Collection) retMap.get(edge);
            if( retColl != null ) {
                for( Iterator retIt = retColl.iterator(); retIt.hasNext(); ) {
                    final Cons ret = (Cons) retIt.next();
                    addSimple( t.tgtc(), (VarNode)ret.car(),
                                 t.srcc(), (VarNode)ret.cdr() );
                    change = true;
                }
            }
        }
        return change;
    }

    private void addSimple( Context srcc, VarNode src, Context dstc, VarNode dst ) {
        if( src instanceof GlobalVarNode ) srcc = null;
        if( dst instanceof GlobalVarNode ) dstc = null;
        csimple.add( srcc, src, dstc, dst );
    }

    private Map parmMap = new HashMap();
    private Map retMap = new HashMap();
}

