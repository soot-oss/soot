/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
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

package soot.dex;

import java.util.Arrays;

import soot.DoubleType;
import soot.FloatType;
import soot.RefType;
import soot.Type;

public class Util {
    /**
     * Return the dotted class name of a type descriptor, i.e. change Ljava/lang/Object; to java.lang.Object.
     *
     * @raises IllegalArgumentException if classname is not of the form Lpath; or [Lpath;
     * @return the dotted name.
     */
    public static String dottedClassName(String typeDescriptor) {
        int endpos = typeDescriptor.indexOf(';');
        if (!isByteCodeClassName(typeDescriptor))
            throw new IllegalArgumentException("typeDescriptor is not a class typedescriptor");

        String className = typeDescriptor.substring(typeDescriptor.indexOf('L') + 1, endpos);
        return className.replace('/', '.');
    }

    /**
     * Check if passed class name is a byte code classname.
     *
     * @param className the classname to check.
     */
    public static boolean isByteCodeClassName(String className) {
        return ((className.startsWith("L") || className.startsWith("["))
                && className.endsWith(";")
                && (className.indexOf('/') != -1 || className.indexOf('.') == -1));
    }

    /**
     * Concatenate two arrays.
     *
     * @param first first array
     * @param second second array.
     */
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Returns if the type is a floating point type.
     *
     * @param t the type to test
     */
    public static boolean isFloatLike(Type t) {
        return t.equals(FloatType.v()) ||
               t.equals(DoubleType.v()) ||
               t.equals(RefType.v("java.lang.Float")) ||
               t.equals(RefType.v("java.lang.Double"));
    }
}
