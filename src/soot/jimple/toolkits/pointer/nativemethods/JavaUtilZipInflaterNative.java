/**
 * Simulates the native method side effects in class java.util.zip.Inflater
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaUtilZipInflaterNative extends NativeMethodClass {

  private static JavaUtilZipInflaterNative instance =
    new JavaUtilZipInflaterNative();

  private JavaUtilZipInflaterNative(){}

  public static JavaUtilZipInflaterNative v() { return instance; }

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

  /*********************** java.util.zip.Inflater ******************/
  /**
   * All methods should have no side effects.
   * 
   *    private static native void initIDs();
   *    private static native long init(boolean);
   *    private static native void setDictionary(long, byte[], int, int);
   *    private native int inflateBytes(byte[], int, int) 
   *                     throws java.util.zip.DataFormatException;
   *    private static native int getAdler(long);
   *    private static native int getTotalIn(long);
   *    private static native int getTotalOut(long);
   *    private static native void reset(long);
   *    private static native void end(long);
   * 
   * @see default(...)
   */
}
