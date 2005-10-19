/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *      modified 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.scalar;

import soot.*;
import soot.util.*;
import java.util.*;

/**
 * Represents information for flow analysis.  
 * A FlowSet is an element of a lattice; this lattice might be described by a
 * FlowUniverse.
 * If add, remove, size, isEmpty, toList and contains are implemented, the
 * lattice must be the powerset of some set.
 *
 * @see: FlowUniverse
 */
public interface FlowSet {
  /**
   * Clones the current FlowSet.
   */
  public Object clone();

  /** 
   * returns an empty set, most often more efficient than:
   * <code>((FlowSet)clone()).clear()</code>
   */
  public Object emptySet();

  /**
   * Copies the current FlowSet into dest.
   */
  public void copy(FlowSet dest);

  /** 
   * Sets this FlowSet to the empty set (more generally, the bottom element
   * of the lattice.) */
  public void clear();

  /**
   * Returns the union (join) of this FlowSet and <code>other</code>, putting
   * result into <code>this</code>. */
  public void union(FlowSet other);

  /** 
   * Returns the union (join) of this FlowSet and <code>other</code>, putting
   * result into <code>dest</code>. <code>dest</code>, <code>other</code> and
   * <code>this</code> could be the same object.
   */
  public void union(FlowSet other, FlowSet dest);

  /**
   * Returns the intersection (meet) of this FlowSet and <code>other</code>,
   * putting result into <code>this</code>.
   */
  public void intersection(FlowSet other);

  /**
   * Returns the intersection (meet) of this FlowSet and <code>other</code>,
   * putting result into <code>dest</code>. <code>dest</code>,
   * <code>other</code> and <code>this</code> could be the same object.
   */
  public void intersection(FlowSet other, FlowSet dest);

  /** 
   * Returns the set difference (this intersect ~other) of this FlowSet and
   * <code>other</code>, putting result into <code>this</code>.
   */
  public void difference(FlowSet other);

  /**
   * Returns the set difference (this intersect ~other) of this FlowSet and 
   * <code>other</code>, putting result into <code>dest</code>.
   * <code>dest</code>, <code>other</code> and <code>this</code> could be the
   * same object.
   */
  public void difference(FlowSet other, FlowSet dest);

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
  public void add(Object obj);

  /**
   * puts <code>this</code> union <code>obj</code> into <code>dest</code>.
   */
  public void add(Object obj, FlowSet dest);

  /**
   * Removes <code>obj</code> from <code>this</code>.
   */
  public void remove(Object obj);

  /**
   * Puts <code>this</code> minus <code>obj</code> into <code>dest</code>.
   */
  public void remove(Object obj, FlowSet dest);

  /**
   * Returns true if this FlowSet contains <code>obj</code>.
   */
  public boolean contains(Object obj);

  /**
   * returns an iterator over the elements of the flowSet. Note that the
   * iterator might be backed, and hence be faster in the creation, than doing
   * <code>toList().iterator()</code>.
   */
  public Iterator iterator();

  /**
   * Returns an unbacked list of contained objects for this FlowSet.
   */
  public List toList();
}

