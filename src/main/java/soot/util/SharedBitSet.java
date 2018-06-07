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

public final class SharedBitSet {
  BitVector value;
  boolean own = true;

  public SharedBitSet(int i) {
    value = new BitVector(i);
  }

  public SharedBitSet() {
    this(32);
  }

  private void acquire() {
    if (own) {
      return;
    }
    own = true;
    value = (BitVector) value.clone();
  }

  private void canonicalize() {
    value = SharedBitSetCache.v().canonicalize(value);
    own = false;
  }

  public boolean set(int bit) {
    acquire();
    return value.set(bit);
  }

  public void clear(int bit) {
    acquire();
    value.clear(bit);
  }

  public boolean get(int bit) {
    return value.get(bit);
  }

  public void and(SharedBitSet other) {
    if (own) {
      value.and(other.value);
    } else {
      value = BitVector.and(value, other.value);
      own = true;
    }
    canonicalize();
  }

  public void or(SharedBitSet other) {
    if (own) {
      value.or(other.value);
    } else {
      value = BitVector.or(value, other.value);
      own = true;
    }
    canonicalize();
  }

  public boolean orAndAndNot(SharedBitSet orset, SharedBitSet andset, SharedBitSet andnotset) {
    acquire();
    boolean ret = value.orAndAndNot(orset.value, andset.value, andnotset.value);
    canonicalize();
    return ret;
  }

  public boolean orAndAndNot(SharedBitSet orset, BitVector andset, SharedBitSet andnotset) {
    acquire();
    boolean ret = value.orAndAndNot(orset.value, andset, andnotset == null ? null : andnotset.value);
    canonicalize();
    return ret;
  }

  public BitSetIterator iterator() {
    return value.iterator();
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    for (BitSetIterator it = iterator(); it.hasNext();) {
      b.append(it.next());
      b.append(",");
    }
    return b.toString();
  }
}
