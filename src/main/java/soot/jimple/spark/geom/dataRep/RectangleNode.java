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
 * The rectangle figure for encoding the many-to-many relation.
 *
 * @author xiao
 *
 */
public class RectangleNode extends SegmentNode {

  // I1 : the starting x coordinate
  // I2 : the starting y coordinate
  // L : the length of the x-axis side
  // L_prime : the length of the y-axis side
  public long L_prime;

  public RectangleNode() {
  }

  public RectangleNode(RectangleNode other) {
    copyRectangle(other);
  }

  public void copyRectangle(RectangleNode other) {
    I1 = other.I1;
    I2 = other.I2;
    L = other.L;
    L_prime = other.L_prime;
  }

  public RectangleNode(long I1, long I2, long L, long LL) {
    super(I1, I2, L);
    L_prime = LL;
  }

  public boolean equals(RectangleNode other) {
    if (I1 == other.I1 && I2 == other.I2 && L == other.L && L_prime == other.L_prime) {
      return true;
    }

    return false;
  }

  @Override
  public long yEnd() {
    return I2 + L_prime;
  }

  @Override
  public boolean intersect(SegmentNode q) {
    RectangleNode p = this;

    if (q instanceof SegmentNode) {
      // If one of the end point is in the body of the rectangle
      if (point_within_rectangle(q.I1, q.I2, p) || point_within_rectangle(q.I1 + q.L - 1, q.I2 + q.L - 1, p)) {
        return true;
      }

      // Otherwise, the diagonal line must intersect with one of the boundary lines
      if (diagonal_line_intersect_horizontal(q, p.I1, p.I2, p.L)
          || diagonal_line_intersect_horizontal(q, p.I1, p.I2 + p.L_prime - 1, p.L)
          || diagonal_line_intersect_vertical(q, p.I1, p.I2, p.L_prime)
          || diagonal_line_intersect_vertical(q, p.I1 + p.L - 1, p.I2, p.L_prime)) {
        return true;
      }
    } else {
      RectangleNode rect_q = (RectangleNode) q;

      // If the segment is not entirely above, below, to the left, to the right of this rectangle
      // then, they must intersect

      if (p.I2 >= rect_q.I2 + rect_q.L_prime) {
        return false;
      }

      if (p.I2 + p.L_prime <= rect_q.I2) {
        return false;
      }

      if (p.I1 + p.L <= rect_q.I1) {
        return false;
      }

      if (p.I1 >= rect_q.I1 + rect_q.L) {
        return false;
      }

      return true;
    }

    return false;
  }

  private boolean point_within_rectangle(long x, long y, RectangleNode rect) {
    if (x >= rect.I1 && x < rect.I1 + rect.L) {
      if (y >= rect.I2 && y < rect.I2 + rect.L_prime) {
        return true;
      }
    }

    return false;
  }

  private boolean diagonal_line_intersect_vertical(SegmentNode p, long x, long y, long L) {
    if (x >= p.I1 && x < (p.I1 + p.L)) {
      long y_cross = x - p.I1 + p.I2;
      if (y_cross >= y && y_cross < y + L) {
        return true;
      }
    }

    return false;
  }

  private boolean diagonal_line_intersect_horizontal(SegmentNode p, long x, long y, long L) {
    if (y >= p.I2 && y < (p.I2 + p.L)) {
      long x_cross = y - p.I2 + p.I1;
      if (x_cross >= x && x_cross < x + L) {
        return true;
      }
    }

    return false;
  }
}
