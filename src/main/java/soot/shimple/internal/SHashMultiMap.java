package soot.shimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Navindra Umanee
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

import java.util.LinkedHashSet;
import java.util.Set;

import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * A map with ordered sets as values, HashMap implementation.
 *
 * @author Navindra Umanee
 **/
public class SHashMultiMap<K, V> extends HashMultiMap<K, V> {

  private static final long serialVersionUID = -860669798578291979L;

  public SHashMultiMap() {
    super();
  }

  public SHashMultiMap(MultiMap<K, V> m) {
    super(m);
  }

  protected Set<V> newSet() {
    return new LinkedHashSet<V>(4);
  }
}
