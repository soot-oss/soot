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
import soot.jimple.toolkits.invoke.InvokeGraph;
import soot.jimple.toolkits.invoke.InvokeGraphBuilder;
import soot.jimple.toolkits.invoke.InvokeGraphTrimmer;
import soot.jimple.*;
import java.util.*;

/** Main entry point for Spark.
 * @author Ondrej Lhotak
 */
public class SparkTransformer extends SceneTransformer
{ 
    private static SparkTransformer instance = 
	new SparkTransformer();
    private SparkTransformer() {}
    private InvokeGraph ig;

    public static SparkTransformer v() { return instance; }

    public String getDeclaredOptions() { return super.getDeclaredOptions() +
	SparkOptions.getDeclaredOptions(); }

    public String getDefaultOptions() { return SparkOptions.getDefaultOptions(); }

    private static void reportTime( String desc, Date start, Date end ) {
        long time = end.getTime()-start.getTime();
        System.out.println( "[Spark] "+desc+" in "+time/1000+"."+(time/100)%10+" seconds." );
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

        // Build invoke graph
        Date startIg = new Date();
        InvokeGraphBuilder.v().transform( phaseName + ".igb" );
        ig = Scene.v().getActiveInvokeGraph();
        Date endIg = new Date();
        reportTime( "Invoke Graph", startIg, endIg );

        // Build pointer assignment graph
        Builder b = new ContextInsensitiveBuilder();
        b.preJimplify();
        if( opts.forceGCs() ) doGC();
        Date startBuild = new Date();
        final PAG pag = b.build( opts );
        Date endBuild = new Date();
        reportTime( "Pointer Assignment Graph", startBuild, endBuild );
        if( opts.forceGCs() ) doGC();

        // Build type masks
        Date startTM = new Date();
        pag.getTypeManager().makeTypeMask( pag );
        Date endTM = new Date();
        reportTime( "Type masks", startTM, endTM );
        if( opts.forceGCs() ) doGC();

        // Simplify pag
        Date startSimplify = new Date();
        if( opts.simplifySCCs() || opts.VTA() ) {
            new SCCCollapser( pag, opts.ignoreTypesForSCCs() ).collapse();
        }
        if( opts.simplifyOffline() ) new EBBCollapser( pag ).collapse();
        if( true || opts.simplifySCCs() || opts.VTA() || opts.simplifyOffline() ) {
            pag.cleanUpMerges();
        }
        Date endSimplify = new Date();
        reportTime( "Pointer Graph simplified", startSimplify, endSimplify );
        if( opts.forceGCs() ) doGC();

        // Dump pag
        PAGDumper dumper = null;
        if( opts.dumpPAG() || opts.dumpSolution() ) {
            dumper = new PAGDumper( pag );
        }
        if( opts.dumpPAG() ) dumper.dump();

        // Propagate
        Date startProp = new Date();
        final Propagator[] propagator = new Propagator[1];
        opts.propagator( new SparkOptions.Switch_propagator() {
            public void case_iter() {
                propagator[0] = new PropIter( pag );
            }
            public void case_worklist() {
                propagator[0] = new PropWorklist( pag );
            }
            public void case_merge() {
                propagator[0] = new PropMerge( pag );
            }
            public void case_alias() {
                propagator[0] = new PropAlias( pag );
            }
            public void case_none() {
            }
        } );
        if( propagator[0] != null ) propagator[0].propagate();
        Date endProp = new Date();
        reportTime( "Propagation", startProp, endProp );
        if( opts.forceGCs() ) doGC();
        reportTime( "Solution found", startSimplify, endProp );

        //findSetMass( pag );

        /*
        if( propagator[0] instanceof PropMerge ) {
            new MergeChecker( pag ).check();
        } else if( propagator[0] != null ) {
            new Checker( pag ).check();
        }
        */

        if( opts.dumpAnswer() ) new ReachingTypeDumper( pag ).dump();
        if( opts.dumpSolution() ) dumper.dumpPointsToSets();
        if( opts.dumpHTML() ) new PAG2HTML( pag ).dump();
        if( false ) {
            BitPointsToSet.delete();
            HybridPointsToSet.delete();
            Parm.delete();
        } else {
            Scene.v().setActivePointsToAnalysis( pag );
        }
        if( opts.trimInvokeGraph() ) {
            new InvokeGraphTrimmer( pag, ig ).trimInvokeGraph();
        }
    }
    protected void findSetMass( PAG pag ) {
        int mass = 0;
        int varMass = 0;
        int adfs = 0;
        int scalars = 0;
        for( Iterator vIt = pag.allVarNodes().iterator(); vIt.hasNext(); ) {
            final VarNode v = (VarNode) vIt.next();
                scalars++;
            PointsToSetInternal set = v.getP2Set();
            if( set != null ) mass += set.size();
            if( set != null ) varMass += set.size();
            if( set != null && set.size() > 0 ) {
                //System.out.println( "V "+v.getValue()+" "+set.size() );
            //    System.out.println( ""+v.getValue()+" "+v.getMethod()+" "+set.size() );
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
            //        System.out.println( ""+adf.getBase().getNewExpr()+"."+adf.getField()+" "+set.size() );
                }
            }
        }
        System.out.println( "Set mass: " + mass );
        System.out.println( "Variable mass: " + varMass );
        System.out.println( "Scalars: "+scalars );
        System.out.println( "adfs: "+adfs );
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
        System.out.println( "Dereference counts BEFORE trimming (total = "+total+"):" );
        for( int i=0; i < deRefCounts.length; i++ ) {
            if( deRefCounts[i] > 0 ) {
                System.out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
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
            System.out.println( "Dereference counts AFTER trimming (total = "+total+"):" );
            for( int i=0; i < deRefCounts.length; i++ ) {
                if( deRefCounts[i] > 0 ) {
                    System.out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
                }
            }
        }
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
        System.out.println( "Virtual invoke target counts (total = "+total+"):" );
        for( int i=0; i < deRefCounts.length; i++ ) {
            if( deRefCounts[i] > 0 ) {
                System.out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
            }
        }
    }
}


