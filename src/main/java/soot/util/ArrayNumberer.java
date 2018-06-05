package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class that numbers objects, so they can be placed in bitsets.
 *
 * @author Ondrej Lhotak
 * @author xiao, generalize it.
 */

public class ArrayNumberer<E extends Numberable> implements IterableNumberer<E> {
  protected E[] numberToObj;
  protected int lastNumber;

  @SuppressWarnings("unchecked")
  public ArrayNumberer() {
    numberToObj = (E[]) new Numberable[1024];
    lastNumber = 0;
  }

  public ArrayNumberer(E[] elements) {
    numberToObj = elements;
    lastNumber = elements.length;
  }

  private void resize(int n) {
    numberToObj = Arrays.copyOf(numberToObj, n);
  }

  public synchronized void add(E o) {
    if (o.getNumber() != 0) {
      return;
    }

    ++lastNumber;
    if (lastNumber >= numberToObj.length) {
      resize(numberToObj.length * 2);
    }
    numberToObj[lastNumber] = o;
    o.setNumber(lastNumber);
  }

  public long get(E o) {
    if (o == null) {
      return 0;
    }
    int ret = o.getNumber();
    if (ret == 0) {
      throw new RuntimeException("unnumbered: " + o);
    }
    return ret;
  }

  public E get(long number) {
    if (number == 0) {
      return null;
    }
    E ret = numberToObj[(int) number];
    if (ret == null) {
      throw new RuntimeException("no object with number " + number);
    }
    return ret;
  }

  public int size() {
    return lastNumber;
  }

  public Iterator<E> iterator() {
    return new Iterator<E>() {
      int cur = 1;

      public final boolean hasNext() {
        return cur <= lastNumber && cur < numberToObj.length && numberToObj[cur] != null;
      }

      public final E next() {
        if (hasNext()) {
          return numberToObj[cur++];
        }
        throw new NoSuchElementException();
      }

      public final void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
