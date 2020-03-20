package soot.util;

import java.util.Iterator;

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

/**
 * Common interface for numbered maps
 * 
 * @author Steven Arzt
 *
 * @param <K>
 *          The common type of the keys
 * @param <V>
 *          The common type of the values
 */
public interface INumberedMap<K extends Numberable, V> {

  /**
   * Associates a value with a key.
   * 
   * @param key
   *          The key
   * @param value
   *          The value
   * @return True if the association was new, false if the same value was already associated with the given key before
   */
  public boolean put(K key, V value);

  /**
   * Returns the value associated with a given key.
   * 
   * @param key
   *          The key
   * @return The value associated with the given key
   */
  public V get(K key);

  /**
   * Returns an iterator over the keys with non-null values.
   * 
   * @return The iterator
   */
  public Iterator<K> keyIterator();

  /**
   * Removes the given key from the map
   * 
   * @param key
   *          The key to be removed
   */
  public void remove(K key);

}
