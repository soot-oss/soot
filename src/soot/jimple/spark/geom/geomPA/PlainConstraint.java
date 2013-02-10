/* Soot - a J*va Optimization Framework
 * Copyright (C) 2011 Richard Xiao
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
package soot.jimple.spark.geom.geomPA;

import java.util.Set;

import soot.jimple.spark.pag.SparkField;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.scalar.Pair;

/**
 * The geometric encoding based constraint descriptor.
 * 
 * @author xiao
 *
 */
public class PlainConstraint {
	// Plain constraint descriptor
	// This is a full description that we can read/write without context
	// A constraint has the form : lhs -> rhs, where lhs/rhs is a pointer p or a field p.f, which assigns the value of lhs to rhs
	
	/** The type of this constraint, e.g. allocation, assignment or complex */
	public int type;
	/** The two pointers involved in this constraint */
	public Pair<IVarAbstraction, IVarAbstraction> expr = new Pair<IVarAbstraction, IVarAbstraction>();
	/** Used in complex constraint. If this constraint is a store p.f = q, we say otherSide = q */
	public IVarAbstraction otherSide = null;
	/** Indicate the mapping relation between the two pointers, 1-1, 1-many, ... */
	public int code;
	/** The field that is involved in a complex constraint */
	public SparkField f = null;
	/** If this constraint represents a parameter passing or function return, the corresponding call edge is identified here */
	public Set<Edge> interCallEdges = null;
	/** To indicate if this constraint is useful or not (may be deleted by cycle detection) */
	public boolean isViable = true;
}
