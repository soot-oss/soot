/**
 * Simulates the native method side effects in class java.lang.reflect.Proxy
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangReflectProxyNative extends NativeMethodClass {

  private static JavaLangReflectProxyNative instance =
    new JavaLangReflectProxyNative();

  private JavaLangReflectProxyNative(){}

  public static JavaLangReflectProxyNative v() { return instance; }

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

    if (subSignature.equals("java.lang.Class defineClass0(java.lang.ClassLoader,java.lang.String,byte[],int,int)")){
      java_lang_reflect_Proxy_defineClass0(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /*********  java.lang.reflect.Proxy *********************/
  /**
   * We have to assume all possible classes will be returned.
   * But it is still possible to make a new class.
   *
   * NOTE: assuming a close world, and this method should not
   *       be called.
   *
   * private static native java.lang.Class defineClass0(java.lang.ClassLoader, 
   *                                                    java.lang.String, 
   *                                                    byte[], int, int);
   */
  public static 
    void java_lang_reflect_Proxy_defineClass0(SootMethod method,
					      ReferenceVariable thisVar,
					      ReferenceVariable returnVar,
					      ReferenceVariable params[]) {
    throw new NativeMethodNotSupportedException(method);
  }
}
