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

/** Propagates points-to sets along pointer assignment graph using a worklist,
 * merging AllocDotField (yellow) nodes with their FieldRefNodes (red).
 * @author Ondrej Lhotak
 */

public class PropMerge extends Propagator {
    protected final Set varNodeWorkList = new TreeSet();
    protected final Set fieldRefNodeWorkList = new HashSet();

    public PropMerge( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public void propagate() {
        new TopoSorter( pag, false ).sort();
	for( Iterator it = pag.allocSources().iterator(); it.hasNext(); ) {
	    handleAllocNode( (AllocNode) it.next() );
	}

        boolean verbose = pag.getOpts().verbose();
	do {
            if( verbose ) {
                System.out.println( "VarNodeWorklist has "+varNodeWorkList.size()+
                        " nodes." );
            }
            while( !varNodeWorkList.isEmpty() ) {
                VarNode src = (VarNode) varNodeWorkList.iterator().next();
                varNodeWorkList.remove( src );
		handleVarNode( src );
            }
            if( verbose ) {
                System.out.println( "VarNodeWorklist has "+
                    varNodeWorkList.size()+" nodes." );
                System.out.println( "Now handling loads" );
            }
            if( verbose ) {
                System.out.println( "FieldRefNodeWorklist has "+fieldRefNodeWorkList.size()+
                        " nodes." );
            }
            while( !fieldRefNodeWorkList.isEmpty() ) {
                FieldRefNode src = (FieldRefNode) fieldRefNodeWorkList.iterator().next();
                fieldRefNodeWorkList.remove( src );
		handleFieldRefNode( src );
            }
            if( verbose ) {
                System.out.println( "FieldRefNodeWorklist has "+
                    fieldRefNodeWorkList.size()+" nodes." );
                System.out.println( "Now handling loads" );
            }
	} while( !varNodeWorkList.isEmpty() || !fieldRefNodeWorkList.isEmpty() );
    }

    /* End of public methods. Nothing to see here; move along. */
    /* End of package methods. Nothing to see here; move along. */

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected boolean handleAllocNode( AllocNode src ) {
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
    protected boolean handleVarNode( final VarNode src ) {
	boolean ret = false;

        final OnFlyCallGraph ofcg = pag.getOnFlyCallGraph();
        if( ofcg != null ) {
            final LinkedList touchedNodes = new LinkedList();
            ret = src.getP2Set().getNewSet().forall( new P2SetVisitor() {
                public void visit( Node n ) {
                    returnValue = ofcg.addReachingType(
                        src, n.getType(), touchedNodes ) | returnValue;
                }
            } ) | ret;
            for( Iterator it = touchedNodes.iterator(); it.hasNext(); ) {
                Node n = (Node) it.next();
                PointsToSetInternal p2set = n.getP2Set();
                if( p2set != null ) p2set.unFlushNew();
                if( n instanceof VarNode ) varNodeWorkList.add( n );
                if( n instanceof FieldRefNode ) fieldRefNodeWorkList.add( n );
                ret = true;
            }
        }

	final PointsToSetInternal newP2Set = src.getP2Set().getNewSet();
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
                fieldRefNodeWorkList.add( fr );
            }
        }

	Collection fieldRefs = src.getAllFieldRefs();
	for( Iterator it = fieldRefs.iterator(); it.hasNext(); ) {
	    final FieldRefNode fr = (FieldRefNode) it.next();
	    final SparkField field = fr.getField();
            if( newP2Set.forall( new P2SetVisitor() {
                public void visit( Node n ) {
                    AllocDotField nDotF = pag.makeAllocDotField(
                        (AllocNode) n, field );
                    fr.mergeWith( nDotF );
                    returnValue = true;
                }
            } ) ) {
                ret = true;
                fieldRefNodeWorkList.add( fr );
            }
	}
	src.getP2Set().flushNew();
	return ret;
    }

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected boolean handleFieldRefNode( FieldRefNode src ) {
	boolean ret = false;
	final PointsToSetInternal newP2Set = src.getP2Set().getNewSet();
	if( newP2Set.isEmpty() ) return false;

	Node[] loadTargets = pag.loadLookup( src );
	for( int i = 0; i < loadTargets.length; i++ ) {
	    if( loadTargets[i].makeP2Set().addAll( newP2Set, null ) ) {
                varNodeWorkList.add( loadTargets[i] );
                ret = true;
            }
	}
	src.getP2Set().flushNew();
        return ret;
    }

    protected PAG pag;
}



