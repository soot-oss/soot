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
public class TradCallEdgeHandler extends AbsCallEdgeHandler
{ 
    TradCallEdgeHandler( 
        Rsrcc_srcm_stmt_kind_tgtc_tgtm in,
        Qsrcm_stmt_kind_tgtm_src_dst parms,
        Qsrcm_stmt_kind_tgtm_src_dst rets ) {
        super(in, parms, rets);
    }

    Set seenEdges = new HashSet();
    public void update() {
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
            if( !t.kind().passesParameters() ) continue;

            if( !seenEdges.add( new Cons(new Cons(t.srcm(), t.stmt()),
                                        new Cons(t.kind(), t.tgtm())))) continue;

            processEdge( t );
        }
    }

    protected void processEdge( Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t ) {
        MethodNodeFactory srcnf = new MethodNodeFactory(t.srcm());
        MethodNodeFactory tgtnf = new MethodNodeFactory(t.tgtm());
        if( t.kind().isExplicit() || t.kind() == Kind.THREAD ) {
            addCallTarget( t, srcnf, tgtnf );
        } else {
            if( t.kind() == Kind.PRIVILEGED ) {
                // Flow from first parameter of doPrivileged() invocation
                // to this of target, and from return of target to the
                // return of doPrivileged()

                InvokeExpr ie = ((Stmt) t.stmt()).getInvokeExpr();
                addParmEdge( t, srcnf.getNode(ie.getArg(0)), tgtnf.caseThis() );

                if( t.stmt() instanceof AssignStmt ) {
                    AssignStmt as = (AssignStmt) t.stmt();
                    addRetEdge( t, tgtnf.caseRet(), srcnf.getNode(as.getLeftOp()) );
                }
            } else if( t.kind() == Kind.INVOKE_FINALIZE ) {
                addParmEdge( t, srcnf.caseParm(0), tgtnf.caseThis() );
            } else if( t.kind() == Kind.FINALIZE ) {
                AssignStmt as = (AssignStmt) t.stmt();
                Local lhs = (Local) as.getLeftOp();
                addParmEdge( t, srcnf.getNode(lhs), tgtnf.caseParm(0) );
            } else if( t.kind() == Kind.NEWINSTANCE ) {
                Stmt s = (Stmt) t.stmt();
                InstanceInvokeExpr iie = (InstanceInvokeExpr) s.getInvokeExpr();
                VarNode cls = (VarNode) srcnf.getNode( iie.getBase() );
                Node newObject = PaddleScene.v().nodeFactory().caseNewInstance( cls );

                addParmEdge( t, newObject, tgtnf.caseThis() );
            } else {
                throw new RuntimeException( "Unhandled edge "+t );
            }
        }
    }

    /** Adds method target as a possible target of the invoke expression in s.
     **/
    final private void addCallTarget( Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t,
                                     MethodNodeFactory srcnf,
                                     MethodNodeFactory tgtnf ) {
        Stmt s = (Stmt) t.stmt();
        InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
        int numArgs = ie.getArgCount();
        for( int i = 0; i < numArgs; i++ ) {
            Value arg = ie.getArg( i );
            if( !( arg.getType() instanceof RefLikeType ) ) continue;
            if( arg instanceof NullConstant ) continue;

            addParmEdge( t, srcnf.getNode(arg), tgtnf.caseParm(i) );
        }
        if( ie instanceof InstanceInvokeExpr ) {
            InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;

            addParmEdge( t, srcnf.getNode(iie.getBase()), tgtnf.caseThis() );
        }
        if( s instanceof AssignStmt ) {
            Value dest = ( (AssignStmt) s ).getLeftOp();
            if( dest.getType() instanceof RefLikeType && !(dest instanceof NullConstant) ) {

                addRetEdge( t, tgtnf.caseRet(), srcnf.getNode(dest) );
            }
        }
    }

    protected void addParmEdge( Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple cgEdge, Node src, Node dst ) {
        parms.add(cgEdge.srcm(), cgEdge.stmt(), cgEdge.kind(), cgEdge.tgtm(),
                (VarNode) src, (VarNode) dst);
    }
    private void addRetEdge( Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple cgEdge, Node src, Node dst ) {
        rets.add(cgEdge.srcm(), cgEdge.stmt(), cgEdge.kind(), cgEdge.tgtm(),
                (VarNode) src, (VarNode) dst);
    }
}


