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
import java.util.NoSuchElementException;

/**
 * A java.util.Map with Numberable objects as the keys. This one is designed for maps close to the size of the universe. For
 * smaller maps, use SmallNumberedMap.
 *
 * @author Ondrej Lhotak
 */
public final class LargeNumberedMap<K extends Numberable, V> implements INumberedMap<K, V> {

  private final IterableNumberer<K> universe;
  private V[] values;

  public LargeNumberedMap(IterableNumberer<K> universe) {
    this.universe = universe;
    int size = universe.size();
    this.values = newArray(size < 8 ? 8 : size);
  }

  @SuppressWarnings("unchecked")
  private static <T> T[] newArray(int size) {
    return (T[]) new Object[size];
  }

  @Override
  public boolean put(K key, V value) {
    int number = key.getNumber();
    if (number == 0) {
      throw new RuntimeException(String.format("oops, forgot to initialize. Object is of type %s, and looks like this: %s",
          key.getClass().getName(), key.toString()));
    }
    if (number >= values.length) {
      Object[] oldValues = values;
      values = newArray(Math.max(universe.size() * 2, number) + 5);
      System.arraycopy(oldValues, 0, values, 0, oldValues.length);
    }
    boolean ret = (values[number] != value);
    values[number] = value;
    return ret;
  }

  @Override
  public V get(K key) {
    int i = key.getNumber();
    if (i >= values.length) {
      return null;
    }
    return values[i];
  }

  @Override
  public void remove(K key) {
    int i = key.getNumber();
    if (i < values.length) {
      values[i] = null;
    }
  }

  @Override
  public Iterator<K> keyIterator() {
    return new Iterator<K>() {
      int cur = 0;

      private void advance() {
        while (cur < values.length && values[cur] == null) {
          cur++;
        }
      }

      @Override
      public boolean hasNext() {
        advance();
        return cur < values.length;
      }

      @Override
      public K next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return universe.get(cur++);
      }

      @Override
      public void remove() {
        values[cur - 1] = null;
      }
    };
  }
}
