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

public class JavaLangThreadNative extends NativeMethodClass {
  public JavaLangThreadNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    if (subSignature.equals("java.lang.Thread currentThread()")) {
      java_lang_Thread_currentThread(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /*************************** java.lang.Thread **********************/
  /**
   * Returns the single variable pointing to all thread objects.
   *
   * This makes our analysis conservative on thread objects.
   *
   * public static native java.lang.Thread currentThread();
   */
  public void java_lang_Thread_currentThread(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getThreadObject());
  }

  /**
   * Following native methods have no side effects.
   *
   * private static native void registerNatives(); public static native void yield(); public static native void sleep(long)
   * throws java.lang.InterruptedException; public native synchronized void start(); private native boolean
   * isInterrupted(boolean); public final native boolean isAlive(); public native int countStackFrames(); private native void
   * setPriority0(int); private native void stop0(java.lang.Object); private native void suspend0(); private native void
   * resume0(); private native void interrupt0();
   */
}
