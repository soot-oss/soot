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

/** Represents an allocation site node (Blue) in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class AllocNode extends Node {
    /** Returns the new expression of this allocation site. */
    public Object getNewExpr() { return newExpr; }
    /** Returns all field ref nodes having this node as their base. */
    public Collection getAllFieldRefs() { 
        if( fields == null ) return Collections.EMPTY_LIST;
        return fields.values();
    }
    /** Returns the field ref node having this node as its base,
     * and field as its field; null if nonexistent. */
    public AllocDotField dot( SparkField field ) 
    { return fields == null ? null : (AllocDotField) fields.get( field ); }
    public String toString() {
	return "AllocNode "+id+" "+newExpr;
    }

    /* End of public methods. Nothing to see here; move along. */

    AllocNode( PAG pag, Object newExpr, Type t ) {
	super( pag, t );
	this.newExpr = newExpr;
    }
    /** Registers a AllocDotField as having this node as its base. */
    void addField( AllocDotField adf, SparkField field ) {
	if( fields == null ) fields = new HashMap();
        fields.put( field, adf );
    }

    /* End of package methods. Nothing to see here; move along. */

    protected void assignId() {
	id = pag.getNextAllocNodeId();
    }

    protected Object newExpr;
    protected Map fields;
}

