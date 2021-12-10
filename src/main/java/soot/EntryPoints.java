package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import soot.util.NumberedString;
import soot.util.StringNumberer;

/**
 * Returns the various potential entry points of a Java program.
 * 
 * @author Ondrej Lhotak
 */
public class EntryPoints {

  final NumberedString sigMain;
  final NumberedString sigFinalize;
  final NumberedString sigExit;
  final NumberedString sigClinit;
  final NumberedString sigInit;
  final NumberedString sigStart;
  final NumberedString sigRun;
  final NumberedString sigObjRun;
  final NumberedString sigForName;

  public EntryPoints(Singletons.Global g) {
    final StringNumberer subSigNumberer = Scene.v().getSubSigNumberer();
    sigMain = subSigNumberer.findOrAdd("void main(java.lang.String[])");
    sigFinalize = subSigNumberer.findOrAdd("void finalize()");
    sigExit = subSigNumberer.findOrAdd("void exit()");
    sigClinit = subSigNumberer.findOrAdd("void <clinit>()");
    sigInit = subSigNumberer.findOrAdd("void <init>()");
    sigStart = subSigNumberer.findOrAdd("void start()");
    sigRun = subSigNumberer.findOrAdd("void run()");
    sigObjRun = subSigNumberer.findOrAdd("java.lang.Object run()");
    sigForName = subSigNumberer.findOrAdd("java.lang.Class forName(java.lang.String)");
  }

  public static EntryPoints v() {
    return G.v().soot_EntryPoints();
  }

  protected void addMethod(List<SootMethod> set, SootClass cls, NumberedString methodSubSig) {
    SootMethod sm = cls.getMethodUnsafe(methodSubSig);
    if (sm != null) {
      set.add(sm);
    }
  }

  protected void addMethod(List<SootMethod> set, String methodSig) {
    final Scene sc = Scene.v();
    if (sc.containsMethod(methodSig)) {
      set.add(sc.getMethod(methodSig));
    }
  }

  /**
   * Returns only the application entry points, not including entry points invoked implicitly by the VM.
   */
  public List<SootMethod> application() {
    List<SootMethod> ret = new ArrayList<SootMethod>();
    final Scene sc = Scene.v();
    if (sc.hasMainClass()) {
      SootClass mainClass = sc.getMainClass();
      addMethod(ret, mainClass, sigMain);
      for (SootMethod clinit : clinitsOf(mainClass)) {
        ret.add(clinit);
      }
    }
    return ret;
  }

  /** Returns only the entry points invoked implicitly by the VM. */
  public List<SootMethod> implicit() {
    List<SootMethod> ret = new ArrayList<SootMethod>();
    addMethod(ret, "<java.lang.System: void initializeSystemClass()>");
    addMethod(ret, "<java.lang.ThreadGroup: void <init>()>");
    // addMethod( ret, "<java.lang.ThreadGroup: void
    // remove(java.lang.Thread)>");
    addMethod(ret, "<java.lang.Thread: void exit()>");
    addMethod(ret, "<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>");
    // addMethod( ret, "<java.lang.System: void
    // loadLibrary(java.lang.String)>");
    addMethod(ret, "<java.lang.ClassLoader: void <init>()>");
    addMethod(ret, "<java.lang.ClassLoader: java.lang.Class loadClassInternal(java.lang.String)>");
    addMethod(ret, "<java.lang.ClassLoader: void checkPackageAccess(java.lang.Class,java.security.ProtectionDomain)>");
    addMethod(ret, "<java.lang.ClassLoader: void addClass(java.lang.Class)>");
    addMethod(ret, "<java.lang.ClassLoader: long findNative(java.lang.ClassLoader,java.lang.String)>");
    addMethod(ret, "<java.security.PrivilegedActionException: void <init>(java.lang.Exception)>");
    // addMethod( ret, "<java.lang.ref.Finalizer: void
    // register(java.lang.Object)>");
    addMethod(ret, "<java.lang.ref.Finalizer: void runFinalizer()>");
    addMethod(ret, "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.Runnable)>");
    addMethod(ret, "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>");
    return ret;
  }

  /** Returns all the entry points. */
  public List<SootMethod> all() {
    List<SootMethod> ret = new ArrayList<SootMethod>();
    ret.addAll(application());
    ret.addAll(implicit());
    return ret;
  }

  /** Returns a list of all static initializers. */
  public List<SootMethod> clinits() {
    List<SootMethod> ret = new ArrayList<SootMethod>();
    for (SootClass cl : Scene.v().getClasses()) {
      addMethod(ret, cl, sigClinit);
    }
    return ret;
  }

  /** Returns a list of all constructors taking no arguments. */
  public List<SootMethod> inits() {
    List<SootMethod> ret = new ArrayList<SootMethod>();
    for (SootClass cl : Scene.v().getClasses()) {
      addMethod(ret, cl, sigInit);
    }
    return ret;
  }

  /** Returns a list of all constructors. */
  public List<SootMethod> allInits() {
    List<SootMethod> ret = new ArrayList<SootMethod>();
    for (SootClass cl : Scene.v().getClasses()) {
      for (SootMethod m : cl.getMethods()) {
        if ("<init>".equals(m.getName())) {
          ret.add(m);
        }
      }
    }
    return ret;
  }

  /** Returns a list of all concrete methods of all application classes. */
  public List<SootMethod> methodsOfApplicationClasses() {
    List<SootMethod> ret = new ArrayList<SootMethod>();
    for (SootClass cl : Scene.v().getApplicationClasses()) {
      for (SootMethod m : cl.getMethods()) {
        if (m.isConcrete()) {
          ret.add(m);
        }
      }
    }
    return ret;
  }

  /**
   * Returns a list of all concrete main(String[]) methods of all application classes.
   */
  public List<SootMethod> mainsOfApplicationClasses() {
    List<SootMethod> ret = new ArrayList<SootMethod>();
    for (SootClass cl : Scene.v().getApplicationClasses()) {
      SootMethod m = cl.getMethodUnsafe("void main(java.lang.String[])");
      if (m != null && m.isConcrete()) {
        ret.add(m);
      }
    }
    return ret;
  }

  /** Returns a list of all clinits of class cl and its superclasses. */
  public Iterable<SootMethod> clinitsOf(SootClass cl) {
    // Do not create an actual list, since this method gets called quite often
    // Instead, callers usually just want to iterate over the result.
    SootMethod init = cl.getMethodUnsafe(sigClinit);
    SootClass superClass  = cl.getSuperclassUnsafe();
    // check super classes until finds a constructor or no super class there anymore.  
    while (init == null && superClass != null) { 
      init = superClass.getMethodUnsafe(sigClinit);
      superClass = superClass.getSuperclassUnsafe();
    }
    if (init == null) {
      return Collections.emptyList();
    }
    SootMethod initStart = init; 
    return new Iterable<SootMethod>() {

      @Override
      public Iterator<SootMethod> iterator() {
        return new Iterator<SootMethod>() {
          SootMethod current = initStart;

          @Override
          public SootMethod next() {
            if (!hasNext()) {
              throw new NoSuchElementException();
            }
            SootMethod n = current;

            // Pre-fetch the next element
            current = null;
            SootClass currentClass = n.getDeclaringClass();
            while (true) {
              SootClass superClass = currentClass.getSuperclassUnsafe();
              if (superClass == null) {
                break;
              }

              SootMethod m = superClass.getMethodUnsafe(sigClinit);
              if (m != null) {
                current = m;
                break;
              }

              currentClass = superClass;
            }

            return n;
          }

          @Override
          public boolean hasNext() {
            return current != null;
          }
        };
      }
    };
  }
}
