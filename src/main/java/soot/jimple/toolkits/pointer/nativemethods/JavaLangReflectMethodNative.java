package soot.jimple.toolkits.pointer.nativemethods;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Feng Qian
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import soot.SootMethod;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;
import soot.jimple.toolkits.pointer.util.NativeHelper;

public class JavaLangReflectMethodNative extends NativeMethodClass {
  public JavaLangReflectMethodNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    if (subSignature.equals("java.lang.Object invoke(java.lang.Object,java.lang.Object[])")) {
      java_lang_reflect_Method_invoke(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /****************** java.lang.reflect.Method *********************/
  /**
   * nvokes the underlying method represented by this Method object, on the specified object with the specified parameters.
   * Individual parameters are automatically unwrapped to match primitive formal parameters, and both primitive and reference
   * parameters are subject to widening conversions as necessary. The value returned by the underlying method is
   * automatically wrapped in an object if it has a primitive type.
   *
   * Method invocation proceeds with the following steps, in order:
   *
   * If the underlying method is static, then the specified obj argument is ignored. It may be null.
   *
   * NOTE: @this is an variable pointing to method objects,
   *
   * @param0 points to receivers
   *
   *         The possible target of this call is made by [thisVar] X [param0]
   *
   *         Also the parameters are not distinguishable.
   *
   *         public native java.lang.Object invoke(java.lang.Object, java.lang.Object[]) throws
   *         java.lang.IllegalAccessException, java.lang.IllegalArgumentException,
   *         java.lang.reflect.InvocationTargetException
   */
  public void java_lang_reflect_Method_invoke(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    throw new NativeMethodNotSupportedException(method);
  }
}
