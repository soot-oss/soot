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
import soot.util.*;
import java.util.*;
import soot.options.PaddleOptions;
/** Propagates points-to sets along pointer assignment graph using an
 * alias edge analysis.
 * @author Ondrej Lhotak
 */

public final class PropAlias extends AbsPropagator {
    public PropAlias( Rsrcc_src_dstc_dst simple, Rsrcc_src_fld_dstc_dst load, Rsrcc_src_dstc_dst_fld store,
            Robjc_obj_varc_var alloc, Qvarc_var_objc_obj propout, AbsPAG pag ) {
        super( simple, load, store, alloc, propout, pag );
        inSets = new P2SetMap();
        outSets = new P2SetMap();
    }

    private AbsP2Sets p2sets;
    /** Actually does the propagation. */
    public final boolean update() {
        boolean ret = false;

        p2sets = PaddleScene.v().p2sets;
        new TopoSorter( pag, false ).sort();
        varNodeWorkList.heapify();
        /*
        for( Iterator frIt = pag.loadSources(); frIt.hasNext(); ) {
            final ContextFieldRefNode fr = (ContextFieldRefNode) frIt.next();
            fieldToBase.put( fr.field(), fr.base() );
        }
        for( Iterator frIt = pag.storeInvSources(); frIt.hasNext(); ) {
            final ContextFieldRefNode fr = (ContextFieldRefNode) frIt.next();
            fieldToBase.put( fr.field(), fr.base() );
        }
	for( Iterator it = pag.allocSources(); it.hasNext(); ) {
	    handleContextAllocNode( (ContextAllocNode) it.next() );
	}
        */
        if( newEdges() ) {
            ret = true;
        }

        boolean verbose = PaddleScene.v().options().verbose();
        if( verbose ) {
            G.v().out.println( "Worklist has "+varNodeWorkList.size()+
                    " nodes." );
        }
        while( !varNodeWorkList.isEmpty() ) {
            ContextVarNode src = (ContextVarNode) varNodeWorkList.removeMin();
            addToAliasWorkList( src );
            if( handleContextVarNode( src ) ) {
                ret = true;
            }
        }
        if( verbose ) {
            G.v().out.println( "Now handling field references" );
        }

        for( Iterator srcIt = aliasWorkList.iterator(); srcIt.hasNext(); ) {

            final ContextVarNode src = (ContextVarNode) srcIt.next();
            for( Iterator srcFrIt = src.fields(); srcFrIt.hasNext(); ) {
                final ContextFieldRefNode srcFr = (ContextFieldRefNode) srcFrIt.next();
                PaddleField field = srcFr.field();
                for( Iterator dstIt = fieldToBase.get( field ).iterator(); dstIt.hasNext(); ) {
                    final ContextVarNode dst = (ContextVarNode) dstIt.next();
                    if( p2setsGet(src).hasNonEmptyIntersection(
                                p2setsGet(dst) ) ) {
                        ret = true;
                        ContextFieldRefNode dstFr = dst.dot( field );
                        aliasEdges.put( srcFr, dstFr );
                        aliasEdges.put( dstFr, srcFr );
                        addToFieldRefWorkList( srcFr );
                        addToFieldRefWorkList( dstFr );
                        if( outSetsMake(dstFr).addAll( 
                                inSetsGet(srcFr).getOldSet(), null ) ) {
                            addToOutFieldRefWorkList( dstFr );
                        }
                        if( outSetsMake(srcFr).addAll( 
                                inSetsGet(dstFr).getOldSet(), null ) ) {
                            addToOutFieldRefWorkList( srcFr );
                        }
                    }
                }
            }
        }
        aliasWorkList = new HashSet();
        for( Iterator srcIt = fieldRefWorkList.iterator(); srcIt.hasNext(); ) {
            final ContextFieldRefNode src = (ContextFieldRefNode) srcIt.next();
            for( Iterator dstIt = aliasEdges.get( src ).iterator(); dstIt.hasNext(); ) {
                final ContextFieldRefNode dst = (ContextFieldRefNode) dstIt.next();
                if( outSetsMake( dst ).addAll( inSetsGet(src).getNewSet(), null ) ) {
                    addToOutFieldRefWorkList( dst );
                }
            }
            inSetsMake(src).flushNew();
        }
        fieldRefWorkList = new HashSet();
        for( Iterator srcIt = outFieldRefWorkList.iterator(); srcIt.hasNext(); ) {
            final ContextFieldRefNode src = (ContextFieldRefNode) srcIt.next();
            PointsToSetReadOnly set = outSetsGet(src).getNewSet();
            if( set.isEmpty() ) continue;
            outSetsMake( src ).flushNew();
            for( Iterator targetIt = pag.loadLookup(src); targetIt.hasNext(); ) {
                final ContextVarNode target = (ContextVarNode) targetIt.next();
                if( p2setsMake(target).addAll( set, null ) ) {
                    addToVarNodeWorkList( target );
                    ret = true;
                }
            }
        }
        outFieldRefWorkList = new HashSet();
        return ret;
    }

    /* End of public methods. */
    /* End of package methods. */

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleContextAllocNode( ContextAllocNode src ) {
	boolean ret = false;
        for( Iterator targetIt = pag.allocLookup(src); targetIt.hasNext(); ) {
            final ContextVarNode target = (ContextVarNode) targetIt.next();
	    if( p2setsMake(target).add( src ) ) {
                addToVarNodeWorkList( target );
                ret = true;
            }
	}
	return ret;
    }
    private boolean newEdges() {
        boolean ret = false;
        for( Iterator tIt = newSimple.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_dstc_dst.Tuple t = (Rsrcc_src_dstc_dst.Tuple) tIt.next();
            ret = true;
            if( p2setsMake(t.dstc(), t.dst()).addAll( p2setsGet(t.srcc(), t.src()), null ) )
                addToVarNodeWorkList( ContextVarNode.make(t.dstc(), t.dst()) );
        }
        for( Iterator tIt = newAlloc.iterator(); tIt.hasNext(); ) {
            final Robjc_obj_varc_var.Tuple t = (Robjc_obj_varc_var.Tuple) tIt.next();
            ret = true;
            ContextVarNode cvn = ContextVarNode.make(t.varc(), t.var());
            if( p2setsMake(cvn).add( t.objc(), t.obj() ) )
                addToVarNodeWorkList( cvn );
        }
        for( Iterator tIt = newLoad.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_fld_dstc_dst.Tuple t = (Rsrcc_src_fld_dstc_dst.Tuple) tIt.next();
            ret = true;
            ContextVarNode srccvn = ContextVarNode.make(t.srcc(), t.src());
            if( fieldToBase.put( t.fld(), srccvn ) ) {
                addToAliasWorkList( srccvn );
            }
            ContextFieldRefNode src = srccvn.dot(t.fld());
            ContextVarNode dst = ContextVarNode.make(t.dstc(), t.dst());
            if( p2setsMake(dst).addAll(outSetsGet(src), null) ) {
                addToVarNodeWorkList(dst);
                ret = true;
            }
        }
        for( Iterator tIt = newStore.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_dstc_dst_fld.Tuple t = (Rsrcc_src_dstc_dst_fld.Tuple) tIt.next();
            ret = true;
            ContextVarNode dstcvn = ContextVarNode.make(t.dstc(), t.dst());
            if( fieldToBase.put( t.fld(), dstcvn ) ) {
                addToAliasWorkList( dstcvn );
            }
            ContextFieldRefNode storeTarget = dstcvn.dot(t.fld());
            ContextVarNode src = ContextVarNode.make(t.srcc(), t.src());
            if( inSetsMake(storeTarget).addAll( p2setsGet(src), null ) ) {
                addToFieldRefWorkList( storeTarget );
                ret = true;
            }
        }
        return ret;
    }

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleContextVarNode( final ContextVarNode src ) {
	boolean ret = false;

	final PointsToSetReadOnly newP2Set = p2setsGet(src).getNewSet();
	if( newP2Set.isEmpty() ) return false;
	p2setsMake(src).flushNew();

        newP2Set.forall( new P2SetVisitor() {

        public final void visit( ContextAllocNode n ) {
        	ContextAllocNode can = (ContextAllocNode) n;
            ptout.add( src.ctxt(), src.var(), can.ctxt(), can.obj() );
        }} );
        //PaddleScene.v().updateCallGraph();
        //if( newEdges() ) ret = true;

        for( Iterator simpleTargetIt = pag.simpleLookup(src); simpleTargetIt.hasNext(); ) {

            final ContextVarNode simpleTarget = (ContextVarNode) simpleTargetIt.next();
	    if( p2setsMake(simpleTarget).addAll( newP2Set, null ) ) {
                addToVarNodeWorkList( simpleTarget );
                ret = true;
            }
	}

        for( Iterator storeTargetIt = pag.storeLookup(src); storeTargetIt.hasNext(); ) {

            final ContextFieldRefNode storeTarget = (ContextFieldRefNode) storeTargetIt.next();
            if( inSetsMake(storeTarget).addAll( newP2Set, null ) ) {
                addToFieldRefWorkList( storeTarget );
                ret = true;
            }
        }

	return ret;
    }

    protected MultiMap fieldToBase = new HashMultiMap();
    protected MultiMap aliasEdges = new HashMultiMap();
    protected PointsToSetInternal inSetsMake( ContextFieldRefNode cfrn ) {
        return inSets.make(cfrn);
    }
    protected PointsToSetReadOnly inSetsGet( ContextFieldRefNode cfrn ) {
        return inSets.get(cfrn);
    }
    protected P2SetMap inSets;
    protected PointsToSetInternal outSetsMake( ContextFieldRefNode cfrn ) {
        return outSets.make(cfrn);
    }
    protected PointsToSetReadOnly outSetsGet( ContextFieldRefNode cfrn ) {
        return outSets.get(cfrn);
    }
    protected P2SetMap outSets;
    protected final Heap varNodeWorkList = new Heap(new Heap.Keys() {
        public int key(Object o) {
            ContextVarNode cvn = (ContextVarNode) o;
            return cvn.finishingNumber();
        }
    });
    private boolean addToVarNodeWorkList( ContextVarNode cvn ) {
        return varNodeWorkList.add(cvn); 
    }
    protected Set aliasWorkList = new HashSet();
    private boolean addToAliasWorkList( ContextVarNode cvn ) {
        return aliasWorkList.add(cvn); 
    }
    protected Set fieldRefWorkList = new HashSet();
    private boolean addToFieldRefWorkList( ContextFieldRefNode cvn ) {
        return fieldRefWorkList.add(cvn); 
    }
    protected Set outFieldRefWorkList = new HashSet();
    private boolean addToOutFieldRefWorkList( ContextFieldRefNode cvn ) {
        return outFieldRefWorkList.add(cvn); 
    }
    
    private PointsToSetReadOnly p2setsGet( Context ctxt, VarNode vn ) {
        return p2sets.get(ctxt, vn);
    }
    private PointsToSetInternal p2setsMake( Context ctxt, VarNode vn ) {
        return p2sets.make(ctxt, vn);
    }
    private PointsToSetReadOnly p2setsGet( ContextVarNode cvn ) {
        return p2sets.get(cvn);
    }
    private PointsToSetInternal p2setsMake( ContextVarNode cvn ) {
        return p2sets.make(cvn);
    }
}



