/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003, 2004 Ondrej Lhotak
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

/** Represents an allocation site node (Blue) in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public abstract class AllocNode extends Node implements Context {
    /** Returns all field ref nodes having this node as their base. */
    public Iterator fields() { 
        return new Iterator() {
            private AllocDotField frn = fieldNodes;
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
            private ContextAllocNode cvn = contextNodes;
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

    /** Returns the new expression of this allocation site. */
    public Object getNewExpr() { return newExpr; }
    
    /** Returns the field ref node having this node as its base,
     * and field as its field; null if nonexistent. */
    public AllocDotField dot( PaddleField field ) 
    { return AllocDotField.get( this, field ); }

    public Type getType() { return type; }
    protected Type type;

    /* End of public methods. */

    AllocNode( Object newExpr, Type t ) {
	this.type = t;
        if( t instanceof RefType ) {
            RefType rt = (RefType) t;
            if( rt.getSootClass().isAbstract() ) {
                throw new RuntimeException( "Attempt to create allocnode with abstract type "+t );
            }
        }
	this.newExpr = newExpr;
        if( newExpr instanceof ContextVarNode ) throw new RuntimeException();
        PaddleNumberers.v().allocNodeNumberer().add( this );
    }
    /** Registers a AllocDotField as having this node as its base. */
    void addField( AllocDotField adf ) {
        adf.nextByField = fieldNodes;
        fieldNodes = adf;
    }

    void addContext( ContextAllocNode cvn ) {
        cvn.nextByContext = contextNodes;
        contextNodes = cvn;
    }

    /* End of package methods. */

    protected Object newExpr;
    protected ContextAllocNode contextNodes = null;
    protected AllocDotField fieldNodes = null;
}

