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
import soot.util.queue.*;
import soot.jimple.spark.builder.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.solver.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.callgraph.*;
import soot.jimple.toolkits.pointer.DumbPointerAnalysis;
import soot.jimple.toolkits.invoke.InvokeGraph;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.options.SparkOptions;

/** Main entry point for Spark.
 * @author Ondrej Lhotak
 */
public class SparkTransformer extends SceneTransformer
{ 
    public SparkTransformer( Singletons.Global g ) {}
    public static SparkTransformer v() { return G.v().SparkTransformer(); }

    private static void reportTime( String desc, Date start, Date end ) {
        long time = end.getTime()-start.getTime();
        G.v().out.println( "[Spark] "+desc+" in "+time/1000+"."+(time/100)%10+" seconds." );
    }
    private static void doGC() {
        // Do 5 times because the garbage collector doesn't seem to always collect
        // everything on the first try.
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
    }
    public void internalTransform( String phaseName, Map options )
    {
        SparkOptions opts = new SparkOptions( options );

        // Build pointer assignment graph
        Builder b = new ContextInsensitiveBuilder();
        if( opts.pre_jimplify() ) b.preJimplify();
        if( opts.force_gc() ) doGC();
        Date startBuild = new Date();
        final PAG pag = b.setup( opts );
        if( opts.trim_invoke_graph() ) {
            b.getCallGraph().setInvokeGraph( new InvokeGraph() );
        }
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
        if( opts.simplify_sccs() || opts.vta() ) {
            new SCCCollapser( pag, opts.ignore_types_for_sccs() ).collapse();
        }
        if( opts.simplify_offline() ) new EBBCollapser( pag ).collapse();
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
        if( opts.force_gc() ) doGC();
        reportTime( "Solution found", startSimplify, endProp );


        if( opts.verbose() ) {
            G.v().out.println( "[Spark] Number of reachable methods: "
                    +b.getCallGraph().numReachableMethods() );
        }

        //findSetMass( pag, b );

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
        Scene.v().setActivePointsToAnalysis( pag );
        if( opts.trim_invoke_graph() ) {
            Scene.v().setActiveInvokeGraph( b.getCallGraph().getInvokeGraph() );
        }
        if( opts.add_tags() ) {
            addTags( pag );
        }
    }

    protected void addTags( PAG pag ) {
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
                        if( false && v != null ) {
                            PointsToSetInternal p2set = v.getP2Set();
                            p2set.forall( new P2SetVisitor() {
                            public final void visit( Node n ) {
                                s.addTag( new soot.tagkit.StringTag( n.toString() ) );
                            }} );
                            s.addTag( new soot.tagkit.StringTag( v.toString() ) );
                            Node[] simpleSources = pag.simpleInvLookup(v);
                            for( int i=0; i < simpleSources.length; i++ ) {
                                s.addTag( new soot.tagkit.StringTag( simpleSources[i].toString() ) );
                            }
                            simpleSources = pag.allocInvLookup(v);
                            for( int i=0; i < simpleSources.length; i++ ) {
                                s.addTag( new soot.tagkit.StringTag( simpleSources[i].toString() ) );
                            }
                        }
                    }
                }
            }
        }
    }

    protected void findSetMass( PAG pag, Builder b ) {
        int mass = 0;
        int varMass = 0;
        int adfs = 0;
        int scalars = 0;
        HashMultiMap graph = b.getCallGraph().graph;
        if( false ) {
            for( Iterator srcIt = graph.keySet().iterator(); srcIt.hasNext(); ) {
                final SootMethod src = (SootMethod) srcIt.next();
                for( Iterator dstIt = graph.get(src).iterator(); dstIt.hasNext(); ) {
                    final SootMethod dst = (SootMethod) dstIt.next();
                    G.v().out.println( src.getBytecodeSignature()+" -> "+
                            dst.getBytecodeSignature() );
                }
            }
        }
        if( false ) {
            boolean change;
            do {
                change = false;
                for( Iterator srcIt = new ArrayList( graph.keySet() ).iterator(); srcIt.hasNext(); ) {
                    final SootMethod src = (SootMethod) srcIt.next();
                    for( Iterator dstIt = new ArrayList( graph.get(src) ).iterator(); dstIt.hasNext(); ) {
                        final SootMethod dst = (SootMethod) dstIt.next();
                        change = graph.putAll( src, graph.get( dst ) ) | change;
                    }
                }
            } while( change );
        }
        if( false ) {
            for( Iterator it = b.getCallGraph().reachableMethods(); it.hasNext(); ) {
                SootMethod m = (SootMethod) it.next();
                G.v().out.println( m.getBytecodeSignature() );
            }
        }


        for( Iterator vIt = pag.getVarNodeNumberer().iterator(); vIt.hasNext(); ) {


            final VarNode v = (VarNode) vIt.next();
                scalars++;
            PointsToSetInternal set = v.getP2Set();
            if( set != null ) mass += set.size();
            if( set != null ) varMass += set.size();
            if( set != null && set.size() > 0 ) {
                //G.v().out.println( "V "+v.getVariable()+" "+set.size() );
            //    G.v().out.println( ""+v.getVariable()+" "+v.getMethod()+" "+set.size() );
            }
        }
        for( Iterator anIt = pag.allocSources().iterator(); anIt.hasNext(); ) {
            final AllocNode an = (AllocNode) anIt.next();
            for( Iterator adfIt = an.getFields().iterator(); adfIt.hasNext(); ) {
                final AllocDotField adf = (AllocDotField) adfIt.next();
                PointsToSetInternal set = adf.getP2Set();
                if( set != null ) mass += set.size();
                if( set != null && set.size() > 0 ) {
                    adfs++;
            //        G.v().out.println( ""+adf.getBase().getNewExpr()+"."+adf.getField()+" "+set.size() );
                }
            }
        }
        G.v().out.println( "Set mass: " + mass );
        G.v().out.println( "Variable mass: " + varMass );
        G.v().out.println( "Scalars: "+scalars );
        G.v().out.println( "adfs: "+adfs );
        // Compute points-to set sizes of dereference sites BEFORE
        // trimming sets by declared type
        int[] deRefCounts = new int[30001];
        for( Iterator vIt = pag.getDereferences().iterator(); vIt.hasNext(); ) {
            final VarNode v = (VarNode) vIt.next();
            PointsToSetInternal set = v.getP2Set();
            int size = 0;
            if( set != null ) size = set.size();
            deRefCounts[size]++;
        }
        int total = 0;
        for( int i=0; i < deRefCounts.length; i++ ) total+= deRefCounts[i];
        G.v().out.println( "Dereference counts BEFORE trimming (total = "+total+"):" );
        for( int i=0; i < deRefCounts.length; i++ ) {
            if( deRefCounts[i] > 0 ) {
                G.v().out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
            }
        }
        // Compute points-to set sizes of dereference sites AFTER
        // trimming sets by declared type
        if( pag.getTypeManager().getFastHierarchy() == null ) {
            pag.getTypeManager().clearTypeMask();
            pag.getTypeManager().setFastHierarchy( Scene.v().getOrMakeFastHierarchy() );
            pag.getTypeManager().makeTypeMask( pag );
            deRefCounts = new int[30001];
            for( Iterator vIt = pag.getDereferences().iterator(); vIt.hasNext(); ) {
                final VarNode v = (VarNode) vIt.next();
                PointsToSetInternal set = 
                    pag.getSetFactory().newSet( v.getType(), pag );
                int size = 0;
                if( set != null ) {
                    v.getP2Set().setType( null );
                    v.getP2Set().getNewSet().setType( null );
                    v.getP2Set().getOldSet().setType( null );
                    set.addAll( v.getP2Set(), null );
                    size = set.size();
                }
                deRefCounts[size]++;
            }
            total = 0;
            for( int i=0; i < deRefCounts.length; i++ ) total+= deRefCounts[i];
            G.v().out.println( "Dereference counts AFTER trimming (total = "+total+"):" );
            for( int i=0; i < deRefCounts.length; i++ ) {
                if( deRefCounts[i] > 0 ) {
                    G.v().out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
                }
            }
        }
        /*
        deRefCounts = new int[30001];
        for( Iterator siteIt = ig.getAllSites().iterator(); siteIt.hasNext(); ) {
            final Stmt site = (Stmt) siteIt.next();
            SootMethod method = ig.getDeclaringMethod( site );
            Value ie = site.getInvokeExpr();
            if( !(ie instanceof VirtualInvokeExpr) 
                    && !(ie instanceof InterfaceInvokeExpr) ) continue;
            InstanceInvokeExpr expr = (InstanceInvokeExpr) site.getInvokeExpr();
            Local receiver = (Local) expr.getBase();
            Collection types = pag.reachingObjects( method, site, receiver )
                .possibleTypes();
            Type receiverType = receiver.getType();
            if( receiverType instanceof ArrayType ) {
                receiverType = RefType.v( "java.lang.Object" );
            }
            Collection targets =
                Scene.v().getOrMakeFastHierarchy().resolveConcreteDispatchWithoutFailing(
                        types, expr.getMethod(), (RefType) receiverType );
            deRefCounts[targets.size()]++;
        }
        total = 0;
        for( int i=0; i < deRefCounts.length; i++ ) total+= deRefCounts[i];
        G.v().out.println( "Virtual invoke target counts (total = "+total+"):" );
        for( int i=0; i < deRefCounts.length; i++ ) {
            if( deRefCounts[i] > 0 ) {
                G.v().out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
            }
        }
        */
    }
}


