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
import soot.jimple.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;

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
    protected NodeManager nm = PaddleScene.v().nodeManager();
    public boolean update() {
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
            build(t.method());
        }
        return true;
    }
    protected void build(SootMethod method) {
        MethodNodeFactory nf = new MethodNodeFactory(method);
        if( method.isNative() ) {
            if( PaddleScene.v().options().simulate_natives() ) {
                buildNative(method, nf);
            }
        } else {
            if( method.isConcrete() && !method.isPhantom() ) {
                buildNormal(method, nf);
            }
        }
        nf.addMiscEdges();
    }
    protected void buildNormal( SootMethod method, MethodNodeFactory nf ) {
        Body b = method.retrieveActiveBody();
        Iterator unitsIt = b.getUnits().iterator();
        while( unitsIt.hasNext() )
        {
            Stmt s = (Stmt) unitsIt.next();
            nf.handleStmt( s );
        }
    }
    protected void buildNative( SootMethod method, MethodNodeFactory nf ) {
        Node thisNode = null;
        Node retNode = null; 
        if( !method.isStatic() ) { 
	    thisNode = nf.caseThis();
        }
        if( method.getReturnType() instanceof RefLikeType ) {
	    retNode = nf.caseRet();
	}
        Node[] args = new Node[ method.getParameterCount() ];
        for( int i = 0; i < method.getParameterCount(); i++ ) {
            if( !( method.getParameterType(i) instanceof RefLikeType ) ) continue;
	    args[i] = nf.caseParm(i);
        }
        NativeMethodDriver.v().process( method, thisNode, retNode, args );
    }
}

