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
import soot.*;
import java.util.*;

/** Propagates points-to sets along pointer assignment graph using iteration.
 * @author Ondrej Lhotak
 */

public class PropIter extends Propagator {
    public PropIter( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public void propagate() {
        final OnFlyCallGraph ofcg = pag.getOnFlyCallGraph();
        new TopoSorter( pag, false ).sort();
	for( Iterator it = pag.allocSources().iterator(); it.hasNext(); ) {
	    handleAllocNode( (AllocNode) it.next() );
	}

        int iteration = 1;
	boolean change;
        TreeSet simpleSources = new TreeSet( pag.simpleSources() );
        TreeSet storeSources = new TreeSet( pag.storeSources() );
	do {
            if( pag.getOpts().verbose() ) {
                System.out.println( "Iteration "+(iteration++) );
            }
	    change = false;
	    for( Iterator it = simpleSources.iterator(); it.hasNext(); ) {
                change = handleSimples( (VarNode) it.next() ) | change;
	    }
	    for( Iterator it = storeSources.iterator(); it.hasNext(); ) {
                change = handleStores( (VarNode) it.next() ) | change;
	    }
	    for( Iterator it = pag.loadSources().iterator(); it.hasNext(); ) {
                change = handleLoads( (FieldRefNode) it.next() ) | change;
	    }
            if( ofcg != null ) {
                for( Iterator it = ofcg.allReceivers().iterator(); it.hasNext(); ) {
                    final VarNode rec = pag.findVarNode( it.next() );
                    PointsToSetInternal recSet = rec.getP2Set();
                    if( recSet != null ) {
                        change = rec.getP2Set().forall( new P2SetVisitor() {
                            public void visit( Node n ) {
                                returnValue = ofcg.addReachingType(
                                    rec, n.getType(), null ) | returnValue;
                            }
                        } ) | change;
                    }
                }
            }
	} while( change );
    }

    /* End of public methods. Nothing to see here; move along. */
    /* End of package methods. Nothing to see here; move along. */

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected boolean handleAllocNode( AllocNode src ) {
	boolean ret = false;
	Node[] targets = pag.allocLookup( src );
	for( int i = 0; i < targets.length; i++ ) {
	    ret = targets[i].makeP2Set().add( src ) | ret;
	}
	return ret;
    }

    protected boolean handleSimples( VarNode src ) {
	boolean ret = false;
	PointsToSetInternal srcSet = src.getP2Set();
	if( srcSet.isEmpty() ) return false;
	Node[] simpleTargets = pag.simpleLookup( src );
	for( int i = 0; i < simpleTargets.length; i++ ) {
	    ret = simpleTargets[i].makeP2Set().addAll( srcSet, null ) | ret;
	}
        return ret;
    }

    protected boolean handleStores( VarNode src ) {
	boolean ret = false;
	final PointsToSetInternal srcSet = src.getP2Set();
	if( srcSet.isEmpty() ) return false;
	Node[] storeTargets = pag.storeLookup( src );
	for( int i = 0; i < storeTargets.length; i++ ) {
            final FieldRefNode fr = (FieldRefNode) storeTargets[i];
            final SparkField f = fr.getField();
            ret = fr.getBase().getP2Set().forall( new P2SetVisitor() {
                public void visit( Node n ) {
                    AllocDotField nDotF = pag.makeAllocDotField( 
                        (AllocNode) n, f );
                    if( nDotF.makeP2Set().addAll( srcSet, null ) ) {
                        returnValue = true;
                    }
                }
            } ) | ret;
	}
        return ret;
    }

    protected boolean handleLoads( FieldRefNode src ) {
	boolean ret = false;
	final Node[] loadTargets = pag.loadLookup( src );
        final SparkField f = src.getField();
        ret = src.getBase().getP2Set().forall( new P2SetVisitor() {
            public void visit( Node n ) {
                AllocDotField nDotF = pag.makeAllocDotField( (AllocNode) n, f );
                PointsToSetInternal set = nDotF.getP2Set();
                for( int i = 0; i < loadTargets.length; i++ ) {
                    VarNode target = (VarNode) loadTargets[i];
                    if( target.makeP2Set().addAll( set, null ) ) {
                        returnValue = true;
                    }
                }
            }
        } ) | ret;
        return ret;
    }

    protected PAG pag;
}



