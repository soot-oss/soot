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
import soot.Type;
import soot.jimple.ClassConstant;
import soot.jimple.spark.sets.EqualsSupportingPointsToSet;
import soot.jimple.spark.sets.PointsToSetInternal;

public class WrappedPointsToSet implements EqualsSupportingPointsToSet {
  
  final PointsToSetInternal wrapped;

  public PointsToSetInternal getWrapped() {
    return wrapped;
  }

  public WrappedPointsToSet(final PointsToSetInternal wrapped) {
    super();
    this.wrapped = wrapped;
  }

  public boolean hasNonEmptyIntersection(PointsToSet other) {
    if (other instanceof AllocAndContextSet) {
      return other.hasNonEmptyIntersection(this);
    } else if (other instanceof WrappedPointsToSet) {
      return hasNonEmptyIntersection(((WrappedPointsToSet) other).getWrapped());
    } else {
      return wrapped.hasNonEmptyIntersection(other);
    }
  }

  public boolean isEmpty() {
    return wrapped.isEmpty();
  }

  public Set<ClassConstant> possibleClassConstants() {
    return wrapped.possibleClassConstants();
  }

  public Set<String> possibleStringConstants() {
    return wrapped.possibleStringConstants();
  }

  public Set<Type> possibleTypes() {
    return wrapped.possibleTypes();
  }

  public String toString() {
    return wrapped.toString();
  }
  
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if(obj==null) {
			return false;
		}
		if(this==obj) {
			return true;
		}		
		
		//have to get around the tyranny of reference losing equality
		if(obj instanceof WrappedPointsToSet) {
			WrappedPointsToSet wrapper = (WrappedPointsToSet) obj;
			
			return wrapped.equals(wrapper.wrapped);
		}
		
		return obj.equals(wrapped);
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return wrapped.hashCode();
	}

    /**
     * {@inheritDoc}
     */
    public boolean pointsToSetEquals(Object other) {
        if(!(other instanceof EqualsSupportingPointsToSet)) {
            return false;
        }
        EqualsSupportingPointsToSet otherPts = (EqualsSupportingPointsToSet) unwrapIfNecessary(other);
        return wrapped.pointsToSetEquals(otherPts);
    }

    /**
     * {@inheritDoc}
     */
    public int pointsToSetHashCode() {
        return wrapped.pointsToSetHashCode();
    }

    protected Object unwrapIfNecessary(Object obj) {
        if(obj instanceof WrappedPointsToSet) {
            WrappedPointsToSet wrapper = (WrappedPointsToSet) obj;
            obj = wrapper.wrapped;
        }
        return obj;
    }

}
