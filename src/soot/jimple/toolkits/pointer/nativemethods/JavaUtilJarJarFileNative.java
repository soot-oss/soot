/**
 * Simulates the native method side effects in class java.util.jar.JarFile
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaUtilJarJarFileNative extends NativeMethodClass {

  private static JavaUtilJarJarFileNative instance =
    new JavaUtilJarJarFileNative();

  private JavaUtilJarJarFileNative(){}

  public static JavaUtilJarJarFileNative v() { return instance; }

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

    if (subSignature.equals("java.lang.String[] getMetaInfoEntryNames()")) {
      java_util_jar_JarFile_getMetaInfoEntryNames(method,
						  thisVar,
						  returnVar,
						  params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /*********************** java.util.jar.JarFile ******************/
  /**
   * The methods returns an array of strings.
   * 
   * @return = new String[]
   *
   *     private native java.lang.String getMetaInfEntryNames()[];
   */
  public static 
    void java_util_jar_JarFile_getMetaInfoEntryNames(
				        SootMethod method,
                                        ReferenceVariable thisVar,
					ReferenceVariable returnVar,
					ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.getStringObject());
  }
}
