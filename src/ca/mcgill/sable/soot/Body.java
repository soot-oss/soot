package ca.mcgill.sable.soot;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
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

 - Modified on March 24, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Add some edges to the flow graph regarding exceptions.
 
 - Modified on March 15, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Added a pseudo topological order iterator (and its reverse).
   Moved in Patrick's getPath code.
   
 - Modified on March 13, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Re-organized the timers.

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on September 22, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Added support for exception edge inclusion.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Many changes.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.toolkit.scalar.*;


public class Body 
{

    /* temp */
    SootMethod method;

    Chain localChain, trapChain;
    PatchingChain unitChain;

    public SootMethod getMethod()
    {
        return method;
    }
    
    public int getLocalCount()
    {
        return localChain.size();
    }

    public Body(SootMethod m) 
    {        
        localChain = new HashChain();
        trapChain = new HashChain();
        unitChain = new PatchingChain(new HashChain());

        this.method = m;
        Chain units = getUnits();

        Iterator it = units.iterator();
        HashMap bindings = new HashMap();

        // Clone units in body's statement list 
        while(it.hasNext()) {
            Unit original = (Unit) it.next();
            Unit copy = (Unit) original.clone();
             
            // Add cloned unit to our unitChain.
            unitChain.addLast(copy);

            // Build old <-> new map to be able to patch up references to other units 
            // within the cloned units. (these are still refering to the original
            // unit objects).
            bindings.put(original, copy);
            //            stmtList.add(copy); //hack
        }

        // Clone trap units.
        it = getTraps().iterator();
        while(it.hasNext()) {
            Trap original = (Trap) it.next();
            Trap copy = (Trap) original.clone();
            
            // Add cloned unit to our trap list.
            trapChain.addLast(copy);

            // Store old <-> new mapping.
            bindings.put(original, copy);
        }

        
        // Clone local units.
        it = getLocals().iterator();
        while(it.hasNext()) {
            Value original = (Value) it.next();
            Value copy = (Value) original.clone();
            
            // Add cloned unit to our trap list.
            localChain.addLast(copy);

            // Build old <-> new mapping.
            bindings.put(original, copy);
        }
        


        // Patch up references within units using our (old <-> new) map.
        it = getUnitBoxes().iterator();
        while(it.hasNext()) {
            UnitBox box = (UnitBox) it.next();
            Unit newObject, oldObject = box.getUnit();
            
            // if we have a reference to an old object, replace it 
            // it's clone.
            if( (newObject = (Unit)  bindings.get(oldObject)) != null )
                box.setUnit(newObject);
                
        }        



        // backpatching all local variables.
        it = getUseAndDefBoxes().iterator();
        while(it.hasNext()) {
            ValueBox vb = (ValueBox) it.next();
            if(vb.getValue() instanceof Local) 
                vb.setValue((Value) bindings.get(vb.getValue()));
        }

        validateLocals();


    }
    
    public void validateLocals()
    {
        Iterator it =  getUseAndDefBoxes().iterator();
        
        while(it.hasNext()){
            ValueBox vb = (ValueBox) it.next();
            Value value;
            if( (value = vb.getValue()) instanceof Local) {
                if(!localChain.contains(value))
                    throw new RuntimeException("not in chain");
                
            }
        }
       
        
    }
        
    public Chain getLocals() {return localChain;} 
    public Chain getTraps() {return trapChain;}
    public Chain getUnits() {return unitChain;}
                 
    public List getUnitBoxes() 
    {
        ArrayList unitBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            unitBoxList.addAll(item.getUnitBoxes());  
        }
        
        it = trapChain.iterator();
        while(it.hasNext()) {
            Trap item = (Trap) it.next();
            unitBoxList.addAll(item.getUnitBoxes());  
        }
        
        return unitBoxList;
    }

    
    public List getUseBoxes()
    {
        ArrayList useBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            useBoxList.addAll(item.getUseBoxes());  
        }
        return useBoxList;
    }


    public List getDefBoxes()
    {
        ArrayList defBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            defBoxList.addAll(item.getDefBoxes());  
        }
        return defBoxList;
    }

    public List getUseAndDefBoxes()
    {        
        ArrayList useAndDefBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            useAndDefBoxList.addAll(item.getUseAndDefBoxes());  
        }
        return useAndDefBoxList;
    }

    
    


     public void printTo(java.io.PrintWriter out)
    {
        printTo(out, 0);
    }

    public void printTo(PrintWriter out, int printBodyOptions)
    {
        boolean isPrecise = !PrintJimpleBodyOption.useAbbreviations(printBodyOptions);
 

        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);

        out.println("    " + getMethod().getDeclaration());        
        out.println("    {");


        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(this.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = this.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    List localList;
 
                    String typeName = (isPrecise) ? local.getType().toString() : local.getType().toBriefString();
                    
                    if(typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while(typeIt.hasNext())
                {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();

                    out.print("        " + type + " ");

                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
                }
            }


            if(!typeToLocals.isEmpty())
                out.println();
        }

        // Print out statements
            printStatementsInBody(out, isPrecise);

        out.println("    }");
    }

    void printStatementsInBody(java.io.PrintWriter out, boolean isPrecise)
    {
        
        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);
        UnitGraph unitGraph = new BriefUnitGraph(this);
        
        CompleteUnitGraph completeUnitGraph = new CompleteUnitGraph(this);
        SimpleLocalDefs localDefs = new SimpleLocalDefs(completeUnitGraph);

        // Create statement name table
        {
            Iterator boxIt = this.getUnitBoxes().iterator();

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

                Iterator stmtIt = unitChain.iterator();

                while(stmtIt.hasNext())
                {
                    Unit s = (Unit) stmtIt.next();

                    if(labelStmts.contains(s))
                        stmtToName.put(s, "label" + (labelCount++));
                }
            }
        }

        
        Iterator unitIt = unitChain.iterator();
        Unit currentStmt = null, previousStmt;

        while(unitIt.hasNext()) {
            
            previousStmt = currentStmt;
            currentStmt = (Unit) unitIt.next();
            
            // Put an empty line if the previous node was a branch node, the current node is a join node
            //   or the previous statement does not have this statement as a successor, or if
            //   this statement has a label on it
            
            if(currentStmt != unitChain.getFirst()) {
                

                if(unitGraph.getSuccsOf(previousStmt).size() != 1 ||
                   unitGraph.getPredsOf(currentStmt).size() != 1 ||
                   stmtToName.containsKey(currentStmt))
                    out.println();
                else {
                    // Or if the previous node does not have this statement as a successor.
                    
                    List succs = unitGraph.getSuccsOf(previousStmt);
                    
                    if(succs.get(0) != currentStmt)
                        out.println();
                }
            }
             
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
            Iterator trapIt = this.getTraps().iterator();

            if(trapIt.hasNext())
                out.println();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                out.println("        catch '" + trap.getException().getName() + "' from " +
                    stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
                    " with " + stmtToName.get(trap.getHandlerUnit()) + ";");
            }
        }
    }

    


}
