package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

/**
 * This is the Soot internal implementation of java.util.BitSet with Felix and Jerome's clever efficient iterator. It was
 * re-implemented from scratch by Ondrej Lhotak to avoid licence issues. It was named BitVector rather than BitSet to avoid a
 * name clash with the one in the standard Java library.
 *
 * @author Ondrej Lhotak
 */
public class BitVector {

  private long[] bits;

  public BitVector() {
    this(64);
  }

  /** Copy constructor */
  // Added by Adam Richard. More efficient than clone(), and easier to extend
  public BitVector(BitVector other) {
    final long[] otherBits = other.bits;
    bits = new long[otherBits.length];
    System.arraycopy(otherBits, 0, bits, 0, otherBits.length);
  }

  public BitVector(int numBits) {
    int lastIndex = indexOf(numBits - 1);
    bits = new long[lastIndex + 1];
  }

  private int indexOf(int bit) {
    return bit >> 6;
  }

  private long mask(int bit) {
    return 1L << (bit & 63);
  }

  public void and(BitVector other) {
    if (this == other) {
      return;
    }
    final long[] otherBits = other.bits;
    int numToAnd = otherBits.length;
    if (bits.length < numToAnd) {
      numToAnd = bits.length;
    }
    int i;
    for (i = 0; i < numToAnd; i++) {
      bits[i] = bits[i] & otherBits[i];
    }
    for (; i < bits.length; i++) {
      bits[i] = 0L;
    }
  }

  public void andNot(BitVector other) {
    final long[] otherBits = other.bits;
    int numToAnd = otherBits.length;
    if (bits.length < numToAnd) {
      numToAnd = bits.length;
    }
    for (int i = 0; i < numToAnd; i++) {
      bits[i] = bits[i] & ~otherBits[i];
    }
  }

  public void clear(int bit) {
    if (indexOf(bit) < bits.length) {
      bits[indexOf(bit)] &= ~mask(bit);
    }
  }

  @Override
  public Object clone() {
    try {
      BitVector ret = (BitVector) super.clone();
      System.arraycopy(bits, 0, ret.bits, 0, ret.bits.length);
      return ret;
    } catch (CloneNotSupportedException e) {
      // cannot occur
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BitVector)) {
      return false;
    }
    final long[] otherBits = ((BitVector) o).bits;
    long[] longer = otherBits;
    int min = bits.length;
    if (otherBits.length < min) {
      min = otherBits.length;
      longer = bits;
    }
    int i;
    for (i = 0; i < min; i++) {
      if (bits[i] != otherBits[i]) {
        return false;
      }
    }
    for (; i < longer.length; i++) {
      if (longer[i] != 0L) {
        return false;
      }
    }
    return true;
  }

  public boolean get(int bit) {
    if (indexOf(bit) >= bits.length) {
      return false;
    }
    return (bits[indexOf(bit)] & mask(bit)) != 0L;
  }

  @Override
  public int hashCode() {
    long ret = 0;
    for (long element : bits) {
      ret ^= element;
    }
    return (int) ((ret >> 32) ^ ret);
  }

  /** Returns index of highest-numbered one bit. */
  public int length() {
    int i;
    for (i = bits.length - 1; i >= 0; i--) {
      if (bits[i] != 0L) {
        break;
      }
    }
    if (i < 0) {
      return 0;
    }
    long j = bits[i];
    i++;
    i <<= 6;
    for (long k = 1L << 63; (k & j) == 0L; k >>= 1, i--) {
    }
    return i;
  }

  public void copyFrom(BitVector other) {
    if (this == other) {
      return;
    }
    final long[] otherBits = other.bits;
    int j;
    for (j = otherBits.length - 1; j >= 0; j--) {
      if (otherBits[j] != 0L) {
        break;
      }
    }
    expand(j << 6);
    int i = j + 1;
    for (; j >= 0; j--) {
      bits[j] = otherBits[j];
    }
    for (; i < bits.length; i++) {
      bits[i] = 0L;
    }
  }

  public void or(BitVector other) {
    if (this == other) {
      return;
    }
    final long[] otherBits = other.bits;
    int j;
    for (j = otherBits.length - 1; j >= 0; j--) {
      if (otherBits[j] != 0L) {
        break;
      }
    }
    expand(j << 6);
    for (; j >= 0; j--) {
      bits[j] |= otherBits[j];
    }
  }

  /**
   * Count the number of ones in the bitvector.
   *
   * @author Adam Richard This is Brian Kernighan's algorithm from: http://graphics.stanford.edu/~seander/bithacks.html and
   *         is efficient for sparse bit sets.
   */
  public int cardinality() {
    int c = 0;
    for (long v : bits) {
      while (v != 0) {
        v &= v - 1;
        ++c;
      }
    }
    return c;
  }

  /**
   * Returns true if the both the current and the specified bitvectors have at least one bit set in common.
   *
   * @author Quentin Sabah Inspired by the BitVector.and method.
   */
  public boolean intersects(BitVector other) {
    final long[] otherBits = other.bits;
    int numToCheck = otherBits.length;
    if (bits.length < numToCheck) {
      numToCheck = bits.length;
    }
    int i;
    for (i = 0; i < numToCheck; i++) {
      if ((bits[i] & otherBits[i]) != 0) {
        return true;
      }
    }
    return false;
  }

  private void expand(int bit) {
    int n = indexOf(bit) + 1;
    if (n <= bits.length) {
      return;
    }
    if (bits.length * 2 > n) {
      n = bits.length * 2;
    }
    long[] newBits = new long[n];
    System.arraycopy(bits, 0, newBits, 0, bits.length);
    bits = newBits;
  }

  public void xor(BitVector other) {
    if (this == other) {
      return;
    }
    final long[] otherBits = other.bits;
    int j;
    for (j = otherBits.length - 1; j >= 0; j--) {
      if (otherBits[j] != 0L) {
        break;
      }
    }
    expand(j << 6);
    for (; j >= 0; j--) {
      bits[j] ^= otherBits[j];
    }
  }

  public boolean set(int bit) {
    expand(bit);
    boolean ret = !get(bit);
    bits[indexOf(bit)] |= mask(bit);
    return ret;
  }

  /** Returns number of bits in the underlying array. */
  public int size() {
    return bits.length << 6;
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    ret.append('{');
    boolean start = true;
    for (BitSetIterator it = new BitSetIterator(bits); it.hasNext();) {
      int bit = it.next();
      if (start) {
        start = false;
      } else {
        ret.append(", ");
      }
      ret.append(bit);
    }
    ret.append('}');
    return ret.toString();
  }

  /**
   * Computes this = this OR ((orset AND andset ) AND (NOT andnotset)) Returns true iff this is modified.
   */
  public boolean orAndAndNot(BitVector orset, BitVector andset, BitVector andnotset) {
    final long[] a, b, c, d;
    final int al, bl, cl, dl;
    {
      a = this.bits;
      al = a.length;
    }
    if (orset == null) {
      b = null;
      bl = 0;
    } else {
      b = orset.bits;
      bl = b.length;
    }
    if (andset == null) {
      c = null;
      cl = 0;
    } else {
      c = andset.bits;
      cl = c.length;
    }
    if (andnotset == null) {
      d = null;
      dl = 0;
    } else {
      d = andnotset.bits;
      dl = d.length;
    }

    final long[] e;
    if (al < bl) {
      e = new long[bl];
      System.arraycopy(a, 0, e, 0, al);
      this.bits = e;
    } else {
      e = a;
    }

    boolean ret = false;
    if (c == null) {
      if (dl <= bl) {
        int i = 0;
        for (; i < dl; i++) {
          long l = b[i] & ~d[i];
          if ((l & ~e[i]) != 0) {
            ret = true;
          }
          e[i] |= l;
        }
        for (; i < bl; i++) {
          long l = b[i];
          if ((l & ~e[i]) != 0) {
            ret = true;
          }
          e[i] |= l;
        }
      } else {
        for (int i = 0; i < bl; i++) {
          long l = b[i] & ~d[i];
          if ((l & ~e[i]) != 0) {
            ret = true;
          }
          e[i] |= l;
        }
      }
    } else if (bl <= cl && bl <= dl) {
      // bl is the shortest
      for (int i = 0; i < bl; i++) {
        long l = b[i] & c[i] & ~d[i];
        if ((l & ~e[i]) != 0) {
          ret = true;
        }
        e[i] |= l;
      }
    } else if (cl <= bl && cl <= dl) {
      // cl is the shortest
      for (int i = 0; i < cl; i++) {
        long l = b[i] & c[i] & ~d[i];
        if ((l & ~e[i]) != 0) {
          ret = true;
        }
        e[i] |= l;
      }
    } else {
      // dl is the shortest
      int i = 0;
      for (; i < dl; i++) {
        long l = b[i] & c[i] & ~d[i];
        if ((l & ~e[i]) != 0) {
          ret = true;
        }
        e[i] |= l;
      }
      for (int shorter = (bl < cl) ? bl : cl; i < shorter; i++) {
        long l = b[i] & c[i];
        if ((l & ~e[i]) != 0) {
          ret = true;
        }
        e[i] |= l;
      }
    }

    return ret;
  }

  public static BitVector and(BitVector set1, BitVector set2) {
    int min = set1.size();
    {
      int max = set2.size();
      if (min > max) {
        min = max;
      }
      // max is not necessarily correct at this point, so let it go
      // out of scope
    }

    BitVector ret = new BitVector(min);
    long[] retbits = ret.bits;
    long[] bits1 = set1.bits;
    long[] bits2 = set2.bits;
    min >>= 6;
    for (int i = 0; i < min; i++) {
      retbits[i] = bits1[i] & bits2[i];
    }
    return ret;
  }

  public static BitVector or(BitVector set1, BitVector set2) {
    int min = set1.size();
    int max = set2.size();
    if (min > max) {
      min = max;
      max = set1.size();
    }

    BitVector ret = new BitVector(max);
    long[] retbits = ret.bits;
    long[] bits1 = set1.bits;
    long[] bits2 = set2.bits;
    min >>= 6;
    max >>= 6;
    for (int i = 0; i < min; i++) {
      retbits[i] = bits1[i] | bits2[i];
    }
    if (bits1.length == min) {
      System.arraycopy(bits2, min, retbits, min, max - min);
    } else {
      System.arraycopy(bits1, min, retbits, min, max - min);
    }
    return ret;
  }

  public BitSetIterator iterator() {
    return new BitSetIterator(bits);
  }
}
