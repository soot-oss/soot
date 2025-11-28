package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2025 Marc Miltenberger
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

import java.util.BitSet;

/**
 * This bitset keeps record about the lowest and the highest set bit. This can come in handy when comparing bitsets.
 */
public class MinMaxBitSet extends BitSet {

  private int max = -1;
  private int min = -1;

  public MinMaxBitSet() {
  }

  public MinMaxBitSet(int nbits) {
    super(nbits);
  }

  @Override
  public void and(BitSet set) {
    MinMaxBitSet other = (MinMaxBitSet) set;
    // narrow down further
    this.min = Math.max(min, other.min);
    this.max = Math.min(max, other.max);
    super.or(set);
  }

  @Override
  public void or(BitSet set) {
    MinMaxBitSet other = (MinMaxBitSet) set;
    this.min = Math.min(min, other.min);
    this.max = Math.max(max, other.max);
    super.or(set);
  }

  @Override
  public boolean intersects(BitSet set) {
    if (set instanceof MinMaxBitSet) {
      MinMaxBitSet o = (MinMaxBitSet) set;
      int myMax = max;
      int myMin = min;
      int otherMax = o.max;
      int otherMin = o.min;
      if (myMax < otherMin || otherMax < myMin || myMin > otherMax || otherMin > myMax) {
        // no need to check
        return false;
      }
    }
    return super.intersects(set);
  }

  @Override
  public void set(int bitIndex) {
    if (min == -1) {
      max = min = bitIndex;
    } else {
      min = Math.min(bitIndex, min);
      max = Math.max(bitIndex, max);
    }
    super.set(bitIndex);
  }

  @Override
  public void set(int bitIndex, boolean value) {
    if (!value) {
      throw new IllegalArgumentException("Not supporting unsetting");
    }
    this.set(bitIndex);
  }

  @Override
  public void clear(int bitIndex) {
    throw new IllegalArgumentException("Not supporting clearing");
  }

  @Override
  public void clear() {
    super.clear();
    min = -1;
    max = -1;
  }

  @Override
  public void clear(int fromIndex, int toIndex) {
    throw new IllegalArgumentException("Not supporting clearing");
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }
}
