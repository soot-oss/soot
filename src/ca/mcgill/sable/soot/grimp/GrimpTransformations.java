/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Grimp, an aggregated-expression Java(TM) bytecode representation. *
 * Copyright (C) 1998 Patrick Lam (plam@sable.mcgill.ca)             *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Raja Vallee-Rai (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Raja Vallee-Rai.  All rights reserved.             *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on March 1, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Renamed method to foldConstructors.  
   Renamed ConstructExpr to NewInvokeExpr.
   
 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca). (*)
   First release of Grimp.
*/

package ca.mcgill.sable.soot.grimp;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;

public class GrimpTransformations
{

  /* Change all new Obj/<init>(args) pairs to new Obj(args) construction. */
  public static void foldConstructors(GrimpBody body)
    {
      StmtList stmtList = body.getStmtList();
      
      if(Main.isVerbose)
	System.out.println("[" + body.getMethod().getName() + "] Constructor-folding");
      
      if(Main.isVerbose)
	System.out.println("[" + body.getMethod().getName() + "] Constructing StmtGraph...");
      
      if(Main.isProfilingOptimization)
	Main.graphTimer.start();
	
      CompleteStmtGraph graph = new CompleteStmtGraph(stmtList);
	
      if(Main.isProfilingOptimization)
	Main.graphTimer.end();
	
      if(Main.isVerbose)
	System.out.println("[" + body.getMethod().getName() + "] Constructing LocalDefs...");
	
      if(Main.isProfilingOptimization)
	Main.defsTimer.start();
	
      LocalDefs localDefs = new SimpleLocalDefs(graph);
	
      if(Main.isProfilingOptimization)
	Main.defsTimer.end();
	
      if(Main.isVerbose)
	System.out.println("[" + body.getMethod().getName() + "] Constructing LocalUses...");
	
      if(Main.isProfilingOptimization)
	Main.usesTimer.start();
	
      LocalUses localUses = new SimpleLocalUses(graph, localDefs);

      if(Main.isProfilingOptimization)
	Main.usesTimer.end();
	
      Iterator stmtIt = stmtList.iterator();
	
      /* fold in NewExpr's with specialinvoke's */
      while (stmtIt.hasNext())
	{
	  Stmt s = (Stmt)(stmtIt.next());
	    
	  if (!(s instanceof AssignStmt))
	    continue;
	    
	  /* this should be generalized to ArrayRefs */
	  Value lhs = ((AssignStmt)s).getLeftOp();
	  if (!(lhs instanceof Local))
	    continue;
	    
	  Value rhs = ((AssignStmt)s).getRightOp();
	  if (!(rhs instanceof NewExpr))
	    continue;

	  /* TO BE IMPLEMENTED LATER: move any copy of the object reference
	     for lhs down beyond the NewInvokeExpr, with the rationale
	     being that you can't modify the object before the constructor
	     call in any case.

	     Also, do note that any new's (object creation) without
	     corresponding constructors must be dead. */
	    
	  List lu = localUses.getUsesOf((DefinitionStmt)s);
	  Iterator luIter = lu.iterator();
	  boolean MadeNewInvokeExpr = false;
	  
	  while (luIter.hasNext())
	    {
	      Stmt use = ((StmtValueBoxPair)(luIter.next())).stmt;
	      if (!(use instanceof InvokeStmt))
		break;
	      InvokeStmt is = (InvokeStmt)use;
	      if (!(is.getInvokeExpr() instanceof SpecialInvokeExpr) ||
		  lhs != ((SpecialInvokeExpr)is.getInvokeExpr()).getBase())
		break;
	      
	      SpecialInvokeExpr oldInvoke = 
		((SpecialInvokeExpr)is.getInvokeExpr());
	      LinkedList invokeArgs = new LinkedList();
	      for (int i = 0; i < oldInvoke.getArgCount(); i++)
		invokeArgs.add(oldInvoke.getArg(i));
	      
	      AssignStmt constructStmt = Grimp.v().newAssignStmt
		((AssignStmt)s);
	      constructStmt.setRightOp
		(Grimp.v().newNewInvokeExpr
		 (((NewExpr)rhs).getBaseType(), oldInvoke.getMethod(), invokeArgs));
	      MadeNewInvokeExpr = true;
	      
	      body.redirectJumps(use, constructStmt);
	      body.eliminateBackPointersTo(use);
	      stmtList.add(stmtList.indexOf(use), constructStmt);
	      stmtList.remove(use);
	    }
	  if (MadeNewInvokeExpr)
	    {
	      body.eliminateBackPointersTo(s);
	      stmtIt.remove();
	    }
	}
    }  
}
