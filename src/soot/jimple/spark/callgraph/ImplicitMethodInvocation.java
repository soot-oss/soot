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
import soot.jimple.*;
import soot.util.*;
import java.util.*;


/** Given a method, returns the set of methods that may be invoked implicitly
 * by the VM for that method.
 * @author Ondrej Lhotak
 */
public class ImplicitMethodInvocation
{ 
    private static final NumberedString sigMain = Scene.v().getSubSigNumberer().
        findOrAdd( "void main(java.lang.String[])" );
    private static final NumberedString sigFinalize = Scene.v().getSubSigNumberer().
        findOrAdd( "void finalize()" );
    private static final NumberedString sigClinit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <clinit>()" );
    private static final NumberedString sigStart = Scene.v().getSubSigNumberer().
        findOrAdd( "void start()" );
    private static final NumberedString sigRun = Scene.v().getSubSigNumberer().
        findOrAdd( "void run()" );
    private static final NumberedString sigForName = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Class forName(java.lang.String)" );
    private final void addMethod( NumberedSet set, SootClass cls, NumberedString methodSubSig ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            set.add( cls.getMethod( methodSubSig ) );
        }
    }
    private final void addMethod( NumberedSet set, String methodName ) {
        SootMethod m = Scene.v().forceGetMethod( methodName );
        if( m != null ) set.add(m);
    }
    public NumberedSet getEntryPoints() {
        NumberedSet ret = new NumberedSet( Scene.v().getMethodNumberer() );
        addMethod( ret, Scene.v().getMainClass(), sigMain );
        addMethod( ret, Scene.v().getMainClass(), sigClinit );
        addMethod( ret, "<java.lang.System: void initializeSystemClass()>" );
        addMethod( ret, "<java.lang.ThreadGroup: void <init>()>");
        addMethod( ret, "<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>");
        addMethod( ret, "<java.lang.System: void loadLibrary(java.lang.String)>");
        addMethod( ret, "<java.lang.ClassLoader: java.lang.Class loadClassInternal(java.lang.String)>");
        return ret;
    }
    public NumberedSet getImplicitTargets( SootMethod source ) {
        NumberedSet ret = new NumberedSet( Scene.v().getMethodNumberer() );
        if( source.isNative() ) return ret;
        if( source.getSubSignature().indexOf( "<init>" ) >= 0 ) {
            addMethod( ret, source.getDeclaringClass(), sigFinalize );
        }
        if( source.getNumberedSubSignature() == sigClinit 
        && source.getDeclaringClass().hasSuperclass() ) {
            addMethod( ret, source.getDeclaringClass().getSuperclass(), sigClinit );
        }
        Body b = source.retrieveActiveBody();
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s.containsInvokeExpr() ) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
                if( ie.getMethod().getSignature().equals( "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>" ) ) {
                    System.out.println( "WARNING: call to java.lang.reflect.Method: invoke() from "+source+
                            "; graph will be incomplete!" );
                }
                if( ie instanceof StaticInvokeExpr ) {
                    ret.add( ie.getMethod() );
                } else if( ie.getMethod().getNumberedSubSignature() == sigForName ) {
                    Value name = ie.getArg(0);
                    if( name instanceof StringConstant ) {
                        String cls = ((StringConstant) name ).value;
                        addMethod( ret, Scene.v().loadClassAndSupport( cls ), sigClinit );
                    } else {
                        System.out.println( "WARNING: Method "+source+
                                " is reachable, and calls Class.forName on a"+
                                " non-constant String; graph will be incomplete!" );
                    }
                }
                addMethod( ret, ie.getMethod().getDeclaringClass(), sigClinit );
            }
            if( s.containsFieldRef() ) {
                FieldRef fr = (FieldRef) s.getFieldRef();
                if( fr instanceof StaticFieldRef ) {
                    SootClass cl = fr.getField().getDeclaringClass();
                    if( cl.declaresMethod( sigClinit ) )
                        ret.add( cl.getMethod( sigClinit ) );
                }
            }
            if( s instanceof AssignStmt ) {
                Value rhs = ((AssignStmt)s).getRightOp();
                if( rhs instanceof NewExpr ) {
                    NewExpr r = (NewExpr) rhs;
                    addMethod( ret, r.getBaseType().getSootClass(), sigClinit );
                } else if( rhs instanceof NewArrayExpr || rhs instanceof NewMultiArrayExpr ) {
                    Type t = rhs.getType();
                    if( t instanceof ArrayType ) t = ((ArrayType)t).baseType;
                    if( t instanceof RefType ) {
                        addMethod( ret, ((RefType) t).getSootClass(), sigClinit );
                    }
                }
            }
        }
        return ret;
    }
}


