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

package soot.jimple.paddle;
import soot.jimple.paddle.queue.*;
import soot.*;
import soot.util.queue.*;
import java.util.*;
import soot.options.PaddleOptions;

/** Propagates points-to sets along pointer assignment graph using iteration.
 * @author Ondrej Lhotak
 */

public final class PropIter extends AbsPropagator {
    public PropIter(  Rsrc_dst simple,
            Rsrc_fld_dst load,
            Rsrc_fld_dst store,
            Robj_var alloc,
            Qvar_obj propout,
            AbsPAG pag
 ) {
        super( simple, load, store, alloc, propout, pag );
    }
    private AbsP2Sets p2sets;
    /** Actually does the propagation. */
    public final void update() {
        p2sets = PaddleScene.v().p2sets;
        new TopoSorter( pag, false ).sort();
        for( Iterator it = pag.allocSources(); it.hasNext(); ) {
            handleAllocNode( (AllocNode) it.next() );
        }
        int iteration = 1;
	boolean change;
	do {
	    change = false;
            TreeSet simpleSources = new TreeSet();
            for( Iterator sourceIt = pag.simpleSources(); sourceIt.hasNext(); ) {
            	simpleSources.add(sourceIt.next());
            }
            if( PaddleScene.v().options().verbose() ) {
                G.v().out.println( "Iteration "+(iteration++) );
            }
            for( Iterator it = simpleSources.iterator(); it.hasNext(); ) {
                change = handleSimples( (VarNode) it.next() ) | change;
            }
            
            for( Iterator srcIt = PaddleNumberers.v().varNodeNumberer().iterator(); srcIt.hasNext(); ) {
            
                final VarNode src = (VarNode) srcIt.next();
                p2sets.get(src).getNewSet().forall( new P2SetVisitor() {
                public final void visit( Node n ) {
                    ptout.add( src, (AllocNode) n );
                }} );
            }
            PaddleScene.v().updateCallGraph();

            for( Iterator tIt = newSimple.iterator(); tIt.hasNext(); ) {

                final Rsrc_dst.Tuple t = (Rsrc_dst.Tuple) tIt.next();
                change = true;
                PointsToSetReadOnly p2set = p2sets.get(t.src());
                if( p2set instanceof PointsToSetInternal ) {
                    ((PointsToSetInternal)p2set).unFlushNew();
                }
            }
            for( Iterator tIt = newLoad.iterator(); tIt.hasNext(); ) {
                final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
                change = true;
            }
            for( Iterator tIt = newStore.iterator(); tIt.hasNext(); ) {
                final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
                change = true;
                PointsToSetReadOnly p2set = p2sets.get(t.src());
                if( p2set instanceof PointsToSetInternal ) {
                    ((PointsToSetInternal)p2set).unFlushNew();
                }
            }
            for( Iterator tIt = newAlloc.iterator(); tIt.hasNext(); ) {
                final Robj_var.Tuple t = (Robj_var.Tuple) tIt.next();
                change = true;
                p2sets.make(t.var()).add( t.obj() );
            }
            if( change ) {
                new TopoSorter( pag, false ).sort();
            }
	    for( Iterator it = pag.loadSources(); it.hasNext(); ) {
                change = handleLoads( (FieldRefNode) it.next() ) | change;
	    }
	    for( Iterator it = pag.storeSources(); it.hasNext(); ) {
                change = handleStores( (VarNode) it.next() ) | change;
	    }
	} while( change );
    }

    /* End of public methods. */
    /* End of package methods. */

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleAllocNode( AllocNode src ) {
	boolean ret = false;
        for( Iterator targetIt = pag.allocLookup(src); targetIt.hasNext(); ) {
            final VarNode target = (VarNode) targetIt.next();
	    ret = p2sets.make(target).add( src ) | ret;
	}
	return ret;
    }

    protected final boolean handleSimples( VarNode src ) {
	boolean ret = false;
	PointsToSetReadOnly srcSet = p2sets.get(src);
	if( srcSet.isEmpty() ) return false;
        for( Iterator simpleTargetIt = pag.simpleLookup(src); simpleTargetIt.hasNext(); ) {
            final VarNode simpleTarget = (VarNode) simpleTargetIt.next();
	    ret = p2sets.make(simpleTarget).addAll( srcSet, null ) | ret;
	}
        return ret;
    }

    protected final boolean handleStores( VarNode src ) {
	boolean ret = false;
	final PointsToSetReadOnly srcSet = p2sets.get(src);
	if( srcSet.isEmpty() ) return false;
        for( Iterator storeTargetIt = pag.storeLookup(src); storeTargetIt.hasNext(); ) {
            final FieldRefNode storeTarget = (FieldRefNode) storeTargetIt.next();
            final PaddleField f = storeTarget.getField();
            ret = p2sets.get(storeTarget.getBase()).forall( new P2SetVisitor() {
            public final void visit( Node n ) {
                    AllocDotField nDotF = PaddleScene.v().nodeManager().makeAllocDotField( 
                        (AllocNode) n, f );
                    if( p2sets.make(nDotF).addAll( srcSet, null ) ) {
                        returnValue = true;
                    }
                }
            } ) | ret;
	}
        return ret;
    }

    protected final boolean handleLoads( final FieldRefNode src ) {
	boolean ret = false;
        final PaddleField f = src.getField();
        ret = p2sets.get(src.getBase()).forall( new P2SetVisitor() {
        public final void visit( Node n ) {
                AllocDotField nDotF = ((AllocNode)n).dot( f );
                if( nDotF == null ) return;
                PointsToSetReadOnly set = p2sets.get(nDotF);
                if( set.isEmpty() ) return;
                for( Iterator loadTargetIt = pag.loadLookup(src); loadTargetIt.hasNext(); ) {
                    final VarNode loadTarget = (VarNode) loadTargetIt.next();
                    if( p2sets.make(loadTarget).addAll( set, null ) ) {
                        returnValue = true;
                    }
                }
            }
        } ) | ret;
        return ret;
    }
}



