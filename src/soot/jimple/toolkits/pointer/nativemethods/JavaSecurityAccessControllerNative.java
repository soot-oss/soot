/**
 * Simulates the native method side effects in class java.security.AccessController
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaSecurityAccessControllerNative extends NativeMethodClass {

  private static JavaSecurityAccessControllerNative instance =
    new JavaSecurityAccessControllerNative();

  private JavaSecurityAccessControllerNative(){}

  public static JavaSecurityAccessControllerNative v() { return instance; }

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

    if (subSignature.equals("java.lang.Object doPrivileged(java.security.PrivilegedAction)")){
      java_security_AccessController_doPrivileged(method, thisVar, 
						  returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction)")){
      java_security_AccessController_doPrivileged(method, thisVar, 
						  returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Object doPrivileged(java.security.PrivilegedAction,java.security.AccessControlContext)")){
      java_security_AccessController_doPrivileged(method, thisVar, 
						  returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction,java.security.AccessControlContext)")){
      java_security_AccessController_doPrivileged(method, thisVar, 
						  returnVar, params);
      return;

    } else if (subSignature.equals("java.security.AccessControlContext getStackAccessControlContext()")){
      java_security_AccessController_getStackAccessControlContext(method,
								  thisVar,
								  returnVar,
								  params);
      return;

    } else if (subSignature.equals("java.security.AccessControlContext getInheritedAccessControlContext()")){
      java_security_AccessController_getInheritedAccessControlContext(
					        method,
						thisVar,
						returnVar,
						params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /************************ java.security.AccessController ************/
  /* The return value of doPrivileged depends on the implementation.
   * 
   * public static native 
   *   java.lang.Object doPrivileged(java.security.PrivilegedAction);
   *
   * public static native 
   *   java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction) 
   *                    throws java.security.PrivilegedActionException;
   *
   * public static native 
   *   java.lang.Object doPrivileged(java.security.PrivilegedAction, 
   *                                  java.security.AccessControlContext);
   *
   * public static native 
   *   java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction, 
   *                                 java.security.AccessControlContext) 
   *                    throws java.security.PrivilegedActionException;
   */
  public static
    void java_security_AccessController_doPrivileged(
				 SootMethod method,
				 ReferenceVariable thisVar,
				 ReferenceVariable returnVar,
				 ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.getLeastObject());
  }

  /**
   * Creates an access control context object.
   *
   * private static native 
   *   java.security.AccessControlContext getStackAccessControlContext();
   */
  public static 
    void java_security_AccessController_getStackAccessControlContext(
					    SootMethod method,
					    ReferenceVariable thisVar,
					    ReferenceVariable returnVar,
					    ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.getLeastObject());
      //    throw new NativeMethodNotSupportedException(method);
  }


  /**
   * NOTE: not documented and not called by anyone
   *
   * static native 
   *   java.security.AccessControlContext getInheritedAccessControlContext();
   */
  public static
    void java_security_AccessController_getInheritedAccessControlContext(
					   SootMethod method,
					   ReferenceVariable thisVar,
					   ReferenceVariable returnVar,
					   ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.getLeastObject());
    //    throw new NativeMethodNotSupportedException(method);
  }
}
