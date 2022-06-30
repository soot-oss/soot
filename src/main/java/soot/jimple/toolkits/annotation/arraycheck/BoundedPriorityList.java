package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * BoundedPriorityList keeps a list in a priority queue. The order is decided by the initial list.
 *
 * @author Eric Bodden (adapted from Feng Qian's code)
 */
public class BoundedPriorityList implements Collection {
  protected final List fulllist;
  protected ArrayList worklist;

  public BoundedPriorityList(List list) {
    this.fulllist = list;
    this.worklist = new ArrayList(list);
  }

  @Override
  public boolean isEmpty() {
    return worklist.isEmpty();
  }

  public Object removeFirst() {
    return worklist.remove(0);
  }

  @Override
  public boolean add(Object toadd) {
    if (contains(toadd)) {
      return false;
    }

    /* it is not added to the end, but keep it in the order */
    int index = fulllist.indexOf(toadd);

    for (ListIterator worklistIter = worklist.listIterator(); worklistIter.hasNext();) {
      Object tocomp = worklistIter.next();
      int tmpidx = fulllist.indexOf(tocomp);
      if (index < tmpidx) {
        worklistIter.add(toadd);
        return true;
      }
    }

    return false;
  }

  // rest is only necessary to implement the Collection interface

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addAll(Collection c) {
    boolean addedSomething = false;
    for (Object o : c) {
      addedSomething |= add(o);
    }
    return addedSomething;
  }

  /**
   * {@inheritDoc}
   */
  public boolean addAll(int index, Collection c) {
    throw new RuntimeException("Not supported. You should use addAll(Collection) to keep priorities.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    worklist.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean contains(Object o) {
    return worklist.contains(o);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsAll(Collection c) {
    return worklist.containsAll(c);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator iterator() {
    return worklist.iterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Object o) {
    return worklist.remove(o);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeAll(Collection c) {
    return worklist.removeAll(c);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean retainAll(Collection c) {
    return worklist.retainAll(c);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return worklist.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] toArray() {
    return worklist.toArray();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] toArray(Object[] a) {
    return worklist.toArray(a);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return worklist.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    return worklist.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return worklist.hashCode();
  }
}
