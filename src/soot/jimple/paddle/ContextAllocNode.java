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
import java.util.*;

/** A pair of a node with a context.
 * @author Ondrej Lhotak
 */
public class ContextAllocNode extends ContextNode {
    /** Returns all context alloc dot field nodes having this node as their base. */
    public Iterator fields() { 
        return new Iterator() {
            private ContextAllocDotField frn = fieldNodes;
            public boolean hasNext() { return frn != null; }
            public Object next() { 
                if( frn == null ) throw new NoSuchElementException();
                Object ret = frn;
                frn = frn.nextByField;
                return ret;
            }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    public static ContextAllocNode get( Context ctxt, AllocNode node ) {
        return PaddleScene.v().nodeManager().get(ctxt, node);
    }
    public static ContextAllocNode make( Context ctxt, AllocNode node ) {
        return PaddleScene.v().nodeManager().make(ctxt, node);
    }
    ContextAllocNode( Context ctxt, AllocNode node ) {
        super(ctxt, node);
        node.addContext(this);
        PaddleNumberers.v().contextAllocNodeNumberer().add(this);
    }
    public AllocNode obj() { return (AllocNode) node; }
    public ContextAllocDotField dot( PaddleField field ) {
        return ContextAllocDotField.get( this, field );
    }
    void addField( ContextAllocDotField adf ) {
        adf.nextByField = fieldNodes;
        fieldNodes = adf;
    }
    protected ContextAllocDotField fieldNodes = null;
    ContextAllocNode nextByContext = null;
}
