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
import soot.Type;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.spark.sets.EmptyPointsToSet;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;

/** Represents every node in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class Node implements ReferenceVariable {
    public int hashCode() { return id; }
    public boolean equals( Object other ) { return id == ( (Node) other ).id; }
    /** Returns an integer unique to this node. */
    public int getId() { return id; }
    /** Returns the declared type of this node, null for unknown. */
    public Type getType() { return type; }
    /** Sets the declared type of this node, null for unknown. */
    public void setType( Type type ) { this.type = type; }
    /** If this node has been merged with another, returns the new node to be
     * used as the representative of this node; returns this if the node has
     * not been merged. */
    public Node getReplacement() { 
	if( replacement != replacement.replacement ) {
	    replacement = replacement.getReplacement();
	}
	return replacement;
    }
    /** Merge with the node other. */
    public void mergeWith( Node other ) {
	other.replacement = getReplacement();
	if( other.p2set != p2set 
	&& other.p2set != null 
	&& !other.p2set.isEmpty() ) {
	    if( p2set == null || p2set.isEmpty() ) {
		p2set = other.p2set;
	    } else {
		p2set.mergeWith( other.p2set );
	    }
	}
	other.p2set = null;
        pag.mergedWith( this, other );
    }
    /** Returns the points-to set for this node. */
    public PointsToSetInternal getP2Set() {
	if( p2set != null ) {
            if( replacement != this ) throw new RuntimeException(
                    "Node "+this+" has replacement "+replacement+" but has p2set" );
            return p2set;
        }
        Node rep = getReplacement();
        if( rep == this ) {
            return EmptyPointsToSet.v();
        }
        return rep.getP2Set();
    }
    /** Returns the points-to set for this node, makes it if necessary. */
    public PointsToSetInternal makeP2Set() {
	if( p2set != null ) {
            if( replacement != this ) throw new RuntimeException(
                    "Node "+this+" has replacement "+replacement+" but has p2set" );
            return p2set;
        }
        Node rep = getReplacement();
        if( rep == this ) {
            p2set = pag.getSetFactory().newSet( type, pag );
        }
        return rep.getP2Set();
    }
    /** Returns the pointer assignment graph that this node is a part of. */
    public PAG getPag() { return pag; }

    /* End of public methods. Nothing to see here; move along. */

    /** Creates a new node of pointer assignment graph pag, with type type. */
    Node( PAG pag, Type type ) {
	this.type = type;
	this.pag = pag;
	assignId();
        replacement = this;
    }

    /* End of package methods. Nothing to see here; move along. */

    protected void assignId() {
	id = pag.getNextNodeId();
    }

    protected int id;
    protected Type type;
    protected Node replacement;
    protected PAG pag;
    protected PointsToSetInternal p2set;
}
