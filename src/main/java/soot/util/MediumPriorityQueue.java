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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Steven Lambeth
 *
 */
class MediumPriorityQueue<E> extends PriorityQueue<E> {
  final static int MAX_CAPACITY = Long.SIZE * Long.SIZE;

  private int size = 0;
  private long modCount = 0;
  private long[] data;
  private long lookup = 0;

  void addAll() {
    size = N;
    Arrays.fill(data, -1);
    data[data.length - 1] = -1L >>> -size;
    lookup = -1L >>> -data.length;
    min = 0;
    modCount++;
  }

  MediumPriorityQueue(List<? extends E> universe, Map<E, Integer> ordinalMap) {
    super(universe, ordinalMap);
    data = new long[(N + Long.SIZE - 1) >>> 6];
    assert N > SmallPriorityQueue.MAX_CAPACITY;
    assert N <= MAX_CAPACITY;
  }

  @Override
  public void clear() {
    size = 0;
    Arrays.fill(data, 0);
    lookup = 0;
    min = Integer.MAX_VALUE;
    modCount++;
  }

  @Override
  int nextSetBit(int fromIndex) {
    assert fromIndex >= 0;

    int bb = fromIndex >>> 6;

    while (fromIndex < N) {
      // remove everything from t1 that is less than "fromIndex",
      long m1 = -1L << fromIndex;
      // t1 contains now all active bits
      long t1 = data[bb] & m1;

      // the expected index m1 in t1 is set (optional test if NOTZ is
      // expensive)
      if ((t1 & -m1) != 0) {
        return fromIndex;
      }

      // some bits are left in t1, so we can finish
      if (t1 != 0) {
        return (bb << 6) + numberOfTrailingZeros(t1);
      }

      // we know the previous block is empty, so we start our lookup on
      // the next one
      long m0 = -1L << ++bb;
      long t0 = lookup & m0;

      // find next used block
      if ((t0 & -m0) == 0) {
        bb = numberOfTrailingZeros(t0);
      }

      // re-assign new search index
      fromIndex = bb << 6;

      // next and last round
    }
    return fromIndex;
  }

  @Override
  boolean add(int ordinal) {
    int bucket = ordinal >>> 6;
    long prv = data[bucket];
    long now = prv | (1L << ordinal);
    if (prv == now) {
      return false;
    }
    data[bucket] = now;
    lookup |= (1L << bucket);
    size++;
    modCount++;
    min = Math.min(min, ordinal);
    return true;
  }

  @Override
  boolean contains(int ordinal) {
    assert ordinal >= 0;
    assert ordinal < N;
    return ((data[ordinal >>> 6] >>> ordinal) & 1L) == 1L;
  }

  @Override
  boolean remove(int index) {
    assert index >= 0;
    assert index < N;

    int bucket = index >>> 6;
    long old = data[bucket];
    long now = old & ~(1L << index);

    if (old == now) {
      return false;
    }

    if (0 == now) {
      lookup &= ~(1L << bucket);
    }

    size--;
    modCount++;

    data[bucket] = now;

    if (min == index) {
      min = nextSetBit(min + 1);
    }

    return true;
  }

  @Override
  public Iterator<E> iterator() {
    return new Itr() {
      @Override
      long getExpected() {
        return modCount;
      }
    };
  }

  @Override
  public int size() {
    return size;
  }
}
