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


package soot.jimple;

import soot.tagkit.*;
import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

/** This class implements the <code>printStatementsInBody</code> method,
  * which writes out a JimpleBody to a PrintWriter.
  *
  * Users may choose to replace this class with another StmtPrinter
  * implementation, for instance to include analysis results. This should
  * be done by setting the JimpleStmtPrinter in the Scene. */
public class DefaultStmtPrinter implements StmtPrinter
{
    private static DefaultStmtPrinter instance = new DefaultStmtPrinter();
    private DefaultStmtPrinter() {}

    public static DefaultStmtPrinter v() { return instance; }

    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    public void printStatementsInBody(Body body, java.io.PrintWriter out, boolean isPrecise, boolean isNumbered)
    {

	
        Chain units = body.getUnits();

        Map stmtToName = new HashMap(units.size() * 2 + 1, 0.7f);
        UnitGraph unitGraph = new soot.toolkits.graph.BriefUnitGraph(body);

        // Create statement name table
        {
            Iterator boxIt = body.getUnitBoxes().iterator();

            Set labelStmts = new HashSet();

            // Build labelStmts
            {
                if(!isNumbered)
                    while(boxIt.hasNext())
                    {
                        UnitBox box = (UnitBox) boxIt.next();
                        Unit stmt = (Unit) box.getUnit();
    
                        labelStmts.add(stmt);
                    }
                else
                    labelStmts.addAll(units);

            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;

                Iterator stmtIt = units.iterator();
                
                
                while(stmtIt.hasNext())
                {
                    Unit s = (Unit) stmtIt.next();

                    if(labelStmts.contains(s))
                    {
                        if(isNumbered)
                            stmtToName.put(s, new Integer(labelCount++).toString());
                        else
                            stmtToName.put(s, "label" + (labelCount++));
                    }
                }
            }
        }        


        
        Iterator unitIt = units.iterator();
        Unit currentStmt = null, previousStmt;
        String indent = (isNumbered) ? "    " : "        ";
        
	
        while(unitIt.hasNext()) {
            
            previousStmt = currentStmt;
            currentStmt = (Unit) unitIt.next();
            
            // Print appropriate header.
                if(isNumbered)
                    out.print("  " + stmtToName.get(currentStmt) + ":");
                else            
                {
                    // Put an empty line if the previous node was a branch node, the current node is a join node
                    //   or the previous statement does not have body statement as a successor, or if
                    //   body statement has a label on it

                    if(currentStmt != units.getFirst()) 
                        {       
                            if(unitGraph.getSuccsOf(previousStmt).size() != 1 ||
                               unitGraph.getPredsOf(currentStmt).size() != 1 ||
                               stmtToName.containsKey(currentStmt)) {
                                out.println();
				if (body.getMethod().getDeclaringClass().isAddJimpleLn()) {
                            		body.getMethod().getDeclaringClass().incJimpleLnNum();
				}
			    }
                            else {
                                // Or if the previous node does not have body statement as a successor.
                                
                                List succs = unitGraph.getSuccsOf(previousStmt);
                                
                                if(succs.get(0) != currentStmt) {
                                    out.println();
				    if (body.getMethod().getDeclaringClass().isAddJimpleLn()) {
					body.getMethod().getDeclaringClass().incJimpleLnNum();
				    }
				    
				}
                            }
                        }
                    
                     if(stmtToName.containsKey(currentStmt)) {
                         out.println("     " + stmtToName.get(currentStmt) + ":");
			 if (body.getMethod().getDeclaringClass().isAddJimpleLn()) {
				body.getMethod().getDeclaringClass().incJimpleLnNum();
			 }
			 
		     }
		     
                }
                   

                if(isPrecise)
                    out.print(currentStmt.toString(stmtToName, indent));
                else
                    out.print(currentStmt.toBriefString(stmtToName, indent));
	
		out.print(";"); 
		out.println();
		if (body.getMethod().getDeclaringClass().isAddJimpleLn()) { 
			body.getMethod().getDeclaringClass().setJimpleLnNum(addJimpleLnTags(body.getMethod().getDeclaringClass().getJimpleLnNum(), currentStmt));
	        }
		
		// only print them if not generating attributes files 
		// because they mess up line number
		if (!body.getMethod().getDeclaringClass().isAddJimpleLn()){
		Iterator tagIterator = currentStmt.getTags().iterator();
		while(tagIterator.hasNext()) {
		    Tag t = (Tag) tagIterator.next();		   		    
		    out.println(t);
		}		 
        }
        }

        // Print out exceptions
        {
            Iterator trapIt = body.getTraps().iterator();

            if(trapIt.hasNext()) {
                out.println();
		if (body.getMethod().getDeclaringClass().isAddJimpleLn()) {
	  		body.getMethod().getDeclaringClass().incJimpleLnNum();
		}
	    }

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                out.println("        catch " + Scene.v().quotedNameOf(trap.getException().getName()) + " from " +
                    stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
                    " with " + stmtToName.get(trap.getHandlerUnit()) + ";");
		
		if (body.getMethod().getDeclaringClass().isAddJimpleLn()) {
			body.getMethod().getDeclaringClass().incJimpleLnNum();
		}
						
            }
        }

    }

    private int addJimpleLnTags(int lnNum, Unit stmt) {
	stmt.addTag(new JimpleLineNumberTag(lnNum));
	lnNum++;
	return lnNum;
    }
    
    // moved here from body ; should be factorized with the above
    public void printDebugStatementsInBody(Body b, java.io.PrintWriter out, boolean isPrecise)
    {
        Map stmtToName = new HashMap(b.getUnits().size() * 2 + 1, 0.7f);

        // Create statement name table
        {
            Iterator boxIt = b.getUnitBoxes().iterator();

            Set labelStmts = new HashSet();

            // Build labelStmts
            {
                while(boxIt.hasNext())
                {
                    UnitBox box = (UnitBox) boxIt.next();
                    Unit stmt = (Unit) box.getUnit();

                    labelStmts.add(stmt);
                }
            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;

                Iterator stmtIt = b.getUnits().iterator();

                while(stmtIt.hasNext())
                {
                    Unit s = (Unit) stmtIt.next();

                    if(labelStmts.contains(s))
                        stmtToName.put(s, "label" + (labelCount++));
                }
            }
        }

        
        Iterator unitIt = b.getUnits().iterator();
        Unit currentStmt = null, previousStmt;

        while(unitIt.hasNext()) {
            
            previousStmt = currentStmt;
            currentStmt = (Unit) unitIt.next();
            
            if(stmtToName.containsKey(currentStmt))
                out.println("     " + stmtToName.get(currentStmt) + ":");

            if(isPrecise)
                out.print(currentStmt.toString(stmtToName, "        "));
            else
                out.print(currentStmt.toBriefString(stmtToName, "        "));

            out.print(";"); 
            out.println();
        }

        // Print out exceptions
        {
            Iterator trapIt = b.getTraps().iterator();

            if(trapIt.hasNext())
                out.println();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                out.println("        catch " + trap.getException().getName() + " from " +
                    stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
                    " with " + stmtToName.get(trap.getHandlerUnit()) + ";");
            }
        }
    }

}


