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

package soot.jimple.spark.solver;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.internal.*;
import soot.*;
import soot.util.queue.*;
import java.util.*;
import soot.options.SparkOptions;
import soot.relations.*;

/** Propagates points-to sets along pointer assignment graph using BDDs.
 * @author Ondrej Lhotak
 */

public final class BDDPropagator extends Propagator {
    public BDDPropagator( BDDPAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public final void propagate() {
        ofcg = pag.getOnFlyCallGraph();

        Domain stp_var = new Domain( pag.getVarNodeNumberer(), pag.v1, "stp.var" );

        Relation oldPointsTo = new Relation( pag.pt_var, pag.pt_obj );
        Relation newPointsTo = new Relation( pag.pt_var, pag.pt_obj );
        Relation tmp;
        Relation objectsBeingStored;
        Relation loadsFromHeap;
        Relation newStorePt;
        Relation storePt = new Relation( stp_var, pag.st_fd, pag.hpt_obj );
        Relation loadPt;
        Relation newFieldPt;
        Relation loadAss = new Relation( pag.pt_obj, pag.ld_fd, pag.ld_dst );
        Relation newLoadPt;

        pag.pt.unionEq( pag.alloc );
        newPointsTo.unionEq( pag.pt );

        // start solving 
        do {

            // repeat rule (1) in the inner loop
            do {
                tmp = pag.es.relprod( pag.es_src, newPointsTo, pag.pt_var );
                tmp = tmp.replace( pag.es_dst, pag.pt_var );
                tmp = tmp.minus( pag.pt );
                // newPointsTo = newPt3 & typeFilter;
                newPointsTo = tmp;
                pag.pt.unionEq( newPointsTo );
                if( pag.getOpts().verbose() ) {
                    G.v().out.println( "Minor iteration: "+newPointsTo.projectDownTo( pag.pt_var ).size()+" changed p2sets" );
                }
            } while( !newPointsTo.isEmpty() );

            newPointsTo = pag.pt.minus( oldPointsTo );

            // apply rule (2)
            objectsBeingStored = pag.st.relprod( pag.st_src, newPointsTo, pag.pt_var );
            objectsBeingStored = objectsBeingStored.replace( 
                    Domain.box( pag.st_dst, pag.pt_obj ),
                    Domain.box( stp_var, pag.hpt_obj ) );
            newStorePt = objectsBeingStored.minus( storePt );
            storePt.unionEq( newStorePt );

            newFieldPt = storePt.relprod( stp_var, newPointsTo, pag.pt_var );
            newFieldPt.unionEq(
                    newStorePt.relprod( stp_var, oldPointsTo, pag.pt_var ) );
            newFieldPt = newFieldPt.replace(
                    Domain.box( pag.st_fd, pag.pt_obj ),
                    Domain.box( pag.hpt_fd, pag.hpt_base ) );
            newFieldPt = newFieldPt.minus( pag.hpt );
            pag.hpt.unionEq( newFieldPt );

            // apply rule (3)
            loadsFromHeap = pag.ld.relprod( pag.ld_src, newPointsTo, pag.pt_var );
            loadsFromHeap = loadsFromHeap.minus( loadAss );
            newLoadPt = loadAss.relprod( pag.ld_fd, pag.pt_obj, newFieldPt, pag.hpt_fd, pag.hpt_base );
            newLoadPt.unionEq(
                    loadsFromHeap.relprod( pag.ld_fd, pag.pt_obj, pag.hpt, pag.hpt_fd, pag.hpt_base ) );
            // cache loadAss
            loadAss.unionEq( loadsFromHeap );
            
            // update oldPointsTo
            oldPointsTo.unionEq( pag.pt ); 

            // convert new points-to relation to normal type
            newPointsTo = newLoadPt.replace( 
                    Domain.box( pag.ld_dst, pag.hpt_obj ),
                    Domain.box( pag.pt_var, pag.pt_obj ) );
            newPointsTo.minusEq( pag.pt );
    
            // apply typeFilter
            //newPointsTo = typeFilter & newPointsTo;
            pag.pt.unionEq( newPointsTo );

            if( pag.getOpts().verbose() ) {
                G.v().out.println( "Major iteration: "+newPointsTo.projectDownTo( pag.pt_var ).size()+" changed p2sets" );
            }
        } while(!newPointsTo.isEmpty());
    }

    /* End of public methods. */
    /* End of package methods. */

    
    protected BDDPAG pag;
    protected OnFlyCallGraph ofcg;
}



