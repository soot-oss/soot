/**
 * Simulates the native method side effects in class java.util.zip.ZipEntry
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaUtilZipZipEntryNative extends NativeMethodClass {

  private static JavaUtilZipZipEntryNative instance =
    new JavaUtilZipZipEntryNative();

  private JavaUtilZipZipEntryNative(){}

  public static JavaUtilZipZipEntryNative v() { return instance; }

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
}
