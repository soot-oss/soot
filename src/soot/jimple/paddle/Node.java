/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003 Ondrej Lhotak
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

package soot.jimple.paddle;
import soot.*;
import soot.util.*;
import soot.Type;
import soot.jimple.paddle.PointsToSetInternal;
import soot.jimple.paddle.EmptyPointsToSet;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;

/** Represents every node in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class Node implements ReferenceVariable, Numberable {
    public final int hashCode() { return number; }
    public final boolean equals( Object other ) { 
        return this == other;
    }
    /** Returns the declared type of this node, null for unknown. */
    public Type getType() { return type; }
    /** Sets the declared type of this node, null for unknown. */
    public void setType( Type type ) { this.type = type; }

    /* End of public methods. */

    /** Creates a new node of pointer assignment graph pag, with type type. */
    Node( Type type ) {
	this.type = type;
    }

    /* End of package methods. */

    public final int getNumber() { return number; }
    public final void setNumber( int number ) { this.number = number; }

    private int number = 0;

    protected Type type;
}
