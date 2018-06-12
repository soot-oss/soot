package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Provides an implementation of a flow universe, wrapping collections.
 */
public class CollectionFlowUniverse<E> implements FlowUniverse<E> {
  Set<E> elements;

  public CollectionFlowUniverse(Collection<? extends E> elements) {
    this.elements = new HashSet<E>(elements);
  }

  public int size() {
    return elements.size();
  }

  public Iterator<E> iterator() {
    return elements.iterator();
  }

  @SuppressWarnings("unchecked")
  public E[] toArray() {
    return (E[]) elements.toArray();
  }
}
