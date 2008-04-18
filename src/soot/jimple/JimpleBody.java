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
     * Validates that the body starts with an @this-assignment (if the method is non-static)
     * and that then parameter identity statements follow in the correct order.
     */
    public void validateIdentityStatements() {
		if (method.isAbstract())
			return;
		
		Body body=method.getActiveBody();
		Chain<Unit> units=body.getUnits().getNonPatchingChain();
		List params=method.getParameterTypes();
		
		Iterator<Unit> itUnits=units.iterator();
		if (!method.isStatic()) {
			Stmt first=(Stmt)itUnits.next();		
			
			IdentityStmt id=(IdentityStmt) first;
			Local local=(Local)id.getLeftOp();
			ThisRef ref=(ThisRef)id.getRightOp();
			if (!ref.getType().equals(method.getDeclaringClass().getType()))
				throw new RuntimeException("this-ref has wrong type!"+id);
			
			if (!local.getType().equals(method.getDeclaringClass().getType()))
				throw new RuntimeException("this-local has wrong type!"+id);
			
		}	
		
		Iterator it=params.iterator();
		int i=0;
		while (it.hasNext()) {
			Type type=(Type)it.next();
			Stmt stmt=(Stmt)itUnits.next();
			IdentityStmt id=(IdentityStmt)stmt;
			Local local=(Local)id.getLeftOp();
			ParameterRef ref=(ParameterRef)id.getRightOp();
			if (!Type.toMachineType(local.getType()).equals(Type.toMachineType(type))) {
				throw new RuntimeException("Parameter reference "+ref.getIndex()+" has wrong type: "+id);
			}
			if (ref.getIndex()!=i++) {
				throw new RuntimeException("Parameter reference in wrong order");
			}
			if (!ref.getType().equals(type)) {
				throw new RuntimeException("Parameter reference "+ref.getIndex()+" has wrong type: "+id);
			}				
		}
		
		//validate that only CaughtExceptionRef occurs in the rest of the body
		while(itUnits.hasNext()) {
			Unit u = itUnits.next();
			if(u instanceof IdentityStmt) {
				IdentityStmt identityStmt = (IdentityStmt) u;
				if(!(identityStmt.getRightOp() instanceof CaughtExceptionRef))
					throw new RuntimeException("Identity statement in middle of method: "+u);
			}
		}
    }
    
    /** Inserts usual statements for handling this & parameters into body. */
    public void insertIdentityStmts()
    {
        int i = 0;

        if (!getMethod().isStatic())
         {
             Local l = Jimple.v().newLocal("this", 
                                           RefType.v(getMethod().getDeclaringClass()));
             getLocals().add(l);
             getUnits().addFirst(Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef((RefType)l.getType())));
         }

        Iterator parIt = getMethod().getParameterTypes().iterator();
        while (parIt.hasNext())
        {
            Type t = (Type)parIt.next();
            Local l = Jimple.v().newLocal("parameter"+i, t);
            getLocals().add(l);
            getUnits().addFirst(Jimple.v().newIdentityStmt(l, Jimple.v().newParameterRef(l.getType(), i)));
            i++;
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



