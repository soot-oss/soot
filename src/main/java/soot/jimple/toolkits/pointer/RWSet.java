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

/** Represents the read or write set of a statement. */
public abstract class RWSet {
    public abstract boolean getCallsNative();
    public abstract boolean setCallsNative();

    /** Returns an iterator over any globals read/written. */
    public abstract int size();
    public abstract Set<?> getGlobals();
    public abstract Set<?> getFields();
    public abstract PointsToSet getBaseForField( Object f );
    public abstract boolean hasNonEmptyIntersection( RWSet other );
    /** Adds the RWSet other into this set. */
    public abstract boolean union( RWSet other );
    public abstract boolean addGlobal( SootField global );
    public abstract boolean addFieldRef( PointsToSet otherBase, Object field );
    public abstract boolean isEquivTo( RWSet other );
}
