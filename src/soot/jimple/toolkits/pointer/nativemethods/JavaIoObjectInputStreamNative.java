/**
 * Simulates the native method side effects in class java.io.ObjectInputStream
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaIoObjectInputStreamNative extends NativeMethodClass {

  private static JavaIoObjectInputStreamNative instance =
    new JavaIoObjectInputStreamNative();

  private JavaIoObjectInputStreamNative(){}

  public static JavaIoObjectInputStreamNative v() { return instance; }

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

    if (subSignature.equals("java.lang.ClassLoader latestUserDefinedLoader()")){
      java_io_ObjectInputStream_latestUserDefinedLoader(method,
							thisVar,
							returnVar,
							params);
      return;

    } else if (subSignature.equals("java.lang.Object allocateNewObject(java.lang.Class,java.lang.Class)")) {
      java_io_ObjectInputStream_allocateNewObject(method,
						  thisVar,
						  returnVar,
						  params);
      return;

    } else if (subSignature.equals("java.lang.Object allocateNewArray(java.lang.Class,int)")){
      java_io_ObjectInputStream_allocateNewArray(method,
						 thisVar,
						 returnVar,
						 params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /*********************** java.io.ObjectInputStream *******************/
  /**
   * NOTE: conservatively returns a reference pointing to the only copy 
   * of the class loader.
   *
   * private static native java.lang.ClassLoader latestUserDefinedLoader() 
   *                        throws java.lang.ClassNotFoundException;
   */
  public static 
    void java_io_ObjectInputStream_latestUserDefinedLoader(
				        SootMethod method,
					ReferenceVariable thisVar,
					ReferenceVariable returnVar,
					ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.getClassLoaderObject());
  }

  /**
   * Serialization has to be avoided by static analyses, since each
   * object comes out of the same place.
   *
   * private static native java.lang.Object allocateNewObject(java.lang.Class,
   *                                                          java.lang.Class)
   *                             throws java.lang.InstantiationException, 
   *                             java.lang.IllegalAccessException;
   */
  public static 
    void java_io_ObjectInputStream_allocateNewObject(
						 SootMethod method,
                                                 ReferenceVariable thisVar,
						 ReferenceVariable returnVar,
						 ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }

  /**
   * private static native java.lang.Object allocateNewArray(java.lang.Class, 
   *                                                         int);
   */
  public static 
    void java_io_ObjectInputStream_allocateNewArray(
						 SootMethod method,
						 ReferenceVariable thisVar,
						 ReferenceVariable returnVar,
						 ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }

  /**
   * Following methods have NO side effect, (the last one?????)
   * to be verified with serialization and de-serialization.
   * 
   * private static native void bytesToFloats(byte[], int, float[], int, int);
   * private static native void bytesToDoubles(byte[], int, 
   *                                           double[], int, int);
   * private static native void setPrimitiveFieldValues(java.lang.Object, 
   *                                                    long[], 
   *                                                    char[], 
   *                                                    byte[]);
   * private static native void setObjectFieldValue(java.lang.Object, 
   *                                                long, 
   *                                                java.lang.Class, 
   *                                                java.lang.Object);
   */

}
