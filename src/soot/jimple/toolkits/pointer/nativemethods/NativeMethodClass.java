/**
 * NativeMethodClass defines side-effect simulation of native methods 
 * in a class. 
 */
package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public abstract class NativeMethodClass {

  private static boolean DEBUG = false;

  /* If a native method has no side effect, call this method.
   * Currently, it does nothing.
   */
  public static void defaultMethod(SootMethod method,
				   ReferenceVariable thisVar,
				   ReferenceVariable returnVar,
				   ReferenceVariable params[]){
    if (DEBUG) {
      G.v().out.println("No side effects : "+method.toString());
    }
  }

  /* To be implemented by individual classes */
  public abstract void simulateMethod(SootMethod method,
				      ReferenceVariable thisVar,
				      ReferenceVariable returnVar,
				      ReferenceVariable params[]);

  
}
