/**
 * Simulates the native method side effects in class java.lang.reflect.Method
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangReflectMethodNative extends NativeMethodClass {

  private static JavaLangReflectMethodNative instance =
    new JavaLangReflectMethodNative();

  private JavaLangReflectMethodNative(){}

  public static JavaLangReflectMethodNative v() { return instance; }

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

    if (subSignature.equals("java.lang.Object invoke(java.lang.Object,java.lang.Object[])")){
      java_lang_reflect_Method_invoke(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /****************** java.lang.reflect.Method *********************/
  /**
   * nvokes the underlying method represented by this Method object,
   * on the specified object with the specified parameters.
   * Individual parameters are automatically unwrapped to match
   * primitive formal parameters, and both primitive and reference
   * parameters are subject to widening conversions as necessary. The
   * value returned by the underlying method is automatically wrapped
   * in an object if it has a primitive type.
   *
   * Method invocation proceeds with the following steps, in order: 
   *
   * If the underlying method is static, then the specified obj
   * argument is ignored. It may be null.  
   *
   * NOTE: @this is an variable pointing to method objects,
   *       @param0 points to receivers
   * 
   *       The possible target of this call is made by 
   *       [thisVar] X [param0]
   * 
   *       Also the parameters are not distinguishable.
   *
   * public native java.lang.Object invoke(java.lang.Object, 
   *                                       java.lang.Object[]) 
   *                    throws java.lang.IllegalAccessException, 
   *                           java.lang.IllegalArgumentException, 
   *                           java.lang.reflect.InvocationTargetException
   */
  public static 
    void java_lang_reflect_Method_invoke(SootMethod method,
					 ReferenceVariable thisVar,
					 ReferenceVariable returnVar,
					 ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }
}
