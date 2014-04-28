/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 - 2006 Ondrej Lhotak
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
import soot.util.IdentityHashSet;
import soot.util.queue.*;
import java.util.*;

/** Propagates points-to sets along pointer assignment graph using a worklist.
 * @author Ondrej Lhotak
 */

public final class PropWorklist extends Propagator {
    protected final Set<VarNode> varNodeWorkList = new TreeSet<VarNode>();

    public PropWorklist( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public final void propagate() {
        ofcg = pag.getOnFlyCallGraph();
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
            while( !varNodeWorkList.isEmpty() ) {
                VarNode src = varNodeWorkList.iterator().next();
                varNodeWorkList.remove( src );
                handleVarNode( src );
            }
            if( verbose ) {
                G.v().out.println( "Now handling field references" );
            }
            for (Object object : pag.storeSources()) {
                final VarNode src = (VarNode) object;
                Node[] targets = pag.storeLookup( src );
                for (Node element0 : targets) {
                    final FieldRefNode target = (FieldRefNode) element0;
                    target.getBase().makeP2Set().forall( new P2SetVisitor() {
                    public final void visit( Node n ) {
                            AllocDotField nDotF = pag.makeAllocDotField( 
                                (AllocNode) n, target.getField() );
                            nDotF.makeP2Set().addAll( src.getP2Set(), null );
                        }
                    } );
                }
            }
            HashSet<Object[]> edgesToPropagate = new HashSet<Object[]>();
	    for (Object object : pag.loadSources()) {
                handleFieldRefNode( (FieldRefNode) object, edgesToPropagate );
	    }
	        IdentityHashSet<PointsToSetInternal> nodesToFlush = new IdentityHashSet<PointsToSetInternal>();
            for (Object[] pair : edgesToPropagate) {
                PointsToSetInternal nDotF = (PointsToSetInternal) pair[0];
		PointsToSetInternal newP2Set = nDotF.getNewSet();
                VarNode loadTarget = (VarNode) pair[1];
                if( loadTarget.makeP2Set().addAll( newP2Set, null ) ) {
                    varNodeWorkList.add( loadTarget );
                }
                nodesToFlush.add( nDotF );
            }
            for (PointsToSetInternal nDotF : nodesToFlush) {
                nDotF.flushNew();
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
                varNodeWorkList.add( (VarNode) element );
                ret = true;
            }
	}
	return ret;
    }
    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleVarNode( final VarNode src ) {
	boolean ret = false;
        boolean flush = true;

        if( src.getReplacement() != src ) throw new RuntimeException(
                "Got bad node "+src+" with rep "+src.getReplacement() );

	final PointsToSetInternal newP2Set = src.getP2Set().getNewSet();
	if( newP2Set.isEmpty() ) return false;

        if( ofcg != null ) {
            QueueReader addedEdges = pag.edgeReader();
            ofcg.updatedNode( src );
            ofcg.build();

            while(addedEdges.hasNext()) {
                Node addedSrc = (Node) addedEdges.next();
                Node addedTgt = (Node) addedEdges.next();
                ret = true;
                if( addedSrc instanceof VarNode ) {
                    if( addedTgt instanceof VarNode ) {
                        VarNode edgeSrc = (VarNode) addedSrc.getReplacement();
                        VarNode edgeTgt = (VarNode) addedTgt.getReplacement();

                        if( edgeTgt.makeP2Set().addAll( edgeSrc.getP2Set(), null ) ) {
                            varNodeWorkList.add( edgeTgt );
                            if(edgeTgt == src) flush = false;
                        }
                    }
                } else if( addedSrc instanceof AllocNode ) {
                    AllocNode edgeSrc = (AllocNode) addedSrc;
                    VarNode edgeTgt = (VarNode) addedTgt.getReplacement();
                    if( edgeTgt.makeP2Set().add( edgeSrc ) ) {
                        varNodeWorkList.add( edgeTgt );
                        if(edgeTgt == src) flush = false;
                    }
                }
            }
        }

	Node[] simpleTargets = pag.simpleLookup( src );
	for (Node element : simpleTargets) {
	    if( element.makeP2Set().addAll( newP2Set, null ) ) {
                varNodeWorkList.add( (VarNode) element );
                if(element == src) flush = false;
                ret = true;
            }
	}

        Node[] storeTargets = pag.storeLookup( src );
        for (Node element : storeTargets) {
            final FieldRefNode fr = (FieldRefNode) element;
            final SparkField f = fr.getField();
            ret = fr.getBase().getP2Set().forall( new P2SetVisitor() {
            public final void visit( Node n ) {
                    AllocDotField nDotF = pag.makeAllocDotField( 
                        (AllocNode) n, f );
                    if( nDotF.makeP2Set().addAll( newP2Set, null ) ) {
                        returnValue = true;
                    }
		}
	    } ) | ret;
        }

        final HashSet<Node[]> storesToPropagate = new HashSet<Node[]>();
        final HashSet<Node[]> loadsToPropagate = new HashSet<Node[]>();
	for( final FieldRefNode fr : src.getAllFieldRefs()) {
	    final SparkField field = fr.getField();
	    final Node[] storeSources = pag.storeInvLookup( fr );
            if( storeSources.length > 0 ) {
                newP2Set.forall( new P2SetVisitor() {
                public final void visit( Node n ) {
                        AllocDotField nDotF = pag.makeAllocDotField(
                            (AllocNode) n, field );
                        for (Node element : storeSources) {
                            Node[] pair = { element,
                                nDotF.getReplacement() };
                            storesToPropagate.add( pair );
                        }
                    }
                } );
            }

	    final Node[] loadTargets = pag.loadLookup( fr );
            if( loadTargets.length > 0 ) {
                newP2Set.forall( new P2SetVisitor() {
                public final void visit( Node n ) {
                        AllocDotField nDotF = pag.makeAllocDotField(
                            (AllocNode) n, field );
                        if( nDotF != null ) {
                            for (Node element : loadTargets) {
                                Node[] pair = { nDotF.getReplacement(),
                                    element };
                                loadsToPropagate.add( pair );
                            }
                        }
                    }
                } );
            }
	}
        if(flush) src.getP2Set().flushNew();
        for (Node[] p : storesToPropagate) {
            VarNode storeSource = (VarNode) p[0];
            AllocDotField nDotF = (AllocDotField) p[1];
            if( nDotF.makeP2Set().addAll( storeSource.getP2Set(), null ) ) {
                ret = true;
            }
        }
        for (Node[] p : loadsToPropagate) {
            AllocDotField nDotF = (AllocDotField) p[0];
            VarNode loadTarget = (VarNode) p[1];
            if( loadTarget.makeP2Set().
                addAll( nDotF.getP2Set(), null ) ) {
                varNodeWorkList.add( loadTarget );
                ret = true;
            }
        }
	return ret;
    }

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final void handleFieldRefNode( FieldRefNode src, 
            final HashSet<Object[]> edgesToPropagate ) {
	final Node[] loadTargets = pag.loadLookup( src );
	if( loadTargets.length == 0 ) return;
        final SparkField field = src.getField();

	src.getBase().getP2Set().forall( new P2SetVisitor() {

	public final void visit( Node n ) {
                AllocDotField nDotF = pag.makeAllocDotField( 
                    (AllocNode) n, field );
                if( nDotF != null ) {
                    PointsToSetInternal p2Set = nDotF.getP2Set();
                    if( !p2Set.getNewSet().isEmpty() ) {
                        for (Node element : loadTargets) {
                            Object[] pair = { p2Set, element };
                            edgesToPropagate.add( pair );
                        }
                    }
                }
	    }
	} );
    }
    
    protected PAG pag;
    protected OnFlyCallGraph ofcg;
}



