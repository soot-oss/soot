package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2019 William Bonnaventure
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

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A class that numbers objects but keeps weak references for garbage collection
 *
 * @author William Bonnaventure
 *
 * @param <T>
 */
public class WeakMapNumberer<T extends Numberable> implements IterableNumberer<T> {

  final Map<T, Integer> map = new WeakHashMap<T, Integer>();
  final Map<Integer, WeakReference<T>> rmap = new WeakHashMap<Integer, WeakReference<T>>();
  int nextIndex = 1;

  public WeakMapNumberer() {
  }

  @Override
  public synchronized void add(T o) {
    if (o.getNumber() != 0) {
      return;
    }
    if (!map.containsKey(o)) {
      Integer key = nextIndex;
      map.put(o, key);
      rmap.put(key, new WeakReference<T>(o));
      o.setNumber(nextIndex++);
    }
  }

  @Override
  public boolean remove(T o) {
    if (o == null) {
      return false;
    }
    int num = o.getNumber();
    if (num == 0) {
      return false;
    }
    o.setNumber(0);
    Integer i = map.remove(o);
    if (i == null) {
      return false;
    }
    rmap.remove(i);
    return true;
  }

  @Override
  public long get(T o) {
    if (o == null) {
      return 0;
    }
    Integer i = map.get(o);
    if (i == null) {
      throw new RuntimeException("couldn't find " + o);
    }
    return i;
  }

  @Override
  public T get(long number) {
    if (number == 0) {
      return null;
    }
    return rmap.get((int) number).get();
  }

  @Override
  public int size() {
    return nextIndex - 1;
  }

  public boolean contains(T o) {
    return map.containsKey(o);
  }

  @Override
  public Iterator<T> iterator() {
    return map.keySet().iterator();
  }
}
