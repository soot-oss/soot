package soot.dotnet;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

import soot.jimple.ClassConstant;

/**
 * Expand ClassConstant with .NET type name converter
 */
public class DotnetClassConstant extends ClassConstant {

  private DotnetClassConstant(String s) {
    super(convertDotnetClassToJvmDescriptor(s));
  }

  /**
   * Convert Dotnet Class to Java Descriptor https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3
   *
   * @param s
   *          class to convert
   * @return converted to descriptor
   */
  private static String convertDotnetClassToJvmDescriptor(String s) {
    try {
      return "L" + s.replace(".", "/").replace("+", "$") + ";";
    } catch (Exception e) {
      throw new RuntimeException("Cannot convert Dotnet class \"" + s + "\" to JVM Descriptor: " + e);
    }
  }

  public static DotnetClassConstant v(String value) {
    return new DotnetClassConstant(value);
  }

  // In this case, equals should be structural equality.
  @Override
  public boolean equals(Object c) {
    return (c instanceof ClassConstant && ((ClassConstant) c).value.equals(this.value));
  }
}
