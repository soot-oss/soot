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
import java.util.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.paddle.queue.*;

/** Analysis for detecting thisJoinPoints that are only queried for
 * static things.
 * @author Ondrej Lhotak
 */
public class JoinPointAnalysis
{ 
    private final Type joinPointInterfaceType;
    private final SootClass joinPointImplementationClass;
    private final List dynamicMethods = new ArrayList();
    private final Set processedStmts = new HashSet();
    private Set joinPoints = null;

    public JoinPointAnalysis(Type joinPointInterfaceType, SootClass joinPointImplementationClass ) {
        this.joinPointInterfaceType = joinPointInterfaceType;
        this.joinPointImplementationClass = joinPointImplementationClass;

        dynamicMethods.add( Scene.v().makeMethodRef(
            joinPointImplementationClass,
            "getThis",
            new ArrayList(),
            RefType.v("java.lang.Object"),
            false
            ).resolve() );
        dynamicMethods.add( Scene.v().makeMethodRef(
            joinPointImplementationClass,
            "getTarget",
            new ArrayList(),
            RefType.v("java.lang.Object"),
            false
            ).resolve() );
        dynamicMethods.add( Scene.v().makeMethodRef(
            joinPointImplementationClass,
            "getArgs",
            new ArrayList(),
            ArrayType.v(RefType.v("java.lang.Object"), 1),
            false
            ).resolve() );
    }

    /** Sets up the analysis.
     * @param joinPoints A set of statements that call the join point
     * factory method that could be replaced with calls to the static
     * part factory method. */
    public void setup( final Set/*AssignStmt*/ joinPoints ) {
        this.joinPoints = joinPoints;
        System.out.println("join points are: "+joinPoints);

        PaddleScene.v().ceh = new TradCallEdgeHandler(
            PaddleScene.v().ecsout.reader("ceh"),
            PaddleScene.v().parms,
            PaddleScene.v().rets) {
            protected void processEdge( Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t ) {
                // if it's not a join point, let the superclass handle it
                if( !joinPoints.contains(t.stmt()) ) {
                    super.processEdge(t);
                    return;
                }
                System.out.println( "found a JP in method "+t.srcm() );
                processedStmts.add(t.stmt());

                if(t.kind() != Kind.STATIC) throw new RuntimeException("unexpected edge kind "+t.kind());

                // handle the normal parameters to the method
                MethodNodeFactory srcnf = new MethodNodeFactory(t.srcm());
                MethodNodeFactory tgtnf = new MethodNodeFactory(t.tgtm());
                StaticInvokeExpr ie = (StaticInvokeExpr) ((Stmt) t.stmt()).getInvokeExpr();
                int numArgs = ie.getArgCount();
                for( int i = 0; i < numArgs; i++ ) {
                    Value arg = ie.getArg( i );
                    if( !( arg.getType() instanceof RefLikeType ) ) continue;
                    if( arg instanceof NullConstant ) continue;

                    addParmEdge( t, srcnf.getNode(arg), tgtnf.caseParm(i) );
                }

                // create an object node for the join point info
                AllocNode an = PaddleScene.v().nodeManager().makeGlobalAllocNode(
                    t.stmt(), joinPointImplementationClass.getType(), t.srcm());
                AssignStmt as = (AssignStmt) t.stmt();
                VarNode vn = PaddleScene.v().nodeManager().makeLocalVarNode(
                    as.getLeftOp(), as.getLeftOp().getType(), t.srcm());
                PaddleScene.v().alloc.add( an, vn );
            }
        };
    }

    /** Returns the result of the analysis, in the form a subset of
     * the set provided in the call to setup, containing only those
     * AssignStmts that can be replaced with creations of the static
     * part. */
    public Set/*AssignStmt*/ getResult() {
        Set ret = new HashSet(processedStmts);
        for( Iterator mIt = dynamicMethods.iterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            VarNode vn = (VarNode) new MethodNodeFactory(m).caseThis();
            for( Iterator cvnIt = vn.contexts(); cvnIt.hasNext(); ) {
                final ContextVarNode cvn = (ContextVarNode) cvnIt.next();
                PointsToSetReadOnly ptset = PaddleScene.v().p2sets.get( cvn );
                for( Iterator sIt = processedStmts.iterator(); sIt.hasNext(); ) {
                    final Stmt s = (Stmt) sIt.next();
                    GlobalAllocNode an = PaddleScene.v().nodeManager().findGlobalAllocNode(s);
                    for( Iterator canIt = an.contexts(); canIt.hasNext(); ) {
                        final ContextAllocNode can = (ContextAllocNode) canIt.next();
                        if( ptset.contains(can) ) {
                            ret.remove(s);
                        }
                    }
                }
            }
        }
        System.out.println("Out of "+joinPoints.size()+" dynamic join points, "+ret.size()+" can be made static.");
        System.out.println("Number of join points found reachable: "+processedStmts.size());
        return ret;
    }
}
