/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Feng Qian
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

/**
 * Simulates the native method side effects in class java.lang.Runtime
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangRuntimeNative extends NativeMethodClass {
    public JavaLangRuntimeNative( NativeHelper helper ) { super(helper); }

  /**
   * Implements the abstract method simulateMethod.
   * It distributes the request to the corresponding methods 
   * by signatures.
   */
  public void simulateMethod(SootMethod method,
			     ReferenceVariable thisVar,
			     ReferenceVariable returnVar,
			     ReferenceVariable params[]){

    String subSignature = method.getSubSignature();

    if (subSignature.equals("java.lang.Process execInternal(java.lang.String[],java.lang.String[],java.lang.String)")) {
      java_lang_Runtime_execInternal(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;
    }
  }

  /************************ java.lang.Runtime *********************/
  /**
   * execInternal is called by all exec method.
   * It return a Process object.
   *
   * NOTE: creates a Process object.
   *
   * private native 
   *   java.lang.Process execInternal(java.lang.String[], 
   *                                  java.lang.String[], 
   *                                  java.lang.String) 
   *                          throws java.io.IOException;
   */
  public 
    void java_lang_Runtime_execInternal(SootMethod method,
					ReferenceVariable thisVar,
					ReferenceVariable returnVar,
					ReferenceVariable params[]){
    helper.assignObjectTo(returnVar, Environment.v().getProcessObject());
  }

  /**
   * Following methods have NO side effects.
   *
   *    public native long freeMemory();
   *    public native long totalMemory();
   *    public native void gc();
   *    private static native void runFinalization0();
   *    public native void traceInstructions(boolean);
   *    public native void traceMethodCalls(boolean);
   */
}
