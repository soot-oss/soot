/**
 * Simulates the native method side effects in class java.io.FileDescriptor
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaIoFileDescriptorNative extends NativeMethodClass {

  private static JavaIoFileDescriptorNative instance =
    new JavaIoFileDescriptorNative();

  private JavaIoFileDescriptorNative(){}

  public static JavaIoFileDescriptorNative v() { return instance; }

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

    /* TODO */
    {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }


}
