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

import java.util.Arrays;
import java.util.Iterator;

/**
 * Provides an implementation of a flow universe, wrapping arrays.
 */
public class ArrayFlowUniverse<E> implements FlowUniverse<E> {
  E[] elements;

  public ArrayFlowUniverse(E[] elements) {
    this.elements = elements;
  }

  public int size() {
    return elements.length;
  }

  public Iterator<E> iterator() {
    return Arrays.asList(elements).iterator();
  }

  public E[] toArray() {
    return elements;
  }
}
