package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Implementation of HashMap which guarantees a stable (between executions) order for its elements upon iteration.
 *
 * This is quite useful for maps of Locals, to avoid nondeterministic local-name drift.
 */
public class DeterministicHashMap<K, V> extends HashMap<K, V> {
  Set<K> keys = new TrustingMonotonicArraySet<K>();

  /** Constructs a DeterministicHashMap with the given initial capacity. */
  public DeterministicHashMap(int initialCapacity) {
    super(initialCapacity);
  }

  /** Constructs a DeterministicHashMap with the given initial capacity and load factor. */
  public DeterministicHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  /** Inserts a mapping in this HashMap from <code>key</code> to <code>value</code>. */
  @Override
  public V put(K key, V value) {
    if (!containsKey(key)) {
      keys.add(key);
    }

    return super.put(key, value);
  }

  /** Removes the given object from this HashMap (unsupported). */
  @Override
  public V remove(Object obj) {
    throw new UnsupportedOperationException();
  }

  /** Returns a backed list of keys for this HashMap (unsupported). */
  @Override
  public Set<K> keySet() {
    return keys;
  }
}

/**
 * ArraySet which doesn't check that the elements that you insert are previous uncontained.
 */

class TrustingMonotonicArraySet<T> extends AbstractSet<T> {
  private static final int DEFAULT_SIZE = 8;

  private int numElements;
  private int maxElements;
  private T[] elements;

  @SuppressWarnings("unchecked")
  public TrustingMonotonicArraySet() {
    maxElements = DEFAULT_SIZE;
    elements = (T[]) new Object[DEFAULT_SIZE];
    numElements = 0;
  }

  /**
   * Create a set which contains the given elements.
   */

  public TrustingMonotonicArraySet(T[] elements) {
    this();

    for (T element : elements) {
      add(element);
    }
  }

  public void clear() {
    numElements = 0;
  }

  public boolean contains(Object obj) {
    for (int i = 0; i < numElements; i++) {
      if (elements[i].equals(obj)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean add(T e) {
    // Expand array if necessary
    if (numElements == maxElements) {
      doubleCapacity();
    }

    // Add element
    elements[numElements++] = e;
    return true;
  }

  @Override
  public int size() {
    return numElements;
  }

  @Override
  public Iterator<T> iterator() {
    return new ArrayIterator();
  }

  private class ArrayIterator implements Iterator<T> {
    int nextIndex;

    ArrayIterator() {
      nextIndex = 0;
    }

    public boolean hasNext() {
      return nextIndex < numElements;
    }

    @Override
    public T next() throws NoSuchElementException {
      if (!(nextIndex < numElements)) {
        throw new NoSuchElementException();
      }

      return elements[nextIndex++];
    }

    @Override
    public void remove() throws NoSuchElementException {
      if (nextIndex == 0) {
        throw new NoSuchElementException();
      } else {
        removeElementAt(nextIndex - 1);
        nextIndex = nextIndex - 1;
      }
    }
  }

  private void removeElementAt(int index) {
    throw new UnsupportedOperationException();
    /*
     * // Handle simple case if(index == numElements - 1) { numElements--; return; }
     *
     * // Else, shift over elements System.arraycopy(elements, index + 1, elements, index, numElements - (index + 1));
     * numElements--;
     */
  }

  private void doubleCapacity() {
    int newSize = maxElements * 2;

    @SuppressWarnings("unchecked")
    T[] newElements = (T[]) new Object[newSize];

    System.arraycopy(elements, 0, newElements, 0, numElements);
    elements = newElements;
    maxElements = newSize;
  }

  @Override
  public T[] toArray() {
    @SuppressWarnings("unchecked")
    T[] array = (T[]) new Object[numElements];

    System.arraycopy(elements, 0, array, 0, numElements);
    return array;
  }
}
