/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.dava.internal.javaRep;

import soot.*;
import soot.jimple.*;

public class DStaticFieldRef extends StaticFieldRef 
{
    private boolean supressDeclaringClass;

    public void toString( UnitPrinter up ) {
        if( !supressDeclaringClass ) {
            up.type( fieldRef.declaringClass().getType() );
            up.literal( "." );
        }
        up.fieldRef( fieldRef );
    }

    public DStaticFieldRef( SootFieldRef fieldRef, String myClassName)
    {
	super( fieldRef);
	supressDeclaringClass = myClassName.equals( fieldRef.declaringClass().getName());
    }

    public DStaticFieldRef( SootFieldRef fieldRef, boolean supressDeclaringClass)
    {
	super( fieldRef);
	this.supressDeclaringClass = supressDeclaringClass;
    }

    public Object clone()
    {
	return new DStaticFieldRef( fieldRef, supressDeclaringClass);
    }
}
