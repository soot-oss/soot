/**
 * Simulates the native method side effects in class java.lang.Package
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangPackageNative extends NativeMethodClass {

  private static JavaLangPackageNative instance =
    new JavaLangPackageNative();

  private JavaLangPackageNative(){}

  public static JavaLangPackageNative v() { return instance; }

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

    if (subSignature.equals("java.lang.String getSystemPackage0(java.lang.String)")) {
      java_lang_Package_getSystemPackage0(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.String[] getSystemPackages0()")){
      java_lang_Package_getSystemPackages0(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /************************** java.lang.Package *************************/
  /**
   * This is an undocumented private native method, it returns the first
   * (without caller) method's package.
   *
   * It should be formulated as a string constants.
   * private static 
   *   native java.lang.String getSystemPackage0(java.lang.String);
   */
  public static 
    void java_lang_Package_getSystemPackage0(SootMethod method,
					     ReferenceVariable thisVar,
					     ReferenceVariable returnVar,
					     ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.getStringObject());
  }

  /**
   * private static native java.lang.String getSystemPackages0()[];
   */
  public static 
    void java_lang_Package_getSystemPackages0(SootMethod method,
					      ReferenceVariable thisVar,
					      ReferenceVariable returnVar,
					      ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.getLeastArrayObject());
  }


}
