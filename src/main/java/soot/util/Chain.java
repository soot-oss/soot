package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Augmented data type guaranteeing O(1) insertion and removal from a set of ordered, unique elements.
 *
 * @param <E>
 *          element type
 */
public interface Chain<E> extends Collection<E>, Serializable {
  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  public void insertBefore(List<E> toInsert, E point);

  /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
  public void insertAfter(List<E> toInsert, E point);

  /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
  public void insertAfter(E toInsert, E point);

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  public void insertBefore(E toInsert, E point);

  /**
   * Inserts <code>toInsert</code> in the Chain before <code>point</code>. (It would probably be better to make Chain
   * implement List)
   */
  public void insertBefore(Chain<E> toInsert, E point);

  /**
   * Inserts <code>toInsert</code> in the Chain after <code>point</code>. (It would probably be better to make Chain
   * implement List)
   */
  public void insertAfter(Chain<E> toInsert, E point);

  /**
   * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
   * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
   *
   * @param toInsert
   *          the instrumentation to be added in the Chain
   * @param point_src
   *          the source point of an edge in CFG
   * @param point_tgt
   *          the target point of an edge
   */
  public void insertOnEdge(E toInsert, E point_src, E point_tgt);

  /**
   * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
   * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
   *
   * @param toInsert
   *          instrumentation to be added in the Chain
   * @param point_src
   *          the source point of an edge in CFG
   * @param point_tgt
   *          the target point of an edge
   */
  public void insertOnEdge(List<E> toInsert, E point_src, E point_tgt);

  /**
   * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
   * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
   *
   * @param toInsert
   *          instrumentation to be added in the Chain
   * @param point_src
   *          the source point of an edge in CFG
   * @param point_tgt
   *          the target point of an edge
   */
  public void insertOnEdge(Chain<E> toInsert, E point_src, E point_tgt);

  /** Replaces <code>out</code> in the Chain by <code>in</code>. */
  public void swapWith(E out, E in);

  /**
   * Removes the given object from this Chain. Parameter has to be of type {@link Object} to be compatible with the
   * {@link Collection} interface.
   */
  public boolean remove(Object u);

  /** Adds the given object at the beginning of the Chain. */
  public void addFirst(E u);

  /** Adds the given object at the end of the Chain. */
  public void addLast(E u);

  /** Removes the first object contained in this Chain. */
  public void removeFirst();

  /** Removes the last object contained in this Chain. */
  public void removeLast();

  /** Returns true if object <code>someObject</code> follows object <code>someReferenceObject</code> in the Chain. */
  public boolean follows(E someObject, E someReferenceObject);

  /** Returns the first object in this Chain. */
  public E getFirst();

  /** Returns the last object in this Chain. */
  public E getLast();

  /** Returns the object immediately following <code>point</code>. */
  public E getSuccOf(E point);

  /** Returns the object immediately preceding <code>point</code>. */
  public E getPredOf(E point);

  /**
   * Returns an iterator over a copy of this chain. This avoids ConcurrentModificationExceptions from being thrown if the
   * underlying Chain is modified during iteration. Do not use this to remove elements which have not yet been iterated over!
   */
  public Iterator<E> snapshotIterator();

  /** Returns an iterator over this Chain. */
  public Iterator<E> iterator();

  /** Returns an iterator over this Chain, starting at the given object. */
  public Iterator<E> iterator(E u);

  /** Returns an iterator over this Chain, starting at head and reaching tail (inclusive). */
  public Iterator<E> iterator(E head, E tail);

  /** Returns the size of this Chain. */
  public int size();

  /** Returns the number of times this chain has been modified. */
  long getModificationCount();

  /**
   * Gets all elements in the chain. There is no guarantee on sorting. On the other hand, the collection returned by this
   * method is thread-safe. You can iterate over it even in the case of concurrent modifications to the underlying chain.
   *
   * @return All elements in the chain in an unsorted collection
   */
  public Collection<E> getElementsUnsorted();

}
