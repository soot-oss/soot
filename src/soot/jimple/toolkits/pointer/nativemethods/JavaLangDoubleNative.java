/**
 * Simulates the native method side effects in class java.lang.Double
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangDoubleNative extends NativeMethodClass {

  private static JavaLangDoubleNative instance =
    new JavaLangDoubleNative();

  private JavaLangDoubleNative(){}

  public static JavaLangDoubleNative v() { return instance; }

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

  /************************** java.lang.Double **********************/
  /**
   * Following methods have no side effects.
   *    public static native long doubleToLongBits(double);
   *    public static native long doubleToRawLongBits(double);
   *    public static native double longBitsToDouble(long);
   */
}
