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

/** Propagates points-to sets along pointer assignment graph using a worklist.
 * @author Ondrej Lhotak
 */

public class PropWorklist extends Propagator {
    protected final Set varNodeWorkList = new TreeSet();

    public PropWorklist( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public void propagate() {
        new TopoSorter( pag, false ).sort();
	for( Iterator it = pag.allocSources().iterator(); it.hasNext(); ) {
	    handleAllocNode( (AllocNode) it.next() );
	}

        boolean verbose = pag.getOpts().verbose();
	do {
            if( verbose ) {
                System.out.println( "Worklist has "+varNodeWorkList.size()+
                        " nodes." );
            }
            while( !varNodeWorkList.isEmpty() ) {
                VarNode src = (VarNode) varNodeWorkList.iterator().next();
                varNodeWorkList.remove( src );
		handleVarNode( src );
            }
            if( verbose ) {
                System.out.println( "Worklist has "+
                    varNodeWorkList.size()+" nodes." );
                System.out.println( "Now handling loads" );
            }
            HashSet edgesToPropagate = new HashSet();
	    for( Iterator it = pag.loadSources().iterator(); it.hasNext(); ) {
                handleFieldRefNode( (FieldRefNode) it.next(), edgesToPropagate );
	    }
            HashSet nodesToFlush = new HashSet();
            for( Iterator it = edgesToPropagate.iterator(); it.hasNext(); ) {
                Pair p = (Pair) it.next();
                PointsToSetInternal nDotF = (PointsToSetInternal) p.getO1();
		PointsToSetInternal newP2Set = nDotF.getNewSet();
                VarNode loadTarget = (VarNode) p.getO2();
                if( loadTarget.makeP2Set().addAll( newP2Set, null ) ) {
                    varNodeWorkList.add( loadTarget );
                }
                nodesToFlush.add( nDotF );
            }
            for( Iterator it = nodesToFlush.iterator(); it.hasNext(); ) {
                PointsToSetInternal nDotF = (PointsToSetInternal) it.next();
                nDotF.flushNew();
            }
	} while( !varNodeWorkList.isEmpty() );
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
            final SparkField f = fr.getField();
            ret = fr.getBase().getP2Set().forall( new P2SetVisitor() {
		public void visit( Node n ) {
                    AllocDotField nDotF = pag.makeAllocDotField( 
                        (AllocNode) n, f );
                    if( nDotF.makeP2Set().addAll( newP2Set, null ) ) {
                        returnValue = true;
                    }
		}
	    } ) | ret;
        }

        final HashSet storesToPropagate = new HashSet();
        final HashSet loadsToPropagate = new HashSet();
	Collection fieldRefs = src.getAllFieldRefs();
	for( Iterator it = fieldRefs.iterator(); it.hasNext(); ) {
	    final FieldRefNode fr = (FieldRefNode) it.next();
	    final SparkField field = fr.getField();
	    final Node[] storeSources = pag.storeInvLookup( fr );
            if( storeSources.length > 0 ) {
                newP2Set.forall( new P2SetVisitor() {
                    public void visit( Node n ) {
                        AllocDotField nDotF = pag.makeAllocDotField(
                            (AllocNode) n, field );
                        for( int i = 0; i < storeSources.length; i++ ) {
                            storesToPropagate.add( new Pair( storeSources[i],
                                    nDotF.getReplacement() ) );
                        }
                    }
                } );
            }

	    final Node[] loadTargets = pag.loadLookup( fr );
            if( loadTargets.length > 0 ) {
                newP2Set.forall( new P2SetVisitor() {
                    public void visit( Node n ) {
                        AllocDotField nDotF = pag.findAllocDotField(
                            (AllocNode) n, field );
                        if( nDotF != null ) {
                            for( int i = 0; i < loadTargets.length; i++ ) {
                                loadsToPropagate.add( new Pair(
                                        nDotF.getReplacement(), loadTargets[i] ) );
                            }
                        }
                    }
                } );
            }
	}
	src.getP2Set().flushNew();
        for( Iterator it = storesToPropagate.iterator(); it.hasNext(); ) {
            Pair p = (Pair) it.next();
            VarNode storeSource = (VarNode) p.getO1();
            AllocDotField nDotF = (AllocDotField) p.getO2();
            if( nDotF.makeP2Set().addAll( storeSource.getP2Set(), null ) ) {
                ret = true;
            }
        }
        for( Iterator it = loadsToPropagate.iterator(); it.hasNext(); ) {
            Pair p = (Pair) it.next();
            AllocDotField nDotF = (AllocDotField) p.getO1();
            VarNode loadTarget = (VarNode) p.getO2();
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
    protected void handleFieldRefNode( FieldRefNode src, 
            final HashSet edgesToPropagate ) {
	final Node[] loadTargets = pag.loadLookup( src );
	if( loadTargets.length == 0 ) return;
        final SparkField field = src.getField();

	src.getBase().getP2Set().forall( new P2SetVisitor() {
	    public void visit( Node n ) {
                AllocDotField nDotF = pag.findAllocDotField( 
                    (AllocNode) n, field );
                if( nDotF != null ) {
                    PointsToSetInternal p2Set = nDotF.getP2Set();
                    if( !p2Set.getNewSet().isEmpty() ) {
                        for( int i = 0; i < loadTargets.length; i++ ) {
                            edgesToPropagate.add( new Pair( p2Set,
                                    loadTargets[i] ) );
                        }
                    }
                }
	    }
	} );
    }

    protected PAG pag;
}



