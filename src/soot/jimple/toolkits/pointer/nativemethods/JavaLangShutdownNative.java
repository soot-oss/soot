/**
 * Simulates the native method side effects in class java.lang.Shutdown
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangShutdownNative extends NativeMethodClass {
    public JavaLangShutdownNative( Singletons.Global g ) {}
    public static JavaLangShutdownNative v() { return G.v().JavaLangShutdownNative(); }

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

    {
      defaultMethod(method, thisVar, returnVar, params);
      return;
    }
  }

  /************************** java.lang.Shutdown *********************/
  /**
   * Both methods has NO side effects.
   *
   *    static native void halt(int);
   *    private static native void runAllFinalizers();
   */

}
