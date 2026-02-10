package soot.util.queue;

import java.util.Collection;
import java.util.Collections;

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

import soot.util.Invalidable;

/**
 * A queue of Object's. One can add objects to the queue, and they are later read by a QueueReader. One can create arbitrary
 * numbers of QueueReader's for a queue, and each one receives all the Object's that are added. Only objects that have not
 * been read by all the QueueReader's are kept. A QueueReader only receives the Object's added to the queue <b>after</b> the
 * QueueReader was created.
 *
 * This QueueReader does <emph>not</emph> accept <code>null</code> values. 
 * @author Ondrej Lhotak
 */
public class QueueReader<E> implements java.util.Iterator<E> {
  protected E[] q;
  protected int index;

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
        //this is the case when someone concurrently invalidates 
        //the rest of the elements
        return null;
      }
      if (index == q.length - 1) {
        q = (E[]) q[index];
        index = 0;
        if (q[index] == null) {
          //this is the case when someone concurrently invalidates 
          //the rest of the elements
          return null;
        }
      }
      ret = q[index];
      index++;
    } while (skip(ret));
    return (E) ret;
  }

  protected boolean skip(Object ret) {
    if (ret instanceof Invalidable) {
      final Invalidable invalidable = (Invalidable) ret;
      if (invalidable.isInvalid()) {
        return true;
      }
    }
    return ret == ChunkedQueue.DELETED_CONST;
  }

  /** Returns true iff there is currently another object in the queue. */
  @SuppressWarnings("unchecked")
  public boolean hasNext() {
    do {
      E ret = q[index];
      if (ret == null) {
        return false;
      }
      if (index == q.length - 1) {
        q = (E[]) ret;
        index = 0;
        if (q[index] == null) {
          return false;
        }
      }
      if (skip(ret)) {
        index++;
      } else {
        return true;
      }
    } while (true);
  }

  /**
   * Removes an element from the underlying queue. This operation can only delete elements that have not yet been consumed by
   * this reader.
   *
   * @param o
   *          The element to remove
   */
  public void remove(E o) {
    if (o instanceof Invalidable) {
      ((Invalidable) o).invalidate();
      return;
    }
    remove(Collections.singleton(o));
  }

  /**
   * Removes elements from the underlying queue. This operation can only delete elements that have not yet been consumed by
   * this reader.
   *
   * @param toRemove
   *          The elements to remove
   */
  @SuppressWarnings("unchecked")
  public void remove(Collection<E> toRemove) {
    boolean allInvalidable = true;
    for (E o : toRemove) {
      if (!(o instanceof Invalidable)) {
        allInvalidable = false;
        continue;
      }

      ((Invalidable) o).invalidate();
    }
    if (allInvalidable) {
      return;
    }
    int idx = 0;
    Object[] curQ = q;
    while (curQ[idx] != null) {
      // Do we need to switch to a new list?
      if (idx == curQ.length - 1) {
        curQ = (E[]) curQ[idx];
        idx = 0;
      }

      // Is this the element to delete?
      if (toRemove.contains(curQ[idx])) {
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    boolean isFirst = true;

    int idx = index;
    Object[] curArray = q;
    while (idx < curArray.length) {
      Object curObj = curArray[idx];
      if (curObj == null) {
        break;
      }
      if (isFirst) {
        isFirst = false;
      } else {
        sb.append(", ");
      }
      if (curObj instanceof Object[]) {
        curArray = (Object[]) curObj;
        idx = 0;
      } else {
        sb.append(curObj.toString());
        idx++;
      }
    }
    sb.append("]");
    return sb.toString();
  }

}
