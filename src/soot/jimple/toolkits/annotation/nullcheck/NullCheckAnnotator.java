/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


/* Added by Feng, to annotate the object references in the bytecode. 
 */

package soot.jimple.toolkits.annotation.nullcheck;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.io.*;
import java.util.*;
import soot.tagkit.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

 /*
	ArrayRef
	GetField
	PutField
	InvokeVirtual
	InvokeSpecial
	InvokeInterface
	ArrayLength
-	AThrow
-	MonitorEnter
-	MonitorExit	
 */

public class NullCheckAnnotator extends BodyTransformer 
{
    private static NullCheckAnnotator instance = 
    			new NullCheckAnnotator();

    private boolean isProfiling = false;
 
    private static boolean enable = true;
    
    private NullCheckAnnotator() {}

    public static NullCheckAnnotator v()
    {
    	return instance;
    }

    public String getDeclaredOptions()
    {
        return enable?"enabled":"disabled";
    }

    public void setOptions(boolean op)
    {
        enable = op;
    }

    public void options(boolean isprof)
    {
        isProfiling = isprof;
    }

    public void internalTransform(Body body, String phaseName, Map options)
    {
	{
	    BranchedRefVarsAnalysis analysis = new BranchedRefVarsAnalysis(
	    					new CompleteUnitGraph(body));


        SootClass counterClass = Scene.v().loadClassAndSupport("soot.counter");
        SootMethod increase = counterClass.getMethod("void increase(int)") ;


	    Chain units = body.getUnits();

	    Iterator stmtIt = units.snapshotIterator() ;

	    while (stmtIt.hasNext())
	    {
	        Stmt s = (Stmt)stmtIt.next() ;

		Iterator boxIt = s.getUseAndDefBoxes().iterator();

		while (boxIt.hasNext())
		{
		    ValueBox vBox = (ValueBox)boxIt.next();
		    Value v = vBox.getValue();

		    Value obj = null;
 
 /*
		    // Throw
		    if (s instanceof ThrowStmt)
		    {
		    	obj = ((ThrowStmt)s).getOp();
		    }
		    else
		    // Monitor enter and exit 
		    if (s instanceof MonitorStmt)
		    { 
		        obj = ((MonitorStmt)s).getOp();
		    }
		    else
*/
		    // For array references
		    if (v instanceof ArrayRef)
		    {
		    	obj = ((ArrayRef)v).getBase();
		    }

	if (enable)
	{
			// putfield, and getfield 
		    if (v instanceof InstanceFieldRef)
		    {
		    	obj = ((InstanceFieldRef)v).getBase();
		    }
		    else
			// invokevirtual, invokespecial, invokeinterface
		    if (v instanceof InstanceInvokeExpr)
		    {
		    	obj = ((InstanceInvokeExpr)v).getBase();
		    }
		    else
			// arraylength 
		    if (v instanceof LengthExpr)
		    {
		    	obj = ((LengthExpr)v).getOp();
		    }
	}
	
		    // annotate it or now 
		    if (obj != null)
		    {
		    	FlowSet beforeSet = (FlowSet)analysis.getFlowBefore(s);
			
			int vInfo = BranchedRefVarsAnalysis.anyRefInfo(obj, beforeSet);
			
			boolean needCheck = 
				(vInfo != BranchedRefVarsAnalysis.kNonNull);

			if (isProfiling)
			{
			  int whichCounter = 5;
			  if (!needCheck)
			    whichCounter = 6;   
  
                           units.insertBefore(Jimple.v().newInvokeStmt(
					      Jimple.v().newStaticInvokeExpr(increase,
					      IntConstant.v(whichCounter))), s);

			}
			
			{
			  Tag nullTag = new NullCheckTag(needCheck);

			  s.addTag(nullTag);
			}
		    }
		}
	    }
	}    
    }
}
