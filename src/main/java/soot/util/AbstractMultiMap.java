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

import heros.solver.Pair;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMultiMap<K, V> implements MultiMap<K, V>, Serializable {

  private static final long serialVersionUID = 4558567794548019671L;

  private class EntryIterator implements Iterator<Pair<K, V>> {

    Iterator<K> keyIterator = keySet().iterator();
    Iterator<V> valueIterator = null;
    K currentKey = null;

    @Override
    public boolean hasNext() {
      if (valueIterator != null && valueIterator.hasNext()) {
        return true;
      }

      // Prepare for the next key
      valueIterator = null;
      currentKey = null;
      return keyIterator.hasNext();
    }

    @Override
    public Pair<K, V> next() {
      // Obtain the next key
      if (valueIterator == null) {
        currentKey = keyIterator.next();
        valueIterator = get(currentKey).iterator();
      }
      return new Pair<K, V>(currentKey, valueIterator.next());
    }

    @Override
    public void remove() {
      if (valueIterator == null) {
        // Removing an element twice or removing no valid element does not make sense
        return;
      }
      valueIterator.remove();

      if (get(currentKey).isEmpty()) {
        keyIterator.remove();
        valueIterator = null;
        currentKey = null;
      }
    }

  }

  @Override
  public boolean putAll(MultiMap<K, V> m) {
    boolean hasNew = false;
    for (K key : m.keySet()) {
      if (putAll(key, m.get(key))) {
        hasNew = true;
      }
    }
    return hasNew;
  }

  @Override
  public boolean putAll(Map<K, Set<V>> m) {
    boolean hasNew = false;
    for (K key : m.keySet()) {
      if (putAll(key, m.get(key))) {
        hasNew = true;
      }
    }
    return hasNew;
  }

  @Override
  public boolean isEmpty() {
    return numKeys() == 0;
  }

  @Override
  public boolean contains(K key, V value) {
    Set<V> set = get(key);
    if (set == null) {
      return false;
    }
    return set.contains(value);
  }

  @Override
  public Iterator<Pair<K, V>> iterator() {
    return new EntryIterator();
  }
}
