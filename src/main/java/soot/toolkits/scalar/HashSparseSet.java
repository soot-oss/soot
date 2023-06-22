package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Reference implementation for a FlowSet. Items are stored in an HashSet which is a more scalable
 * version of ArraySparseSet.
 */
public class HashSparseSet<T> extends AbstractFlowSet<T> {
  protected LinkedHashSet<T> elements;

  public HashSparseSet() {
    @SuppressWarnings("unchecked")
    LinkedHashSet<T> newElements = new LinkedHashSet<T>();
    elements = newElements;
  }

  private HashSparseSet(HashSparseSet<T> other) {
    elements = new LinkedHashSet<T>(other.elements);
  }

  /**
   * Returns true if flowSet is the same type of flow set as this.
   */
  private boolean sameType(Object flowSet) {
    return (flowSet instanceof HashSparseSet);
  }

  @Override
  public HashSparseSet<T> clone() {
    return new HashSparseSet<T>(this);
  }

  @Override
  public FlowSet<T> emptySet() {
    return new HashSparseSet<T>();
  }

  @Override
  public void clear() {
    elements.clear();
    ;
  }

  @Override
  public int size() {
    return elements.size();
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  /**
   * Returns a unbacked list of elements in this set.
   */
  @Override
  public List<T> toList() {
    return new ArrayList<T>(elements);
  }

  /*
   * Expand array only when necessary, pointed out by Florian Loitsch March 08, 2002
   */
  @Override
  public void add(T e) {
    elements.add(e);
  }

  @Override
  public void remove(Object obj) {
    elements.remove(obj);
  }

  public void remove(int idx) {
    Iterator<T> itr = elements.iterator();
    int currentIndex = 0;
    Object elem = null;
    while (itr.hasNext()) {
      elem = itr.next();
      if (currentIndex == idx) {
        break;
      }
      currentIndex++;
    }
    if (elem != null) {
      elements.remove(elem);
    }
  }

  @Override
  public void union(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      HashSparseSet<T> other = (HashSparseSet<T>) otherFlow;
      HashSparseSet<T> dest = (HashSparseSet<T>) destFlow;
      dest.elements.addAll(this.elements);
      dest.elements.addAll(other.elements);
    } else {
      super.union(otherFlow, destFlow);
    }
  }

  @Override
  public void intersection(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      HashSparseSet<T> other = (HashSparseSet<T>) otherFlow;
      HashSparseSet<T> dest = (HashSparseSet<T>) destFlow;
      HashSparseSet<T> workingSet = new HashSparseSet<>(this);
      workingSet.elements.retainAll(other.elements);
      dest.elements = workingSet.elements;
    } else {
      super.intersection(otherFlow, destFlow);
    }
  }

  @Override
  public void difference(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      HashSparseSet<T> other = (HashSparseSet<T>) otherFlow;
      HashSparseSet<T> dest = (HashSparseSet<T>) destFlow;
      HashSparseSet<T> workingSet;

      if (dest == other || dest == this) {
        workingSet = new HashSparseSet<T>();
      } else {
        workingSet = dest;
        workingSet.clear();
      }
      for (Object elem : this.elements) {
        if (!other.elements.contains(elem)) {
          workingSet.elements.add((T) elem);
        }
      }
      if (workingSet != dest) {
        workingSet.copy(dest);
      }
    } else {
      super.difference(otherFlow, destFlow);
    }
  }

  @Override
  public boolean contains(Object obj) {
    return elements.contains(obj);
  }

  @Override
  public boolean equals(Object otherFlow) {
    if (sameType(otherFlow)) {
      @SuppressWarnings("unchecked")
      HashSparseSet<T> other = (HashSparseSet<T>) otherFlow;
      return this.elements.equals(other.elements);
    } else {
      return super.equals(otherFlow);
    }
  }

  @Override
  public void copy(FlowSet<T> destFlow) {
    if (sameType(destFlow)) {
      HashSparseSet<T> dest = (HashSparseSet<T>) destFlow;
      dest.elements.clear();
      dest.elements.addAll(this.elements);
    } else {
      super.copy(destFlow);
    }
  }

  @Override
  public Iterator<T> iterator() {
    return elements.iterator();
  }
}
