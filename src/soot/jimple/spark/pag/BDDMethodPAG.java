/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.spark.pag;
import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.jimple.spark.*;
import soot.jimple.spark.builder.*;
import soot.jimple.spark.internal.*;
import soot.util.*;
import soot.util.queue.*;
import soot.toolkits.scalar.Pair;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;
import soot.relations.*;


/** Part of a pointer assignment graph for a single method.
 * @author Ondrej Lhotak
 */
public final class BDDMethodPAG extends AbstractMethodPAG {
    private BDDPAG pag;
    public AbstractPAG pag() { return pag; }

    public static BDDMethodPAG v( BDDPAG pag, SootMethod m ) {
        BDDMethodPAG ret = (BDDMethodPAG) G.v().MethodPAG_methodToPag.get( m );
        if( ret == null ) { 
            ret = new BDDMethodPAG( pag, m );
            G.v().MethodPAG_methodToPag.put( m, ret );
        }
        return ret;
    }
    protected BDDMethodPAG( BDDPAG pag, SootMethod m ) {
        this.pag = pag;
        this.method = m;
        this.nodeFactory = new MethodNodeFactory( pag, this );
	internalEdgeSet = pag.edgeSet.sameDomains();
	inEdgeSet = pag.edgeSet.sameDomains();
	outEdgeSet = pag.edgeSet.sameDomains();
        stores = pag.stores.sameDomains();
        loads = pag.loads.sameDomains();
        alloc = pag.alloc.sameDomains();
    }

    /** Adds this method to the main PAG, with all VarNodes parameterized by
     * varNodeParameter. */
    public void addToPAG( Object varNodeParameter ) {
        if( varNodeParameter != null ) throw new RuntimeException( "NYI" );
        if( hasBeenAdded ) return;
        hasBeenAdded = true;
        pag.edgeSet.eqUnion( pag.edgeSet, internalEdgeSet );
        pag.edgeSet.eqUnion( pag.edgeSet, inEdgeSet );
        pag.edgeSet.eqUnion( pag.edgeSet, outEdgeSet );
        pag.stores.eqUnion( pag.stores, stores );
        pag.loads.eqUnion( pag.loads, loads );
        pag.alloc.eqUnion( pag.alloc, alloc );
    }
    private static Numberable[] box ( Numberable n1, Numberable n2 ) {
        Numberable[] ret = { n1, n2 };
        return ret;
    }
    private static Numberable[] box ( Numberable n1, Numberable n2, Numberable n3 ) {
        Numberable[] ret = { n1, n2, n3 };
        return ret;
    }
    public void addInternalEdge( Node src, Node dst ) {
        addEdge( src, dst, internalEdgeSet );
    }

    public void addInEdge( Node src, Node dst ) {
        addEdge( src, dst, inEdgeSet );
    }

    public void addOutEdge( Node src, Node dst ) {
        addEdge( src, dst, outEdgeSet );
    }

    private void addEdge( Node src, Node dst, Relation edgeSet ) {
        if( src instanceof VarNode ) {
            if( dst instanceof VarNode ) {
                edgeSet.add( pag.src, (VarNode) src,
                             pag.dst, (VarNode) dst );
            } else {
                FieldRefNode fdst = (FieldRefNode) dst;
                stores.add( pag.src, (VarNode) src,
                            pag.dst, fdst.getBase(),
                            pag.fld, fdst.getField() );
            }
        } else if( src instanceof FieldRefNode ) {
            FieldRefNode fsrc = (FieldRefNode) src;
            loads.add( pag.src, fsrc.getBase(),
                       pag.fld, fsrc.getField(),
                       pag.dst, (VarNode) dst );
        } else {
            alloc.add( pag.obj, (AllocNode) src,
                       pag.var, (VarNode) dst );
        }
    }

    final public Relation internalEdgeSet;
    final public Relation inEdgeSet;
    final public Relation outEdgeSet;
    final public Relation stores;
    final public Relation loads;
    final public Relation alloc;

}

