/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Antoine Mine
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

/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;

/** 
 * An edge in a purity graph.
 * Each edge has a soruce PurityNode, a taget PurityNode, and a field label
 * (we use a String here).
 * To represent an array element, the convention is to use the [] field label.
 * Edges are mmuable and hashable. They compare equal only if they link
 * equal nodes and have equal labels.
 *
 */
public class PurityEdge
{
    private String     field; 
    private PurityNode source, target;
    private boolean    inside;

    PurityEdge(PurityNode source, String field, PurityNode target, boolean inside)
    {
	this.source = source;
	this.field  = field;
	this.target = target;
	this.inside = inside;
    }

    public String     getField()  { return field; }
    public PurityNode getTarget() { return target; }
    public PurityNode getSource() { return source; }
    public boolean    isInside()  { return inside; }

    public int hashCode() 
    { return field.hashCode()+target.hashCode()+source.hashCode()+(inside?69:0); }

    public boolean equals(Object o)
    {
	if (!(o instanceof PurityEdge)) return false;
	PurityEdge e = (PurityEdge)o;
	return source.equals(e.source) && field.equals(e.field) 
	    && target.equals(e.target) && inside==e.inside;
    }

    public String toString()
    {
	if (inside)
	    return source.toString()+" = "+field+" => "+target.toString(); 
	else
	    return source.toString()+" - "+field+" -> "+target.toString(); 
	
    }
}

