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
import java.util.BitSet;
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
  protected BitSet freeNumbers;

  @SuppressWarnings("unchecked")
  public ArrayNumberer() {
    this.numberToObj = (E[]) new Numberable[1024];
    this.lastNumber = 0;
  }

  public ArrayNumberer(E[] elements) {
    this.numberToObj = elements;
    this.lastNumber = elements.length;
  }

  private void resize(int n) {
    numberToObj = Arrays.copyOf(numberToObj, n);
  }

  @Override
  public synchronized void add(E o) {
    if (o.getNumber() != 0) {
      return;
    }

    // In case we removed entries from the numberer, we want to re-use the free space
    int chosenNumber = -1;
    if (freeNumbers != null) {
      int ns = freeNumbers.nextSetBit(0);
      if (ns != -1) {
        chosenNumber = ns;
        freeNumbers.clear(ns);
      }
    }
    if (chosenNumber == -1) {
      chosenNumber = ++lastNumber;
    }
    if (chosenNumber >= numberToObj.length) {
      resize(numberToObj.length * 2);
    }
    numberToObj[chosenNumber] = o;
    o.setNumber(chosenNumber);
  }

  @Override
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

  @Override
  public E get(long number) {
    if (number == 0) {
      return null;
    }
    E ret = numberToObj[(int) number];
    if (ret == null) {
      return null;
    }
    return ret;
  }

  @Override
  public int size() {
    return lastNumber;
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      int cur = 1;

      @Override
      public final boolean hasNext() {
        return cur <= lastNumber && cur < numberToObj.length && numberToObj[cur] != null;
      }

      @Override
      public final E next() {
        if (hasNext()) {
          return numberToObj[cur++];
        }
        throw new NoSuchElementException();
      }

      @Override
      public final void remove() {
        ArrayNumberer.this.remove(numberToObj[cur - 1]);
      }
    };
  }

  @Override
  public boolean remove(E o) {
    if (o == null) {
      return false;
    }

    int num = o.getNumber();
    if (num == 0) {
      return false;
    }
    if (freeNumbers == null) {
      freeNumbers = new BitSet(2 * num);
    }
    numberToObj[num] = null;
    o.setNumber(0);
    freeNumbers.set(num);
    return true;
  }
}
