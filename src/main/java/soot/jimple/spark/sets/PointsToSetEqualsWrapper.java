/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Eric Bodden
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
package soot.jimple.spark.sets;

import java.util.Set;

import soot.PointsToSet;
import soot.Type;
import soot.jimple.ClassConstant;

/**
 * A decorator that implements equals/hashCode for {@link PointsToSet} supporting the {@link EqualsSupportingPointsToSet} interface.
 *
 * @author Eric Bodden
 */
public class PointsToSetEqualsWrapper implements PointsToSet {

    protected EqualsSupportingPointsToSet pts;

    public PointsToSetEqualsWrapper(EqualsSupportingPointsToSet pts) {
        this.pts = pts;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {        
        //delegate
        return pts.pointsToSetHashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if(this==obj || this.pts==obj) {
            return true;
        }
        
        //unwrap other
        obj = unwrapIfNecessary(obj);       
        //delegate
        return pts.pointsToSetEquals(obj);
    }

    /**
     * @param other
     * @return
     * @see soot.PointsToSet#hasNonEmptyIntersection(soot.PointsToSet)
     */
    public boolean hasNonEmptyIntersection(PointsToSet other) {
        //unwrap other
        other = (PointsToSet) unwrapIfNecessary(other);       
        return pts.hasNonEmptyIntersection(other);
    }

    /**
     * @return
     * @see soot.PointsToSet#isEmpty()
     */
    public boolean isEmpty() {
        return pts.isEmpty();
    }

    /**
     * @return
     * @see soot.PointsToSet#possibleClassConstants()
     */
    public Set<ClassConstant> possibleClassConstants() {
        return pts.possibleClassConstants();
    }

    /**
     * @return
     * @see soot.PointsToSet#possibleStringConstants()
     */
    public Set<String> possibleStringConstants() {
        return pts.possibleStringConstants();
    }

    /**
     * @return
     * @see soot.PointsToSet#possibleTypes()
     */
    public Set<Type> possibleTypes() {
        return pts.possibleTypes();
    }
    
    protected Object unwrapIfNecessary(Object obj) {
        if(obj instanceof PointsToSetEqualsWrapper) {
            PointsToSetEqualsWrapper wrapper = (PointsToSetEqualsWrapper) obj;
            obj = wrapper.pts;
        }
        return obj;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return pts.toString();
    }
}
