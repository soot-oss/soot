/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.jimple.spark.ondemand.genericutil;

import java.util.Collection;
import java.util.Set;

public class ArraySetMultiMap<K, V> extends AbstractMultiMap<K, V> {

  public static final ArraySetMultiMap EMPTY = new ArraySetMultiMap<Object, Object>() {

    public boolean put(Object key, Object val) {
      throw new RuntimeException();
    }

    public boolean putAll(Object key, Collection<? extends Object> vals) {
      throw new RuntimeException();
    }

  };

  public ArraySetMultiMap() {
    super(false);
  }

  public ArraySetMultiMap(boolean create) {
    super(create);
  }

  @Override
  protected Set<V> createSet() {
    return new ArraySet<V>();
  }

  protected Set<V> emptySet() {
    return ArraySet.<V> empty();
  }

  public ArraySet<V> get(K key) {
    return (ArraySet<V>) super.get(key);
  }
}