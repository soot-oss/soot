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
import soot.util.*;

/** Creates inter-procedural pointer assignment edges.
 * @author Ondrej Lhotak
 */
public class TradPAGBuilder extends AbsPAGBuilder
{ 
    TradPAGBuilder( 
        Rsrcc_srcm_stmt_kind_tgtc_tgtm in,
        Qsrcc_src_dstc_dst simple,
        Qsrcc_src_fld_dstc_dst load,
        Qsrcc_src_fld_dstc_dst store,
        Qobjc_obj_varc_var alloc ) {
        super(in, simple, load, store, alloc);
    }
    private NodeFactory nf = PaddleScene.v().nodeFactory();
    private NodeManager nm = PaddleScene.v().nodeManager();

    public void update() {
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
            if( !t.kind().passesParameters() ) continue;
            MethodNodeFactory srcnf = new MethodNodeFactory(t.srcm());
            MethodNodeFactory tgtnf = new MethodNodeFactory(t.tgtm());
            if( t.kind().isExplicit() || t.kind() == Kind.THREAD ) {
                addCallTarget( srcnf, tgtnf, (Stmt) t.stmt(),
                            t.srcc(), t.tgtc() );
            } else {
                if( t.kind() == Kind.PRIVILEGED ) {
                    // Flow from first parameter of doPrivileged() invocation
                    // to this of target, and from return of target to the
                    // return of doPrivileged()

                    InvokeExpr ie = ((Stmt) t.stmt()).getInvokeExpr();
                    addEdge( t.srcc(), srcnf.getNode(ie.getArg(0)),
                             t.tgtc(), tgtnf.caseThis() );

                    if( t.stmt() instanceof AssignStmt ) {
                        AssignStmt as = (AssignStmt) t.stmt();
                        addEdge( t.tgtc(), tgtnf.caseRet(),
                                 t.srcc(), srcnf.getNode(as.getLeftOp()) );
                    }
                } else if( t.kind() == Kind.FINALIZE ) {
                    addEdge( t.srcc(), srcnf.caseThis(),
                             t.tgtc(), tgtnf.caseThis() );
                } else if( t.kind() == Kind.NEWINSTANCE ) {
                    Stmt s = (Stmt) t.stmt();
                    InstanceInvokeExpr iie = (InstanceInvokeExpr) s.getInvokeExpr();
                    VarNode cls = (VarNode) srcnf.getNode( iie.getBase() );
                    Node newObject = nf.caseNewInstance( cls );

                    addEdge( t.srcc(), newObject, t.tgtc(), tgtnf.caseThis() );
                } else {
                    throw new RuntimeException( "Unhandled edge "+t );
                }
            }
        }
    }

    /** Adds method target as a possible target of the invoke expression in s.
     **/
    final private void addCallTarget( MethodNodeFactory srcnf,
                                     MethodNodeFactory tgtnf,
                                     Stmt s,
                                     Context srcContext,
                                     Context tgtContext ) {
        InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
        int numArgs = ie.getArgCount();
        for( int i = 0; i < numArgs; i++ ) {
            Value arg = ie.getArg( i );
            if( !( arg.getType() instanceof RefLikeType ) ) continue;
            if( arg instanceof NullConstant ) continue;

            addEdge( srcContext, srcnf.getNode(arg),
                     tgtContext, tgtnf.caseParm(i) );
        }
        if( ie instanceof InstanceInvokeExpr ) {
            InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;

            addEdge( srcContext, srcnf.getNode(iie.getBase()),
                     tgtContext, tgtnf.caseThis() );
        }
        if( s instanceof AssignStmt ) {
            Value dest = ( (AssignStmt) s ).getLeftOp();
            if( dest.getType() instanceof RefLikeType && !(dest instanceof NullConstant) ) {

                addEdge( tgtContext, tgtnf.caseRet(),
                         srcContext, srcnf.getNode(dest) );
            }
        }
    }

    private Context parm( Node n, Context c ) {
        if( n instanceof VarNode ) {
            if( n instanceof LocalVarNode ) return c;
            else return null;
        }
        if( n instanceof FieldRefNode ) {
            FieldRefNode frn = (FieldRefNode) n;
            if( frn.base() instanceof LocalVarNode ) return c;
            else return null;
        }
        throw new RuntimeException( "NYI: "+n );
    }

    private Set seenEdges = new HashSet();
    private void addEdge( Context srcc, Node src, Context dstc, Node dst ) {
        srcc = parm(src, srcc);
        dstc = parm(dst, dstc);
        if( !seenEdges.add(new Cons(new Cons(srcc, src),
                                    new Cons(dstc, dst)))) return;
        if( src instanceof VarNode ) {
            VarNode srcvn = (VarNode) src;
            if( dst instanceof VarNode ) {
                VarNode dstvn = (VarNode) dst;
                simple.add( srcc, srcvn, dstc, dstvn );
            } else if( dst instanceof FieldRefNode ) {
                FieldRefNode fdst = (FieldRefNode) dst;
                store.add( srcc, srcvn, fdst.field(), dstc, fdst.base() );
            } else throw new RuntimeException( "Bad PA edge "+src+" -> "+dst );
        } else if( src instanceof FieldRefNode ) {
            FieldRefNode srcfrn = (FieldRefNode) src;
            VarNode dstvn = (VarNode) dst;
            load.add( srcc, srcfrn.base(), srcfrn.field(), dstc, dstvn );
        } else if( src instanceof AllocNode ) {
            AllocNode srcan = (AllocNode) src;
            VarNode dstvn = (VarNode) dst;
            alloc.add( srcc, srcan, dstc, dstvn );
        } else throw new RuntimeException( "Bad PA edge "+src+" -> "+dst );
    }
}


