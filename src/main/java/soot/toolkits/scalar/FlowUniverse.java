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

import java.util.Iterator;

/**
 * Provides an interface of a flow universe, used by an implementation of BoundedFlowSet to do complementation.
 */
public interface FlowUniverse<E> extends Iterable<E> {

  /**
   * returns the number of elements of the universe.
   *
   * @return the size of the universe.
   */
  public int size();

  /**
   * returns an iterator over the elements of the universe.
   *
   * @return an Iterator over the elements.
   */
  public Iterator<E> iterator();

  /**
   * returns the elements of the universe in form of an array.<br>
   * The returned array could be backed or not. If you want to be sure that it is unbacked, clone() it.
   *
   * @return the elements of the universe.
   */
  public E[] toArray();
}
