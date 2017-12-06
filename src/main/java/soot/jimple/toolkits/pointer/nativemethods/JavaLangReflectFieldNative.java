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
 * Simulates the native method side effects in class java.lang.reflect.Field
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangReflectFieldNative extends NativeMethodClass {
    public JavaLangReflectFieldNative( NativeHelper helper ) { super(helper); }

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

    if (subSignature.equals("void set(java.lang.Object,java.lang.Object)")){
      java_lang_reflect_Field_set(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Object get(java.lang.Object)")){
      java_lang_reflect_Field_get(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /*********************** java.lang.reflect.Field *********************/
  /**
   * NOTE: make all fields pointing to @param1
   *
   *     public native void set(java.lang.Object, java.lang.Object) 
   *                   throws java.lang.IllegalArgumentException, 
   *                          java.lang.IllegalAccessException;
   */
  public 
    void java_lang_reflect_Field_set(SootMethod method,
				     ReferenceVariable thisVar,
				     ReferenceVariable returnVar,
				     ReferenceVariable params[]){
    /* Warn the user that reflection may not be handle correctly. */
    throw new NativeMethodNotSupportedException(method);
  }

  /**
   * Returns the value of the field represented by this Field, on the
   * specified object. The value is automatically wrapped in an object
   * if it has a primitive type.
   *
   * NOTE: this really needs precise info of @this (its name).
   *       conservative way, makes return value possibly point
   *       to universal objects. 
   *
   *  public native java.lang.Object get(java.lang.Object) 
   *                throws java.lang.IllegalArgumentException, 
   *                       java.lang.IllegalAccessException;
   */
  public 
    void java_lang_reflect_Field_get(SootMethod method,
				     ReferenceVariable thisVar,
				     ReferenceVariable returnVar,
				     ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }
  
  /**
   * All other native methods in this class has no side effects.
   *
   * public native boolean getBoolean(java.lang.Object) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native byte getByte(java.lang.Object) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   * 
   * public native char getChar(java.lang.Object) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native short getShort(java.lang.Object) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native int getInt(java.lang.Object) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *     
   * public native long getLong(java.lang.Object) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native float getFloat(java.lang.Object) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native double getDouble(java.lang.Object) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native void setBoolean(java.lang.Object, boolean) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native void setByte(java.lang.Object, byte) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   * 
   * public native void setChar(java.lang.Object, char) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native void setShort(java.lang.Object, short) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *   
   * public native void setInt(java.lang.Object, int) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native void setLong(java.lang.Object, long) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native void setFloat(java.lang.Object, float) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * public native void setDouble(java.lang.Object, double) 
   *                       throws java.lang.IllegalArgumentException, 
   *                              java.lang.IllegalAccessException;
   *
   * @see default(...)
   */
}
