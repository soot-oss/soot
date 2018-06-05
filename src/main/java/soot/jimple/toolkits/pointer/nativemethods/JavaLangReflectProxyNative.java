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
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;
import soot.jimple.toolkits.pointer.util.NativeHelper;

public class JavaLangReflectProxyNative extends NativeMethodClass {
  public JavaLangReflectProxyNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    if (subSignature.equals("java.lang.Class defineClass0(java.lang.ClassLoader,java.lang.String,byte[],int,int)")) {
      java_lang_reflect_Proxy_defineClass0(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /********* java.lang.reflect.Proxy *********************/
  /**
   * We have to assume all possible classes will be returned. But it is still possible to make a new class.
   *
   * NOTE: assuming a close world, and this method should not be called.
   *
   * private static native java.lang.Class defineClass0(java.lang.ClassLoader, java.lang.String, byte[], int, int);
   */
  public void java_lang_reflect_Proxy_defineClass0(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    throw new NativeMethodNotSupportedException(method);
  }
}
