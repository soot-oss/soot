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
    public JavaLangRuntimeNative( Singletons.Global g ) {}
    public static JavaLangRuntimeNative v() { return G.v().JavaLangRuntimeNative(); }

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
  public static 
    void java_lang_Runtime_execInternal(SootMethod method,
					ReferenceVariable thisVar,
					ReferenceVariable returnVar,
					ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.getProcessObject());
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
