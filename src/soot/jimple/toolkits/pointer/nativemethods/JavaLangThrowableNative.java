/**
 * Simulates the native method side effects in class java.lang.Throwable
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangThrowableNative extends NativeMethodClass {

  private static JavaLangThrowableNative instance =
    new JavaLangThrowableNative();

  private JavaLangThrowableNative(){}

  public static JavaLangThrowableNative v() { return instance; }

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

    if (subSignature.equals("java.lang.Throwable fillInStackTrace()")) {
      java_lang_Throwable_fillInStackTrace(method, thisVar, 
					   returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /************************** java.lang.Throwable *******************/
  /**
   * NOTE: this method just fills in the stack state in this throwable 
   *       object content.
   *
   * public native java.lang.Throwable fillInStackTrace();
   */
  public static 
    void java_lang_Throwable_fillInStackTrace(SootMethod method,
					      ReferenceVariable thisVar,
					      ReferenceVariable returnVar,
					      ReferenceVariable params[]) {
    NativeHelper.assign(returnVar, thisVar);
  }

  /**
   * NO side effects.
   * 
   * private native void printStackTrace0(java.lang.Object);
   */

}
