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
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.*;
import java.util.*;

/** Collapses nodes that are members of simple trees (EBBs)
 * in the pointer assignment graph.
 * @author Ondrej Lhotak
 */

public class EBBCollapser {
    /** Actually collapse the EBBs in the PAG. */
    public void collapse() {
        boolean verbose = pag.getOpts().verbose();
        if( verbose ) {
            System.out.println( "Total VarNodes: "+pag.allVarNodes().size()+". Collapsing EBBs..." );
        }
        collapseAlloc();
        collapseLoad();
        collapseSimple();
        if( verbose ) {
            System.out.println( ""+numCollapsed+" nodes were collapsed." );
        }
    }
    public EBBCollapser( PAG pag ) {
        this.pag = pag;
    }
    
    /* End of public methods. */
    /* End of package methods. */

    protected int numCollapsed = 0;
    protected PAG pag;
    protected void collapseAlloc() {
        for( Iterator nIt = pag.allocSources().iterator(); nIt.hasNext(); ) {
            final AllocNode n = (AllocNode) nIt.next();
            Node[] succs = pag.allocLookup( n );
            VarNode firstSucc = null;
            for( int i = 0; i < succs.length; i++ ) {
                VarNode succ = (VarNode) succs[i];
                if( pag.allocInvLookup( succ ).length > 1 ) continue;
                if( pag.loadInvLookup( succ ).length > 0 ) continue;
                if( pag.simpleInvLookup( succ ).length > 0 ) continue;
                if( firstSucc == null ) {
                    firstSucc = succ;
                } else {
                    if( firstSucc.getType().equals( succ.getType() ) ) {
                        firstSucc.mergeWith( succ );
                        numCollapsed++;
                    }
                }
            }
        }
    }
    protected void collapseSimple() {
        boolean verbose = pag.getOpts().verbose();
        boolean change;
        do {
            change = false;
            for( Iterator nIt = new LinkedList( pag.simpleSources() ).iterator(); nIt.hasNext(); ) {
                final VarNode n = (VarNode) nIt.next();
                Type nType = n.getType();
                Node[] succs = pag.simpleLookup( n );
                for( int i = 0; i < succs.length; i++ ) {
                    VarNode succ = (VarNode) succs[i];
                    Type sType = succ.getType();
                    if( !PointsToSetInternal
                            .castNeverFails( nType, sType ) ) continue;
                    if( pag.allocInvLookup( succ ).length > 0 ) continue;
                    if( pag.loadInvLookup( succ ).length > 0 ) continue;
                    if( pag.simpleInvLookup( succ ).length > 1 ) continue;
                    n.mergeWith( succ );
                    change = true;
                    numCollapsed++;
                }
            }
        } while( change );
    }
    protected void collapseLoad() {
        for( Iterator nIt = new LinkedList( pag.loadSources() ).iterator(); nIt.hasNext(); ) {
            final FieldRefNode n = (FieldRefNode) nIt.next();
            Type nType = n.getType();
            Node[] succs = pag.loadLookup( n );
            Node firstSucc = null;
            HashMap typeToSucc = new HashMap();
            for( int i = 0; i < succs.length; i++ ) {
                VarNode succ = (VarNode) succs[i];
                Type sType = succ.getType();
                if( pag.allocInvLookup( succ ).length > 0 ) continue;
                if( pag.loadInvLookup( succ ).length > 1 ) continue;
                if( pag.simpleInvLookup( succ ).length > 0 ) continue;
                if( PointsToSetInternal.castNeverFails( nType, sType ) ) {
                    if( firstSucc == null ) {
                        firstSucc = succ;
                    } else {
                        firstSucc.mergeWith( succ );
                        numCollapsed++;
                    }
                } else {
                    VarNode rep = (VarNode) typeToSucc.get( succ.getType() );
                    if( rep == null ) {
                        typeToSucc.put( succ.getType(), succ );
                    } else {
                        rep.mergeWith( succ );
                        numCollapsed++;
                    }
                }
            }
        }
    }
}



