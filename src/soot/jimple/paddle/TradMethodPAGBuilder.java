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

package soot.jimple.paddle;
import soot.*;
import soot.jimple.paddle.queue.*;
import java.util.*;

/** Creates intra-procedural pointer assignment edges.
 * @author Ondrej Lhotak
 */
public class TradMethodPAGBuilder extends AbsMethodPAGBuilder
{ 
    TradMethodPAGBuilder( 
        Rctxt_method in,
        Qsrc_dst simple,
        Qsrc_fld_dst load,
        Qsrc_fld_dst store,
        Qobj_var alloc ) {
        super(in, simple, load, store, alloc);
    }
    private Map mpags = new HashMap();
    protected NodeManager nm = PaddleScene.v().nodeManager();
    public MethodPAG v( SootMethod m ) {
        MethodPAG mpag = (MethodPAG) mpags.get( m );
        if( mpag == null ) {
            mpag = new MethodPAG( m );
            mpag.build();
            mpags.put( m, mpag );
        }
        return mpag;
    }
    public void update() {
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
            MethodPAG mpag = v( t.method() );
            for( Iterator eIt = mpag.simple().iterator(); eIt.hasNext(); ) {
                final Rsrc_dst.Tuple e = (Rsrc_dst.Tuple) eIt.next();
                simple.add( parm( e.src(), t.ctxt() ),
                            parm( e.dst(), t.ctxt() ) );
            }
            for( Iterator eIt = mpag.load().iterator(); eIt.hasNext(); ) {
                final Rsrc_fld_dst.Tuple e = (Rsrc_fld_dst.Tuple) eIt.next();
                if( e.fld() == null ) throw new RuntimeException( ""+e );
                VarNode base = parm( e.src(), t.ctxt() );
                nm.makeFieldRefNode( base, e.fld() );
                load.add( base,
                          e.fld(),
                          parm( e.dst(), t.ctxt() ) );
            }
            for( Iterator eIt = mpag.store().iterator(); eIt.hasNext(); ) {
                final Rsrc_fld_dst.Tuple e = (Rsrc_fld_dst.Tuple) eIt.next();
                VarNode base = parm( e.dst(), t.ctxt() );
                nm.makeFieldRefNode( base, e.fld() );
                store.add( parm( e.src(), t.ctxt() ),
                          e.fld(),
                          base );
            }
            for( Iterator eIt = mpag.alloc().iterator(); eIt.hasNext(); ) {
                final Robj_var.Tuple e = (Robj_var.Tuple) eIt.next();
                alloc.add( e.obj(), parm( e.var(), t.ctxt() ) );
            }
        }
    }
    private VarNode parm( VarNode vn, Context cn ) {
        if( vn instanceof LocalVarNode ) return ((LocalVarNode) vn).context( cn );
        return vn;
    }
}


