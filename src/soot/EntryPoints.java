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

package soot;
import soot.jimple.*;
import soot.util.*;
import java.util.*;


/** Returns the various potential entry points of a Java program.
 * @author Ondrej Lhotak
 */
public class EntryPoints
{ 
    public EntryPoints( Singletons.Global g ) {}
    public static EntryPoints v() { return G.v().soot_EntryPoints(); }

    final NumberedString sigMain = Scene.v().getSubSigNumberer().
        findOrAdd( "void main(java.lang.String[])" );
    final NumberedString sigFinalize = Scene.v().getSubSigNumberer().
        findOrAdd( "void finalize()" );
    final NumberedString sigExit = Scene.v().getSubSigNumberer().
        findOrAdd( "void exit()" );
    final NumberedString sigClinit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <clinit>()" );
    final NumberedString sigInit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <init>()" );
    final NumberedString sigStart = Scene.v().getSubSigNumberer().
        findOrAdd( "void start()" );
    final NumberedString sigRun = Scene.v().getSubSigNumberer().
        findOrAdd( "void run()" );
    final NumberedString sigObjRun = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Object run()" );
    final NumberedString sigForName = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Class forName(java.lang.String)" );
    private final void addMethod( List set, SootClass cls, NumberedString methodSubSig ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            set.add( cls.getMethod( methodSubSig ) );
        }
    }
    private final void addMethod( List set, String methodSig ) {
        if( Scene.v().containsMethod( methodSig ) ) {
            set.add( Scene.v().getMethod( methodSig ) );
        }
    }
    /** Returns only the application entry points, not including entry points
     * invoked implicitly by the VM. */
    public List application() {
        List ret = new ArrayList();
        addMethod( ret, Scene.v().getMainClass(), sigMain );
        for( Iterator clinitIt = clinitsOf(Scene.v().getMainClass() ).iterator(); clinitIt.hasNext(); ) {
            final SootMethod clinit = (SootMethod) clinitIt.next();
            ret.add(clinit);
        }
        return ret;
    }
    /** Returns only the entry points invoked implicitly by the VM. */
    public List implicit() {
        List ret = new ArrayList();
        addMethod( ret, "<java.lang.System: void initializeSystemClass()>" );
        addMethod( ret, "<java.lang.ThreadGroup: void <init>()>");
        //addMethod( ret, "<java.lang.ThreadGroup: void remove(java.lang.Thread)>");
        addMethod( ret, "<java.lang.Thread: void exit()>");
        addMethod( ret, "<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>");
        //addMethod( ret, "<java.lang.System: void loadLibrary(java.lang.String)>");
        addMethod( ret, "<java.lang.ClassLoader: void <init>()>");
        addMethod( ret, "<java.lang.ClassLoader: java.lang.Class loadClassInternal(java.lang.String)>");
        addMethod( ret, "<java.lang.ClassLoader: void checkPackageAccess(java.lang.Class,java.security.ProtectionDomain)>");
        addMethod( ret, "<java.lang.ClassLoader: void addClass(java.lang.Class)>");
        addMethod( ret, "<java.lang.ClassLoader: long findNative(java.lang.ClassLoader,java.lang.String)>");
        addMethod( ret, "<java.security.PrivilegedActionException: void <init>(java.lang.Exception)>");
        //addMethod( ret, "<java.lang.ref.Finalizer: void register(java.lang.Object)>");
        addMethod( ret, "<java.lang.ref.Finalizer: void runFinalizer()>");
        addMethod( ret, "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.Runnable)>");
        addMethod( ret, "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>");
        return ret;
    }
    /** Returns all the entry points. */
    public List all() {
        List ret = new ArrayList();
        ret.addAll( application() );
        ret.addAll( implicit() );
        return ret;
    }
    /** Returns a list of all static initializers. */
    public List clinits() {
        List ret = new ArrayList();
        for( Iterator clIt = Scene.v().getClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            addMethod( ret, cl, sigClinit );
        }
        return ret;
    }
    /** Returns a list of all constructors taking no arguments. */
    public List inits() {
        List ret = new ArrayList();
        for( Iterator clIt = Scene.v().getClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            addMethod( ret, cl, sigInit );
        }
        return ret;
    }

    /** Returns a list of all concrete methods of all application classes. */
    public List methodsOfApplicationClasses() {
        List ret = new ArrayList();
        for( Iterator clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            for( Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();
                if( m.isConcrete() ) ret.add( m );
            }
        }
        return ret;
    }

    /** Returns a list of all concrete main(String[]) methods of all
     * application classes. */
    public List mainsOfApplicationClasses() {
        List ret = new ArrayList();
        for( Iterator clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            if( cl.declaresMethod( "void main(java.lang.String[])" ) ) {
                SootMethod m = cl.getMethod("void main(java.lang.String[])" );
                if( m.isConcrete() ) ret.add( m );
            }
        }
        return ret;
    }

    /** Returns a list of all clinits of class cl and its superclasses. */
    public List clinitsOf( SootClass cl ) {
        List ret = new ArrayList();
        while(true) {
            addMethod( ret, cl, sigClinit );
            if( !cl.hasSuperclass() ) break;
            cl = cl.getSuperclass();
        }
        return ret;
    }
}


