/* Soot - a J*va Optimization Framework
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

package soot.jimple.paddle;
import soot.*;

/** A pair of a node with a context.
 * @author Ondrej Lhotak
 */
public class ContextNode extends Node {
    public static ContextNode get( Context ctxt, Node node ) {
        return make(ctxt, node);
    }
    public static ContextNode make( Context ctxt, Node node ) {
        if( node instanceof VarNode ) 
            return ContextVarNode.make( ctxt, (VarNode) node );
        if( node instanceof FieldRefNode ) 
            return ContextFieldRefNode.make( ctxt, (FieldRefNode) node );
        if( node instanceof AllocNode ) 
            return ContextAllocNode.make( ctxt, (AllocNode) node );
        throw new RuntimeException(node.getClass().toString());
    }
    protected ContextNode( Context ctxt, Node node ) {
        this.ctxt = ctxt;
        this.node = node;
    }
    protected Context ctxt;
    public Context ctxt() { return ctxt; }
    protected Node node;
    public Node node() { return node; }

    public Type getType() { return node.getType(); }
    public String toString() {
        return node+" in context "+ctxt;
    }
}
