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
public class ContextVarNode extends ContextNode implements Comparable {
    /** Returns all field ref nodes having this node as their base. */
    public Iterator fields() { 
        return new Iterator() {
            private ContextFieldRefNode frn = fieldNodes;
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
    public static ContextVarNode get( Context ctxt, VarNode node ) {
        return PaddleScene.v().nodeManager().get(ctxt, node);
    }
    public static ContextVarNode make( Context ctxt, VarNode node ) {
        return PaddleScene.v().nodeManager().make(ctxt, node);
    }
    ContextVarNode( Context ctxt, VarNode node ) {
        super( ctxt, node );
        setFinishingNumber( ++(PaddleScene.v().nodeManager().maxFinishNumber) );
        node.addContextNode(this);
        PaddleNumberers.v().contextVarNodeNumberer().add(this);
    }
    public VarNode var() { return (VarNode) node; }
    public ContextFieldRefNode dot( PaddleField field ) {
        return ContextFieldRefNode.get( ctxt, FieldRefNode.get( var(), field ) );
    }

    public int compareTo( Object o ) {
	ContextVarNode other = (ContextVarNode) o;
        if( other.finishingNumber == finishingNumber && other != this ) {
            G.v().out.println( "This is: "+this+" with id "+getNumber()+" and number "+finishingNumber );
            G.v().out.println( "Other is: "+other+" with id "+other.getNumber()+" and number "+other.finishingNumber );
            throw new RuntimeException("Comparison error" );
        }
	return other.finishingNumber - finishingNumber;
    }
    public void setFinishingNumber( int i ) {
        finishingNumber = i;
        if( i > PaddleScene.v().nodeManager().maxFinishNumber ) PaddleScene.v().nodeManager().maxFinishNumber = i;
    }
    protected int finishingNumber = 0;
    public int finishingNumber() { return finishingNumber; }
    void addField( ContextFieldRefNode frn ) {
        frn.nextByField = fieldNodes;
        fieldNodes = frn;
    }
    protected ContextFieldRefNode fieldNodes;
    ContextVarNode nextByContext = null;
}
