/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003 Ondrej Lhotak
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
import soot.util.*;
import soot.util.queue.*;
import java.util.*;
import soot.options.PaddleOptions;
/** Propagates points-to sets along pointer assignment graph using an
 * alias edge analysis.
 * @author Ondrej Lhotak
 */

public final class PropAlias extends AbsPropagator {
    protected final Set varNodeWorkList = new TreeSet();
    protected Set aliasWorkList;
    protected Set fieldRefWorkList = new HashSet();
    protected Set outFieldRefWorkList = new HashSet();

    public PropAlias( Rsrc_dst simple, Rsrc_fld_dst load, Rsrc_fld_dst store,
            Robj_var alloc, Qvar_obj propout, AbsPAG pag ) {
        super( simple, load, store, alloc, propout, pag );
        inSets = new P2SetMap( PaddleNumberers.v().fieldRefNodeNumberer() );
        outSets = new P2SetMap( PaddleNumberers.v().fieldRefNodeNumberer() );
    }

    private AbsP2Sets p2sets;
    /** Actually does the propagation. */
    public final void update() {
        p2sets = PaddleScene.v().p2sets;
        new TopoSorter( pag, false ).sort();
        for( Iterator frIt = pag.loadSources(); frIt.hasNext(); ) {
            final FieldRefNode fr = (FieldRefNode) frIt.next();
            fieldToBase.put( fr.getField(), fr.getBase() );
        }
        for( Iterator frIt = pag.storeInvSources(); frIt.hasNext(); ) {
            final FieldRefNode fr = (FieldRefNode) frIt.next();
            fieldToBase.put( fr.getField(), fr.getBase() );
        }
	for( Iterator it = pag.allocSources(); it.hasNext(); ) {
	    handleAllocNode( (AllocNode) it.next() );
	}

        boolean verbose = PaddleScene.v().options().verbose();
	do {
            if( verbose ) {
                G.v().out.println( "Worklist has "+varNodeWorkList.size()+
                        " nodes." );
            }
            aliasWorkList = new HashSet();
            while( !varNodeWorkList.isEmpty() ) {
                VarNode src = (VarNode) varNodeWorkList.iterator().next();
                varNodeWorkList.remove( src );
                aliasWorkList.add( src );
                handleVarNode( src );
            }
            if( verbose ) {
                G.v().out.println( "Now handling field references" );
            }

            for( Iterator srcIt = aliasWorkList.iterator(); srcIt.hasNext(); ) {

                final VarNode src = (VarNode) srcIt.next();
                for( Iterator srcFrIt = src.getAllFieldRefs().iterator(); srcFrIt.hasNext(); ) {
                    final FieldRefNode srcFr = (FieldRefNode) srcFrIt.next();
                    PaddleField field = srcFr.getField();
                    for( Iterator dstIt = fieldToBase.get( field ).iterator(); dstIt.hasNext(); ) {
                        final VarNode dst = (VarNode) dstIt.next();
                        if( p2sets.get(src).hasNonEmptyIntersection(
                                    p2sets.get(dst) ) ) {
                            FieldRefNode dstFr = dst.dot( field );
                            aliasEdges.put( srcFr, dstFr );
                            aliasEdges.put( dstFr, srcFr );
                            fieldRefWorkList.add( srcFr );
                            fieldRefWorkList.add( dstFr );
                            if( outSets.make(dstFr).addAll( 
                                    inSets.get(srcFr).getOldSet(), null ) ) {
                                outFieldRefWorkList.add( dstFr );
                            }
                            if( outSets.make(srcFr).addAll( 
                                    inSets.get(dstFr).getOldSet(), null ) ) {
                                outFieldRefWorkList.add( srcFr );
                            }
                        }
                    }
                }
            }
            for( Iterator srcIt = fieldRefWorkList.iterator(); srcIt.hasNext(); ) {
                final FieldRefNode src = (FieldRefNode) srcIt.next();
                for( Iterator dstIt = aliasEdges.get( src ).iterator(); dstIt.hasNext(); ) {
                    final FieldRefNode dst = (FieldRefNode) dstIt.next();
                    if( outSets.make( dst ).addAll( inSets.get(src).getNewSet(), null ) ) {
                        outFieldRefWorkList.add( dst );
                    }
                }
                inSets.make(src).flushNew();
            }
            fieldRefWorkList = new HashSet();
            for( Iterator srcIt = outFieldRefWorkList.iterator(); srcIt.hasNext(); ) {
                final FieldRefNode src = (FieldRefNode) srcIt.next();
                PointsToSetReadOnly set = outSets.get(src).getNewSet();
                if( set.isEmpty() ) continue;
                for( Iterator targetIt = pag.loadLookup(src); targetIt.hasNext(); ) {
                    final VarNode target = (VarNode) targetIt.next();
                    if( p2sets.make(target).addAll( set, null ) ) {
                        addToWorklist( target );
                    }
                }
                outSets.make( src ).flushNew();
            }
            outFieldRefWorkList = new HashSet();
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
                addToWorklist( target );
                ret = true;
            }
	}
	return ret;
    }
    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleVarNode( final VarNode src ) {
	boolean ret = false;

	final PointsToSetReadOnly newP2Set = inSets.get(src).getNewSet();
	if( newP2Set.isEmpty() ) return false;

        newP2Set.forall( new P2SetVisitor() {

        public final void visit( Node n ) {
            ptout.add( src, (AllocNode) n );
        }} );
        PaddleScene.v().updateCallGraph();
        for( Iterator tIt = newSimple.iterator(); tIt.hasNext(); ) {
            final Rsrc_dst.Tuple t = (Rsrc_dst.Tuple) tIt.next();
            ret = true;
            if( outSets.make(t.dst()).addAll( inSets.get(t.src()), null ) )
                addToWorklist( t.dst() );
        }
        for( Iterator tIt = newAlloc.iterator(); tIt.hasNext(); ) {
            final Robj_var.Tuple t = (Robj_var.Tuple) tIt.next();
            ret = true;
            if( p2sets.make(t.var()).add( t.obj() ) )
                addToWorklist( t.var() );
        }
        for( Iterator tIt = newLoad.iterator(); tIt.hasNext(); ) {
            final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
            ret = true;
            if( fieldToBase.put( t.fld(), t.src() ) ) {
                aliasWorkList.add( t.src() );
            }
        }
        for( Iterator tIt = newStore.iterator(); tIt.hasNext(); ) {
            final Rsrc_fld_dst.Tuple t = (Rsrc_fld_dst.Tuple) tIt.next();
            ret = true;
            if( fieldToBase.put( t.fld(), t.dst() ) ) {
                aliasWorkList.add( t.dst() );
            }
        }

        for( Iterator simpleTargetIt = pag.simpleLookup(src); simpleTargetIt.hasNext(); ) {

            final VarNode simpleTarget = (VarNode) simpleTargetIt.next();
	    if( p2sets.make(simpleTarget).addAll( newP2Set, null ) ) {
                addToWorklist( simpleTarget );
                ret = true;
            }
	}

        for( Iterator storeTargetIt = pag.storeLookup(src); storeTargetIt.hasNext(); ) {

            final FieldRefNode storeTarget = (FieldRefNode) storeTargetIt.next();
            if( inSets.make(storeTarget).addAll( newP2Set, null ) ) {
                fieldRefWorkList.add( storeTarget );
                ret = true;
            }
        }

	inSets.make(src).flushNew();
	return ret;
    }

    private boolean addToWorklist( VarNode n ) {
        return varNodeWorkList.add( n );
    }

    protected MultiMap fieldToBase = new HashMultiMap();
    protected MultiMap aliasEdges = new HashMultiMap();
    protected P2SetMap inSets;
    protected P2SetMap outSets;
}



