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
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SootSig {
  private static final Logger logger = LoggerFactory.getLogger(SootSig.class);

  private static Map<Constructor<?>, String> constrCache = new ConcurrentHashMap<Constructor<?>, String>(); // TODO should be
                                                                                                            // a map with
                                                                                                            // soft keys,
                                                                                                            // actually
  private static Map<Method, String> methodCache = new ConcurrentHashMap<Method, String>(); // TODO should be a map with soft
                                                                                            // keys, actually

  public static String sootSignature(Constructor<?> c) {
    String res = constrCache.get(c);
    if (res == null) {
      String[] paramTypes = classesToTypeNames(c.getParameterTypes());
      res = sootSignature(c.getDeclaringClass().getName(), "void", "<init>", paramTypes);
      constrCache.put(c, res);
    }
    return res;
  }

  public static String sootSignature(Object receiver, Method m) {
    Class<?> receiverClass = Modifier.isStatic(m.getModifiers()) ? m.getDeclaringClass() : receiver.getClass();
    try {
      // resolve virtual call
      Method resolved = null;
      Class<?> c = receiverClass;
      do {
        try {
          resolved = c.getDeclaredMethod(m.getName(), m.getParameterTypes());
        } catch (NoSuchMethodException e) {
          c = c.getSuperclass();
        }
      } while (resolved == null && c != null);
      if (resolved == null) {
        Error error = new Error("Method not found : " + m + " in class " + receiverClass + " and super classes.");
        logger.error(error.getMessage(), error);
      }

      String res = methodCache.get(resolved);
      if (res == null) {
        String[] paramTypes = classesToTypeNames(resolved.getParameterTypes());
        res = sootSignature(resolved.getDeclaringClass().getName(), getTypeName(resolved.getReturnType()),
            resolved.getName(), paramTypes);
        methodCache.put(resolved, res);
      }
      return res;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static String[] classesToTypeNames(Class<?>[] params) {
    String[] paramTypes = new String[params.length];
    int i = 0;
    for (Class<?> type : params) {
      paramTypes[i] = getTypeName(type);
      i++;
    }
    return paramTypes;
  }

  private static String getTypeName(Class<?> type) {
    // copied from java.lang.reflect.Field.getTypeName(Class)
    if (type.isArray()) {
      try {
        Class<?> cl = type;
        int dimensions = 0;
        while (cl.isArray()) {
          dimensions++;
          cl = cl.getComponentType();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(cl.getName());
        for (int i = 0; i < dimensions; i++) {
          sb.append("[]");
        }
        return sb.toString();
      } catch (Throwable e) {
        /* FALLTHRU */ }
    }
    return type.getName();
  }

  private static String sootSignature(String declaringClass, String returnType, String name, String... paramTypes) {
    StringBuilder b = new StringBuilder();
    b.append("<");
    b.append(declaringClass);
    b.append(": ");
    b.append(returnType);
    b.append(" ");
    b.append(name);
    b.append("(");
    int i = 0;
    for (String type : paramTypes) {
      i++;
      b.append(type);
      if (i < paramTypes.length) {
        b.append(",");
      }
    }
    b.append(")>");
    return b.toString();
  }

  public static String sootSignature(Field f) {
    StringBuilder b = new StringBuilder();
    b.append("<");
    b.append(getTypeName(f.getDeclaringClass()));
    b.append(": ");
    b.append(getTypeName(f.getType()));
    b.append(" ");
    b.append(f.getName());
    b.append(">");
    return b.toString();
  }

}
