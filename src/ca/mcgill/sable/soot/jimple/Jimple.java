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
 The reference version is: $JimpleVersion: 0.5 $

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

 - Modified on September 12, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Changed PrintStream to PrintWriter.

 - Modified on 31-Aug-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Minor print changes.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Changed Hashtable to HashMap.
   
 - Modified on July 5, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Changed caseDefault to defaultCase, to avoid name conflicts (and conform
   to the standard).

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.io.*;

/**
    The Jimple class contains all the constructors for the components of the Jimple
    grammar for the Jimple body.
    
    Immediate -> Local | Constant <br>
    RValue -> Local | Constant | ConcreteRef | Expr<br>
    Variable -> Local | ArrayRef | InstanceFieldRef | StaticFieldRef <br>
 */
 

public class Jimple implements BodyRepresentation
{
    private static Jimple jimpleRepresentation = new Jimple();
        
    private Jimple()
    {
    }
    
    public static Jimple v()
    {
        return jimpleRepresentation;
    }
    
    public Body getBodyOf(SootMethod m)
    {
        return new JimpleBody(m);
   
    }
        
    public static void setVerbose(boolean flag)
    {
        Main.isVerbose = flag;
    }
    
    /**
     * Not guaranteed to stay in the API.  Use at your own risk!
     */
    
    public static void setLocalPacking(boolean flag)
    {
        Main.noLocalPacking = !flag;
    }
    
    static void printJimpleBody(JimpleBody stmtBody, java.io.PrintWriter out, boolean isPrecise)
    {
        StmtList stmtList = stmtBody.getStmtList();
        
        Map stmtToName = new HashMap(stmtList.size() * 2 + 1, 0.7f);
        StmtGraph stmtGraph = new BriefStmtGraph(stmtList);
        
        // Create statement name table
        {
            Iterator boxIt = stmtBody.getUnitBoxes().iterator();
            
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
            Iterator trapIt = stmtBody.getTraps().iterator();
            
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

    static void printJimpleBody_debug(JimpleBody stmtBody, java.io.PrintWriter out)
    {
        StmtList stmtList = stmtBody.getStmtList();
        
        Map stmtToName = new HashMap(stmtList.size() * 2 + 1, 0.7f);
        
        StmtGraph stmtGraph = new BriefStmtGraph(stmtList);
        
        /*        
        System.out.println("Constructing LocalDefs of " + stmtBody.getMethod().getName() + "...");
        
        LocalDefs localDefs = new LocalDefs(graphBody);

        System.out.println("Constructing LocalUses of " + getName() + "...");
        
        LocalUses localUses = new LocalUses(stmtGraph, localDefs);       
        
        LocalCopies localCopies = new LocalCopies(stmtGraph);
                
        System.out.println("Constructing LiveLocals of " + stmtBody.getMethod().getName() + " ...");
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
    
     static void printMethodBody(SootMethod method, java.io.PrintWriter out, boolean isPrecise)
    {         
        //System.out.println("Constructing the graph of " + getName() + "...");
        JimpleBody stmtBody = (JimpleBody) method.getBody(Jimple.v());
        StmtList stmtList = stmtBody.getStmtList();
        
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
            Map typeToLocalSet = new HashMap(stmtBody.getLocalCount() * 2 + 1, 0.7f);
            
            // Collect locals
            {
                Iterator localIt = stmtBody.getLocals().iterator();
            
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
            printJimpleBody(stmtBody, out, isPrecise);
                                                
        out.println("    }");
    }

     static void printMethodSignature(SootMethod method, java.io.PrintWriter out)
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

        // Print exceptions
        {
            Iterator exceptionIt = method.getExceptions().iterator();

            if(exceptionIt.hasNext())
            {
                buffer.append(" throws ");
                buffer.append(((SootClass) exceptionIt.next()).getName());  
                
                while(exceptionIt.hasNext())
                {
                    buffer.append(", ");
                    buffer.append(((SootClass) exceptionIt.next()).getName());  
                }
            }
            
        }        
        
        out.print(buffer.toString());
    }

    /**
     * @deprecated
     * Use printClass instead.
     */

    public static void printSootClass(SootClass bclass, PrintWriter out, boolean isPrecise)
    {   
        printClass(bclass, out, isPrecise);
    }    
    
    public static void printClass(SootClass bclass, PrintWriter out, boolean isPrecise)
    {   
        // Print class name + modifiers
        {
            String classPrefix = "";
            
            classPrefix = classPrefix + " " + Modifier.toString(bclass.getModifiers());
            classPrefix = classPrefix.trim();            
            
            if(!Modifier.isInterface(bclass.getModifiers()))
            {
                classPrefix = classPrefix + " class";
                classPrefix = classPrefix.trim();
            }   
        
            out.print(classPrefix + " " + bclass.getName());
        }
        
        // Print extension
        {
            if(bclass.hasSuperClass())
                out.print(" extends " + bclass.getSuperClass().getName());
        }
        
        // Print interfaces
        {
            Iterator interfaceIt = bclass.getInterfaces().iterator();
            
            if(interfaceIt.hasNext())
            {
                out.print(" implements ");
            
                out.print(((SootClass) interfaceIt.next()).getName());
                    
                while(interfaceIt.hasNext())
                {
                    out.print(",");           
                    out.print(" " + ((SootClass) interfaceIt.next()).getName());
                }
            }
        }
        
        out.println();
        out.println("{");
        
        // Print fields
        {
            Iterator fieldIt = bclass.getFields().iterator();
            
            if(fieldIt.hasNext())
            {
                while(fieldIt.hasNext())
                    out.println("    " + ((SootField) fieldIt.next()).toString() + ";");
            }
        }

        // Print methods
        {
            Iterator methodIt = bclass.getMethods().iterator();
            
            if(methodIt.hasNext())
            { 
                if(bclass.getMethods().size() != 0)
                    out.println();

                while(methodIt.hasNext())
                {
                    SootMethod method = (SootMethod) methodIt.next();
                    
                    if(!Modifier.isAbstract(method.getModifiers()) && 
                        !Modifier.isNative(method.getModifiers()))
                    {
                        printMethodBody(method, out, isPrecise);
                        
                        if(methodIt.hasNext())
                            out.println();
                    }
                    else {
                        out.print("    ");
                        printMethodSignature(method, out);
                        out.println(";");
                        
                        if(methodIt.hasNext())
                            out.println();
                    }
                }                    
            }    
        }        
        out.println("}");
        
    }    
    
    static void printStmtPrecisely(Stmt stmt, final Map stmtToName, final String indentation, 
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
    
    static void printValuePrecisely(Value value, final PrintWriter out)
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

        
    static void printStmtBriefly(Stmt stmt, final Map stmtToName, final String indentation, 
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
    
    static void printValueBriefly(Value value, final PrintWriter out)
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

    /**
        Constructs a XorExpr(Immediate, Immediate) grammar chunk.
     */
     
    public XorExpr newXorExpr(Value op1, Value op2)
    {
        return new XorExpr(op1, op2);
    }
    
    
    /**
        Constructs a UshrExpr(Immediate, Immediate) grammar chunk.
     */
     
    public UshrExpr newUshrExpr(Value op1, Value op2)
    {
        return new UshrExpr(op1, op2);
    }
     
    
    /**
        Constructs a SubExpr(Immediate, Immediate) grammar chunk.
     */
     
    public SubExpr newSubExpr(Value op1, Value op2)
    {
        return new SubExpr(op1, op2);
    }
    
    
    /**
        Constructs a ShrExpr(Immediate, Immediate) grammar chunk.
     */
     
    public ShrExpr newShrExpr(Value op1, Value op2)
    {
        return new ShrExpr(op1, op2);
    }
    
    
    /**
        Constructs a ShlExpr(Immediate, Immediate) grammar chunk.
     */
     
    public ShlExpr newShlExpr(Value op1, Value op2)
    {
        return new ShlExpr(op1, op2);
    }
    
    
    /**
        Constructs a RemExpr(Immediate, Immediate) grammar chunk.
     */
     
    public RemExpr newRemExpr(Value op1, Value op2)
    {
        return new RemExpr(op1, op2);
    }
    
    
    /**
        Constructs a OrExpr(Immediate, Immediate) grammar chunk.
     */
     
    public OrExpr newOrExpr(Value op1, Value op2)
    {
        return new OrExpr(op1, op2);
    }
    
    
    /**
        Constructs a NeExpr(Immediate, Immediate) grammar chunk.
     */
     
    public NeExpr newNeExpr(Value op1, Value op2)
    {
        return new NeExpr(op1, op2);
    }
    
    
    /**
        Constructs a MulExpr(Immediate, Immediate) grammar chunk.
     */
     
    public MulExpr newMulExpr(Value op1, Value op2)
    {
        return new MulExpr(op1, op2);
    }
    
    
    /**
        Constructs a LeExpr(Immediate, Immediate) grammar chunk.
     */
     
    public LeExpr newLeExpr(Value op1, Value op2)
    {
        return new LeExpr(op1, op2);
    }
    
    
    /**
        Constructs a GeExpr(Immediate, Immediate) grammar chunk.
     */
     
    public GeExpr newGeExpr(Value op1, Value op2)
    {
        return new GeExpr(op1, op2);
    }
    
    
    /**
        Constructs a EqExpr(Immediate, Immediate) grammar chunk.
     */
     
    public EqExpr newEqExpr(Value op1, Value op2)
    {
        return new EqExpr(op1, op2);
    }
    
    /**
        Constructs a DivExpr(Immediate, Immediate) grammar chunk.
     */
     
    public DivExpr newDivExpr(Value op1, Value op2)
    {
        return new DivExpr(op1, op2);
    }
    
    
    /**
        Constructs a CmplExpr(Immediate, Immediate) grammar chunk.
     */
     
    public CmplExpr newCmplExpr(Value op1, Value op2)
    {
        return new CmplExpr(op1, op2);
    }
    
    
    /**
        Constructs a CmpgExpr(Immediate, Immediate) grammar chunk.
     */
     
    public CmpgExpr newCmpgExpr(Value op1, Value op2)
    {
        return new CmpgExpr(op1, op2);
    }
    
    
    /**
        Constructs a CmpExpr(Immediate, Immediate) grammar chunk.
     */
     
    public CmpExpr newCmpExpr(Value op1, Value op2)
    {
        return new CmpExpr(op1, op2);
    }
    
        
    /**
        Constructs a GtExpr(Immediate, Immediate) grammar chunk.
     */
     
    public GtExpr newGtExpr(Value op1, Value op2)
    {
        return new GtExpr(op1, op2);
    }
    
    
    /**
        Constructs a LtExpr(Immediate, Immediate) grammar chunk.
     */
     
    public LtExpr newLtExpr(Value op1, Value op2)
    {
        return new LtExpr(op1, op2);
    }
    
    /**
        Constructs a AddExpr(Immediate, Immediate) grammar chunk.
     */
     
    public AddExpr newAddExpr(Value op1, Value op2)
    {
        return new AddExpr(op1, op2);
    }
    
    
    /**
        Constructs a AndExpr(Immediate, Immediate) grammar chunk.
     */
     
    public AndExpr newAndExpr(Value op1, Value op2)
    {
        return new AndExpr(op1, op2);
    }
    
    
    /**
        Constructs a NegExpr(Immediate, Immediate) grammar chunk.
     */
     
    public NegExpr newNegExpr(Value op)
    {
        return new NegExpr(op);
    }
    
    
    /**
        Constructs a LengthExpr(Immediate) grammar chunk.
     */
     
    public LengthExpr newLengthExpr(Value op)
    {
        return new LengthExpr(op);
    }
    
    
    /**
        Constructs a CastExpr(Immediate, Type) grammar chunk.
     */
     
    public CastExpr newCastExpr(Value op1, Type t)
    {
        return new CastExpr(op1, t);
    }
    
    /**
        Constructs a InstanceOfExpr(Immediate, Type) 
        grammar chunk.
     */
     
    public InstanceOfExpr newInstanceOfExpr(Value op1, Type t)
    {
        return new InstanceOfExpr(op1, t);
    }
    
    
    /**
        Constructs a NewExpr(RefType) grammar chunk.
     */
     
    public NewExpr newNewExpr(RefType type)
    {
        return new NewExpr(type);
    }

    
    /**
        Constructs a NewArrayExpr(Type, Immediate) grammar chunk.
     */
     
    public NewArrayExpr newNewArrayExpr(Type type, Value size)
    {
        return new NewArrayExpr(type, size);
    }

    /**
        Constructs a NewMultiArrayExpr(ArrayType, List of Immediate) grammar chunk.
     */
     
    public NewMultiArrayExpr newNewMultiArrayExpr(ArrayType type, List sizes)
    {
        return new NewMultiArrayExpr(type, sizes);
    }

    
    /**
        Constructs a NewStaticInvokeExpr(ArrayType, List of Immediate) grammar chunk.
     */
     
    public StaticInvokeExpr newStaticInvokeExpr(SootMethod method, List args)
    {
        return new StaticInvokeExpr(method, args);
    }


    /**
        Constructs a NewSpecialInvokeExpr(Local base, SootMethod method, List of Immediate) grammar chunk.
     */
     
    public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethod method, List args)
    {
        return new SpecialInvokeExpr(base, method, args);
    }

    
    /**
        Constructs a NewVirtualInvokeExpr(Local base, SootMethod method, List of Immediate) grammar chunk.
     */
     
    public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethod method, List args)
    {
        return new VirtualInvokeExpr(base, method, args);
    }

    
    /**
        Constructs a NewInterfaceInvokeExpr(Local base, SootMethod method, List of Immediate) grammar chunk.
     */
     
    public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethod method, List args)
    {
        return new InterfaceInvokeExpr(base, method, args);
    }

    
    /**
        Constructs a ThrowStmt(Immediate) grammar chunk.
     */
        
    public ThrowStmt newThrowStmt(Value op)
    {
        return new ThrowStmt(op);
    }
    
    
    /**
        Constructs a ExitMonitorStmt(Immediate) grammar chunk
     */
        
    public ExitMonitorStmt newExitMonitorStmt(Value op)
    {
        return new ExitMonitorStmt(op);
    }
    
            
    /**
        Constructs a EnterMonitorStmt(Immediate) grammar chunk.
     */
        
    public EnterMonitorStmt newEnterMonitorStmt(Value op)
    {
        return new EnterMonitorStmt(op);
    }
    
    
    /**
        Constructs a BreakpointStmt() grammar chunk.
     */
        
    public BreakpointStmt newBreakpointStmt()
    {
        return new BreakpointStmt();
    }

    
    /**
        Constructs a GotoStmt(Stmt) grammar chunk.
     */
        
    public GotoStmt newGotoStmt(Unit target)
    {
        return new GotoStmt(target);
    }

    
    /**
        Constructs a NopStmt() grammar chunk.
     */
        
    public NopStmt newNopStmt()
    {
        return new NopStmt();
    }

            
    /**
        Constructs a ReturnVoidStmt() grammar chunk.
     */
        
    public ReturnVoidStmt newReturnVoidStmt()
    {
        return new ReturnVoidStmt();
    }


    /**
        Constructs a ReturnStmt(Immediate) grammar chunk.
     */
        
    public ReturnStmt newReturnStmt(Value op)
    {
        return new ReturnStmt(op);
    }
    
    
    /**
        Constructs a RetStmt(Local) grammar chunk.
     */
        
    public RetStmt newRetStmt(Value stmtAddress)
    {
        return new RetStmt(stmtAddress);
    }

    
    /**
        Constructs a IfStmt(Condition, Stmt) grammar chunk.
     */
        
    public IfStmt newIfStmt(Value condition, Unit target)
    {
        return new IfStmt(condition, target);
    }

    
    /**
        Constructs a IdentityStmt(Local, IdentityRef) grammar chunk.
     */
        
    public IdentityStmt newIdentityStmt(Value local, Value identityRef)
    {
        return new IdentityStmt(local, identityRef);
    }

    
    /**
        Constructs a AssignStmt(Variable, RValue) grammar chunk.
     */
        
    public AssignStmt newAssignStmt(Value variable, Value rvalue)
    {
        return new AssignStmt(variable, rvalue);
    }

    
    /**
        Constructs a InvokeStmt(InvokeExpr) grammar chunk.
     */
        
    public InvokeStmt newInvokeStmt(Value op)
    {
        return new InvokeStmt(op);
    }
    
    
    /**
        Constructs a TableSwitchStmt(Immediate, int, int, List of Unit, Stmt) grammar chunk.
     */
        
    public TableSwitchStmt newTableSwitchStmt(Value key, int lowIndex, int highIndex, List targets, Unit defaultTarget)
    {
        return new TableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget);
    }
    
    
    /**
        Constructs a LookupSwitchStmt(Immediate, List of Immediate, List of Unit, Stmt) grammar chunk.
     */
        
    public LookupSwitchStmt newLookupSwitchStmt(Value key, List lookupValues, List targets, Unit defaultTarget)
    {
        return new LookupSwitchStmt(key, lookupValues, targets, defaultTarget);
    }
    
    /** 
        Constructs a Local with the given name and type.  
    */
    
    public Local newLocal(String name, Type t)
    {
        return new Local(name, t);
    } 
    
    /** 
        Constructs a new Trap for the given exception on the given Stmt range with the given Stmt handler.
    */
    
    public Trap newTrap(SootClass exception, Unit beginStmt, Unit endStmt, Unit handlerStmt)
    {
        return new Trap(exception, beginStmt, endStmt, handlerStmt);
    }
    
    
    /**
        Constructs a StaticFieldRef(SootField) grammar chunk.
     */
     
    public StaticFieldRef newStaticFieldRef(SootField f)
    {
        return new StaticFieldRef(f);
    }

    
    /**
        Constructs a ThisRef(SootClass) grammar chunk.
     */
     
    public ThisRef newThisRef(SootClass c)
    {
        return new ThisRef(c);
    }

    
    /**
        Constructs a ParameterRef(SootMethod, int) grammar chunk.
     */
     
    public ParameterRef newParameterRef(SootMethod m, int number)
    {
        return new ParameterRef(m, number);
    }

    
    /**
        Constructs a NextNextStmtRef() grammar chunk.
     */
     
    public NextNextStmtRef newNextNextStmtRef()
    {
        return new NextNextStmtRef();
    }

    
    /**
        Constructs a InstanceFieldRef(Value, SootField) grammar chunk.
     */
     
    public InstanceFieldRef newInstanceFieldRef(Value base, SootField f)
    {
        return new InstanceFieldRef(base, f);
    }

    
    /**
        Constructs a CaughtExceptionRef() grammar chunk.
     */
     
    public CaughtExceptionRef newCaughtExceptionRef()
    {
        return new CaughtExceptionRef();
    }


    /**
        Constructs a ArrayRef(Local, Immediate) grammar chunk.
     */
     
    public ArrayRef newArrayRef(Value base, Value index)
    {
        return new ArrayRef(base, index);
    }
    
    public ValueBox newVariableBox(Value value)
    {
        return new VariableBox(value);
    }

    public ValueBox newLocalBox(Value value)
    {
        return new LocalBox(value);
    }
    
    public ValueBox newRValueBox(Value value)
    {
        return new RValueBox(value);
    }
    
    public ValueBox newImmediateBox(Value value)
    {
        return new ImmediateBox(value);
    }
    
    public ValueBox newIdentityRefBox(Value value)
    {
        return new IdentityRefBox(value);
    }
    
    public ValueBox newConditionExprBox(Value value)
    {
        return new ConditionExprBox(value);
    }
    
    public ValueBox newInvokeExprBox(Value value)
    {
        return new InvokeExprBox(value);
    }

    public UnitBox newStmtBox(Unit unit)
    {
        return new StmtBox((Stmt) unit);
    }    
}



