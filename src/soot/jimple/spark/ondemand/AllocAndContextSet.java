/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.jimple.spark.ondemand;

import java.util.Set;

import soot.PointsToSet;
import soot.jimple.spark.ondemand.genericutil.ArraySet;
import soot.jimple.spark.ondemand.genericutil.ImmutableStack;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;

public final class AllocAndContextSet extends ArraySet<AllocAndContext> implements PointsToSet {

  public boolean hasNonEmptyIntersection(PointsToSet other) {
    if (other instanceof AllocAndContextSet) {
      return nonEmptyHelper((AllocAndContextSet) other);
    } else if (other instanceof WrappedPointsToSet) {
      return hasNonEmptyIntersection(((WrappedPointsToSet) other).getWrapped());
    } else if (other instanceof PointsToSetInternal) {
      return ((PointsToSetInternal) other).forall(new P2SetVisitor() {

        @Override
        public void visit(Node n) {
          if (!returnValue) {
            for (AllocAndContext allocAndContext : AllocAndContextSet.this) {
              if (n.equals(allocAndContext.alloc)) {
                returnValue = true;
                break;
              }
            }
          }
        }

      });
    }
    throw new UnsupportedOperationException("can't check intersection with set of type " + other.getClass());
  }

  private boolean nonEmptyHelper(AllocAndContextSet other) {
    for (AllocAndContext otherAllocAndContext : other) {
      for (AllocAndContext myAllocAndContext : this) {
        if (otherAllocAndContext.alloc.equals(myAllocAndContext.alloc)) {
          ImmutableStack<Integer> myContext = myAllocAndContext.context;
          ImmutableStack<Integer> otherContext = otherAllocAndContext.context;
          if (myContext.topMatches(otherContext) || otherContext.topMatches(myContext)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public Set possibleClassConstants() {
    throw new UnsupportedOperationException();
  }

  public Set possibleStringConstants() {
    throw new UnsupportedOperationException();
  }

  public Set possibleTypes() {
    throw new UnsupportedOperationException();
  }
}