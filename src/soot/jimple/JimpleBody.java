/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple;

import soot.*;
import soot.util.Chain;

import java.util.*;

/** Implementation of the Body class for the Jimple IR. */
public class JimpleBody extends StmtBody
{
    /**
        Construct an empty JimpleBody 
     **/
    
    JimpleBody(SootMethod m)
    {
        super(m);
    }

    /**
       Construct an extremely empty JimpleBody, for parsing into.
    **/

    JimpleBody() 
    {
    }

    /** Clones the current body, making deep copies of the contents. */
    public Object clone()
    {
        Body b = new JimpleBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    /** Make sure that the JimpleBody is well formed.  If not, throw
     *  an exception.  Right now, performs only a handful of checks.  
     */
    public void validate()
    {
        super.validate();
        validateIdentityStatements();
    }
    
    /**
     * Checks the following invariants on this Jimple body:
     * <ol>
     * <li> this-references may only occur in instance methods
     * <li> this-references may only occur as the first statement in a method, if they occur at all
     * <li> param-references must precede all statements that are not themselves param-references or this-references,
     *      if they occur at all
     * </ol>
     */
    public void validateIdentityStatements() {
		if (method.isAbstract())
			return;
		
		Body body=method.getActiveBody();
		Chain<Unit> units=body.getUnits().getNonPatchingChain();

		boolean foundNonThisOrParamIdentityStatement = false;
		boolean firstStatement = true;
		
		for (Unit unit : units) {
			if(unit instanceof IdentityStmt) {
				IdentityStmt identityStmt = (IdentityStmt) unit;
				if(identityStmt.getRightOp() instanceof ThisRef) {					
					if(method.isStatic()) {
						throw new RuntimeException("@this-assignment in a static method!");
					}					
					if(!firstStatement) {
						throw new RuntimeException("@this-assignment statement should precede all other statements");
					}
				} else if(identityStmt.getRightOp() instanceof ParameterRef) {
					if(foundNonThisOrParamIdentityStatement) {
						throw new RuntimeException("@param-assignment statements should precede all non-identity statements");
					}
				} else {
					//@caughtexception statement					
					foundNonThisOrParamIdentityStatement = true;
				}
			} else {
				//non-identity statement
				foundNonThisOrParamIdentityStatement = true;
			}
			firstStatement = false;
		}
    }
    
    /** Inserts usual statements for handling this & parameters into body. */
    public void insertIdentityStmts()
    {
        int i = 0;

        Iterator parIt = getMethod().getParameterTypes().iterator();
        while (parIt.hasNext())
        {
            Type t = (Type)parIt.next();
            Local l = Jimple.v().newLocal("parameter"+i, t);
            getLocals().add(l);
            getUnits().addFirst(Jimple.v().newIdentityStmt(l, Jimple.v().newParameterRef(l.getType(), i)));
            i++;
        }
        
        //add this-ref before everything else
        if (!getMethod().isStatic())
        {
        	Local l = Jimple.v().newLocal("this", 
        			RefType.v(getMethod().getDeclaringClass()));
        	getLocals().add(l);
        	getUnits().addFirst(Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef((RefType)l.getType())));
        }
    }

    /** Returns the first non-identity stmt in this body. */
    public Stmt getFirstNonIdentityStmt()
    {
        Iterator it = getUnits().iterator();
        Object o = null;
        while (it.hasNext())
            if (!((o = it.next()) instanceof IdentityStmt))
                break;
        if (o == null)
            throw new RuntimeException("no non-id statements!");
        return (Stmt)o;
    }
}



