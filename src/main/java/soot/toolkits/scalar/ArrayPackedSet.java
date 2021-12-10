package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 *       updated 2002 Florian Loitsch
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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Reference implementation for a BoundedFlowSet. Items are stored in an Array.
 */
public class ArrayPackedSet<T> extends AbstractBoundedFlowSet<T> {

  protected final ObjectIntMapper<T> map;
  protected final BitSet bits;

  public ArrayPackedSet(FlowUniverse<T> universe) {
    this(new ObjectIntMapper<T>(universe));
  }

  ArrayPackedSet(ObjectIntMapper<T> map) {
    this(map, new BitSet());
  }

  ArrayPackedSet(ObjectIntMapper<T> map, BitSet bits) {
    this.map = map;
    this.bits = bits;
  }

  @Override
  public ArrayPackedSet<T> clone() {
    return new ArrayPackedSet<T>(map, (BitSet) bits.clone());
  }

  @Override
  public FlowSet<T> emptySet() {
    return new ArrayPackedSet<T>(map);
  }

  @Override
  public int size() {
    return bits.cardinality();
  }

  @Override
  public boolean isEmpty() {
    return bits.isEmpty();
  }

  @Override
  public void clear() {
    bits.clear();
  }

  private BitSet copyBitSet(ArrayPackedSet<?> dest) {
    assert (dest.map == this.map);
    if (this != dest) {
      dest.bits.clear();
      dest.bits.or(bits);
    }
    return dest.bits;
  }

  /** Returns true if flowSet is the same type of flow set as this. */
  private boolean sameType(Object flowSet) {
    return (flowSet instanceof ArrayPackedSet) && (((ArrayPackedSet<?>) flowSet).map == this.map);
  }

  private List<T> toList(BitSet bits, int base) {
    final int len = bits.cardinality();
    switch (len) {
      case 0:
        return emptyList();

      case 1:
        return singletonList(map.getObject((base - 1) + bits.length()));

      default:
        List<T> elements = new ArrayList<T>(len);

        int i = bits.nextSetBit(0);
        do {
          int endOfRun = bits.nextClearBit(i + 1);
          do {
            elements.add(map.getObject(base + i++));
          } while (i < endOfRun);
          i = bits.nextSetBit(i + 1);
        } while (i >= 0);

        return elements;
    }
  }

  public List<T> toList(int lowInclusive, int highInclusive) {
    if (lowInclusive > highInclusive) {
      return emptyList();
    }
    if (lowInclusive < 0) {
      throw new IllegalArgumentException();
    }

    int highExclusive = highInclusive + 1;
    return toList(bits.get(lowInclusive, highExclusive), lowInclusive);
  }

  @Override
  public List<T> toList() {
    return toList(bits, 0);
  }

  @Override
  public void add(T obj) {
    bits.set(map.getInt(obj));
  }

  @Override
  public void complement(FlowSet<T> destFlow) {
    if (sameType(destFlow)) {
      ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;
      copyBitSet(dest).flip(0, dest.map.size());
    } else {
      super.complement(destFlow);
    }
  }

  @Override
  public void remove(T obj) {
    bits.clear(map.getInt(obj));
  }

  @Override
  public boolean isSubSet(FlowSet<T> other) {
    if (other == this) {
      return true;
    }
    if (sameType(other)) {
      ArrayPackedSet<T> o = (ArrayPackedSet<T>) other;

      BitSet tmp = (BitSet) o.bits.clone();
      tmp.andNot(bits);
      return tmp.isEmpty();
    }
    return super.isSubSet(other);
  }

  @Override
  public void union(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      ArrayPackedSet<T> other = (ArrayPackedSet<T>) otherFlow;
      ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;

      copyBitSet(dest).or(other.bits);
    } else {
      super.union(otherFlow, destFlow);
    }
  }

  @Override
  public void difference(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      ArrayPackedSet<T> other = (ArrayPackedSet<T>) otherFlow;
      ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;

      copyBitSet(dest).andNot(other.bits);
    } else {
      super.difference(otherFlow, destFlow);
    }
  }

  @Override
  public void intersection(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      ArrayPackedSet<T> other = (ArrayPackedSet<T>) otherFlow;
      ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;

      copyBitSet(dest).and(other.bits);
    } else {
      super.intersection(otherFlow, destFlow);
    }
  }

  /**
   * Returns true, if the object is in the set.
   */
  @Override
  public boolean contains(T obj) {
    /*
     * check if the object is in the map, direct call of map.getInt will add the object into the map.
     */
    return map.contains(obj) && bits.get(map.getInt(obj));
  }

  @Override
  public boolean equals(Object otherFlow) {
    if (sameType(otherFlow)) {
      return bits.equals(((ArrayPackedSet<?>) otherFlow).bits);
    } else {
      return super.equals(otherFlow);
    }
  }

  @Override
  public void copy(FlowSet<T> destFlow) {
    if (this == destFlow) {
      return;
    }
    if (sameType(destFlow)) {
      ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;
      copyBitSet(dest);
    } else {
      super.copy(destFlow);
    }
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      int curr = -1;
      int next = bits.nextSetBit(0);

      @Override
      public boolean hasNext() {
        return (next >= 0);
      }

      @Override
      public T next() {
        if (next < 0) {
          throw new NoSuchElementException();
        }
        curr = next;
        next = bits.nextSetBit(curr + 1);
        return map.getObject(curr);
      }

      @Override
      public void remove() {
        if (curr < 0) {
          throw new IllegalStateException();
        }
        bits.clear(curr);
        curr = -1;
      }
    };
  }
}
