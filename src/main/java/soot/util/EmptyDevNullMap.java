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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Class representing an unmodifiable empty map. Does nothing when trying to add new elements
 * 
 * @author Marc Miltenberger
 * 
 * @param <T>
 */
public class EmptyDevNullMap<K, V> implements Map<K, V> {

  public static EmptyDevNullMap INSTANCE = new EmptyDevNullMap();

  @Override
  public void clear() {
  }

  @Override
  public boolean containsKey(Object arg0) {
    return false;
  }

  @Override
  public boolean containsValue(Object arg0) {
    return false;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return Collections.emptySet();
  }

  @Override
  public V get(Object arg0) {
    return null;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public Set<K> keySet() {
    return Collections.emptySet();
  }

  @Override
  public V put(K arg0, V arg1) {
    return null;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
  }

  @Override
  public V remove(Object key) {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Collection<V> values() {
    return Collections.emptyList();
  }

  public static <K, V> Map<K, V> v() {
    return INSTANCE;
  }

}
