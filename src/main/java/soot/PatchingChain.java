package soot;

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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import soot.util.Chain;

/**
 * An implementation of a Chain which can contain only Units, and handles patching to deal with element insertions and
 * removals. This is done by calling Unit.redirectJumpsToThisTo at strategic times.
 */
@SuppressWarnings("serial")
public class PatchingChain<E extends Unit> extends AbstractCollection<E> implements Chain<E> {
  protected Chain<E> innerChain;

  /** Constructs a PatchingChain from the given Chain. */
  public PatchingChain(Chain<E> aChain) {
    innerChain = aChain;
  }

  /**
   * Returns the inner chain used by the PatchingChain. In general, this should not be used. However, direct access to the
   * inner chain may be necessary if you wish to perform certain operations (such as control-flow manipulations) without
   * interference from the patching algorithms.
   **/
  public Chain<E> getNonPatchingChain() {
    return innerChain;
  }

  /** Adds the given object to this Chain. */
  public boolean add(E o) {
    return innerChain.add(o);
  }

  /** Replaces <code>out</code> in the Chain by <code>in</code>. */
  public void swapWith(E out, E in) {
    innerChain.swapWith(out, in);
    out.redirectJumpsToThisTo(in);
  }

  /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
  public void insertAfter(E toInsert, E point) {
    innerChain.insertAfter(toInsert, point);
  }

  /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
  public void insertAfter(List<E> toInsert, E point) {
    innerChain.insertAfter(toInsert, point);
  }

  public void insertAfter(Chain<E> toInsert, E point) {
    innerChain.insertAfter(toInsert, point);
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  public void insertBefore(List<E> toInsert, E point) {
    E previousPoint = point;

    for (int i = toInsert.size() - 1; i >= 0; i--) {
      E o = toInsert.get(i);
      insertBeforeNoRedirect(o, previousPoint);
      previousPoint = o;
    }
    point.redirectJumpsToThisTo(toInsert.get(0));
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  public void insertBefore(Chain<E> toInsert, E point) {
    Object[] obj = toInsert.toArray();
    E previousPoint = point;
    for (int i = obj.length - 1; i >= 0; i--) {
      @SuppressWarnings("unchecked")
      E o = (E) obj[i];
      insertBefore(o, previousPoint);
      previousPoint = o;
    }
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  public void insertBefore(E toInsert, E point) {
    point.redirectJumpsToThisTo(toInsert);
    innerChain.insertBefore(toInsert, point);
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code> WITHOUT redirecting jumps. */
  public void insertBeforeNoRedirect(E toInsert, E point) {
    innerChain.insertBefore(toInsert, point);
  }

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
  public void insertOnEdge(E toInsert, E point_src, E point_tgt) {
    innerChain.insertOnEdge(toInsert, point_src, point_tgt);
  }

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
  public void insertOnEdge(List<E> toInsert, E point_src, E point_tgt) {
    innerChain.insertOnEdge(toInsert, point_src, point_tgt);
  }

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
  public void insertOnEdge(Chain<E> toInsert, E point_src, E point_tgt) {
    innerChain.insertOnEdge(toInsert, point_src, point_tgt);
  }

  /** Returns true if object <code>a</code> follows object <code>b</code> in the Chain. */
  public boolean follows(E a, E b) {
    return innerChain.follows(a, b);
  }

  /** Removes the given object from this Chain. */
  @SuppressWarnings("unchecked")
  public boolean remove(Object obj) {
    boolean res = false;

    if (contains(obj)) {
      Unit successor = getSuccOf((E) obj);
      if (successor == null) {
        successor = getPredOf((E) obj);
      }

      // Note that redirecting to the last unit in the method
      // like this is probably incorrect when dealing with a Trap.
      // I.e., let's say that the final unit in the method used to
      // be U10, preceded by U9, and that there was a Trap which
      // returned U10 as getEndUnit(). I.e., before the trap covered U9.
      // When we redirect the Trap's end unit to U9, the trap will no
      // longer cover U9. I know this is incorrect, but I'm not sure how
      // to fix it, so I'm leaving this comment in the hopes that some
      // future maintainer will see the right course to take.

      res = innerChain.remove(obj);

      ((E) obj).redirectJumpsToThisTo(successor);
    }

    return res;
  }

  /** Returns true if this patching chain contains the specified element. */
  public boolean contains(Object u) {
    return innerChain.contains(u);
  }

  /** Adds the given object at the beginning of the Chain. */
  public void addFirst(E u) {
    innerChain.addFirst(u);
  }

  /** Adds the given object at the end of the Chain. */
  public void addLast(E u) {
    innerChain.addLast(u);
  }

  /** Removes the first object from this Chain. */
  public void removeFirst() {
    remove(innerChain.getFirst());
  }

  /** Removes the last object from this Chain. */
  public void removeLast() {
    remove(innerChain.getLast());
  }

  /** Returns the first object in this Chain. */
  public E getFirst() {
    return innerChain.getFirst();
  }

  /** Returns the last object in this Chain. */
  public E getLast() {
    return innerChain.getLast();
  }

  /** Returns the object immediately following <code>point</code>. */
  public E getSuccOf(E point) {
    return innerChain.getSuccOf(point);
  }

  /** Returns the object immediately preceding <code>point</code>. */
  public E getPredOf(E point) {
    return innerChain.getPredOf(point);
  }

  protected class PatchingIterator implements Iterator<E> {
    protected Iterator<E> innerIterator = null;
    protected E lastObject;
    protected boolean state = false;

    protected PatchingIterator(Chain<E> innerChain) {
      innerIterator = innerChain.iterator();
    }

    protected PatchingIterator(Chain<E> innerChain, E u) {
      innerIterator = innerChain.iterator(u);
    }

    protected PatchingIterator(Chain<E> innerChain, E head, E tail) {
      innerIterator = innerChain.iterator(head, tail);
    }

    public boolean hasNext() {
      return innerIterator.hasNext();
    }

    public E next() {
      lastObject = innerIterator.next();
      state = true;
      return lastObject;
    }

    public void remove() {
      if (!state) {
        throw new IllegalStateException("remove called before first next() call");
      }

      Unit successor;

      if ((successor = getSuccOf(lastObject)) == null) {
        successor = getPredOf(lastObject);
        // Note that redirecting to the last unit in the method
        // like this is probably incorrect when dealing with a Trap.
        // I.e., let's say that the final unit in the method used to
        // be U10, preceded by U9, and that there was a Trap which
        // returned U10 as getEndUnit(). I.e., before the trap covered U9.
        // When we redirect the Trap's end unit to U9, the trap will no
        // longer cover U9. I know this is incorrect, but I'm not sure how
        // to fix it, so I'm leaving this comment in the hopes that some
        // future maintainer will see the right course to take.
      }

      innerIterator.remove();

      lastObject.redirectJumpsToThisTo(successor);
    }
  }

  /**
   * Returns an iterator over a copy of this chain. This avoids ConcurrentModificationExceptions from being thrown if the
   * underlying Chain is modified during iteration. Do not use this to remove elements which have not yet been iterated over!
   */
  public Iterator<E> snapshotIterator() {
    List<E> l = new LinkedList<E>(this);
    return l.iterator();
  }

  /** Returns an iterator over this Chain. */
  public Iterator<E> iterator() {
    return new PatchingIterator(innerChain);
  }

  /** Returns an iterator over this Chain, starting at the given object. */
  public Iterator<E> iterator(E u) {
    return new PatchingIterator(innerChain, u);
  }

  /** Returns an iterator over this Chain, starting at head and reaching tail (inclusive). */
  public Iterator<E> iterator(E head, E tail) {
    return new PatchingIterator(innerChain, head, tail);
  }

  /** Returns the size of this Chain. */
  public int size() {
    return innerChain.size();
  }

  @Override
  /** Returns the number of times this chain has been modified. */
  public long getModificationCount() {
    return innerChain.getModificationCount();
  }

  @Override
  public Collection<E> getElementsUnsorted() {
    return innerChain.getElementsUnsorted();
  }
}
