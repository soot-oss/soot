package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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

/** A Lisp-style cons cell. */
public final class Cons<U, V> {
  final private U car;
  final private V cdr;

  public Cons(U car, V cdr) {
    this.car = car;
    this.cdr = cdr;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((car == null) ? 0 : car.hashCode());
    result = prime * result + ((cdr == null) ? 0 : cdr.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    @SuppressWarnings("unchecked")
    Cons<U, V> other = (Cons<U, V>) obj;
    if (car == null) {
      if (other.car != null) {
        return false;
      }
    } else if (!car.equals(other.car)) {
      return false;
    }
    if (cdr == null) {
      if (other.cdr != null) {
        return false;
      }
    } else if (!cdr.equals(other.cdr)) {
      return false;
    }
    return true;
  }

  public U car() {
    return car;
  }

  public V cdr() {
    return cdr;
  }

  @Override
  public String toString() {
    return car.toString() + "," + cdr.toString();
  }
}
