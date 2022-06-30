package soot.jimple.spark.ondemand.genericutil;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
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

public class UnorderedPair<U, V> {

  public U o1;
  public V o2;

  public UnorderedPair(U o1, V o2) {
    this.o1 = o1;
    this.o2 = o2;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    if (obj != null && obj.getClass() == UnorderedPair.class) {
      UnorderedPair u = (UnorderedPair) obj;
      return (u.o1.equals(o1) && u.o2.equals(o2)) || (u.o1.equals(o2) && u.o2.equals(o1));
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return o1.hashCode() + o2.hashCode();
  }

  public String toString() {
    return "{" + o1.toString() + ", " + o2.toString() + "}";
  }

}