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

import java.util.HashSet;
import java.util.Set;

import soot.PointsToSet;
import soot.Type;
import soot.jimple.ClassConstant;
import soot.jimple.spark.ondemand.genericutil.ArraySet;
import soot.jimple.spark.ondemand.genericutil.ImmutableStack;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ClassConstantNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.StringConstantNode;
import soot.jimple.spark.sets.EqualsSupportingPointsToSet;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;

public final class AllocAndContextSet extends ArraySet<AllocAndContext> implements EqualsSupportingPointsToSet {

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

  public Set<ClassConstant> possibleClassConstants() {
	  Set<ClassConstant> res = new HashSet<ClassConstant>();
	  for (AllocAndContext allocAndContext : this) {
          AllocNode n = allocAndContext.alloc;
          if( n instanceof ClassConstantNode ) {
			res.add( ((ClassConstantNode)n).getClassConstant() );
          } else {
        	  return null;
          }
      }
	  return res;
  }

  public Set<String> possibleStringConstants() {
	  Set<String> res = new HashSet<String>();
	  for (AllocAndContext allocAndContext : this) {
          AllocNode n = allocAndContext.alloc;
          if( n instanceof StringConstantNode ) {
			res.add( ((StringConstantNode)n).getString() );
          } else {
        	  return null;
          }
      }
	  return res;
  }

  public Set<Type> possibleTypes() {
	  Set res = new HashSet<Type>();
      for (AllocAndContext allocAndContext : this) {
    	  res.add(allocAndContext.alloc.getType());
      }
      return res;
  }
  
  /**
   * Computes a hash code based on the contents of the points-to set.
   * Note that hashCode() is not overwritten on purpose.
   * This is because Spark relies on comparison by object identity.
   */
  public int pointsToSetHashCode() {
      final int PRIME = 31;
      int result = 1;
      for (AllocAndContext elem : this) {
          result = PRIME * result + elem.hashCode();
      }
      return result;
  }
  
  /**
   * Returns <code>true</code> if and only if other holds the same alloc nodes as this.
   * Note that equals() is not overwritten on purpose.
   * This is because Spark relies on comparison by object identity.
   */
  public boolean pointsToSetEquals(Object other) {
      if(this==other) {
          return true;
      }
      if(!(other instanceof AllocAndContextSet)) {
          return false;
      }
      AllocAndContextSet otherPts = (AllocAndContextSet) other;
      
      //both sets are equal if they are supersets of each other 
      return superSetOf(otherPts, this) && superSetOf(this, otherPts);        
  }
  
  /**
   * Returns <code>true</code> if <code>onePts</code> is a (non-strict) superset of <code>otherPts</code>.
   */
  private boolean superSetOf(AllocAndContextSet onePts, final AllocAndContextSet otherPts) {
      return onePts.containsAll(otherPts);
  }
}