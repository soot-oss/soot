/**
 * Simulates the native method side effects in class java.util.zip.CRC32
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaUtilZipCRC32Native extends NativeMethodClass {
    public JavaUtilZipCRC32Native( Singletons.Global g ) {}
    public static JavaUtilZipCRC32Native v() { return G.v().JavaUtilZipCRC32Native(); }

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

  /*********************** java.util.zip.CRC32 *********************/
  /**
   * NO side effects.
   *
   *    private static native int update(int, int);
   *    private static native int updateBytes(int, byte[], int, int);
   *
   * @see default(...)
   */

}
