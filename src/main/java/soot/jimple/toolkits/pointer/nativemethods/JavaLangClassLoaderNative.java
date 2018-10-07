package soot.jimple.toolkits.pointer.nativemethods;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Feng Qian
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

import soot.SootMethod;
import soot.jimple.toolkits.pointer.representations.Environment;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;
import soot.jimple.toolkits.pointer.util.NativeHelper;

public class JavaLangClassLoaderNative extends NativeMethodClass {
  public JavaLangClassLoaderNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    if (subSignature
        .equals("java.lang.Class defineClass0(java.lang.String,byte[],int,int,java.lang.security.ProtectionDomain)")) {
      java_lang_ClassLoader_defineClass0(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Class findBootstrapClass(java.lang.String)")) {
      java_lang_ClassLoader_findBootstrapClass(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Class findLoadedClass(java.lang.String)")) {
      java_lang_ClassLoader_findLoadedClass(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.ClassLoader getCallerClassLoader()")) {
      java_lang_ClassLoader_getCallerClassLoader(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /************************** java.lang.ClassLoader ******************/
  /**
   * Converts an array of bytes into an instance of class Class. Before the Class can be used it must be resolved.
   *
   * NOTE: an object representing an class object. To be conservative, the side-effect of this method will return an abstract
   * reference points to all possible class object in current analysis environment.
   *
   * private native java.lang.Class defineClass0(java.lang.String, byte[], int, int, java.security.ProtectionDomain);
   */
  public void java_lang_ClassLoader_defineClass0(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getClassObject());
  }

  /**
   * NOTE: undocumented, finding the bootstrap class
   *
   * Assuming all classes
   *
   * private native java.lang.Class findBootstrapClass(java.lang.String) throws java.lang.ClassNotFoundException;
   */
  public void java_lang_ClassLoader_findBootstrapClass(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getClassObject());
  }

  /**
   * Finds the class with the given name if it had been previously loaded through this class loader.
   *
   * NOTE: assuming all classes.
   *
   * protected final native java.lang.Class findLoadedClass(java.lang.String);
   */
  public void java_lang_ClassLoader_findLoadedClass(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getClassObject());
  }

  /**
   * Returns a variable pointing to the only class loader
   *
   * static native java.lang.ClassLoader getCallerClassLoader();
   */
  public void java_lang_ClassLoader_getCallerClassLoader(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getClassLoaderObject());
  }

  /**
   * NO side effects.
   *
   * Assuming that resolving a class has not effect on the class load and class object
   *
   * private native void resolveClass0(java.lang.Class);
   */
}
