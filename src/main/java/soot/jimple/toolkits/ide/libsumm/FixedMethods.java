package soot.jimple.toolkits.ide.libsumm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;

/**
 * Determines whether, according to the type hierarchy, a method call is fixed, i.e., cannot be overwritten by client code.
 */
// TODO implement caching for class and method info
public class FixedMethods {

  public final static boolean ASSUME_PACKAGES_SEALED = false;

  /**
   * Returns true if a method call is fixed, i.e., assuming that all classes in the Scene resemble library code, then client
   * code cannot possible overwrite the called method. This is trivially true for InvokeStatic and InvokeSpecial, but can
   * also hold for virtual invokes if all possible call targets in the library cannot be overwritten.
   *
   * @see #clientOverwriteableOverwrites(SootMethod)
   */
  public static boolean isFixed(InvokeExpr ie) {
    return ie instanceof StaticInvokeExpr || ie instanceof SpecialInvokeExpr
        || !clientOverwriteableOverwrites(ie.getMethod());
  }

  /**
   * Returns true if this method itself is visible to the client and overwriteable or if the same holds for any of the
   * methods in the library that overwrite the argument method.
   *
   * @see #clientOverwriteable(SootMethod)
   */
  private static boolean clientOverwriteableOverwrites(SootMethod m) {
    if (clientOverwriteable(m)) {
      return true;
    }

    SootClass c = m.getDeclaringClass();
    // TODO could use PTA and call graph to filter subclasses further
    for (SootClass cPrime : Scene.v().getFastHierarchy().getSubclassesOf(c)) {
      SootMethod mPrime = cPrime.getMethodUnsafe(m.getSubSignature());
      if (mPrime != null) {
        if (clientOverwriteable(mPrime)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns true if the given method itself is visible to the client and overwriteable. This is true if neither the method
   * nor its declaring class are final, if the method is visible and if the declaring class can be instantiated.
   *
   * @see #visible(SootMethod)
   * @see #clientCanInstantiate(SootClass)
   */
  private static boolean clientOverwriteable(SootMethod m) {
    SootClass c = m.getDeclaringClass();
    if (!c.isFinal() && !m.isFinal() && visible(m) && clientCanInstantiate(c)) {
      return true;
    }
    return false;
  }

  /**
   * Returns true if clients can instantiate the given class. This holds if the given class is actually an interface, or if
   * it contains a visible constructor. If the class is an inner class, then the enclosing classes must be instantiable as
   * well.
   */
  private static boolean clientCanInstantiate(SootClass cPrime) {
    // subtypes of interface types can always be instantiated
    if (cPrime.isInterface()) {
      return true;
    }

    for (SootMethod m : cPrime.getMethods()) {
      if (m.getName().equals(SootMethod.constructorName)) {
        if (visible(m)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns true if the given method is visible to client code.
   */
  private static boolean visible(SootMethod mPrime) {
    SootClass cPrime = mPrime.getDeclaringClass();
    return (cPrime.isPublic() || cPrime.isProtected() || (!cPrime.isPrivate() && !ASSUME_PACKAGES_SEALED))
        && (mPrime.isPublic() || mPrime.isProtected() || (!mPrime.isPrivate() && !ASSUME_PACKAGES_SEALED));
  }
}
