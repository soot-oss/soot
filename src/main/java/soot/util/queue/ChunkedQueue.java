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

/**
 * A queue of Object's. One can add objects to the queue, and they are later read by a QueueReader. One can create arbitrary
 * numbers of QueueReader's for a queue, and each one receives all the Object's that are added. Only objects that have not
 * been read by all the QueueReader's are kept. A QueueReader only receives the Object's added to the queue <b>after</b> the
 * QueueReader was created.
 *
 * @author Ondrej Lhotak
 */
@SuppressWarnings("unchecked")
public class ChunkedQueue<E> {

  protected static final Object DELETED_CONST = new Object();

  protected static final int LENGTH = 60;
  protected Object[] q;
  protected int index;

  public ChunkedQueue() {
    q = new Object[LENGTH];
    index = 0;
  }

  /** Add an object to the queue. */
  public void add(E o) {
    if (o == null) {
      throw new IllegalArgumentException("Null is not allowed");
    }
    if (index == LENGTH - 1) {
      Object[] temp = new Object[LENGTH];
      q[index] = temp;
      q = temp;
      index = 0;
    }
    q[index++] = o;
  }

  /** Create reader which will read objects from the queue. */
  public QueueReader<E> reader() {
    return new QueueReader<E>((E[]) q, index);
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
