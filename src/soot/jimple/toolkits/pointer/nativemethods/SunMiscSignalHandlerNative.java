/**
 * Simulates the native method side effects in class sun.misc.SignalHandler
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class SunMiscSignalHandlerNative extends NativeMethodClass {

  private static SunMiscSignalHandlerNative instance =
    new SunMiscSignalHandlerNative();

  private SunMiscSignalHandlerNative(){}

  public static SunMiscSignalHandlerNative v() { return instance; }

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
