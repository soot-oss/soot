package soot.jimple.spark.geom.dataRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 Richard Xiao
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
 * The segment figure for encoding the one-to-one relation.
 *
 * @author richardxx
 *
 */
public class SegmentNode implements Comparable<SegmentNode> {

  // I1 : start interval of the pointer
  // I2 : start interval of the pointed to object
  // L : length of the interval
  // is_new : a flag to indicate that this interval has not been processed
  public long I1;
  public long I2;
  public long L;
  public SegmentNode next = null;
  public boolean is_new = true;

  public SegmentNode() {
  }

  public SegmentNode(SegmentNode other) {
    copySegment(other);
  }

  public void copySegment(SegmentNode other) {
    I1 = other.I1;
    I2 = other.I2;
    L = other.L;
  }

  public SegmentNode(long i1, long i2, long l) {
    I1 = i1;
    I2 = i2;
    L = l;
  }

  public boolean equals(SegmentNode other) {
    if (other instanceof RectangleNode) {
      return false;
    }

    if (I1 == other.I1 && I2 == other.I2 && L == other.L) {
      return true;
    }

    return false;
  }

  @Override
  public int compareTo(SegmentNode o) {
    long d;

    d = I1 - o.I1;
    if (d != 0) {
      return d < 0 ? -1 : 1;
    }

    d = I2 - o.I2;
    if (d != 0) {
      return d < 0 ? -1 : 1;
    }

    d = L - o.L;
    if (d != 0) {
      return d < 0 ? -1 : 1;
    }

    if (this instanceof RectangleNode && o instanceof RectangleNode) {
      d = ((RectangleNode) this).L_prime - ((RectangleNode) o).L_prime;
      if (d != 0) {
        return d < 0 ? -1 : 1;
      }
    }

    return 0;
  }

  public long xEnd() {
    return I1 + L;
  }

  public long yEnd() {
    return I2 + L;
  }

  /**
   * Testing if two figures are intersected. This interface implements standard intersection testing that ignores the
   * semantics of the X- and Y- axis. Processing the semantics issues before calling this method. A sample usage, please @see
   * heap_sensitive_intersection
   *
   * @param q
   * @return
   */
  public boolean intersect(SegmentNode q) {
    // Intersection with a rectangle is tested in the overrode method
    if (q instanceof RectangleNode) {
      return q.intersect(this);
    }

    SegmentNode p = this;

    if ((p.I2 - p.I1) == (q.I2 - q.I1)) {
      // Two segments have the same offset, so they are on the same line
      if (p.I1 <= q.I1) {
        return q.I1 < p.I1 + p.L;
      } else {
        return p.I1 < q.I1 + q.L;
      }
    }

    return false;
  }

  public boolean projYIntersect(SegmentNode q) {
    long py1 = this.I2;
    long py2 = yEnd();
    long qy1 = q.I2;
    long qy2 = q.yEnd();

    if (py1 <= qy1) {
      return qy1 < py2;
    }

    return py1 < qy2;
  }
}
