/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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
import soot.jimple.*;
import java.util.*;

/** Creates inter-procedural pointer assignment edges.
 * @author Ondrej Lhotak
 */
public class TradPAGBuilder extends AbsPAGBuilder
{ 
    TradPAGBuilder( 
        Rsrcc_srcm_stmt_kind_tgtc_tgtm in,
        Qsrc_dst simple,
        Qsrc_fld_dst load,
        Qsrc_fld_dst store,
        Qobj_var alloc ) {
        super(in, simple, load, store, alloc);
    }
    private NodeFactory nf = PaddleScene.v().nodeFactory();

    public void update() {
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
            if( !t.kind().passesParameters() ) continue;
            MethodPAG srcmpag = PaddleScene.v().mpb.v( t.srcm() );
            MethodPAG tgtmpag = PaddleScene.v().mpb.v( t.tgtm() );
            if( t.kind().isExplicit() || t.kind() == Kind.THREAD ) {
                addCallTarget( srcmpag, tgtmpag, (Stmt) t.stmt(),
                            t.srcc(), t.tgtc() );
            } else {
                if( t.kind() == Kind.PRIVILEGED ) {
                    // Flow from first parameter of doPrivileged() invocation
                    // to this of target, and from return of target to the
                    // return of doPrivileged()

                    InvokeExpr ie = ((Stmt) t.stmt()).getInvokeExpr();

                    Node parm = srcmpag.nodeFactory().getNode( ie.getArg(0) );
                    parm = parm( parm, t.srcc() );

                    Node thiz = tgtmpag.nodeFactory().caseThis();
                    thiz = parm( thiz, t.tgtc() );

                    addEdge( parm, thiz );

                    if( t.stmt() instanceof AssignStmt ) {
                        AssignStmt as = (AssignStmt) t.stmt();

                        Node ret = tgtmpag.nodeFactory().caseRet();
                        ret = parm( ret, t.tgtc() );

                        Node lhs = srcmpag.nodeFactory().getNode(as.getLeftOp());
                        lhs = parm( lhs, t.srcc() );

                        addEdge( ret, lhs );
                    }
                } else if( t.kind() == Kind.FINALIZE ) {
                    Node srcThis = srcmpag.nodeFactory().caseThis();
                    srcThis = parm( srcThis, t.srcc() );

                    Node tgtThis = tgtmpag.nodeFactory().caseThis();
                    tgtThis = parm( tgtThis, t.tgtc() );

                    addEdge( srcThis, tgtThis );
                } else if( t.kind() == Kind.NEWINSTANCE ) {
                    Stmt s = (Stmt) t.stmt();
                    InstanceInvokeExpr iie = (InstanceInvokeExpr) s.getInvokeExpr();

                    Node cls = srcmpag.nodeFactory().getNode( iie.getBase() );
                    cls = parm( cls, t.srcc() );
                    Node newObject = nf.caseNewInstance( (VarNode) cls );

                    Node initThis = tgtmpag.nodeFactory().caseThis();
                    initThis = parm( initThis, t.tgtc() );

                    addEdge( newObject, initThis );
                } else {
                    throw new RuntimeException( "Unhandled edge "+t );
                }
            }
        }
    }

    /** Adds method target as a possible target of the invoke expression in s.
     **/
    final private void addCallTarget( MethodPAG srcmpag,
                                     MethodPAG tgtmpag,
                                     Stmt s,
                                     Context srcContext,
                                     Context tgtContext ) {
        MethodNodeFactory srcnf = srcmpag.nodeFactory();
        MethodNodeFactory tgtnf = tgtmpag.nodeFactory();
        InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
        int numArgs = ie.getArgCount();
        for( int i = 0; i < numArgs; i++ ) {
            Value arg = ie.getArg( i );
            if( !( arg.getType() instanceof RefLikeType ) ) continue;
            if( arg instanceof NullConstant ) continue;

            Node argNode = srcnf.getNode( arg );
            argNode = parm( argNode, srcContext );

            Node parm = tgtnf.caseParm( i );
            parm = parm( parm, tgtContext );

            addEdge( argNode, parm );
        }
        if( ie instanceof InstanceInvokeExpr ) {
            InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;

            Node baseNode = srcnf.getNode( iie.getBase() );
            baseNode = parm( baseNode, srcContext );

            Node thisRef = tgtnf.caseThis();
            thisRef = parm( thisRef, tgtContext );
            addEdge( baseNode, thisRef );
        }
        if( s instanceof AssignStmt ) {
            Value dest = ( (AssignStmt) s ).getLeftOp();
            if( dest.getType() instanceof RefLikeType && !(dest instanceof NullConstant) ) {

                Node destNode = srcnf.getNode( dest );
                destNode = parm( destNode, srcContext );

                Node retNode = tgtnf.caseRet();
                retNode = parm( retNode, tgtContext );

                addEdge( retNode, destNode );
            }
        }
    }

    private Node parm( Node n, Context c ) {
        if( c == null ) return n;
        if( n instanceof LocalVarNode ) return ((LocalVarNode) n).context(c);
        if( n instanceof FieldRefNode ) {
            FieldRefNode frn = (FieldRefNode) n;
            return ((VarNode) parm(frn.base(), c)).dot( frn.field() );
        }
        return n;
    }

    private void addEdge( Node src, Node dst ) {
        nf.addEdge( src, dst );
    }
}


