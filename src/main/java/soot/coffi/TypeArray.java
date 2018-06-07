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

import soot.ArrayType;
import soot.RefType;
import soot.Type;

class TypeArray {
  private Type[] types;

  private TypeArray() {
  }

  /**
   * Returns an empty array of types.
   *
   */

  public static TypeArray v(int size) {
    TypeArray newArray = new TypeArray();

    newArray.types = new Type[size];

    for (int i = 0; i < size; i++) {
      newArray.types[i] = UnusuableType.v();
    }

    return newArray;
  }

  public Type get(int index) {
    return types[index];
  }

  public TypeArray set(int index, Type type) {
    TypeArray newArray = new TypeArray();

    newArray.types = types.clone();
    newArray.types[index] = type;

    return newArray;
  }

  public boolean equals(Object obj) {
    if (obj instanceof TypeArray) {
      TypeArray other = (TypeArray) obj;

      if (types.length != other.types.length) {
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

  public TypeArray merge(TypeArray otherArray) {
    TypeArray newArray = new TypeArray();

    if (types.length != otherArray.types.length) {
      throw new RuntimeException("Merging of type arrays failed; unequal array length");
    }

    newArray.types = new Type[types.length];

    for (int i = 0; i < types.length; i++) {
      if (types[i].equals(otherArray.types[i])) {
        newArray.types[i] = types[i];
      } else if ((types[i] instanceof ArrayType || types[i] instanceof RefType)
          && (otherArray.types[i] instanceof ArrayType || otherArray.types[i] instanceof RefType)) {
        // This type merge does not need to be accurate, because it is not really used

        newArray.types[i] = RefType.v("java.lang.Object");
      } else {
        newArray.types[i] = UnusuableType.v();
      }
    }
    return newArray;
  }

  public void print(PrintStream out) {
    for (int i = 0; i < types.length; i++) {
      out.println(i + ": " + types[i].toString());
    }
  }
}
