package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 *      modified 2002 Florian Loitsch
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
 * Represents information for flow analysis. A FlowSet is an element of a lattice; this lattice might be described by a
 * FlowUniverse. If add, remove, size, isEmpty, toList and contains are implemented, the lattice must be the powerset of some
 * set.
 *
 * @see: FlowUniverse
 */
public interface FlowSet<T> extends Iterable<T> {
  /**
   * Clones the current FlowSet.
   */
  public FlowSet<T> clone();

  /**
   * returns an empty set, most often more efficient than: <code>((FlowSet)clone()).clear()</code>
   */
  public FlowSet<T> emptySet();

  /**
   * Copies the current FlowSet into dest.
   */
  public void copy(FlowSet<T> dest);

  /**
   * Sets this FlowSet to the empty set (more generally, the bottom element of the lattice.)
   */
  public void clear();

  /**
   * Returns the union (join) of this FlowSet and <code>other</code>, putting result into <code>this</code>.
   */
  public void union(FlowSet<T> other);

  /**
   * Returns the union (join) of this FlowSet and <code>other</code>, putting result into <code>dest</code>.
   * <code>dest</code>, <code>other</code> and <code>this</code> could be the same object.
   */
  public void union(FlowSet<T> other, FlowSet<T> dest);

  /**
   * Returns the intersection (meet) of this FlowSet and <code>other</code>, putting result into <code>this</code>.
   */
  public void intersection(FlowSet<T> other);

  /**
   * Returns the intersection (meet) of this FlowSet and <code>other</code>, putting result into <code>dest</code>.
   * <code>dest</code>, <code>other</code> and <code>this</code> could be the same object.
   */
  public void intersection(FlowSet<T> other, FlowSet<T> dest);

  /**
   * Returns the set difference (this intersect ~other) of this FlowSet and <code>other</code>, putting result into
   * <code>this</code>.
   */
  public void difference(FlowSet<T> other);

  /**
   * Returns the set difference (this intersect ~other) of this FlowSet and <code>other</code>, putting result into
   * <code>dest</code>. <code>dest</code>, <code>other</code> and <code>this</code> could be the same object.
   */
  public void difference(FlowSet<T> other, FlowSet<T> dest);

  /**
   * Returns true if this FlowSet is the empty set.
   */
  public boolean isEmpty();

  /* The following methods force the FlowSet to be a powerset. */

  /**
   * Returns the size of the current FlowSet.
   */
  public int size();

  /**
   * Adds <code>obj</code> to <code>this</code>.
   */
  public void add(T obj);

  /**
   * puts <code>this</code> union <code>obj</code> into <code>dest</code>.
   */
  public void add(T obj, FlowSet<T> dest);

  /**
   * Removes <code>obj</code> from <code>this</code>.
   */
  public void remove(T obj);

  /**
   * Puts <code>this</code> minus <code>obj</code> into <code>dest</code>.
   */
  public void remove(T obj, FlowSet<T> dest);

  /**
   * Returns true if this FlowSet contains <code>obj</code>.
   */
  public boolean contains(T obj);

  /**
   * Returns true if the <code>other</code> FlowSet is a subset of <code>this</code> FlowSet.
   */
  public boolean isSubSet(FlowSet<T> other);

  /**
   * returns an iterator over the elements of the flowSet. Note that the iterator might be backed, and hence be faster in the
   * creation, than doing <code>toList().iterator()</code>.
   */
  public Iterator<T> iterator();

  /**
   * Returns an unbacked list of contained objects for this FlowSet.
   */
  public List<T> toList();
}
