/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Grimp, an aggregated-expression Java(TM) bytecode representation. *
 * Copyright (C) 1998 Patrick Lam (plam@sable.mcgill.ca)             *
 * All rights reserved.                                              *
 *                                                                   *
 * Portions by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Raja Vallee-Rai (rvalleerai@sable.mcgill.ca).  *
 * All rights reserved.                                              *
 *                                                                   *
 * Portions by Etienne Gagnon (gagnon@sable.mcgill.ca) are           *
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

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca). (*)
   First release of Grimp.
*/

package ca.mcgill.sable.soot.grimp;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
import java.io.*;

public class GrimpBody implements StmtBody
{
    List locals = new ArrayList();
    SootMethod method;

    StmtList stmtList;
    List traps = new ArrayList();

    /**
        Construct an empty GrimpBody 
     **/
     
    public GrimpBody(SootMethod m)
    {
        this.method = m;
        stmtList = new StmtList(this);   
    }

    public GrimpBody(Body body)
    {   
        this(body, 0);
    }
    
    /**
        Constructs a GrimpBody from the given Body.
     */

    public GrimpBody(Body body, int buildOptions)
    {
        SootMethod m = body.getMethod();
        
        JimpleBody jBody = null;

        if(body instanceof ClassFileBody)
            {
                /* we need to resolve conflicts between */
                /* Jimple & Grimp buildOptions! */
                jBody = new JimpleBody(body, buildOptions);
            }
        else if (body instanceof JimpleBody)
            {
                jBody = (JimpleBody)body;
            }
        else
            throw new RuntimeException("Can only construct GrimpBody's from ClassFileBody's or JimpleBody's (for now)");

        this.method = jBody.getMethod();
        this.locals = new LinkedList();
        Iterator it = jBody.getLocals().iterator();
        while (it.hasNext())
            this.locals.add(((Local)(it.next())));
            //            this.locals.add(((Local)(it.next())).clone());

        stmtList = new StmtList(this);
        it = jBody.getStmtList().iterator();

        final HashMap oldToNew = new HashMap(stmtList.size() * 2 + 1, 0.7f);
        LinkedList updates = new LinkedList();

        /* we should Grimpify the Stmt's here... */
        while (it.hasNext())
        {
            Stmt oldStmt = (Stmt)(it.next());
            final StmtBox newStmtBox = (StmtBox) Grimp.v().newStmtBox(null);
            final StmtBox updateStmtBox = (StmtBox) Grimp.v().newStmtBox(null);

            /* we can't have a general StmtSwapper on Grimp.v() */
            /* because we need to collect a list of updates */
            oldStmt.apply(new AbstractStmtSwitch()
            {
                public void caseAssignStmt(AssignStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newAssignStmt(s));
                }
                public void caseIdentityStmt(IdentityStmt s)
                  {
                    newStmtBox.setUnit(Grimp.v().newIdentityStmt(s));
                }
                public void caseBreakpointStmt(BreakpointStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newBreakpointStmt(s));
                }
                public void caseInvokeStmt(InvokeStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newInvokeStmt(s));
                }
                public void defaultCase(Stmt s)
                  {
                    throw new RuntimeException("invalid jimple stmt: "+s);
                }
                public void caseEnterMonitorStmt(EnterMonitorStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newEnterMonitorStmt(s));
                }
                public void caseExitMonitorStmt(ExitMonitorStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newExitMonitorStmt(s));
                }
                public void caseGotoStmt(GotoStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newGotoStmt(s));
                    updateStmtBox.setUnit(s);
                }
                public void caseIfStmt(IfStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newIfStmt(s));
                    updateStmtBox.setUnit(s);
                }
                public void caseLookupSwitchStmt(LookupSwitchStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newLookupSwitchStmt(s));
                    updateStmtBox.setUnit(s);
                }
                public void caseNopStmt(NopStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newNopStmt(s));
                }
                public void caseRetStmt(RetStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newRetStmt(s));
                }
                public void caseReturnStmt(ReturnStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newReturnStmt(s));
                }
                public void caseReturnVoidStmt(ReturnVoidStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newReturnVoidStmt(s));
                }
                public void caseTableSwitchStmt(TableSwitchStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newTableSwitchStmt(s));
                    updateStmtBox.setUnit(s);
                }
                public void caseThrowStmt(ThrowStmt s)
                {
                    newStmtBox.setUnit(Grimp.v().newThrowStmt(s));
                }
            });

            /* map old Expr's to new Expr's. */
            Stmt newStmt = (Stmt)(newStmtBox.getUnit());
            Iterator useBoxesIt = (Iterator)
                newStmt.getUseAndDefBoxes().iterator();
            while(useBoxesIt.hasNext())
                {
                    ValueBox b = (ValueBox) (useBoxesIt.next());
                    b.setValue(Grimp.v().newExpr(b.getValue()));
                }

            stmtList.add(newStmt);
            oldToNew.put(oldStmt, newStmt);
            if (updateStmtBox.getUnit() != null)
                updates.add(updateStmtBox.getUnit());
        }

        /* fixup stmt's which have had moved targets */
        it = updates.iterator();
        while (it.hasNext())
        {
            Stmt stmt = (Stmt)(it.next());

            stmt.apply(new AbstractStmtSwitch()
            {
                public void defaultCase(Stmt s)
                  {
                    throw new RuntimeException("Internal error: "+s);
                }
                public void caseGotoStmt(GotoStmt s)
                {
                    GotoStmt newStmt = (GotoStmt)(oldToNew.get(s));
                    newStmt.setTarget((Stmt)oldToNew.get(newStmt.getTarget()));
                }
                public void caseIfStmt(IfStmt s)
                {
                    IfStmt newStmt = (IfStmt)(oldToNew.get(s));
                    newStmt.setTarget((Stmt)oldToNew.get(newStmt.getTarget()));
                }
                public void caseLookupSwitchStmt(LookupSwitchStmt s)
                {
                    LookupSwitchStmt newStmt = 
                        (LookupSwitchStmt)(oldToNew.get(s));
                    newStmt.setDefaultTarget
                        ((Unit)(oldToNew.get(newStmt.getDefaultTarget())));
                    Unit[] newTargList = new Unit[newStmt.getTargetCount()];
                    for (int i = 0; i < newStmt.getTargetCount(); i++)
                        newTargList[i] = (Unit)(oldToNew.get
                                                (newStmt.getTarget(i)));
                    newStmt.setTargets(newTargList);
                }
                public void caseTableSwitchStmt(TableSwitchStmt s)
                {
                    TableSwitchStmt newStmt = 
                        (TableSwitchStmt)(oldToNew.get(s));
                    newStmt.setDefaultTarget
                        ((Unit)(oldToNew.get(newStmt.getDefaultTarget())));
                    int tc = newStmt.getHighIndex() - newStmt.getLowIndex()+1;
                    LinkedList newTargList = new LinkedList();
                    for (int i = 0; i < tc; i++)
                        newTargList.add(oldToNew.get
                                        (newStmt.getTarget(i)));
                    newStmt.setTargets(newTargList);
                }
            });
        }

        this.traps = new ArrayList();
        it = jBody.getTraps().iterator();
        while (it.hasNext())
        {
            Trap oldTrap = (Trap)(it.next());
            this.traps.add(Grimp.v().newTrap
                           (oldTrap.getException(),
                            (Unit)(oldToNew.get(oldTrap.getBeginUnit())),
                            (Unit)(oldToNew.get(oldTrap.getEndUnit())),
                            (Unit)(oldToNew.get(oldTrap.getHandlerUnit()))));
        }

        if(BuildGrimpBodyOption.aggressiveAggregating(buildOptions))
        {
            Aggregator.aggregate(this);
            GrimpTransformations.foldConstructors(this);
            Aggregator.aggregate(this);   
            Transformations.removeUnusedLocals(this);
        }
        else if (!BuildGrimpBodyOption.noAggregating(buildOptions))
        {
            Aggregator.aggregateStackVariables(this);
            GrimpTransformations.foldConstructors(this);
            Aggregator.aggregateStackVariables(this);   
            Transformations.removeUnusedLocals(this);
        }
    
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

            if(local.getName().equals(name))
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

            if(local.getName().equals(localName))
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

        if(PrintJimpleBodyOption.debugMode(printBodyOptions))
        {
            print_debug(out);
            return;
        }
        
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
            Map typeToLocals = new DeterministicHashMap(this.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = this.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    List localList;
 
                    if(typeToLocals.containsKey(local.getType().toString()))
                        localList = (List) typeToLocals.get(local.getType().toString());
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(local.getType().toString(), localList);
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
/*
        StmtGraph stmtGraph = new BriefStmtGraph(stmtList);
*/
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

            out.print(s.toString(stmtToName, "        "));
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
}
