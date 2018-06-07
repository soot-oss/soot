package soot.rtlib.tamiflex;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultHandler implements IUnexpectedReflectiveCallHandler {
  public void methodInvoke(Object receiver, Method m) {
    System.err.println("Unexpected reflective call to method " + m);
  }

  public void constructorNewInstance(Constructor<?> c) {
    System.err.println("Unexpected reflective instantiation via constructor " + c);
  }

  public void classNewInstance(Class<?> c) {
    System.err.println("Unexpected reflective instantiation via Class.newInstance on class " + c);
  }

  public void classForName(String typeName) {
    System.err.println("Unexpected reflective loading of class " + typeName);
  }

  public void fieldSet(Object receiver, Field f) {
    System.err.println("Unexpected reflective field set: " + f);
  }

  public void fieldGet(Object receiver, Field f) {
    System.err.println("Unexpected reflective field get: " + f);
  }
}
