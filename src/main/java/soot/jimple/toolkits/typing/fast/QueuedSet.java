package soot.jimple.toolkits.typing.fast;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2008 Ben Bellamy 
 * 
 * All rights reserved.
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Ben Bellamy
 */
public class QueuedSet<E> {
  private Set<E> hs;
  private LinkedList<E> ll;

  public QueuedSet() {
    this.hs = new HashSet<E>();
    this.ll = new LinkedList<E>();
  }

  public QueuedSet(List<E> os) {
    this();
    for (E o : os) {
      this.ll.addLast(o);
      this.hs.add(o);
    }
  }

  public QueuedSet(QueuedSet<E> qs) {
    this(qs.ll);
  }

  public boolean isEmpty() {
    return this.ll.isEmpty();
  }

  public boolean addLast(E o) {
    boolean r = this.hs.contains(o);
    if (!r) {
      this.ll.addLast(o);
      this.hs.add(o);
    }
    return r;
  }

  public int addLast(List<E> os) {
    int r = 0;
    for (E o : os) {
      if (this.addLast(o)) {
        r++;
      }
    }
    return r;
  }

  public E removeFirst() {
    E r = this.ll.removeFirst();
    this.hs.remove(r);
    return r;
  }
}