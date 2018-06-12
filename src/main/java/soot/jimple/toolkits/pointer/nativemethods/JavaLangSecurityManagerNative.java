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

public class JavaLangSecurityManagerNative extends NativeMethodClass {
  public JavaLangSecurityManagerNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    if (subSignature.equals("java.lang.Class[] getClassContext()")) {
      java_lang_SecurityManager_getClassContext(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.ClassLoader currentClassLoader0()")) {
      java_lang_SecurityManager_currentClassLoader0(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Class currentLoadedClass0()")) {
      java_lang_SecurityManager_currentLoadedClass0(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /************************* java.lang.SecurityManager ***************/
  /**
   * Returns the current execution stack as an array of classes.
   *
   * NOTE: an array of object may be created.
   *
   * protected native java.lang.Class getClassContext()[];
   */
  public void java_lang_SecurityManager_getClassContext(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getLeastArrayObject());
  }

  /**
   * Returns the class loader of the most recently executing method from a class defined using a non-system class loader. A
   * non-system class loader is defined as being a class loader that is not equal to the system class loader (as returned by
   * ClassLoader.getSystemClassLoader()) or one of its ancestors.
   *
   * NOTE: returns a variable pointing to the only class loader object.
   *
   * private native java.lang.ClassLoader currentClassLoader0();
   */
  public void java_lang_SecurityManager_currentClassLoader0(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getClassLoaderObject());
  }

  /**
   * Returns a variable pointing to all class objects.
   *
   * private native java.lang.Class currentLoadedClass0();
   */
  public void java_lang_SecurityManager_currentLoadedClass0(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getClassObject());
  }

  /**
   * Both methods have NO side effects.
   *
   * protected native int classDepth(java.lang.String); private native int classLoaderDepth0();
   */

}
