/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
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
import ca.mcgill.sable.soot.baf.*;
import java.io.*;

public class JimpleBody implements Body
{
    List locals = new ArrayList();
    SootMethod method;

    Local thisLocal;
    StmtList stmtList;
    List traps = new ArrayList();


    /**
        Constructs a JimpleBody from the given Body.
     */

    JimpleBody(SootMethod m, Body body, int buildOptions)
    {
        ClassFileBody fileBody;

        if(body instanceof ClassFileBody)
            fileBody = (ClassFileBody) body;
        else
            throw new RuntimeException("Can only construct JimpleBody's directly from ClassFileBody's (for now)");

        this.method = fileBody.getMethod();
        this.stmtList = new StmtList(this);

        ca.mcgill.sable.soot.coffi.ClassFile coffiClass = fileBody.coffiClass;
        ca.mcgill.sable.soot.coffi.method_info coffiMethod = fileBody.coffiMethod;

        /*
            I need to set these to null to free Coffi structures.
        fileBody.coffiClass = null;
        bafBody.coffiMethod = null;

        */
        if(Main.isVerbose)
            System.out.println("[" + method.getName() + "] Jimplifying...");

        if(Modifier.isAbstract(method.getModifiers()) || Modifier.isNative(method.getModifiers()))
        {
            return;
        }
        else {
            if(Main.isProfilingOptimization)
                Main.conversionTimer.start();

            if(coffiMethod.cfg == null)
            {
                if(Main.isVerbose)
                    System.out.println("[" + method.getName() +
                        "]     Building Coffi CFG...");

                new ca.mcgill.sable.soot.coffi.CFG(coffiMethod);

                if(Main.isVerbose)
                    System.out.println("[" + method.getName() +
                        "]     Coffi CFG complete.");

            }

            if(Main.isVerbose)
                System.out.println("[" + method.getName() +
                    "]      Producing naive Jimple...");

            coffiMethod.cfg.jimplify(coffiClass.constant_pool,
                coffiClass.this_class, this);

            if(Main.isProfilingOptimization)
            {
                Main.conversionTimer.end();
                Main.conversionLocalCount += getLocalCount();
                Main.conversionStmtCount += stmtList.size();
            }

             if(Main.isVerbose)
                System.out.println("[" + method.getName() +
                    "]      Naive typeless Jimple produced.");
        }

        // Jimple.printStmtList_debug(this, System.out);

        if(!BuildJimpleBodyOption.noCleanup(buildOptions))
        {
            if(Main.isProfilingOptimization)
                Main.cleanup1Timer.start();

            Transformations.cleanupCode(this);
            Transformations.removeUnusedLocals(this);

            if(Main.isProfilingOptimization)
            {
                Main.cleanup1Timer.end();
                Main.cleanup1LocalCount += getLocalCount();
                Main.cleanup1StmtCount += stmtList.size();
            }
        }

        if(!BuildJimpleBodyOption.noSplitting(buildOptions))
        {
            if(Main.isProfilingOptimization)
                Main.splitTimer.start();

            Transformations.splitLocals(this);

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
                Transformations.assignTypesToLocals(this);

                // Check to see if any locals are untyped
                {
                    Iterator localIt = this.getLocals().iterator();

                    while(localIt.hasNext())
                    {
                        Local l = (Local) localIt.next();

                        if(l.getType().equals(UnknownType.v()) ||
                            l.getType().equals(ErroneousType.v()))
                        {
                            if(!Main.isInDebugMode)
                                throw new RuntimeException("type inference failed!");
                        }
                    }
                }

                if(Main.isProfilingOptimization)
                {
                    Main.assignLocalCount += getLocalCount();
                    Main.assignStmtCount += stmtList.size();

                    Main.assignTimer.end();
                }
            }

            Transformations.renameLocals(this);

            if(!BuildJimpleBodyOption.noPacking(buildOptions))
            {
                if(Main.isProfilingOptimization)
                    Main.packTimer.start();

                Transformations.packLocals(this);
                Transformations.removeUnusedLocals(this);

                if(Main.isProfilingOptimization)
                {
                    Main.packLocalCount += getLocalCount();
                    Main.packStmtCount += stmtList.size();

                    Main.packTimer.end();
                }
            }

            /*
            if(!Main.noCleanUp)
            {
                if(Main.isProfilingOptimization)
                    Main.cleanup2Timer.start();

                Transformations.cleanupCode(this);
                Transformations.removeUnusedLocals(this);

                if(Main.isProfilingOptimization)
                {
                    Main.cleanup2LocalCount += getLocalCount();
                    Main.cleanup2StmtCount += stmtList.size();

                    Main.cleanup2Timer.end();
                }
            }
            */

            Transformations.renameLocals(this);
         }
    }

    public StmtList getStmtList()
    {
        return stmtList;
    }

    void redirectJumps(Stmt oldLocation, Stmt newLocation)
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

    void eliminateBackPointersTo(Stmt oldLocation)
    {
        Iterator boxIt = oldLocation.getUnitBoxes().iterator();

        while(boxIt.hasNext())
        {
            StmtBox box = (StmtBox) boxIt.next();
            Stmt stmt = (Stmt) box.getUnit();

            stmt.getBoxesPointingToThis().remove(oldLocation);
        }
    }

    public int getLocalCount()
    {
        return locals.size();
    }

    /**
     * Returns a backed list of locals.
     */

    public List getLocals()
    {
        return locals;
    }

    public void addLocal(Local l) throws AlreadyDeclaredException
    {
        locals.add(l);
    }

    public void removeLocal(Local l) throws IncorrectDeclarerException
    {
        locals.remove(l);
    }

    public Local getLocal(String name) throws ca.mcgill.sable.soot.jimple.NoSuchLocalException
    {
        Iterator localIt = getLocals().iterator();

        while(localIt.hasNext())
        {
            Local local = (Local) localIt.next();

            if(local.name.equals(name))
                return local;
        }

        throw new ca.mcgill.sable.soot.jimple.NoSuchLocalException();
    }


    public boolean declaresLocal(String localName)
    {
        Iterator localIt = getLocals().iterator();

        while(localIt.hasNext())
        {
            Local local = (Local) localIt.next();

            if(local.name.equals(localName))
                return true;
        }

        return false;
    }

    public SootMethod getMethod()
    {
        return method;
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
            Iterator trapIt = traps.iterator();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();
                stmtBoxes.addAll(trap.getUnitBoxes());
            }
        }

        return stmtBoxes;
    }

    public List getTraps()
    {
        return traps;
    }

    public void addTrap(Trap t)
    {
        traps.add(t);
    }

    public void removeTrap(Trap t)
    {
        traps.remove(t);
    }

    public void printTo(java.io.PrintWriter out)
    {
        printTo(out, 0);
    }

    public void printTo(PrintWriter out, int printBodyOptions)
    {
        boolean isPrecise = !PrintJimpleBodyOption.useAbbreviations(printBodyOptions);

        //System.out.println("Constructing the graph of " + getName() + "...");

        StmtList stmtList = this.getStmtList();

        Map stmtToName = new HashMap(stmtList.size() * 2 + 1, 0.7f);

        // Print out method name plus parameters
        {
            StringBuffer buffer = new StringBuffer();

            buffer.append(Modifier.toString(method.getModifiers()));

            if(buffer.length() != 0)
                buffer.append(" ");

            buffer.append(method.getReturnType().toString() + " " + method.getName());
            buffer.append("(");

            Iterator typeIt = method.getParameterTypes().iterator();

            if(typeIt.hasNext())
            {
                buffer.append(typeIt.next());

                while(typeIt.hasNext())
                {
                    buffer.append(", ");
                    buffer.append(typeIt.next());
                }
            }

            buffer.append(")");

            out.print("    " + buffer.toString());
        }

        out.println();
        out.println("    {");

        /*
        // Print out local variables
        {
            Local[] locals = getLocals();

            for(int j = 0; j < locals.length; j++)
                out.println("        " + locals[j].getType().toString() + " " +
                    locals[j].getName());
        }

        */

        // Print out local variables
        {
            Map typeToLocalSet = new HashMap(this.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = this.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    Set localSet;

                    if(typeToLocalSet.containsKey(local.getType().toString()))
                        localSet = (Set) typeToLocalSet.get(local.getType().toString());
                    else
                    {
                        localSet = new HashSet();
                        typeToLocalSet.put(local.getType().toString(), localSet);
                    }

                    localSet.add(local);
                }
            }

            // Print locals
            {
                Set typeSet = typeToLocalSet.keySet();

                Object[] types = typeSet.toArray();

                for(int j = 0; j < types.length; j++)
                {
                    String type = (String) types[j];

                    Set localSet = (Set) typeToLocalSet.get(type);
                    Object[] locals = localSet.toArray();

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


            if(!typeToLocalSet.isEmpty())
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
                printStmtPrecisely(s, stmtToName, "        ", out);
            else
                printStmtBriefly(s, stmtToName, "        ", out);

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

                out.println("        .catch " + trap.getException().getName() + " from " +
                    stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
                    " with " + stmtToName.get(trap.getHandlerUnit()));
            }
        }
    }

    void print_debug(java.io.PrintWriter out)
    {
        StmtList stmtList = this.getStmtList();

        Map stmtToName = new HashMap(stmtList.size() * 2 + 1, 0.7f);

        StmtGraph stmtGraph = new BriefStmtGraph(stmtList);

        /*
        System.out.println("Constructing LocalDefs of " + this.getMethod().getName() + "...");

        LocalDefs localDefs = new LocalDefs(graphBody);

        System.out.println("Constructing LocalUses of " + getName() + "...");

        LocalUses localUses = new LocalUses(stmtGraph, localDefs);

        LocalCopies localCopies = new LocalCopies(stmtGraph);

        System.out.println("Constructing LiveLocals of " + this.getMethod().getName() + " ...");
        LiveLocals liveLocals = new LiveLocals(stmtGraph);
        */

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

        for(int j = 0; j < stmtList.size(); j++)
        {
            Stmt s = ((Stmt) stmtList.get(j));

            out.print("    " + stmtToName.get(s) + ": ");

            printStmtPrecisely(s, stmtToName, "        ", out);
            out.print(";");
        /*

            // Print info about live locals
            {
                Iterator localIt = liveLocals.getLiveLocalsAfter(s).iterator();

                out.print("   [");

                while(localIt.hasNext())
                {
                    out.print(localIt.next());

                    if(localIt.hasNext())
                        out.print(", ");

                }

                out.print("]");
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
            } */
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
            Iterator trapIt = this.getTraps().iterator();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                out.println(".catch " + trap.getException().getName() + " from " +
                    stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
                    " with " + stmtToName.get(trap.getHandlerUnit()));
            }
        }
    }


    void printStmtPrecisely(Stmt stmt, final Map stmtToName, final String indentation,
        final PrintWriter out)
    {
        out.print(indentation);

        stmt.apply(new AbstractStmtSwitch()
        {
            public void caseAssignStmt(AssignStmt s)
            {
                printValuePrecisely(s.getLeftOp(), out);
                out.print(" = ");
                printValuePrecisely(s.getRightOp(), out);
            }

            public void caseIdentityStmt(IdentityStmt s)
            {
                printValuePrecisely(s.getLeftOp(), out);
                out.print(" := ");
                printValuePrecisely(s.getRightOp(), out);
            }

            public void caseBreakpointStmt(BreakpointStmt s)
            {
                out.print("breakpoint");
            }

            public void caseInvokeStmt(InvokeStmt s)
            {
                printValuePrecisely(s.getInvokeExpr(), out);
            }

            public void defaultCase(Stmt s)
            {
                throw new RuntimeException("unhandled stmt!");
            }

            public void caseEnterMonitorStmt(EnterMonitorStmt s)
            {
                out.print("entermonitor ");
                printValuePrecisely(s.getOp(), out);
            }

            public void caseExitMonitorStmt(ExitMonitorStmt s)
            {
                out.print("exitmonitor ");
                printValuePrecisely(s.getOp(), out);
            }

            public void caseGotoStmt(GotoStmt s)
            {
                out.print("goto " + (String) stmtToName.get(s.getTarget()));
            }


            public void caseIfStmt(IfStmt s)
            {
                out.print("if ");
                printValuePrecisely(s.getCondition(), out);
                out.print(" goto " + (String) stmtToName.get(s.getTarget()));
            }

            public void caseLookupSwitchStmt(LookupSwitchStmt s)
            {
                out.print("lookupswitch(");
                printValuePrecisely(s.getKey(), out);
                out.println(")");
                out.println(indentation + "{");

                List lookupValues = s.getLookupValues();

                for(int i = 0; i < s.getTargetCount(); i++)
                {
                    out.println(indentation + "    case " + lookupValues.get(i) + ": goto " +
                        (String) stmtToName.get(s.getTarget(i)) + ";");
                }

                out.println(indentation + "    default: goto " +
                    (String) stmtToName.get(s.getDefaultTarget()) + ";");
                out.print(indentation + "}");
            }

            public void caseNopStmt(NopStmt s)
            {
                out.print("nop");
            }

            public void caseRetStmt(RetStmt s)
            {
                out.print("ret ");
                printValuePrecisely(s.getStmtAddress(), out);
            }

            public void caseReturnStmt(ReturnStmt s)
            {
                out.print("return ");
                printValuePrecisely(s.getReturnValue(), out);
            }

            public void caseReturnVoidStmt(ReturnVoidStmt s)
            {
                out.print("return");
            }

            public void caseTableSwitchStmt(TableSwitchStmt s)
            {
                out.print("tableswitch(");
                printValuePrecisely(s.getKey(), out);
                out.println(")");
                out.println(indentation + "{");

                int lowIndex = s.getLowIndex(),
                    highIndex = s.getHighIndex();

                for(int i = lowIndex; i <= highIndex; i++)
                {
                    out.println(indentation + "    case " + i + ": goto " +
                        (String) stmtToName.get(s.getTarget(i - lowIndex)) + ";");
                }

                out.println(indentation + "    default: goto " +
                    (String) stmtToName.get(s.getDefaultTarget()) + ";");
                out.print(indentation + "}");
            }

            public void caseThrowStmt(ThrowStmt s)
            {
                out.print("throw ");
                printValuePrecisely(s.getOp(), out);
            }
        });
    }

    void printValuePrecisely(Value value, final PrintWriter out)
    {
        value.apply(new AbstractJimpleValueSwitch()
        {
            public void caseAddExpr(AddExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" + ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseAndExpr(AndExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" & ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseArrayRef(ArrayRef v)
            {
                printValuePrecisely(v.getBase(), out);
                out.print("[");
                printValuePrecisely(v.getIndex(), out);
                out.print("]");
            }

            public void caseCastExpr(CastExpr v)
            {
                out.print("(" + v.getCastType().toString() +") ");
                printValuePrecisely(v.getOp(), out);
            }

            public void caseCmpExpr(CmpExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" cmp ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseCmpgExpr(CmpgExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" cmpg ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseCmplExpr(CmplExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" cmpl ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void defaultCase(Value v)
            {
                throw new RuntimeException("unhandled value case!");
            }

            public void caseDivExpr(DivExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" / ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseDoubleConstant(DoubleConstant v)
            {
                out.print(v.toString());
            }

            public void caseFloatConstant(FloatConstant v)
            {
                out.print(v.toString());
            }


            public void caseInstanceFieldRef(InstanceFieldRef v)
            {
                printValuePrecisely(v.getBase(), out);
                out.print(".[" + v.getField().getSignature() + "]");
            }

            public void caseInstanceOfExpr(InstanceOfExpr v)
            {
                printValuePrecisely(v.getOp(), out);
                out.print(" instanceof " + v.getCheckType().toString());
            }

            public void caseIntConstant(IntConstant v)
            {
                out.print(v.toString());
            }

            public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v)
            {
                out.print("interfaceinvoke ");
                printValuePrecisely(v.getBase(), out);
                out.print(".[" + v.getMethod().getSignature() + "](");

                for(int i = 0; i < v.getArgCount(); i++)
                {
                    if(i != 0)
                        out.print(", ");

                    printValuePrecisely(v.getArg(i), out);
                }

                out.print(")");
            }

            public void caseLengthExpr(LengthExpr v)
            {
                out.print("lengthof ");
                printValuePrecisely(v.getOp(), out);
            }

            public void caseLocal(Local v)
            {
                out.print(v.getName());
            }

            public void caseLongConstant(LongConstant v)
            {
                out.print(v.toString());
            }

            public void caseMulExpr(MulExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" * ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseLtExpr(LtExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" < ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseLeExpr(LeExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" <= ");
                printValuePrecisely(v.getOp2(), out);

            }

            public void caseGtExpr(GtExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" > ");
                printValuePrecisely(v.getOp2(), out);

            }

            public void caseGeExpr(GeExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" >= ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseNeExpr(NeExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" != ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseEqExpr(EqExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" == ");
                printValuePrecisely(v.getOp2(), out);

            }

            public void caseNegExpr(NegExpr v)
            {
                out.print("-");
                printValuePrecisely(v.getOp(), out);
            }

            public void caseNewArrayExpr(NewArrayExpr v)
            {
                out.print("new " + v.getBaseType().toString() + "[");
                printValuePrecisely(v.getSize(), out);
                out.print("]");
            }

            public void caseNewMultiArrayExpr(NewMultiArrayExpr v)
            {
                out.print("newmulti " + v.getBaseType().baseType.toString());

                for(int i = 0; i < v.getSizeCount(); i++)
                {
                    out.print("[");
                    printValuePrecisely(v.getSize(i), out);
                    out.print("]");
                }

                for(int i = 0; i < v.getBaseType().numDimensions - v.getSizeCount(); i++)
                    out.print("[]");
            }

            public void caseNewExpr(NewExpr v)
            {
                out.print("new " + v.getBaseType().toString());
            }

            public void caseNullConstant(NullConstant v)
            {
                out.print(v.toString());
            }

            public void caseOrExpr(OrExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" | ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseRemExpr(RemExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" % ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseShlExpr(ShlExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" << ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseShrExpr(ShrExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" >> ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseSpecialInvokeExpr(SpecialInvokeExpr v)
            {
                out.print("specialinvoke ");
                printValuePrecisely(v.getBase(), out);
                out.print(".[" + v.getMethod().getSignature() + "](");

                for(int i = 0; i < v.getArgCount(); i++)
                {
                    if(i != 0)
                        out.print(", ");

                    printValuePrecisely(v.getArg(i), out);
                }

                out.print(")");
            }

            public void caseStaticInvokeExpr(StaticInvokeExpr v)
            {
                out.print("staticinvoke ");
                out.print("[" + v.getMethod().getSignature() + "](");

                for(int i = 0; i < v.getArgCount(); i++)
                {
                    if(i != 0)
                        out.print(", ");

                    printValuePrecisely(v.getArg(i), out);
                }

                out.print(")");
            }

            public void caseStaticFieldRef(StaticFieldRef v)
            {
                out.print("[" + v.getField().getSignature() + "]");
            }

            public void caseStringConstant(StringConstant v)
            {
                out.print(v.toString());
            }

            public void caseSubExpr(SubExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" - ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseUshrExpr(UshrExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" ushr ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseVirtualInvokeExpr(VirtualInvokeExpr v)
            {
                out.print("virtualinvoke ");
                printValuePrecisely(v.getBase(), out);
                out.print(".[" + v.getMethod().getSignature() + "](");

                for(int i = 0; i < v.getArgCount(); i++)
                {
                    if(i != 0)
                        out.print(", ");

                    printValuePrecisely(v.getArg(i), out);
                }

                out.print(")");
            }

            public void caseXorExpr(XorExpr v)
            {
                printValuePrecisely(v.getOp1(), out);
                out.print(" xor ");
                printValuePrecisely(v.getOp2(), out);
            }

            public void caseParameterRef(ParameterRef v)
            {
                out.print(v.toString());
            }

            public void caseNextNextStmtRef(NextNextStmtRef v)
            {
                out.print(v.toString());
            }

            public void caseCaughtExceptionRef(CaughtExceptionRef v)
            {
                out.print(v.toString());
            }

            public void caseThisRef(ThisRef v)
            {
                out.print(v.toString());
            }

        });
    }


    void printStmtBriefly(Stmt stmt, final Map stmtToName, final String indentation,
        final PrintWriter out)
    {
        out.print(indentation);

        stmt.apply(new AbstractStmtSwitch()
        {
            public void caseAssignStmt(AssignStmt s)
            {
                printValueBriefly(s.getLeftOp(), out);
                out.print(" = ");
                printValueBriefly(s.getRightOp(), out);
            }

            public void caseIdentityStmt(IdentityStmt s)
            {
                printValueBriefly(s.getLeftOp(), out);
                out.print(" := ");
                printValueBriefly(s.getRightOp(), out);
            }

            public void caseBreakpointStmt(BreakpointStmt s)
            {
                out.print("breakpoint");
            }

            public void caseInvokeStmt(InvokeStmt s)
            {
                printValueBriefly(s.getInvokeExpr(), out);
            }

            public void defaultCase(Stmt s)
            {
                throw new RuntimeException("unhandled stmt!");
            }

            public void caseEnterMonitorStmt(EnterMonitorStmt s)
            {
                out.print("entermonitor ");
                printValueBriefly(s.getOp(), out);
            }

            public void caseExitMonitorStmt(ExitMonitorStmt s)
            {
                out.print("exitmonitor ");
                printValueBriefly(s.getOp(), out);
            }

            public void caseGotoStmt(GotoStmt s)
            {
                out.print("goto " + (String) stmtToName.get(s.getTarget()));
            }


            public void caseIfStmt(IfStmt s)
            {
                out.print("if ");
                printValueBriefly(s.getCondition(), out);
                out.print(" goto " + (String) stmtToName.get(s.getTarget()));
            }

            public void caseLookupSwitchStmt(LookupSwitchStmt s)
            {
                out.print("lookupswitch(");
                printValueBriefly(s.getKey(), out);
                out.println(")");
                out.println(indentation + "{");

                List lookupValues = s.getLookupValues();

                for(int i = 0; i < s.getTargetCount(); i++)
                {
                    out.println(indentation + "    case " + lookupValues.get(i) + ": goto " +
                        (String) stmtToName.get(s.getTarget(i)) + ";");
                }

                out.println(indentation + "    default: goto " +
                    (String) stmtToName.get(s.getDefaultTarget()) + ";");
                out.print(indentation + "}");
            }

            public void caseNopStmt(NopStmt s)
            {
                out.print("nop");
            }

            public void caseRetStmt(RetStmt s)
            {
                out.print("ret ");
                printValueBriefly(s.getStmtAddress(), out);
            }

            public void caseReturnStmt(ReturnStmt s)
            {
                out.print("return ");
                printValueBriefly(s.getReturnValue(), out);
            }

            public void caseReturnVoidStmt(ReturnVoidStmt s)
            {
                out.print("return");
            }

            public void caseTableSwitchStmt(TableSwitchStmt s)
            {
                out.print("tableswitch(");
                printValueBriefly(s.getKey(), out);
                out.println(")");
                out.println(indentation + "{");

                int lowIndex = s.getLowIndex(),
                    highIndex = s.getHighIndex();

                for(int i = lowIndex; i <= highIndex; i++)
                {
                    out.println(indentation + "    case " + i + ": goto " +
                        (String) stmtToName.get(s.getTarget(i - lowIndex)) + ";");
                }

                out.println(indentation + "    default: goto " +
                    (String) stmtToName.get(s.getDefaultTarget()) + ";");
                out.print(indentation + "}");
            }

            public void caseThrowStmt(ThrowStmt s)
            {
                out.print("throw ");
                printValueBriefly(s.getOp(), out);
            }
        });
    }

    void printValueBriefly(Value value, final PrintWriter out)
    {
        value.apply(new AbstractJimpleValueSwitch()
        {
            public void caseAddExpr(AddExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" + ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseAndExpr(AndExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" & ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseArrayRef(ArrayRef v)
            {
                printValueBriefly(v.getBase(), out);
                out.print("[");
                printValueBriefly(v.getIndex(), out);
                out.print("]");
            }

            public void caseCastExpr(CastExpr v)
            {
                out.print("(" + v.getCastType().toString() +") ");
                printValueBriefly(v.getOp(), out);
            }

            public void caseCmpExpr(CmpExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" cmp ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseCmpgExpr(CmpgExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" cmpg ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseCmplExpr(CmplExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" cmpl ");
                printValueBriefly(v.getOp2(), out);
            }

            public void defaultCase(Value v)
            {
                throw new RuntimeException("unhandled value case!");
            }

            public void caseDivExpr(DivExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" / ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseDoubleConstant(DoubleConstant v)
            {
                out.print(v.toString());
            }

            public void caseFloatConstant(FloatConstant v)
            {
                out.print(v.toString());
            }


            public void caseInstanceFieldRef(InstanceFieldRef v)
            {
                printValueBriefly(v.getBase(), out);
                out.print("." + v.getField().getName() + "");
            }

            public void caseInstanceOfExpr(InstanceOfExpr v)
            {
                printValueBriefly(v.getOp(), out);
                out.print(" instanceof " + v.getCheckType().toString());
            }

            public void caseIntConstant(IntConstant v)
            {
                out.print(v.toString());
            }

            public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v)
            {
                printValueBriefly(v.getBase(), out);
                out.print("." + v.getMethod().getName() +"(");

                for(int i = 0; i < v.getArgCount(); i++)
                {
                    if(i != 0)
                        out.print(", ");

                    printValueBriefly(v.getArg(i), out);
                }

                out.print(")");
            }

            public void caseLengthExpr(LengthExpr v)
            {
                out.print("lengthof ");
                printValueBriefly(v.getOp(), out);
            }

            public void caseLocal(Local v)
            {
                out.print(v.getName());
            }

            public void caseLongConstant(LongConstant v)
            {
                out.print(v.toString());
            }

            public void caseMulExpr(MulExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" * ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseLtExpr(LtExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" < ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseLeExpr(LeExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" <= ");
                printValueBriefly(v.getOp2(), out);

            }

            public void caseGtExpr(GtExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" > ");
                printValueBriefly(v.getOp2(), out);

            }

            public void caseGeExpr(GeExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" >= ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseNeExpr(NeExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" != ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseEqExpr(EqExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" == ");
                printValueBriefly(v.getOp2(), out);

            }

            public void caseNegExpr(NegExpr v)
            {
                out.print("-");
                printValueBriefly(v.getOp(), out);
            }

            public void caseNewArrayExpr(NewArrayExpr v)
            {
                out.print("new " + v.getBaseType().toString() + "[");
                printValueBriefly(v.getSize(), out);
                out.print("]");
            }

            public void caseNewMultiArrayExpr(NewMultiArrayExpr v)
            {
                out.print("newmulti " + v.getBaseType().baseType.toString());

                for(int i = 0; i < v.getSizeCount(); i++)
                {
                    out.print("[");
                    printValueBriefly(v.getSize(i), out);
                    out.print("]");
                }

                for(int i = 0; i < v.getBaseType().numDimensions - v.getSizeCount(); i++)
                    out.print("[]");
            }

            public void caseNewExpr(NewExpr v)
            {
                out.print("new " + v.getBaseType().toString());
            }

            public void caseNullConstant(NullConstant v)
            {
                out.print(v.toString());
            }

            public void caseOrExpr(OrExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" | ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseRemExpr(RemExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" % ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseShlExpr(ShlExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" << ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseShrExpr(ShrExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" >> ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseSpecialInvokeExpr(SpecialInvokeExpr v)
            {
                out.print("specialinvoke ");
                printValueBriefly(v.getBase(), out);
                out.print("." + v.getMethod().getName() + "(");

                for(int i = 0; i < v.getArgCount(); i++)
                {
                    if(i != 0)
                        out.print(", ");

                    printValueBriefly(v.getArg(i), out);
                }

                out.print(")");
            }

            public void caseStaticInvokeExpr(StaticInvokeExpr v)
            {
                out.print(v.getMethod().getDeclaringClass().getName() + "." +
                    v.getMethod().getName() + "(");

                for(int i = 0; i < v.getArgCount(); i++)
                {
                    if(i != 0)
                        out.print(", ");

                    printValueBriefly(v.getArg(i), out);
                }

                out.print(")");
            }

            public void caseStaticFieldRef(StaticFieldRef v)
            {
                out.print(v.getField().getDeclaringClass().getName() + "." + v.getField().getName());
            }

            public void caseStringConstant(StringConstant v)
            {
                out.print(v.toString());
            }

            public void caseSubExpr(SubExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" - ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseUshrExpr(UshrExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" ushr ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseVirtualInvokeExpr(VirtualInvokeExpr v)
            {
                printValueBriefly(v.getBase(), out);
                out.print("." + v.getMethod().getName() + "(");

                for(int i = 0; i < v.getArgCount(); i++)
                {
                    if(i != 0)
                        out.print(", ");

                    printValueBriefly(v.getArg(i), out);
                }

                out.print(")");
            }

            public void caseXorExpr(XorExpr v)
            {
                printValueBriefly(v.getOp1(), out);
                out.print(" xor ");
                printValueBriefly(v.getOp2(), out);
            }

            public void caseParameterRef(ParameterRef v)
            {
                out.print(v.toString());
            }

            public void caseNextNextStmtRef(NextNextStmtRef v)
            {
                out.print(v.toString());
            }

            public void caseCaughtExceptionRef(CaughtExceptionRef v)
            {
                out.print(v.toString());
            }

            public void caseThisRef(ThisRef v)
            {
                out.print(v.toString());
            }

        });
    }

}

