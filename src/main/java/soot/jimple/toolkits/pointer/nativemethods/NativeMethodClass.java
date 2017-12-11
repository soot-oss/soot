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
 * NativeMethodClass defines side-effect simulation of native methods 
 * in a class. 
 */
package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public abstract class NativeMethodClass {

  private static final boolean DEBUG = false;
  protected NativeHelper helper;
  public NativeMethodClass(NativeHelper helper) {
      this.helper = helper;
  }

  /* If a native method has no side effect, call this method.
   * Currently, it does nothing.
   */
  public static void defaultMethod(SootMethod method,
				   ReferenceVariable thisVar,
				   ReferenceVariable returnVar,
				   ReferenceVariable params[]){
    if (DEBUG) {
      G.v().out.println("No side effects : "+method.toString());
    }
  }

  /* To be implemented by individual classes */
  public abstract void simulateMethod(SootMethod method,
				      ReferenceVariable thisVar,
				      ReferenceVariable returnVar,
				      ReferenceVariable params[]);

  
}
