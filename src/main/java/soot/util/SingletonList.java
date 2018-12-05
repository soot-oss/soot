package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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
 * A list containing exactly one object, immutable.
 *
 * @author Ondrej Lhotak
 */

@Deprecated
public class SingletonList<E> extends java.util.AbstractList<E> {
  private E o;

  public SingletonList(E o) {
    this.o = o;
  }

  public int size() {
    return 1;
  }

  public boolean contains(Object other) {
    return other.equals(o);
  }

  public E get(int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException("Singleton list; index = " + index);
    }
    return o;
  }
}
