/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.spark;
import soot.*;
import soot.jimple.spark.builder.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.solver.*;
import soot.jimple.spark.sets.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.options.BDDSparkOptions;
import soot.tagkit.*;
import soot.relations.*;

/** Main entry point for Spark.
 * @author Ondrej Lhotak
 */
public class BDDSparkTransformer extends AbstractSparkTransformer
{ 
    public BDDSparkTransformer( Singletons.Global g ) {}
    public static BDDSparkTransformer v() { return G.v().soot_jimple_spark_BDDSparkTransformer(); }

    protected void internalTransform( String phaseName, Map options )
    {
        BDDSparkOptions opts = new BDDSparkOptions( options );

        // Build pointer assignment graph
        ContextInsensitiveBuilder b = new ContextInsensitiveBuilder();
        if( opts.pre_jimplify() ) b.preJimplify();
        if( opts.force_gc() ) doGC();
        Date startBuild = new Date();
        final BDDPAG pag = (BDDPAG) b.setup( opts );
        b.build();
        Date endBuild = new Date();
        reportTime( "Pointer Assignment Graph", startBuild, endBuild );
        if( opts.force_gc() ) doGC();

        // Build type masks
        Date startTM = new Date();
        pag.getTypeManager().makeTypeMask();
        Date endTM = new Date();
        reportTime( "Type masks", startTM, endTM );
        if( opts.force_gc() ) doGC();

        if( opts.verbose() ) {
            G.v().out.println( "VarNodes: "+pag.getVarNodeNumberer().size() );
            G.v().out.println( "FieldRefNodes: "+pag.getFieldRefNodeNumberer().size() );
            G.v().out.println( "AllocNodes: "+pag.getAllocNodeNumberer().size() );
        }

        // Propagate
        Date startProp = new Date();
        BDDPropagator propagator = new BDDPropagator( pag );
        propagator.propagate();
        Date endProp = new Date();
        reportTime( "Propagation", startProp, endProp );
        if( opts.force_gc() ) doGC();
        
        if( !opts.on_fly_cg() || opts.vta() ) {
            CallGraphBuilder cgb = new CallGraphBuilder( pag );
            cgb.build();
        }

        if( opts.verbose() ) {
            G.v().out.println( "[Spark] Number of reachable methods: "
                    +Scene.v().getReachableMethods().size() );
        }

        if( opts.set_mass() ) findSetMass( pag, b );
        Scene.v().setPointsToAnalysis( pag );
        if( opts.add_tags() ) {
            addTags( pag );
        }
        if( opts.verbose() ) {
            JBuddyProfiler.v().printInfo();
        }
    }

    private void addTags( BDDPAG pag ) {
        throw new RuntimeException( "NYI" );
    }

    private void findSetMass( BDDPAG pag, ContextInsensitiveBuilder b ) {
        throw new RuntimeException( "NYI" );
    }
}


