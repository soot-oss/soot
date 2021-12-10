package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
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

/**
 * provides functional code for most of the methods. Subclasses are invited to provide a more efficient version. Most often
 * this will be done in the following way:<br>
 *
 * <pre>
 * public void yyy(FlowSet dest) {
 *   if (dest instanceof xxx) {
 *     blahblah;
 *   } else
 *     super.yyy(dest)
 * }
 * </pre>
 */
public abstract class AbstractFlowSet<T> implements FlowSet<T> {

  @Override
  public abstract AbstractFlowSet<T> clone();

  /**
   * implemented, but inefficient.
   */
  @Override
  public FlowSet<T> emptySet() {
    FlowSet<T> t = clone();
    t.clear();
    return t;
  }

  @Override
  public void copy(FlowSet<T> dest) {
    if (this == dest) {
      return;
    }
    dest.clear();
    for (T t : this) {
      dest.add(t);
    }
  }

  /**
   * implemented, but *very* inefficient.
   */
  @Override
  public void clear() {
    for (T t : this) {
      remove(t);
    }
  }

  @Override
  public void union(FlowSet<T> other) {
    if (this == other) {
      return;
    }
    union(other, this);
  }

  @Override
  public void union(FlowSet<T> other, FlowSet<T> dest) {
    if (dest != this && dest != other) {
      dest.clear();
    }

    if (dest != null && dest != this) {
      for (T t : this) {
        dest.add(t);
      }
    }

    if (other != null && dest != other) {
      for (T t : other) {
        dest.add(t);
      }
    }
  }

  @Override
  public void intersection(FlowSet<T> other) {
    if (this == other) {
      return;
    }
    intersection(other, this);
  }

  @Override
  public void intersection(FlowSet<T> other, FlowSet<T> dest) {
    if (dest == this && dest == other) {
      return;
    }
    FlowSet<T> elements = null;
    FlowSet<T> flowSet = null;
    if (dest == this) {
      /*
       * makes automaticly a copy of <code>this</code>, as it will be cleared
       */
      elements = this;
      flowSet = other;
    } else {
      /* makes a copy o <code>other</code>, as it might be cleared */
      elements = other;
      flowSet = this;
    }
    dest.clear();
    for (T t : elements) {
      if (flowSet.contains(t)) {
        dest.add(t);
      }
    }
  }

  @Override
  public void difference(FlowSet<T> other) {
    difference(other, this);
  }

  @Override
  public void difference(FlowSet<T> other, FlowSet<T> dest) {
    if (dest == this && dest == other) {
      dest.clear();
      return;
    }

    FlowSet<T> flowSet = (other == dest) ? other.clone() : other;
    dest.clear(); // now safe, since we have copies of this & other

    for (T t : this) {
      if (!flowSet.contains(t)) {
        dest.add(t);
      }
    }
  }

  @Override
  public abstract boolean isEmpty();

  @Override
  public abstract int size();

  @Override
  public abstract void add(T obj);

  @Override
  public void add(T obj, FlowSet<T> dest) {
    if (dest != this) {
      copy(dest);
    }
    dest.add(obj);
  }

  @Override
  public abstract void remove(T obj);

  @Override
  public void remove(T obj, FlowSet<T> dest) {
    if (dest != this) {
      copy(dest);
    }
    dest.remove(obj);
  }

  @Override
  public boolean isSubSet(FlowSet<T> other) {
    if (other == this) {
      return true;
    }

    for (T t : other) {
      if (!contains(t)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public abstract boolean contains(T obj);

  @Override
  public abstract Iterator<T> iterator();

  @Override
  public abstract List<T> toList();

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof FlowSet)) {
      return false;
    }
    FlowSet<T> other = (FlowSet<T>) o;
    if (size() != other.size()) {
      return false;
    }
    for (T t : this) {
      if (!other.contains(t)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (T t : this) {
      result += t.hashCode();
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer("{");

    boolean isFirst = true;
    for (T t : this) {
      if (!isFirst) {
        buffer.append(", ");
      }
      isFirst = false;

      buffer.append(t);
    }
    buffer.append("}");
    return buffer.toString();
  }
}
