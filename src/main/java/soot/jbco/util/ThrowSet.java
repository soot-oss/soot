package soot.jbco.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

/**
 * @author Michael Batchelder
 * 
 *         Created on 20-Jun-2006
 */
public class ThrowSet {

  private static SootClass throwable[] = null;

  public static SootClass getRandomThrowable() {
    if (throwable == null) {
      initThrowables();
    }

    return throwable[Rand.getInt(throwable.length)];
  }

  private static void initThrowables() {
    Scene sc = Scene.v();

    throwable = new SootClass[10];
    throwable[0] = sc.getRefType("java.lang.RuntimeException").getSootClass();
    throwable[1] = sc.getRefType("java.lang.ArithmeticException").getSootClass();
    throwable[2] = sc.getRefType("java.lang.ArrayStoreException").getSootClass();
    throwable[3] = sc.getRefType("java.lang.ClassCastException").getSootClass();
    throwable[4] = sc.getRefType("java.lang.IllegalMonitorStateException").getSootClass();
    throwable[5] = sc.getRefType("java.lang.IndexOutOfBoundsException").getSootClass();
    throwable[6] = sc.getRefType("java.lang.ArrayIndexOutOfBoundsException").getSootClass();
    throwable[7] = sc.getRefType("java.lang.NegativeArraySizeException").getSootClass();
    throwable[8] = sc.getRefType("java.lang.NullPointerException").getSootClass();
    throwable[9] = sc.getRefType("java.lang.Throwable").getSootClass();
  }
}
