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

/** Represents a field reference node (Red) in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class FieldRefNode extends Node {
    public static FieldRefNode get( VarNode var, PaddleField field ) {
        return PaddleScene.v().nodeManager().get( var, field );
    }
    public static FieldRefNode make( VarNode var, PaddleField field ) {
        return PaddleScene.v().nodeManager().make( var, field );
    }
    /** Returns all context var nodes having this node as their base. */
    public Iterator contexts() { 
        return new Iterator() {
            private ContextFieldRefNode cvn = contextNodes;
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

    /** Returns the base of this field reference. */
    public VarNode base() { return base; }
    /** Returns the field of this field reference. */
    public PaddleField field() { return field; }
    public String toString() {
	return "FieldRefNode "+base+"."+field;
    }

    public Type getType() { return null; }

    FieldRefNode( VarNode base, PaddleField field ) {
	if( field == null ) throw new RuntimeException( "null field" );
	this.base = base;
	this.field = field;
	base.addField( this );
        PaddleNumberers.v().fieldRefNodeNumberer().add( this );
    }
    void addContext( ContextFieldRefNode cvn ) {
        cvn.nextByContext = contextNodes;
        contextNodes = cvn;
    }

    /* End of package methods. */

    protected VarNode base;
    protected PaddleField field;
    protected ContextFieldRefNode contextNodes = null;
    FieldRefNode nextByField = null;
}

