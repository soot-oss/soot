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

package soot.jimple.spark.builder;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.jimple.toolkits.invoke.InvokeGraph;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;
import soot.jimple.toolkits.pointer.util.NativeHelper;
import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.jimple.spark.internal.*;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.spark.solver.OnFlyCallGraph;

/** A context insensitive pointer assignment graph builder.
 * @author Ondrej Lhotak
 */
public class ContextInsensitiveBuilder implements Builder {
    /** Builds and returns a pointer assignment graph. */
    public PAG build( SparkOptions opts ) {
	pag = new PAG( opts );
	ig = Scene.v().getActiveInvokeGraph();
        if( opts.simulateNatives() ) {
            NativeHelper.register( new SparkNativeHelper( pag ) );
        }
	parms = new StandardParms( pag );
        if( opts.onFlyCallGraph() ) {
            pag.setOnFlyCallGraph( new OnFlyCallGraph(
                        pag,
                        Scene.v().getOrMakeFastHierarchy(),
                        ig,
                        parms ) );
        }
	Iterator classesIt = Scene.v().getClasses().iterator();
	while( classesIt.hasNext() ) {
	    SootClass c = (SootClass) classesIt.next();
	    handleClass( c );
	    addMiscEdges( c );
	}
	return pag;
    }

    /* End of public methods. */
    /* End of package methods. */
    protected void handleClass( SootClass c ) {
	Iterator methodsIt = c.getMethods().iterator();
	while( methodsIt.hasNext() ) 
	{
	    SootMethod m = (SootMethod) methodsIt.next();
	    if( pag.getOpts().simulateNatives() && m.isNative() ) {
		buildNative( m );
	    }
	    if( !m.isConcrete() ) continue;
            if( !ig.mcg.isReachable(m)) continue;
	    Body b = m.retrieveActiveBody();
	    parms.setCurrentMethod( m );
	    Iterator unitsIt = b.getUnits().iterator();
	    while( unitsIt.hasNext() )
	    {
		parms.handleStmt( (Stmt) unitsIt.next() );
	    }
	}
    }


    private static final RefType string = RefType.v("java.lang.String");
    private static final ArrayType strAr = ArrayType.v(string, 1);
    private static final List strArL = Collections.singletonList( strAr );
    private static final String main =
	SootMethod.getSubSignature( "main", strArL, VoidType.v() );
    private static final String finalize =
	SootMethod.getSubSignature( "finalize", Collections.EMPTY_LIST, VoidType.v() );
    protected void addMiscEdges( SootClass c ) {
        // Add node for parameter (String[]) in main method
        if( c.declaresMethod( main ) ) {
            SootMethod m = c.getMethod( main );
            parms.setCurrentMethod( m );
            parms.addEdge( parms.caseArgv(), parms.caseParm( m, 0 ) );
        }

        // Add objects reaching this of finalize() methods
        if( c.declaresMethod( finalize ) ) {
            // In VTA, there was the comment:
            // I have no clue whether or not this is right.
            for( Iterator mIt = c.getMethods().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();
                if( !m.getName().equals("<init>") ) continue;
                parms.addEdge( parms.caseThis( m ),
                        parms.caseThis( c.getMethod( finalize ) ) );
            }
        }
    }

    protected void buildNative( SootMethod m ) {
        ValNode thisNode = null;
        ValNode retNode = null; 
	parms.setCurrentMethod( m );
        if( !m.isStatic() ) { 
	    thisNode = (ValNode) parms.caseThis( m );
        }
        if( m.getReturnType() instanceof RefLikeType ) {
	    retNode = (ValNode) parms.caseRet( m );
	}
        ValNode[] args = new ValNode[ m.getParameterCount() ];
        for( int i = 0; i < m.getParameterCount(); i++ ) {
            if( !( m.getParameterType(i) instanceof RefLikeType ) ) continue;
	    args[i] = (ValNode) parms.caseParm( m, i );
        }
        NativeMethodDriver.process( m, thisNode, retNode, args );
    }

    InvokeGraph ig;
    PAG pag;
    Parms parms;
}

