/**
 * Simulates the native method side effects in class java.util.ResourceBundle
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaUtilResourceBundleNative extends NativeMethodClass {
    public JavaUtilResourceBundleNative( Singletons.Global g ) {}
    public static JavaUtilResourceBundleNative v() { return G.v().JavaUtilResourceBundleNative(); }

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

    if (subSignature.equals("java.lang.Class[] getClassContext()")) {
      java_util_ResourceBundle_getClassContext(method, 
					       thisVar, 
					       returnVar, 
					       params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /********** java.util.ResourceBundle ******************/
  /**
   * Undocumented, returns an array of all possible classes.
   * NOTE: @return = new Class[];
   *       @return[] = { all classes }
   *
   *     private static native java.lang.Class getClassContext()[];
   */
  public static 
    void java_util_ResourceBundle_getClassContext(SootMethod method,
						  ReferenceVariable thisVar,
						  ReferenceVariable returnVar,
						  ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }
}
