package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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
 * Just a pair of arbitrary objects.
 * 
 * @author Ondrej Lhotak
 * @author Manu Sridharan (genericized it)
 */
public class IdentityPair<T, U> {

  protected final T o1;
  protected final U o2;
  protected final int hashCode;

  public IdentityPair(T o1, U o2) {
    this.o1 = o1;
    this.o2 = o2;
    this.hashCode = computeHashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return hashCode;
  }

  private int computeHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + System.identityHashCode(o1);
    result = prime * result + System.identityHashCode(o2);
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    final IdentityPair<?, ?> other = (IdentityPair<?, ?>) obj;
    return this.o1 == other.o1 && this.o2 == other.o2;
  }

  public T getO1() {
    return o1;
  }

  public U getO2() {
    return o2;
  }

  @Override
  public String toString() {
    return "IdentityPair " + o1 + "," + o2;
  }
}