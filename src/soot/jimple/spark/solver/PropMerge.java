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
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.internal.*;
import soot.*;
import java.util.*;
import soot.options.SparkOptions;

/** Propagates points-to sets along pointer assignment graph using a merging
 * of field reference (Red) nodes to improve scalability.
 * @author Ondrej Lhotak
 */

public final class PropMerge extends Propagator {
    protected final Set varNodeWorkList = new TreeSet();

    public PropMerge( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public final void propagate() {
        new TopoSorter( pag, false ).sort();
	for( Iterator it = pag.allocSources().iterator(); it.hasNext(); ) {
	    handleAllocNode( (AllocNode) it.next() );
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
            for( Iterator srcIt = pag.storeSources().iterator(); srcIt.hasNext(); ) {
                final VarNode src = (VarNode) srcIt.next();
                Node[] storeTargets = pag.storeLookup( src );
                for( int i = 0; i < storeTargets.length; i++ ) {
                    final FieldRefNode fr = (FieldRefNode) storeTargets[i];
                    fr.makeP2Set().addAll( src.getP2Set(), null );
                }
            }
            for( Iterator srcIt = pag.loadSources().iterator(); srcIt.hasNext(); ) {
                final FieldRefNode src = (FieldRefNode) srcIt.next();
                if( src != src.getReplacement() ) {
                    throw new RuntimeException( "shouldn't happen" );
                }
                Node[] targets = pag.loadLookup( src );
                for( int i = 0; i < targets.length; i++ ) {
                    VarNode target = (VarNode) targets[i];
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
	for( int i = 0; i < targets.length; i++ ) {
	    if( targets[i].makeP2Set().add( src ) ) {
                varNodeWorkList.add( targets[i] );
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
	for( int i = 0; i < simpleTargets.length; i++ ) {
	    if( simpleTargets[i].makeP2Set().addAll( newP2Set, null ) ) {
                varNodeWorkList.add( simpleTargets[i] );
                ret = true;
            }
	}

        Node[] storeTargets = pag.storeLookup( src );
        for( int i = 0; i < storeTargets.length; i++ ) {
            final FieldRefNode fr = (FieldRefNode) storeTargets[i];
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



