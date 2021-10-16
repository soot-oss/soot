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
import soot.jimple.toolkits.pointer.representations.Environment;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;
import soot.jimple.toolkits.pointer.util.NativeHelper;

public class JavaLangPackageNative extends NativeMethodClass {
  public JavaLangPackageNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    if (subSignature.equals("java.lang.String getSystemPackage0(java.lang.String)")) {
      java_lang_Package_getSystemPackage0(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.String[] getSystemPackages0()")) {
      java_lang_Package_getSystemPackages0(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /************************** java.lang.Package *************************/
  /**
   * This is an undocumented private native method, it returns the first (without caller) method's package.
   *
   * It should be formulated as a string constants. private static native java.lang.String
   * getSystemPackage0(java.lang.String);
   */
  public void java_lang_Package_getSystemPackage0(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getStringObject());
  }

  /**
   * private static native java.lang.String getSystemPackages0()[];
   */
  public void java_lang_Package_getSystemPackages0(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getStringObject());
  }

}
