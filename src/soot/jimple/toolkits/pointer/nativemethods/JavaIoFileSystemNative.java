/**
 * Simulates the native method side effects in class java.io.FileSystem
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaIoFileSystemNative extends NativeMethodClass {

  private static JavaIoFileSystemNative instance =
    new JavaIoFileSystemNative();

  private JavaIoFileSystemNative(){}

  public static JavaIoFileSystemNative v() { return instance; }

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

    if (subSignature.equals("java.io.FileSystem getFileSystem()")) {
      java_io_FileSystem_getFileSystem(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /************************ java.io.FileSystem ***********************/
  /**
   * Returns a variable pointing to the file system constant
   *
   *    public static native java.io.FileSystem getFileSystem();
   */
  public static 
    void java_io_FileSystem_getFileSystem(SootMethod method,
					  ReferenceVariable thisVar,
					  ReferenceVariable returnVar,
					  ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.getFileSystemObject());
  }
}
