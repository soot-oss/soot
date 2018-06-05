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
import java.util.HashSet;
import java.util.Set;

public class ReflectiveCalls {

  private final static Set<String> classForName = new HashSet<String>();
  private final static Set<String> classNewInstance = new HashSet<String>();
  private final static Set<String> constructorNewInstance = new HashSet<String>();
  private final static Set<String> methodInvoke = new HashSet<String>();
  private final static Set<String> fieldSet = new HashSet<String>();
  private final static Set<String> fieldGet = new HashSet<String>();

  static {
    // soot will add initialization code here
  }

  public static void knownClassForName(int contextId, String className) {
    if (!classForName.contains(contextId + className)) {
      UnexpectedReflectiveCall.classForName(className);
    }
  }

  public static void knownClassNewInstance(int contextId, Class<?> c) {
    if (!classNewInstance.contains(contextId + c.getName())) {
      UnexpectedReflectiveCall.classNewInstance(c);
    }
  }

  public static void knownConstructorNewInstance(int contextId, Constructor<?> c) {
    if (!constructorNewInstance.contains(contextId + SootSig.sootSignature(c))) {
      UnexpectedReflectiveCall.constructorNewInstance(c);
    }
  }

  public static void knownMethodInvoke(int contextId, Object o, Method m) {
    if (!methodInvoke.contains(contextId + SootSig.sootSignature(o, m))) {
      UnexpectedReflectiveCall.methodInvoke(o, m);
    }
  }

  public static void knownFieldSet(int contextId, Object o, Field f) {
    if (!fieldSet.contains(contextId + SootSig.sootSignature(f))) {
      UnexpectedReflectiveCall.fieldSet(o, f);
    }
  }

  public static void knownFieldGet(int contextId, Object o, Field f) {
    if (!fieldGet.contains(contextId + SootSig.sootSignature(f))) {
      UnexpectedReflectiveCall.fieldGet(o, f);
    }
  }
}
