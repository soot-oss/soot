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

public interface MultiMap<K, V> {

    public Set<V> get(K key);

    public boolean put(K key, V val);

    public boolean remove(K key, V val);

    public Set<K> keySet();

    public boolean containsKey(K key);

    public int size();

    public String toString();

    public boolean putAll(K key, Collection<? extends V> vals);

    public Set<V> removeAll(K key);
    
    public void clear();

    public boolean isEmpty();
}