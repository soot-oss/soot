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
import soot.util.*;

/** Propagates points-to sets using an on-line cycle detection algorithm
 * based on Heintze and Tardieu, PLDI 2000.
 * @author Ondrej Lhotak
 */

public final class PropCycle extends Propagator {
    public PropCycle( PAG pag ) {
        this.pag = pag;
        typeManager = (TypeManager) pag.getTypeManager();
        varNodeToIteration = new LargeNumberedMap( pag.getVarNodeNumberer() );
    }

    /** Actually does the propagation. */
    public final void propagate() {
        ofcg = pag.getOnFlyCallGraph();
        boolean verbose = pag.getOpts().verbose();
        Collection bases = new HashSet();
        for( Iterator frnIt = pag.getFieldRefNodeNumberer().iterator(); frnIt.hasNext(); ) {
            final FieldRefNode frn = (FieldRefNode) frnIt.next();
            bases.add( frn.getBase() );
        }
        bases = new ArrayList( bases );
        int iteration = 0;
        boolean changed;
        boolean finalIter = false;
	do {
            changed = false;
            iteration++;
            currentIteration = new Integer( iteration );
            if( verbose ) G.v().out.println( "Iteration: "+iteration );
            for( Iterator vIt = bases.iterator(); vIt.hasNext(); ) {
                final VarNode v = (VarNode) vIt.next();
                changed = computeP2Set( (VarNode) v.getReplacement(), new ArrayList() ) | changed;
            }
            if( ofcg != null ) throw new RuntimeException( "NYI" );
            if( verbose ) G.v().out.println( "Processing stores" );
            for( Iterator srcIt = pag.storeSources().iterator(); srcIt.hasNext(); ) {
                final VarNode src = (VarNode) srcIt.next();
                Node[] targets = pag.storeLookup( src );
                for( int i = 0; i < targets.length; i++ ) {
                    final FieldRefNode target = (FieldRefNode) targets[i];
                    changed = target.getBase().makeP2Set().forall( new P2SetVisitor() {
                    public final void visit( Node n ) {
                            AllocDotField nDotF = pag.makeAllocDotField( 
                                (AllocNode) n, target.getField() );
                            nDotF.makeP2Set().addAll( src.getP2Set(), null );
                        }
                    } ) | changed;
                }
            }
            if( !changed && !finalIter ) {
                finalIter = true;
                if( verbose ) G.v().out.println( "Doing full graph" );
                bases = new ArrayList(pag.getVarNodeNumberer().size());
                for( Iterator vIt = pag.getVarNodeNumberer().iterator(); vIt.hasNext(); ) {
                    final VarNode v = (VarNode) vIt.next();
                    bases.add( v );
                }
                changed = true;
            }
	} while( changed );
    }


    /* End of public methods. */
    /* End of package methods. */

    private boolean computeP2Set( final VarNode v, ArrayList path ) {
        boolean ret = false;

        if( path.contains( v ) ) {
            for( Iterator nIt = path.iterator(); nIt.hasNext(); ) {
                final Node n = (Node) nIt.next();
        //        if( n != v ) n.mergeWith( v );
            }
            return false;
        }

        if( currentIteration == varNodeToIteration.get(v) ) return false;
        varNodeToIteration.put(v, currentIteration);

        path.add( v );
        if( v.getP2Set().isEmpty() ) {
            Node[] srcs = pag.allocInvLookup( v );
            for( int i = 0; i < srcs.length; i++ ) {
                ret = v.makeP2Set().add( srcs[i] ) | ret;
            }
        }
        {
            Node[] srcs = pag.simpleInvLookup( v );
            for( int i = 0; i < srcs.length; i++ ) {
                VarNode src = (VarNode) srcs[i];
                ret = computeP2Set( src, path ) | ret;
                ret = v.makeP2Set().addAll( src.getP2Set(), null ) | ret;
            }
        }
        {
            Node[] srcs = pag.loadInvLookup( v );
            for( int i = 0; i < srcs.length; i++ ) {
                final FieldRefNode src = (FieldRefNode) srcs[i];
                ret = src.getBase().getP2Set().forall( new P2SetVisitor() {
                public final void visit( Node n ) {
                    AllocNode an = (AllocNode) n;
                    AllocDotField adf = 
                        pag.makeAllocDotField( an, src.getField() );
                    returnValue = v.makeP2Set().addAll( adf.getP2Set(), null ) | returnValue;
                }} ) | ret;
            }
        }
        path.remove(path.size()-1);
        return ret;
    }

    private PAG pag;
    private OnFlyCallGraph ofcg;
    private Integer currentIteration;
    private LargeNumberedMap varNodeToIteration;
    private TypeManager typeManager;
}



