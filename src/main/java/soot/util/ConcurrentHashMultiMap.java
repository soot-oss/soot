package soot.util;

import java.util.Collection;

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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * A concurrent version of the {@link HashMultiMap}
 *
 * @author Steven Arzt
 */
public class ConcurrentHashMultiMap<K, V> extends AbstractMultiMap<K, V> {

  private static final long serialVersionUID = -3182515910302586044L;

  private final Map<K, ConcurrentMap<V, V>> m = new ConcurrentHashMap<K, ConcurrentMap<V, V>>(0);

  private final Function<? super K, ? extends ConcurrentMap<V, V>> createNewSet
      = new Function<Object, ConcurrentMap<V, V>>() {

        @Override
        public ConcurrentMap<V, V> apply(Object t) {
          return newSet();
        }

      };

  public ConcurrentHashMultiMap() {
  }

  public ConcurrentHashMultiMap(MultiMap<K, V> m) {
    putAll(m);
  }

  @Override
  public int numKeys() {
    return m.size();
  }

  @Override
  public boolean containsKey(Object key) {
    return m.containsKey(key);
  }

  @Override
  public boolean containsValue(V value) {
    for (Map<V, V> s : m.values()) {
      if (s.containsKey(value)) {
        return true;
      }
    }
    return false;
  }

  protected ConcurrentMap<V, V> newSet() {
    return new ConcurrentHashMap<V, V>();
  }

  private ConcurrentMap<V, V> findSet(K key) {
    ConcurrentMap<V, V> s = m.computeIfAbsent(key, createNewSet);
    return s;
  }

  @Override
  public boolean put(K key, V value) {
    return findSet(key).put(value, value) == null;
  }

  public V putIfAbsent(K key, V value) {
    return findSet(key).putIfAbsent(value, value);
  }

  private class PutAllCreate implements Function<Object, ConcurrentMap<V, V>> {

    private Collection<V> values;
    private Boolean result;

    public PutAllCreate(Collection<V> values) {
      this.values = values;
    }

    @Override
    public ConcurrentMap<V, V> apply(Object t) {
      ConcurrentMap<V, V> newSet = newSet();
      result = false;
      for (V v : values) {
        if (newSet.put(v, v) != null) {
          result = true;
        }
      }
      return newSet;
    }

  }

  @Override
  public boolean putAll(K key, Collection<V> values) {
    if (values == null || values.isEmpty()) {
      return false;
    }

    PutAllCreate create = new PutAllCreate(values);
    ConcurrentMap<V, V> s = m.computeIfAbsent(key, create);

    Boolean r = create.result;
    if (r != null) {
      return r;
    }
    // We need to add the elements to the already existing set
    boolean ok = false;
    for (V v : values) {
      if (s.put(v, v) == null) {
        ok = true;
      }
    }

    return ok;
  }

  @Override
  public boolean remove(K key, V value) {
    Map<V, V> s = m.get(key);
    if (s == null) {
      return false;
    }
    boolean ret = s.remove(value) != null;
    if (s.isEmpty()) {
      m.remove(key);
    }
    return ret;
  }

  @Override
  public boolean remove(K key) {
    return null != m.remove(key);
  }

  @Override
  public boolean removeAll(K key, Collection<V> values) {
    Map<V, V> s = m.get(key);
    if (s == null) {
      return false;
    }
    boolean ret = false;
    for (V v : values) {
      if (s.remove(v) != null) {
        ret = true;
      }
    }
    if (s.isEmpty()) {
      m.remove(key);
    }
    return ret;
  }

  @Override
  public Set<V> get(K o) {
    Map<V, V> ret = m.get(o);
    if (ret == null) {
      return Collections.emptySet();
    } else {
      return Collections.unmodifiableSet(ret.keySet());
    }
  }

  @Override
  public Set<K> keySet() {
    return m.keySet();
  }

  @Override
  public Set<V> values() {
    Set<V> ret = new HashSet<V>(m.size());
    for (Map<V, V> s : m.values()) {
      ret.addAll(s.keySet());
    }
    return ret;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MultiMap)) {
      return false;
    }
    @SuppressWarnings("unchecked")
    MultiMap<K, V> mm = (MultiMap<K, V>) o;
    if (!keySet().equals(mm.keySet())) {
      return false;
    }
    for (Map.Entry<K, ConcurrentMap<V, V>> e : m.entrySet()) {
      Map<V, V> s = e.getValue();
      if (!s.equals(mm.get(e.getKey()))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return m.hashCode();
  }

  @Override
  public int size() {
    return m.size();
  }

  @Override
  public void clear() {
    m.clear();
  }

  @Override
  public String toString() {
    return m.toString();
  }
}
