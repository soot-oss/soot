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
import soot.jimple.spark.*;
import soot.*;
import java.util.*;

/** Represents a simple variable node (Green) in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class VarNode extends ValNode implements Comparable {
    /** Returns all field ref nodes having this node as their base. */
    public Collection getAllFieldRefs() { 
	if( fields == null ) return Collections.EMPTY_LIST;
	return fields.values(); 
    }
    /** Returns the field ref node having this node as its base,
     * and field as its field; null if nonexistent. */
    public FieldRefNode dot( SparkField field ) 
    { return fields == null ? null : (FieldRefNode) fields.get( field ); }
    public String toString() {
	return "VarNode "+id+" "+value+" "+method;
    }
    public int compareTo( Object o ) {
	VarNode other = (VarNode) o;
        if( other.finishingNumber == finishingNumber && other != this ) {
            System.out.println( "This is: "+this+" with id "+getId()+" and number "+finishingNumber );
            System.out.println( "Other is: "+other+" with id "+other.getId()+" and number "+other.finishingNumber );
            throw new RuntimeException("Comparison error" );
        }
	return other.finishingNumber - finishingNumber;
    }
    public void setFinishingNumber( int i ) {
        finishingNumber = i;
        finishingNumbersSet = true;
    }
    /** NOTE: The method is here only for dumping the graph; not all VarNodes
     * will have a method so don't rely on it.
     */
    public SootMethod getMethod() {
        return method;
    }
    /** Returns the underlying value that this node represents. */
    public Object getValue() {
        return value;
    }

    /** Designates this node as the potential target of a interprocedural 
     * assignment edge which may be added during on-the-fly call graph 
     * updating. */
    public void setInterProcTarget() {
        interProcTarget = true;
    }
    /** Returns true if this node is the potential target of a interprocedural 
     * assignment edge which may be added during on-the-fly call graph 
     * updating. */
    public boolean isInterProcTarget() {
        return interProcTarget;
    }

    /* End of public methods. */

    VarNode( PAG pag, Object value, Type t, SootMethod m ) {
	super( pag, t );
	if( !(t instanceof RefLikeType) ) {
	    throw new RuntimeException( "Attempt to create VarNode of type "+t );
	}
        if( finishingNumbersSet ) {
	    throw new RuntimeException( "Attempt to create VarNode after toposort for "+value+" of type "+t+" in method "+m );
        }
	this.value = value;
        this.method = m;
    }
    /** Registers a frn as having this node as its base. */
    void addField( FieldRefNode frn, SparkField field ) {
	if( fields == null ) fields = new HashMap();
	fields.put( field, frn );
    }

    /* End of package methods. */

    protected Object value;
    protected Map fields;
    protected int finishingNumber = 0;
    static protected boolean finishingNumbersSet = false;
    protected SootMethod method;
    protected boolean interProcTarget = false;
    protected int numDerefs = 0;
}

