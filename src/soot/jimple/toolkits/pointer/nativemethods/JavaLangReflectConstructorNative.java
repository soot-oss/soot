/**
 * Simulates the native method side effects in class java.lang.reflect.Constructor
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangReflectConstructorNative extends NativeMethodClass {
    public JavaLangReflectConstructorNative( Singletons.Global g ) {}
    public static JavaLangReflectConstructorNative v() { return G.v().JavaLangReflectConstructorNative(); }

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

    if (subSignature.equals("java.lang.Object newInstance(java.lang.Object[])")){
      java_lang_reflect_Constructor_newInstance(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /********************** java.lang.reflect.Constructor ****************/
  /**
   * Uses the constructor represented by this Constructor object to
   * create and initialize a new instance of the constructor's
   * declaring class, with the specified initialization
   * parameters. Individual parameters are automatically unwrapped to
   * match primitive formal parameters, and both primitive and
   * reference parameters are subject to method invocation conversions
   * as necessary. Returns the newly created and initialized object.  
   *
   * NOTE: @return = new Object; but we lose type information.
   *
   * public native java.lang.Object newInstance(java.lang.Object[]) 
   *                throws java.lang.InstantiationException, 
   *                       java.lang.IllegalAccessException, 
   *                       java.lang.IllegalArgumentException, 
   *                       java.lang.reflect.InvocationTargetException;
   */
  public static 
    void java_lang_reflect_Constructor_newInstance(SootMethod method,
						   ReferenceVariable thisVar,
						   ReferenceVariable returnVar,
						   ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }
}
