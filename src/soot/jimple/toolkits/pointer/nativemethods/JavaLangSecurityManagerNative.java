/**
 * Simulates the native method side effects in class java.lang.SecurityManager
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangSecurityManagerNative extends NativeMethodClass {
    public JavaLangSecurityManagerNative( Singletons.Global g ) {}
    public static JavaLangSecurityManagerNative v() { return G.v().JavaLangSecurityManagerNative(); }

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
      java_lang_SecurityManager_getClassContext(method, thisVar, 
						returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.ClassLoader currentClassLoader0()")){
      java_lang_SecurityManager_currentClassLoader0(method,
						    thisVar, 
						    returnVar, 
						    params);
      return;

    } else if (subSignature.equals("java.lang.Class currentLoadedClass0()")){
      java_lang_SecurityManager_currentLoadedClass0(method,
						    thisVar,
						    returnVar,
						    params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /************************* java.lang.SecurityManager ***************/
  /**
   * Returns the current execution stack as an array of classes. 
   *
   * NOTE: an array of object may be created.
   * 
   *     protected native java.lang.Class getClassContext()[];
   */
  public static 
    void java_lang_SecurityManager_getClassContext(SootMethod method,
						   ReferenceVariable thisVar,
						   ReferenceVariable returnVar,
						   ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.getLeastArrayObject());
  }

  /**
   * Returns the class loader of the most recently executing method
   * from a class defined using a non-system class loader. A
   * non-system class loader is defined as being a class loader that
   * is not equal to the system class loader (as returned by
   * ClassLoader.getSystemClassLoader()) or one of its ancestors.
   *
   * NOTE: returns a variable pointing to the only class loader object. 
   *
   *     private native java.lang.ClassLoader currentClassLoader0();
   */
  public static 
    void java_lang_SecurityManager_currentClassLoader0(
			    SootMethod method,
			    ReferenceVariable thisVar,
			    ReferenceVariable returnVar,
			    ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.getClassLoaderObject());
  }

  /**
   * Returns a variable pointing to all class objects.
   * 
   *    private native java.lang.Class currentLoadedClass0();
   */
  public static 
    void java_lang_SecurityManager_currentLoadedClass0(
					 SootMethod method,
					 ReferenceVariable thisVar,
					 ReferenceVariable returnVar,
					 ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.getClassObject());
  }

  /**
   * Both methods have NO side effects.
   *
   *     protected native int classDepth(java.lang.String);
   *     private native int classLoaderDepth0(); 
   */

}
