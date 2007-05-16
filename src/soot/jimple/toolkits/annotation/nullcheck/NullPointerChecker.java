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


/* Added by Feng, to annotate the object references in the bytecode. 
 */

package soot.jimple.toolkits.annotation.nullcheck;
import soot.options.*;

import soot.*;
import soot.jimple.*;
import soot.util.*;
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
    public NullPointerChecker( Singletons.Global g ) {}
    public static NullPointerChecker v() { return G.v().soot_jimple_toolkits_annotation_nullcheck_NullPointerChecker(); }

    private boolean isProfiling = false;
 
    private boolean enableOther = true;
    
    protected void internalTransform(Body body, String phaseName, Map options)
    {
	isProfiling = PhaseOptions.getBoolean(options, "profiling");
	enableOther = !PhaseOptions.getBoolean(options, "onlyarrayref");

	{
	    Date start = new Date();

	    if (Options.v().verbose())
		G.v().out.println("[npc] Null pointer check for "+body.getMethod().getName()
				   +" started on "+start);
		
	    BranchedRefVarsAnalysis analysis = new BranchedRefVarsAnalysis(
	    					new ExceptionalUnitGraph(body));

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
		    ArrayRef aref = s.getArrayRef();
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
			
		    int vInfo = analysis.anyRefInfo(obj, beforeSet);
			
		    boolean needCheck = 
			(vInfo != BranchedRefVarsAnalysis.kNonNull);

		    if (isProfiling)
		    {
			int whichCounter = 5;
			if (!needCheck)
			    whichCounter = 6;   
  
			units.insertBefore(Jimple.v().newInvokeStmt(
					      Jimple.v().newStaticInvokeExpr(increase.makeRef(),
					      IntConstant.v(whichCounter))), s);
		    }
			
		    {
			Tag nullTag = new NullCheckTag(needCheck);
			s.addTag(nullTag);
		    }
		}	       
	    }

	    Date finish = new Date();
	    if (Options.v().verbose())
	    {
		long runtime = finish.getTime()-start.getTime();
		long mins = runtime/60000;
		long secs = (runtime%60000)/1000;
		G.v().out.println("[npc] Null pointer checker finished. It took "
				   +mins+" mins and "+secs+" secs.");
	    }
	}    
    }
}
