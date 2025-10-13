package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import com.google.common.collect.Iterators;

import java.util.Iterator;

/**
 * Is an iterator returning all elements of an other iterator concatenated with one element.
 * @param <T>
 */
public class IteratorConcatElement<T> implements Iterator<T> {

  private Iterator<T> it;
  private T element;
  private boolean shownSingleElement;

  public IteratorConcatElement(Iterator<T> it, T element) {
    this.it = it;
    this.element = element;
  }

  public static <T> Iterator<T> v(Iterator<T> it, T element) {
    if (!it.hasNext()) {
      return Iterators.singletonIterator(element);
    } else {
      return new IteratorConcatElement<T>(it, element);
    }
  }

  @Override
  public boolean hasNext() {
    boolean b = it.hasNext();
    if (!b && !shownSingleElement) {
      return true;
    }
    return b;
  }

  @Override
  public T next() {
    if (!it.hasNext()) {
      if (!shownSingleElement) {
        shownSingleElement = true;
        return element;
      }
      throw new IllegalStateException("No more elements");
    }
    return it.next();
  }

}
