package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Sable Research Group
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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class IterableMap<K, V> implements Map<K, V> {
  private final HashMap<K, V> content_map;
  private final HashMap<V, HashChain<K>> back_map;
  private final HashChain<K> key_chain;
  private final HashChain<V> value_chain;

  private transient Set<K> keySet = null;
  private transient Set<V> valueSet = null;
  private transient Collection<V> values = null;

  public IterableMap() {
    this(7, 0.7f);
  }

  public IterableMap(int initialCapacity) {
    this(initialCapacity, 0.7f);
  }

  public IterableMap(int initialCapacity, float loadFactor) {
    content_map = new HashMap<K, V>(initialCapacity, loadFactor);
    back_map = new HashMap<V, HashChain<K>>(initialCapacity, loadFactor);
    key_chain = new HashChain<K>();
    value_chain = new HashChain<V>();
  }

  @Override
  public void clear() {
    for (K next : key_chain) {
      content_map.remove(next);
    }
    for (V next : value_chain) {
      back_map.remove(next);
    }
    key_chain.clear();
    value_chain.clear();
  }

  public Iterator<K> iterator() {
    return key_chain.iterator();
  }

  @Override
  public boolean containsKey(Object key) {
    return key_chain.contains(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return value_chain.contains(value);
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    return content_map.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof IterableMap)) {
      return false;
    }

    IterableMap<?, ?> other = (IterableMap<?, ?>) o;
    if (!this.key_chain.equals(other.key_chain)) {
      return false;
    }

    // check that the other has our mapping
    for (K ko : key_chain) {
      if (other.content_map.get(ko) != this.content_map.get(ko)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public V get(Object key) {
    return content_map.get(key);
  }

  @Override
  public int hashCode() {
    return content_map.hashCode();
  }

  @Override
  public boolean isEmpty() {
    return key_chain.isEmpty();
  }

  @Override
  public Set<K> keySet() {
    if (keySet == null) {
      keySet = new AbstractSet<K>() {
        @Override
        public Iterator<K> iterator() {
          return key_chain.iterator();
        }

        @Override
        public int size() {
          return key_chain.size();
        }

        @Override
        public boolean contains(Object o) {
          return key_chain.contains(o);
        }

        @Override
        public boolean remove(Object o) {
          if (!key_chain.contains(o)) {
            return false;
          }

          if (IterableMap.this.content_map.get(o) == null) {
            IterableMap.this.remove(o);
            return true;
          }

          return (IterableMap.this.remove(o) != null);
        }

        @Override
        public void clear() {
          IterableMap.this.clear();
        }
      };
    }
    return keySet;
  }

  public Set<V> valueSet() {
    if (valueSet == null) {
      valueSet = new AbstractSet<V>() {
        @Override
        public Iterator<V> iterator() {
          return value_chain.iterator();
        }

        @Override
        public int size() {
          return value_chain.size();
        }

        @Override
        public boolean contains(Object o) {
          return value_chain.contains(o);
        }

        @Override
        public boolean remove(Object o) {
          if (!value_chain.contains(o)) {
            return false;
          }

          HashChain c = (HashChain) IterableMap.this.back_map.get(o);

          for (Iterator it = c.snapshotIterator(); it.hasNext();) {
            Object ko = it.next();

            if (IterableMap.this.content_map.get(o) == null) {
              IterableMap.this.remove(ko);
            } else if (IterableMap.this.remove(ko) == null) {
              return false;
            }
          }
          return true;
        }

        @Override
        public void clear() {
          IterableMap.this.clear();
        }
      };
    }
    return valueSet;
  }

  @Override
  public V put(K key, V value) {
    if (key_chain.contains(key)) {
      V old_value = content_map.get(key);
      if (old_value == value) {
        return value;
      }

      HashChain<K> kc = back_map.get(old_value);
      kc.remove(key);
      if (kc.isEmpty()) {
        value_chain.remove(old_value);
        back_map.remove(old_value);
      }

      kc = back_map.get(value);
      if (kc == null) {
        kc = new HashChain<K>();
        back_map.put(value, kc);
        value_chain.add(value);
      }
      kc.add(key);

      return old_value;
    } else {
      key_chain.add(key);
      content_map.put(key, value);

      HashChain<K> kc = back_map.get(value);
      if (kc == null) {
        kc = new HashChain<K>();
        back_map.put(value, kc);
        value_chain.add(value);
      }
      kc.add(key);

      return null;
    }
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> t) {
    Iterator<? extends K> it;
    if (t instanceof IterableMap) {
      it = ((IterableMap<? extends K, ? extends V>) t).key_chain.iterator();
    } else {
      it = t.keySet().iterator();
    }
    while (it.hasNext()) {
      K key = it.next();
      put(key, t.get(key));
    }
  }

  @Override
  public V remove(Object key) {
    if (!key_chain.contains(key)) {
      return null;
    }

    key_chain.remove(key);
    V value = content_map.remove(key);
    HashChain<K> c = back_map.get(value);
    c.remove(key);
    if (c.isEmpty()) {
      back_map.remove(value);
    }

    return value;
  }

  @Override
  public int size() {
    return key_chain.size();
  }

  @Override
  public Collection<V> values() {
    if (values == null) {
      values = new AbstractCollection<V>() {
        @Override
        public Iterator<V> iterator() {
          return new Mapping_Iterator<K, V>(IterableMap.this.key_chain, IterableMap.this.content_map);
        }

        @Override
        public int size() {
          return key_chain.size();
        }

        @Override
        public boolean contains(Object o) {
          return value_chain.contains(o);
        }

        @Override
        public void clear() {
          IterableMap.this.clear();
        }
      };
    }
    return values;
  }

  public static class Mapping_Iterator<K, V> implements Iterator<V> {
    private final Iterator<K> it;
    private final HashMap<K, V> m;

    public Mapping_Iterator(HashChain<K> c, HashMap<K, V> m) {
      this.it = c.iterator();
      this.m = m;
    }

    @Override
    public boolean hasNext() {
      return it.hasNext();
    }

    @Override
    public V next() throws NoSuchElementException {
      return m.get(it.next());
    }

    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("You cannot remove from an Iterator on the values() for an IterableMap.");
    }
  }
}
