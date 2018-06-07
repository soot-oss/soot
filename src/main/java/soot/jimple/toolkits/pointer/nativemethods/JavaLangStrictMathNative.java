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

public class JavaLangStrictMathNative extends NativeMethodClass {
  public JavaLangStrictMathNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /************************* java.lang.StrictMath *******************/
  /**
   * Methods have no side effects.
   *
   * public static native strictfp double sin(double); public static native strictfp double cos(double); public static native
   * strictfp double tan(double); public static native strictfp double asin(double); public static native strictfp double
   * acos(double); public static native strictfp double atan(double); public static native strictfp double exp(double);
   * public static native strictfp double log(double); public static native strictfp double sqrt(double); public static
   * native strictfp double IEEEremainder(double, double); public static native strictfp double ceil(double); public static
   * native strictfp double floor(double); public static native strictfp double rint(double); public static native strictfp
   * double atan2(double, double); public static native strictfp double pow(double, double);
   */
}
