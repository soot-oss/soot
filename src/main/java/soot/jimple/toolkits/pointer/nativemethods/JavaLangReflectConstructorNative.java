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
 * Simulates the native method side effects in class java.lang.reflect.Constructor
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangReflectConstructorNative extends NativeMethodClass {
    public JavaLangReflectConstructorNative( NativeHelper helper ) { super(helper); }

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

    if (subSignature.equals("java.lang.Object newInstance(java.lang.Object[])")){
      java_lang_reflect_Constructor_newInstance(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /********************** java.lang.reflect.Constructor ****************/
  /**
   * Uses the constructor represented by this Constructor object to
   * create and initialize a new instance of the constructor's
   * declaring class, with the specified initialization
   * parameters. Individual parameters are automatically unwrapped to
   * match primitive formal parameters, and both primitive and
   * reference parameters are subject to method invocation conversions
   * as necessary. Returns the newly created and initialized object.  
   *
   * NOTE: @return = new Object; but we lose type information.
   *
   * public native java.lang.Object newInstance(java.lang.Object[]) 
   *                throws java.lang.InstantiationException, 
   *                       java.lang.IllegalAccessException, 
   *                       java.lang.IllegalArgumentException, 
   *                       java.lang.reflect.InvocationTargetException;
   */
  public 
    void java_lang_reflect_Constructor_newInstance(SootMethod method,
						   ReferenceVariable thisVar,
						   ReferenceVariable returnVar,
						   ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }
}
