package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Sable Research Group
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

import java.util.Collection;
import java.util.Set;

public class IterableSet<T> extends HashChain<T> implements Set<T> {

  public IterableSet(Collection<T> c) {
    super();
    addAll(c);
  }

  public IterableSet() {
    super();
  }

  @Override
  public boolean add(T o) {
    if (o == null) {
      throw new IllegalArgumentException("Cannot add \"null\" to an IterableSet.");
    }
    if (contains(o)) {
      return false;
    }
    return super.add(o);
  }

  @Override
  public boolean remove(Object o) {
    if (o == null || !contains(o)) {
      return false;
    }
    return super.remove(o);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (this == o) {
      return true;
    }

    if (!(o instanceof IterableSet)) {
      return false;
    }

    IterableSet<?> other = (IterableSet<?>) o;
    if (this.size() != other.size()) {
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
    int code = 23 * size();
    for (T t : this) {
      // use addition here to have hash code independent of order
      code += t.hashCode();
    }
    return code;
  }

  @Override
  public Object clone() {
    IterableSet<T> s = new IterableSet<T>();
    s.addAll(this);
    return s;
  }

  public boolean isSubsetOf(IterableSet<T> other) {
    if (other == null) {
      throw new IllegalArgumentException("Cannot set compare an IterableSet with \"null\".");
    }

    if (this.size() > other.size()) {
      return false;
    }
    for (T t : this) {
      if (!other.contains(t)) {
        return false;
      }
    }
    return true;
  }

  public boolean isSupersetOf(IterableSet<T> other) {
    if (other == null) {
      throw new IllegalArgumentException("Cannot set compare an IterableSet with \"null\".");
    }

    if (this.size() < other.size()) {
      return false;
    }
    for (T t : other) {
      if (!contains(t)) {
        return false;
      }
    }
    return true;
  }

  public boolean isStrictSubsetOf(IterableSet<T> other) {
    if (other == null) {
      throw new IllegalArgumentException("Cannot set compare an IterableSet with \"null\".");
    }

    if (this.size() >= other.size()) {
      return false;
    }
    return isSubsetOf(other);
  }

  public boolean isStrictSupersetOf(IterableSet<T> other) {
    if (other == null) {
      throw new IllegalArgumentException("Cannot set compare an IterableSet with \"null\".");
    }

    if (this.size() <= other.size()) {
      return false;
    }
    return isSupersetOf(other);
  }

  public boolean intersects(IterableSet<T> other) {
    if (other == null) {
      throw new IllegalArgumentException("Cannot set intersect an IterableSet with \"null\".");
    }

    if (other.size() < this.size()) {
      for (T t : other) {
        if (this.contains(t)) {
          return true;
        }
      }
    } else {
      for (T t : this) {
        if (other.contains(t)) {
          return true;
        }
      }
    }
    return false;
  }

  public IterableSet<T> intersection(IterableSet<T> other) {
    if (other == null) {
      throw new IllegalArgumentException("Cannot set intersect an IterableSet with \"null\".");
    }

    IterableSet<T> c = new IterableSet<T>();
    if (other.size() < this.size()) {
      for (T t : other) {
        if (this.contains(t)) {
          c.add(t);
        }
      }
    } else {
      for (T t : this) {
        if (other.contains(t)) {
          c.add(t);
        }
      }
    }
    return c;
  }

  public IterableSet<T> union(IterableSet<T> other) {
    if (other == null) {
      throw new IllegalArgumentException("Cannot set union an IterableSet with \"null\".");
    }

    IterableSet<T> c = new IterableSet<T>();
    c.addAll(this);
    c.addAll(other);
    return c;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    for (T t : this) {
      b.append(t.toString()).append('\n');
    }
    return b.toString();
  }

  public UnmodifiableIterableSet<T> asUnmodifiable() {
    return new UnmodifiableIterableSet<T>(this);
  }
}
