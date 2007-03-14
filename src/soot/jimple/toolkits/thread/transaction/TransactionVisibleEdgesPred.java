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

package soot.jimple.toolkits.thread.transaction;

import java.util.*;
import soot.*;
import soot.jimple.toolkits.callgraph.*;

/** A predicate that accepts edges that are not part of the class library and do not have a source statement that falls inside a transaction.
 * @author Richard L. Halpert
 */
public class TransactionVisibleEdgesPred implements EdgePredicate
{
	Collection tns;
	Transaction exemptTn;
	
	public TransactionVisibleEdgesPred(Collection tns)
	{
		this.tns = tns;
	}
	
	public void setExemptTransaction(Transaction exemptTn)
	{
		this.exemptTn = exemptTn;
	}
	
    /** Returns true iff the edge e is wanted. */
    public boolean want( Edge e )
    {
		String tgtMethod = e.tgt().toString();
        String tgtClass = e.tgt().getDeclaringClass().toString();
        String srcMethod = e.src().toString();
        String srcClass = e.src().getDeclaringClass().toString();
        
        // Remove Deep Library Calls
	    if(tgtClass.startsWith("sun."))
	    	return false;
	    if(tgtClass.startsWith("com.sun."))
	    	return false;

    	// Remove static initializers
    	if(tgtMethod.endsWith("void <clinit>()>"))
    		return false;

    	// Remove calls to equals in the library
    	if((tgtClass.startsWith("java.") || tgtClass.startsWith("javax.")) && 
    		e.tgt().toString().endsWith("boolean equals(java.lang.Object)>"))
    		return false;

		// Remove anything called inside a class in java.util
		// the source call will be conservatively simulated as a read and a write to the receiving object
		if(srcClass.startsWith("java.util"))
			return false;
			
		// Remove anything called inside a class in java.lang
		// the source call will be conservatively (?) simulated as a read and a write to the receiving object
		if(srcClass.startsWith("java.lang"))
			return false;
    		
		// Remove Calls from within a transaction
		// one transaction is exempt - so that we may analyze calls within it
	    if(tns != null)
	    {
			Iterator tnIt = tns.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = (Transaction) tnIt.next();
				if(tn != exemptTn && tn.units.contains(e.srcStmt())) // if this method call originates inside a transaction...
				{
					return false; // ignore it
				}
			}
		}
		
		return true;
	}
}
