/**
 * Simulates the native method side effects in class java.util.TimeZone
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaUtilTimeZoneNative extends NativeMethodClass {

  private static JavaUtilTimeZoneNative instance =
    new JavaUtilTimeZoneNative();

  private JavaUtilTimeZoneNative(){}

  public static JavaUtilTimeZoneNative v() { return instance; }

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

    if (subSignature.equals("java.lang.String getSystemTimeZoneID(java.lang.String,java.lang.String)")){
      java_util_TimeZone_getSystemTimeZoneID(method, thisVar, 
					     returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /*********************** java.util.TimeZone **********************/
  /**
   * It should return a constant for TimeZone
   *
   * Gets the TimeZone for the given ID.
   *
   * private static native 
   *   java.lang.String getSystemTimeZoneID(java.lang.String, 
   *                                        java.lang.String);
   */
  public static 
    void java_util_TimeZone_getSystemTimeZoneID(SootMethod method,
						ReferenceVariable thisVar,
						ReferenceVariable returnVar,
						ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.getStringObject());
  }
}
