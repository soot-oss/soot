/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Feng Qian
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

package soot.jimple.toolkits.annotation.arraycheck;
import soot.options.*;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import soot.tagkit.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.io.*;
import java.util.*;


public class ClassFieldAnalysis 
{
    public ClassFieldAnalysis( Singletons.Global g ) {}
    public static ClassFieldAnalysis v() { return G.v().soot_jimple_toolkits_annotation_arraycheck_ClassFieldAnalysis(); }

    private boolean final_in = true;
    private boolean private_in = true;

    /* A map hold class object to other information
     * 
     * SootClass --> FieldInfoTable
     */
 
    private Map classToFieldInfoMap = new HashMap();	
  
    protected void internalTransform(SootClass c)
    {
 	if (classToFieldInfoMap.containsKey(c))
		return;
     
	/* Summerize class information here. */
	Date start = new Date();
 	if (Options.v().verbose()) 
	    G.v().out.println("[] ClassFieldAnalysis started on : "
			       +start+" for "
			       +c.getPackageName()+c.getName());
	
	Hashtable fieldInfoTable = new Hashtable();
	classToFieldInfoMap.put(c, fieldInfoTable);
	
	/* Who is the candidate for analysis?
	   Int, Array, field. Also it should be PRIVATE now.
	*/
	HashSet candidSet = new HashSet();

	int arrayTypeFieldNum = 0;

	Iterator fieldIt = c.getFields().iterator();
	while(fieldIt.hasNext())
	{
	    SootField field = (SootField)fieldIt.next();
	    int modifiers = field.getModifiers();

	    Type type = field.getType();
	    if (type instanceof ArrayType)
	    {
		if ( (final_in 
		      && ((modifiers & Modifier.FINAL) != 0)) 
		     || (private_in 
			 && ((modifiers & Modifier.PRIVATE) != 0))) 
		{
		    candidSet.add(field);
		    arrayTypeFieldNum ++;
		}
	    }
	}	

	if (arrayTypeFieldNum == 0)
	{
	    if (Options.v().verbose()) 
		G.v().out.println("[] ClassFieldAnalysis finished with nothing");
	    return;
	}

	/* For FINAL field, it only needs to scan the <clinit> and <init> methods. */

	/* For PRIVATE field, <clinit> is scanned to make sure that it is always
           assigned a value before other uses. And no other assignment in other methods.*/
	   

	/* The fastest way to determine the value of one field may get.
	   Scan all method to get all definitions, and summerize the final value.
	   For PRIVATE STATIC field, if it is not always assigned value, it may count null pointer
	   exception before array exception */

	Iterator methodIt = c.methodIterator();
	while (methodIt.hasNext())
	{
	    ScanMethod ((SootMethod)methodIt.next(),
			candidSet,
			fieldInfoTable);	    
	}

	Date finish = new Date();
	if (Options.v().verbose()) 
	{
	    long runtime=finish.getTime()-start.getTime();
	    long mins=runtime/60000;
	    long secs=(runtime%60000)/1000;
	    G.v().out.println("[] ClassFieldAnalysis finished normally. "
			       +"It took "+mins+" mins and "+secs+" secs.");
	}
    }

    public Object getFieldInfo(SootField field)
    {
	SootClass c = field.getDeclaringClass();

	Hashtable fieldInfoTable = (Hashtable)classToFieldInfoMap.get(c);

	if (fieldInfoTable == null)
	{
	    internalTransform(c);
	    fieldInfoTable = (Hashtable)classToFieldInfoMap.get(c);
	}
	
	return fieldInfoTable.get(field);
    }

    /* method, to be scanned
       candidates, the candidate set of fields, fields with value TOP are moved out of the set.
       fieldinfo, keep the field -> value.
    */

    public void ScanMethod (SootMethod method, 
				   Set candidates,
				   Hashtable fieldinfo)
    {
	if (!method.isConcrete())
	    return;

	Body body = method.retrieveActiveBody();

	if (body == null)
	    return;

	/* no array locals, then definitely it has no array type field references. */
	{
	    boolean hasArrayLocal = false;

	    Chain locals = body.getLocals();

	    Iterator localIt = locals.iterator();
	    while (localIt.hasNext())
	    {
		Local local = (Local)localIt.next();
		Type type = local.getType();

		if (type instanceof ArrayType)
		{
		    hasArrayLocal = true;
		    break;
		}
	    }

	    if (!hasArrayLocal)
	    {
		return;
	    }
	}

	/* only take care of the first dimension of array size */
	/* check the assignment of fields. */
	
	/* Linearly scan the method body, if it has field references in candidate set. */
	/* Only a.f = ... needs consideration.
	   this.f, or other.f are treated as same because we summerize the field as a class's field. 
	*/

	HashMap stmtfield = new HashMap();

	{
	    Iterator unitIt = body.getUnits().iterator();
	    while (unitIt.hasNext())
	    {
		Stmt stmt = (Stmt)unitIt.next();
		if (stmt.containsFieldRef())
		{
		    Value leftOp = ((AssignStmt)stmt).getLeftOp();
		    if (leftOp instanceof FieldRef)
		    {
			FieldRef fref = (FieldRef)leftOp;
			SootField field = fref.getField();

			if (candidates.contains(field))
			    stmtfield.put(stmt, field);
		    }
		}
	    }

	    if (stmtfield.size() == 0)
	    {
		return;
	    }
	}


	if (Options.v().verbose())
	{
	    G.v().out.println("[] ScanMethod for field started.");
	}

	/* build D/U web, find the value of each candidate */
	{
            UnitGraph g = new ExceptionalUnitGraph(body);
	    LocalDefs localDefs = new SmartLocalDefs(g, new SimpleLiveLocals(g));
	    
	    Set entries = stmtfield.entrySet();

	    Iterator entryIt = entries.iterator();
	    while (entryIt.hasNext())
	    {
		Map.Entry entry = (Map.Entry)entryIt.next();
		Stmt where = (Stmt)entry.getKey();
		SootField which = (SootField)entry.getValue();

		IntValueContainer length = new IntValueContainer();

		// take out the right side of assign stmt
		Value rightOp = ((AssignStmt)where).getRightOp();

		if (rightOp instanceof Local)
		{
		    // tracing down the defs of right side local.
		    Local local = (Local)rightOp;
		    DefinitionStmt usestmt = (DefinitionStmt)where;

		    while (length.isBottom())
		    {
			List defs = localDefs.getDefsOfAt(local, usestmt);
			if (defs.size() == 1)
			{
			    usestmt = (DefinitionStmt)defs.get(0);

			    if (Options.v().debug())
				G.v().out.println("        "+usestmt);

			    Value tmp_rhs = usestmt.getRightOp();
			    if ( (tmp_rhs instanceof NewArrayExpr)
				 ||(tmp_rhs instanceof NewMultiArrayExpr))
			    {
				Value size;

				if (tmp_rhs instanceof NewArrayExpr)
				    size = ((NewArrayExpr)tmp_rhs).getSize();
				else
				    size = ((NewMultiArrayExpr)tmp_rhs).getSize(0);
				
				if (size instanceof IntConstant)
				    length.setValue(((IntConstant)size).value);
				else
				if (size instanceof Local)
				{
				    local = (Local)size;
				    
				    //  defs = localDefs.getDefsOfAt((Local)size, (Unit)usestmt);

				    continue;
				}
				else
				    length.setTop();
			    }
			    else
			    if (tmp_rhs instanceof IntConstant)
			    {
				length.setValue(((IntConstant)tmp_rhs).value);
			    }
			    else
			    if (tmp_rhs instanceof Local)
			    {
				//  defs = localDefs.getDefsOfAt((Local)tmp_rhs, usestmt);
				local = (Local)tmp_rhs;

				continue;
			    }
			    else
				length.setTop();
			}
			else
			    length.setTop();
		    }
		}
		else
		    /* it could be null */
		    continue;

		IntValueContainer oldv = (IntValueContainer)fieldinfo.get(which);

		/* the length is top, set the field to top */
		if (length.isTop())
		{
		    if (oldv == null)
			fieldinfo.put(which, length.dup());
		    else
			oldv.setTop();

		    /* remove from the candidate set. */
		    candidates.remove(which);
		}		
		else
		if (length.isInteger())
		{
		    if (oldv == null)
		    {
			fieldinfo.put(which, length.dup());
		    }
		    else
		    {
			if (oldv.isInteger()
			  && oldv.getValue() != length.getValue())
			{
			    oldv.setTop();
			    candidates.remove(which);
			}
		    }
		}
	    }
	}	
	
	if (Options.v().verbose())
	{
	    G.v().out.println("[] ScanMethod finished.");
	}
    }
}




