/**
 * Simulates the native method side effects in class java.lang.ClassLoader$NativeLibrary
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangClassLoaderNativeLibraryNative extends NativeMethodClass {

  private static JavaLangClassLoaderNativeLibraryNative instance =
    new JavaLangClassLoaderNativeLibraryNative();

  private JavaLangClassLoaderNativeLibraryNative(){}

  public static JavaLangClassLoaderNativeLibraryNative v() { return instance; }

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
  /************** java.lang.ClassLoader$NativeLibrary ****************/
  /**
   * NO side effects
   *
   *        native void load(java.lang.String);
   *        native long find(java.lang.String);
   *        native void unload();
   */

}
