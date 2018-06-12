/**
 * 
 */
package soot.util;

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

import static java.lang.Long.numberOfTrailingZeros;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Steven Lambeth
 *
 */
class SmallPriorityQueue<E> extends PriorityQueue<E> {
  final static int MAX_CAPACITY = Long.SIZE;

  private long queue = 0;

  void addAll() {
    if (N == 0) {
      return;
    }

    queue = -1L >>> -N;
    min = 0;
  }

  SmallPriorityQueue(List<? extends E> universe, Map<E, Integer> ordinalMap) {
    super(universe, ordinalMap);
    assert universe.size() <= Long.SIZE;
  }

  @Override
  public void clear() {
    queue = 0L;
    min = Integer.MAX_VALUE;
  }

  @Override
  public Iterator<E> iterator() {
    return new Itr() {
      @Override
      long getExpected() {
        return queue;
      }
    };
  }

  @Override
  public int size() {
    return Long.bitCount(queue);
  }

  @Override
  int nextSetBit(int fromIndex) {
    assert fromIndex >= 0;

    if (fromIndex > N) {
      return fromIndex;
    }

    long m0 = -1L << fromIndex;
    long t0 = queue & m0;
    if ((t0 & -m0) != 0) {
      return fromIndex;
    }

    return numberOfTrailingZeros(t0);
  }

  @Override
  boolean add(int ordinal) {
    long old = queue;
    queue |= (1L << ordinal);
    if (old == queue) {
      return false;
    }
    min = Math.min(min, ordinal);
    return true;
  }

  @Override
  boolean contains(int ordinal) {
    assert ordinal >= 0;
    assert ordinal < N;

    return ((queue >>> ordinal) & 1L) == 1L;
  }

  @Override
  boolean remove(int index) {
    assert index >= 0;
    assert index < N;

    long old = queue;
    queue &= ~(1L << index);

    if (old == queue) {
      return false;
    }

    if (min == index) {
      min = nextSetBit(min + 1);
    }
    return true;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    long mask = 0;
    for (Object o : c) {
      mask |= (1L << getOrdinal(o));
    }
    long old = queue;
    queue &= ~mask;
    min = nextSetBit(min);
    return old != queue;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    long mask = 0;
    for (Object o : c) {
      mask |= (1L << getOrdinal(o));
    }
    long old = queue;
    queue &= mask;
    min = nextSetBit(min);
    return old != queue;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    long mask = 0;
    for (Object o : c) {
      mask |= (1L << getOrdinal(o));
    }
    return (mask & ~queue) == 0;
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    long mask = 0;
    for (Object o : c) {
      mask |= (1L << getOrdinal(o));
    }
    long old = queue;
    queue |= mask;
    if (old == queue) {
      return false;
    }
    min = nextSetBit(0);
    return true;
  }

}
