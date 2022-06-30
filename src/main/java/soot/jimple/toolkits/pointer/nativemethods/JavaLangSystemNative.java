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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.SootMethod;
import soot.jimple.toolkits.pointer.representations.Environment;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;
import soot.jimple.toolkits.pointer.util.NativeHelper;

public class JavaLangSystemNative extends NativeMethodClass {
  private static final Logger logger = LoggerFactory.getLogger(JavaLangSystemNative.class);

  public JavaLangSystemNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    if (subSignature.equals("void arraycopy(java.lang.Object,int,java.lang.Object,int,int)")) {
      java_lang_System_arraycopy(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("void setIn0(java.io.InputStream)")) {
      java_lang_System_setIn0(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("void setOut0(java.io.PrintStream)")) {
      java_lang_System_setOut0(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("void setErr0(java.io.PrintStream)")) {
      java_lang_System_setErr0(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.util.Properties initProperties(java.util.Properties)")) {
      java_lang_System_initProperties(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.String mapLibraryName(java.lang.String)")) {
      java_lang_System_mapLibraryName(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Class getCallerClass()")) {
      java_lang_System_getCallerClass(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;
    }
  }

  /********************* java.lang.System *********************/
  /**
   * Copies an array from the specified source array, beginning at the specified position, to the specified position of the
   * destination array.
   *
   * NOTE: If the content of array is reference type, then it is necessary to build a connection between elements of two
   * arrays
   *
   * dst[] = src[]
   *
   * public static native void arraycopy(java.lang.Object, int, java.lang.Object, int, int);
   */
  public void java_lang_System_arraycopy(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    ReferenceVariable srcElm = helper.arrayElementOf(params[0]);
    ReferenceVariable dstElm = helper.arrayElementOf(params[2]);
    // never make a[] = b[], it violates the principle of jimple statement.
    // make a temporary variable.
    ReferenceVariable tmpVar = helper.tempLocalVariable(method);
    helper.assign(tmpVar, srcElm);
    helper.assign(dstElm, tmpVar);
  }

  /**
   * NOTE: this native method is not documented in JDK API. It should have the side effect: System.in = parameter
   *
   * private static native void setIn0(java.io.InputStream);
   */
  public void java_lang_System_setIn0(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    ReferenceVariable sysIn = helper.staticField("java.lang.System", "in");
    helper.assign(sysIn, params[0]);
  }

  /**
   * NOTE: the same explanation as setIn0: G.v().out = parameter
   *
   * private static native void setOut0(java.io.PrintStream);
   */
  public void java_lang_System_setOut0(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    ReferenceVariable sysOut = helper.staticField("java.lang.System", "out");
    helper.assign(sysOut, params[0]);
  }

  /**
   * NOTE: the same explanation as setIn0: System.err = parameter
   *
   * private static native void setErr0(java.io.PrintStream);
   */
  public void java_lang_System_setErr0(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    ReferenceVariable sysErr = helper.staticField("java.lang.System", "err");
    helper.assign(sysErr, params[0]);
  }

  /**
   * NOTE: this method is not documented, it should do following:
   *
   * @return = System.props; System.props = parameter;
   *
   *         private static native java.util.Properties initProperties(java.util.Properties);
   */
  public void java_lang_System_initProperties(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    ReferenceVariable sysProps = helper.staticField("java.lang.System", "props");
    helper.assign(returnVar, sysProps);
    helper.assign(sysProps, params[0]);
  }

  /**
   * NOTE: it is platform-dependent, create a new string, needs to be verified.
   *
   * public static native java.lang.String mapLibraryName(java.lang.String);
   */
  public void java_lang_System_mapLibraryName(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getStringObject());
  }

  /**
   * Undocumented, used by class loading.
   *
   * static native java.lang.Class getCallerClass();
   */
  public void java_lang_System_getCallerClass(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getClassObject());
  }

  /**
   * Following methods have NO side effects.
   *
   * private static native void registerNatives(); public static native long currentTimeMillis(); public static native int
   * identityHashCode(java.lang.Object);
   */

}
