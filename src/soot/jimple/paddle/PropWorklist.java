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

/** Propagates points-to sets along pointer assignment graph using a worklist.
 * @author Ondrej Lhotak
 */

public final class PropWorklist extends AbsPropagator {
    protected final Set varNodeWorkList = new TreeSet();
    private NodeManager nm = PaddleScene.v().nodeManager();

    public PropWorklist( Rsrc_dst simple,
            Rsrc_fld_dst load,
            Rsrc_fld_dst store,
            Robj_var alloc,
            Qvar_obj propout,
            AbsPAG pag ) {
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

        boolean verbose = PaddleScene.v().options().verbose();
	do {
            if( verbose ) {
                G.v().out.println( "Worklist has "+varNodeWorkList.size()+
                        " nodes." );
            }
            while( !varNodeWorkList.isEmpty() ) {
                VarNode src = (VarNode) varNodeWorkList.iterator().next();
                varNodeWorkList.remove( src );
                handleVarNode( src );
            }
            if( verbose ) {
                G.v().out.println( "Now handling field references" );
            }
            for( Iterator srcIt = pag.storeSources(); srcIt.hasNext(); ) {
                final VarNode src = (VarNode) srcIt.next();
                for( Iterator targetIt = pag.storeLookup(src); targetIt.hasNext(); ) {
                    final FieldRefNode target = (FieldRefNode) targetIt.next();
                    p2sets.make(target.getBase()).forall( new P2SetVisitor() {
                    public final void visit( Node n ) {
                            AllocDotField nDotF = nm.makeAllocDotField( 
                                (AllocNode) n, target.getField() );
                            p2sets.make(nDotF).addAll( p2sets.get(src), null );
                        }
                    } );
                }
            }
            HashSet edgesToPropagate = new HashSet();
	    for( Iterator it = pag.loadSources(); it.hasNext(); ) {
                handleFieldRefNode( (FieldRefNode) it.next(), edgesToPropagate );
	    }
            HashSet nodesToFlush = new HashSet();
            for( Iterator pairIt = edgesToPropagate.iterator(); pairIt.hasNext(); ) {
                final Object[] pair = (Object[]) pairIt.next();
                PointsToSetInternal nDotF = (PointsToSetInternal) pair[0];
		PointsToSetReadOnly newP2Set = nDotF.getNewSet();
                VarNode loadTarget = (VarNode) pair[1];
                if( p2sets.make(loadTarget).addAll( newP2Set, null ) ) {
                    varNodeWorkList.add( loadTarget );
                }
                nodesToFlush.add( nDotF );
            }
            for( Iterator nDotFIt = nodesToFlush.iterator(); nDotFIt.hasNext(); ) {
                final PointsToSetInternal nDotF = (PointsToSetInternal) nDotFIt.next();
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
        for( Iterator targetIt = pag.allocLookup(src); targetIt.hasNext(); ) {
            final VarNode target = (VarNode) targetIt.next();
	    if( p2sets.make(target).add( src ) ) {
                varNodeWorkList.add( target );
                ret = true;
            }
	}
	return ret;
    }
    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleVarNode( final VarNode src ) {
	boolean ret = false;

        
	final PointsToSetReadOnly newP2Set = p2sets.get(src).getNewSet();
	if( newP2Set.isEmpty() ) return false;

        newP2Set.forall( new P2SetVisitor() {

        public final void visit( Node n ) {
            ptout.add( src, (AllocNode) n );
        }} );
        PaddleScene.v().updateCallGraph();
        for( Iterator tIt = newSimple.iterator(); tIt.hasNext(); ) {
            final Rsrc_dst.Tuple t = (Rsrc_dst.Tuple) tIt.next();
            ret = true;
            if( p2sets.make(t.dst()).addAll( p2sets.get(t.src()), null ) )
                varNodeWorkList.add( t.dst() );
        }
        for( Iterator tIt = newAlloc.iterator(); tIt.hasNext(); ) {
            final Robj_var.Tuple t = (Robj_var.Tuple) tIt.next();
            ret = true;
            if( p2sets.make(t.var()).add( t.obj() ) )
                varNodeWorkList.add( t.var() );
        }

        for( Iterator simpleTargetIt = pag.simpleLookup(src); simpleTargetIt.hasNext(); ) {

            final VarNode simpleTarget = (VarNode) simpleTargetIt.next();
	    if( p2sets.make(simpleTarget).addAll( newP2Set, null ) ) {
                varNodeWorkList.add( simpleTarget );
                ret = true;
            }
	}

        for( Iterator storeTargetIt = pag.storeLookup(src); storeTargetIt.hasNext(); ) {

            final FieldRefNode storeTarget = (FieldRefNode) storeTargetIt.next();
            final PaddleField f = storeTarget.getField();
            ret = p2sets.get(storeTarget.getBase()).forall( new P2SetVisitor() {
            public final void visit( Node n ) {
                    AllocDotField nDotF = nm.makeAllocDotField( 
                        (AllocNode) n, f );
                    if( p2sets.make(nDotF).addAll( newP2Set, null ) ) {
                        returnValue = true;
                    }
		}
	    } ) | ret;
        }

        final HashSet storesToPropagate = new HashSet();
        final HashSet loadsToPropagate = new HashSet();
	Collection fieldRefs = src.getAllFieldRefs();
	for( Iterator frIt = fieldRefs.iterator(); frIt.hasNext(); ) {
	    final FieldRefNode fr = (FieldRefNode) frIt.next();
	    final PaddleField field = fr.getField();
            for( Iterator storeSourceIt = pag.storeInvLookup(fr); storeSourceIt.hasNext(); ) {
                final VarNode storeSource = (VarNode) storeSourceIt.next();
                newP2Set.forall( new P2SetVisitor() {
                public final void visit( Node n ) {
                        AllocDotField nDotF = nm.makeAllocDotField(
                            (AllocNode) n, field );
                        Node[] pair = { storeSource, nDotF };
                        storesToPropagate.add( pair );
                    }
                } );
            }

            for( Iterator loadTargetIt = pag.loadLookup(fr); loadTargetIt.hasNext(); ) {

                final VarNode loadTarget = (VarNode) loadTargetIt.next();
                newP2Set.forall( new P2SetVisitor() {
                public final void visit( Node n ) {
                        AllocDotField nDotF = nm.findAllocDotField(
                            (AllocNode) n, field );
                        if( nDotF != null ) {
                            Node[] pair = { nDotF, loadTarget };
                            loadsToPropagate.add( pair );
                        }
                    }
                } );
            }
	}
	p2sets.make(src).flushNew();
        for( Iterator pIt = storesToPropagate.iterator(); pIt.hasNext(); ) {
            final Node[] p = (Node[]) pIt.next();
            VarNode storeSource = (VarNode) p[0];
            AllocDotField nDotF = (AllocDotField) p[1];
            if( p2sets.make(nDotF).addAll( p2sets.get(storeSource), null ) ) {
                ret = true;
            }
        }
        for( Iterator pIt = loadsToPropagate.iterator(); pIt.hasNext(); ) {
            final Node[] p = (Node[]) pIt.next();
            AllocDotField nDotF = (AllocDotField) p[0];
            VarNode loadTarget = (VarNode) p[1];
            if( p2sets.make(loadTarget).
                addAll( p2sets.get(nDotF), null ) ) {
                varNodeWorkList.add( loadTarget );
                ret = true;
            }
        }
	return ret;
    }

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final void handleFieldRefNode( final FieldRefNode src, 
            final HashSet edgesToPropagate ) {
        final PaddleField field = src.getField();

        for( Iterator loadTargetIt = pag.loadLookup(src); loadTargetIt.hasNext(); ) {

            final VarNode loadTarget = (VarNode) loadTargetIt.next();
            p2sets.get(src.getBase()).forall( new P2SetVisitor() {
            public final void visit( Node n ) {
                    AllocDotField nDotF = nm.findAllocDotField( 
                        (AllocNode) n, field );
                    if( nDotF != null ) {
                        PointsToSetReadOnly p2Set = p2sets.get(nDotF);
                        if( !p2Set.getNewSet().isEmpty() ) {
                            Object[] pair = { p2Set, loadTarget };
                            edgesToPropagate.add( pair );
                        }
                    }
                }
            } );
        }
    }
    
}



