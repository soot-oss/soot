/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.spark.callgraph;
import soot.*;
import soot.options.*;
import soot.jimple.*;
import soot.jimple.spark.*;
import soot.jimple.toolkits.callgraph.*;
import java.util.*;
import soot.util.*;
import soot.util.queue.*;
import soot.jimple.spark.pag.*;

/** Models the call graph.
 * @author Ondrej Lhotak
 */
public final class CallGraphBuilder
{ 
    private QueueReader worklist;
    private HashMap invokeExprToVCS = new HashMap();
    private LargeNumberedMap localToVCS = new LargeNumberedMap( Scene.v().getLocalNumberer() );
    private PointsToAnalysis pa;
    private CGOptions options;
    private ReachableMethods reachables;
    public ReachableMethods reachables() { return reachables; }

    public CallGraphBuilder( PointsToAnalysis pa ) {
        this.pa = pa;
        options = new CGOptions( PackManager.v().getPhaseOptions("cg") );
        cg = new CallGraph();
        Scene.v().setCallGraph( cg );
        reachables = Scene.v().getReachableMethods();
        worklist = reachables.listener();
    }
    public void build() {
        processWorklist();
    }
    private void processWorklist() {
        while(true) {
            SootMethod m = (SootMethod) worklist.next();
            if( m == null ) {
                reachables.update();
                m = (SootMethod) worklist.next();
                if( m == null ) break;
            }
            processNewMethod( m );
        }
    }

    private void handleClassName( SootMethod m, AssignStmt s, String name ) {
        if( name.length() == 0 ) return;
        if( name.charAt(0) == '[' ) return;
        if( pa instanceof PAG ) {
            PAG pag = (PAG) pa;
            AllocNode an = pag.makeClassConstantNode( name );
            VarNode vn = pag.makeVarNode( s.getLeftOp(), s.getLeftOp().getType(), m );
            pag.addEdge( an, vn );
        }
    }

    private void processNewMethod( SootMethod m ) {
        if( m.isNative() ) {
            return;
        }
        Body b = m.retrieveActiveBody();
        HashSet receivers = new HashSet();
        for( Iterator eIt = ImplicitMethodInvocation.v().getImplicitTargets( m, options.safe_forname() ).iterator(); eIt.hasNext(); ) {
            final Edge e = (Edge) eIt.next();
            cg.addEdge( e );
        }
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s.containsInvokeExpr() ) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();

                if( ie instanceof InstanceInvokeExpr ) {
                    VirtualCallSite vcs = new VirtualCallSite( s, m );
                    invokeExprToVCS.put( ie, vcs );
                    Local receiver = (Local) ((InstanceInvokeExpr)ie).getBase();
                    HashSet vcss = (HashSet) localToVCS.get( receiver );
                    if( vcss == null ) {
                        localToVCS.put( receiver, vcss = new HashSet() );
                    }
                    vcss.add( vcs );
                    receivers.add( receiver );

                } else {
                    SootMethod tgt = ((StaticInvokeExpr)ie).getMethod();
                    cg.addEdge( new Edge( m, s, tgt ) );
                }
            }
        }
        for( Iterator receiverIt = receivers.iterator(); receiverIt.hasNext(); ) {
            final Local receiver = (Local) receiverIt.next();
            Set types = pa.reachingObjects( receiver ).possibleTypes();
            HashSet vcss = (HashSet) localToVCS.get( receiver );
            for( Iterator vcsIt = vcss.iterator(); vcsIt.hasNext(); ) {
                final VirtualCallSite vcs = (VirtualCallSite) vcsIt.next();
                for( Iterator tIt = types.iterator(); tIt.hasNext(); ) {
                    final Type t = (Type) tIt.next();
                    VirtualCalls.v().resolve( t, vcs.getInstanceInvokeExpr(), vcs.getContainer(),
                            targetsQueue );
                }
                while(true) {
                    SootMethod target = (SootMethod) targets.next();
                    if( target == null ) break;
                    cg.addEdge(
                        new Edge( vcs.getContainer(), vcs.getStmt(), target ) );
                }
                if( vcs.getInstanceInvokeExpr().getMethod().getNumberedSubSignature() == ImplicitMethodInvocation.v().sigStart ) {
                    for( Iterator tIt = types.iterator(); tIt.hasNext(); ) {
                        final Type t = (Type) tIt.next();
                        VirtualCalls.v().resolve( t, vcs.getInstanceInvokeExpr(), vcs.getContainer(),
                                targetsQueue );
                    }
                    while(true) {
                        SootMethod target = (SootMethod) targets.next();
                        if( target == null ) break;
                        cg.addEdge(
                            new Edge( vcs.getContainer(), vcs.getStmt(), target,
                                Edge.THREAD ) );
                    }
                }
            }
        }
    }

    HashSet currentvcss = null;
    public boolean wantTypes( Local l ) {
        currentvcss = (HashSet) localToVCS.get( l );
        return currentvcss != null && !currentvcss.isEmpty();
    }
    public void addType( Type t ) {
        for( Iterator vcsIt = currentvcss.iterator(); vcsIt.hasNext(); ) {
            final VirtualCallSite vcs = (VirtualCallSite) vcsIt.next();
            VirtualCalls.v().resolve( t, vcs.getInstanceInvokeExpr(), vcs.getContainer(),
                    targetsQueue );
            while(true) {
                SootMethod target = (SootMethod) targets.next();
                if( target == null ) break;
                cg.addEdge(
                    new Edge( vcs.getContainer(), vcs.getStmt(), target ) );
            }
            if( vcs.getInstanceInvokeExpr().getMethod().getNumberedSubSignature() == ImplicitMethodInvocation.v().sigStart ) {
                VirtualCalls.v().resolve( t, vcs.getInstanceInvokeExpr(), vcs.getContainer(),
                        targetsQueue );
                while(true) {
                    SootMethod target = (SootMethod) targets.next();
                    if( target == null ) break;
                    cg.addEdge(
                        new Edge( vcs.getContainer(), vcs.getStmt(), target,
                            Edge.THREAD ) );
                }
            }
        }
    }
    public void doneTypes() {
        currentvcss = null;
        processWorklist();
    }

    public boolean wantStringConstants( Local l ) {
        return wantedStringConstants.keySet().contains( l );
    }

    public void newStringConstant( Local l, String name ) {
        for( Iterator sIt = wantedStringConstants.get( l ).iterator(); sIt.hasNext(); ) {
            final AssignStmt s = (AssignStmt) sIt.next();
            SootMethod m = (SootMethod) stmtToMethod.get(s);
            if( name == null ) {
                G.v().out.println( "WARNING: Method "+m+
                    " is reachable, and calls Class.forName on a"+
                    " non-constant String; graph will be incomplete!" );
                wantedStringConstants.remove( l );
            } else {
                handleClassName( m, s, name );
            }
        }
    }

    public CallGraph getCallGraph() {
        return cg;
    }

    private HashMultiMap wantedStringConstants = new HashMultiMap();
    private HashMap stmtToMethod = new HashMap();
    private CallGraph cg;
    private ChunkedQueue targetsQueue = new ChunkedQueue();
    private QueueReader targets = targetsQueue.reader();
}

