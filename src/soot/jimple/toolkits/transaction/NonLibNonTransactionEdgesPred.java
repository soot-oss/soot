/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.toolkits.transaction;

import java.util.*;
import soot.*;
import soot.jimple.toolkits.callgraph.*;

/** A predicate that accepts edges that are not part of the class library and do not have a source statement that falls inside a transaction.
 * @author Richard L. Halpert
 */
public class NonLibNonTransactionEdgesPred implements EdgePredicate
{
	Collection tns;
	SootMethod methodToExclude;
	
	public NonLibNonTransactionEdgesPred(Collection tns)
	{
		this.tns = tns;
	}
	
	public void setExcludedMethod(SootMethod methodToExclude)
	{
		this.methodToExclude = methodToExclude;
	}
	
    /** Returns true iff the edge e is wanted. */
    public boolean want( Edge e )
    {
        String tgtClass = e.tgt().getDeclaringClass().toString();
        if(tgtClass.startsWith("java."))
        	return false;
        if(tgtClass.startsWith("javax."))
	        return false;
	    if(tgtClass.startsWith("sun."))
	    	return false;
	    if(tgtClass.startsWith("com.sun."))
	    	return false;
	    if(tns != null)
	    {
			Iterator tnIt = tns.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = (Transaction) tnIt.next();
				if(tn.method != methodToExclude && tn.units.contains(e.srcStmt()))
				{
					return false;
				}
			}
		}
		return true;
	}
}
