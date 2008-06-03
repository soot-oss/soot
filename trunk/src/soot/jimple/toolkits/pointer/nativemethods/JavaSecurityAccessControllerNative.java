/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Feng Qian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

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
    public JavaSecurityAccessControllerNative( NativeHelper helper ) { super(helper); }

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
  public 
    void java_security_AccessController_doPrivileged(
				 SootMethod method,
				 ReferenceVariable thisVar,
				 ReferenceVariable returnVar,
				 ReferenceVariable params[]){
    // No longer necessary since Spark handles it itself in a more precise
    // way.
    //helper.assignObjectTo(returnVar, Environment.v().getLeastObject());
    helper.throwException(Environment.v().getPrivilegedActionExceptionObject());
  }

  /**
   * Creates an access control context object.
   *
   * private static native 
   *   java.security.AccessControlContext getStackAccessControlContext();
   */
  public 
    void java_security_AccessController_getStackAccessControlContext(
					    SootMethod method,
					    ReferenceVariable thisVar,
					    ReferenceVariable returnVar,
					    ReferenceVariable params[]){
    helper.assignObjectTo(returnVar, Environment.v().getLeastObject());
  }


  /**
   * NOTE: not documented and not called by anyone
   *
   * static native 
   *   java.security.AccessControlContext getInheritedAccessControlContext();
   */
  public 
    void java_security_AccessController_getInheritedAccessControlContext(
					   SootMethod method,
					   ReferenceVariable thisVar,
					   ReferenceVariable returnVar,
					   ReferenceVariable params[]){
    helper.assignObjectTo(returnVar, Environment.v().getLeastObject());
  }
}
