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

/** Resolves virtual calls based on the actual type of the receiver.
 * @author Ondrej Lhotak
 */
public class TestVirtualCalls extends AbsVirtualCalls
{ 
    TradVirtualCalls tradcalls;
    Qvarc_var_objc_obj tradpt = new Qvarc_var_objc_objTrad("tradpt");
    Qvar_srcm_stmt_signature_kind tradrcv = new Qvar_srcm_stmt_signature_kindTrad("tradrcv");
    Qvar_srcm_stmt_tgtm tradspc = new Qvar_srcm_stmt_tgtmTrad("tradspc");
    Rctxt_var_obj_srcm_stmt_kind_tgtm tradoutrdr;
    Rsrcc_srcm_stmt_kind_tgtc_tgtm tradstatrdr;


    BDDVirtualCalls bddcalls;
    Qvarc_var_objc_obj bddpt = new Qvarc_var_objc_objTrad("bddpt");
    Qvar_srcm_stmt_signature_kind bddrcv = new Qvar_srcm_stmt_signature_kindTrad("bddrcv");
    Qvar_srcm_stmt_tgtm bddspc = new Qvar_srcm_stmt_tgtmTrad("bddspc");
    Qctxt_var_obj_srcm_stmt_kind_tgtm bddout = new Qctxt_var_obj_srcm_stmt_kind_tgtmTrad("bddout");
    Qsrcc_srcm_stmt_kind_tgtc_tgtm bddstat = new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("bddstat");
    Rctxt_var_obj_srcm_stmt_kind_tgtm bddoutrdr;
    Rsrcc_srcm_stmt_kind_tgtc_tgtm bddstatrdr;


    TestVirtualCalls( Rvarc_var_objc_obj pt,
            Rvar_srcm_stmt_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qctxt_var_obj_srcm_stmt_kind_tgtm out,
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

    public boolean update() {
        boolean change = false;
        for( Iterator tupleIt = receivers.iterator(); tupleIt.hasNext(); ) {
            final Rvar_srcm_stmt_signature_kind.Tuple tuple = (Rvar_srcm_stmt_signature_kind.Tuple) tupleIt.next();
            tradrcv.add( tuple.var(), tuple.srcm(), tuple.stmt(), tuple.signature(), tuple.kind() );
            bddrcv.add( tuple.var(), tuple.srcm(), tuple.stmt(), tuple.signature(), tuple.kind() );
        }

        for( Iterator tupleIt = specials.iterator(); tupleIt.hasNext(); ) {

            final Rvar_srcm_stmt_tgtm.Tuple tuple = (Rvar_srcm_stmt_tgtm.Tuple) tupleIt.next();
            tradspc.add( tuple.var(), tuple.srcm(), tuple.stmt(), tuple.tgtm() );
            bddspc.add( tuple.var(), tuple.srcm(), tuple.stmt(), tuple.tgtm() );
        }

        for( Iterator tupleIt = pt.iterator(); tupleIt.hasNext(); ) {

            final Rvarc_var_objc_obj.Tuple tuple = (Rvarc_var_objc_obj.Tuple) tupleIt.next();
            Set tradTuples = new HashSet();
            Set bddTuples = new HashSet();

            tradpt.add( tuple.varc(), tuple.var(), tuple.objc(), tuple.obj() );
            boolean tradret = tradcalls.update();
            for( Iterator t2It = tradoutrdr.iterator(); t2It.hasNext(); ) {
                final Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple t2 = (Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple) t2It.next();
                tradTuples.add(t2);
            }
            for( Iterator t2It = tradstatrdr.iterator(); t2It.hasNext(); ) {
                final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t2 = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) t2It.next();
                //System.out.println( ""+t2 );
            }

            bddpt.add( tuple.varc(), tuple.var(), tuple.objc(), tuple.obj() );
            boolean bddret = bddcalls.update();
            for( Iterator t2It = bddoutrdr.iterator(); t2It.hasNext(); ) {
                final Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple t2 = (Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple) t2It.next();
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
                    final Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple t2 = (Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple) t2It.next();
                    System.out.println( ""+t2 );
                    System.out.println( t2.tgtm().getNumber() );
                }
                System.out.println( ">>>>> TRAD" );
                System.out.println( "<<<<< BDD" );
                for( Iterator t2It = bddTuples.iterator(); t2It.hasNext(); ) {
                    final Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple t2 = (Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple) t2It.next();
                    System.out.println( ""+t2 );
                    System.out.println( t2.tgtm().getNumber() );
                }
                System.out.println( ">>>>> BDD" );
            }
            if( tradret != bddret ) {
                throw new RuntimeException( "Returns from update are not equal: tradret="+tradret+" bddret="+bddret );
            }
            change = change | tradret;
        }
        return change;
    }
}


