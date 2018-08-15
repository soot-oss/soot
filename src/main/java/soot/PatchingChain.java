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
import java.util.LinkedList;
import java.util.List;

import soot.jimple.GotoStmt;
import soot.jimple.Jimple;
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

  /** Inserts <code>toInsert</code> in the Chain before <code>point</code>. */
  @Override
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
  @Override
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
  @Override
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
    List<E> o = new ArrayList<E>();
    o.add(toInsert);
    insertOnEdge(o, point_src, point_tgt);

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
  public void insertOnEdge(Collection<? extends E> toInsert, E point_src, E point_tgt) {

    if (toInsert == null) {
      throw new RuntimeException("Bad idea! You tried to insert a null object into a Chain!");
    }

    // Insert 'toInsert' before 'target' point in chain if the source point
    // is null
    if (point_src == null && point_tgt != null) {
      point_tgt.redirectJumpsToThisTo(toInsert.iterator().next());
      innerChain.insertBefore(toInsert, point_tgt);
      return;
    }

    // Insert 'toInsert' after 'source' point in chain if the target point
    // is null
    if (point_src != null && point_tgt == null) {
      innerChain.insertAfter(toInsert, point_src);
      return;
    }

    // Throw an exception if both source and target is null
    if (point_src == null && point_tgt == null) {
      throw new RuntimeException("insertOnEdge failed! Both source and target points are null.");
    }

    // If target is right after the source in the Chain
    // 1- Redirect all jumps (if any) from 'source' to 'target', to
    // 'toInsert[0]'
    // (source->target) ==> (source->toInsert[0])
    // 2- Insert 'toInsert' after 'source' in Chain
    if (getSuccOf(point_src) == point_tgt) {
      List<UnitBox> boxes = point_src.getUnitBoxes();
      for (UnitBox box : boxes) {
        if (box.getUnit() == point_tgt) {
          box.setUnit(toInsert.iterator().next());
        }
      }
      innerChain.insertAfter(toInsert, point_src);
      return;
    }

    // If the target is not right after the source in chain then,
    // 1- Redirect all jumps (if any) from 'source' to 'target', to
    // 'toInsert[0]'
    // (source->target) ==> (source->toInsert[0])
    // 1.1- if there are no jumps from source to target, then such an edge
    // does not exist. Throw an exception.
    // 2- Insert 'toInsert' before 'target' in Chain
    // 3- If required, add a 'goto target' statement so that no other edge
    // executes 'toInsert'
    boolean validEdgeFound = false;
    Unit originalPred = getPredOf(point_tgt);

    List<UnitBox> boxes = point_src.getUnitBoxes();
    for (UnitBox box : boxes) {
      if (box.getUnit() == point_tgt) {

        if (point_src instanceof GotoStmt) {

          box.setUnit(toInsert.iterator().next());
          innerChain.insertAfter(toInsert, point_src);

          Unit goto_unit = Jimple.v().newGotoStmt(point_tgt);
          if (toInsert instanceof List) {
            List<Unit> l = (List<Unit>) toInsert;
            innerChain.insertAfter((E) goto_unit, (E) l.get(l.size() - 1));
          } else {
            innerChain.insertAfter((E) goto_unit, (E) toInsert.toArray()[toInsert.size() - 1]);
          }
          return;
        }

        box.setUnit(toInsert.iterator().next());

        validEdgeFound = true;
      }
    }
    if (validEdgeFound) {
      innerChain.insertBefore(toInsert, point_tgt);

      if (originalPred != point_src) {
        if (originalPred instanceof GotoStmt) {
          return;
        }

        Unit goto_unit = Jimple.v().newGotoStmt(point_tgt);
        innerChain.insertBefore((List<E>) goto_unit, toInsert.iterator().next());
      }
      return;
    }

    // In certain scenarios, the above code can add extra 'goto' units on a
    // different edge
    // So, an edge [src --> tgt] becomes [src -> goto tgt -> tgt].
    // When this happens, the original edge [src -> tgt] ceases to exist.
    // The following code handles such scenarios.
    final Unit succ = getSuccOf(point_src);
    if (succ instanceof GotoStmt) {
      if (succ.getUnitBoxes().get(0).getUnit() == point_tgt) {

        getSuccOf(point_src).redirectJumpsToThisTo(toInsert.iterator().next());
        innerChain.insertBefore(toInsert, getSuccOf(point_src));

        return;
      }
    }

    // If the control reaches this point, it means that an edge [src -> tgt]
    // as specified by user does not exist and is thus invalid
    // Return an exception.
    throw new RuntimeException(
        "insertOnEdge failed! No such edge found. The edge on which you want to insert an instrumentation is invalid.");
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
    insertOnEdge((Collection<E>) toInsert, point_src, point_tgt);
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
    insertOnEdge((Collection<E>) toInsert, point_src, point_tgt);
  }

  /** Returns true if object <code>a</code> follows object <code>b</code> in the Chain. */
  @Override
  public boolean follows(E a, E b) {
    return innerChain.follows(a, b);
  }

  /** Removes the given object from this Chain. */
  @Override
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
  @Override
  public Iterator<E> snapshotIterator() {
    List<E> l = new LinkedList<E>(this);
    return l.iterator();
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

  /** Returns the size of this Chain. */
  @Override
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

  @Override
  public void insertAfter(Collection<? extends E> toInsert, E point) {
    innerChain.insertAfter(toInsert, point);
  }

  @Override
  public void insertBefore(Collection<? extends E> toInsert, E point) {
    innerChain.insertBefore(toInsert, point);
  }
}
