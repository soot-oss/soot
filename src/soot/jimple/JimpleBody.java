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
import soot.toolkits.scalar.*;
import soot.util.*;
import java.util.*;
import java.io.*;

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

        // Check validity of traps.
	/* this check may not hold when jop.bcm is enabled. 
	 *           disabled by Feng Qian, May 2002
         */
	/*
        {
            Iterator it = getTraps().iterator();
            
            while(it.hasNext())
            {
                Trap t = (Trap) it.next();
                
                Stmt s = (Stmt) t.getHandlerUnit();
                                
                if(!(s instanceof IdentityStmt) 
		   || !(((IdentityStmt) s).getRightOp() instanceof CaughtExceptionRef)){
                    G.v().out.println(s);
                    throw new RuntimeException("Trap handler is not of the form x := caughtexceptionref");
                }
            }
        }
	*/
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



