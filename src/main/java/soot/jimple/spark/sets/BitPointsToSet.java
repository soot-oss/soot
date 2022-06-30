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

import soot.Type;
import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.util.BitSetIterator;
import soot.util.BitVector;

/**
 * Implementation of points-to set using a bit vector.
 * 
 * @author Ondrej Lhotak
 */
public final class BitPointsToSet extends PointsToSetInternal {
  public BitPointsToSet(Type type, PAG pag) {
    super(type);
    this.pag = pag;
    bits = new BitVector(pag.getAllocNodeNumberer().size());
  }

  /** Returns true if this set contains no run-time objects. */
  public final boolean isEmpty() {
    return empty;
  }

  private final boolean superAddAll(PointsToSetInternal other, PointsToSetInternal exclude) {
    boolean ret = super.addAll(other, exclude);
    if (ret) {
      empty = false;
    }
    return ret;
  }

  private final boolean nativeAddAll(BitPointsToSet other, BitPointsToSet exclude) {
    BitVector mask = null;
    TypeManager typeManager = pag.getTypeManager();
    if (!typeManager.castNeverFails(other.getType(), this.getType())) {
      mask = typeManager.get(this.getType());
    }
    BitVector obits = (other == null ? null : other.bits);
    BitVector ebits = (exclude == null ? null : exclude.bits);
    boolean ret = bits.orAndAndNot(obits, mask, ebits);
    if (ret) {
      empty = false;
    }
    return ret;
  }

  /**
   * Adds contents of other into this set, returns true if this set changed.
   */
  public final boolean addAll(PointsToSetInternal other, PointsToSetInternal exclude) {
    if (other != null && !(other instanceof BitPointsToSet)) {
      return superAddAll(other, exclude);
    }
    if (exclude != null && !(exclude instanceof BitPointsToSet)) {
      return superAddAll(other, exclude);
    }
    return nativeAddAll((BitPointsToSet) other, (BitPointsToSet) exclude);
  }

  /** Calls v's visit method on all nodes in this set. */
  public final boolean forall(P2SetVisitor v) {
    for (BitSetIterator it = bits.iterator(); it.hasNext();) {
      v.visit((Node) pag.getAllocNodeNumberer().get(it.next()));
    }
    return v.getReturnValue();
  }

  /** Adds n to this set, returns true if n was not already in this set. */
  public final boolean add(Node n) {
    if (pag.getTypeManager().castNeverFails(n.getType(), type)) {
      return fastAdd(n);
    }
    return false;
  }

  /** Returns true iff the set contains n. */
  public final boolean contains(Node n) {
    return bits.get(n.getNumber());
  }

  public static P2SetFactory getFactory() {
    return new P2SetFactory() {
      public PointsToSetInternal newSet(Type type, PAG pag) {
        return new BitPointsToSet(type, pag);
      }
    };
  }

  /* End of public methods. */
  /* End of package methods. */

  private boolean fastAdd(Node n) {
    boolean ret = bits.set(n.getNumber());
    if (ret) {
      empty = false;
    }
    return ret;
  }

  private BitVector bits = null;
  private boolean empty = true;
  private PAG pag = null;
}
