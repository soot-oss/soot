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
 * Copyright (C) 1999 Patrick Lam (plam@sable.mcgill.ca).  All       *
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

 - Modified on March 23, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Added a correction to the peephole optimizer.
   Fixed a bug with the instanceof code generation.
   Fixed a bug with floating point infinities.
         
 - Modified on March 13, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Re-organized the timers.

 - Modified on March 4, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added peephole optimizations for the code generation of ++ like structures.
      
 - Modified on February 19, 1999 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Uses bipush & sipush instead of ldc
   More efficient branch generation.
   Uses the iinc bytecode instruction to generate more efficient code.
   
 - Modified on February 17, 1999 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Added the emitting of stack height.
   
 - Modified on February 15, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Fixed bug with booleans, chars, bytes and shorts and if_cmpxx.
   Fixed bug with type casts of bytes/chars/booleans/shorts to ints.

 - Modified on November 18, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Fixed a jsr generation bug.
   Fixed generation of 'and's.
   Changed the output of constants.  (uses L and F)
   
 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on October 4, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Method names in the .jasmin format now are output as strings.
   Class which has no superclass should indicate in its .jasmin file
   that it is its own superclass.

 - Modified on October 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Fixed the generation of code for acmp_ifne and acmp_ifeq, aload, astore
    when nulls are involved.

 - Modified on September 25, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Does not output empty exception ranges.  (verifier doesn't like that)
   Fixed the generation of invokeinterface.
   Fixed the generation of array references.
   Fixed the generation of bastores.
   Fixed the generation of castores.

 - Modified on September 22, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Fixed the generation of jsr code.
   Added support for casts.
   Fixed a bug with the return instruction.

 - Modified on September 15, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Implemented the jsr jump. (needs some type checks however)

 - Modified on September 12, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Changed PrintStream to PrintWriter.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Renamed Hashtable to Hashmap, and minor changes.

 - Modified on July 5, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Changed caseDefault to defaultCase, to avoid name conflicts (and conform
   to the standard).

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.internal.*;
import ca.mcgill.sable.soot.toolkit.scalar.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import java.io.*;

public class JasminClass
{
    Map stmtToLabel;
    Map localToSlot;
    Map subroutineToReturnAddressSlot;

    List code;

    boolean isEmittingMethodCode;
    int labelCount;

    boolean isNextGotoAJsr;
    int returnAddressSlot;
    int currentStackHeight = 0;
    int maxStackHeight = 0;

    Map localToGroup;
    Map groupToColorCount;
    Map localToColor; 
            
    String slashify(String s)
    {
        return s.replace('.', '/');
    }

    int sizeOfType(Type t)
    {
        if(t instanceof DoubleType || t instanceof LongType)
            return 2;
        else if(t instanceof VoidType)
            return 0;
        else
            return 1;
    }

    int argCountOf(SootMethod m)
    {
        int argCount = 0;
        Iterator typeIt = m.getParameterTypes().iterator();

        while(typeIt.hasNext())
        {
            Type t = (Type) typeIt.next();

            argCount += sizeOfType(t);
        }

        return argCount;
    }

    String jasminDescriptorOf(Type type)
    {
        TypeSwitch sw;

        type.apply(sw = new TypeSwitch()
        {
            public void caseBooleanType(BooleanType t)
            {
                setResult("Z");
            }

            public void caseByteType(ByteType t)
            {
                setResult("B");
            }

            public void caseCharType(CharType t)
            {
                setResult("C");
            }

            public void caseDoubleType(DoubleType t)
            {
                setResult("D");
            }

            public void caseFloatType(FloatType t)
            {
                setResult("F");
            }

            public void caseIntType(IntType t)
            {
                setResult("I");
            }

            public void caseLongType(LongType t)
            {
                setResult("J");
            }

            public void caseShortType(ShortType t)
            {
                setResult("S");
            }

            public void defaultCase(Type t)
            {
                throw new RuntimeException("Invalid type: " + t);
            }

            public void caseArrayType(ArrayType t)
            {
                StringBuffer buffer = new StringBuffer();

                for(int i = 0; i < t.numDimensions; i++)
                    buffer.append("[");

                setResult(buffer.toString() + jasminDescriptorOf(t.baseType));
            }

            public void caseRefType(RefType t)
            {
                setResult("L" + t.className.replace('.', '/') + ";");
            }

            public void caseVoidType(VoidType t)
            {
                setResult("V");
            }
        });

        return (String) sw.getResult();

    }

    String jasminDescriptorOf(SootMethod m)
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("(");

        // Add methods parameters
        {
            Iterator typeIt = m.getParameterTypes().iterator();

            while(typeIt.hasNext())
            {
                Type t = (Type) typeIt.next();

                buffer.append(jasminDescriptorOf(t));
            }
        }

        buffer.append(")");

        buffer.append(jasminDescriptorOf(m.getReturnType()));

        return buffer.toString();
    }

    void emit(String s)
    {
        okayEmit(s);
    }
    
    void okayEmit(String s)
    {
        if(isEmittingMethodCode && !s.endsWith(":"))
            code.add("    " + s);
        else
            code.add(s);

//        System.out.println(s);
    }

    void emit(String s, int stackChange)
    {
        modifyStackHeight(stackChange);        
        okayEmit(s);
    }
    
    void modifyStackHeight(int stackChange)
    {
        currentStackHeight += stackChange;
        
        if(currentStackHeight < 0)
            throw new RuntimeException("Stack height is negative!");
            
        if(currentStackHeight > maxStackHeight)
            maxStackHeight = currentStackHeight;
    }
    
    public JasminClass(SootClass SootClass)
    {
        if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
            ca.mcgill.sable.soot.Main.buildJasminTimer.start();
        
        code = new LinkedList();

        // Emit the header
        {
            int modifiers = SootClass.getModifiers();

            if(Modifier.isInterface(modifiers))
            {
                modifiers -= Modifier.INTERFACE;

                emit(".interface " + Modifier.toString(modifiers) + " " + slashify(SootClass.getName()));
            }
            else
                emit(".class " + Modifier.toString(modifiers) + " " + slashify(SootClass.getName()));

            if(SootClass.hasSuperclass())
                emit(".super " + slashify(SootClass.getSuperclass().getName()));
            else
                emit(".super " + slashify(SootClass.getName()));

            emit("");
        }

        // Emit the interfaces
        {
            Iterator interfaceIt = SootClass.getInterfaces().iterator();

            while(interfaceIt.hasNext())
            {
                SootClass inter = (SootClass) interfaceIt.next();

                emit(".implements " + slashify(inter.getName()));
            }

            if(SootClass.getInterfaceCount() != 0)
                emit("");
        }

        // Emit the fields
        {
            Iterator fieldIt = SootClass.getFields().iterator();

            while(fieldIt.hasNext())
            {
                SootField field = (SootField) fieldIt.next();

                emit(".field " + Modifier.toString(field.getModifiers()) + " " +
                     "\"" + field.getName() + "\"" + " " + jasminDescriptorOf(field.getType()));
            }

            if(SootClass.getFieldCount() != 0)
                emit("");
        }

        // Emit the methods
        {
            Iterator methodIt = SootClass.getMethods().iterator();

            while(methodIt.hasNext())
            {
                emitMethod((SootMethod) methodIt.next());
                emit("");
            }
        }
        
        if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
            ca.mcgill.sable.soot.Main.buildJasminTimer.end();
    }

    void assignColorsToLocals(StmtBody body)
    {
        if(Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() +
                "] Assigning colors to locals...");
        
        if(Main.isProfilingOptimization)
            Main.packTimer.start();

        localToGroup = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
        groupToColorCount = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
        localToColor = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
        
        // Assign each local to a group, and set that group's color count to 0.
        {
            Iterator localIt = body.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();
                Object g;
                
                if(sizeOfType(l.getType()) == 1)
                    g = IntType.v();
                else
                    g = LongType.v();
                
                localToGroup.put(l, g);
                
                if(!groupToColorCount.containsKey(g))
                {
                    groupToColorCount.put(g, new Integer(0));
                }
            }
        }

        // Assign colors to the parameter locals.
        {
            Iterator codeIt = body.getUnits().iterator();

            while(codeIt.hasNext())
            {
                Stmt s = (Stmt) codeIt.next();

                if(s instanceof IdentityStmt &&
                    ((IdentityStmt) s).getLeftOp() instanceof Local)
                {
                    Local l = (Local) ((IdentityStmt) s).getLeftOp();
                    
                    Object group = localToGroup.get(l);
                    int count = ((Integer) groupToColorCount.get(group)).intValue();
                    
                    localToColor.put(l, new Integer(count));
                    
                    count++;
                    
                    groupToColorCount.put(group, new Integer(count));
                }
            }
        }
        
        // Call the graph colorer.
            FastColorer.assignColorsToLocals(body, localToGroup,
                localToColor, groupToColorCount);

        if(Main.isProfilingOptimization)
            Main.packTimer.end();
                    
    }
    
    void emitMethod(SootMethod method)
    {
        if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
            ca.mcgill.sable.soot.Main.buildJasminTimer.end();
        
        Body activeBody = method.getActiveBody();
        
        if(!(activeBody instanceof StmtBody))
            throw new RuntimeException("method: " + method.getName() + " has an invalid active body!");
        
        StmtBody body = (StmtBody) activeBody;
        
        if(body == null)
            throw new RuntimeException("method: " + method.getName() + " has no active body!");
            
        if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
            ca.mcgill.sable.soot.Main.buildJasminTimer.start();
        
        Chain units = body.getUnits();

        // let's create a u-d web for the ++ peephole optimization.

        if(Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() +
                "] Performing peephole optimizations...");

        CompleteUnitGraph stmtGraph = new CompleteUnitGraph(body);

        LocalDefs ld = new SimpleLocalDefs(stmtGraph);
        LocalUses lu = new SimpleLocalUses(stmtGraph, ld);

        int stackLimitIndex = -1;
        
        // Emit prologue
            emit(".method " + Modifier.toString(method.getModifiers()) + " " +
                 method.getName() + jasminDescriptorOf(method));

        subroutineToReturnAddressSlot = new HashMap(10, 0.7f);

        // Determine the stmtToLabel map
        {
            Iterator boxIt = body.getUnitBoxes().iterator();

            stmtToLabel = new HashMap(units.size() * 2 + 1, 0.7f);
            labelCount = 0;

            while(boxIt.hasNext())
            {
                // Assign a label for each statement reference
                {
                    StmtBox box = (StmtBox) boxIt.next();

                    if(!stmtToLabel.containsKey(box.getUnit()))
                        stmtToLabel.put(box.getUnit(), "label" + labelCount++);
                }
            }
        }

        // Emit the exceptions
        {
            Iterator trapIt = body.getTraps().iterator();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                if(trap.getBeginUnit() != trap.getEndUnit())
                    emit(".catch " + slashify(trap.getException().getName()) + " from " +
                        stmtToLabel.get(trap.getBeginUnit()) + " to " + stmtToLabel.get(trap.getEndUnit()) +
                        " using " + stmtToLabel.get(trap.getHandlerUnit()));
            }
        }

        // Determine where the locals go
        {
            int localCount = 0;
            int[] paramSlots = new int[method.getParameterCount()];
            int thisSlot = 0;
            Set assignedLocals = new HashSet();
            Map groupColorPairToSlot = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
            
            localToSlot = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);

            assignColorsToLocals(body);
            
            // Determine slots for 'this' and parameters
            {
                List paramTypes = method.getParameterTypes();

                if(!method.isStatic())
                {
                    thisSlot = 0;
                    localCount++;
                }

                for(int i = 0; i < paramTypes.size(); i++)
                {
                    paramSlots[i] = localCount;
                    localCount += sizeOfType((Type) paramTypes.get(i));
                }
            }

            // Handle identity statements
            {
                Iterator stmtIt = units.iterator();

                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();

                    if(s instanceof IdentityStmt && ((IdentityStmt) s).getLeftOp() instanceof Local)
                    {
                        Local l = (Local) ((IdentityStmt) s).getLeftOp();
                        IdentityRef identity = (IdentityRef) ((IdentityStmt) s).getRightOp();

                        int slot = 0;
                                                
                        if(identity instanceof ThisRef)
                        {
                            if(method.isStatic())
                                throw new RuntimeException("Attempting to use 'this' in static method");

                            slot = thisSlot;
                        }
                        else if(identity instanceof ParameterRef)
                            slot = paramSlots[((ParameterRef) identity).getIndex()];
                        else {
                            // Exception ref.  Skip over this
                            continue;
                        }
                        
                        // Make this (group, color) point to the given slot,
                        // so that all locals of the same color can be pointed here too
                        {
                            
                            GroupIntPair pair = new GroupIntPair(localToGroup.get(l), 
                                ((Integer) localToColor.get(l)).intValue());
                                
                            groupColorPairToSlot.put(pair, new Integer(slot));
                        }
                            
                        localToSlot.put(l, new Integer(slot));
                        assignedLocals.add(l);
                        
                    }
                }
            }

            // Assign the rest of the locals
            {
                Iterator localIt = body.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    if(!assignedLocals.contains(local))
                    {
                        GroupIntPair pair = new GroupIntPair(localToGroup.get(local), 
                                ((Integer) localToColor.get(local)).intValue());
                            
                        int slot;

                        if(groupColorPairToSlot.containsKey(pair))
                        {
                            // This local should share the same slot as the previous local with
                            // the same (group, color);
                            
                            slot = ((Integer) groupColorPairToSlot.get(pair)).intValue();
                        }
                        else { 
                            slot = localCount;           
                            localCount += sizeOfType(local.getType());
                    
                            groupColorPairToSlot.put(pair, new Integer(slot));
                         }
                            
                        localToSlot.put(local, new Integer(slot));
                        assignedLocals.add(local);
                    }
                }

                if (!Modifier.isNative(method.getModifiers())
                    && !Modifier.isAbstract(method.getModifiers()))
                  {
                    emit("    .limit stack ?");
                    stackLimitIndex = code.size() - 1;
                    
                    emit("    .limit locals " + localCount);
                  }
            }
        }

        // Emit code in one pass
        {
            Iterator codeIt = units.iterator();

            isEmittingMethodCode = true;
            maxStackHeight = 0; 
            isNextGotoAJsr = false;

            while(codeIt.hasNext())
            {
                Stmt s = (Stmt) codeIt.next();

                if(stmtToLabel.containsKey(s))
                    emit(stmtToLabel.get(s) + ":");

                if(subroutineToReturnAddressSlot.containsKey(s))
                {
                    AssignStmt assignStmt = (AssignStmt) s;

                    modifyStackHeight(1); // simulate the pushing of address onto the stack by the jsr

                    int slot = ((Integer) localToSlot.get(assignStmt.getLeftOp())).intValue();
                    
                    if(slot >= 0 && slot <= 3)
                        emit("astore_" + slot, -1);
                    else
                        emit("astore " + slot, -1);

                    //emit("astore " + ( ( Integer ) subroutineToReturnAddressSlot.get( s ) ).intValue() );

                }


                // Test for postincrement operators ++ and --
                // We can optimize them further.

                boolean contFlag = false;
                // this is a fake do, to give us break;
                do
                  {     
                    if (!(s instanceof AssignStmt))
                      break;

                    AssignStmt stmt = (AssignStmt)s;

                    // sanityCheck: see that we have another statement after s.
                    if (!codeIt.hasNext())
                      break;

                    Stmt ns = (Stmt)(stmtGraph.getSuccsOf(stmt).get(0));
                    if (!(ns instanceof AssignStmt))
                      break;
                    AssignStmt nextStmt = (AssignStmt)ns;

                    List l = stmtGraph.getSuccsOf(nextStmt);
                    if (l.size() != 1)
                      break;

                    Stmt nextNextStmt = (Stmt)(l.get(0));

                    final Value lvalue = stmt.getLeftOp();
                    final Value rvalue = stmt.getRightOp();

                    if (!(lvalue instanceof Local))
                      break;

                    // we're looking for this pattern: 
                    // local = <lvalue>; <lvalue> = local +/- 1; use(local);

                    // we need some notion of equals 
                    // for rvalue & nextStmt.getLeftOp().

                    if (!(lvalue instanceof Local)
                        || !nextStmt.getLeftOp().equals(rvalue)
                        || !(nextStmt.getRightOp() instanceof AddExpr))
                      break;

                    // make sure that nextNextStmt uses the local exactly once
                    {
                        Iterator boxIt = nextNextStmt.getUseBoxes().iterator();
                        
                        boolean foundExactlyOnce = false;
                        
                        while(boxIt.hasNext())
                        {
                            ValueBox box = (ValueBox) boxIt.next();
                            
                            if(box.getValue() == lvalue)
                            {
                                if(!foundExactlyOnce)
                                    foundExactlyOnce = true;
                                else
                                {
                                    foundExactlyOnce = false;
                                    break;
                                }
                            }
                        }    
                        
                        if(!foundExactlyOnce)
                            break;
                    }
                    
                    AddExpr addexp = (AddExpr)nextStmt.getRightOp();
                    if (!addexp.getOp1().equals(lvalue))
                      break;

                    Value added /* tax? */ = addexp.getOp2();
                        
                    if (!(added instanceof IntConstant)
                        || ((IntConstant)(added)).value != 1)
                      break;

                    /* check that we have two uses and that these */
                    /* uses live precisely in nextStmt and nextNextStmt */
                    /* LocalDefs tells us this: if there was no use, */
                    /* there would be no corresponding def. */
                    if (lu.getUsesOf(stmt).size() != 2 ||
                        ld.getDefsOfAt((Local)lvalue, nextStmt).size() != 1 ||
                        ld.getDefsOfAt((Local)lvalue, nextNextStmt).size() !=1)
                      break;

                    /* emit dup slot */

            /*
                    System.out.println("found ++ instance:");
                    System.out.println(s); System.out.println(nextStmt);
                    System.out.println(nextNextStmt);
                */
                    /* this should be redundant, but we do it */
                    /* just in case. */
                    if (lvalue.getType() != IntType.v())
                        break;

                    /* our strategy is as follows: eat the */
                    /* two incrementing statements, push the lvalue to */
                    /* be incremented & its holding local on a */
                    /* plusPlusStack and deal with it in */
                    /* emitLocal. */
                       
                    currentStackHeight = 0;

                    /* emit statements as before */
                    plusPlusValue = rvalue;
                    plusPlusHolder = (Local)lvalue;
                    plusPlusIncrementer = nextStmt;
                    plusPlusState = 0;

                    /* emit new statement with quickness */
                    emitStmt(nextNextStmt);

                    /* hm.  we didn't use local.  emit incrementage */
                    if (plusPlusHolder != null)
                        { emitStmt(stmt); emitStmt(nextStmt); }

                    if(currentStackHeight != 0)
                        throw new RuntimeException("Stack has height " + currentStackHeight + " after execution of stmt: " + s);
                    contFlag = true;
                    codeIt.next(); codeIt.next();
                  }
                while(false);
                if (contFlag) 
                    continue;

                // emit this statement
                {
                    currentStackHeight = 0;
                    emitStmt(s);
                        
                    if(currentStackHeight != 0)
                        throw new RuntimeException("Stack has height " + currentStackHeight + " after execution of stmt: " + s);
                }
            }

            isEmittingMethodCode = false;
            
            if (!Modifier.isNative(method.getModifiers())
                && !Modifier.isAbstract(method.getModifiers()))
              code.set(stackLimitIndex, "    .limit stack " + maxStackHeight);
        }

        // Emit epilogue
            emit(".end method");

    }

    public void print(PrintWriter out)
    {
        Iterator it = code.iterator();

        while(it.hasNext())
            out.println(it.next());
    }

    void emitAssignStmt(AssignStmt stmt)
    {
        final Value lvalue = stmt.getLeftOp();
        final Value rvalue = stmt.getRightOp();

        // Handle simple subcase where you can use the efficient iinc bytecode
            if(lvalue instanceof Local && (rvalue instanceof AddExpr || rvalue instanceof SubExpr))
            {
                Local l = (Local) lvalue;
                BinopExpr expr = (BinopExpr) rvalue;
                Value op1 = expr.getOp1();
                Value op2 = expr.getOp2();
                                
                if(l.getType().equals(IntType.v()))
                {
                    boolean isValidCase = false;
                    int x = 0;
                    
                    if(op1 == l && op2 instanceof IntConstant) 
                    {
                        x = ((IntConstant) op2).value;
                        isValidCase = true;
                    }
                    else if(expr instanceof AddExpr && 
                        op2 == l && op1 instanceof IntConstant)
                    {
                        // Note expr can't be a SubExpr because that would be x = 3 - x
                        
                        x = ((IntConstant) op1).value;
                        isValidCase = true;
                    }
                    
                    if(isValidCase && x >= Short.MIN_VALUE && x <= Short.MAX_VALUE)
                    {
                        emit("iinc " + ((Integer) localToSlot.get(l)).intValue() + " " +  
                            ((expr instanceof AddExpr) ? x : -x), 0);
                        return;
                    }        
                }
            }

            lvalue.apply(new AbstractJimpleValueSwitch()
            {
                public void caseArrayRef(ArrayRef v)
                {
                    emitValue(v.getBase());
                    emitValue(v.getIndex());
                    emitValue(rvalue);
                    
                    v.getType().apply(new TypeSwitch()
                    {
                        public void caseArrayType(ArrayType t)
                        {
                            emit("aastore", -3);
                        }

                        public void caseDoubleType(DoubleType t)
                        {
                            emit("dastore", -4);
                        }

                        public void caseFloatType(FloatType t)
                        {
                            emit("fastore", -3);
                        }

                        public void caseIntType(IntType t)
                        {
                            emit("iastore", -3);
                        }

                        public void caseLongType(LongType t)
                        {
                            emit("lastore", -4);
                        }

                        public void caseRefType(RefType t)
                        {
                            emit("aastore", -3);
                        }

                        public void caseByteType(ByteType t)
                        {
                            emit("bastore", -3);
                        }

                        public void caseBooleanType(BooleanType t)
                        {
                            emit("bastore", -3);
                        }

                        public void caseCharType(CharType t)
                        {
                            emit("castore", -3);
                        }

                        public void caseShortType(ShortType t)
                        {
                            emit("sastore", -3);
                        }

                        public void defaultCase(Type t)
                        {
                            throw new RuntimeException("Invalid type: " + t);
                        }
                    });
                }
                
                public void defaultCase(Value v)
                    {
                        throw new RuntimeException("Can't store in value " + v);
                    }
                
                public void caseInstanceFieldRef(InstanceFieldRef v)
                    {
                        emitValue(v.getBase());
                        emitValue(rvalue);
                        
                        emit("putfield " + slashify(v.getField().getDeclaringClass().getName()) + "/" +
                             v.getField().getName() + " " + jasminDescriptorOf(v.getField().getType()), 
                             -1 + -sizeOfType(v.getField().getType()));
                    }
                
                public void caseLocal(final Local v)
                {
                    final int slot = ((Integer) localToSlot.get(v)).intValue();
                        
                    v.getType().apply(new TypeSwitch()
                    {
                        public void caseArrayType(ArrayType t)
                        {
                            emitValue(rvalue);

                            if(slot >= 0 && slot <= 3)
                                emit("astore_" + slot, -1);
                            else
                                emit("astore " + slot, -1);
                        }

                        public void caseDoubleType(DoubleType t)
                        {
                            emitValue(rvalue);

                            if(slot >= 0 && slot <= 3)
                                emit("dstore_" + slot, -2);
                            else
                                emit("dstore " + slot, -2);
                        }
                        
                        public void caseFloatType(FloatType t)
                        {
                            emitValue(rvalue);
                            
                            if(slot >= 0 && slot <= 3)
                                emit("fstore_" + slot, -1);
                            else
                                emit("fstore " + slot, -1);
                        }

                        public void caseIntType(IntType t)
                            {
                                emitValue(rvalue);
                                
                                if(slot >= 0 && slot <= 3)
                                    emit("istore_" + slot, -1);
                                else
                                    emit("istore " + slot, -1);
                            }

                        public void caseLongType(LongType t)
                            {
                                emitValue(rvalue);
                                
                                if(slot >= 0 && slot <= 3)
                                    emit("lstore_" + slot, -2);
                                else
                                    emit("lstore " + slot, -2);
                            }
                        
                        public void caseRefType(RefType t)
                            {
                                emitValue(rvalue);
                                
                                if(slot >= 0 && slot <= 3)
                                    emit("astore_" + slot, -1);
                                else
                                    emit("astore " + slot, -1);
                            }

                        public void caseStmtAddressType(StmtAddressType t)
                            {
                                isNextGotoAJsr = true;
                                returnAddressSlot = slot;
                                
                                /*
                                  if ( slot >= 0 && slot <= 3)
                                  emit("astore_" + slot,  );
                                  else
                                  emit("astore " + slot,  );
                                  
                                */
                                
                            }
                        
                        public void caseNullType(NullType t)
                            {
                                emitValue(rvalue);
                                
                                if(slot >= 0 && slot <= 3)
                                    emit("astore_" + slot, -1);
                                else
                                    emit("astore " + slot, -1);
                            }
                        
                        public void defaultCase(Type t)
                            {
                                throw new RuntimeException("Invalid local type: " + t);
                            }
                    });
                }
                
                public void caseStaticFieldRef(StaticFieldRef v)
                    {
                        SootField field = v.getField();
                        
                        emitValue(rvalue);
                        emit("putstatic " + slashify(field.getDeclaringClass().getName()) + "/" +
                             field.getName() + " " + jasminDescriptorOf(field.getType()),
                             -sizeOfType(v.getField().getType()));
                    }
            });
    }

    void emitIfStmt(IfStmt stmt)
    {
        Value cond = stmt.getCondition();

        final Value op1 = ((BinopExpr) cond).getOp1();
        final Value op2 = ((BinopExpr) cond).getOp2();
        final String label = (String) stmtToLabel.get(stmt.getTarget());

        // Handle simple subcase where op1 is null
            if(op2 instanceof NullConstant || op1 instanceof NullConstant)
            {
                if(op2 instanceof NullConstant)
                    emitValue(op1);
                else
                    emitValue(op2);
                    
                if(cond instanceof EqExpr)
                    emit("ifnull " + label, -1);
                else if(cond instanceof NeExpr)  
                    emit("ifnonnull "+ label, -1);
                else
                    throw new RuntimeException("invalid condition");
                    
                return;
            }

        // Handle simple subcase where op2 is 0  
            if(op2 instanceof IntConstant && ((IntConstant) op2).value == 0)
            {
                emitValue(op1);
                
                cond.apply(new AbstractJimpleValueSwitch()
                {
                    public void caseEqExpr(EqExpr expr)
                    {
                        emit("ifeq " + label, -1);
                    }
        
                    public void caseNeExpr(NeExpr expr)
                    {
                        emit("ifne " + label, -1);
                    }
        
                    public void caseLtExpr(LtExpr expr)
                    {
                        emit("iflt " + label, -1); 
                    }
                    
                    public void caseLeExpr(LeExpr expr)
                    {
                        emit("ifle " + label, -1);
                    }
        
                    public void caseGtExpr(GtExpr expr)
                    {
                        emit("ifgt " + label, -1);
                    }
        
                    public void caseGeExpr(GeExpr expr)
                    {
                        emit("ifge " + label, -1);
                    }
        
                    public void defaultCase(Value v)
                    {
                        throw new RuntimeException("invalid condition " + v);
                    }
                });               
                 
                return;
            }
        
        // Handle simple subcase where op1 is 0  (flip directions)
            if(op1 instanceof IntConstant && ((IntConstant) op1).value == 0)
            {
                emitValue(op2);
                
                cond.apply(new AbstractJimpleValueSwitch()
                {
                    public void caseEqExpr(EqExpr expr)
                    {
                        emit("ifeq " + label, -1);
                    }
        
                    public void caseNeExpr(NeExpr expr)
                    {
                        emit("ifne " + label, -1);
                    }
        
                    public void caseLtExpr(LtExpr expr)
                    {
                        emit("ifgt " + label, -1); 
                    }
                    
                    public void caseLeExpr(LeExpr expr)
                    {
                        emit("ifge " + label, -1);
                    }
        
                    public void caseGtExpr(GtExpr expr)
                    {
                        emit("iflt " + label, -1);
                    }
        
                    public void caseGeExpr(GeExpr expr)
                    {
                        emit("ifle " + label, -1);
                    }
        
                    public void defaultCase(Value v)
                    {
                        throw new RuntimeException("invalid condition " + v);
                    }
                });               
                 
                return;
            }
        
        emitValue(op1);
        emitValue(op2);

        cond.apply(new AbstractJimpleValueSwitch()
        {
            public void caseEqExpr(EqExpr expr)
            {
                op1.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpeq " + label, -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpeq " + label, -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpeq " + label, -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpeq " + label, -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpeq " + label, -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifeq " + label, -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifeq " + label, -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifeq " + label, -1);
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpeq " + label, -2);
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpeq " + label, -2);
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpeq " + label, -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseNeExpr(NeExpr expr)
            {
                op1.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpne " + label, -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpne " + label, -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpne " + label, -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpne " + label, -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpne " + label, -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifne " + label, -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifne " + label, -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifne " + label, -1);
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpne " + label, -2);
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpne " + label, -2);
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpne " + label, -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type for NeExpr: " + t);
                    }
                });
            }

            public void caseLtExpr(LtExpr expr)
            {
                op1.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmplt " + label, -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmplt " + label, -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmplt " + label, -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmplt " + label, -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmplt " + label, -2);
                    }


                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("iflt " + label, -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("iflt " + label, -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("iflt " + label, -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseLeExpr(LeExpr expr)
            {
                op1.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmple " + label, -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmple " + label, -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmple " + label, -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmple " + label, -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmple " + label, -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifle " + label, -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifle " + label, -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifle " + label, -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseGtExpr(GtExpr expr)
            {
                op1.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpgt " + label, -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpgt " + label, -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpgt " + label, -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpgt " + label, -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpgt " + label, -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifgt " + label, -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifgt " + label, -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifgt " + label, -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseGeExpr(GeExpr expr)
            {
                op1.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpge " + label, -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpge " + label, -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpge " + label, -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpge " + label, -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpge " + label, -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifge " + label, -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifge " + label, -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifge " + label, -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void defaultCase(Value v)
            {
                throw new RuntimeException("invalid condition " + v);
            }
        });
    }

    void emitStmt(Stmt stmt)
    {
        stmt.apply(new AbstractStmtSwitch()
        {
            public void caseAssignStmt(AssignStmt s)
            {
                emitAssignStmt(s);
            }

            public void caseIdentityStmt(IdentityStmt s)
            {
                if(s.getRightOp() instanceof CaughtExceptionRef &&
                    s.getLeftOp() instanceof Local)
                {
                    int slot = ((Integer) localToSlot.get(s.getLeftOp())).intValue();

                    modifyStackHeight(1); // simulate the pushing of the exception onto the 
                                          // stack by the jvm
                                          
                    if(slot >= 0 && slot <= 3)
                        emit("astore_" + slot, -1);
                    else
                        emit("astore " + slot, -1);
                }
            }

            public void caseBreakpointStmt(BreakpointStmt s)
            {
                emit("breakpoint", 0);
            }

            public void caseInvokeStmt(InvokeStmt s)
            {
                emitValue(s.getInvokeExpr());

                Type returnType = ((InvokeExpr) s.getInvokeExpr()).getMethod().getReturnType();

                if(!returnType.equals(VoidType.v()))
                {
                    // Need to do some cleanup because this value is not used.

                    if(sizeOfType(returnType) == 1)
                        emit("pop", -1);
                    else
                        emit("pop2", -2);
                }
            }

            public void defaultCase(Stmt s)
            {
                throw new RuntimeException("invalid stmt: " + s);
            }

            public void caseEnterMonitorStmt(EnterMonitorStmt s)
            {
                emitValue(s.getOp());
                emit("monitorenter", -1);
            }

            public void caseExitMonitorStmt(ExitMonitorStmt s)
            {
                emitValue(s.getOp());
                emit("monitorexit", -1);
            }

            public void caseGotoStmt(GotoStmt s)
            {
                if(isNextGotoAJsr)
                {
                    emit("jsr " + stmtToLabel.get(s.getTarget()));
                    isNextGotoAJsr = false;

                    subroutineToReturnAddressSlot.put(s.getTarget(), new Integer(returnAddressSlot));
                }
                else
                    emit("goto " + stmtToLabel.get(s.getTarget()));
            }


            public void caseIfStmt(IfStmt s)
            {
                emitIfStmt(s);
            }

            public void caseLookupSwitchStmt(LookupSwitchStmt s)
            {
                emitValue(s.getKey());
                emit("lookupswitch", -1);

                List lookupValues = s.getLookupValues();
                List targets = s.getTargets();

                for(int i = 0; i < lookupValues.size(); i++)
                    emit("  " + lookupValues.get(i) + " : " + stmtToLabel.get(targets.get(i)));

                emit("  default : " + stmtToLabel.get(s.getDefaultTarget()));
            }

            public void caseNopStmt(NopStmt s)
            {
                emit("nop", 0);
            }

            public void caseRetStmt(RetStmt s)
            {
                emit("ret " + localToSlot.get(s.getStmtAddress()), 0);
            }

            public void caseReturnStmt(ReturnStmt s)
            {
                emitValue(s.getOp());

                Value returnValue = s.getOp();

                returnValue.getType().apply(new TypeSwitch()
                {
                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid return type " + t.toBriefString());
                     }

                     public void caseDoubleType(DoubleType t)
                     {
                        emit("dreturn", -2);
                     }

                     public void caseFloatType(FloatType t)
                     {
                        emit("freturn", -1);
                     }

                     public void caseIntType(IntType t)
                     {
                        emit("ireturn", -1);
                     }

                     public void caseByteType(ByteType t)
                     {
                        emit("ireturn", -1);
                     }

                     public void caseShortType(ShortType t)
                     {
                        emit("ireturn", -1);
                     }

                     public void caseCharType(CharType t)
                     {
                        emit("ireturn", -1);
                     }

                     public void caseBooleanType(BooleanType t)
                     {
                        emit("ireturn", -1);
                     }

                     public void caseLongType(LongType t)
                     {
                        emit("lreturn", -2);
                     }

                     public void caseArrayType(ArrayType t)
                     {
                        emit("areturn", -1);
                     }

                     public void caseRefType(RefType t)
                     {
                        emit("areturn", -1);
                     }

                     public void caseNullType(NullType t)
                     {
                        emit("areturn", -1);
                     }

                });
            }

            public void caseReturnVoidStmt(ReturnVoidStmt s)
            {
                emit("return", 0);
            }

            public void caseTableSwitchStmt(TableSwitchStmt s)
            {
                emitValue(s.getKey());
                emit("tableswitch " + s.getLowIndex() + " ; high = " + s.getHighIndex(), -1);

                List targets = s.getTargets();

                for(int i = 0; i < targets.size(); i++)
                    emit("  " + stmtToLabel.get(targets.get(i)));

                emit("default : " + stmtToLabel.get(s.getDefaultTarget()));
            }

            public void caseThrowStmt(ThrowStmt s)
            {
                emitValue(s.getOp());
                emit("athrow", -1);
            }
        });
    }

    /* try to pre-duplicate a local and fix-up its dup_xn parameter. */
    /* if we find that we're unable to proceed, we swap the dup_xn */
    /* for a store pl, load pl combination */
    Value plusPlusValue;
    Local plusPlusHolder;
    int plusPlusState;
    int plusPlusPlace;
    int plusPlusHeight;
    Stmt plusPlusIncrementer;

    void emitLocal(Local v)
    {
        final int slot = ((Integer) localToSlot.get(v)).intValue();
        final Local vAlias = v;

        v.getType().apply(new TypeSwitch()
        {
            public void caseArrayType(ArrayType t)
            {
                if(slot >= 0 && slot <= 3)
                    emit("aload_" + slot, 1);
                else
                    emit("aload " + slot, 1);
            }
            
            public void defaultCase(Type t)
            {
                throw new RuntimeException("invalid local type to load" + t);
            }

            public void caseDoubleType(DoubleType t)
            {
                if(slot >= 0 && slot <= 3)
                    emit("dload_" + slot, 2);
                else
                    emit("dload " + slot, 2);
            }

            public void caseFloatType(FloatType t)
            {
                if(slot >= 0 && slot <= 3)
                    emit("fload_" + slot, 1);
                else
                    emit("fload " + slot, 1);
            }
            
            public void caseIntType(IntType t)
            {
                if (vAlias.equals(plusPlusHolder))
                {
                    switch(plusPlusState)
                    {
                    case 0:    
                        // ok, we're called upon to emit the
                        // ++ target, whatever it was.
                        
                        // now we need to emit a statement incrementing
                        // the correct value.  
                        // actually, just remember the local to be incremented.

                        plusPlusState = 1;
                        
                        emitStmt(plusPlusIncrementer);
                        int diff = plusPlusHeight - currentStackHeight + 1;
                        if (diff > 0)
                          code.set(plusPlusPlace, "    dup_x"+diff);
                        plusPlusHolder = null;

                        // afterwards we have the value on the stack.
                        return;
                    case 1:
                        plusPlusHeight = currentStackHeight;

                        emitValue(plusPlusValue);

                        plusPlusPlace = code.size();
                        emit("dup", 1);

                        return;
                    }
                }
                if(slot >= 0 && slot <= 3)
                    emit("iload_" + slot, 1);
                else
                    emit("iload " + slot, 1);
            }

            public void caseLongType(LongType t)
            {
                if(slot >= 0 && slot <= 3)
                    emit("lload_" + slot, 2);
                else
                    emit("lload " + slot, 2);
            }

            public void caseRefType(RefType t)
            {
                if(slot >= 0 && slot <= 3)
                    emit("aload_" + slot, 1);
                else
                    emit("aload " + slot, 1);
            }

            public void caseNullType(NullType t)
            {
                if(slot >= 0 && slot <= 3)
                    emit("aload_" + slot, 1);
                else
                    emit("aload " + slot, 1);
            }
        });
    }

    void emitValue(Value value)
    {
        value.apply(new AbstractJimpleValueSwitch()
        {
            public void caseAddExpr(AddExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("iadd", -1);
                    }
                    
                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("ladd", -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dadd", -2);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fadd", -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for add");
                    }
                });

            }

            public void caseAndExpr(AndExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("iand", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("land", -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for and");
                    }
                });
            }

            public void caseArrayRef(ArrayRef v)
            {
                emitValue(v.getBase());
                emitValue(v.getIndex());

                v.getType().apply(new TypeSwitch()
                {
                    public void caseArrayType(ArrayType ty)
                    {
                        emit("aaload", -1);
                    }

                    public void caseBooleanType(BooleanType ty)
                    {
                        emit("baload", -1);
                    }

                    public void caseByteType(ByteType ty)
                    {
                        emit("baload", -1);
                    }

                    public void caseCharType(CharType ty)
                    {
                        emit("caload", -1);
                    }

                    public void defaultCase(Type ty)
                    {
                        throw new RuntimeException("invalid base type");
                    }

                    public void caseDoubleType(DoubleType ty)
                    {
                        emit("daload", 0);
                    }

                    public void caseFloatType(FloatType ty)
                    {
                        emit("faload", -1);
                    }

                    public void caseIntType(IntType ty)
                    {
                        emit("iaload", -1);
                    }

                    public void caseLongType(LongType ty)
                    {
                        emit("laload", 0);
                    }

                    public void caseNullType(NullType ty)
                    {
                        emit("aaload", -1);
                    }
                    public void caseRefType(RefType ty)
                    {
                        emit("aaload", -1);
                    }

                    public void caseShortType(ShortType ty)
                    {
                        emit("saload", -1);
                    }
                });
            }

            public void caseCastExpr(final CastExpr v)
            {
                final Type toType = v.getCastType();
                final Type fromType = v.getOp().getType();

                emitValue(v.getOp());

                if(toType instanceof RefType)
                    emit("checkcast " + slashify(toType.toBriefString()), 0);
                else if(toType instanceof ArrayType)
                    emit("checkcast " + jasminDescriptorOf(toType), 0);
                else {
                    fromType.apply(new TypeSwitch()
                    {
                        public void defaultCase(Type ty)
                        {
                            throw new RuntimeException("invalid fromType " + fromType);
                        }

                        public void caseDoubleType(DoubleType ty)
                        {
                            if(toType.equals(IntType.v()))
                                emit("d2i", -1);
                            else if(toType.equals(LongType.v()))
                                emit("d2l", 0);
                            else if(toType.equals(FloatType.v()))
                                emit("d2f", -1);
                            else
                                throw new RuntimeException("invalid toType from double: " + toType);
                        }

                        public void caseFloatType(FloatType ty)
                        {
                            if(toType.equals(IntType.v()))
                                emit("f2i", 0);
                            else if(toType.equals(LongType.v()))
                                emit("f2l", 1);
                            else if(toType.equals(DoubleType.v()))
                                emit("f2d", 1);
                            else
                                throw new RuntimeException("invalid toType from float: " + toType);
                        }

                        public void caseIntType(IntType ty)
                        {
                            emitIntToTypeCast();
                        }

                        public void caseBooleanType(BooleanType ty)
                        {
                              emitIntToTypeCast();
                        }

                        public void caseByteType(ByteType ty)
                        {
                            emitIntToTypeCast();
                        }

                        public void caseCharType(CharType ty)
                        {
                            emitIntToTypeCast();
                        }

                        public void caseShortType(ShortType ty)
                        {
                            emitIntToTypeCast();
                        }

                        private void emitIntToTypeCast()
                        {
                            if(toType.equals(ByteType.v()))
                                emit("i2b", 0);
                            else if(toType.equals(CharType.v()))
                                emit("i2c", 0);
                            else if(toType.equals(ShortType.v()))
                                emit("i2s", 0);
                            else if(toType.equals(FloatType.v()))
                                emit("i2f", 0);
                            else if(toType.equals(LongType.v()))
                                emit("i2l", 1);
                            else if(toType.equals(DoubleType.v()))
                                emit("i2d", 1);
                            else if(toType.equals(IntType.v()))
                                ;  // this shouldn't happen?
                            else
                                throw new RuntimeException("invalid toType from int: " + toType +
                                    " " + v.toBriefString());
                        }

                        public void caseLongType(LongType ty)
                        {
                            if(toType.equals(IntType.v()))
                                emit("l2i", -1);
                            else if(toType.equals(FloatType.v()))
                                emit("l2f", -1);
                            else if(toType.equals(DoubleType.v()))
                                emit("l2d", 0);
                            else if(toType.equals(ByteType.v()))
                              { emit("l2i", -1); emitIntToTypeCast(); }
                            else if(toType.equals(ShortType.v()))
                              { emit("l2i", -1); emitIntToTypeCast(); }
                            else if(toType.equals(CharType.v()))
                              { emit("l2i", -1); emitIntToTypeCast(); }
                            else if(toType.equals(BooleanType.v()))
                              { emit("l2i", -1); emitIntToTypeCast(); }
                            else
                                throw new RuntimeException("invalid toType from long: " + toType);
                        }
                    });
                }
            }

            public void caseCmpExpr(CmpExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());
                emit("lcmp", -3);
            }

            public void caseCmpgExpr(CmpgExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                if(v.getOp1().getType().equals(FloatType.v()))
                    emit("fcmpg", -1);
                else
                    emit("dcmpg", -3);
            }

            public void caseCmplExpr(CmplExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                if(v.getOp1().getType().equals(FloatType.v()))
                    emit("fcmpl", -1);
                else
                    emit("dcmpl", -3);
            }

            public void defaultCase(Value v)
            {
                throw new RuntimeException("Can't load value: " + v);
            }

            public void caseDivExpr(DivExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("idiv", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("ldiv", -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("ddiv", -2);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fdiv", -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for div");
                    }
                });

            }

            public void caseDoubleConstant(DoubleConstant v)
            {
                if(v.value == 0)
                    emit("dconst_0", 2);
                else if(v.value == 1)
                    emit("dconst_1", 2);
                else {
                    String s = v.toString();
                    
                    if(s.equals("Infinity"))
                        s="+DoubleInfinity";
                    
                    if(s.equals("-Infinity"))
                        s="-DoubleInfinity";
                    
                    if(s.equals("NaN"))
                        s="+DoubleNaN";
                        
                    emit("ldc2_w " + s, 2);
                }
            }

            public void caseFloatConstant(FloatConstant v)
            {
                if(v.value == 0)
                    emit("fconst_0", 1);
                else if(v.value == 1)
                    emit("fconst_1", 1);
                else if(v.value == 2)
                    emit("fconst_2", 1);
                else {
                    String s = v.toString();
                    
                    if(s.equals("InfinityF"))
                        s="+FloatInfinity";
                    if(s.equals("-InfinityF"))
                        s="-FloatInfinity";
                        
                    if(s.equals("NaNF"))
                        s="+FloatNaN";
                    
                    emit("ldc " + s, 1);
                }
            }


            public void caseInstanceFieldRef(InstanceFieldRef v)
            {
                emitValue(v.getBase());

                emit("getfield " + slashify(v.getField().getDeclaringClass().getName()) + "/" +
                    v.getField().getName() + " " + jasminDescriptorOf(v.getField().getType()), 
                    -1 + sizeOfType(v.getField().getType()));
            }

            public void caseInstanceOfExpr(InstanceOfExpr v)
            {
                final Type checkType;
                
                emitValue(v.getOp());

                checkType = v.getCheckType();
                
                if(checkType instanceof RefType)
                    emit("instanceof " + slashify(checkType.toBriefString()), 0);
                else if(checkType instanceof ArrayType)
                    emit("instanceof " + jasminDescriptorOf(checkType), 0);
            }

            public void caseIntConstant(IntConstant v)
            {
                if(v.value == -1)
                    emit("iconst_m1", 1);
                else if(v.value >= 0 && v.value <= 5)
                    emit("iconst_" + v.value, 1);
                else if(v.value >= Byte.MIN_VALUE && v.value <= Byte.MAX_VALUE)
                    emit("bipush " + v.value, 1);
                else if(v.value >= Short.MIN_VALUE && v.value <= Short.MAX_VALUE)
                    emit("sipush " + v.value, 1);
                else
                    emit("ldc " + v.toString(), 1);
            }

            public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v)
            {
                SootMethod m = v.getMethod();

                emitValue(v.getBase());

                for(int i = 0; i < m.getParameterCount(); i++)
                    emitValue(v.getArg(i));

                emit("invokeinterface " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m) + " " + (argCountOf(m) + 1),
                    -(argCountOf(m) + 1) + sizeOfType(m.getReturnType()));
            }

            public void caseLengthExpr(LengthExpr v)
            {
                emitValue(v.getOp());
                emit("arraylength", 0);
            }

            public void caseLocal(Local v)
            {
                emitLocal(v);
            }

            public void caseLongConstant(LongConstant v)
            {
                if(v.value == 0)
                    emit("lconst_0", 2);
                else if(v.value == 1)
                    emit("lconst_1", 2);
                else
                    emit("ldc2_w " + v.toString(), 2);
            }


            public void caseMulExpr(MulExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("imul", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lmul", -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dmul", -2);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fmul", -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for mul");
                    }
                });
            }

            public void caseLtExpr(LtExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getOp1().getType().apply(new TypeSwitch()
                {
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emitBooleanBranch("iflt");
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emitBooleanBranch("iflt");
                    }

                    private void handleIntCase()
                    {
                        emit("if_icmplt", -2);
                    }


                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emitBooleanBranch("iflt");
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseLeExpr(LeExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getOp1().getType().apply(new TypeSwitch()
                {
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emitBooleanBranch("ifle");
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emitBooleanBranch("ifle");
                    }

                    private void handleIntCase()
                    {
                        emit("if_icmple", -2);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emitBooleanBranch("ifle");
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseGtExpr(GtExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getOp1().getType().apply(new TypeSwitch()
                {
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emitBooleanBranch("ifgt");
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emitBooleanBranch("ifgt");
                    }

                    private void handleIntCase()
                    {
                        emit("if_icmpgt", -2);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emitBooleanBranch("ifgt");
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseGeExpr(GeExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getOp1().getType().apply(new TypeSwitch()
                {
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emitBooleanBranch("ifge");
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emitBooleanBranch("ifge");
                    }

                    private void handleIntCase()
                    {
                        emit("if_icmpge", -2);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emitBooleanBranch("ifge");
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseNeExpr(NeExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getOp1().getType().apply(new TypeSwitch()
                {
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("iconst_0", 1);
                        emitBooleanBranch("if_icmpne");
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("iconst_0", 1);
                        emitBooleanBranch("if_icmpne");
                    }

                    private void handleIntCase()
                    {
                        emit("if_icmpne", -2);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("iconst_0", 1);
                        emitBooleanBranch("if_icmpne");
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emitBooleanBranch("if_acmpne");
                    }

                    public void caseRefType(RefType t)
                    {
                        emitBooleanBranch("if_acmpne");
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseEqExpr(EqExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getOp1().getType().apply(new TypeSwitch()
                {
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("iconst_0", 1);
                        emitBooleanBranch("if_icmpeq");
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -3);
                        emit("iconst_0", 1);
                        emitBooleanBranch("if_icmpeq");
                    }

                    private void handleIntCase()
                    {
                        emit("if_icmpeq", -2);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("iconst_0", 1);
                        emitBooleanBranch("if_icmpeq");
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emitBooleanBranch("if_acmpeq");
                    }

                    public void casbeRefType(RefType t)
                    {
                        emitBooleanBranch("if_acmpeq");
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseNegExpr(final NegExpr v)
            {
                emitValue(v.getOp());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("ineg", 0);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lneg", 0);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dneg", 0);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fneg", 0);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for neg: " + t + ": " + v);
                    }
                });

            }

            public void caseNewArrayExpr(NewArrayExpr v)
            {
                Value size = v.getSize();

                emitValue(size);

                if(v.getBaseType() instanceof RefType)
                    emit("anewarray " + slashify(v.getBaseType().toBriefString()), 0);
                else if(v.getBaseType() instanceof ArrayType)
                    emit("anewarray " + jasminDescriptorOf(v.getBaseType()), 0);
                else
                    emit("newarray " + v.getBaseType().toBriefString(), 0);
            }

            public void caseNewMultiArrayExpr(NewMultiArrayExpr v)
            {
                List sizes = v.getSizes();

                for(int i = 0; i < sizes.size(); i++)
                    emitValue((Value) sizes.get(i));

                emit("multianewarray " + jasminDescriptorOf(v.getBaseType()) + " " + sizes.size(), -sizes.size() + 1);
            }

            public void caseNewExpr(NewExpr v)
            {
                emit("new " + slashify(v.getBaseType().toBriefString()), 1);
            }

            public void caseNewInvokeExpr(NewInvokeExpr v)
            {
                emit("new " + slashify(v.getBaseType().toBriefString()), 1);
                emit("dup", 1);
                
                SootMethod m = v.getMethod();

                // emitValue(v.getBase());
                // already on the stack
                
                for(int i = 0; i < m.getParameterCount(); i++)
                    emitValue(v.getArg(i));

                emit("invokespecial " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m),
                    -(argCountOf(m) + 1) + sizeOfType(m.getReturnType()));
            }

            public void caseNullConstant(NullConstant v)
            {
                emit("aconst_null", 1);
            }

            public void caseOrExpr(OrExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("ior", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lor", -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for or");
                    }
                });
            }

            public void caseRemExpr(RemExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("irem", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lrem", -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("drem", -2);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("frem", -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for rem");
                    }
                });
            }

            public void caseShlExpr(ShlExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("ishl", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lshl", -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for shl");
                    }
                });
            }

            public void caseShrExpr(ShrExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("ishr", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lshr", -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for shr");
                    }
                });
            }

            public void caseSpecialInvokeExpr(SpecialInvokeExpr v)
            {
                SootMethod m = v.getMethod();

                emitValue(v.getBase());

                for(int i = 0; i < m.getParameterCount(); i++)
                    emitValue(v.getArg(i));

                emit("invokespecial " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m),
                    -(argCountOf(m) + 1) + sizeOfType(m.getReturnType()));
            }

            public void caseStaticInvokeExpr(StaticInvokeExpr v)
            {
                SootMethod m = v.getMethod();

                for(int i = 0; i < m.getParameterCount(); i++)
                    emitValue(v.getArg(i));

                emit("invokestatic " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m),
                    -(argCountOf(m)) + sizeOfType(m.getReturnType()));
            }

            public void caseStaticFieldRef(StaticFieldRef v)
            {
                emit("getstatic " + slashify(v.getField().getDeclaringClass().getName()) + "/" +
                    v.getField().getName() + " " + jasminDescriptorOf(v.getField().getType()),
                    sizeOfType(v.getField().getType()));
            }

            public void caseStringConstant(StringConstant v)
            {
                emit("ldc " + v.toString(), 1);
            }

            public void caseSubExpr(SubExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("isub", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lsub", -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dsub", -2);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fsub", -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for sub");
                    }
                });

            }

            public void caseUshrExpr(UshrExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("iushr", -1);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lushr", -1);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for ushr");
                    }
                });
            }

            public void caseVirtualInvokeExpr(VirtualInvokeExpr v)
            {
                SootMethod m = v.getMethod();

                emitValue(v.getBase());

                for(int i = 0; i < m.getParameterCount(); i++)
                    emitValue(v.getArg(i));

                emit("invokevirtual " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m),
                    -(argCountOf(m) + 1) + sizeOfType(m.getReturnType()));
            }

            public void caseXorExpr(XorExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());

                v.getType().apply(new TypeSwitch()
                {
                    private void handleIntCase() 
                    { 
                        emit ("ixor", -1); 
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("lxor", -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for xor");
                    }
                });
            }
        });
    }

    public void emitBooleanBranch(String s)
    {
        int count;
        
        if(s.indexOf("icmp") != -1 || s.indexOf("acmp") != -1)
            count = -2;
        else
            count = -1;
            
        emit(s + " label" + labelCount, count);
        emit("iconst_0", 1);
        emit("goto label" + labelCount+1, 0);
        emit("label" + labelCount++ + ":");
        emit("iconst_1", 1);
        emit("label" + labelCount++ + ":");
    }

}





