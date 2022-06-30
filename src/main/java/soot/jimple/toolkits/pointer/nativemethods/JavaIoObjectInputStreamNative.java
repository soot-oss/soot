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

public class JavaIoObjectInputStreamNative extends NativeMethodClass {
  public JavaIoObjectInputStreamNative(NativeHelper helper) {
    super(helper);
  }

  /**
   * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
   */
  public void simulateMethod(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
      ReferenceVariable params[]) {

    String subSignature = method.getSubSignature();

    if (subSignature.equals("java.lang.ClassLoader latestUserDefinedLoader()")) {
      java_io_ObjectInputStream_latestUserDefinedLoader(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Object allocateNewObject(java.lang.Class,java.lang.Class)")) {
      java_io_ObjectInputStream_allocateNewObject(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Object allocateNewArray(java.lang.Class,int)")) {
      java_io_ObjectInputStream_allocateNewArray(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /*********************** java.io.ObjectInputStream *******************/
  /**
   * NOTE: conservatively returns a reference pointing to the only copy of the class loader.
   *
   * private static native java.lang.ClassLoader latestUserDefinedLoader() throws java.lang.ClassNotFoundException;
   */
  public void java_io_ObjectInputStream_latestUserDefinedLoader(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getClassLoaderObject());
  }

  /**
   * Serialization has to be avoided by static analyses, since each object comes out of the same place.
   *
   * private static native java.lang.Object allocateNewObject(java.lang.Class, java.lang.Class) throws
   * java.lang.InstantiationException, java.lang.IllegalAccessException;
   */
  public void java_io_ObjectInputStream_allocateNewObject(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    throw new NativeMethodNotSupportedException(method);
  }

  /**
   * private static native java.lang.Object allocateNewArray(java.lang.Class, int);
   */
  public void java_io_ObjectInputStream_allocateNewArray(SootMethod method, ReferenceVariable thisVar,
      ReferenceVariable returnVar, ReferenceVariable params[]) {
    throw new NativeMethodNotSupportedException(method);
  }

  /**
   * Following methods have NO side effect, (the last one?????) to be verified with serialization and de-serialization.
   *
   * private static native void bytesToFloats(byte[], int, float[], int, int); private static native void
   * bytesToDoubles(byte[], int, double[], int, int); private static native void setPrimitiveFieldValues(java.lang.Object,
   * long[], char[], byte[]); private static native void setObjectFieldValue(java.lang.Object, long, java.lang.Class,
   * java.lang.Object);
   */

}
