package soot.jimple.spark.ondemand.genericutil;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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

import java.util.Collection;

/**
 * @author manu_s
 *
 */
public final class Stack<T> implements Cloneable {

  private T[] elems;

  private int size = 0;

  @SuppressWarnings("unchecked")
  public Stack(int numElems_) {
    elems = (T[]) new Object[numElems_];
  }

  public Stack() {
    this(4);
  }

  @SuppressWarnings("unchecked")
  public void push(T obj_) {
    assert obj_ != null;
    if (size == elems.length) {
      // lengthen array
      Object[] tmp = elems;
      elems = (T[]) new Object[tmp.length * 2];
      System.arraycopy(tmp, 0, elems, 0, tmp.length);
    }
    elems[size] = obj_;
    size++;
  }

  public void pushAll(Collection<T> c) {
    for (T t : c) {
      push(t);
    }
  }

  public T pop() {
    if (size == 0) {
      return null;
    }
    size--;
    T ret = elems[size];
    elems[size] = null;
    return ret;
  }

  public T peek() {
    if (size == 0) {
      return null;
    }
    return elems[size - 1];
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void clear() {
    size = 0;
  }

  @SuppressWarnings("unchecked")
  public Stack<T> clone() {
    Stack<T> ret = null;
    try {
      ret = (Stack<T>) super.clone();
      ret.elems = (T[]) new Object[elems.length];
      System.arraycopy(elems, 0, ret.elems, 0, size);
      return ret;
    } catch (CloneNotSupportedException e) {
      // should not happen
      throw new InternalError();
    }
  }

  public Object get(int i) {
    return elems[i];
  }

  public boolean contains(Object o) {
    return Util.arrayContains(elems, o, size);
  }

  /**
   * returns first index
   * 
   * @param o
   * @return
   */
  public int indexOf(T o) {
    for (int i = 0; i < size && elems[i] != null; i++) {
      if (elems[i].equals(o)) {
        return i;
      }
    }
    return -1;
  }

  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("[");
    for (int i = 0; i < size && elems[i] != null; i++) {
      if (i > 0) {
        s.append(", ");
      }
      s.append(elems[i].toString());
    }
    s.append("]");
    return s.toString();
  }
}
