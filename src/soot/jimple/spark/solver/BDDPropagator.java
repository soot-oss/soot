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
        final Domain var = pag.var;
        final Domain src = pag.src;
        final Domain dst = pag.dst;
        final Domain base = pag.base;
        final Domain obj = pag.obj;
        final Domain fld = pag.fld;

        final PhysicalDomain v1 = pag.v1;
        final PhysicalDomain v2 = pag.v2;
        final PhysicalDomain h1 = pag.h1;
        final PhysicalDomain h2 = pag.h2;
        final PhysicalDomain fd = pag.fd;

        final Relation edgeSet = pag.edgeSet;
        final Relation pointsTo = pag.pointsTo;
        final Relation alloc = pag.alloc;
        final Relation loads = pag.loads;
        final Relation stores = pag.stores;
        final Relation fieldPt = pag.fieldPt;

        // Variable var points to object obj
        final Relation oldPointsTo = pointsTo.sameDomains();
        final Relation newPointsTo = pointsTo.sameDomains();
        final Relation tmpPointsTo = newPointsTo.sameDomains();


        // Object obj is being stored into var.fld
        final Relation objectsBeingStored = new Relation( obj, var, fld,
                                                          h2,  v1,  fd );
        final Relation oldStorePt = objectsBeingStored.sameDomains();
        final Relation newStorePt = objectsBeingStored.sameDomains();

        final Relation newFieldPt = fieldPt.sameDomains();
        final Relation tmpFieldPt = fieldPt.sameDomains();

        // The objects pointed to by base.fld are being loaded into dst
        final Relation loadsFromHeap = new Relation( base, fld, dst,
                                                     h1,   fd,  v2 );
        final Relation loadAss = loadsFromHeap.sameDomains();



        pointsTo.eqUnion( pointsTo, alloc );
        newPointsTo.eqUnion( newPointsTo, pointsTo );

        // start solving 
        do {

            // repeat rule (1) in the inner loop
            do {
                newPointsTo.eqRelprod( edgeSet, src, newPointsTo, var,
                                       var, edgeSet,     dst,
                                       obj, newPointsTo, obj );
                newPointsTo.eqMinus( newPointsTo, pointsTo );
                // newPointsTo = newPointsTo & typeFilter;
                pointsTo.eqUnion( pointsTo, newPointsTo );

                if( pag.getOpts().verbose() ) {
                    G.v().out.println( "Minor iteration: "+newPointsTo.
                            projectDownTo( var ).size()+" changed p2sets" );
                }

            } while( !newPointsTo.isEmpty() );

            newPointsTo.eqMinus( pointsTo, oldPointsTo );

            // apply rule (2)
            objectsBeingStored.eqRelprod( stores, src, newPointsTo, var,
                                          obj, newPointsTo, obj,
                                          var, stores,      dst, 
                                          fld, stores,      fld );
                                          
            newStorePt.eqMinus( objectsBeingStored, oldStorePt );
            oldStorePt.eqUnion( oldStorePt, newStorePt );

            newFieldPt.eqRelprod( oldStorePt, var, newPointsTo, var,
                                  base, newPointsTo, obj,
                                  fld,  oldStorePt,  fld,
                                  obj,  oldStorePt,  obj );

            tmpFieldPt.eqRelprod( newStorePt, var, oldPointsTo, var,
                                  base, oldPointsTo, obj,
                                  fld,  newStorePt,  fld,
                                  obj,  newStorePt,  obj );
            newFieldPt.eqUnion( newFieldPt, tmpFieldPt );
                                  
            newFieldPt.eqMinus( newFieldPt, fieldPt );
            fieldPt.eqUnion( fieldPt, newFieldPt );

            // apply rule (3)
            loadsFromHeap.eqRelprod( loads, src, newPointsTo, var,
                                     base, newPointsTo, obj,
                                     fld,  loads,       fld,
                                     dst,  loads,       dst );

            loadsFromHeap.eqMinus( loadsFromHeap, loadAss );
                                     
            newPointsTo.eqRelprod( loadAss, base, fld, newFieldPt, base, fld,
                                   var, loadAss,    dst,
                                   obj, newFieldPt, obj );
            tmpPointsTo.eqRelprod( loadsFromHeap, base, fld, fieldPt, base, fld,
                                   var, loadsFromHeap, dst,
                                   obj, fieldPt,       obj );

            newPointsTo.eqUnion( newPointsTo, tmpPointsTo );

            // cache loadAss
            loadAss.eqUnion( loadAss, loadsFromHeap );
            
            // update oldPointsTo
            oldPointsTo.eq( pointsTo ); 

            // convert new points-to relation to normal type
            newPointsTo.eqMinus( newPointsTo, pointsTo );
    
            // apply typeFilter
            //newPointsTo = typeFilter & newPointsTo;
            pointsTo.eqUnion( pointsTo, newPointsTo );

            if( pag.getOpts().verbose() ) {
                G.v().out.println( "Major iteration: "+newPointsTo.projectDownTo( pag.var ).size()+" changed p2sets" );
            }
        } while(!newPointsTo.isEmpty());
    }

    /* End of public methods. */
    /* End of package methods. */

    
    protected BDDPAG pag;
    protected OnFlyCallGraph ofcg;
}



