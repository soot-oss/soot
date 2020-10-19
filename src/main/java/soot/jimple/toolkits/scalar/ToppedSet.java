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

  protected final FlowSet<T> underlyingSet;
  protected boolean isTop;

  public ToppedSet(FlowSet<T> under) {
    underlyingSet = under;
  }

  @Override
  public ToppedSet<T> clone() {
    ToppedSet<T> newSet = new ToppedSet<T>(underlyingSet.clone());
    newSet.setTop(this.isTop());
    return newSet;
  }

  @Override
  public void copy(FlowSet<T> d) {
    if (this != d) {
      ToppedSet<T> dest = (ToppedSet<T>) d;
      dest.isTop = this.isTop;
      if (!this.isTop()) {
        this.underlyingSet.copy(dest.underlyingSet);
      }
    }
  }

  @Override
  public FlowSet<T> emptySet() {
    return new ToppedSet<T>(underlyingSet.emptySet());
  }

  @Override
  public void clear() {
    isTop = false;
    underlyingSet.clear();
  }

  @Override
  public void union(FlowSet<T> o, FlowSet<T> d) {
    if (o instanceof ToppedSet && d instanceof ToppedSet) {
      ToppedSet<T> other = (ToppedSet<T>) o;
      ToppedSet<T> dest = (ToppedSet<T>) d;

      if (this.isTop()) {
        this.copy(dest);
      } else if (other.isTop()) {
        other.copy(dest);
      } else {
        underlyingSet.union(other.underlyingSet, dest.underlyingSet);
        dest.setTop(false);
      }
    } else {
      super.union(o, d);
    }
  }

  @Override
  public void intersection(FlowSet<T> o, FlowSet<T> d) {
    if (this.isTop()) {
      o.copy(d);
      return;
    }

    ToppedSet<T> other = (ToppedSet<T>) o, dest = (ToppedSet<T>) d;
    if (other.isTop()) {
      this.copy(dest);
    } else {
      underlyingSet.intersection(other.underlyingSet, dest.underlyingSet);
      dest.setTop(false);
    }
  }

  @Override
  public void difference(FlowSet<T> o, FlowSet<T> d) {
    ToppedSet<T> other = (ToppedSet<T>) o, dest = (ToppedSet<T>) d;
    if (this.isTop()) {
      if (other.isTop()) {
        dest.clear();
      } else if (other.underlyingSet instanceof BoundedFlowSet) {
        ((BoundedFlowSet<T>) other.underlyingSet).complement(dest);
      } else {
        throw new RuntimeException("can't take difference!");
      }
    } else if (other.isTop()) {
      dest.clear();
    } else {
      underlyingSet.difference(other.underlyingSet, dest.underlyingSet);
    }
  }

  @Override
  public boolean isEmpty() {
    return this.isTop() ? false : underlyingSet.isEmpty();
  }

  @Override
  public int size() {
    if (this.isTop()) {
      throw new UnsupportedOperationException();
    }
    return underlyingSet.size();
  }

  @Override
  public void add(T obj) {
    if (!this.isTop()) {
      underlyingSet.add(obj);
    }
  }

  @Override
  public void remove(T obj) {
    if (!this.isTop()) {
      underlyingSet.remove(obj);
    }
  }

  @Override
  public boolean contains(T obj) {
    return this.isTop() ? true : underlyingSet.contains(obj);
  }

  @Override
  public List<T> toList() {
    if (this.isTop()) {
      throw new UnsupportedOperationException();
    }
    return underlyingSet.toList();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + this.underlyingSet.hashCode();
    hash = 97 * hash + (this.isTop() ? 1 : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    final ToppedSet<?> other = (ToppedSet<?>) obj;
    return (this.isTop() == other.isTop()) && this.underlyingSet.equals(other.underlyingSet);
  }

  @Override
  public String toString() {
    return isTop() ? "{TOP}" : underlyingSet.toString();
  }

  @Override
  public Iterator<T> iterator() {
    if (isTop()) {
      throw new UnsupportedOperationException();
    }
    return underlyingSet.iterator();
  }

  public void setTop(boolean top) {
    isTop = top;
  }

  public boolean isTop() {
    return isTop;
  }
}
