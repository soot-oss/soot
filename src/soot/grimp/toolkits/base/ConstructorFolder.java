/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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


 



package soot.grimp.toolkits.base;

import soot.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.grimp.*;
import soot.util.*;
import java.util.*;

public class ConstructorFolder extends BodyTransformer
{
    private static ConstructorFolder instance = new ConstructorFolder();
    private ConstructorFolder() {}

    public static ConstructorFolder v() { return instance; }

    /** This method change all new Obj/<init>(args) pairs to new Obj(args) idioms. */
    protected void internalTransform(Body b, String phaseName, Map options)
    {
        GrimpBody body = (GrimpBody)b;

        if(soot.Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() +
                "] Folding constructors...");

      Chain units = body.getUnits();
      List stmtList = new ArrayList();
      stmtList.addAll(units);

      Iterator it = stmtList.iterator();

      CompleteUnitGraph graph = new CompleteUnitGraph(body);
        
      LocalDefs localDefs = new SimpleLocalDefs(graph);
      LocalUses localUses = new SimpleLocalUses(graph, localDefs);

      /* fold in NewExpr's with specialinvoke's */
      while (it.hasNext())
        {
          Stmt s = (Stmt)it.next();
            
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
              Unit use = ((UnitValueBoxPair)(luIter.next())).unit;
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
              
              use.redirectJumpsToThisTo(constructStmt);
              units.insertBefore(constructStmt, use);
              units.remove(use);
            }
          if (MadeNewInvokeExpr)
            {
              units.remove(s);
            }
        }
    }  
}
