/**
 * Simulates the native method side effects in class java.io.FileInputStream
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaIoFileInputStreamNative extends NativeMethodClass {

  private static JavaIoFileInputStreamNative instance =
    new JavaIoFileInputStreamNative();

  private JavaIoFileInputStreamNative(){}

  public static JavaIoFileInputStreamNative v() { return instance; }

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
  /************************ java.io.FileInputStream *****************/
  /**
   * Following methods have NO side effects.
   *
   *    private native void open(java.lang.String) 
   *                  throws java.io.FileNotFoundException;
   *    public native int read() throws java.io.IOException;
   *    private native int readBytes(byte[], int, int) 
   *                        throws java.io.IOException;
   *    public native int available() throws java.io.IOException;
   *    public native void close() throws java.io.IOException;
   *    private static native void initIDs();
   */
}
