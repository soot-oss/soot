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
import soot.*;
import soot.jimple.toolkits.callgraph.*;

/** A predicate that accepts edges that are the result of an explicit invoke.
 * @author Ondrej Lhotak
 */
public class NonClinitEdgesPred implements EdgePredicate
{ 
    /** Returns true iff the edge e is wanted. */
    public boolean want( Edge e ) {
		String tgtMethod = e.tgt().toString();
		String tgtClass = e.tgt().getDeclaringClass().toString();
    
    	// RULE BASED ELIMINATIONS
    	// ***********************    	
    	// get rid of calls to packeges in sun.anything and com.sun.anything
    	if(tgtClass.startsWith("sun.") || tgtClass.startsWith("com.sun."))
    		return false;
    		
    	// get rid of static initializers
    	if(e.tgt().toString().endsWith("void <clinit>()>"))
    		return false;
    		
    	// LIST BASED ELIMINATIONS
    	// ***********************
    	// get rid of calls to equals in the library
    	if((tgtClass.startsWith("java.") || tgtClass.startsWith("javax.")) && 
    		e.tgt().toString().endsWith("boolean equals(java.lang.Object)>"))
    		return false;
    		
    	// get rid of anything relating to exception handling in the library *** DIDN'T DO MUCH ***
    	if(tgtClass.matches("java.*Exception.*"))
    		return false;

		if(tgtClass.startsWith("java.io.Reader") || tgtClass.startsWith("java.io.Writer"))
			return false;
    		
    	// get rid of calls to synchronized methods in the library DEFINITELY UNSOUND (breaks consistency guarantees in some cases)
    	// conservatively, these could be treated like writes to the object of synch
    	// note that errors in the library's synch could cause breakage here
//      if((tgtClass.startsWith("java.") || tgtClass.startsWith("javax.")) &&
//        	e.tgt().isSynchronized())
//    		return false;
    		
    	// everything else is ok
    	return true;
    }
}


