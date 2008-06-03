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
 * Simulates the native method side effects in class java.lang.reflect.Array
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangReflectArrayNative extends NativeMethodClass {
    public JavaLangReflectArrayNative( NativeHelper helper ) { super(helper); }

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

    if (subSignature.equals("java.lang.Object get(java.lang.Object,int)")) {
      java_lang_reflect_Array_get(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("void set(java.lang.Object,int,java.lang.Object)")) {
      java_lang_reflect_Array_set(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Object newArray(java.lang.Class,int)")){
      java_lang_reflect_Array_newArray(method, thisVar, returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.Object multiNewArray(java.lang.Class,int[])")){
      java_lang_reflect_Array_multiNewArray(method, thisVar, 
					    returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /************ java.lang.reflect.Array **********************/
  /**
   * Returns the value of the indexed component in the specified array
   * object. The value is automatically wrapped in an object if it has
   * a primitive type.
   *
   * NOTE: @return = @param0[]
   *
   * public static native java.lang.Object get(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   */
  public 
    void java_lang_reflect_Array_get(SootMethod method,
				     ReferenceVariable thisVar,
				     ReferenceVariable returnVar,
				     ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }
  
  /**
   * @param0[] = @param1
   *
   * public static native void set(java.lang.Object, int, java.lang.Object) 
   *                         throws java.lang.IllegalArgumentException, 
   *                                java.lang.ArrayIndexOutOfBoundsException;
   */
  public 
    void java_lang_reflect_Array_set(SootMethod method,
				     ReferenceVariable thisVar,
				     ReferenceVariable returnVar,
				     ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }

  /**
   * Treat this method as
   * @return = new A[];
   *
   * private static native java.lang.Object newArray(java.lang.Class, int) 
   *                        throws java.lang.NegativeArraySizeException;
   */
  public 
    void java_lang_reflect_Array_newArray(SootMethod method,
					  ReferenceVariable thisVar,
					  ReferenceVariable returnVar,
					  ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }

  /**
   * Treat this method as
   * @return = new A[][];
   *
   * private static native java.lang.Object multiNewArray(java.lang.Class, 
   *                                                      int[]) 
   *                        throws java.lang.IllegalArgumentException, 
   *                               java.lang.NegativeArraySizeException;
   */
  public 
    void java_lang_reflect_Array_multiNewArray(SootMethod method,
					       ReferenceVariable thisVar,
					       ReferenceVariable returnVar,
					       ReferenceVariable params[]){
    throw new NativeMethodNotSupportedException(method);
  }

  /**
   * Following native methods have no side effects.
   *    
   * public static native int getLength(java.lang.Object) 
   *                     throws java.lang.IllegalArgumentException;
   *
   * public static native boolean getBoolean(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native byte getByte(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   * 
   * public static native char getChar(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *                
   * public static native short getShort(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native int getInt(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native long getLong(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native float getFloat(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   * 		                java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native double getDouble(java.lang.Object, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *           		        java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native void setBoolean(java.lang.Object, int, boolean) 
   *                     throws java.lang.IllegalArgumentException, 
   *	            	        java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native void setByte(java.lang.Object, int, byte) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native void setChar(java.lang.Object, int, char) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native void setShort(java.lang.Object, int, short) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native void setInt(java.lang.Object, int, int) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native void setLong(java.lang.Object, int, long) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native void setFloat(java.lang.Object, int, float) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   *
   * public static native void setDouble(java.lang.Object, int, double) 
   *                     throws java.lang.IllegalArgumentException, 
   *                            java.lang.ArrayIndexOutOfBoundsException;
   * @see default(...)
   */
}
