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

    private LargeNumberedMap receiverToSites = new LargeNumberedMap( Scene.v().getLocalNumberer() );
    private LargeNumberedMap methodToReceivers = new LargeNumberedMap( Scene.v().getMethodNumberer() );
    public LargeNumberedMap methodToReceivers() { return methodToReceivers; }

    private SmallNumberedMap stringConstToSites = new SmallNumberedMap( Scene.v().getLocalNumberer() );
    private LargeNumberedMap methodToStringConstants = new LargeNumberedMap( Scene.v().getMethodNumberer() );
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
            MethodOrMethodContext momc = (MethodOrMethodContext) worklist.next();
            if( momc == null ) {
                rm.update();
                momc = (MethodOrMethodContext) worklist.next();
                if( momc == null ) break;
            }
            SootMethod m = momc.method();
            if( appOnly && !m.getDeclaringClass().isApplicationClass() ) continue;
            if( analyzedMethods.add( m ) ) processNewMethod( m );
            processNewMethodContext( momc );
        }
    }
    public boolean wantTypes( Local receiver ) {
        return receiverToSites.get(receiver) != null;
    }
    public void addType( Local receiver, Object srcContext, Type type, Object typeContext ) {
        for( Iterator siteIt = ((Collection) receiverToSites.get( receiver )).iterator(); siteIt.hasNext(); ) {
            final VirtualCallSite site = (VirtualCallSite) siteIt.next();
            VirtualCalls.v().resolve( type,
                    site.getInstanceInvokeExpr(),
                    site.getContainer(),
                    targetsQueue );
            while(true) {
                SootMethod target = (SootMethod) targets.next();
                if( target == null ) break;
                cm.addVirtualEdge(
                        MethodContext.v( site.getContainer(), srcContext ),
                        new Edge( site.getContainer(),
                                  site.getStmt(),
                                  target ),
                        typeContext );
            }
            if( site.getInstanceInvokeExpr().getMethod().getNumberedSubSignature() == sigStart ) {
                VirtualCalls.v().resolveThread( type,
                        site.getInstanceInvokeExpr(),
                        site.getContainer(),
                        targetsQueue );
                while(true) {
                    SootMethod target = (SootMethod) targets.next();
                    if( target == null ) break;
                    cm.addVirtualEdge(
                            MethodContext.v( site.getContainer(), srcContext ),
                            new Edge( site.getContainer(),
                                      site.getStmt(),
                                      target,
                                      Edge.THREAD ),
                            typeContext );
                }
            }
        }
    }
    public boolean wantStringConstants( Local stringConst ) {
        return stringConstToSites.get(stringConst) != null;
    }
    public void addStringConstant( Local l, Object srcContext, String constant, Object typeContext ) {
        for( Iterator siteIt = ((Collection) stringConstToSites.get( l )).iterator(); siteIt.hasNext(); ) {
            final VirtualCallSite site = (VirtualCallSite) siteIt.next();
            if( constant == null ) {
                if( options.verbose() ) {
                    G.v().out.println( "Warning: Method "+site.getContainer()+
                        " is reachable, and calls Class.forName on a"+
                        " non-constant String; graph will be incomplete!"+
                        " Use safe-forname option for a conservative result." );
                }
            } else {
                if( constant.charAt(0) == '[' ) {
                    if( constant.charAt(1) == 'L' 
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
                    if( sootcls.declaresMethod( sigClinit ) ) {
                        cm.addStaticEdge(
                              MethodContext.v( site.getContainer(),
                                               srcContext ),
                              new Edge( site.getContainer(),
                                        site.getStmt(),
                                        sootcls.getMethod(sigClinit),
                                        Edge.CLINIT ) );
                    }
                }
            }
        }
    }

    /* End of public methods. */

    private void processNewMethod( SootMethod m ) {
        if( m.isNative() || m.isPhantom() ) {
            return;
        }
        Body b = m.retrieveActiveBody();
        getImplicitTargets( m );
        findReceivers(m, b);
    }
    private void findReceivers(SootMethod m, Body b) {
        List receivers = (List) methodToReceivers.get(m);
        if( receivers == null )
            methodToReceivers.put(m, receivers = new ArrayList());
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if (s.containsInvokeExpr()) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();

                if (ie instanceof InstanceInvokeExpr) {
                    VirtualCallSite site = new VirtualCallSite(s, m);
                    Local receiver =
                        (Local) ((InstanceInvokeExpr) ie).getBase();
                    List sites = (List) receiverToSites.get(receiver);
                    if (sites == null) {
                        receiverToSites.put(receiver, sites = new ArrayList());
                        receivers.add(receiver);
                    }
                    sites.add(site);

                } else {
                    SootMethod tgt = ((StaticInvokeExpr) ie).getMethod();
                    cicg.addEdge(new Edge(m, s, tgt));
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
                            cicg.addEdge( new Edge( source, s, tgt, Edge.NEWINSTANCE ) );
                        }
                    } else {
                        if( options.verbose() ) {
                            G.v().out.println( "Warning: Method "+source+
                                " is reachable, and calls Class.newInstance;"+
                                " graph will be incomplete!"+
                                " Use safe-newinstance option for a conservative result." );
                        }
                    } 
                }
                if( ie instanceof StaticInvokeExpr ) {
                    addEdge( source, s, ie.getMethod().getDeclaringClass(),
                        sigClinit, Edge.CLINIT );
                }
                if( ie.getMethod().getNumberedSubSignature() == sigForName ) {
                    Value className = ie.getArg(0);
                    if( className instanceof StringConstant ) {
                        String cls = ((StringConstant) className ).value;
                        constantForName( cls, source, s );
                    } else {
                        Local constant = (Local) className;
                        if( options.safe_forname() ) {
                            for( Iterator tgtIt = EntryPoints.v().clinits().iterator(); tgtIt.hasNext(); ) {
                                final SootMethod tgt = (SootMethod) tgtIt.next();
                                cicg.addEdge( new Edge( source, s, tgt, Edge.CLINIT ) );
                            }
                        } else {
                            VirtualCallSite site = new VirtualCallSite( s, source );
                            stringConstToSites.put( constant, site );
                            stringConstants.add( constant );
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

    private void processNewMethodContext( MethodOrMethodContext momc ) {
        SootMethod m = momc.method();
        Object ctxt = momc.context();
        Iterator it = cicg.edgesOutOf(m);
        while( it.hasNext() ) {
            Edge e = (Edge) it.next();
            cm.addStaticEdge( momc, e );
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

    private final void addEdge(  SootMethod src, Stmt stmt, SootClass cls, NumberedString methodSubSig, int type ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            cicg.addEdge(
                new Edge( src, stmt, cls.getMethod( methodSubSig ), type ) );
        }
    }
    private final void addEdge( SootMethod src, Stmt stmt, String methodSig, int type ) {
        if( Scene.v().containsMethod( methodSig ) ) {
            cicg.addEdge(
                new Edge( src, stmt, Scene.v().getMethod( methodSig ), type ) );
        }
    }




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

