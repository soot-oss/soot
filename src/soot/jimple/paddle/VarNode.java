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

/** Represents a simple variable node (Green) in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public abstract class VarNode extends Node {
    /** Returns all field ref nodes having this node as their base. */
    public Iterator fields() { 
        return new Iterator() {
            private FieldRefNode frn = fieldNodes;
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
    /** Returns all context var nodes having this node as their base. */
    public Iterator contexts() { 
        return new Iterator() {
            private ContextVarNode cvn = contextNodes;
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

    /** Returns the field ref node having this node as its base,
     * and field as its field; null if nonexistent. */
    public FieldRefNode dot( PaddleField field ) 
    { return FieldRefNode.get( this, field ); }
    /** Returns the underlying variable that this node represents. */
    public Object getVariable() {
        return variable;
    }

    public Type getType() { return type; }
    protected Type type;

    /* End of public methods. */

    VarNode( Object variable, Type t ) {
	this.type = t;
	if( !(t instanceof RefLikeType) || t instanceof AnySubType ) {
	    throw new RuntimeException( "Attempt to create VarNode of type "+t );
	}
	this.variable = variable;
        PaddleNumberers.v().varNodeNumberer().add(this);
    }
    /** Registers a frn as having this node as its base. */
    void addField( FieldRefNode frn ) {
        frn.nextByField = fieldNodes;
        fieldNodes = frn;
    }


    void addContextNode( ContextVarNode cvn ) {
        cvn.nextByContext = contextNodes;
        contextNodes = cvn;
    }

    /* End of package methods. */

    protected Object variable;
    protected ContextVarNode contextNodes = null;
    protected FieldRefNode fieldNodes = null;
}

