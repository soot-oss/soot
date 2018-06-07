package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 Clark Verbrugge
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

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.RefType;
import soot.Type;

/*
 * A less resource hungry implementation of the TypeStack would just have pointers to
 * 'sub-stacks' instead of copying the entire array around.
 */

class TypeStack {
  private static final Logger logger = LoggerFactory.getLogger(TypeStack.class);
  private Type[] types;

  private TypeStack() {
    // no constructor
  }

  public Object clone() {
    TypeStack newTypeStack = new TypeStack();

    newTypeStack.types = types.clone();

    return newTypeStack;
  }

  /**
   * Returns an empty stack.
   */

  public static TypeStack v() {
    TypeStack typeStack = new TypeStack();

    typeStack.types = new Type[0];

    return typeStack;
  }

  public TypeStack pop() {
    TypeStack newStack = new TypeStack();

    newStack.types = new Type[types.length - 1];
    System.arraycopy(types, 0, newStack.types, 0, types.length - 1);

    return newStack;
  }

  public TypeStack push(Type type) {
    TypeStack newStack = new TypeStack();

    newStack.types = new Type[types.length + 1];
    System.arraycopy(types, 0, newStack.types, 0, types.length);

    newStack.types[types.length] = type;

    return newStack;
  }

  public Type get(int index) {
    return types[index];
  }

  public int topIndex() {
    return types.length - 1;
  }

  public Type top() {
    if (types.length == 0) {
      throw new RuntimeException("TypeStack is empty");
    } else {
      return types[types.length - 1];
    }
  }

  public boolean equals(Object object) {
    if (object instanceof TypeStack) {
      TypeStack otherStack = (TypeStack) object;

      if (otherStack.types.length != types.length) {
        return false;
      }

      for (Type element : types) {
        if (!element.equals(element)) {
          return false;
        }
      }

      return true;
    } else {
      return false;
    }
  }

  public TypeStack merge(TypeStack other) {

    if (types.length != other.types.length) {
      throw new RuntimeException(
          "TypeStack merging failed; unequal " + "stack lengths: " + types.length + " and " + other.types.length);
    }

    TypeStack newStack = new TypeStack();

    newStack.types = new Type[other.types.length];

    for (int i = 0; i < types.length; i++) {
      if (types[i].equals(other.types[i])) {
        newStack.types[i] = types[i];
      } else {
        if ((!(types[i] instanceof ArrayType) && !(types[i] instanceof RefType))
            || (!(other.types[i] instanceof RefType) && !(other.types[i] instanceof ArrayType))) {
          throw new RuntimeException("TypeStack merging failed; incompatible types " + types[i] + " and " + other.types[i]);
        }

        // logger.debug("Merging: " + types[i] + " with " + other.types[i]);

        newStack.types[i] = RefType.v("java.lang.Object");
      }
    }

    return newStack;
  }

  public void print(PrintStream out) {
    for (int i = types.length - 1; i >= 0; i--) {
      out.println(i + ": " + types[i].toString());
    }

    if (types.length == 0) {
      out.println("<empty>");
    }
  }
}
