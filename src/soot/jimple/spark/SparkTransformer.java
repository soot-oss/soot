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

    protected void internalTransform( String phaseName, Map options)
    {
	Date startIg = new Date();
	InvokeGraphBuilder.v().transform( phaseName + ".igb" );
	ig = Scene.v().getActiveInvokeGraph();
	Date startBuild = new Date();
	System.out.println( "Invoke Graph built in "+(startBuild.getTime() - startIg.getTime() )/1000+" seconds." );
	Builder b = new ContextInsensitiveBuilder();
	SparkOptions opts = new SparkOptions( options );
	final PAG pag = b.build( opts );
	Date startCompute = new Date();
	System.out.println( "Pointer Graph built in "+(startCompute.getTime() - startBuild.getTime() )/1000+" seconds." );
        if( opts.collapseSCCs() ) {
            new SCCCollapser( pag, opts.ignoreTypesForSCCs() ).collapse();
        }
        if( opts.collapseEBBs() ) new EBBCollapser( pag ).collapse();
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
            public void case_none() {
            }
        } );
	if( propagator[0] != null ) propagator[0].propagate();
	Date doneCompute = new Date();
	System.out.println( "Solution found in "+(doneCompute.getTime() - startCompute.getTime() )/1000+" seconds." );
        findSetMass( pag );
        if( opts.dumpAnswer() ) new ReachingTypeDumper( pag ).dump();
        if( opts.dumpSolution() ) dumper.dumpPointsToSets();
        if( opts.dumpHTML() ) new PAG2HTML( pag ).dump();
    }
    protected void findSetMass( PAG pag ) {
        int mass = 0;
        for( Iterator it = pag.allVarNodes().iterator(); it.hasNext(); ) {
            VarNode v = (VarNode) it.next();
            PointsToSetInternal set = v.makeP2Set();
            mass += set.size();
        }
        System.out.println( "Set mass: " + mass );
    }
}


