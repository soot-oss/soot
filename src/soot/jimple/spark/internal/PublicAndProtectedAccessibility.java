/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.spark.internal;

import soot.G;
import soot.Singletons;
import soot.SootField;
import soot.SootMethod;

/**
 * Using this oracle one assumes, that a client of the target library can call
 * every public or protected method and access every public or protected field.
 * 
 * @author Florian Kuebler
 *
 */
public class PublicAndProtectedAccessibility implements ClientAccessibilityOracle {
	
    public PublicAndProtectedAccessibility( Singletons.Global g ) {	
    }
    
    public static PublicAndProtectedAccessibility v() { return G.v().soot_jimple_spark_internal_PublicAndProtectedAccessibility(); }

	@Override
	public boolean isAccessible(SootMethod method) {
		return method.isPublic() || method.isProtected();
	}

	@Override
	public boolean isAccessible(SootField field) {
		return field.isPublic() || field.isProtected();
	}

}
