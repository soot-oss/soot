/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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
import soot.*;
import java.util.*;

public class MemoryEfficientRasUnion extends Union {
    HashSet subsets;

    public boolean isEmpty() {
	if( subsets == null ) return true;
	for( Iterator subsetIt = subsets.iterator(); subsetIt.hasNext(); ) {
	    final PointsToSet subset = (PointsToSet) subsetIt.next();
	    if( !subset.isEmpty() ) return false;
	}
	return true;
    }
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
	if( subsets == null ) return true;
	for( Iterator subsetIt = subsets.iterator(); subsetIt.hasNext(); ) {
	    final PointsToSet subset = (PointsToSet) subsetIt.next();
	    if( other instanceof Union ) {
		if( other.hasNonEmptyIntersection( subset ) ) return true;
	    } else {
		if( subset.hasNonEmptyIntersection( other ) ) return true;
	    }
	}
	return false;
    }
    public boolean addAll( PointsToSet s ) {
	boolean ret = false;
	if( subsets == null ) subsets = new HashSet();
	if( s instanceof Union ) {
	    MemoryEfficientRasUnion meru = (MemoryEfficientRasUnion) s;
	    if( meru.subsets == null || subsets.containsAll( meru.subsets ) ) {
		return false;
	    }
	    return subsets.addAll( meru.subsets );
	} else {
	    PointsToSet r = (PointsToSet) s;
	    return subsets.add( s );
	}
    }
    public Object clone() {
	MemoryEfficientRasUnion ret = new MemoryEfficientRasUnion();
	ret.addAll( this );
	return ret;
    }
    public Set possibleTypes() {
	if( subsets == null ) {
	    return Collections.EMPTY_SET;
	}
	HashSet ret = new HashSet();
	for( Iterator subsetIt = subsets.iterator(); subsetIt.hasNext(); ) {
	    final PointsToSet subset = (PointsToSet) subsetIt.next();
	    ret.addAll( subset.possibleTypes() );
	}
	return ret;
    }
    public MemoryEfficientRasUnion() {
    }


}
