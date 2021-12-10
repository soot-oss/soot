package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Eric Bodden
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
 * An optimized kind of {@link IdentityHashSet} that only holds two objects. (Allows for faster comparison.)
 * 
 * @author Eric Bodden
 */
public class BinaryIdentitySet<T> {

  protected final T o1;
  protected final T o2;
  protected final int hashCode;

  public BinaryIdentitySet(T o1, T o2) {
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
    int result = 1;
    // must be commutative
    result += System.identityHashCode(o1);
    result += System.identityHashCode(o2);
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
    final BinaryIdentitySet<?> other = (BinaryIdentitySet<?>) obj;
    // must be commutative
    return (this.o1 == other.o1 || this.o1 == other.o2) && (this.o2 == other.o2 || this.o2 == other.o1);
  }

  public T getO1() {
    return o1;
  }

  public T getO2() {
    return o2;
  }

  @Override
  public String toString() {
    return "IdentityPair " + o1 + "," + o2;
  }
}