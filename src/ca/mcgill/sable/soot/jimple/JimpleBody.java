/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
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

 - Modified on March 23, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Made some tweeks to make printTo deterministic.
   
 - Modified on March 16, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Introduced a patch to get around current typing limitations.

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 21, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Added a constructor and handling of debug option.
   
 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on October 4, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Errors in type inference now throws an exception (disabled with -debug).

 - Modified on September 3, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Sets coffi pointers to null after usage for memory release.

 - Modified on July 29, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added code to hande the "noSplitting" global option.

 - Modified on July 29, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added code to hande the "noSplitting" global option.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Added a constructor for StmtListBody.
   And other misc. changes.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
import java.io.*;

public class JimpleBody extends AbstractBody implements StmtBody
{
    StmtList stmtList;
    
    /**
        Construct an empty JimpleBody 
     **/
     
    public JimpleBody(SootMethod m)
    {
        super(m);
        stmtList = new StmtList(this);   
    }


    public JimpleBody(UnitBody body)
    {
        super(body.getMethod());
        locals = HashChain.hashChainToList((HashChain) body.getLocals());
        stmtList = new StmtList(this);
        stmtList.addAll(HashChain.hashChainToList((HashChain)body.getUnits()));
        traps = HashChain.hashChainToList((HashChain) body.getTraps());
    }


    public JimpleBody(Body body)
    {
        this(body, 0);
    }
    
    /**
        Constructs a JimpleBody from the given Body.
     */

    public JimpleBody(Body body, int buildOptions)
    {
        super(body.getMethod());
        ClassFileBody fileBody;

        if(body instanceof ClassFileBody)
            fileBody = (ClassFileBody) body;
        else
            throw new RuntimeException("Can only construct JimpleBody's directly from ClassFileBody's (for now)");

        this.stmtList = new StmtList(this);

        ca.mcgill.sable.soot.coffi.ClassFile coffiClass = fileBody.coffiClass;
        ca.mcgill.sable.soot.coffi.method_info coffiMethod = fileBody.coffiMethod;

        /*
            I need to set these to null to free Coffi structures.
        fileBody.coffiClass = null;
        bafBody.coffiMethod = null;

        */
        if(Main.isVerbose)
            System.out.println("[" + getMethod().getName() + "] Jimplifying...");

        if(Modifier.isAbstract(getMethod().getModifiers()) || Modifier.isNative(getMethod().getModifiers()))
            return;
            
        if(Main.isProfilingOptimization)
            Main.conversionTimer.start();

        if(coffiMethod.instructions == null)
        {
            if(Main.isVerbose)
                System.out.println("[" + getMethod().getName() +
                    "]     Parsing Coffi instructions...");

             coffiClass.parseMethod(coffiMethod);
        }
                
        if(coffiMethod.cfg == null)
        {
            if(Main.isVerbose)
                System.out.println("[" + getMethod().getName() +
                    "]     Building Coffi CFG...");

             new ca.mcgill.sable.soot.coffi.CFG(coffiMethod);

         }

         if(Main.isVerbose)
             System.out.println("[" + getMethod().getName() +
                    "]     Producing naive Jimple...");
                    
         coffiMethod.cfg.jimplify(coffiClass.constant_pool,
             coffiClass.this_class, this);

         coffiMethod.instructions = null;
         coffiMethod.cfg = null;
            // don't need these structures anymore.
                    
         if(Main.isProfilingOptimization)
         {
             Main.conversionTimer.end();
             Main.conversionLocalCount += getLocalCount();
             Main.conversionStmtCount += stmtList.size();
         }

        // Jimple.printStmtList_debug(this, System.out);

        if(!BuildJimpleBodyOption.noSplitting(buildOptions))
        {
            if(Main.isProfilingOptimization)
                Main.splitTimer.start();

            LocalSplitter.splitLocals(this);

            if(Main.isProfilingOptimization)
            {
                Main.splitTimer.end();
                Main.splitLocalCount += getLocalCount();
                Main.splitStmtCount += stmtList.size();
            }

            if(!BuildJimpleBodyOption.noTyping(buildOptions))
            {
                if(Main.isProfilingOptimization)
                    Main.assignTimer.start();

                // Jimple.printStmtListBody_debug(this, System.out);
                //System.out.println("before typing");
                //printTo(new PrintWriter(System.out, true));
                Transformations.assignTypesToLocals(this);

                //System.out.println("after typing");
                //printTo(new PrintWriter(System.out, true));
                
                if(typingFailed())
                {
                    patchForTyping();
                    
                    Transformations.assignTypesToLocals(this);
                    
                    if(typingFailed())
                        throw new RuntimeException("type inference failed!");
                        
                }
                
                if(Main.isProfilingOptimization)
                {
                    Main.assignLocalCount += getLocalCount();
                    Main.assignStmtCount += stmtList.size();

                    Main.assignTimer.end();
                }
            }
        }
        
        //printTo(new PrintWriter(System.out, true));
        
        if(BuildJimpleBodyOption.aggressiveAggregating(buildOptions))
        {
            Aggregator.aggregate(this);
            Transformations.removeUnusedLocals(this);
        }
        else if(!BuildJimpleBodyOption.noAggregating(buildOptions))
        {
            Aggregator.aggregateStackVariables(this);
            Transformations.removeUnusedLocals(this);            
        }

        if(!BuildJimpleBodyOption.useOriginalNames(buildOptions))
            Transformations.standardizeLocalNames(this);
        else
        {   
            LocalPacker.unsplitOriginalLocals(this);
            Transformations.standardizeStackLocalNames(this);
        }
        
        //printDebugTo(new PrintWriter(System.out, true));
        
        if(BuildJimpleBodyOption.usePacking(buildOptions))
        {
            LocalPacker.packLocals(this);
        }

        if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
            ca.mcgill.sable.soot.Main.stmtCount += getStmtList().size();
            
    }

    /** Temporary patch to get the typing algorithm working.
      */
      
    private void patchForTyping()
    {
        int localCount = 0;
        Local newObjectLocal = null;
        
        Transformations.cleanupCode(this);
        Transformations.removeUnusedLocals(this);
        
        int count = stmtList.size();
        for (int ind = 0; ind < count; ind++)
          {
            Stmt s = (Stmt) stmtList.get(ind);
                    
            if(s instanceof AssignStmt)
            {
                AssignStmt as = (AssignStmt) s;
                
                if(as.getRightOp() instanceof NewExpr &&
                   as.getLeftOp() instanceof Local)
                {
                    // Add new local
                        Local tmpLocal = Jimple.v().newLocal("tmp" + localCount, 
                            UnknownType.v());
                        addLocal(tmpLocal);
                            
                        localCount++;
                    
                    // Change left hand side of new
                        newObjectLocal = (Local) as.getLeftOp();
                        as.setLeftOp(tmpLocal);
                    
                    // Find matching special invoke
                    {
                        ListIterator matchIt = stmtList.listIterator(ind+1);
                        boolean foundMatch = false;
                               
                        while(matchIt.hasNext())
                        {   
                            Stmt r = (Stmt) matchIt.next();
                            
                            if(r instanceof InvokeStmt)
                            {
                               InvokeExpr expr = (InvokeExpr) ((InvokeStmt) r).getInvokeExpr();
                                
                                if(expr instanceof SpecialInvokeExpr &&
                                    ((SpecialInvokeExpr) expr).getBase() == newObjectLocal)
                                {
                                    // Set base of special invoke
                                        ((SpecialInvokeExpr) expr).setBase(tmpLocal);
                                    
                                    // Add copy newObjectLocal = tmpLocal
                                    matchIt.add(Jimple.v().newAssignStmt(newObjectLocal,
                                        tmpLocal));
                                    count++;
                                 
                                    foundMatch = true;
                                    break;       
                                }
                            }
                        }
                        
                        if(!foundMatch)
                            throw new RuntimeException("unable to patch code"); 
                    }
                }
            }
        }
        
        
    }
    
    private boolean typingFailed()
    {
        // Check to see if any locals are untyped
        {
            Iterator localIt = this.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();

                  if(l.getType().equals(UnknownType.v()) ||
                    l.getType().equals(ErroneousType.v()))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    
    public StmtList getStmtList()
    {
        return stmtList;
    }

    public void redirectJumps(Stmt oldLocation, Stmt newLocation)
    {
        List boxesPointing = oldLocation.getBoxesPointingToThis();

        Object[] boxes = boxesPointing.toArray();
            // important to change this to an array to have a static copy

        for(int i = 0; i < boxes.length; i++)
        {
            StmtBox box = (StmtBox) boxes[i];

            if(box.getUnit() != oldLocation)
                throw new RuntimeException("Something weird's happening");

            box.setUnit(newLocation);
        }

    }

    public void eliminateBackPointersTo(Stmt oldLocation)
    {
        Iterator boxIt = oldLocation.getUnitBoxes().iterator();

        while(boxIt.hasNext())
        {
            StmtBox box = (StmtBox) boxIt.next();
            Stmt stmt = (Stmt) box.getUnit();

            stmt.getBoxesPointingToThis().remove(oldLocation);
        }
    }

    public List getUnitBoxes()
    {
        List stmtBoxes = new ArrayList();

        // Put in all statement boxes from the statements
            Iterator stmtIt = stmtList.iterator();

            while(stmtIt.hasNext())
            {
                Stmt stmt = (Stmt) stmtIt.next();

                Iterator boxIt = stmt.getUnitBoxes().iterator();

                while(boxIt.hasNext())
                    stmtBoxes.add(boxIt.next());
            }

        // Put in all statement boxes from the trap table
        {
            Iterator trapIt = getTraps().iterator();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();
                stmtBoxes.addAll(trap.getUnitBoxes());
            }
        }

        return stmtBoxes;
    }

    public void printTo(java.io.PrintWriter out)
    {
        printTo(out, 0);
    }

    public void printTo(PrintWriter out, int printBodyOptions)
    {
        boolean isPrecise = !PrintJimpleBodyOption.useAbbreviations(printBodyOptions);

        /*
        if(PrintJimpleBodyOption.debugMode(printBodyOptions))
        {
            print_debug(out);
            return;
        }
        */
        
        //System.out.println("Constructing the graph of " + getName() + "...");

        StmtList stmtList = this.getStmtList();

        Map stmtToName = new HashMap(stmtList.size() * 2 + 1, 0.7f);

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
        StmtList stmtList = this.getStmtList();

        Map stmtToName = new HashMap(stmtList.size() * 2 + 1, 0.7f);
        StmtGraph stmtGraph = new BriefStmtGraph(stmtList);

        // Create statement name table
        {
            Iterator boxIt = this.getUnitBoxes().iterator();

            Set labelStmts = new HashSet();

            // Build labelStmts
            {
                while(boxIt.hasNext())
                {
                    StmtBox box = (StmtBox) boxIt.next();
                    Stmt stmt = (Stmt) box.getUnit();

                    labelStmts.add(stmt);
                }
            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;

                Iterator stmtIt = stmtList.iterator();

                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();

                    if(labelStmts.contains(s))
                        stmtToName.put(s, "label" + (labelCount++));
                }
            }
        }

        for(int j = 0; j < stmtList.size(); j++)
        {
            Stmt s = ((Stmt) stmtList.get(j));

            // Put an empty line if the previous node was a branch node, the current node is a join node
            //   or the previous statement does not have this statement as a successor, or if
            //   this statement has a label on it
            {
                if(j != 0)
                {
                    Stmt previousStmt = (Stmt) stmtList.get(j - 1);

                    if(stmtGraph.getSuccsOf(previousStmt).size() != 1 ||
                        stmtGraph.getPredsOf(s).size() != 1 ||
                        stmtToName.containsKey(s))
                        out.println();
                    else {
                        // Or if the previous node does not have this statement as a successor.

                        List succs = stmtGraph.getSuccsOf(previousStmt);

                        if(succs.get(0) != s)
                            out.println();

                    }
                }
            }

            if(stmtToName.containsKey(s))
                out.println("     " + stmtToName.get(s) + ":");

            if(isPrecise)
                out.print(s.toString(stmtToName, "        "));
            else
                out.print(s.toBriefString(stmtToName, "        "));

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

    public void printDebugTo(java.io.PrintWriter out)
    {   
        StmtBody stmtBody = this; 
        StmtList stmtList = stmtBody.getStmtList();
        Map stmtToName = new HashMap(stmtList.size() * 2 + 1, 0.7f);
        //CompleteStmtGraph stmtGraph = new CompleteStmtGraph(stmtList);
        
        //LocalDefs localDefs = new SimpleLocalDefs(stmtGraph);

        System.out.println("debug output for " + getMethod().getSignature());
        /*
        LocalUses localUses = new LocalUses(stmtGraph, localDefs);
*/
        
        //LocalCopies localCopies = new SimpleLocalCopies(stmtGraph);
        // LiveLocals liveLocals = new SimpleLiveLocals(stmtGraph);
        //EqualLocals equalLocals = new SimpleEqualLocals(stmtGraph);
        
        // Create statement name table
        {
           int labelCount = 0;

            Iterator stmtIt = stmtList.iterator();

            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();

                stmtToName.put(s, new Integer(labelCount++).toString());
            }
        }

        Zonation zonation = new Zonation(this);
        
        for(int j = 0; j < stmtList.size(); j++)
        {
            Stmt s = ((Stmt) stmtList.get(j));

            out.print("    " + stmtToName.get(s) + ": ");

            out.print(s.toBriefString(stmtToName, "        "));
            out.print(";");

            out.print(zonation.getZoneOf(s));
            
        /*               
            // Print info about live locals
            {
                out.print(liveLocals.getLiveLocalsAfter(s));
            } */
            
            /*
            // Print info about local copies
            {
                out.print(localCopies.getCopiesBefore(s));
            }
            */
            /*
            // Print info about local equalities
            {
                out.print(equalLocals.getCopiesAt(s));
            }
*/

             /*
             // Print info about uses
                if(s instanceof DefinitionStmt)
                {
                    Iterator useIt = localUses.getUsesOf((DefinitionStmt) s).iterator();

                    out.print("   (");

                    while(useIt.hasNext())
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(stmtToName.get(useIt.next()));
                    }

                    out.print(")");
                }
            */
        /*
            // Print info about defs
            {
                Iterator boxIt = s.getUseBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox useBox = (ValueBox) boxIt.next();

                    if(useBox.getValue() instanceof Local)
                    {
                        Iterator defIt = localDefs.getDefsOfAt((Local) useBox.getValue(), s).iterator();

                        out.print("  " + useBox.getValue() + " = {");

                        while(defIt.hasNext())
                        {
                            out.print(stmtToName.get((Stmt) defIt.next()));

                            if(defIt.hasNext())
                                out.print(", ");
                        }

                        out.print("}");
                    }
                }
            } 
          */
            
            /*
            // Print info about successors
            {
                Iterator succIt = stmtGraph.getSuccsOf(s).iterator();

                out.print("    [");

                if(succIt.hasNext())
                {
                    out.print(stmtToName.get(succIt.next()));

                    while(succIt.hasNext())
                    {
                        Stmt stmt = (Stmt) succIt.next();

                        out.print(", " + stmtToName.get(stmt));
                    }
                }

                out.print("]");
            }
                */
            /*
            // Print info about predecessors
            {
                Stmt[] preds = stmtGraph.getPredsOf(s);

                out.print("    {");

                for(int k = 0; k < preds.length; k++)
                {
                    if(k != 0)
                        out.print(", ");

                    out.print(stmtToName.get(preds[k]));
                }

                out.print("}");
            }
            */
            out.println();
        }

        // Print out exceptions
        {
            Iterator trapIt = stmtBody.getTraps().iterator();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                out.println(".catch " + trap.getException().getName() + " from " +
                    stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
                    " with " + stmtToName.get(trap.getHandlerUnit()));
            }
        }
    }
}









