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

package soot.jimple.toolkits.callgraph;
import soot.*;
import soot.options.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.util.queue.*;

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
    private boolean appOnly = false;
    public ReachableMethods reachables() { return reachables; }

    public CallGraphBuilder( PointsToAnalysis pa ) {
        this.pa = pa;
        options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        cg = new CallGraph();
        Scene.v().setCallGraph( cg );
        reachables = Scene.v().getReachableMethods();
        worklist = reachables.listener();
        if( !options.verbose() ) {
            G.v().out.println( "[Call Graph] For information on where the call graph may be incomplete, use the verbose option to the cg phase." );
        }
    }
    public CallGraphBuilder() {
        G.v().out.println( "Warning: using incomplete callgraph containing "+
                "only application classes." );
        pa = soot.jimple.toolkits.pointer.DumbPointerAnalysis.v();
        options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        cg = new CallGraph();
        Scene.v().setCallGraph(cg);
        reachables = Scene.v().getReachableMethods();
        worklist = reachables.listener();
        appOnly = true;
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
            if( appOnly && !m.getDeclaringClass().isApplicationClass() )
                continue;
            processNewMethod( m );
        }
    }

    private void handleClassName( VirtualCallSite vcs, String name ) {
        if( name == null ) {
            if( options.verbose() ) {
                G.v().out.println( "Warning: Method "+vcs.getContainer()+
                    " is reachable, and calls Class.forName on a"+
                    " non-constant String; graph will be incomplete!"+
                    " Use safe-forname option for a conservative result." );
            }
        } else {
            constantForName( name, vcs.getContainer(), vcs.getStmt() );
        }
    }

    private void processNewMethod( SootMethod m ) {
        if( m.isNative() ) {
            return;
        }
        Body b = m.retrieveActiveBody();
        HashSet receivers = new HashSet();
        getImplicitTargets( m );
        findReceivers(m, b, receivers);
        processReceivers(receivers);
    }

    private void processReceivers(HashSet receivers) {
        for( Iterator receiverIt = receivers.iterator(); receiverIt.hasNext(); ) {
            final Local receiver = (Local) receiverIt.next();
            Set types = pa.reachingObjects( receiver ).possibleTypes();
            HashSet vcss = (HashSet) localToVCS.get( receiver );
            for( Iterator vcsIt = vcss.iterator(); vcsIt.hasNext(); ) {
                final VirtualCallSite vcs = (VirtualCallSite) vcsIt.next();
                final Type t;
                SootMethod target;
                resolveInvokes(types, vcs);
                if( vcs.getInstanceInvokeExpr().getMethod().getNumberedSubSignature() == sigStart ) {
                    resolveThreadInvokes(types, vcs);
                }
            }
        }
    }

    private void resolveThreadInvokes(Set types, final VirtualCallSite vcs) {
        for( Iterator tIt = types.iterator(); tIt.hasNext(); ) {
            final Type t = (Type) tIt.next();
            VirtualCalls.v().resolveThread( t, vcs.getInstanceInvokeExpr(), vcs.getContainer(),
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

    private void resolveInvokes(Set types, final VirtualCallSite vcs) {
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
    }

    private void findReceivers(SootMethod m, Body b, HashSet receivers) {
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if (s.containsInvokeExpr()) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();

                if (ie instanceof InstanceInvokeExpr) {
                    VirtualCallSite vcs = new VirtualCallSite(s, m);
                    invokeExprToVCS.put(ie, vcs);
                    Local receiver =
                        (Local) ((InstanceInvokeExpr) ie).getBase();
                    HashSet vcss = (HashSet) localToVCS.get(receiver);
                    if (vcss == null) {
                        localToVCS.put(receiver, vcss = new HashSet());
                    }
                    vcss.add(vcs);
                    receivers.add(receiver);

                } else {
                    SootMethod tgt = ((StaticInvokeExpr) ie).getMethod();
                    cg.addEdge(new Edge(m, s, tgt));
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
            if( vcs.getInstanceInvokeExpr().getMethod().getNumberedSubSignature() == sigStart ) {
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
        for( Iterator vcsIt = wantedStringConstants.get( l ).iterator(); vcsIt.hasNext(); ) {
            final VirtualCallSite vcs = (VirtualCallSite) vcsIt.next();
            handleClassName( vcs, name );
        }
        if( name == null ) wantedStringConstants.remove( l );
    }
    public void doneStringConstants() {
        processWorklist();
    }

    public CallGraph getCallGraph() {
        return cg;
    }

    private final void addEdge(  SootMethod src, Stmt stmt, SootClass cls, NumberedString methodSubSig, int type ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            cg.addEdge(
                new Edge( src, stmt, cls.getMethod( methodSubSig ), type ) );
        }
    }
    private final void addEdge( SootMethod src, Stmt stmt, String methodSig, int type ) {
        if( Scene.v().containsMethod( methodSig ) ) {
            cg.addEdge(
                new Edge( src, stmt, Scene.v().getMethod( methodSig ), type ) );
        }
    }
    private void constantForName( String cls, SootMethod src, Stmt srcUnit ) {
        if( cls.charAt(0) == '[' ) {
            if( cls.charAt(1) == 'L' && cls.charAt(cls.length()-1) == ';' ) {
                cls = cls.substring(2,cls.length()-1);
                constantForName( cls, src, srcUnit );
            }
        } else {
            if( !Scene.v().containsClass( cls ) ) {
                if( options.verbose() ) {
                    G.v().out.println( "Warning: Class "+cls+" is"+
                        " a dynamic class, and you did not specify"+
                        " it as such; graph will be incomplete!" );
                }
            } else {
                SootClass sootcls = Scene.v().getSootClass( cls );
                if( !sootcls.isApplicationClass() ) {
                    sootcls.setLibraryClass();
                }
                addEdge( src, srcUnit, sootcls, sigClinit, Edge.CLINIT );
            }
        }
    }
    public void getImplicitTargets( SootMethod source ) {
        final SootClass scl = source.getDeclaringClass();
        if( source.isNative() ) return;
        if( source.getSubSignature().indexOf( "<init>" ) >= 0 ) {
            handleInit(source, scl);
        }
        Body b = source.retrieveActiveBody();
        boolean warnedAlready = false;
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s.containsInvokeExpr() ) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
                if( ie.getMethod().getSignature().equals( "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>" ) ) {
                    if( !warnedAlready ) {
                        if( options.verbose() ) {
                            G.v().out.println( "Warning: call to "+
                                "java.lang.reflect.Method: invoke() from "+source+
                                "; graph will be incomplete!" );
                        }
                        warnedAlready = true;
                    }
                }
                if( ie instanceof StaticInvokeExpr ) {
                    addEdge( source, s, ie.getMethod().getDeclaringClass(),
                        sigClinit, Edge.CLINIT );
                }
                if( ie.getMethod().getNumberedSubSignature() == sigForName ) {
                    Value name = ie.getArg(0);
                    if( name instanceof StringConstant ) {
                        String cls = ((StringConstant) name ).value;
                        constantForName( cls, source, s );
                    } else {
                        if( options.safe_forname() ) {
                            for( Iterator tgtIt = EntryPoints.v().clinits().iterator(); tgtIt.hasNext(); ) {
                                final SootMethod tgt = (SootMethod) tgtIt.next();
                                cg.addEdge( new Edge( source, s, tgt, Edge.CLINIT ) );
                            }
                        } else {
                            VirtualCallSite vcs = new VirtualCallSite( s, source );
                            wantedStringConstants.put( name, vcs );
                            Set names = pa.reachingObjects( (Local) name ).possibleStringConstants();
                            if( names == null ) {
                                handleClassName( vcs, null );
                                wantedStringConstants.remove( name );
                            } else {
                                for( Iterator nameStrIt = names.iterator(); nameStrIt.hasNext(); ) {
                                    final String nameStr = (String) nameStrIt.next();
                                    handleClassName( vcs, nameStr );
                                }
                            }

                        }
                    }
                }
            }
            if( s.containsFieldRef() ) {
                FieldRef fr = (FieldRef) s.getFieldRef();
                if( fr instanceof StaticFieldRef ) {
                    SootClass cl = fr.getField().getDeclaringClass();
                    addEdge( source, s, cl, sigClinit, Edge.CLINIT );
                }
            }
            if( s instanceof AssignStmt ) {
                Value rhs = ((AssignStmt)s).getRightOp();
                if( rhs instanceof NewExpr ) {
                    NewExpr r = (NewExpr) rhs;
                    addEdge( source, s, r.getBaseType().getSootClass(),
                            sigClinit, Edge.CLINIT );
                } else if( rhs instanceof NewArrayExpr || rhs instanceof NewMultiArrayExpr ) {
                    Type t = rhs.getType();
                    if( t instanceof ArrayType ) t = ((ArrayType)t).baseType;
                    if( t instanceof RefType ) {
                        addEdge( source, s, ((RefType) t).getSootClass(),
                                sigClinit, Edge.CLINIT );
                    }
                }
            }
        }
    }

    private void handleInit(SootMethod source, final SootClass scl) {
        addEdge( source, null, scl, sigFinalize, Edge.FINALIZE );
        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
        if( fh.canStoreType( scl.getType(), clPrivilegedAction )
        ||  fh.canStoreType( scl.getType(), clPrivilegedExceptionAction ) ) {
            addEdge( source, null, scl, sigObjRun, Edge.PRIVILEGED );
        }
        if( fh.canStoreType( scl.getType(), clRunnable ) ) {
            addEdge( source, null, scl, sigExit, Edge.EXIT );
        }
    }


    private HashMultiMap wantedStringConstants = new HashMultiMap();
    private HashMap stmtToMethod = new HashMap();
    private CallGraph cg;
    private ChunkedQueue targetsQueue = new ChunkedQueue();
    private QueueReader targets = targetsQueue.reader();

    private final NumberedString sigMain = Scene.v().getSubSigNumberer().
        findOrAdd( "void main(java.lang.String[])" );
    private final NumberedString sigFinalize = Scene.v().getSubSigNumberer().
        findOrAdd( "void finalize()" );
    private final NumberedString sigExit = Scene.v().getSubSigNumberer().
        findOrAdd( "void exit()" );
    private final NumberedString sigClinit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <clinit>()" );
    private final NumberedString sigStart = Scene.v().getSubSigNumberer().
        findOrAdd( "void start()" );
    private final NumberedString sigRun = Scene.v().getSubSigNumberer().
        findOrAdd( "void run()" );
    private final NumberedString sigObjRun = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Object run()" );
    private final NumberedString sigForName = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Class forName(java.lang.String)" );
    private final RefType clPrivilegedAction = RefType.v("java.security.PrivilegedAction");
    private final RefType clPrivilegedExceptionAction = RefType.v("java.security.PrivilegedExceptionAction");
    private final RefType clRunnable = RefType.v("java.lang.Runnable");

}

