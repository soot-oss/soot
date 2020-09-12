package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapNumberer<T> implements Numberer<T> {

  final Map<T, Integer> map = new HashMap<T, Integer>();
  final ArrayList<T> al = new ArrayList<T>();
  int nextIndex = 1;

  public MapNumberer() {
    al.add(null);
  }

  @Override
  public void add(T o) {
    if (!map.containsKey(o)) {
      map.put(o, nextIndex);
      al.add(o);
      nextIndex++;
    }
  }

  @Override
  public T get(long number) {
    return al.get((int) number);
  }

  @Override
  public long get(Object o) {
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
  public int size() {
    /* subtract 1 for null */
    return nextIndex - 1;
  }

  public boolean contains(Object o) {
    return map.containsKey(o);
  }

  @Override
  public boolean remove(T o) {
    Integer i = map.remove(o);
    if (i == null) {
      return false;
    } else {
      al.set(i, null);
      return true;
    }
  }
}
