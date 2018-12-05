package soot.util.queue;

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

import java.util.NoSuchElementException;

/**
 * A queue of Object's. One can add objects to the queue, and they are later read by a QueueReader. One can create arbitrary
 * numbers of QueueReader's for a queue, and each one receives all the Object's that are added. Only objects that have not
 * been read by all the QueueReader's are kept. A QueueReader only receives the Object's added to the queue <b>after</b> the
 * QueueReader was created.
 *
 * @author Ondrej Lhotak
 */
public class QueueReader<E> implements java.util.Iterator<E> {
  private E[] q;
  private int index;

  protected QueueReader(E[] q, int index) {
    this.q = q;
    this.index = index;
  }

  /**
   * Returns (and removes) the next object in the queue, or null if there are none.
   */
  @SuppressWarnings("unchecked")
  public E next() {
    Object ret = null;
    do {
      if (q[index] == null) {
        throw new NoSuchElementException();
      }
      if (index == q.length - 1) {
        q = (E[]) q[index];
        index = 0;
        if (q[index] == null) {
          throw new NoSuchElementException();
        }
      }
      ret = q[index];
      if (ret == ChunkedQueue.NULL_CONST) {
        ret = null;
      }
      index++;
    } while (ret == ChunkedQueue.DELETED_CONST);
    return (E) ret;
  }

  /** Returns true iff there is currently another object in the queue. */
  @SuppressWarnings("unchecked")
  public boolean hasNext() {
    do {
      if (q[index] == null) {
        return false;
      }
      if (index == q.length - 1) {
        q = (E[]) q[index];
        index = 0;
        if (q[index] == null) {
          return false;
        }
      }
      if (q[index] == ChunkedQueue.DELETED_CONST) {
        index++;
      } else {
        break;
      }
    } while (true);
    return true;
  }

  /**
   * Removes an element from the underlying queue. This operation can only delete elements that have not yet been consumed by
   * this reader.
   *
   * @param o
   *          The element to remove
   */
  @SuppressWarnings("unchecked")
  public void remove(E o) {
    int idx = 0;
    Object[] curQ = q;
    while (curQ[idx] != null) {
      // Do we need to switch to a new list?
      if (idx == curQ.length - 1) {
        curQ = (E[]) curQ[idx];
        idx = 0;
      }

      // Is this the element to delete?
      if (o.equals(curQ[idx])) {
        curQ[idx] = ChunkedQueue.DELETED_CONST;
      }

      // Next element
      idx++;
    }
  }

  @SuppressWarnings("unchecked")
  public void remove() {
    q[index - 1] = (E) ChunkedQueue.DELETED_CONST;
  }

  public QueueReader<E> clone() {
    return new QueueReader<E>(q, index);
  }
}
