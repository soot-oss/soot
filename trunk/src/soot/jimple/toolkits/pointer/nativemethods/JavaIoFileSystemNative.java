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
 * Simulates the native method side effects in class java.io.FileSystem
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaIoFileSystemNative extends NativeMethodClass {
    public JavaIoFileSystemNative( NativeHelper helper ) { super(helper); }


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

    if (subSignature.equals("java.io.FileSystem getFileSystem()")) {
      java_io_FileSystem_getFileSystem(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /************************ java.io.FileSystem ***********************/
  /**
   * Returns a variable pointing to the file system constant
   *
   *    public static native java.io.FileSystem getFileSystem();
   */
  public 
    void java_io_FileSystem_getFileSystem(SootMethod method,
					  ReferenceVariable thisVar,
					  ReferenceVariable returnVar,
					  ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getFileSystemObject());
  }
}
