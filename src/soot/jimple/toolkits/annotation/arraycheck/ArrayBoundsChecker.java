/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Feng Qian
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

package soot.jimple.toolkits.annotation.arraycheck;

import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.*;

import soot.tagkit.*;
import soot.jimple.toolkits.annotation.tags.*;

import java.util.*;

public class ArrayBoundsChecker extends BodyTransformer
{
    private static ArrayBoundsChecker instance =
	new ArrayBoundsChecker();

    private ArrayBoundsChecker() {};

    public static ArrayBoundsChecker v()
    {
	return instance;
    }

    protected boolean takeClassField = false;
    protected boolean takeFieldRef = false;
    protected boolean takeArrayRef = false;
    protected boolean takeCSE = false;
    protected boolean takeRectArray = false;

    private boolean isProfiling = false;

    static boolean debug = soot.Main.isInDebugMode;

    public String getDeclaredOptions()
    {
	return super.getDeclaredOptions()+" with-all with-fieldref with-arrayref"
	    +" with-cse with-classfield with-rectarray profiling";
    }

    public void internalTransform(Body body, String phaseName, Map options)
    {
	if (Options.getBoolean(options, "with-all"))
	{
	    takeClassField = true;
	    takeFieldRef = true;
	    takeArrayRef = true;
	    takeCSE = true;
	    takeRectArray = true;
	}
	else
	{
	    takeClassField = Options.getBoolean(options, "with-classfield");
	    takeFieldRef = Options.getBoolean(options, "with-fieldref");
	    takeArrayRef = Options.getBoolean(options, "with-arrayref");
	    takeCSE = Options.getBoolean(options, "with-cse");
	    takeRectArray = Options.getBoolean(options, "with-rectarray");
	}

	isProfiling = Options.getBoolean(options, "profiling");

	{
	    SootMethod m = body.getMethod();

	    Date start = new Date();

	    if (soot.Main.isVerbose)
	    {
		System.out.println("[abc] Analyzing array bounds information for "+m.getName());
		System.out.println("[abc] Started on "+start);
	    }

	    ArrayBoundsCheckerAnalysis analysis = null;

	    if (hasArrayLocals(body))
	    {
		 analysis = 
		    new ArrayBoundsCheckerAnalysis(body, 
						   takeClassField, 
						   takeFieldRef, 
						   takeArrayRef, 
						   takeCSE, 
						   takeRectArray);
	    }

	    SootClass counterClass = null;
	    SootMethod increase = null;
	    
	    if (isProfiling)
	    {
		counterClass = Scene.v().loadClassAndSupport("MultiCounter");
		increase = counterClass.getMethod("void increase(int)") ;
	    }
	    
	    Chain units = body.getUnits();

	    IntContainer zero = new IntContainer(0);
	    
	    Iterator unitIt = units.snapshotIterator();

	    while (unitIt.hasNext())
	    {
		Stmt stmt = (Stmt)unitIt.next();
	   
		if (stmt.containsArrayRef())
		{
		    ArrayRef aref = (ArrayRef)stmt.getArrayRef();

		    {
			WeightedDirectedSparseGraph vgraph = 
			    (WeightedDirectedSparseGraph)analysis.getFlowBefore(stmt);

			boolean lowercheck = true;
			boolean uppercheck = true;
	    
			{
			    if (debug)
			    {
				if (!vgraph.makeShortestPathGraph())
				{
				    System.out.println(stmt+" :");
				    System.out.println(vgraph);
				}
			    }

			    Value base = aref.getBase();
			    Value index = aref.getIndex();

			    if (index instanceof IntConstant)
			    {
				int indexv = ((IntConstant)index).value;
				
				if (vgraph.hasEdge(base, zero))
				{
				    int alength = vgraph.edgeWeight(base, zero);
			
				    if (-alength > indexv)
					uppercheck = false;
				}
			    
				if (indexv >= 0)
				    lowercheck = false;			
			    }
			    else
			    {
				if (vgraph.hasEdge(base, index))
				{
				    int upperdistance = vgraph.edgeWeight(base, index);
				    if (upperdistance < 0)
					uppercheck = false;
				}
				
				if (vgraph.hasEdge(index, zero))
				{
				    int lowerdistance = vgraph.edgeWeight(index, zero);

				    if (lowerdistance <= 0)
					lowercheck = false;
				}
			    }
			}

			if (isProfiling)
			{
			    int lowercounter = 0;
			    if (!lowercheck)
				lowercounter = 1;

			    units.insertBefore (Jimple.v().newInvokeStmt( 
					Jimple.v().newStaticInvokeExpr(increase, 
					IntConstant.v(lowercounter))), stmt) ;

			    int uppercounter = 2;
			    if (!uppercheck)
				uppercounter = 3;
		    
			    units.insertBefore (Jimple.v().newInvokeStmt( 
					Jimple.v().newStaticInvokeExpr(increase, 
					IntConstant.v(uppercounter))), stmt) ;

			    /*
			    if (!lowercheck && !uppercheck)
			    {
				units.insertBefore(Jimple.v().newInvokeStmt(
				   Jimple.v().newStaticInvokeExpr(increase,
			           IntConstant.v(4))), stmt);

				NullCheckTag nullTag = (NullCheckTag)stmt.getTag("NullCheckTag");
			    
				if (nullTag != null && !nullTag.needCheck())
				    units.insertBefore(Jimple.v().newInvokeStmt(
				    Jimple.v().newStaticInvokeExpr(increase,
			     	    IntConstant.v(7))), stmt);
			    }
			    */

			}
			else
			{
			    Tag checkTag = new ArrayCheckTag(lowercheck, uppercheck);
			    stmt.addTag(checkTag);
			}
		    }
		}
	    }

	    Date finish = new Date();
	    if (soot.Main.isVerbose) 
	    {
		long runtime = finish.getTime() - start.getTime();
		System.out.println("[abc] ended on "+finish
				   +". It took "+(runtime/60000)+" min. "
				   +((runtime%60000)/1000)+" sec.");
	    }
	}
    }

    private boolean hasArrayLocals(Body body)
    {
	Iterator localIt = body.getLocals().iterator();

	while (localIt.hasNext())
	{
	    Local local = (Local)localIt.next();
	    if (local.getType() instanceof ArrayType)
		return true;
	}

	return false;
    }
}
