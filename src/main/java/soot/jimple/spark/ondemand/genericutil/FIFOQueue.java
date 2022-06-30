package soot.jimple.spark.ondemand.genericutil;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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
 * A FIFO queue of objects, implemented as a circular buffer. NOTE: elements stored in the buffer should be non-null; this is
 * not checked for performance reasons.
 *
 * @author Manu Sridharan
 */
public final class FIFOQueue {

  /**
   * the buffer.
   */
  private Object[] _buf;

  /**
   * pointer to current top of buffer
   */
  private int _top;

  /**
   * point to current bottom of buffer, where things will be added invariant: after call to add / remove, should always point
   * to an empty slot in the buffer
   */
  private int _bottom;

  /**
   * @param initialSize_
   *          the initial size of the queue
   */
  public FIFOQueue(int initialSize_) {
    _buf = new Object[initialSize_];
  }

  public FIFOQueue() {
    this(10);
  }

  public boolean push(Object obj_) {
    return add(obj_);
  }

  /**
   * add an element to the bottom of the queue
   */
  public boolean add(Object obj_) {
    // Assert.chk(obj_ != null);
    // add the element
    _buf[_bottom] = obj_;
    // increment bottom, wrapping around if necessary
    _bottom = (_bottom == _buf.length - 1) ? 0 : _bottom + 1;
    // see if we need to increase the queue size
    if (_bottom == _top) {
      // allocate a new array and copy
      int oldLen = _buf.length;
      int newLen = oldLen * 2;
      // System.out.println("growing buffer to size " + newLen);
      Object[] newBuf = new Object[newLen];
      int topToEnd = oldLen - _top;
      int newTop = newLen - topToEnd;
      // copy from 0 to _top to beginning of new buffer,
      // _top to _buf.length to the end of the new buffer
      System.arraycopy(_buf, 0, newBuf, 0, _top);
      System.arraycopy(_buf, _top, newBuf, newTop, topToEnd);
      _buf = newBuf;
      _top = newTop;
      return true;
    }
    return false;
  }

  public Object pop() {
    return remove();
  }

  /**
   * remove the top element from the buffer
   */
  public Object remove() {
    // check if buffer is empty
    if (_bottom == _top) {
      return null;
    }
    Object ret = _buf[_top];
    // increment top, wrapping if necessary
    _top = (_top == _buf.length - 1) ? 0 : _top + 1;
    return ret;
  }

  public boolean isEmpty() {
    return _bottom == _top;
  }

  public String toString() {
    return _bottom + " " + _top;
  }

  public void clear() {
    _bottom = 0;
    _top = 0;
  }
}
