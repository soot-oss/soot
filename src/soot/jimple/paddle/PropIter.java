/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003, 2004 Ondrej Lhotak
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
import java.util.*;
import soot.options.PaddleOptions;

/** Propagates points-to sets along pointer assignment graph using iteration.
 * @author Ondrej Lhotak
 */

public final class PropIter extends AbsPropagator {
    public PropIter(  Rsrcc_src_dstc_dst simple,
            Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_dstc_dst_fld store,
            Robjc_obj_varc_var alloc,
            Qvarc_var_objc_obj propout,
            AbsPAG pag
 ) {
        super( simple, load, store, alloc, propout, pag );
    }
    private AbsP2Sets p2sets;
    /** Actually does the propagation. */
    public final boolean update() {
        p2sets = PaddleScene.v().p2sets;
        new TopoSorter( pag, false ).sort();
        for( Iterator it = pag.allocSources(); it.hasNext(); ) {
            handleContextAllocNode( (ContextAllocNode) it.next() );
        }
        newEdges();
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
                change = handleSimples( (ContextVarNode) it.next() ) | change;
            }
            
            for( Iterator srcIt = PaddleNumberers.v().contextVarNodeNumberer().iterator(); srcIt.hasNext(); ) {
            
                final ContextVarNode src = (ContextVarNode) srcIt.next();
                p2sets.get(src).getNewSet().forall( new P2SetVisitor() {
                public final void visit( ContextAllocNode n ) {
                	ContextAllocNode can = (ContextAllocNode) n;
                    ptout.add( src.ctxt(), src.var(), can.ctxt(), can.obj() );
                }} );
            }
            PaddleScene.v().updateCallGraph();
            if( newEdges() ) change = true;

            if( change ) {
                new TopoSorter( pag, false ).sort();
            }
	    for( Iterator it = pag.loadSources(); it.hasNext(); ) {
                change = handleLoads( (ContextFieldRefNode) it.next() ) | change;
	    }
	    for( Iterator it = pag.storeSources(); it.hasNext(); ) {
                change = handleStores( (ContextVarNode) it.next() ) | change;
	    }
	} while( change );
        return true;
    }
    private boolean newEdges() {
        boolean change = false;
        for( Iterator tIt = newSimple.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_dstc_dst.Tuple t = (Rsrcc_src_dstc_dst.Tuple) tIt.next();
            change = true;
            PointsToSetReadOnly p2set = p2sets.get(t.srcc(), t.src());
            if( p2set instanceof PointsToSetInternal ) {
                ((PointsToSetInternal)p2set).unFlushNew();
            }
        }
        for( Iterator tIt = newLoad.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_fld_dstc_dst.Tuple t = (Rsrcc_src_fld_dstc_dst.Tuple) tIt.next();
            change = true;
        }
        for( Iterator tIt = newStore.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_dstc_dst_fld.Tuple t = (Rsrcc_src_dstc_dst_fld.Tuple) tIt.next();
            change = true;
            PointsToSetReadOnly p2set = p2sets.get(t.srcc(), t.src());
            if( p2set instanceof PointsToSetInternal ) {
                ((PointsToSetInternal)p2set).unFlushNew();
            }
        }
        for( Iterator tIt = newAlloc.iterator(); tIt.hasNext(); ) {
            final Robjc_obj_varc_var.Tuple t = (Robjc_obj_varc_var.Tuple) tIt.next();
            change = true;
            p2sets.make(t.varc(), t.var()).add( t.objc(), t.obj() );
        }
        return change;
    }

    /* End of public methods. */
    /* End of package methods. */

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleContextAllocNode( ContextAllocNode src ) {
	boolean ret = false;
        for( Iterator targetIt = pag.allocLookup(src); targetIt.hasNext(); ) {
            final ContextVarNode target = (ContextVarNode) targetIt.next();
	    ret = p2sets.make(target).add( src ) | ret;
	}
	return ret;
    }

    protected final boolean handleSimples( ContextVarNode src ) {
	boolean ret = false;
	PointsToSetReadOnly srcSet = p2sets.get(src);
	if( srcSet.isEmpty() ) {
            return false;
        }
        for( Iterator simpleTargetIt = pag.simpleLookup(src); simpleTargetIt.hasNext(); ) {
            final ContextVarNode simpleTarget = (ContextVarNode) simpleTargetIt.next();
	    ret = p2sets.make(simpleTarget).addAll( srcSet, null ) | ret;
	}
        return ret;
    }

    protected final boolean handleStores( ContextVarNode src ) {
	boolean ret = false;
	final PointsToSetReadOnly srcSet = p2sets.get(src);
	if( srcSet.isEmpty() ) return false;
        for( Iterator storeTargetIt = pag.storeLookup(src); storeTargetIt.hasNext(); ) {
            final ContextFieldRefNode storeTarget = (ContextFieldRefNode) storeTargetIt.next();
            final PaddleField f = storeTarget.field();
            ret = p2sets.get(storeTarget.base()).forall( new P2SetVisitor() {
            public final void visit( ContextAllocNode n ) {
                    ContextAllocDotField nDotF = ContextAllocDotField.make( 
                        (ContextAllocNode) n, f );
                    if( p2sets.make(nDotF).addAll( srcSet, null ) ) {
                        returnValue = true;
                    }
                }
            } ) | ret;
	}
        return ret;
    }

    protected final boolean handleLoads( final ContextFieldRefNode src ) {
	boolean ret = false;
        final PaddleField f = src.field();
        ret = p2sets.get(src.base()).forall( new P2SetVisitor() {
        public final void visit( ContextAllocNode n ) {
                ContextAllocDotField nDotF = ((ContextAllocNode)n).dot( f );
                if( nDotF == null ) return;
                PointsToSetReadOnly set = p2sets.get(nDotF);
                if( set.isEmpty() ) return;
                for( Iterator loadTargetIt = pag.loadLookup(src); loadTargetIt.hasNext(); ) {
                    final ContextVarNode loadTarget = (ContextVarNode) loadTargetIt.next();
                    if( p2sets.make(loadTarget).addAll( set, null ) ) {
                        returnValue = true;
                    }
                }
            }
        } ) | ret;
        return ret;
    }
}



