/**
 * Simulates the native method side effects in class java.io.FileOutputStream
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaIoFileOutputStreamNative extends NativeMethodClass {

  private static JavaIoFileOutputStreamNative instance =
    new JavaIoFileOutputStreamNative();

  private JavaIoFileOutputStreamNative(){}

  public static JavaIoFileOutputStreamNative v() { return instance; }

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
  /*********************** java.io.FileOutputStream *****************/
  /**
   * NO side effects, may throw exceptions.
   *
   *    private native void open(java.lang.String) 
   *                    throws java.io.FileNotFoundException;
   *    private native void openAppend(java.lang.String) 
   *                    throws java.io.FileNotFoundException;
   *    public native void write(int) 
   *                    throws java.io.IOException;
   *    private native void writeBytes(byte[], int, int) 
   *                    throws java.io.IOException;
   *    public native void close() 
   *                    throws java.io.IOException;
   *    private static native void initIDs();
   */

}
