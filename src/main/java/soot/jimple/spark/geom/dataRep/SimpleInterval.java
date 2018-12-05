package soot.jimple.spark.geom.dataRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2013 Richard Xiao
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
 * The basic representation for an interval on the integer domain. A simple interval is a half-open structure [L, R).
 * 
 * @author xiao
 *
 */
public class SimpleInterval implements Comparable<SimpleInterval> {
  public long L, R;

  public SimpleInterval() {
    L = 0;
    R = 1;
  }

  public SimpleInterval(long l, long r) {
    L = l;
    R = r;
  }

  public SimpleInterval(SimpleInterval o) {
    L = o.L;
    R = o.R;
  }

  @Override
  public String toString() {
    return "[" + L + ", " + R + ")";
  }

  @Override
  public boolean equals(Object o) {
    SimpleInterval other = (SimpleInterval) o;
    return (other.L == L) && (other.R == R);
  }

  @Override
  public int hashCode() {
    int ans = (int) ((L + R) % Integer.MAX_VALUE);
    if (ans < 0) {
      ans = -ans;
    }
    return ans;
  }

  @Override
  public int compareTo(SimpleInterval o) {
    if (L == o.L) {
      return R < o.R ? -1 : 1;
    }

    return L < o.L ? -1 : 1;
  }

  public boolean contains(SimpleInterval o) {
    SimpleInterval osi = (SimpleInterval) o;
    if (L <= osi.L && R >= osi.R) {
      return true;
    }
    return false;
  }

  public boolean merge(SimpleInterval o) {
    SimpleInterval osi = (SimpleInterval) o;

    if (osi.L < L) {
      if (L <= osi.R) {
        L = osi.L;
        if (R < osi.R) {
          R = osi.R;
        }
        return true;
      }
    } else {
      if (osi.L <= R) {
        if (R < osi.R) {
          R = osi.R;
        }
        return true;
      }
    }

    return false;
  }

  public boolean intersect(SimpleInterval o) {
    SimpleInterval osi = (SimpleInterval) o;

    if (L <= osi.L && osi.L < R) {
      return true;
    }
    if (osi.L <= L && L < osi.R) {
      return true;
    }
    return false;
  }
}
