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
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.invoke.InvokeGraph;
import soot.jimple.toolkits.invoke.InvokeGraphBuilder;
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

    /*
    private Node getNodeFor( soot.jimple.toolkits.pointer.Node klojNode, PAG pag ) {
        if( klojNode.getType() instanceof NullType ) {
            System.out.println( "Null node: "+klojNode );
        }
        if( klojNode.getType() instanceof AnyType ) {
            System.out.println( "Anytype node: "+klojNode );
        }
        if( klojNode instanceof soot.jimple.toolkits.pointer.VarNode ) {
            soot.jimple.toolkits.pointer.VarNode vn =
                (soot.jimple.toolkits.pointer.VarNode) klojNode;
            Node ret = pag.makeVarNode( vn.getVal(), vn.getType(), vn.m );
            if( vn.getVal() instanceof Local ) {
                pag.reachingObjects( vn.m, null, (Local) vn.getVal() );
            }
            return ret;
        } else if( klojNode instanceof soot.jimple.toolkits.pointer.FieldRefNode ) {
            soot.jimple.toolkits.pointer.FieldRefNode frn =
                (soot.jimple.toolkits.pointer.FieldRefNode) klojNode;
            soot.jimple.toolkits.pointer.VarNode vn = frn.getBase();
            Object field = frn.getField();
            if( field instanceof SparkField ) {
            } else if( field instanceof Integer ) {
                if( field.equals( PointsToAnalysis.ARRAY_ELEMENTS_NODE ) ) {
                    field = ArrayElement.v();
                }
            } else {
                throw new RuntimeException( field.toString() );
            }
            return pag.makeFieldRefNode( vn.getVal(), vn.getType(), (SparkField) field, frn.m );
        } else if( klojNode instanceof soot.jimple.toolkits.pointer.AllocNode ) {
            soot.jimple.toolkits.pointer.AllocNode an =
                (soot.jimple.toolkits.pointer.AllocNode) klojNode;
            return pag.makeAllocNode( an, an.getType() );
        } else throw new RuntimeException( klojNode.toString() );
    }
    private PAG buildFromKloj( SparkOptions opts ) {
        soot.jimple.toolkits.pointer.NodePPG ppg = 
            new soot.jimple.toolkits.pointer.NodePPG( ig );
        ppg.parmsAsFields = opts.parmsAsFields();
        ppg.returnsAsFields = opts.returnsAsFields();
        ppg.collapseObjects = false;
        ppg.collapseObjects = false;
        ppg.typesForSites = false;
        ppg.mergeStringbuffer = opts.mergeStringBuffer();
        ppg.simulateNatives = opts.simulateNatives();
        if( opts.simulateNatives() ) {
            NativeHelper.register( new
                    soot.jimple.toolkits.pointer.kloj.KlojNativeHelper( ppg ) );
        }
        ppg.build();
        //ppg.collapseEBBs( Scene.v().getOrMakeFastHierarchy() );
        System.out.println( "Now converting from Kloj to PAG" );
        PAG pag = new PAG( opts );
        MultiMap[] edges = { ppg.getSimple(), ppg.getLoads(),
            ppg.getStores(), ppg.getNews() };
        for( int i = 0; i < edges.length; i++ ) {
            MultiMap m = edges[i];
            int size = 0;
            for( Iterator it = m.keySet().iterator(); it.hasNext(); ) {
                soot.jimple.toolkits.pointer.Node src =
                    (soot.jimple.toolkits.pointer.Node) it.next();
                for( Iterator it2 = m.get( src ).iterator(); it2.hasNext(); ) {
                    soot.jimple.toolkits.pointer.Node dest =
                        (soot.jimple.toolkits.pointer.Node) it2.next();
                    if( dest instanceof soot.jimple.toolkits.pointer.FieldRefNode ) {
                        soot.jimple.toolkits.pointer.FieldRefNode frn = 
                            (soot.jimple.toolkits.pointer.FieldRefNode) dest;
                        if( frn.getField().equals( soot.jimple.toolkits.pointer.PointerAnalysis.ARRAY_ELEMENTS_NODE ) )
                        {
                            Type t = frn.getBase().getType();
                            if( t instanceof ArrayType ) {
                                ArrayType at = (ArrayType) t;
                                if(!( at.getElementType() instanceof RefLikeType) )
                                    continue;
                            }
                        }
                    }
                    pag.addEdge( getNodeFor( src, pag ), getNodeFor( dest, pag ) );
                    size++;
                }
            }
            System.out.println( size );
        }
        Set[] edges2 = { pag.simpleSources(), pag.loadSources(),
            pag.storeSources(), pag.allocSources() };
        for( int i = 0; i < edges2.length; i++ ) {
            int size=0;
            int size2=0;
            for( Iterator it = edges2[i].iterator(); it.hasNext(); ) {
                int s=0;
                switch(i) {
                    case 0: s = pag.simpleLookup( (VarNode) it.next() ).length;
                    break;
                    case 1: s = pag.loadLookup( (FieldRefNode) it.next() ).length;
                    break;
                    case 2: s = pag.storeLookup( (VarNode) it.next() ).length;
                    break;
                    case 3: s = pag.allocLookup( (AllocNode) it.next() ).length;
                    break;
                }
                size += s;
                size2 += s*s;
            }
            System.out.println( "Size: "+size+" Size2: "+size2 );
        }
        for( Iterator anIt = pag.allocSources().iterator(); anIt.hasNext(); ) {
            final AllocNode an = (AllocNode) anIt.next();
            System.out.println( "Allocation site: "+an );
            Node[] nodes = pag.allocLookup( an );
            for( int i = 0; i < nodes.length; i++ ) {
                System.out.println( " "+nodes[i] );
            }
        }
        System.out.println( "Done converting from Kloj to PAG" );
        return pag;
    }
*/
    protected void internalTransform( String phaseName, Map options)
    {
	Date startIg = new Date();
	InvokeGraphBuilder.v().transform( phaseName + ".igb" );
	ig = Scene.v().getActiveInvokeGraph();
	SparkOptions opts = new SparkOptions( options );
	Date startBuild = new Date();
	System.out.println( "[Spark] Invoke Graph built in "+(startBuild.getTime() - startIg.getTime() )/1000+" seconds." );
        System.gc();
	Builder b = new ContextInsensitiveBuilder();
	final PAG pag = b.build( opts );
	Date startCompute = new Date();
	System.out.println( "[Spark] Pointer Graph built in "+(startCompute.getTime() - startBuild.getTime() )/1000+" seconds." );
        System.gc();
        if( opts.collapseSCCs() ) {
            new SCCCollapser( pag, opts.ignoreTypesForSCCs() ).collapse();
        }
        if( opts.collapseEBBs() ) new EBBCollapser( pag ).collapse();
        if( opts.collapseSCCs() || opts.collapseEBBs() ) {
            pag.cleanUpMerges();
        }
	Date doneSimplify = new Date();
	System.out.println( "[Spark] Pointer Graph simplified in "+(doneSimplify.getTime() - startCompute.getTime() )/1000+" seconds." );
        PAGDumper dumper = null;
        if( opts.dumpPAG() || opts.dumpSolution() ) {
            dumper = new PAGDumper( pag );
        }
        if( opts.dumpPAG() ) dumper.dump();
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
	Date doneCompute = new Date();
	System.out.println( "[Spark] Propagation done in "+(doneCompute.getTime() - doneSimplify.getTime() )/1000+" seconds." );
	System.out.println( "[Spark] Solution found in "+(doneCompute.getTime() - startCompute.getTime() )/1000+" seconds." );
        System.gc();
        /*
        if( propagator[0] instanceof PropMerge ) {
            new MergeChecker( pag ).check();
        } else if( propagator[0] != null ) {
            new Checker( pag ).check();
        }
        findSetMass( pag );
        pag.dumpNumbersOfEdges();
        */
        if( opts.dumpAnswer() ) new ReachingTypeDumper( pag ).dump();
        if( opts.dumpSolution() ) dumper.dumpPointsToSets();
        if( opts.dumpHTML() ) new PAG2HTML( pag ).dump();
        Scene.v().setActivePointsToAnalysis( pag );
    }
/*
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
    }
    */
}


