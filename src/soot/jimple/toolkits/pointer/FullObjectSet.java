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
import java.util.*;

import soot.*;
import soot.jimple.ClassConstant;

public class FullObjectSet extends Union {
    public FullObjectSet( Singletons.Global g ) {
        this( RefType.v( "java.lang.Object" ) );
    }
    public static FullObjectSet v() { return G.v().soot_jimple_toolkits_pointer_FullObjectSet(); }
    public static FullObjectSet v( RefType t ) { 
        if( t.getClassName().equals( "java.lang.Object" ) ) {
            return v();
        }
        return new FullObjectSet( t );
    }
    private final Set<Type> types;
    private FullObjectSet( RefType declaredType ) {
        Type type = AnySubType.v( declaredType );
        types = Collections.singleton( type );
    }

    public Type type() { return types.iterator().next(); }

    /** Returns true if this set contains no run-time objects. */
    public boolean isEmpty() {
	return false;
    }
    /** Returns true if this set is a subset of other. */
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
	return other != null;
    }
    
    /** Set of all possible run-time types of objects in the set. */
    @Override
    public Set<Type> possibleTypes() {
        return types;
    }

    /** Adds all objects in s into this union of sets, returning true if this
     * union was changed. */
    public boolean addAll( PointsToSet s ) {
	return false;
    }

    public Set<String> possibleStringConstants() { return null; }
    public Set<ClassConstant> possibleClassConstants() { return null; }
	
    /**
	 * {@inheritDoc}
	 */
	public int depth() {
		return 0;
	}
}

