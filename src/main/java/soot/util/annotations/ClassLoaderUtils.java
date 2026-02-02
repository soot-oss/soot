package soot.util.annotations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 * Copyright (C) 2004 Ondrej Lhotak
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

import java.lang.reflect.Array;

/**
 * Loads classes without relying on JBoss.
 * 
 * A general note on dynamically loading classes based on information from target programs: You don't want that. It's a
 * horrible idea, can lead to severe security vulnerabilities, and is bad style. Trust me. But people seem to need it, so
 * this class makes it at least slightly less horrible than the old way. It's still insane, but now it's insanity with style.
 * Somehow.
 * 
 * @author Steven Arzt
 *
 */
public class ClassLoaderUtils {

  /**
   * Don't call me. Just don't.
   * 
   * @param className
   * @return
   * @throws ClassNotFoundException
   */
  public static Class<?> loadClass(String className) throws ClassNotFoundException {
    return loadClass(className, true);
  }

  /**
   * Don't call me. Just don't.
   * 
   * @param className
   * @return
   * @throws ClassNotFoundException
   */
  public static Class<?> loadClass(String className, boolean allowPrimitives) throws ClassNotFoundException {
    // Do we have a primitive class
    if (allowPrimitives) {
      switch (className) {
        case "B":
        case "byte":
          return Byte.TYPE;
        case "C":
        case "char":
          return Character.TYPE;
        case "D":
        case "double":
          return Double.TYPE;
        case "F":
        case "float":
          return Float.TYPE;
        case "I":
        case "int":
          return Integer.TYPE;
        case "J":
        case "long":
          return Long.TYPE;
        case "S":
        case "short":
          return Short.TYPE;
        case "Z":
        case "boolean":
          return Boolean.TYPE;
        case "V":
        case "void":
          return Void.TYPE;
      }
    }

    // JNI format
    if (className.startsWith("L") && className.endsWith(";")) {
      return loadClass(className.substring(1, className.length() - 1), false);
    }

    int arrayDimension = 0;
    while (className.charAt(arrayDimension) == '[') {
      arrayDimension++;
    }

    // If this isn't an array after all
    if (arrayDimension == 0) {
      return Class.forName(className);
    }

    // Load the array
    Class<?> baseClass = loadClass(className.substring(arrayDimension));
    return Array.newInstance(baseClass, new int[arrayDimension]).getClass();
  }

}
