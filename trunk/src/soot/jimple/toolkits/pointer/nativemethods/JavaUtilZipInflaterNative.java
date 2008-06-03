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
 * Simulates the native method side effects in class java.util.zip.Inflater
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaUtilZipInflaterNative extends NativeMethodClass {
    public JavaUtilZipInflaterNative( NativeHelper helper ) { super(helper); }

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
   *    private static native void initIDs();
   *    private static native long init(boolean);
   *    private static native void setDictionary(long, byte[], int, int);
   *    private native int inflateBytes(byte[], int, int) 
   *                     throws java.util.zip.DataFormatException;
   *    private static native int getAdler(long);
   *    private static native int getTotalIn(long);
   *    private static native int getTotalOut(long);
   *    private static native void reset(long);
   *    private static native void end(long);
   * 
   * @see default(...)
   */
}
