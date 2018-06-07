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

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// QND
class LargePriorityQueue<E> extends PriorityQueue<E> {

  BitSet queue;
  private long modCount = 0;

  LargePriorityQueue(List<? extends E> universe, Map<E, Integer> ordinalMap) {
    super(universe, ordinalMap);
    queue = new BitSet(N);
  }

  @Override
  boolean add(int ordinal) {
    if (contains(ordinal)) {
      return false;
    }
    queue.set(ordinal);
    min = Math.min(min, ordinal);
    modCount++;
    return true;
  }

  @Override
  void addAll() {
    queue.set(0, N);
    min = 0;
    modCount++;
  }

  @Override
  int nextSetBit(int fromIndex) {
    int i = queue.nextSetBit(fromIndex);
    return (i < 0) ? Integer.MAX_VALUE : i;
  }

  @Override
  boolean remove(int ordinal) {
    if (!contains(ordinal)) {
      return false;
    }
    queue.clear(ordinal);

    if (min == ordinal) {
      min = nextSetBit(min + 1);
    }

    modCount++;
    return true;
  }

  @Override
  boolean contains(int ordinal) {
    return queue.get(ordinal);
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
    return queue.cardinality();
  }

}
