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
    private final List/*SootMethod*/ dynamicMethods = new ArrayList();
    private final Set/*Local*/ processedLocals = new HashSet();
    private Set/*Local*/ joinPoints = null;

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
     * @param joinPoints A set of locals, each corresponding to a use
     * of thisJoinPoint in the code.
     */
    public void setup( final Set/*Local*/ joinPoints ) {
        this.joinPoints = joinPoints;

        DepItem joinPointModeller = new DepItem() {
            Rvar_method_type locals = PaddleScene.v().locals.reader("jpm");
            public boolean update() {
                boolean ret = false;
                for( Iterator tIt = locals.iterator(); tIt.hasNext(); ) {
                    final Rvar_method_type.Tuple t = (Rvar_method_type.Tuple) tIt.next();
                    if( !joinPoints.contains(t.var().getVariable()) )
                        continue;
                    processedLocals.add(t.var().getVariable());
                    PaddleScene.v().alloc.add(
                        PaddleScene.v().nodeManager().makeGlobalAllocNode(
                            t.var().getVariable(), 
                            joinPointImplementationClass.getType(),
                            t.method() ),
                        t.var() );
                    ret = true;
                }
                return ret;
            }
        };

        PaddleScene.v().depMan.addDep( PaddleScene.v().locals, joinPointModeller );
        PaddleScene.v().depMan.addPrec( PaddleScene.v().mpc, joinPointModeller );
    }

    /** Returns the result of the analysis, in the form a subset of
     * the set provided in the call to setup, containing only those
     * Locals that can be replaced with creations of the static
     * part. */
    public Set/*Local*/ getResult() {
        Set ret = new HashSet(processedLocals);
        for( Iterator mIt = dynamicMethods.iterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            VarNode vn = (VarNode) new MethodNodeFactory(m).caseThis();
            for( Iterator cvnIt = vn.contexts(); cvnIt.hasNext(); ) {
                final ContextVarNode cvn = (ContextVarNode) cvnIt.next();
                PointsToSetReadOnly ptset = PaddleScene.v().p2sets.get( cvn );
                for( Iterator lIt = processedLocals.iterator(); lIt.hasNext(); ) {
                    final Local l = (Local) lIt.next();
                    GlobalAllocNode an = PaddleScene.v().nodeManager().findGlobalAllocNode(l);
                    for( Iterator canIt = an.contexts(); canIt.hasNext(); ) {
                        final ContextAllocNode can = (ContextAllocNode) canIt.next();
                        if( ptset.contains(can) ) {
                            ret.remove(l);
                        }
                    }
                }
            }
        }
        return ret;
    }
}
