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
public final class OnFlyCallGraphBuilder
{ 
    /** context-insensitive stuff */
    private CallGraph cicg = new CallGraph();
    private HashSet analyzedMethods = new HashSet();

    private LargeNumberedMap receiverToSites = new LargeNumberedMap( Scene.v().getLocalNumberer() ); // Local -> List(VirtualCallSite)
    private LargeNumberedMap methodToReceivers = new LargeNumberedMap( Scene.v().getMethodNumberer() ); // SootMethod -> List(Local)
    public LargeNumberedMap methodToReceivers() { return methodToReceivers; }

    private SmallNumberedMap stringConstToSites = new SmallNumberedMap( Scene.v().getLocalNumberer() ); // Local -> List(VirtualCallSite)
    private LargeNumberedMap methodToStringConstants = new LargeNumberedMap( Scene.v().getMethodNumberer() ); // SootMethod -> List(Local)
    public LargeNumberedMap methodToStringConstants() { return methodToStringConstants; }

    private CGOptions options;

    private boolean appOnly;

    /** context-sensitive stuff */
    private ReachableMethods rm;
    private QueueReader worklist;

    private ContextManager cm;

    private ChunkedQueue targetsQueue = new ChunkedQueue();
    private QueueReader targets = targetsQueue.reader();


    public OnFlyCallGraphBuilder( ContextManager cm, ReachableMethods rm ) {
        this.cm = cm;
        this.rm = rm;
        worklist = rm.listener();
        options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        if( !options.verbose() ) {
            G.v().out.println( "[Call Graph] For information on where the call graph may be incomplete, use the verbose option to the cg phase." );
        }
    }
    public OnFlyCallGraphBuilder( ContextManager cm, ReachableMethods rm, boolean appOnly ) {
        this( cm, rm );
        this.appOnly = appOnly;
    }
    public void processReachables() {
        while(true) {
            if( !worklist.hasNext() ) {
                rm.update();
                if( !worklist.hasNext() ) break;
            }
            MethodOrMethodContext momc = (MethodOrMethodContext) worklist.next();
            SootMethod m = momc.method();
            if( appOnly && !m.getDeclaringClass().isApplicationClass() ) continue;
            if( analyzedMethods.add( m ) ) processNewMethod( m );
            processNewMethodContext( momc );
        }
    }
    public boolean wantTypes( Local receiver ) {
        return receiverToSites.get(receiver) != null;
    }
    public void addType( Local receiver, Context srcContext, Type type, Context typeContext ) {
        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
        for( Iterator siteIt = ((Collection) receiverToSites.get( receiver )).iterator(); siteIt.hasNext(); ) {
            final VirtualCallSite site = (VirtualCallSite) siteIt.next();
            InstanceInvokeExpr iie = site.iie();
            if( site.kind() == Kind.THREAD 
            && !fh.canStoreType( type, clRunnable ) )
                continue;

            if( site.iie() instanceof SpecialInvokeExpr ) {
                targetsQueue.add( VirtualCalls.v().resolveSpecial( 
                            (SpecialInvokeExpr) site.iie(),
                            site.subSig(),
                            site.container() ) );
            } else {
                VirtualCalls.v().resolve( type,
                        receiver.getType(),
                        site.subSig(),
                        site.container(), 
                        targetsQueue );
            }
            while(targets.hasNext()) {
                SootMethod target = (SootMethod) targets.next();
                cm.addVirtualEdge(
                        MethodContext.v( site.container(), srcContext ),
                        site.stmt(),
                        target,
                        site.kind(),
                        typeContext );
            }
        }
    }
    public boolean wantStringConstants( Local stringConst ) {
        return stringConstToSites.get(stringConst) != null;
    }
    public void addStringConstant( Local l, Context srcContext, String constant ) {
        for( Iterator siteIt = ((Collection) stringConstToSites.get( l )).iterator(); siteIt.hasNext(); ) {
            final VirtualCallSite site = (VirtualCallSite) siteIt.next();
            if( constant == null ) {
                if( options.verbose() ) {
                    G.v().out.println( "Warning: Method "+site.container()+
                        " is reachable, and calls Class.forName on a"+
                        " non-constant String; graph will be incomplete!"+
                        " Use safe-forname option for a conservative result." );
                }
            } else {
                if( constant.length() > 0 && constant.charAt(0) == '[' ) {
                    if( constant.length() > 1 && constant.charAt(1) == 'L' 
                    && constant.charAt(constant.length()-1) == ';' ) {
                        constant = constant.substring(2,constant.length()-1);
                    } else continue;
                }
                if( !Scene.v().containsClass( constant ) ) {
                    if( options.verbose() ) {
                        G.v().out.println( "Warning: Class "+constant+" is"+
                            " a dynamic class, and you did not specify"+
                            " it as such; graph will be incomplete!" );
                    }
                } else {
                    SootClass sootcls = Scene.v().getSootClass( constant );
                    if( !sootcls.isApplicationClass() ) {
                        sootcls.setLibraryClass();
                    }
                    for( Iterator clinitIt = EntryPoints.v().clinitsOf(sootcls).iterator(); clinitIt.hasNext(); ) {
                        final SootMethod clinit = (SootMethod) clinitIt.next();
                        cm.addStaticEdge(
                                MethodContext.v( site.container(), srcContext ),
                                site.stmt(),
                                clinit,
                                Kind.CLINIT );
                    }
                }
            }
        }
    }

    /* End of public methods. */

    private void addVirtualCallSite( Stmt s, SootMethod m, Local receiver,
            InstanceInvokeExpr iie, NumberedString subSig, Kind kind ) {
        List sites = (List) receiverToSites.get(receiver);
        if (sites == null) {
            receiverToSites.put(receiver, sites = new ArrayList());
            List receivers = (List) methodToReceivers.get(m);
            if( receivers == null )
                methodToReceivers.put(m, receivers = new ArrayList());
            receivers.add(receiver);
        }
        sites.add(new VirtualCallSite(s, m, iie, subSig, kind));
    }
    private void processNewMethod( SootMethod m ) {
        if( m.isNative() || m.isPhantom() ) {
            return;
        }
        Body b = m.retrieveActiveBody();
        getImplicitTargets( m );
        findReceivers(m, b);
    }
    private void findReceivers(SootMethod m, Body b) {
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if (s.containsInvokeExpr()) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();

                if (ie instanceof InstanceInvokeExpr) {
                    InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
                    Local receiver = (Local) iie.getBase();
                    NumberedString subSig = 
                        iie.getMethodRef().getSubSignature();
                    addVirtualCallSite( s, m, receiver, iie, subSig,
                            Edge.ieToKind(iie) );
                    if( subSig == sigStart ) {
                        addVirtualCallSite( s, m, receiver, iie, sigRun,
                                Kind.THREAD );
                    }
                } else {
                    SootMethod tgt = ((StaticInvokeExpr) ie).getMethod();
                    addEdge(m, s, tgt);
                    if( tgt.getSignature().equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedAction)>" )
                    ||  tgt.getSignature().equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction)>" )
                    ||  tgt.getSignature().equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedAction,java.security.AccessControlContext)>" )
                    ||  tgt.getSignature().equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction,java.security.AccessControlContext)>" ) ) {

                        Local receiver = (Local) ie.getArg(0);
                        addVirtualCallSite( s, m, receiver, null, sigObjRun,
                                Kind.PRIVILEGED );
                    }
                }
            }
        }
    }
    
    private void getImplicitTargets( SootMethod source ) {
        List stringConstants = (List) methodToStringConstants.get(source);
        if( stringConstants == null )
            methodToStringConstants.put(source, stringConstants = new ArrayList());
        final SootClass scl = source.getDeclaringClass();
        if( source.isNative() || source.isPhantom() ) return;
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
                if( ie.getMethod().getSignature().equals( "<java.lang.Class: java.lang.Object newInstance()>" ) ) {
                    if( options.safe_newinstance() ) {
                        for( Iterator tgtIt = EntryPoints.v().inits().iterator(); tgtIt.hasNext(); ) {
                            final SootMethod tgt = (SootMethod) tgtIt.next();
                            addEdge( source, s, tgt, Kind.NEWINSTANCE );
                        }
                    } else {
                        for( Iterator clsIt = Scene.v().dynamicClasses().iterator(); clsIt.hasNext(); ) {
                            final SootClass cls = (SootClass) clsIt.next();
                            if( cls.declaresMethod(sigInit) ) {
                                addEdge( source, s, cls.getMethod(sigInit), Kind.NEWINSTANCE );
                            }
                        }
                        
                        if( options.verbose() ) {
                            G.v().out.println( "Warning: Method "+source+
                                " is reachable, and calls Class.newInstance;"+
                                " graph will be incomplete!"+
                                " Use safe-newinstance option for a conservative result." );
                        }
                    } 
                }
                if( ie instanceof StaticInvokeExpr ) {
                    SootClass cl = ie.getMethodRef().declaringClass();
                    for( Iterator clinitIt = EntryPoints.v().clinitsOf(cl).iterator(); clinitIt.hasNext(); ) {
                        final SootMethod clinit = (SootMethod) clinitIt.next();
                        addEdge( source, s, clinit, Kind.CLINIT );
                    }
                }
                if( ie.getMethodRef().getSubSignature() == sigForName ) {
                    Value className = ie.getArg(0);
                    if( className instanceof StringConstant ) {
                        String cls = ((StringConstant) className ).value;
                        constantForName( cls, source, s );
                    } else {
                        Local constant = (Local) className;
                        if( options.safe_forname() ) {
                            for( Iterator tgtIt = EntryPoints.v().clinits().iterator(); tgtIt.hasNext(); ) {
                                final SootMethod tgt = (SootMethod) tgtIt.next();
                                addEdge( source, s, tgt, Kind.CLINIT );
                            }
                        } else {
                            for( Iterator clsIt = Scene.v().dynamicClasses().iterator(); clsIt.hasNext(); ) {
                                final SootClass cls = (SootClass) clsIt.next();
                                for( Iterator clinitIt = EntryPoints.v().clinitsOf(cls).iterator(); clinitIt.hasNext(); ) {
                                    final SootMethod clinit = (SootMethod) clinitIt.next();
                                    addEdge( source, s, clinit, Kind.CLINIT);
                                }
                            }
                            VirtualCallSite site = new VirtualCallSite( s, source, null, null, Kind.CLINIT );
                            List sites = (List) stringConstToSites.get(constant);
                            if (sites == null) {
                                stringConstToSites.put(constant, sites = new ArrayList());
                                stringConstants.add(constant);
                            }
                            sites.add(site);
                        }
                    }
                }
            }
            if( s.containsFieldRef() ) {
                FieldRef fr = (FieldRef) s.getFieldRef();
                if( fr instanceof StaticFieldRef ) {
                    SootClass cl = fr.getFieldRef().declaringClass();
                    for( Iterator clinitIt = EntryPoints.v().clinitsOf(cl).iterator(); clinitIt.hasNext(); ) {
                        final SootMethod clinit = (SootMethod) clinitIt.next();
                        addEdge( source, s, clinit, Kind.CLINIT );
                    }
                }
            }
            if( s instanceof AssignStmt ) {
                Value rhs = ((AssignStmt)s).getRightOp();
                if( rhs instanceof NewExpr ) {
                    NewExpr r = (NewExpr) rhs;
                    SootClass cl = r.getBaseType().getSootClass();
                    for( Iterator clinitIt = EntryPoints.v().clinitsOf(cl).iterator(); clinitIt.hasNext(); ) {
                        final SootMethod clinit = (SootMethod) clinitIt.next();
                        addEdge( source, s, clinit, Kind.CLINIT );
                    }
                } else if( rhs instanceof NewArrayExpr || rhs instanceof NewMultiArrayExpr ) {
                    Type t = rhs.getType();
                    if( t instanceof ArrayType ) t = ((ArrayType)t).baseType;
                    if( t instanceof RefType ) {
                        SootClass cl = ((RefType) t).getSootClass();
                        for( Iterator clinitIt = EntryPoints.v().clinitsOf(cl).iterator(); clinitIt.hasNext(); ) {
                            final SootMethod clinit = (SootMethod) clinitIt.next();
                            addEdge( source, s, clinit, Kind.CLINIT );
                        }
                    }
                }
            }
        }
    }

    private void processNewMethodContext( MethodOrMethodContext momc ) {
        SootMethod m = momc.method();
        Object ctxt = momc.context();
        Iterator it = cicg.edgesOutOf(m);
        while( it.hasNext() ) {
            Edge e = (Edge) it.next();
            cm.addStaticEdge( momc, e.srcUnit(), e.tgt(), e.kind() );
        }
    }

    private void handleInit(SootMethod source, final SootClass scl) {
        addEdge( source, null, scl, sigFinalize, Kind.FINALIZE );
        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
    }
    private void constantForName( String cls, SootMethod src, Stmt srcUnit ) {
        if( cls.length() > 0 && cls.charAt(0) == '[' ) {
            if( cls.length() > 1 && cls.charAt(1) == 'L' && cls.charAt(cls.length()-1) == ';' ) {
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
                for( Iterator clinitIt = EntryPoints.v().clinitsOf(sootcls).iterator(); clinitIt.hasNext(); ) {
                    final SootMethod clinit = (SootMethod) clinitIt.next();
                    addEdge( src, srcUnit, clinit, Kind.CLINIT );
                }

            }
        }
    }

    private void addEdge( SootMethod src, Stmt stmt, SootMethod tgt,
            Kind kind ) {
        cicg.addEdge( new Edge( src, stmt, tgt, kind ) );
    }

    private void addEdge(  SootMethod src, Stmt stmt, SootClass cls, NumberedString methodSubSig, Kind kind ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            addEdge( src, stmt, cls.getMethod( methodSubSig ), kind );
        }
    }
    private void addEdge( SootMethod src, Stmt stmt, String methodSig, Kind kind ) {
        if( Scene.v().containsMethod( methodSig ) ) {
            addEdge( src, stmt, Scene.v().getMethod( methodSig ), kind );
        }
    }
    private void addEdge( SootMethod src, Stmt stmt, SootMethod tgt ) {
        InvokeExpr ie = stmt.getInvokeExpr();
        addEdge( src, stmt, tgt, Edge.ieToKind(ie) );
    }

    private final NumberedString sigMain = Scene.v().getSubSigNumberer().
        findOrAdd( "void main(java.lang.String[])" );
    private final NumberedString sigFinalize = Scene.v().getSubSigNumberer().
        findOrAdd( "void finalize()" );
    private final NumberedString sigExit = Scene.v().getSubSigNumberer().
        findOrAdd( "void exit()" );
    private final NumberedString sigInit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <init>()" );
    private final NumberedString sigStart = Scene.v().getSubSigNumberer().
        findOrAdd( "void start()" );
    private final NumberedString sigRun = Scene.v().getSubSigNumberer().
        findOrAdd( "void run()" );
    private final NumberedString sigObjRun = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Object run()" );
    private final NumberedString sigForName = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Class forName(java.lang.String)" );
    private final RefType clRunnable = RefType.v("java.lang.Runnable");
    
}

