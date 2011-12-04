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

package soot.jimple.spark.pag;
import soot.Context;

/** Represents a simple variable node with context.
 * @author Ondrej Lhotak
 */
public class ContextVarNode extends LocalVarNode {
    private Context context;
    public Context context() { return context; }
    public String toString() {
	return "ContextVarNode "+getNumber()+" "+variable+" "+method+" "+context;
    }

    /* End of public methods. */

    ContextVarNode( PAG pag, LocalVarNode base, Context context ) {
	super( pag, base.getVariable(), base.getType(), base.getMethod() );
        this.context = context;
        base.addContext( this, context );
    }
}

