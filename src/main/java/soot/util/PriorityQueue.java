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

import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fixed size priority queue based on bitsets. The elements of the priority queue are ordered according to a given
 * universe. This priority queue does not permit {@code null} elements. Inserting of elements that are not part of the
 * universe is also permitted (doing so will result in a {@code NoSuchElementException}).
 *
 * @author Steven Lambeth
 * @param <E>
 *          the type of elements held in the universe
 */
abstract public class PriorityQueue<E> extends AbstractQueue<E> {
  private static final Logger logger = LoggerFactory.getLogger(PriorityQueue.class);

  abstract class Itr implements Iterator<E> {
    long expected = getExpected();
    int next = min;
    int now = Integer.MAX_VALUE;

    abstract long getExpected();

    @Override
    public boolean hasNext() {
      return next < N;
    }

    @Override
    public E next() {
      if (expected != getExpected()) {
        throw new ConcurrentModificationException();
      }
      if (next >= N) {
        throw new NoSuchElementException();
      }

      now = next;
      next = nextSetBit(next + 1);
      return universe.get(now);
    }

    @Override
    public void remove() {
      if (now >= N) {
        throw new IllegalStateException();
      }
      if (expected != getExpected()) {
        throw new ConcurrentModificationException();
      }

      PriorityQueue.this.remove(now);
      expected = getExpected();
      now = Integer.MAX_VALUE;
    }
  }

  final List<? extends E> universe;
  final int N;
  int min = Integer.MAX_VALUE;
  private Map<E, Integer> ordinalMap;

  int getOrdinal(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }
    Integer i = ordinalMap.get(o);
    if (i == null) {
      throw new NoSuchElementException();
    }
    return i.intValue();
  }

  /**
   * Adds all elements of the universe to this queue.
   */
  abstract void addAll();

  /**
   * Returns the index of the first bit that is set to <code>true</code> that occurs on or after the specified starting
   * index. If no such bit exists then a value bigger that {@code N} is returned.
   *
   * @param fromIndex
   *          the index to start checking from (inclusive).
   * @return the index of the next set bit.
   */
  abstract int nextSetBit(int fromIndex);

  abstract boolean remove(int ordinal);

  abstract boolean add(int ordinal);

  abstract boolean contains(int ordinal);

  /**
   * {@inheritDoc}
   *
   */
  @Override
  final public E peek() {
    return isEmpty() ? null : universe.get(min);
  }

  /**
   * {@inheritDoc}
   *
   */
  @Override
  final public E poll() {
    if (isEmpty()) {
      return null;
    }
    E e = universe.get(min);
    remove(min);
    return e;
  }

  /**
   * {@inheritDoc}
   *
   * @throws NoSuchElementException
   *           if e not part of the universe
   * @throws NullPointerException
   *           if e is {@code null}
   */
  @Override
  final public boolean add(E e) {
    return offer(e);
  }

  /**
   * {@inheritDoc}
   *
   * @throws NoSuchElementException
   *           if e not part of the universe
   * @throws NullPointerException
   *           if e is {@code null}
   */
  @Override
  final public boolean offer(E e) {
    return add(getOrdinal(e));
  }

  /**
   * {@inheritDoc}
   *
   */
  @Override
  final public boolean remove(Object o) {
    if (o == null || isEmpty()) {
      return false;
    }
    try {
      if (o.equals(peek())) {
        remove(min);
        return true;
      }
      return remove(getOrdinal(o));
    } catch (NoSuchElementException e) {
      logger.debug("" + e.getMessage());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   */
  @Override
  final public boolean contains(Object o) {
    if (o == null) {
      return false;
    }
    try {
      if (o.equals(peek())) {
        return true;
      }

      return contains(getOrdinal(o));
    } catch (NoSuchElementException e) {
      logger.debug("" + e.getMessage());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   */
  @Override
  public boolean isEmpty() {
    return min >= N;
  }

  PriorityQueue(List<? extends E> universe, Map<E, Integer> ordinalMap) {
    assert ordinalMap.size() == universe.size();
    this.universe = universe;
    this.ordinalMap = ordinalMap;
    this.N = universe.size();
  }

  /**
   * Creates a new full priority queue
   *
   * @param universe
   * @return
   */
  public static <E> PriorityQueue<E> of(E[] universe) {
    return of(Arrays.asList(universe));
  }

  /**
   * Creates a new empty priority queue
   *
   * @param universe
   * @return
   */
  public static <E> PriorityQueue<E> noneOf(E[] universe) {
    return noneOf(Arrays.asList(universe));
  }

  /**
   * Creates a new full priority queue
   *
   * @param universe
   * @return
   */
  public static <E> PriorityQueue<E> of(List<? extends E> universe) {
    PriorityQueue<E> q = noneOf(universe);
    q.addAll();
    return q;
  }

  /**
   * Creates a new empty priority queue
   *
   * @param universe
   * @return
   */
  public static <E> PriorityQueue<E> noneOf(List<? extends E> universe) {
    Map<E, Integer> ordinalMap = new HashMap<E, Integer>(2 * universe.size() / 3);
    int i = 0;
    for (E e : universe) {
      if (e == null) {
        throw new NullPointerException("null is not allowed");
      }
      if (ordinalMap.put(e, i++) != null) {
        throw new IllegalArgumentException("duplicate key found");
      }
    }

    return newPriorityQueue(universe, ordinalMap);
  }

  public static <E extends Numberable> PriorityQueue<E> of(List<? extends E> universe, boolean useNumberInterface) {
    PriorityQueue<E> q = noneOf(universe, useNumberInterface);
    q.addAll();
    return q;
  }

  public static <E extends Numberable> PriorityQueue<E> noneOf(final List<? extends E> universe,
      boolean useNumberInterface) {
    if (!useNumberInterface) {
      return noneOf(universe);
    }

    int i = 0;
    for (E e : universe) {
      e.setNumber(i++);
    }

    return newPriorityQueue(universe, new AbstractMap<E, Integer>() {
      @SuppressWarnings("unchecked")
      @Override
      public Integer get(Object key) {
        return ((E) key).getNumber();
      }

      @Override
      public int size() {
        return universe.size();
      }

      @Override
      public Set<java.util.Map.Entry<E, Integer>> entrySet() {
        throw new UnsupportedOperationException();
      }
    });
  }

  private static <E> PriorityQueue<E> newPriorityQueue(List<? extends E> universe, Map<E, Integer> ordinalMap) {
    if (universe.size() <= SmallPriorityQueue.MAX_CAPACITY) {
      return new SmallPriorityQueue<E>(universe, ordinalMap);
    }
    if (universe.size() <= MediumPriorityQueue.MAX_CAPACITY) {
      return new MediumPriorityQueue<E>(universe, ordinalMap);
    }
    return new LargePriorityQueue<E>(universe, ordinalMap);
  }
}
