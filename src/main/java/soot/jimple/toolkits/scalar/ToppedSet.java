package soot.jimple.toolkits.scalar;

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

import java.util.Iterator;
import java.util.List;

import soot.toolkits.scalar.AbstractFlowSet;
import soot.toolkits.scalar.BoundedFlowSet;
import soot.toolkits.scalar.FlowSet;

/**
 * Represents information for flow analysis, adding a top element to a lattice. A FlowSet is an element of a lattice; this
 * lattice might be described by a FlowUniverse. If add, remove, size, isEmpty, toList and contains are implemented, the
 * lattice must be the powerset of some set.
 *
 */
public class ToppedSet<T> extends AbstractFlowSet<T> {
  FlowSet<T> underlyingSet;
  boolean isTop;

  public void setTop(boolean top) {
    isTop = top;
  }

  public boolean isTop() {
    return isTop;
  }

  public ToppedSet(FlowSet<T> under) {
    underlyingSet = under;
  }

  public ToppedSet<T> clone() {
    ToppedSet<T> newSet = new ToppedSet<T>(underlyingSet.clone());
    newSet.setTop(isTop());
    return newSet;
  }

  public void copy(FlowSet<T> d) {
    if (this == d) {
      return;
    }

    ToppedSet<T> dest = (ToppedSet<T>) d;
    dest.isTop = isTop;
    if (!isTop) {
      underlyingSet.copy(dest.underlyingSet);
    }
  }

  public FlowSet<T> emptySet() {
    return new ToppedSet<T>(underlyingSet.emptySet());
  }

  public void clear() {
    isTop = false;
    underlyingSet.clear();
  }

  public void union(FlowSet<T> o, FlowSet<T> d) {
    if (o instanceof ToppedSet && d instanceof ToppedSet) {
      ToppedSet<T> other = (ToppedSet<T>) o;
      ToppedSet<T> dest = (ToppedSet<T>) d;

      if (isTop()) {
        copy(dest);
        return;
      }

      if (other.isTop()) {
        other.copy(dest);
      } else {
        underlyingSet.union(other.underlyingSet, dest.underlyingSet);
        dest.setTop(false);
      }
    } else {
      super.union(o, d);
    }
  }

  public void intersection(FlowSet<T> o, FlowSet<T> d) {
    if (isTop()) {
      o.copy(d);
      return;
    }

    ToppedSet<T> other = (ToppedSet<T>) o, dest = (ToppedSet<T>) d;

    if (other.isTop()) {
      copy(dest);
      return;
    } else {
      underlyingSet.intersection(other.underlyingSet, dest.underlyingSet);
      dest.setTop(false);
    }
  }

  public void difference(FlowSet<T> o, FlowSet<T> d) {
    ToppedSet<T> other = (ToppedSet<T>) o, dest = (ToppedSet<T>) d;

    if (isTop()) {
      if (other.isTop()) {
        dest.clear();
      } else if (other.underlyingSet instanceof BoundedFlowSet) {
        ((BoundedFlowSet<T>) other.underlyingSet).complement(dest);
      } else {
        throw new RuntimeException("can't take difference!");
      }
    } else {
      if (other.isTop()) {
        dest.clear();
      } else {
        underlyingSet.difference(other.underlyingSet, dest.underlyingSet);
      }
    }
  }

  public boolean isEmpty() {
    if (isTop()) {
      return false;
    }
    return underlyingSet.isEmpty();
  }

  public int size() {
    if (isTop()) {
      throw new UnsupportedOperationException();
    }
    return underlyingSet.size();
  }

  public void add(T obj) {
    if (isTop()) {
      return;
    }
    underlyingSet.add(obj);
  }

  public void remove(T obj) {
    if (isTop()) {
      return;
    }
    underlyingSet.remove(obj);
  }

  public boolean contains(T obj) {
    if (isTop()) {
      return true;
    }
    return underlyingSet.contains(obj);
  }

  public List<T> toList() {
    if (isTop()) {
      throw new UnsupportedOperationException();
    }
    return underlyingSet.toList();
  }

  public boolean equals(Object o) {
    if (!(o instanceof ToppedSet)) {
      return false;
    }

    @SuppressWarnings("unchecked")
    ToppedSet<T> other = (ToppedSet<T>) o;
    if (other.isTop() != isTop()) {
      return false;
    }
    return underlyingSet.equals(other.underlyingSet);
  }

  public String toString() {
    if (isTop()) {
      return "{TOP}";
    } else {
      return underlyingSet.toString();
    }
  }

  @Override
  public Iterator<T> iterator() {
    if (isTop()) {
      throw new UnsupportedOperationException();
    }
    return underlyingSet.iterator();
  }

}
