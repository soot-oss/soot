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
import soot.options.SparkOptions;
import soot.tagkit.*;

/** Main entry point for Spark.
 * @author Ondrej Lhotak
 */
public class SparkTransformer extends AbstractSparkTransformer
{ 
    public SparkTransformer( Singletons.Global g ) {}
    public static SparkTransformer v() { return G.v().SparkTransformer(); }

    protected void internalTransform( String phaseName, Map options )
    {
        SparkOptions opts = new SparkOptions( options );

        // Build pointer assignment graph
        ContextInsensitiveBuilder b = new ContextInsensitiveBuilder();
        if( opts.pre_jimplify() ) b.preJimplify();
        if( opts.force_gc() ) doGC();
        Date startBuild = new Date();
        final PAG pag = (PAG) b.setup( opts );
        b.build();
        Date endBuild = new Date();
        reportTime( "Pointer Assignment Graph", startBuild, endBuild );
        if( opts.force_gc() ) doGC();

        // Build type masks
        Date startTM = new Date();
        pag.getTypeManager().makeTypeMask( pag );
        Date endTM = new Date();
        reportTime( "Type masks", startTM, endTM );
        if( opts.force_gc() ) doGC();

        if( opts.verbose() ) {
            G.v().out.println( "VarNodes: "+pag.getVarNodeNumberer().size() );
            G.v().out.println( "FieldRefNodes: "+pag.getFieldRefNodeNumberer().size() );
            G.v().out.println( "AllocNodes: "+pag.getAllocNodeNumberer().size() );
        }

        // Simplify pag
        Date startSimplify = new Date();

        // We only simplify if on_fly_cg is false. But, if vta is true, it
        // overrides on_fly_cg, so we can still simplify. Something to handle
        // these option interdependencies more cleanly would be nice...
        if( ( opts.simplify_sccs() && !opts.on_fly_cg() ) || opts.vta() ) {
                new SCCCollapser( pag, opts.ignore_types_for_sccs() ).collapse();
        }
        if( opts.simplify_offline() && !opts.on_fly_cg() ) {
            new EBBCollapser( pag ).collapse();
        }
        if( true || opts.simplify_sccs() || opts.vta() || opts.simplify_offline() ) {
            pag.cleanUpMerges();
        }
        Date endSimplify = new Date();
        reportTime( "Pointer Graph simplified", startSimplify, endSimplify );
        if( opts.force_gc() ) doGC();

        // Dump pag
        PAGDumper dumper = null;
        if( opts.dump_pag() || opts.dump_solution() ) {
            dumper = new PAGDumper( pag );
        }
        if( opts.dump_pag() ) dumper.dump();

        // Propagate
        Date startProp = new Date();
        final Propagator[] propagator = new Propagator[1];
        switch( opts.propagator() ) {
            case SparkOptions.propagator_iter:
                propagator[0] = new PropIter( pag );
                break;
            case SparkOptions.propagator_worklist:
                propagator[0] = new PropWorklist( pag );
                break;
            case SparkOptions.propagator_cycle:
                propagator[0] = new PropCycle( pag );
                break;
            case SparkOptions.propagator_merge:
                propagator[0] = new PropMerge( pag );
                break;
            case SparkOptions.propagator_alias:
                propagator[0] = new PropAlias( pag );
                break;
            case SparkOptions.propagator_none:
                break;
            default:
                throw new RuntimeException();
        }
        if( propagator[0] != null ) propagator[0].propagate();
        Date endProp = new Date();
        reportTime( "Propagation", startProp, endProp );
        reportTime( "Solution found", startSimplify, endProp );
        if( opts.force_gc() ) doGC();
        
        if( !opts.on_fly_cg() || opts.vta() ) {
            CallGraphBuilder cgb = new CallGraphBuilder( pag );
            cgb.build();
        }

        if( opts.verbose() ) {
            G.v().out.println( "[Spark] Number of reachable methods: "
                    +Scene.v().getReachableMethods().size() );
        }

        if( opts.set_mass() ) findSetMass( pag );

        /*
        if( propagator[0] instanceof PropMerge ) {
            new MergeChecker( pag ).check();
        } else if( propagator[0] != null ) {
            new Checker( pag ).check();
        }
        */

        if( opts.dump_answer() ) new ReachingTypeDumper( pag ).dump();
        if( opts.dump_solution() ) dumper.dumpPointsToSets();
        if( opts.dump_html() ) new PAG2HTML( pag ).dump();
        Scene.v().setPointsToAnalysis( pag );
        if( opts.add_tags() ) {
            addTags( pag );
        }
    }
    protected void addTags( PAG pag ) {
        final Tag unknown = new StringTag( "Untagged Spark node" );
        final Map nodeToTag = pag.getNodeTags();
        for( Iterator cIt = Scene.v().getClasses().iterator(); cIt.hasNext(); ) {
            final SootClass c = (SootClass) cIt.next();
            for( Iterator mIt = c.methodIterator(); mIt.hasNext(); ) {
                SootMethod m = (SootMethod) mIt.next();
                if( !m.isConcrete() ) continue;
                if( !m.hasActiveBody() ) continue;
                for( Iterator sIt = m.getActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
                    final Stmt s = (Stmt) sIt.next();
                    if( s instanceof DefinitionStmt ) {
                        Value lhs = ((DefinitionStmt) s).getLeftOp();
                        VarNode v = null;
                        if( lhs instanceof Local ) {
                            v = pag.findVarNode( (Local) lhs );
                        } else if( lhs instanceof FieldRef ) {
                            v = pag.findVarNode( ((FieldRef) lhs).getField() );
                        }
                        if( v != null ) {
                            PointsToSetInternal p2set = v.getP2Set();
                            p2set.forall( new P2SetVisitor() {
                            public final void visit( Node n ) {
                                addTag( s, n, nodeToTag, unknown );
                            }} );
                            Node[] simpleSources = pag.simpleInvLookup(v);
                            for( int i=0; i < simpleSources.length; i++ ) {
                                addTag( s, simpleSources[i], nodeToTag, unknown );
                            }
                            simpleSources = pag.allocInvLookup(v);
                            for( int i=0; i < simpleSources.length; i++ ) {
                                addTag( s, simpleSources[i], nodeToTag, unknown );
                            }
                            simpleSources = pag.loadInvLookup(v);
                            for( int i=0; i < simpleSources.length; i++ ) {
                                addTag( s, simpleSources[i], nodeToTag, unknown );
                            }
                        }
                    }
                }
            }
        }
    }
}


