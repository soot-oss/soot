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
import soot.jimple.paddle.queue.*;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/** Keeps track of which methods are reachable.
 * @author Ondrej Lhotak
 */
public class TradStaticCallBuilder extends AbsStaticCallBuilder
{ 
    TradStaticCallBuilder( Rctxt_method in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out, Qvar_srcm_stmt_signature_kind receivers, Qvar_srcm_stmt_tgtm specials ) {
        super( in, out, receivers, specials );
    }
    private boolean change;
    public boolean update() {
        change = false;
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
            SootMethod m = t.method();
            if( m.isNative() ) processNativeMethod(m);
            else if( !m.isPhantom() ) processMethod( m );
        }
        return change;
    }

    protected void processNativeMethod( SootMethod source ) {
        if( source.getSignature().equals( "<java.lang.ref.Finalizer: void invokeFinalizeMethod(java.lang.Object)>" )) {
            VarNode receiver = (VarNode) new MethodNodeFactory(source).caseParm(0);
            receivers.add( receiver, source, null, sigFinalize, Kind.INVOKE_FINALIZE );
        }
    }
    protected void processMethod( SootMethod source ) {
        MethodNodeFactory mnf = new MethodNodeFactory(source);
        final SootClass scl = source.getDeclaringClass();
        if( source.isNative() || source.isPhantom() ) return;
        
        Body b = source.retrieveActiveBody();
        boolean warnedAlready = false;
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s.containsInvokeExpr() ) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();

                // Deal with virtual and THREAD calls
                if (ie instanceof InstanceInvokeExpr) {
                    Scene.v().getUnitNumberer().add(s);

                    InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
                    VarNode receiver = (VarNode) mnf.getNode(iie.getBase());
                    if( ie instanceof SpecialInvokeExpr ) {
                        SootMethod tgt = VirtualCalls.v().resolveSpecial(
                                (SpecialInvokeExpr) ie,
                                ie.getMethodRef().getSubSignature(),
                                source );
                        specials.add( receiver, source, s, tgt );
                        change = true;
                    } else {
                        NumberedString subSig = 
                            iie.getMethodRef().getSubSignature();

                        receivers.add( receiver, source, s, subSig, Edge.ieToKind(iie) );
                        change = true;
                        if( subSig == sigStart ) {
                            receivers.add( receiver, source, s, sigRun, Kind.THREAD ); 
                            change = true;
                        }
                    }

                // Deal with PRIVILEGED calls
                } else {
                    SootMethod tgt = ((StaticInvokeExpr) ie).getMethod();
                    addEdge(source, s, tgt);
                    if( tgt.getSignature().equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedAction)>" )
                    ||  tgt.getSignature().equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction)>" )
                    ||  tgt.getSignature().equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedAction,java.security.AccessControlContext)>" )
                    ||  tgt.getSignature().equals( "<java.security.AccessController: java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction,java.security.AccessControlContext)>" ) ) {

                        VarNode receiver = (VarNode) mnf.getNode(ie.getArg(0));
                        receivers.add( receiver, source, s, sigObjRun, Kind.PRIVILEGED );
                        change = true;
                    }
                }
                

                // Deal with calls to Method.invoke()
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
                
                // Deal with Class.newInstance() calls
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
                        if( options.verbose() && Scene.v().dynamicClasses().isEmpty()) {
                            G.v().out.println( "Warning: Method "+source+
                                " is reachable, and calls Class.newInstance,"+
                                " and you didn't specify any dynamic classes;"+
                                " graph may be incomplete!"+
                                " Use safe-newinstance option for a conservative result." );
                        }
                    } 
                }

                // Deal with other CLINIT calls
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
                        VarNode constant = (VarNode) mnf.getNode(className);
                        if( options.safe_forname() ) {
                            for( Iterator tgtIt = EntryPoints.v().clinits().iterator(); tgtIt.hasNext(); ) {
                                final SootMethod tgt = (SootMethod) tgtIt.next();
                                addEdge( source, s, tgt, Kind.CLINIT );
                            }
                        } else {
                            receivers.add( constant, source, s, null, Kind.CLINIT );
                            change = true;
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
                    while(true) {
                        if( !cl.hasSuperclass() ) break;
                        if( cl.declaresMethod( sigFinalize ) ) {
                            if( Scene.v().containsClass("java.lang.ref.Finalizer") ) {
                                addEdge( source, s, Scene.v().getSootClass("java.lang.ref.Finalizer").getMethod(sigRegister), Kind.FINALIZE );
                            }
                            break;
                        }
                        cl = cl.getSuperclass();
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

    protected void addEdge( SootMethod src, Stmt stmt, SootMethod tgt, Kind kind ) {
        Scene.v().getUnitNumberer().add(stmt);
        out.add( null, src, stmt, kind, null, tgt );
        change = true;
    }

    protected void constantForName( String cls, SootMethod src, Stmt srcUnit ) {
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
                for( Iterator clinitIt = EntryPoints.v().clinitsOf(sootcls).iterator(); clinitIt.hasNext(); ) {
                    final SootMethod clinit = (SootMethod) clinitIt.next();
                    addEdge( src, srcUnit, clinit, Kind.CLINIT );
                }
            }
        }
    }

    protected void addEdge(  SootMethod src, Stmt stmt, SootClass cls, NumberedString methodSubSig, Kind kind ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            addEdge( src, stmt, cls.getMethod( methodSubSig ), kind );
        }
    }
    protected void addEdge( SootMethod src, Stmt stmt, String methodSig, Kind kind ) {
        if( Scene.v().containsMethod( methodSig ) ) {
            addEdge( src, stmt, Scene.v().getMethod( methodSig ), kind );
        }
    }
    protected void addEdge( SootMethod src, Stmt stmt, SootMethod tgt ) {
        InvokeExpr ie = stmt.getInvokeExpr();
        if( ie instanceof SpecialInvokeExpr ) throw new RuntimeException("DEBUG");
        addEdge( src, stmt, tgt, Edge.ieToKind(ie) );
    }

    protected final NumberedString sigMain = Scene.v().getSubSigNumberer().
        findOrAdd( "void main(java.lang.String[])" );
    protected final NumberedString sigFinalize = Scene.v().getSubSigNumberer().
        findOrAdd( "void finalize()" );
    protected final NumberedString sigExit = Scene.v().getSubSigNumberer().
        findOrAdd( "void exit()" );
    protected final NumberedString sigInit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <init>()" );
    protected final NumberedString sigClinit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <clinit>()" );
    protected final NumberedString sigStart = Scene.v().getSubSigNumberer().
        findOrAdd( "void start()" );
    protected final NumberedString sigRun = Scene.v().getSubSigNumberer().
        findOrAdd( "void run()" );
    protected final NumberedString sigObjRun = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Object run()" );
    protected final NumberedString sigForName = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Class forName(java.lang.String)" );
    protected final NumberedString sigRegister = Scene.v().getSubSigNumberer().
        findOrAdd( "void register(java.lang.Object)" );
}

