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
import soot.util.*;
import java.util.*;
import soot.entrypoints.*;


/** Returns the various potential entry points of a Java program.
 * The entry points are detected/returned in the following different ways:
 * <ul>
 *  <li>Setting entry points manually in the Scene</li>
 *  <li>Not setting any entry point detector, and rely on the Main detection (default)</li>
 *  <li>Set entry point detectors, which will ignore the default Main detection</li>
 * </ul>
 * @author Ondrej Lhotak
 * @author Marc-Andre Laverdiere-Papineau
 */
public class EntryPoints
{ 
    
    private Collection<EntryPointDetector> entryPoints = new ArrayList<EntryPointDetector>();
    
    public EntryPoints( Singletons.Global g ) {}
    
    /**
    * Adds an entry point detector.
    */ 
    public void addEntryPointDetector(EntryPointDetector epd){
        entryPoints.add(epd);
    }
    
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
    private final void addMethod( List<SootMethod> set, SootClass cls, NumberedString methodSubSig ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            set.add( cls.getMethod( methodSubSig ) );
        }
    }
    private final void addMethod( List<SootMethod> set, String methodSig ) {
        if( Scene.v().containsMethod( methodSig ) ) {
            set.add( Scene.v().getMethod( methodSig ) );
        }
    }
    /** Returns only the application entry points, not including entry points
     * invoked implicitly by the VM. */
    public List<SootMethod> application() {
        List<SootMethod> ret = new ArrayList<SootMethod>();
        Collection<SootClass> scope;
        if (Scene.v().hasCustomEntryPoints())
            return Scene.v().getEntryPoints();
       else if (entryPoints.isEmpty()){
            //Default is detecting a main method
            entryPoints.add(new MainDetector());
            scope = Arrays.asList(Scene.v().getMainClass());
       } else {
           scope = Scene.v().getApplicationClasses();
       }
        ret.addAll(searchEntryPoints(entryPoints, scope));
        ret.addAll(clinitsOf(ret));
        return ret;
    }
    /** Returns only the entry points invoked implicitly by the VM. */
    public List<SootMethod> implicit() {
        Collection<EntryPointDetector> implicitDetectors = new ArrayList<EntryPointDetector>();
        implicitDetectors.add(new SignatureEntryPointDetector("<java.lang.System: void initializeSystemClass()>" ));
        implicitDetectors.add(new SignatureEntryPointDetector("<java.lang.ThreadGroup: void <init>()>" ));
        implicitDetectors.add(new SignatureEntryPointDetector("<java.lang.Thread: void exit()>"));
        implicitDetectors.add(new SignatureEntryPointDetector( "<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>"));
        implicitDetectors.add(new SignatureEntryPointDetector("<java.lang.ClassLoader: void <init>()>"));
        implicitDetectors.add(new SignatureEntryPointDetector("<java.lang.ClassLoader: java.lang.Class loadClassInternal(java.lang.String)>"));
        implicitDetectors.add(new SignatureEntryPointDetector("<java.lang.ClassLoader: void checkPackageAccess(java.lang.Class,java.security.ProtectionDomain)>" ));
        implicitDetectors.add(new SignatureEntryPointDetector( "<java.lang.ClassLoader: void addClass(java.lang.Class)>"));
        implicitDetectors.add(new SignatureEntryPointDetector("<java.lang.ClassLoader: long findNative(java.lang.ClassLoader,java.lang.String)>" ));
        implicitDetectors.add(new SignatureEntryPointDetector("<java.security.PrivilegedActionException: void <init>(java.lang.Exception)>" ));
        implicitDetectors.add(new SignatureEntryPointDetector( "<java.lang.ref.Finalizer: void runFinalizer()>" ));
        implicitDetectors.add(new SignatureEntryPointDetector("<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.Runnable)>" ));
        implicitDetectors.add(new SignatureEntryPointDetector( "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>" ));
        

        return searchEntryPoints(implicitDetectors, Scene.v().getLibraryClasses());
    }
   
    /**
    * Search for entry points among the specified classes
    * @param epd the entry point detector
    * @param classes the collection of classes to examine with the entry point detector
    * @return a non-null list of entry points detected
    */  
    private List<SootMethod> searchEntryPoints(EntryPointDetector epd, Collection<SootClass> classes){
        final List<SootMethod> ret = new ArrayList<SootMethod>();
        for (SootClass sc: classes)
            for (SootMethod sm : sc.getMethods())
                if (epd.isEntryPoint(sm))
                    ret.add(sm);
        return ret;
    }

    /**
    * Search for entry points among the specified classes.
    * The entry point will be detected if any of the detectors specified consider the method as an entry point
    * @param detectors a non-null collection of entry point detectors
    * @param classes the collection of classes to examine with the entry point detector
    * @return a non-null list of entry points detected
    */ 
    private List<SootMethod> searchEntryPoints(Collection<EntryPointDetector> detectors, Collection<SootClass> classes){
        final List<SootMethod> ret = new ArrayList<SootMethod>();
        for (SootClass sc: classes)
            for (SootMethod sm : sc.getMethods())
                for (EntryPointDetector epd : detectors)
                    if (epd.isEntryPoint(sm))
                        ret.add(sm);
        return ret;
    }
    
    /** Returns all the entry points. */
    public List<SootMethod> all() {
        List<SootMethod> ret = new ArrayList<SootMethod>();
        ret.addAll( application() );
        ret.addAll( implicit() );
        return ret;
    }
    /** Returns a list of all static initializers. */
    public List<SootMethod> clinits() {
        return searchEntryPoints(new SubSignatureEntryPointDetector("void <clinit>()"), Scene.v().getClasses(SootClass.SIGNATURES));
    }
    /** Returns a list of all constructors taking no arguments. */
    public List<SootMethod> inits() {
        return searchEntryPoints(new SubSignatureEntryPointDetector("void <init>()"), Scene.v().getClasses(SootClass.SIGNATURES));
    }
    
    /** Returns a list of all constructors. */
    public List<SootMethod> allInits() {
        return searchEntryPoints(new MethodNameEntrypointDetector("<init>"), Scene.v().getClasses());
    }

    /** Returns a list of all concrete methods of all application classes. */
    public List<SootMethod> methodsOfApplicationClasses() {
        return searchEntryPoints(new ConcreteMethodDetector(), Scene.v().getApplicationClasses());
    }

    /** Returns a list of all concrete main(String[]) methods of all
     * application classes. */
    public List<SootMethod> mainsOfApplicationClasses() {
        return searchEntryPoints(new MainDetector(),  Scene.v().getApplicationClasses());
    }

    /**
    * Finds the static initializers of the classes that define the specified methods.
    * There should not be any duplication of initializers.
    * @param cl the methods to examine
    * @return a non-null list of static initializers.
    */ 
    public List<SootMethod> clinitsOf( Collection<SootMethod> cl ) {
        Set<SootMethod> ret = new HashSet<SootMethod>(); //filters duplicates automatically
        for (SootMethod sm : cl)
            ret.addAll(clinitsOf(sm.getDeclaringClass()));
        return new ArrayList<SootMethod>(ret);
    }
    
    /** Returns a list of all clinits of class cl and its superclasses. */
    public List<SootMethod> clinitsOf( SootClass cl ) {
        List<SootMethod> ret = new ArrayList<SootMethod>();
        while(true) {
            addMethod( ret, cl, sigClinit );
            if( !cl.hasSuperclass() ) break;
            cl = cl.getSuperclass();
        }
        return ret;
    }
}


