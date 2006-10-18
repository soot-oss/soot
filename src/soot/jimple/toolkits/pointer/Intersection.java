/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Eric Bodden
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

package soot.jimple.toolkits.pointer;
import java.util.*;

import soot.*;
import soot.jimple.paddle.EmptyPointsToSet;
import soot.jimple.paddle.PointsToSetReadOnly;
import soot.jimple.toolkits.pointer.FullObjectSet;
import soot.jimple.toolkits.pointer.Union;

/**
 * Implements an intersection of two points-to sets without altering them.
 *
 * @author Eric Bodden
 */
public class Intersection implements PointsToSet {

	public static int maxDepth = 0;
	
	/** The two sets. */
	protected PointsToSet s1, s2;
	
	/** set representation for easier equality check */
	private Set asSet;
	
    /**
     * Creates an intersection of <code>s1</code> and <code>s2</code>.
	 * @param s1 the set to intersect with <code>s2</code>
	 * @param s2 the set to intersect with <code>s1</code>
	 */
	protected Intersection(PointsToSet s1, PointsToSet s2) {
		if(s1 instanceof PointsToSetReadOnly) {
			s1 = new PaddlePointsToSetCompatibilityWrapper((PointsToSetReadOnly)s1);
			new RuntimeException("Internal error").printStackTrace();
		}
		if(s2 instanceof PointsToSetReadOnly) {
			s2 = new PaddlePointsToSetCompatibilityWrapper((PointsToSetReadOnly)s2);
			new RuntimeException("Internal error").printStackTrace();
		}
		this.s1 = s1;
		this.s2 = s2;
		
		this.asSet = new HashSet(2);
		this.asSet.add(this.s1);
		this.asSet.add(this.s2);
		
		int depth = this.depth();
		if(depth>maxDepth) maxDepth = depth;
	}

    /**
     * {@inheritDoc}
     */
    public Set possibleStringConstants() { return null; }
    
    /**
     * {@inheritDoc}
     */
    public Set possibleClassConstants() { return null; }

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNonEmptyIntersection(PointsToSet s3) {
		if(s1==null||s2==null||s3==null) {
			//if any of the sets is null, we certainly have an empty
			//intersection
			return false;
		} else {
			//otherwise, we have to check pairwise (set intersection is not
			//transitive)
			return s1.hasNonEmptyIntersection(s2) &&
				s2.hasNonEmptyIntersection(s3) &&				
				s3.hasNonEmptyIntersection(s1);				
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		//the intersection is empty if either of the two sets are empty or if they don't intersect
		return s1==null || s2==null || s1.isEmpty() || s2.isEmpty() || !s1.hasNonEmptyIntersection(s2);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set possibleTypes() {
		if (s1==null||s2==null) {
			return Collections.EMPTY_SET;
		} else {
			//intersect the possible types if s1 and s2
			Set ret = s1.possibleTypes();
			ret.retainAll(s2.possibleTypes());
			return ret;
		}		
	}
	
	/**
	 * Intersects p1 and p2.
	 * TODO comment
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static PointsToSet intersect(PointsToSet p1, PointsToSet p2) {
		if(p1==null || p2==null || p1==EmptyPointsToSet.v() || p2==EmptyPointsToSet.v()) {
			//if either is empty, the intersection is empty
			return EmptyPointsToSet.v();
		} else {
			//if either is the full set, just return the other one
			if(p1==FullObjectSet.v()) {
				return p2;
			} else if(p2==FullObjectSet.v()) {
				return p1;
			} else if(p1.equals(p2)) {
				return p1;
			} else {
				//else create a new Intersection
				return new Intersection(p1,p2);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((asSet == null) ? 0 : asSet.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Intersection other = (Intersection) obj;
		if (asSet == null) {
			if (other.asSet != null)
				return false;
		} else if (!asSet.equals(other.asSet))
			return false;
		return true;
	}
	
	public int depth() {
		int left = 0, right = 0;
		if(s1 instanceof Intersection) {
			Intersection is = (Intersection) s1;
			left = is.depth();
		} else if(s1 instanceof Union) {
			Union un = (Union) s1;
			left = un.depth();
		} 
		if(s2 instanceof Intersection) {
			Intersection is = (Intersection) s2;
			right = is.depth();
		} else if(s2 instanceof Union) {
			Union un = (Union) s2;
			right = un.depth();
		} 
		return Math.max(left, right)+1;		
	}
	

}

