/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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

import soot.tagkit.*;
import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

public class DefaultLocalPrinter implements LocalPrinter
{
    public DefaultLocalPrinter( Singletons.Global g ) {}
    public static DefaultLocalPrinter v() { return G.v().DefaultLocalPrinter(); }

    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    public void printLocalsInBody(Body body, java.io.PrintWriter out, boolean isPrecise)
    {
        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(body.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = body.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    List localList;
 
                    String typeName;
                    Type t = local.getType();

                    typeName = (isPrecise) ?  t.toString() :  t.toBriefString();

                    if(typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while(typeIt.hasNext())
                {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();
                    out.print("        "  + type + " ");
                    
                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
		    if (body.getMethod().getDeclaringClass().isAddJimpleLn()) {
		    	body.getMethod().getDeclaringClass().incJimpleLnNum();
		    }
                }
            }


            if(!typeToLocals.isEmpty()){
                out.println();
		if (body.getMethod().getDeclaringClass().isAddJimpleLn()) {
	        	body.getMethod().getDeclaringClass().incJimpleLnNum();
		}				    
	    }
        }
    }
}
