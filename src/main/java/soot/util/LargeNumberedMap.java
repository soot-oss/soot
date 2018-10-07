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
 * A java.util.Map-like map with Numberable objects as the keys. This one is designed for maps close to the size of the
 * universe. For smaller maps, use SmallNumberedMap.
 *
 * @author Ondrej Lhotak
 */

public final class LargeNumberedMap<K extends Numberable, V> {
  public LargeNumberedMap(ArrayNumberer<K> universe) {
    this.universe = universe;
    int newsize = universe.size();
    if (newsize < 8) {
      newsize = 8;
    }
    values = new Object[newsize];
  }

  public boolean put(Numberable key, V value) {
    int number = key.getNumber();
    if (number == 0) {
      throw new RuntimeException("oops, forgot to initialize");
    }
    if (number >= values.length) {
      Object[] oldValues = values;
      values = new Object[universe.size() * 2 + 5];
      System.arraycopy(oldValues, 0, values, 0, oldValues.length);
    }
    boolean ret = (values[number] != value);
    values[number] = value;
    return ret;
  }

  @SuppressWarnings("unchecked")
  public V get(Numberable key) {
    int i = key.getNumber();
    if (i >= values.length) {
      return null;
    }
    return (V) values[i];
  }

  public Iterator<K> keyIterator() {
    return new Iterator<K>() {
      int cur = 0;

      private void advance() {
        while (cur < values.length && values[cur] == null) {
          cur++;
        }
      }

      public boolean hasNext() {
        advance();
        return cur < values.length;
      }

      public K next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return universe.get(cur++);
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  /* Private stuff. */

  private Object[] values;
  private ArrayNumberer<K> universe;
}
