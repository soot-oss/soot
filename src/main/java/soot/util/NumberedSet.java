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

import java.util.Iterator;

/**
 * Holds a set of Numberable objects.
 *
 * @author Ondrej Lhotak
 */
public final class NumberedSet<N extends Numberable> {

  private final IterableNumberer<N> universe;
  private Numberable[] array = new Numberable[8];
  private BitVector bits;
  private int size = 0;

  public NumberedSet(IterableNumberer<N> universe) {
    this.universe = universe;
  }

  public boolean add(Numberable o) {
    if (array != null) {
      int pos = findPosition(o);
      if (array[pos] == o) {
        return false;
      }
      size++;
      if (size * 3 > array.length * 2) {
        doubleSize();
        if (array != null) {
          pos = findPosition(o);
        } else {
          int number = o.getNumber();
          if (number == 0) {
            throw new RuntimeException("unnumbered");
          }
          return bits.set(number);
        }
      }
      array[pos] = o;
      return true;
    } else {
      int number = o.getNumber();
      if (number == 0) {
        throw new RuntimeException("unnumbered");
      }
      if (bits.set(number)) {
        size++;
        return true;
      } else {
        return false;
      }
    }
  }

  public boolean contains(Numberable o) {
    if (array != null) {
      return array[findPosition(o)] != null;
    } else {
      int number = o.getNumber();
      if (number == 0) {
        throw new RuntimeException("unnumbered");
      }
      return bits.get(number);
    }
  }

  private int findPosition(Numberable o) {
    int number = o.getNumber();
    if (number == 0) {
      throw new RuntimeException("unnumbered");
    }
    number = number & (array.length - 1);
    while (true) {
      Numberable temp = array[number];
      if (temp == o || temp == null) {
        return number;
      }
      number = (number + 1) & (array.length - 1);
    }
  }

  private void doubleSize() {
    int uniSize = universe.size();
    if (array.length * 128 > uniSize) {
      bits = new BitVector(uniSize);
      Numberable[] oldArray = array;
      array = null;
      for (Numberable element : oldArray) {
        if (element != null) {
          bits.set(element.getNumber());
        }
      }
    } else {
      Numberable[] oldArray = array;
      array = new Numberable[array.length * 2];
      for (Numberable element : oldArray) {
        if (element != null) {
          array[findPosition(element)] = element;
        }
      }
    }
  }

  public final int size() {
    return size;
  }

  public Iterator<N> iterator() {
    if (array == null) {
      return new BitSetIterator();
    } else {
      return new NumberedSetIterator();
    }
  }

  private class BitSetIterator implements Iterator<N> {
    private final soot.util.BitSetIterator iter;

    BitSetIterator() {
      iter = NumberedSet.this.bits.iterator();
    }

    @Override
    public final boolean hasNext() {
      return iter.hasNext();
    }

    @Override
    public final N next() {
      return NumberedSet.this.universe.get(iter.next());
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class NumberedSetIterator implements Iterator<N> {
    private int cur = 0;

    NumberedSetIterator() {
      seekNext();
    }

    protected final void seekNext() {
      Numberable[] temp = NumberedSet.this.array;
      try {
        while (temp[cur] == null) {
          cur++;
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        cur = -1;
      }
    }

    @Override
    public final boolean hasNext() {
      return cur != -1;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public final N next() {
      @SuppressWarnings("unchecked")
      N ret = (N) NumberedSet.this.array[cur];
      cur++;
      seekNext();
      return ret;
    }
  }
}
