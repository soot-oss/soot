package soot.jimple.spark.sets;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import java.util.HashSet;
import java.util.Set;

import soot.G;
import soot.PointsToSet;
import soot.Type;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;

/**
 * Implementation of points-to set that holds two sets: one for new elements that have not yet been propagated, and the other
 * for elements that have already been propagated.
 *
 * @author Ondrej Lhotak
 */
public class DoublePointsToSet extends PointsToSetInternal {
  public DoublePointsToSet(Type type, PAG pag) {
    super(type);
    newSet = G.v().newSetFactory.newSet(type, pag);
    oldSet = G.v().oldSetFactory.newSet(type, pag);
    this.pag = pag;
  }

  /** Returns true if this set contains no run-time objects. */
  public boolean isEmpty() {
    return oldSet.isEmpty() && newSet.isEmpty();
  }

  /** Returns true if this set shares some objects with other. */
  public boolean hasNonEmptyIntersection(PointsToSet other) {
    return oldSet.hasNonEmptyIntersection(other) || newSet.hasNonEmptyIntersection(other);
  }

  /** Set of all possible run-time types of objects in the set. */
  public Set<Type> possibleTypes() {
    Set<Type> ret = new HashSet<Type>();
    ret.addAll(oldSet.possibleTypes());
    ret.addAll(newSet.possibleTypes());
    return ret;
  }

  /**
   * Adds contents of other into this set, returns true if this set changed.
   */
  public boolean addAll(PointsToSetInternal other, PointsToSetInternal exclude) {
    if (exclude != null) {
      throw new RuntimeException("NYI");
    }
    return newSet.addAll(other, oldSet);
  }

  /** Calls v's visit method on all nodes in this set. */
  public boolean forall(P2SetVisitor v) {
    oldSet.forall(v);
    newSet.forall(v);
    return v.getReturnValue();
  }

  /** Adds n to this set, returns true if n was not already in this set. */
  public boolean add(Node n) {
    if (oldSet.contains(n)) {
      return false;
    }
    return newSet.add(n);
  }

  /** Returns set of nodes already present before last call to flushNew. */
  public PointsToSetInternal getOldSet() {
    return oldSet;
  }

  /** Returns set of newly-added nodes since last call to flushNew. */
  public PointsToSetInternal getNewSet() {
    return newSet;
  }

  /** Sets all newly-added nodes to old nodes. */
  public void flushNew() {
    oldSet.addAll(newSet, null);
    newSet = G.v().newSetFactory.newSet(type, pag);
  }

  /** Sets all nodes to newly-added nodes. */
  public void unFlushNew() {
    newSet.addAll(oldSet, null);
    oldSet = G.v().oldSetFactory.newSet(type, pag);
  }

  /** Merges other into this set. */
  public void mergeWith(PointsToSetInternal other) {
    if (!(other instanceof DoublePointsToSet)) {
      throw new RuntimeException("NYI");
    }
    final DoublePointsToSet o = (DoublePointsToSet) other;
    if (other.type != null && !(other.type.equals(type))) {
      throw new RuntimeException("different types " + type + " and " + other.type);
    }
    if (other.type == null && type != null) {
      throw new RuntimeException("different types " + type + " and " + other.type);
    }
    final PointsToSetInternal newNewSet = G.v().newSetFactory.newSet(type, pag);
    final PointsToSetInternal newOldSet = G.v().oldSetFactory.newSet(type, pag);
    oldSet.forall(new P2SetVisitor() {
      public final void visit(Node n) {
        if (o.oldSet.contains(n)) {
          newOldSet.add(n);
        }
      }
    });
    newNewSet.addAll(this, newOldSet);
    newNewSet.addAll(o, newOldSet);
    newSet = newNewSet;
    oldSet = newOldSet;
  }

  /** Returns true iff the set contains n. */
  public boolean contains(Node n) {
    return oldSet.contains(n) || newSet.contains(n);
  }

  private static P2SetFactory defaultP2SetFactory = new P2SetFactory() {
    public PointsToSetInternal newSet(Type type, PAG pag) {
      return new DoublePointsToSet(type, pag);
    }
  };

  public static P2SetFactory getFactory(P2SetFactory newFactory, P2SetFactory oldFactory) {
    G.v().newSetFactory = newFactory;
    G.v().oldSetFactory = oldFactory;
    return defaultP2SetFactory;
  }

  /* End of public methods. */
  /* End of package methods. */

  private PAG pag;
  protected PointsToSetInternal newSet;
  protected PointsToSetInternal oldSet;
}
