/**
 * Simulates the native method side effects in class java.lang.StrictMath
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangStrictMathNative extends NativeMethodClass {

  private static JavaLangStrictMathNative instance =
    new JavaLangStrictMathNative();

  private JavaLangStrictMathNative(){}

  public static JavaLangStrictMathNative v() { return instance; }

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
  /************************* java.lang.StrictMath *******************/
  /**
   * Methods have no side effects.
   *
   *    public static native strictfp double sin(double);
   *    public static native strictfp double cos(double);
   *    public static native strictfp double tan(double);
   *    public static native strictfp double asin(double);
   *    public static native strictfp double acos(double);
   *    public static native strictfp double atan(double);
   *    public static native strictfp double exp(double);
   *    public static native strictfp double log(double);
   *    public static native strictfp double sqrt(double);
   *    public static native strictfp double IEEEremainder(double, double);
   *    public static native strictfp double ceil(double);
   *    public static native strictfp double floor(double);
   *    public static native strictfp double rint(double);
   *    public static native strictfp double atan2(double, double);
   *    public static native strictfp double pow(double, double);
   */
}
