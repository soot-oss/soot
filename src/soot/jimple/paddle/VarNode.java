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
import soot.jimple.paddle.*;
import soot.*;
import java.util.*;

/** Represents a simple variable node (Green) in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public abstract class VarNode extends Node implements Comparable {
    public Context context() { return null; }
    /** Returns all field ref nodes having this node as their base. */
    public Collection getAllFieldRefs() { 
	if( fields == null ) return Collections.EMPTY_LIST;
	return fields.values(); 
    }
    /** Returns the field ref node having this node as its base,
     * and field as its field; null if nonexistent. */
    public FieldRefNode dot( PaddleField field ) 
    { return fields == null ? null : (FieldRefNode) fields.get( field ); }
    public int compareTo( Object o ) {
	VarNode other = (VarNode) o;
        if( other.finishingNumber == finishingNumber && other != this ) {
            G.v().out.println( "This is: "+this+" with id "+getNumber()+" and number "+finishingNumber );
            G.v().out.println( "Other is: "+other+" with id "+other.getNumber()+" and number "+other.finishingNumber );
            throw new RuntimeException("Comparison error" );
        }
	return other.finishingNumber - finishingNumber;
    }
    public void setFinishingNumber( int i ) {
        finishingNumber = i;
        if( i > nm.maxFinishNumber ) nm.maxFinishNumber = i;
    }
    /** Returns the underlying variable that this node represents. */
    public Object getVariable() {
        return variable;
    }

    /* End of public methods. */

    VarNode( NodeManager nm, Object variable, Type t ) {
	super( t );
	if( !(t instanceof RefLikeType) || t instanceof AnySubType ) {
	    throw new RuntimeException( "Attempt to create VarNode of type "+t );
	}
	this.variable = variable;
	this.nm = nm;
        PaddleNumberers.v().varNodeNumberer().add(this);
        setFinishingNumber( ++nm.maxFinishNumber );
    }
    /** Registers a frn as having this node as its base. */
    void addField( FieldRefNode frn, PaddleField field ) {
	if( fields == null ) fields = new HashMap();
	fields.put( field, frn );
    }

    /* End of package methods. */

    protected Object variable;
    protected Map fields;
    protected int finishingNumber = 0;
    protected NodeManager nm;
}

