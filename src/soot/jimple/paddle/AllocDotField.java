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

package soot.jimple.paddle;
import soot.*;
import java.util.*;

/** Represents an alloc-site-dot-field node (Yellow) in the pointer
 * assignment graph.
 * @author Ondrej Lhotak
 */
public class AllocDotField extends Node {
    /** Returns all context var nodes having this node as their base. */
    public Iterator contexts() { 
        return new Iterator() {
            private ContextAllocDotField cvn = contextNodes;
            public boolean hasNext() { return cvn != null; }
            public Object next() { 
                if( cvn == null ) throw new NoSuchElementException();
                Object ret = cvn;
                cvn = cvn.nextByContext;
                return ret;
            }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    /** Returns the base AllocNode. */
    public AllocNode base() { return base; }
    /** Returns the field of this node. */
    public PaddleField field() { return field; }
    public String toString() {
	return "AllocDotField "+base+"."+field;
    }

    /* End of public methods. */

    public static AllocDotField get( AllocNode base, PaddleField field ) {
        return PaddleScene.v().nodeManager().get(base, field);
    }
    public static AllocDotField make( AllocNode base, PaddleField field ) {
        return PaddleScene.v().nodeManager().make(base, field);
    }
    AllocDotField( AllocNode base, PaddleField field ) {
	if( field == null ) throw new RuntimeException( "null field" );
	this.base = base;
	this.field = field;
	base.addField(this);
        PaddleNumberers.v().allocDotFieldNumberer().add( this );
    }

    public Type getType() { return null; }
    void addContext( ContextAllocDotField cvn ) {
        cvn.nextByContext = contextNodes;
        contextNodes = cvn;
    }

    /* End of package methods. */

    protected AllocNode base;
    protected PaddleField field;
    protected ContextAllocDotField contextNodes = null;
    AllocDotField nextByField = null;
}

