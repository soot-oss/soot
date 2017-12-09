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
 * A node representing a method parameter.
 * Each method parameter has a number, starting from 0.
 * 
 */
public class PurityParamNode implements PurityNode
{
    private int id;

    PurityParamNode(int id) { this.id = id; }

    public String toString() { return "P_"+id; }

    public int hashCode() { return id; }
    
    public boolean equals(Object o)
    {
	if (o instanceof PurityParamNode) return ((PurityParamNode)o).id==id;
	else return false;
    }

    public boolean isInside() 
    { return false; }

    public boolean isLoad()
    { return false; }

    public boolean isParam() 
    { return true; }
}

