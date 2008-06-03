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
 * Simulates the native method side effects in class java.lang.String
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaLangStringNative extends NativeMethodClass {
    public JavaLangStringNative( NativeHelper helper ) { super(helper); }

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

    if (subSignature.equals("java.lang.String intern()")) {
      java_lang_String_intern(method, thisVar, returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }
  /************************** java.lang.String ***********************/
  /**
   * Returns a canonical representation for the string object.  A pool
   * of strings, initially empty, is maintained privately by the class
   * String.
   *
   * When the intern method is invoked, if the pool already contains a
   * * string equal to this String object as determined by the *
   * equals(Object) method, then the string from the pool is *
   * returned. Otherwise, this String object is added to the pool and
   * a * reference to this String object is returned.
   *
   * It follows that for any two strings s and t, 
   *       s.intern() == t.intern() 
   * is true if and only if s.equals(t) is true.
   *
   * All literal strings and string-valued constant expressions are *
   * interned. String literals are defined in Section 3.10.5 of the Java *
   * Language Specification Returns: a string that has the same
   * contents * as this string, but is guaranteed to be from a pool of
   * unique * strings.
   *
   * Side Effect: from the description, we can see, it is tricky to
   *              know the side effect of this native method.
   *              Take a conservative way to handle this.
   *
   *              It may be @return = this;
   *                         pool = this;
   *   
   * why should we care about String in points-to analysis?
   *
   * public native java.lang.String intern();
   */
  public void java_lang_String_intern(SootMethod method,
					     ReferenceVariable thisVar,
					     ReferenceVariable returnVar,
					     ReferenceVariable params[]) {
    helper.assignObjectTo(returnVar, Environment.v().getStringObject());
  }
}
