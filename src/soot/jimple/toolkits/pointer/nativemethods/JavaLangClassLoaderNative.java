/**
 * Simulates the native method side effects in class java.lang.ClassLoader
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangClassLoaderNative extends NativeMethodClass {
    public JavaLangClassLoaderNative( Singletons.Global g ) {}
    public static JavaLangClassLoaderNative v() { return G.v().JavaLangClassLoaderNative(); }

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

    if (subSignature.equals("java.lang.Class defineClass0(java.lang.String,byte[],int,int,java.lang.security.ProtectionDomain)")){
      java_lang_ClassLoader_defineClass0(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Class findBootstrapClass(java.lang.String)")){
      java_lang_ClassLoader_findBootstrapClass(method, thisVar, 
					       returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Class findLoadedClass(java.lang.String)")){
      java_lang_ClassLoader_findLoadedClass(method, thisVar, 
					    returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.ClassLoader getCallerClassLoader()")){
      java_lang_ClassLoader_getCallerClassLoader(method, thisVar, 
						 returnVar, params);
      return;
      
    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /************************** java.lang.ClassLoader ******************/  
  /**
   * Converts an array of bytes into an instance of class
   * Class. Before the Class can be used it must be resolved.
   *
   * NOTE: an object representing an class object.
   *       To be conservative, the side-effect of this method will
   *       return an abstract reference points to all possible class object
   *       in current analysis environment.
   *
   * private native 
   *   java.lang.Class defineClass0(java.lang.String, 
   *                                byte[], 
   *                                int, 
   *                                int, 
   *                                java.security.ProtectionDomain);
   */
  public static 
    void java_lang_ClassLoader_defineClass0(SootMethod method,
					    ReferenceVariable thisVar,
					    ReferenceVariable returnVar,
					    ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.v().getClassObject());
  }

  /**
   * NOTE: undocumented, finding the bootstrap class
   * 
   * Assuming all classes
   *
   * private native 
   *   java.lang.Class findBootstrapClass(java.lang.String) 
   *                   throws java.lang.ClassNotFoundException;
   */
  public static 
    void java_lang_ClassLoader_findBootstrapClass(
					 SootMethod method,
                                         ReferenceVariable thisVar,
					 ReferenceVariable returnVar,
					 ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.v().getClassObject());
  }

  /**
   * Finds the class with the given name if it had been previously
   * loaded through this class loader.
   * 
   * NOTE: assuming all classes.
   *
   * protected final native java.lang.Class findLoadedClass(java.lang.String);
   */
  public static 
    void java_lang_ClassLoader_findLoadedClass(SootMethod method,
					       ReferenceVariable thisVar,
					       ReferenceVariable returnVar,
					       ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.v().getClassObject());
  }

  /**
   * Returns a variable pointing to the only class loader
   *
   * static native java.lang.ClassLoader getCallerClassLoader();
   */
  public static 
    void java_lang_ClassLoader_getCallerClassLoader(
				    SootMethod method,
				    ReferenceVariable thisVar,
				    ReferenceVariable returnVar,
				    ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.v().getClassLoaderObject());
  }

  /**
   * NO side effects.
   *
   * Assuming that resolving a class has not effect on the class load
   * and class object
   *
   *    private native void resolveClass0(java.lang.Class);
   */
}
