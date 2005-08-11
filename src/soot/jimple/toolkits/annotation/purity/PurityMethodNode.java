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
import soot.*;
import java.util.*;

/**
 * Kind of Stmt inside node, but global to the method.
 * Used for synthetic summary of unalysed methods returning a fresh object.
 */
public class PurityMethodNode implements PurityNode
{
    /** Method that created the node */
    private SootMethod id;

    /** gives a unique id, for pretty-printing purposes */
    private static Map nMap = new HashMap();
    private static int n = 0;

    PurityMethodNode(SootMethod id)
    { 
	this.id = id;
	if (!nMap.containsKey(id)) { nMap.put(id,new Integer(n)); n++; }
    }

    public String toString() 
    { 
	return "M_"+nMap.get(id);
	//return ""+id;
    }

    public int hashCode() 
    { return id.hashCode(); }
    
    public boolean equals(Object o)
    {
	if (o instanceof PurityMethodNode) {
	    PurityMethodNode oo = (PurityMethodNode)o;
	    return id.equals(oo.id);
	}
	else return false;
    }

    public boolean isInside() 
    { return true; }

    public boolean isLoad()
    { return false; }

    public boolean isParam() 
    { return false; }
}
