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
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;
import soot.options.CPOptions;

public class CopyPropagator extends BodyTransformer
{
    public CopyPropagator( Singletons.Global g ) {}
    public static CopyPropagator v() { return G.v().soot_jimple_toolkits_scalar_CopyPropagator(); }

    /** Cascaded copy propagator.
    
        If it encounters situations of the form: A: a = ...; B: ... x = a; C:... use (x); 
        where a has only one definition, and x has only one definition (B), then it can 
        propagate immediately without checking between B and C for redefinitions
        of a (namely) A because they cannot occur.  In this case the propagator is global.
        
        Otherwise, if a has multiple definitions then it only checks for redefinitions of
        Propagates constants and copies in extended basic blocks. 
        
        Does not propagate stack locals when the "only-regular-locals" option is true.
    */
    protected void internalTransform(Body b, String phaseName, Map opts)
    {
        CPOptions options = new CPOptions( opts );
        StmtBody stmtBody = (StmtBody)b;
        int fastCopyPropagationCount = 0;
        int slowCopyPropagationCount = 0;
        
        if(Options.v().verbose())
            G.v().out.println("[" + stmtBody.getMethod().getName() +
                "] Propagating copies...");

        if(Options.v().time())
            Timers.v().propagatorTimer.start();                
                
        Chain<Unit> units = stmtBody.getUnits();

        Map<Local, Integer> localToDefCount = new HashMap<Local, Integer>();
        
        // Count number of definitions for each local.
       	for (Unit u : units) {
       		Stmt s = (Stmt) u;
       		if(s instanceof DefinitionStmt &&
       				((DefinitionStmt) s).getLeftOp() instanceof Local) {
       			Local l = (Local) ((DefinitionStmt) s).getLeftOp();
                     
       			if(!localToDefCount.containsKey(l))
                	localToDefCount.put(l, new Integer(1));
                else 
                	localToDefCount.put(l, new Integer(localToDefCount.get(l).intValue() + 1));
       		}
       	}
        
//            ((JimpleBody) stmtBody).printDebugTo(new java.io.PrintWriter(G.v().out, true));
            
        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(stmtBody);

        LocalDefs localDefs;
        
        localDefs = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));

        // Perform a local propagation pass.
        {
            Iterator<Unit> stmtIt = (new PseudoTopologicalOrderer<Unit>()).newList(graph,false).iterator();
            while(stmtIt.hasNext())
            {
                Stmt stmt = (Stmt) stmtIt.next();
                for (ValueBox useBox : stmt.getUseBoxes())
                {
                	if(useBox.getValue() instanceof Local)
                    {
                        Local l = (Local) useBox.getValue();

                        // We force propagating nulls. If a target can only be null due to
                        // typing, we always inline that constant.
                        if (!(l.getType() instanceof NullType)) {
	                        if(options.only_regular_locals() && l.getName().startsWith("$"))
	                            continue;
	       
	                        if(options.only_stack_locals() && !l.getName().startsWith("$"))
	                            continue;
                        }
                            
                        List<Unit> defsOfUse = localDefs.getDefsOfAt(l, stmt);

                        // We can propagate the definition if we either only have
                        // one definition or all definitions are side-effect free
                        // and equal. For starters, we only support costants in
                        // the case of multiple definitions.
                        boolean propagateDef = defsOfUse.size() == 1;
                        if (!propagateDef && defsOfUse.size() > 0) {
                        	boolean agrees = true;
                        	Constant constVal = null;
                        	for (Unit defUnit : defsOfUse) {
                        		boolean defAgrees = false;
                        		if (defUnit instanceof AssignStmt) {
                        			AssignStmt assign = (AssignStmt) defUnit;
                        			if (assign.getRightOp() instanceof Constant) {
                        				if (constVal == null) {
                        					constVal = (Constant) assign.getRightOp();
                        					defAgrees = true;
                        				}
                        				else if (constVal.equals(assign.getRightOp()))
                        					defAgrees = true;
                        			}
                        		}
                        		agrees &= defAgrees;
                        	}
                        	propagateDef = agrees;
                        }
                        
                        if(propagateDef)
                        {
                            DefinitionStmt def = (DefinitionStmt) defsOfUse.get(0);

                            if (def.getRightOp() instanceof Constant) {
                            	if (useBox.canContainValue(def.getRightOp())) {
                            		useBox.setValue(def.getRightOp());
                            	}
                            	continue;
                            }
                            
                            if(def.getRightOp() instanceof Local)
                            {
                                Local m = (Local) def.getRightOp();

                                if(l != m)
                                {   
                                    Object dcObj = localToDefCount.get(m);

                                    if (dcObj == null)
                                        throw new RuntimeException("Variable " + m + " used without definition!");

                                    int defCount = ((Integer)dcObj).intValue();
                                    
                                    if(defCount == 0)
                                        throw new RuntimeException("Variable " + m + " used without definition!");
                                    else if(defCount == 1)
                                    {
                                        useBox.setValue(m);
                                        fastCopyPropagationCount++;
                                        continue;
                                    }

                                    List<Unit> path = graph.getExtendedBasicBlockPathBetween(def, stmt);
                                    
                                    if(path == null)
                                    {
                                        // no path in the extended basic block
                                        continue;
                                    }
                                     
                                    Iterator<Unit> pathIt = path.iterator();
                                    
                                    // Skip first node
                                        pathIt.next();
                                        
                                    // Make sure that m is not redefined along path
                                    {
                                        boolean isRedefined = false;
                                        
                                        while(pathIt.hasNext())
                                        {
                                            Stmt s = (Stmt) pathIt.next();
                                            
                                            if(stmt == s)
                                            {
                                                // Don't look at the last statement 
                                                // since it is evaluated after the uses
                                                
                                                break;
                                            }   
                                            if(s instanceof DefinitionStmt)
                                            {
                                                if(((DefinitionStmt) s).getLeftOp() == m)
                                                {
                                                    isRedefined = true;
                                                    break;
                                                }        
                                            }
                                        }
                                        
                                        if(isRedefined)
                                            continue;
                                            
                                    }
                                    
                                    useBox.setValue(m);
                                    slowCopyPropagationCount++;
                                }
                            }
                        }
                    }

                 }
            }
        }


        if(Options.v().verbose())
            G.v().out.println("[" + stmtBody.getMethod().getName() +
                "]     Propagated: " +
                fastCopyPropagationCount + " fast copies  " +
                slowCopyPropagationCount + " slow copies");
     
        if(Options.v().time())
            Timers.v().propagatorTimer.end();
    
    }
    
}






























