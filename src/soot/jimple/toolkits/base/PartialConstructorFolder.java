/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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


 



package soot.jimple.toolkits.base;
import soot.options.*;

import soot.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.grimp.*;
import soot.util.*;
import java.util.*;
import soot.tagkit.*;

public class PartialConstructorFolder extends BodyTransformer
{
    //public JimpleConstructorFolder( Singletons.Global g ) {}
    //public static JimpleConstructorFolder v() { return G.v().JimpleConstructorFolder(); }

    private List types;

    public void setTypes(List t){
        types = t;
    }

    public List getTypes(){
        return types;
    }
    
    /** This method pushes all newExpr down to be the stmt directly before every
     * invoke of the init only if they are in the types list*/
    
    public void internalTransform(Body b, String phaseName, Map options)
    {
        JimpleBody body = (JimpleBody)b;

        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() +
                "] Folding Jimple constructors...");

        Chain units = body.getUnits();
        List stmtList = new ArrayList();
        stmtList.addAll(units);

        Iterator it = stmtList.iterator();
        Iterator nextStmtIt = stmtList.iterator();
        // start ahead one
        nextStmtIt.next();
        
        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);
        
        LocalDefs localDefs = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
        LocalUses localUses = new SimpleLocalUses(graph, localDefs);

        /* fold in NewExpr's with specialinvoke's */
        while (it.hasNext())
        {
            Stmt s = (Stmt)it.next();
            
            
            if (!(s instanceof AssignStmt))
                continue;
            
            /* this should be generalized to ArrayRefs */
            // only deal with stmts that are an local = newExpr
            Value lhs = ((AssignStmt)s).getLeftOp();
            if (!(lhs instanceof Local))
                continue;
            
            Value rhs = ((AssignStmt)s).getRightOp();
            if (!(rhs instanceof NewExpr))
                continue;

            
            //check if very next statement is invoke -->
            //this indicates there is no control flow between
            //new and invoke and should do nothing
            if (nextStmtIt.hasNext()){
                Stmt next = (Stmt)nextStmtIt.next();
                if (next instanceof InvokeStmt){
                    InvokeStmt invoke = (InvokeStmt)next;
                
                    if (invoke.getInvokeExpr() instanceof SpecialInvokeExpr) {
                        SpecialInvokeExpr invokeExpr = (SpecialInvokeExpr)invoke.getInvokeExpr();
                        if (invokeExpr.getBase() == lhs){
                            break;
                        }
                    }
                }
            }
            

            // check if new is in the types list - only process these
            if (!types.contains(((NewExpr)rhs).getType())) continue;
            
            List lu = localUses.getUsesOf((DefinitionStmt)s);
            Iterator luIter = lu.iterator();
            boolean MadeNewInvokeExpr = false;
          
            while (luIter.hasNext())
            {
                Unit use = ((UnitValueBoxPair)(luIter.next())).unit;
                if (!(use instanceof InvokeStmt))
                    continue;
                InvokeStmt is = (InvokeStmt)use;
                if (!(is.getInvokeExpr() instanceof SpecialInvokeExpr) ||
                  lhs != ((SpecialInvokeExpr)is.getInvokeExpr()).getBase())
                    continue;
              
             //make a new one here 
              AssignStmt constructStmt = Jimple.v().newAssignStmt
                (((DefinitionStmt)s).getLeftOp(), ((DefinitionStmt)s).getRightOp());
              constructStmt.setRightOp
                (Jimple.v().newNewExpr
                 (((NewExpr)rhs).getBaseType()));
              MadeNewInvokeExpr = true;
              
              // redirect jumps
              use.redirectJumpsToThisTo(constructStmt);
              // insert new one here
              units.insertBefore(constructStmt, use);
              
              constructStmt.addTag((SourceLnPosTag)s.getTag("SourceLnPosTag"));
            }
          if (MadeNewInvokeExpr)
            {
              units.remove(s);
            }
        }
    }  
}
