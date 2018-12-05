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

import heros.solver.Pair;

import java.util.Map;
import java.util.Set;

/**
 * A map with sets as values.
 *
 * @author Ondrej Lhotak
 */

public interface MultiMap<K, V> extends Iterable<Pair<K, V>> {
  public boolean isEmpty();

  public int numKeys();

  public boolean contains(K key, V value);

  public boolean containsKey(K key);

  public boolean containsValue(V value);

  public boolean put(K key, V value);

  public boolean putAll(K key, Set<V> values);

  public boolean putAll(Map<K, Set<V>> m);

  public boolean putAll(MultiMap<K, V> m);

  public boolean remove(K key, V value);

  public boolean remove(K key);

  public boolean removeAll(K key, Set<V> values);

  public Set<V> get(K o);

  public Set<K> keySet();

  public Set<V> values();

  public boolean equals(Object o);

  public int hashCode();

  /**
   * Gets the number of keys in this MultiMap
   * 
   * @return The number of keys in this MultiMap
   */
  public int size();

  public void clear();
}
