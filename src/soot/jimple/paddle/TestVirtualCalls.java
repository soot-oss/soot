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
import soot.util.*;
import soot.jimple.paddle.queue.*;
import java.util.*;
import soot.util.queue.*;
import soot.jimple.*;

/** Resolves virtual calls based on the actual type of the receiver.
 * @author Ondrej Lhotak
 */
public class TestVirtualCalls extends AbsVirtualCalls
{ 
    TradVirtualCalls tradcalls;
    Qvar_obj tradpt = new Qvar_objTrad("tradpt");
    Qlocal_srcm_stmt_signature_kind tradrcv = new Qlocal_srcm_stmt_signature_kindTrad("tradrcv");
    Qlocal_srcm_stmt_tgtm tradspc = new Qlocal_srcm_stmt_tgtmTrad("tradspc");
    Rctxt_local_obj_srcm_stmt_kind_tgtm tradoutrdr;
    Rsrcc_srcm_stmt_kind_tgtc_tgtm tradstatrdr;


    BDDVirtualCalls bddcalls;
    Qvar_obj bddpt = new Qvar_objTrad("bddpt");
    Qlocal_srcm_stmt_signature_kind bddrcv = new Qlocal_srcm_stmt_signature_kindTrad("bddrcv");
    Qlocal_srcm_stmt_tgtm bddspc = new Qlocal_srcm_stmt_tgtmTrad("bddspc");
    Qctxt_local_obj_srcm_stmt_kind_tgtm bddout = new Qctxt_local_obj_srcm_stmt_kind_tgtmTrad("bddout");
    Qsrcc_srcm_stmt_kind_tgtc_tgtm bddstat = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("bddstat");
    Rctxt_local_obj_srcm_stmt_kind_tgtm bddoutrdr;
    Rsrcc_srcm_stmt_kind_tgtc_tgtm bddstatrdr;


    TestVirtualCalls( Rvar_obj pt,
            Rlocal_srcm_stmt_signature_kind receivers,
            Rlocal_srcm_stmt_tgtm specials,
            Qctxt_local_obj_srcm_stmt_kind_tgtm out,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm statics
        ) {
        super( pt, receivers, specials, out, statics );

        tradcalls = new TradVirtualCalls( tradpt.reader("tradcalls"), tradrcv.reader("tradcalls"), tradspc.reader("tradcalls"), out, statics );
        bddcalls = new BDDVirtualCalls( bddpt.reader("tradcalls"), bddrcv.reader("tradcalls"), bddspc.reader("tradcalls"), bddout, bddstat );

        tradoutrdr = out.reader("tradcalls");
        tradstatrdr = statics.reader("tradcalls");
        bddoutrdr = bddout.reader("tradcalls");
        bddstatrdr = bddstat.reader("tradcalls");
    }

    public void update() {
        for( Iterator tupleIt = receivers.iterator(); tupleIt.hasNext(); ) {
            final Rlocal_srcm_stmt_signature_kind.Tuple tuple = (Rlocal_srcm_stmt_signature_kind.Tuple) tupleIt.next();
            tradrcv.add( tuple.local(), tuple.srcm(), tuple.stmt(), tuple.signature(), tuple.kind() );
            bddrcv.add( tuple.local(), tuple.srcm(), tuple.stmt(), tuple.signature(), tuple.kind() );
        }

        for( Iterator tupleIt = specials.iterator(); tupleIt.hasNext(); ) {

            final Rlocal_srcm_stmt_tgtm.Tuple tuple = (Rlocal_srcm_stmt_tgtm.Tuple) tupleIt.next();
            tradspc.add( tuple.local(), tuple.srcm(), tuple.stmt(), tuple.tgtm() );
            bddspc.add( tuple.local(), tuple.srcm(), tuple.stmt(), tuple.tgtm() );
        }

        for( Iterator tupleIt = pt.iterator(); tupleIt.hasNext(); ) {

            final Rvar_obj.Tuple tuple = (Rvar_obj.Tuple) tupleIt.next();
            //if( tuple.obj().getType() instanceof AnySubType ) continue;

            Set tradTuples = new HashSet();
            Set bddTuples = new HashSet();

            tradpt.add( tuple.var(), tuple.obj() );
            tradcalls.update();
            for( Iterator t2It = tradoutrdr.iterator(); t2It.hasNext(); ) {
                final Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple t2 = (Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple) t2It.next();
                tradTuples.add(t2);
            }
            for( Iterator t2It = tradstatrdr.iterator(); t2It.hasNext(); ) {
                final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t2 = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) t2It.next();
                //System.out.println( ""+t2 );
            }

            bddpt.add( tuple.var(), tuple.obj() );
            bddcalls.update();
            for( Iterator t2It = bddoutrdr.iterator(); t2It.hasNext(); ) {
                final Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple t2 = (Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple) t2It.next();
                bddTuples.add(t2);
            }
            for( Iterator t2It = bddstatrdr.iterator(); t2It.hasNext(); ) {
                final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t2 = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) t2It.next();
                //System.out.println( ""+t2 );
            }

            if( !tradTuples.equals(bddTuples) ) {
                System.out.println( "Pt pair: "+tuple );
                System.out.println( "<<<<< TRAD" );
                for( Iterator t2It = tradTuples.iterator(); t2It.hasNext(); ) {
                    final Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple t2 = (Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple) t2It.next();
                    System.out.println( ""+t2 );
                    System.out.println( t2.tgtm().getNumber() );
                }
                System.out.println( ">>>>> TRAD" );
                System.out.println( "<<<<< BDD" );
                for( Iterator t2It = bddTuples.iterator(); t2It.hasNext(); ) {
                    final Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple t2 = (Rctxt_local_obj_srcm_stmt_kind_tgtm.Tuple) t2It.next();
                    System.out.println( ""+t2 );
                    System.out.println( t2.tgtm().getNumber() );
                }
                System.out.println( ">>>>> BDD" );
            }
        }
    }
}


