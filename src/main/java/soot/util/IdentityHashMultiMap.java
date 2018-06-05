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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * An identity-based version of the MultiMap.
 *
 * @author Steven Arzt
 */

public class IdentityHashMultiMap<K, V> extends HashMultiMap<K, V> {

  private static final long serialVersionUID = 4960774381646981495L;

  @Override
  protected Map<K, Set<V>> createMap(int initialSize) {
    return new IdentityHashMap<K, Set<V>>(initialSize);
  }

  @SuppressWarnings("deprecation")
  @Override
  protected Set<V> newSet() {
    return new IdentityHashSet<V>();
  }

}
