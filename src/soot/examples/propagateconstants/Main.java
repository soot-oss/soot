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





package soot.examples.propagateconstants;

import soot.util.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import java.io.*;
import java.util.*;

/**
    PropagateConstants example.
 
 */
 
public class Main
{
    public static void main(String[] args)
    {
        SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
        PrintWriter out = new PrintWriter(System.out, true);
        
        out.println("Before copy propagation: ");
        
        sClass.setApplicationClass();
        
        // Convert all methods to Jimple
        {
            Iterator methodIt = sClass.getMethods().iterator();
            
            while(methodIt.hasNext())
            {
                SootMethod m = (SootMethod) methodIt.next();
                
                m.setActiveBody(Jimple.v().newBody(new ClassFileBody(m)));
            }
        }
           
        sClass.printTo(out, PrintJimpleBodyOption.USE_ABBREVIATIONS);
                               
        // Perform cp on each method
        {
            Iterator methodIt = sClass.getMethods().iterator();
            
            while(methodIt.hasNext())
            {
                SootMethod m = (SootMethod) methodIt.next();
                
                JimpleBody body = (JimpleBody) m.getActiveBody();
                Chain units = body.getUnits();
                CompleteUnitGraph stmtGraph = new CompleteUnitGraph(body);
                
                LocalDefs localDefs = new SimpleLocalDefs(stmtGraph);
                Iterator stmtIt = units.iterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt stmt = (Stmt) stmtIt.next();
                    Iterator useBoxIt = stmt.getUseBoxes().iterator();
                    
                    while(useBoxIt.hasNext())
                    {
                        ValueBox useBox = (ValueBox) useBoxIt.next();
                        
                        if(useBox.getValue() instanceof Local)
                        {
                            Local l = (Local) useBox.getValue();
                            List defsOfUse = localDefs.getDefsOfAt(l, stmt);
                            
                            if(defsOfUse.size() == 1)
                            {
                                DefinitionStmt def = (DefinitionStmt) 
                                    defsOfUse.get(0);
                                
                                if(def.getRightOp() instanceof Constant)
                                {
                                    if(useBox.canContainValue(def.getRightOp()))
                                        useBox.setValue(def.getRightOp());
                                }
                            }
                        }
                    }
                }   
            }
        }
                     
        // Write out the new class
            System.out.println("After copy propagation: ");

            sClass.printTo(out, PrintJimpleBodyOption.USE_ABBREVIATIONS);
    }
}
