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

/** Checks points-to sets with pointer assignment graph to make sure everything
 * has been correctly propagated.
 * @author Ondrej Lhotak
 */

public class Checker {
    public Checker( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public void check() {
	for (Object object : pag.allocSources()) {
	    handleAllocNode( (AllocNode) object );
	}
        for (Object object : pag.simpleSources()) {
            handleSimples( (VarNode) object );
        }
        for (Object object : pag.loadSources()) {
            handleLoads( (FieldRefNode) object );
        }
        for (Object object : pag.storeSources()) {
            handleStores( (VarNode) object );
        }
    }

    /* End of public methods. */
    /* End of package methods. */

    protected void checkAll( final Node container, PointsToSetInternal nodes,
            final Node upstream ) {
        nodes.forall( new P2SetVisitor() {
        public final void visit( Node n ) {
                checkNode( container, n, upstream );
            }
        } );
    }
    protected void checkNode( Node container, Node n, Node upstream ) {
        if( container.getReplacement() != container )
            throw new RuntimeException( "container "+container+" is illegal" );
        if( upstream.getReplacement() != upstream )
            throw new RuntimeException( "upstream "+upstream+" is illegal" );
        PointsToSetInternal p2set = container.getP2Set();
        FastHierarchy fh = pag.getTypeManager().getFastHierarchy();
        if( !p2set.contains( n ) 
                && ( fh == null || container.getType() == null ||
                fh.canStoreType( n.getType(), container.getType() ) ) ) {
            G.v().out.println( "Check failure: "+container+" does not have "+n
                    +"; upstream is "+upstream );
        }
    }
    protected void handleAllocNode( AllocNode src ) {
	Node[] targets = pag.allocLookup( src );
	for (Node element : targets) {
            checkNode( element, src, src );
	}
    }

    protected void handleSimples( VarNode src ) {
	PointsToSetInternal srcSet = src.getP2Set();
	if( srcSet.isEmpty() ) return;
	final Node[] simpleTargets = pag.simpleLookup( src );
	for (Node element : simpleTargets) {
            checkAll( element, srcSet, src );
	}
    }

    protected void handleStores( final VarNode src ) {
	final PointsToSetInternal srcSet = src.getP2Set();
	if( srcSet.isEmpty() ) return;
	Node[] storeTargets = pag.storeLookup( src );
	for (Node element : storeTargets) {
            final FieldRefNode fr = (FieldRefNode) element;
            final SparkField f = fr.getField();
            fr.getBase().getP2Set().forall( new P2SetVisitor() {
            public final void visit( Node n ) {
                    AllocDotField nDotF = pag.makeAllocDotField( 
                        (AllocNode) n, f );
                    checkAll( nDotF, srcSet, src );
                }
            } );
	}
    }

    protected void handleLoads( final FieldRefNode src ) {
	final Node[] loadTargets = pag.loadLookup( src );
        final SparkField f = src.getField();
        src.getBase().getP2Set().forall( new P2SetVisitor() {
        public final void visit( Node n ) {
                AllocDotField nDotF = ((AllocNode)n).dot( f );
                if( nDotF == null ) return;
                PointsToSetInternal set = nDotF.getP2Set();
                if( set.isEmpty() ) return;
                for (Node element : loadTargets) {
                    VarNode target = (VarNode) element;
                    checkAll( target, set, src );
                }
            }
        } );
    }

    protected PAG pag;
}



