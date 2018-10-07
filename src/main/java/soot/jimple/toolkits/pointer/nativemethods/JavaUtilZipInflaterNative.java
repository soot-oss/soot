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

public class JavaUtilZipInflaterNative extends NativeMethodClass {
  public JavaUtilZipInflaterNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    /* TODO */
    {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /*********************** java.util.zip.Inflater ******************/
  /**
   * All methods should have no side effects.
   *
   * private static native void initIDs(); private static native long init(boolean); private static native void
   * setDictionary(long, byte[], int, int); private native int inflateBytes(byte[], int, int) throws
   * java.util.zip.DataFormatException; private static native int getAdler(long); private static native int getTotalIn(long);
   * private static native int getTotalOut(long); private static native void reset(long); private static native void
   * end(long);
   *
   * @see default(...)
   */
}
