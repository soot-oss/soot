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
import java.util.*;
import soot.jimple.toolkits.pointer.util.*;

/** Creates intra-procedural pointer assignment edges.
 * @author Ondrej Lhotak
 */
public class MethodPAG
{ 
    protected SootMethod method;

    protected Qsrc_dstTrad simple = new Qsrc_dstTrad("mpagsimple");
    protected Qsrc_fld_dstTrad load = new Qsrc_fld_dstTrad("mpagload");
    protected Qsrc_fld_dstTrad store = new Qsrc_fld_dstTrad("mpagstore");
    protected Qobj_varTrad alloc = new Qobj_varTrad("mpagalloc");
    protected Rsrc_dstTrad rsimple = (Rsrc_dstTrad) simple.reader("mpag");
    protected Rsrc_fld_dstTrad rload = (Rsrc_fld_dstTrad) load.reader("mpag");
    protected Rsrc_fld_dstTrad rstore = (Rsrc_fld_dstTrad) store.reader("mpag");
    protected Robj_varTrad ralloc = (Robj_varTrad) alloc.reader("mpag");
    public Rsrc_dst simple() { return rsimple.copy(); }
    public Rsrc_fld_dst load() { return rload.copy(); }
    public Rsrc_fld_dst store() { return rstore.copy(); }
    public Robj_var alloc() { return ralloc.copy(); }

    protected NodeFactory gnf;
    protected MethodNodeFactory nf;
    public MethodNodeFactory nodeFactory() { return nf; }

    MethodPAG( SootMethod method ) {
        this.method = method;
        gnf = PaddleScene.v().nodeFactory();
    }
    public void build() {
        nf = new MethodNodeFactory() {
            public SootMethod method() { return method; }
            public MethodPAG mpag() { return MethodPAG.this; }
        };
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
    protected void buildNormal( SootMethod m, MethodNodeFactory nf ) {
        Body b = m.retrieveActiveBody();
        Iterator unitsIt = b.getUnits().iterator();
        while( unitsIt.hasNext() )
        {
            Stmt s = (Stmt) unitsIt.next();
            nf.handleStmt( s );
        }
    }
    protected void buildNative( SootMethod m, MethodNodeFactory nf ) {
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
        PaddleScene.v().nativeHelper().setMPAG(this);
        NativeMethodDriver.v().process( method, thisNode, retNode, args );
        PaddleScene.v().nativeHelper().setMPAG(null);
    }
    private boolean isLocal( Node n ) {
        if( n instanceof LocalVarNode ) return true;
        if( n instanceof FieldRefNode ) {
            FieldRefNode frn = (FieldRefNode) n;
            if( frn.getBase() instanceof LocalVarNode ) return true;
        }
        return false;
    }
    public void addEdge( Node src, Node dst ) {
        if( src == null ) return;
        if( dst == null ) return;
        if( isLocal(src) || isLocal(dst) ) {
            if( src instanceof VarNode ) {
                if( dst instanceof VarNode ) {
                    simple.add( (VarNode) src, (VarNode) dst );
                } else if( dst instanceof FieldRefNode ) {
                    FieldRefNode fdst = (FieldRefNode) dst;
                    store.add( (VarNode) src, fdst.getField(), fdst.getBase() );
                } else throw new RuntimeException( "Bad PA edge "+src+" -> "+dst );
            } else if( src instanceof FieldRefNode ) {
                FieldRefNode fsrc = (FieldRefNode) src;
                load.add( fsrc.getBase(), fsrc.getField(), (VarNode) dst );
            } else if( src instanceof AllocNode ) {
                alloc.add( (AllocNode) src, (VarNode) dst );
            } else throw new RuntimeException( "Bad PA edge "+src+" -> "+dst );
        } else {
            gnf.addEdge(src, dst);
        }
    }
}

