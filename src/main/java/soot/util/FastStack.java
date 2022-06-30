package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville
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

/**
 * A fast, but simple stack implementation. Sadly, java's own stack implementation synchronizes and as such is a bit slower.
 * 
 * Note that this implementation does not perform error checking, however, the original implementation would also have thrown
 * an exception in case this implementation throws an exception. It's just that the exception text differs.
 * 
 * @param <T>
 *          The elements of the stack
 * @author Marc Miltenberger
 */
public class FastStack<T> extends ArrayList<T> {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a new stack
   */
  public FastStack() {
  }

  /**
   * Creates a new stack with the given initial size
   * 
   * @param initialSize
   *          the initial size
   */
  public FastStack(int initialSize) {
    super(initialSize);
  }

  /**
   * Returns the last item on the stack or throws an exception of there is none.
   * 
   * @return the last item on the stack
   */
  public T peek() {
    return get(size() - 1);
  }

  /**
   * Pushes an item onto the stack
   * 
   * @param t
   *          the item
   */
  public void push(T t) {
    add(t);
  }

  /**
   * Returns and removes the last item from the stack. Throws an exception of there is none.
   * 
   * @return the last item on the stack, which got pop-ed.
   */
  public T pop() {
    return remove(size() - 1);
  }

  /**
   * Returns true if and only if the stack is empty
   * 
   * @return true if and only if the stack is empty
   */
  public boolean empty() {
    return size() == 0;
  }
}
