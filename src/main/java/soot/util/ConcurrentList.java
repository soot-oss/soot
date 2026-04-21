package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An implementation of a concurrent "list". Note that this is primarily intended to be
 * backwards compatible with old method signatures (see AbstractHost) and some
 * functionalities are not implemented.
 * 
 * @param <T> the type of data to save in the list
 */
public class ConcurrentList<T> extends ConcurrentLinkedQueue<T> implements List<T> {
  //we do inherit directly from the implementation to save the few bytes for the pointer.
  private static final long serialVersionUID = 1L;

  @Override
  public void add(int arg0, T arg1) {
    throw new RuntimeException("Unsupported");

  }

  @Override
  public boolean addAll(int arg0, Collection<? extends T> arg1) {
    throw new RuntimeException("Unsupported");
  }

  @Override
  public T get(int index) {
    Iterator<T> it = this.iterator();
    for (int i = 0; i < index; i++) {
      it.next();
    }
    return it.next();
  }

  @Override
  public int indexOf(Object o) {
    int idx = 0;
    for (T i : this) {
      if (i.equals(o)) {
        return idx;
      }
      idx++;
    }
    return idx;
  }

  @Override
  public int lastIndexOf(Object o) {
    throw new RuntimeException("Unsupported");
  }

  @Override
  public ListIterator<T> listIterator() {
    throw new RuntimeException("Unsupported");
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    throw new RuntimeException("Unsupported");
  }

  @Override
  public T remove(int index) {
    throw new RuntimeException("Unsupported; this is a bad idea concurrently");
  }

  @Override
  public T set(int arg0, T arg1) {
    throw new RuntimeException("Unsupported; this is a bad idea concurrently");
  }

  @Override
  public List<T> subList(int arg0, int arg1) {
    throw new RuntimeException("Unsupported");
  }

}
