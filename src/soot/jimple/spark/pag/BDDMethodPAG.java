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
        this.parms = new StandardParms( pag, this );
        es = new Relation( pag.es_src, pag.es_dst );
        st = new Relation( pag.st_src, pag.st_fd, pag.st_dst );
        ld = new Relation( pag.ld_src, pag.ld_fd, pag.ld_dst );
        alloc = new Relation( pag.pt_obj, pag.pt_var );
    }
    /** Adds this method to the main PAG, with all VarNodes parameterized by
     * varNodeParameter. */
    public void addToPAG( Object varNodeParameter ) {
        if( varNodeParameter != null ) throw new RuntimeException( "NYI" );
        if( hasBeenAdded ) return;
        hasBeenAdded = true;
        pag.es.unionEq( es );
        pag.st.unionEq( st );
        pag.ld.unionEq( ld );
        pag.alloc.unionEq( alloc );
    }
    private static Numberable[] box ( Numberable n1, Numberable n2 ) {
        Numberable[] ret = { n1, n2 };
        return ret;
    }
    private static Numberable[] box ( Numberable n1, Numberable n2, Numberable n3 ) {
        Numberable[] ret = { n1, n2, n3 };
        return ret;
    }
    public void addEdge( Node src, Node dst ) {
        if( src instanceof VarNode ) {
            if( dst instanceof VarNode ) {
                es.add( (VarNode) src, (VarNode) dst );
            } else {
                FieldRefNode fdst = (FieldRefNode) dst;
                st.add( (VarNode) src, fdst.getField(), fdst.getBase() );
            }
        } else if( src instanceof FieldRefNode ) {
            FieldRefNode fsrc = (FieldRefNode) src;
            ld.add( fsrc.getBase(), fsrc.getField(), (VarNode) dst );
        } else {
            alloc.add( (AllocNode) src, (VarNode) dst );
        }
    }

    public Relation es;
    public Relation st;
    public Relation ld;
    public Relation alloc;

}

