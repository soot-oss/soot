package soot.jimple.spark.ondemand.genericutil;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract class AbstractMultiMap<K, V> implements MultiMap<K, V> {

  protected final Map<K, Set<V>> map = new HashMap<K, Set<V>>();

  protected final boolean create;

  protected AbstractMultiMap(boolean create) {
    this.create = create;
  }

  protected abstract Set<V> createSet();

  protected Set<V> emptySet() {
    return Collections.<V>emptySet();
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.MultiMap#get(K)
   */
  @Override
  public Set<V> get(K key) {
    Set<V> ret = map.get(key);
    if (ret == null) {
      if (create) {
        ret = createSet();
        map.put(key, ret);
      } else {
        ret = emptySet();
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.MultiMap#put(K, V)
   */
  @Override
  public boolean put(K key, V val) {
    Set<V> vals = map.get(key);
    if (vals == null) {
      vals = createSet();
      map.put(key, vals);
    }
    return vals.add(val);
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.MultiMap#remove(K, V)
   */
  @Override
  public boolean remove(K key, V val) {
    Set<V> elems = map.get(key);
    if (elems == null) {
      return false;
    }
    boolean ret = elems.remove(val);
    if (elems.isEmpty()) {
      map.remove(key);
    }
    return ret;
  }

  @Override
  public Set<V> removeAll(K key) {
    return map.remove(key);
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.MultiMap#keys()
   */
  @Override
  public Set<K> keySet() {
    return map.keySet();
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.MultiMap#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey(K key) {
    return map.containsKey(key);
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.MultiMap#size()
   */
  @Override
  public int size() {
    int ret = 0;
    for (K key : keySet()) {
      ret += get(key).size();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.MultiMap#toString()
   */
  @Override
  public String toString() {
    return map.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.MultiMap#putAll(K, java.util.Set)
   */
  @Override
  public boolean putAll(K key, Collection<? extends V> vals) {
    Set<V> edges = map.get(key);
    if (edges == null) {
      edges = createSet();
      map.put(key, edges);
    }
    return edges.addAll(vals);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }
}
