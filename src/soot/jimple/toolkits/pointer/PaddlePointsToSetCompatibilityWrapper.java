/* abc - The AspectBench Compiler
 * Copyright (C) 2006 Eric Bodden
 *
 * This compiler is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This compiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this compiler, in the file LESSER-GPL;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package soot.jimple.toolkits.pointer;

import java.util.Set;

import soot.PointsToSet;
import soot.jimple.paddle.PointsToSetInternal;
import soot.jimple.paddle.PointsToSetReadOnly;
import soot.jimple.toolkits.pointer.Union;

/**
 * This class wraps a paddle points-to set in order to achive compatibility with
 * points-to sets of a type different from {@link PointsToSetInternal}.
 * The main difference lies in {@link #hasNonEmptyIntersection(PointsToSet)}.
 *
 * @author Eric Bodden
 */
public class PaddlePointsToSetCompatibilityWrapper implements PointsToSet {

	protected final PointsToSetReadOnly paddlePts;

	/**
	 * A paddle points-to set to wrap.
	 * @param paddlePts
	 */
	public PaddlePointsToSetCompatibilityWrapper(PointsToSetReadOnly paddlePts) {
		this.paddlePts = paddlePts;
	}

	/**
	 * Delegates so that <code>other</code> compares itself to
	 * <code>this</code>, respectively the wrapped object.
	 * That way, the object itself can do the comparison, which
	 * the paddle points-to set would not be capable of.  
	 */
	public boolean hasNonEmptyIntersection(PointsToSet other) {
	    if(other instanceof PaddlePointsToSetCompatibilityWrapper) {
			PaddlePointsToSetCompatibilityWrapper wrapper = (PaddlePointsToSetCompatibilityWrapper) other;
			return paddlePts.hasNonEmptyIntersection(wrapper.paddlePts);
		} else if(other instanceof PointsToSetReadOnly)
	    	return paddlePts.hasNonEmptyIntersection(other);
		else if(other instanceof Union)
	        return other.hasNonEmptyIntersection(this);
	    else if(other instanceof Intersection)
	        return other.hasNonEmptyIntersection(this);
	    else throw new RuntimeException("unexpected set type: "+other.getClass().getName());
	}

	/**
	 * @return
	 * @see soot.PointsToSet#isEmpty()
	 */
	public boolean isEmpty() {
		return paddlePts.isEmpty();
	}

	/**
	 * @return
	 * @see soot.jimple.paddle.PointsToSetReadOnly#possibleClassConstants()
	 */
	public Set possibleClassConstants() {
		return paddlePts.possibleClassConstants();
	}

	/**
	 * @return
	 * @see soot.jimple.paddle.PointsToSetReadOnly#possibleStringConstants()
	 */
	public Set possibleStringConstants() {
		return paddlePts.possibleStringConstants();
	}

	/**
	 * @return
	 * @see soot.jimple.paddle.PointsToSetReadOnly#possibleTypes()
	 */
	public Set possibleTypes() {
		return paddlePts.possibleTypes();
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
		if(obj instanceof PaddlePointsToSetCompatibilityWrapper) {
			PaddlePointsToSetCompatibilityWrapper wrapper = (PaddlePointsToSetCompatibilityWrapper) obj;
			return paddlePts.equals(wrapper.paddlePts);
		}
		
		return obj.equals(paddlePts);
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return paddlePts.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return paddlePts.toString();
	}
}
