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
import soot.jimple.toolkits.annotation.tags.*;
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

public class NullPointerChecker extends BodyTransformer 
{
    private static NullPointerChecker instance = 
    			new NullPointerChecker();

    private boolean isProfiling = false;
 
    private static boolean enableOther = true;
    
    private NullPointerChecker() {}

    public static NullPointerChecker v()
    {
    	return instance;
    }

    public String getDeclaredOptions()
    {
	return super.getDeclaredOptions()+" profiling onlyarrayref";
    }

    public void internalTransform(Body body, String phaseName, Map options)
    {
	isProfiling = Options.getBoolean(options, "profiling");
	enableOther = !Options.getBoolean(options, "onlyarrayref");

	{
	    Date start = new Date();

	    if (soot.Main.opts.verbose())
		System.out.println("[npc] Null pointer check for "+body.getMethod().getName()
				   +" started on "+start);
		
	    BranchedRefVarsAnalysis analysis = new BranchedRefVarsAnalysis(
	    					new CompleteUnitGraph(body));

	    SootClass counterClass = null;
	    SootMethod increase = null;
	    
	    if (isProfiling)
	    {
		counterClass = Scene.v().loadClassAndSupport("MultiCounter");
		increase = counterClass.getMethod("void increase(int)") ;
	    }

	    Chain units = body.getUnits();

	    Iterator stmtIt = units.snapshotIterator() ;

	    while (stmtIt.hasNext())
	    {
	        Stmt s = (Stmt)stmtIt.next() ;
		
		Value obj = null;

		if (s.containsArrayRef())
		{
		    ArrayRef aref = (ArrayRef)s.getArrayRef();
		    obj = aref.getBase();
		}
		else
	        {
		    if (enableOther)
		    {
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
			{
			    Iterator boxIt;
                            boxIt = s.getDefBoxes().iterator();
			    while (boxIt.hasNext())
			    {
				ValueBox vBox = (ValueBox)boxIt.next();
				Value v = vBox.getValue();

				// putfield, and getfield 
				if (v instanceof InstanceFieldRef)
				{
				    obj = ((InstanceFieldRef)v).getBase();
				    break;
				}
				else
				// invokevirtual, invokespecial, invokeinterface
				if (v instanceof InstanceInvokeExpr)
				{
				    obj = ((InstanceInvokeExpr)v).getBase();
				    break;
				}
				else
				// arraylength 
				if (v instanceof LengthExpr)
				{
				    obj = ((LengthExpr)v).getOp();
				    break;
				}
			    }
                            boxIt = s.getUseBoxes().iterator();
			    while (boxIt.hasNext())
			    {
				ValueBox vBox = (ValueBox)boxIt.next();
				Value v = vBox.getValue();

				// putfield, and getfield 
				if (v instanceof InstanceFieldRef)
				{
				    obj = ((InstanceFieldRef)v).getBase();
				    break;
				}
				else
				// invokevirtual, invokespecial, invokeinterface
				if (v instanceof InstanceInvokeExpr)
				{
				    obj = ((InstanceInvokeExpr)v).getBase();
				    break;
				}
				else
				// arraylength 
				if (v instanceof LengthExpr)
				{
				    obj = ((LengthExpr)v).getOp();
				    break;
				}
			    }
			}
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

	    Date finish = new Date();
	    if (soot.Main.opts.verbose())
	    {
		long runtime = finish.getTime()-start.getTime();
		long mins = runtime/60000;
		long secs = (runtime%60000)/1000;
		System.out.println("[npc] Null pointer checker finished. It took "
				   +mins+" mins and "+secs+" secs.");
	    }
	}    
    }
}
