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






package soot.jimple.toolkits.scalar;
import soot.options.*;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.util.*;
import soot.toolkits.graph.*;
import java.util.*;

public class DeadAssignmentEliminator extends BodyTransformer
{
    public DeadAssignmentEliminator( Singletons.Global g ) {}
    public static DeadAssignmentEliminator v() { return G.v().soot_jimple_toolkits_scalar_DeadAssignmentEliminator(); }

    /** Eliminates dead code in a linear fashion.  Complexity is linear 
        with respect to the statements.
        
        Does not work on grimp code because of the check on the right hand
        side for side effects. 
    */
    
    protected void internalTransform(Body b, String phaseName, Map options)
    {
        boolean eliminateOnlyStackLocals = PhaseOptions.getBoolean(options, "only-stack-locals");

        if(Options.v().verbose())
            G.v().out.println("[" + b.getMethod().getName() +
                "] Eliminating dead code...");
        
        if(Options.v().time())
            Timers.v().deadCodeTimer.start();

        Set essentialStmts = new HashSet();
        LinkedList toVisit = new LinkedList();
        Chain units = b.getUnits();
        
        // Make a first pass through the statements, noting 
        // the statements we must absolutely keep. 
        {
            Iterator stmtIt = units.iterator();
            
            while(stmtIt.hasNext()) 
            {
                Stmt s = (Stmt) stmtIt.next();
                boolean isEssential = true;
                 
                if(s instanceof NopStmt)
                    isEssential = false;
                 
                if(s instanceof AssignStmt)
                {
                    AssignStmt as = (AssignStmt) s;
                    
                    if(as.getLeftOp() instanceof Local &&
                        (!eliminateOnlyStackLocals || 
                            ((Local) as.getLeftOp()).getName().startsWith("$")))
                    {
                        Value rhs = as.getRightOp();
                    
                        isEssential = false;

                        if(rhs instanceof InvokeExpr ||
                           rhs instanceof ArrayRef)
                        {
                           // Note that ArrayRef and InvokeExpr all can
                           // have side effects (like throwing a null pointer exception)
                    
                            isEssential = true;
                        }

                        if(rhs instanceof InstanceFieldRef &&
                           !(!b.getMethod().isStatic() && 
                             ((InstanceFieldRef)rhs).getBase() == 
                                    b.getThisLocal())) 
                        {
                            // Any InstanceFieldRef may have side effects,
                            // unless the base is reading from 'this'
                            // in a non-static method
                            isEssential = true;
                        }


                        else if(rhs instanceof DivExpr || 
                            rhs instanceof RemExpr)
                        {
                            BinopExpr expr = (BinopExpr) rhs;
                            
                            if(expr.getOp1().getType().equals(IntType.v()) ||
                                expr.getOp2().getType().equals(IntType.v()) ||
                               expr.getOp1().getType().equals(LongType.v()) ||
                                expr.getOp2().getType().equals(LongType.v()))
                            {
                                // Can trigger a division by zero   
                                isEssential = true;    
                            }        
                        }

                        else if(rhs instanceof CastExpr)
                        {
                            // Can trigger ClassCastException
                            isEssential = true;
                        }
                        else if(rhs instanceof NewArrayExpr
                                || rhs instanceof NewMultiArrayExpr) {
                            // can throw exception
                            isEssential = true;
                                }
                        else if (rhs instanceof NewExpr
                  			  || (rhs instanceof FieldRef
                  			    && !(rhs instanceof InstanceFieldRef))) {
                          // Can trigger class initialization
                          isEssential = true;
                        } 
                    }
                }
                
                if(isEssential)
                {
                    essentialStmts.add(s);
                    toVisit.addLast(s);                    
                }                     
            }
        }

        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(b);
        LocalDefs defs = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
        LocalUses uses = new SimpleLocalUses(graph, defs);
        
        // Add all the statements which are used to compute values
        // for the essential statements, recursively
        {
            
            while(!toVisit.isEmpty())
            {
                Stmt s = (Stmt) toVisit.removeFirst();
                Iterator boxIt = s.getUseBoxes().iterator();
                                
                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();
                    
                    if(box.getValue() instanceof Local)
                    {
                        Iterator defIt = defs.getDefsOfAt(
                            (Local) box.getValue(), s).iterator();
                        
                        while(defIt.hasNext())
                        {
                            // Add all the definitions as essential stmts
                            
                            Stmt def = (Stmt) defIt.next();
                            
                            if(!essentialStmts.contains(def))
                            {
                                essentialStmts.add(def);
                                toVisit.addLast(def);
                            }    
                        }         
                    }
                }
            }
        }
        
        // Remove the dead statements
        {
            Iterator stmtIt = units.iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                if(!essentialStmts.contains(s)){
                    stmtIt.remove();
                    s.clearUnitBoxes();
                }
                else if(s instanceof AssignStmt &&
                    ((AssignStmt) s).getLeftOp() == ((AssignStmt) s).getRightOp() &&
                    ((AssignStmt) s).getLeftOp() instanceof Local)
                {
                    // Stmt is of the form a = a which is useless
                    
                    stmtIt.remove();
                    s.clearUnitBoxes();
                }   
            }
        }
        
        // Eliminate dead assignments from invokes such as x = f(), where
        //    x is no longer used
        {
            Iterator stmtIt = units.snapshotIterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                if(s instanceof AssignStmt &&
                    s.containsInvokeExpr())
                {
                    Local l = (Local) ((AssignStmt) s).getLeftOp();
                    InvokeExpr e = (InvokeExpr) s.getInvokeExpr();
                    
                    // Just find one use of l which is essential 
                    {   
                        Iterator useIt = uses.getUsesOf(s).iterator();
                        boolean isEssential = false;
                        
                        while(useIt.hasNext())
                        {   
                            UnitValueBoxPair pair = (UnitValueBoxPair)
                                useIt.next();
                                
                            if(essentialStmts.contains(pair.unit))
                            {
                                isEssential = true;
                                break;
                            }
                        }
                        
                        if(!isEssential)
                        {
                            // Transform it into a simple invoke.
                 
                            Stmt newInvoke = Jimple.v().newInvokeStmt(e);
                            newInvoke.addAllTagsOf(s);
                            
                            units.swapWith(s, newInvoke);
                        }
                    }                                        
                }
            }
        }
        
        if(Options.v().time())
            Timers.v().deadCodeTimer.end();

    }
}







