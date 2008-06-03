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
 * Simulates the native method side effects in class java.util.TimeZone
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaUtilTimeZoneNative extends NativeMethodClass {
    public JavaUtilTimeZoneNative( NativeHelper helper ) { super(helper); }

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

    if (subSignature.equals("java.lang.String getSystemTimeZoneID(java.lang.String,java.lang.String)")){
      java_util_TimeZone_getSystemTimeZoneID(method, thisVar, 
					     returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /*********************** java.util.TimeZone **********************/
  /**
   * It should return a constant for TimeZone
   *
   * Gets the TimeZone for the given ID.
   *
   * private static native 
   *   java.lang.String getSystemTimeZoneID(java.lang.String, 
   *                                        java.lang.String);
   */
  public 
    void java_util_TimeZone_getSystemTimeZoneID(SootMethod method,
						ReferenceVariable thisVar,
						ReferenceVariable returnVar,
						ReferenceVariable params[]){
    helper.assignObjectTo(returnVar, Environment.v().getStringObject());
  }
}
