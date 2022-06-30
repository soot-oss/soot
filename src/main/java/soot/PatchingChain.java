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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import soot.util.Chain;

/**
 * An implementation of a Chain which can contain only Units, and handles patching to deal with element insertions and
 * removals. This is done by calling Unit.redirectJumpsToThisTo at strategic times.
 */
@SuppressWarnings("serial")
public class PatchingChain<E extends Unit> extends AbstractCollection<E> implements Chain<E> {

  protected final Chain<E> innerChain;

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
  @Override
  public boolean add(E o) {
    return innerChain.add(o);
  }

  /** Replaces <code>out</code> in the Chain by <code>in</code>. */
  @Override
  public void swapWith(E out, E in) {
    innerChain.swapWith(out, in);
    out.redirectJumpsToThisTo(in);
  }

  /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
  @Override
  public void insertAfter(E toInsert, E point) {
    innerChain.insertAfter(toInsert, point);
  }

  /** Inserts <code>toInsert</code> in the Chain after <code>point</code>. */
  @Override
  public void insertAfter(List<E> toInsert, E point) {
    innerChain.insertAfter(toInsert, point);
  }

  @Override
  public void insertAfter(Chain<E> toInsert, E point) {
    innerChain.insertAfter(toInsert, point);
  }

  @Override
  public void insertAfter(Collection<? extends E> toInsert, E point) {
    innerChain.insertAfter(toInsert, point);
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  @Override
  public void insertBefore(List<E> toInsert, E point) {
    if (!toInsert.isEmpty()) {
      // Insert toInsert backwards into the list
      E previousPoint = point;
      for (ListIterator<E> it = toInsert.listIterator(toInsert.size()); it.hasPrevious();) {
        E o = it.previous();
        insertBeforeNoRedirect(o, previousPoint);
        previousPoint = o;
      }
      point.redirectJumpsToThisTo(toInsert.get(0));
    }
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  @Override
  public void insertBefore(Chain<E> toInsert, E point) {
    if (!toInsert.isEmpty()) {
      // Insert toInsert backwards into the list
      E previousPoint = point;
      for (E o = toInsert.getLast(); o != null; o = toInsert.getPredOf(o)) {
        insertBeforeNoRedirect(o, previousPoint);
        previousPoint = o;
      }
      point.redirectJumpsToThisTo(toInsert.getFirst());
    }
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  @Override
  public void insertBefore(E toInsert, E point) {
    point.redirectJumpsToThisTo(toInsert);
    innerChain.insertBefore(toInsert, point);
  }

  @Override
  public void insertBefore(Collection<? extends E> toInsert, E point) {
    if (toInsert instanceof Chain) {
      @SuppressWarnings("unchecked")
      final Chain<E> temp = (Chain<E>) toInsert;
      insertBefore(temp, point);
    } else if (toInsert instanceof List) {
      @SuppressWarnings("unchecked")
      final List<E> temp = (List<E>) toInsert;
      insertBefore(temp, point);
    } else {
      insertBefore(new ArrayList<>(toInsert), point);
    }
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code> WITHOUT redirecting jumps. */
  public void insertBeforeNoRedirect(E toInsert, E point) {
    innerChain.insertBefore(toInsert, point);
  }

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code> WITHOUT redirecting jumps. */
  public void insertBeforeNoRedirect(List<E> toInsert, E point) {
    if (!toInsert.isEmpty()) {
      // Insert toInsert backwards into the list
      E previousPoint = point;
      for (ListIterator<E> it = toInsert.listIterator(toInsert.size()); it.hasPrevious();) {
        E o = it.previous();
        insertBeforeNoRedirect(o, previousPoint);
        previousPoint = o;
      }
    }
  }

  /** Returns true if object <code>a</code> follows object <code>b</code> in the Chain. */
  @Override
  public boolean follows(E a, E b) {
    return innerChain.follows(a, b);
  }

  /** Removes the given object from this Chain. */
  @Override
  public boolean remove(Object obj) {
    if (contains(obj)) {
      @SuppressWarnings("unchecked")
      E objCast = (E) obj;
      patchBeforeRemoval(innerChain, objCast);
      return innerChain.remove(objCast);
    } else {
      return false;
    }
  }

  protected static <E extends Unit> void patchBeforeRemoval(Chain<E> chain, E removing) {
    Unit successor = chain.getSuccOf(removing);
    if (successor == null) {
      successor = chain.getPredOf(removing);
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
    removing.redirectJumpsToThisTo(successor);
  }

  /** Returns true if this patching chain contains the specified element. */
  @Override
  public boolean contains(Object u) {
    return innerChain.contains(u);
  }

  /** Adds the given object at the beginning of the Chain. */
  @Override
  public void addFirst(E u) {
    innerChain.addFirst(u);
  }

  /** Adds the given object at the end of the Chain. */
  @Override
  public void addLast(E u) {
    innerChain.addLast(u);
  }

  /** Removes the first object from this Chain. */
  @Override
  public void removeFirst() {
    remove(innerChain.getFirst());
  }

  /** Removes the last object from this Chain. */
  @Override
  public void removeLast() {
    remove(innerChain.getLast());
  }

  /** Returns the first object in this Chain. */
  @Override
  public E getFirst() {
    return innerChain.getFirst();
  }

  /** Returns the last object in this Chain. */
  @Override
  public E getLast() {
    return innerChain.getLast();
  }

  /** Returns the object immediately following <code>point</code>. */
  @Override
  public E getSuccOf(E point) {
    return innerChain.getSuccOf(point);
  }

  /** Returns the object immediately preceding <code>point</code>. */
  @Override
  public E getPredOf(E point) {
    return innerChain.getPredOf(point);
  }

  /** Returns the size of this Chain. */
  @Override
  public int size() {
    return innerChain.size();
  }

  /** Returns the number of times this chain has been modified. */
  @Override
  public long getModificationCount() {
    return innerChain.getModificationCount();
  }

  @Override
  public Collection<E> getElementsUnsorted() {
    return innerChain.getElementsUnsorted();
  }

  /**
   * Returns an iterator over a copy of this chain. This avoids ConcurrentModificationExceptions from being thrown if the
   * underlying Chain is modified during iteration. Do not use this to remove elements which have not yet been iterated over!
   */
  @Override
  public Iterator<E> snapshotIterator() {
    return innerChain.snapshotIterator();
  }

  /** Returns an iterator over this Chain. */
  @Override
  public Iterator<E> iterator() {
    return new PatchingIterator(innerChain);
  }

  /** Returns an iterator over this Chain, starting at the given object. */
  @Override
  public Iterator<E> iterator(E u) {
    return new PatchingIterator(innerChain, u);
  }

  /** Returns an iterator over this Chain, starting at head and reaching tail (inclusive). */
  @Override
  public Iterator<E> iterator(E head, E tail) {
    return new PatchingIterator(innerChain, head, tail);
  }

  protected class PatchingIterator implements Iterator<E> {

    protected final Chain<E> innerChain;
    protected final Iterator<E> innerIterator;
    protected E lastObject;
    protected boolean state = false;

    protected PatchingIterator(Chain<E> innerChain) {
      this.innerChain = innerChain;
      this.innerIterator = innerChain.iterator();
    }

    protected PatchingIterator(Chain<E> innerChain, E u) {
      this.innerChain = innerChain;
      this.innerIterator = innerChain.iterator(u);
    }

    protected PatchingIterator(Chain<E> innerChain, E head, E tail) {
      this.innerChain = innerChain;
      this.innerIterator = innerChain.iterator(head, tail);
    }

    @Override
    public boolean hasNext() {
      return innerIterator.hasNext();
    }

    @Override
    public E next() {
      lastObject = innerIterator.next();
      state = true;
      return lastObject;
    }

    @Override
    public void remove() {
      if (!state) {
        throw new IllegalStateException("remove called before first next() call");
      } else {
        state = false;
        patchBeforeRemoval(innerChain, lastObject);
        innerIterator.remove();
      }
    }
  }
}
