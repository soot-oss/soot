package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
 * An unmodifiable version of the IterableSet class
 * 
 * @author Steven Arzt
 *
 */
public class UnmodifiableIterableSet<E> extends IterableSet<E> {

  public UnmodifiableIterableSet() {
    super();
  }

  /**
   * Creates a new unmodifiable iterable set as a copy of an existing one
   * 
   * @param original
   *          The original set to copy
   */
  public UnmodifiableIterableSet(IterableSet<E> original) {
    for (E e : original) {
      super.add(e);
    }
  }

  @Override
  public boolean add(E o) {
    throw new RuntimeException("This set cannot be modified");
  }

  @Override
  public boolean remove(Object o) {
    throw new RuntimeException("This set cannot be modified");
  }

  public boolean forceRemove(Object o) {
    return super.remove(o);
  }

}
