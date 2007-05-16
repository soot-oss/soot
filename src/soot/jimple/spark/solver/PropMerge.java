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

package soot.jimple.spark.solver;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.sets.*;
import soot.*;
import java.util.*;

/** Propagates points-to sets along pointer assignment graph using a merging
 * of field reference (Red) nodes to improve scalability.
 * @author Ondrej Lhotak
 */

public final class PropMerge extends Propagator {
    protected final Set<Node> varNodeWorkList = new TreeSet<Node>();

    public PropMerge( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public final void propagate() {
        new TopoSorter( pag, false ).sort();
	for (Object object : pag.allocSources()) {
	    handleAllocNode( (AllocNode) object );
	}

        boolean verbose = pag.getOpts().verbose();
	do {
            if( verbose ) {
                G.v().out.println( "Worklist has "+varNodeWorkList.size()+
                        " nodes." );
            }
            int iter = 0;
            while( !varNodeWorkList.isEmpty() ) {
                VarNode src = (VarNode) varNodeWorkList.iterator().next();
                varNodeWorkList.remove( src );
		handleVarNode( src );
                if( verbose ) {
                    iter++;
                    if( iter >= 1000 ) {
                        iter = 0;
                        G.v().out.println( "Worklist has "+varNodeWorkList.size()+
                                " nodes." );
                    }
                }
            }
            if( verbose ) {
                G.v().out.println( "Now handling field references" );
            }
            for (Object object : pag.storeSources()) {
                final VarNode src = (VarNode) object;
                Node[] storeTargets = pag.storeLookup( src );
                for (Node element0 : storeTargets) {
                    final FieldRefNode fr = (FieldRefNode) element0;
                    fr.makeP2Set().addAll( src.getP2Set(), null );
                }
            }
            for (Object object : pag.loadSources()) {
                final FieldRefNode src = (FieldRefNode) object;
                if( src != src.getReplacement() ) {
                    throw new RuntimeException( "shouldn't happen" );
                }
                Node[] targets = pag.loadLookup( src );
                for (Node element0 : targets) {
                    VarNode target = (VarNode) element0;
                    if( target.makeP2Set().addAll(
                                src.getP2Set(), null ) ) {
                        varNodeWorkList.add( target );
                    }
                }
            }
	} while( !varNodeWorkList.isEmpty() );
    }

    /* End of public methods. */
    /* End of package methods. */

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleAllocNode( AllocNode src ) {
	boolean ret = false;
	Node[] targets = pag.allocLookup( src );
	for (Node element : targets) {
	    if( element.makeP2Set().add( src ) ) {
                varNodeWorkList.add( element );
                ret = true;
            }
	}
	return ret;
    }
    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleVarNode( final VarNode src ) {
	boolean ret = false;

        if( src.getReplacement() != src )  return ret;
            /*
            throw new RuntimeException(
                "Got bad node "+src+" with rep "+src.getReplacement() );
                */

	final PointsToSetInternal newP2Set = src.getP2Set();
	if( newP2Set.isEmpty() ) return false;

	Node[] simpleTargets = pag.simpleLookup( src );
	for (Node element : simpleTargets) {
	    if( element.makeP2Set().addAll( newP2Set, null ) ) {
                varNodeWorkList.add( element );
                ret = true;
            }
	}

        Node[] storeTargets = pag.storeLookup( src );
        for (Node element : storeTargets) {
            final FieldRefNode fr = (FieldRefNode) element;
            if( fr.makeP2Set().addAll( newP2Set, null ) ) {
                ret = true;
            }
        }

        for( Iterator frIt = src.getAllFieldRefs().iterator(); frIt.hasNext(); ) {

            final FieldRefNode fr = (FieldRefNode) frIt.next();
            final SparkField field = fr.getField();
            ret = newP2Set.forall( new P2SetVisitor() {
            public final void visit( Node n ) {
                AllocDotField nDotF = pag.makeAllocDotField(
                    (AllocNode) n, field );
                Node nDotFNode = nDotF.getReplacement();
                if( nDotFNode != fr ) {
                    fr.mergeWith( nDotFNode );
                    returnValue = true;
                }
            }} ) | ret;
        }
	//src.getP2Set().flushNew();
	return ret;
    }

    protected PAG pag;
}



