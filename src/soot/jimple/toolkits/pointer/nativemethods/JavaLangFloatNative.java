/**
 * Simulates the native method side effects in class java.lang.Float
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangFloatNative extends NativeMethodClass {
    public JavaLangFloatNative( Singletons.Global g ) {}
    public static JavaLangFloatNative v() { return G.v().JavaLangFloatNative(); }

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

  /************************** java.lang.Float ***********************/
  /**
   * Following methods have no side effects.
   *    public static native int floatToIntBits(float);
   *    public static native int floatToRawIntBits(float);
   *    public static native float intBitsToFloat(int);
   */
}
