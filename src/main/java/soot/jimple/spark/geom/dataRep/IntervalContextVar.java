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

import soot.jimple.spark.pag.Node;

/**
 * A particular class to encode contexts in interval manner.
 * 
 * @author xiao
 *
 */
public class IntervalContextVar extends ContextVar implements Comparable<IntervalContextVar> {

  // The interval is [L, R), which stands for a set of consecutive contexts
  public long L = 0, R = 0;

  public IntervalContextVar() {
  }

  public IntervalContextVar(long l, long r, Node v) {
    assert l < r;
    L = l;
    R = r;
    var = v;
  }

  public IntervalContextVar(IntervalContextVar o) {
    L = o.L;
    R = o.R;
    var = o.var;
  }

  @Override
  public String toString() {
    return "<" + var.toString() + ", " + L + ", " + R + ">";
  }

  @Override
  public boolean equals(Object o) {
    IntervalContextVar other = (IntervalContextVar) o;
    return (other.L == L) && (other.R == R) && (other.var == var);
  }

  @Override
  public int hashCode() {
    int ch = (int) ((L + R) % Integer.MAX_VALUE);
    int ans = var.hashCode() + ch;
    if (ans < 0) {
      ans = var.hashCode();
    }
    return ans;
  }

  @Override
  public int compareTo(IntervalContextVar o) {
    if (L == o.L) {
      return R < o.R ? -1 : 1;
    }

    return L < o.L ? -1 : 1;
  }

  @Override
  public boolean contains(ContextVar cv) {
    IntervalContextVar icv = (IntervalContextVar) cv;
    if (L <= icv.L && R >= icv.R) {
      return true;
    }
    return false;
  }

  @Override
  public boolean merge(ContextVar cv) {
    IntervalContextVar icv = (IntervalContextVar) cv;

    if (icv.L < L) {
      if (L <= icv.R) {
        L = icv.L;
        if (R < icv.R) {
          R = icv.R;
        }
        return true;
      }
    } else {
      if (icv.L <= R) {
        if (R < icv.R) {
          R = icv.R;
        }
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean intersect(ContextVar cv) {
    IntervalContextVar icv = (IntervalContextVar) cv;

    if (L <= icv.L && icv.L < R) {
      return true;
    }
    if (icv.L <= L && L < icv.R) {
      return true;
    }
    return false;
  }
}
