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

package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import java.io.*;

public class JasminClass
{
    Map instToLabel;
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

    static int sizeOfType(Type t)
    {
        if(t instanceof DoubleWordType || t instanceof LongType || t instanceof DoubleType)
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

//          System.out.println(s + " @ "+currentStackHeight);
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

            if(SootClass.hasSuperClass())
                emit(".super " + slashify(SootClass.getSuperClass().getName()));
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

    void assignColorsToLocals(BafBody body)
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
            Iterator codeIt = body.getUnitList().iterator();

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
//              FastColorer.assignColorsToLocals(body, localToGroup,
//                  localToColor, groupToColorCount);

        if(Main.isProfilingOptimization)
            Main.packTimer.end();
                    
    }
    
    void emitMethod(SootMethod method)
    {
        if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
            ca.mcgill.sable.soot.Main.buildJasminTimer.end();
        
        Body activeBody = method.getActiveBody();
        
        if(!(activeBody instanceof BafBody))
            throw new RuntimeException("method: " + method.getName() + " has an invalid active body!");
        
        BafBody body = (BafBody) activeBody;
        
        if(body == null)
            throw new RuntimeException("method: " + method.getName() + " has no active body!");
            
        if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
            ca.mcgill.sable.soot.Main.buildJasminTimer.start();
        
        List instList = body.getUnitList();
	try { // debug

        // let's create a u-d web for the ++ peephole optimization.

//          if(Main.isVerbose)
//              System.out.println("[" + body.getMethod().getName() +
//                  "] Performing peephole optimizations...");

//          CompleteStmtGraph stmtGraph = new CompleteStmtGraph(stmtList);

//          LocalDefs ld = new SimpleLocalDefs(stmtGraph);
//              LocalUses lu = new SimpleLocalUses(stmtGraph, ld);

        int stackLimitIndex = -1;
        
        // Emit prologue
            emit(".method " + Modifier.toString(method.getModifiers()) + " " +
                 method.getName() + jasminDescriptorOf(method));

        subroutineToReturnAddressSlot = new HashMap(10, 0.7f);

        // Determine the instToLabel map
        {
            Iterator boxIt = body.getUnitBoxes().iterator();

            instToLabel = new HashMap(instList.size() * 2 + 1, 0.7f);
            labelCount = 0;

            while(boxIt.hasNext())
            {
                // Assign a label for each statement reference
                {
                    InstBox box = (InstBox) boxIt.next();

                    if(!instToLabel.containsKey(box.getUnit()))
                        instToLabel.put(box.getUnit(), "label" + labelCount++);
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
                        instToLabel.get(trap.getBeginUnit()) + " to " + instToLabel.get(trap.getEndUnit()) +
                        " using " + instToLabel.get(trap.getHandlerUnit()));
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

            //assignColorsToLocals(body);
            
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
                Iterator instIt = instList.iterator();

                while(instIt.hasNext())
                {
                    Inst s = (Inst) instIt.next();

                    if(s instanceof IdentityInst && ((IdentityInst) s).getLeftOp() instanceof Local)
                    {
                        Local l = (Local) ((IdentityInst) s).getLeftOp();
                        IdentityRef identity = (IdentityRef) ((IdentityInst) s).getRightOp();

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
                        localToSlot.put(local, new Integer(localCount));
                        localCount += sizeOfType((Type)local.getType());
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
            Iterator codeIt = instList.iterator();

            isEmittingMethodCode = true;
            maxStackHeight = 0; 
            isNextGotoAJsr = false;

            while(codeIt.hasNext())
            {
                Inst s = (Inst) codeIt.next();

                if(instToLabel.containsKey(s))
                    emit(instToLabel.get(s) + ":");

                // emit this statement
                {
                    emitInst(s);
                }
            }

            isEmittingMethodCode = false;
            
            if (!Modifier.isNative(method.getModifiers())
                && !Modifier.isAbstract(method.getModifiers()))
              code.set(stackLimitIndex, "    .limit stack " + maxStackHeight);
        }

        // Emit epilogue
            emit(".end method");
	} catch (RuntimeException e) {
	    System.out.println(e);
	    Iterator unitIt = instList.iterator();
	    while(unitIt.hasNext()) {System.out.println(unitIt.next());}
	    throw e;
	    
	
	
	}

    }

    public void print(PrintWriter out)
    {
        Iterator it = code.iterator();

        while(it.hasNext())
            out.println(it.next());
    }

    void emitInst(Inst inst)
    {
        inst.apply(new InstSwitch()
        {
            public void caseReturnVoidInst(ReturnVoidInst i)
            {
                emit("return", 0);
            }

            public void caseReturnInst(ReturnInst i)
            {
                i.getOpType().apply(new TypeSwitch()
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

            public void caseNopInst(NopInst i) { emit ("nop", 0); }

            public void caseEnterMonitorInst(EnterMonitorInst i) 
            { 
                emit ("monitorenter", -1); 
            }
            
	    public void casePopInst(PopInst inst) 
		{
		    if(inst.getWordCount() == 2) {
			emit("pop2", -2);
		    }
		    else
			emit("pop", -1);
		}
		    

            public void caseExitMonitorInst(ExitMonitorInst i) 
            { 
                emit ("monitorexit", -1); 
            }

            public void caseGotoInst(GotoInst i)
            { 
                emit("goto " + instToLabel.get(i.getTarget()));
            }

            public void casePushInst(PushInst i)
            {
                if (i.getConstant() instanceof IntConstant)
                {
                    IntConstant v = (IntConstant)(i.getConstant());
                    if(v.value == -1)
                        emit("iconst_m1", 1);
                    else if(v.value >= 0 && v.value <= 5)
                        emit("iconst_" + v.value, 1);
                    else if(v.value >= Byte.MIN_VALUE && 
                            v.value <= Byte.MAX_VALUE)
                        emit("bipush " + v.value, 1);
                    else if(v.value >= Short.MIN_VALUE && 
                            v.value <= Short.MAX_VALUE)
                        emit("sipush " + v.value, 1);
                    else
                        emit("ldc " + v.toString(), 1);
                }
                else if (i.getConstant() instanceof StringConstant)
                {
                    emit("ldc " + i.getConstant().toString(), 1);
                }
                else if (i.getConstant() instanceof DoubleConstant)
                {
                    DoubleConstant v = (DoubleConstant)(i.getConstant());

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
                else if (i.getConstant() instanceof FloatConstant)
                {
                    FloatConstant v = (FloatConstant)(i.getConstant());
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
                else if (i.getConstant() instanceof LongConstant)
                {
                    LongConstant v = (LongConstant)(i.getConstant());
                    if(v.value == 0)
                        emit("lconst_0", 2);
                    else if(v.value == 1)
                        emit("lconst_1", 2);
                    else
                        emit("ldc2_w " + v.toString(), 2);
                }
                else if (i.getConstant() instanceof NullConstant)
                    emit("aconst_null", 1);
                else
                    throw new RuntimeException("unsupported opcode");
            }

            public void caseIdentityInst(IdentityInst i)
            {
                if(i.getRightOp() instanceof CaughtExceptionRef &&
                    i.getLeftOp() instanceof Local)
                {
                    int slot = ((Integer) localToSlot.get(i.getLeftOp())).intValue();

                    modifyStackHeight(1); // simulate the pushing of the exception onto the 
                                          // stack by the jvm
                    emit("astore " + slot, -1);
                }
            }

            public void caseStoreInst(StoreInst i)
            {
                    final int slot = 
                        ((Integer) localToSlot.get(i.getLocal())).intValue();

                    i.getOpType().apply(new TypeSwitch()
                    {
                        public void caseArrayType(ArrayType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("astore_" + slot, -1);
                            else
                                emit("astore " + slot, -1);
                        }

                        public void caseDoubleType(DoubleType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("dstore_" + slot, -2);
                            else
                                emit("dstore " + slot, -2);
                        }

                        public void caseFloatType(FloatType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("fstore_" + slot, -1);
                            else
                                emit("fstore " + slot, -1);
                        }

                        public void caseIntType(IntType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("istore_" + slot, -1);
                            else
                                emit("istore " + slot, -1);
                        }

                        public void caseLongType(LongType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("lstore_" + slot, -2);
                            else
                                emit("lstore " + slot, -2);
                        }

                        public void caseRefType(RefType t)
                        {
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
                            if(slot >= 0 && slot <= 3)
                                emit("astore_" + slot, -1);
                            else
                                emit("astore " + slot, -1);
                        }
                        
                        public void defaultCase(Type t)
                        {
                            throw new RuntimeException("Invalid local type:" 
                                                       + t);
                        }
                    });
            }

            public void caseLoadInst(LoadInst i)
            {
                final int slot = 
                    ((Integer) localToSlot.get(i.getLocal())).intValue();

                i.getOpType().apply(new TypeSwitch()
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
                        throw new 
                            RuntimeException("invalid local type to load" + t);
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

            public void caseArrayWriteInst(ArrayWriteInst i)
            {
                i.getOpType().apply(new TypeSwitch()
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
                    }});
                    
                }

            public void caseArrayReadInst(ArrayReadInst i)
            {
                i.getOpType().apply(new TypeSwitch()
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

            public void caseIfNullInst(IfNullInst i)
            {
                emit("ifnull " + instToLabel.get(i.getTarget()));
            }

            public void caseIfNonNullInst(IfNonNullInst i)
            {
                emit("ifnonnull " + instToLabel.get(i.getTarget()));
            }

            public void caseIfEqInst(IfEqInst i)
            {
                emit("ifeq " + instToLabel.get(i.getTarget()));
            }

            public void caseIfNeInst(IfNeInst i)
            {
                emit("ifne " + instToLabel.get(i.getTarget()));
            }

            public void caseIfGtInst(IfGtInst i)
            {
                emit("ifgt " + instToLabel.get(i.getTarget()));
            }

            public void caseIfGeInst(IfGeInst i)
            {
                emit("ifge " + instToLabel.get(i.getTarget()));
            }

            public void caseIfLtInst(IfLtInst i)
            {
                emit("iflt " + instToLabel.get(i.getTarget()));
            }

            public void caseIfLeInst(IfLeInst i)
            {
                emit("ifle " + instToLabel.get(i.getTarget()));
            }

            public void caseIfCmpEqInst(final IfCmpEqInst i)
            {
                i.getOpType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifeq " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifeq " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifeq " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpeq " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpeq " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpeq " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseIfCmpNeInst(final IfCmpNeInst i)
            {
                i.getOpType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifne " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifne " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifne " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpne " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpne " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpne " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseIfCmpGtInst(final IfCmpGtInst i)
            {
                i.getOpType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifgt " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifgt " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifgt " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpgt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpgt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpgt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseIfCmpGeInst(final IfCmpGeInst i)
            {
                i.getOpType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifge " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifge " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifge " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpge " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpge " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpge " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseIfCmpLtInst(final IfCmpLtInst i)
            {
                i.getOpType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("iflt " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("iflt " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("iflt " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmplt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmplt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmplt " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseIfCmpLeInst(final IfCmpLeInst i)
            {
                i.getOpType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg", -3);
                        emit("ifle " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp", -3);
                        emit("ifle " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg", -1);
                        emit("ifle " + 
                             instToLabel.get(i.getTarget()), -1);
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmple " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmple " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmple " + 
                             instToLabel.get(i.getTarget()), -2);
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid type");
                    }
                });
            }

            public void caseStaticGetInst(StaticGetInst i)
            {
                SootField field = i.getField();
                emit("getstatic " + 
                     slashify(field.getDeclaringClass().getName()) + "/" +
                     field.getName() + " " + 
                     jasminDescriptorOf(field.getType()),
                             sizeOfType(field.getType()));
            }

            public void caseStaticPutInst(StaticPutInst i)
            {
                emit("putstatic " + 
                     slashify(i.getField().getDeclaringClass().getName()) + 
                     "/" + i.getField().getName() + " " + 
                     jasminDescriptorOf(i.getField().getType()),
                             -sizeOfType(i.getField().getType()));
            }

            public void caseFieldGetInst(FieldGetInst i)
            {
                emit("getfield " + 
                     slashify(i.getField().getDeclaringClass().getName()) + 
                     "/" + i.getField().getName() + " " + 
                     jasminDescriptorOf(i.getField().getType()),
                     -1 + sizeOfType(i.getField().getType()));
            }

            public void caseFieldPutInst(FieldPutInst i)
            {
                emit("putfield " + 
                     slashify(i.getField().getDeclaringClass().getName()) + 
                     "/" + i.getField().getName() + " " + 
                     jasminDescriptorOf(i.getField().getType()),
                     -1 + -sizeOfType(i.getField().getType()));
            }

            public void caseInstanceCastInst(InstanceCastInst i)
            {
                Type castType = i.getCastType();

                if(castType instanceof RefType)
                    emit("checkcast " + slashify(castType.toBriefString()), 0);
                else if(castType instanceof ArrayType)
                    emit("checkcast " + jasminDescriptorOf(castType), 0);
            }

            public void caseInstanceOfInst(InstanceOfInst i)
            {
                Type checkType = i.getCheckType();

                if(checkType instanceof RefType)
                    emit("instanceof " + slashify(checkType.toBriefString()), 0);
                else if(checkType instanceof ArrayType)
                    emit("instanceof " + jasminDescriptorOf(checkType), 0);
            }

            public void caseNewInst(NewInst i)
            {
                emit("new "+slashify(i.getBaseType().toString()), 1);
            }

            public void casePrimitiveCastInst(PrimitiveCastInst i)
            {
                emit(i.toString(), i.getOutMachineCount() - i.getInMachineCount());
            }

            public void caseStaticInvokeInst(StaticInvokeInst i)
            {
                SootMethod m = i.getMethod();

                emit("invokestatic " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m),
                    -(argCountOf(m)) + sizeOfType(m.getReturnType()));
            }
            
            public void caseVirtualInvokeInst(VirtualInvokeInst i)
            {
                SootMethod m = i.getMethod();

                emit("invokevirtual " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m),
                    -(argCountOf(m) + 1) + sizeOfType(m.getReturnType()));
            }

            public void caseInterfaceInvokeInst(InterfaceInvokeInst i)
            {
                SootMethod m = i.getMethod();

                emit("invokeinterface " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m) + " " + (argCountOf(m) + 1),
                    -(argCountOf(m) + 1) + sizeOfType(m.getReturnType()));
            }

            public void caseSpecialInvokeInst(SpecialInvokeInst i)
            {
                SootMethod m = i.getMethod();

                emit("invokespecial " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m),
                    -(argCountOf(m) + 1) + sizeOfType(m.getReturnType()));
            }

            public void caseThrowInst(ThrowInst i)
            {
                emit("athrow", -1);
            }

            public void caseCmpInst(CmpInst i)
            {
                emit("lcmp", -3);
            }

            public void caseCmplInst(CmplInst i)
            {
                if(i.getOpType().equals(FloatType.v()))
                    emit("fcmpl", -1);
                else
                    emit("dcmpl", -3);
            }

            public void caseCmpgInst(CmpgInst i)
            {
                if(i.getOpType().equals(FloatType.v()))
                    emit("fcmpg", -1);
                else
                    emit("dcmpg", -3);
            }

            private void emitOpTypeInst(final String s, final OpTypeArgInst i)
            {
                i.getOpType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("i"+s, i.getOutMachineCount() - i.getInMachineCount());
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("l"+s, i.getOutMachineCount() - i.getInMachineCount());
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("d"+s, i.getOutMachineCount() - i.getInMachineCount());
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("f"+s, i.getOutMachineCount() - i.getInMachineCount());
                    }

                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for div");
                    }
                });
            }

            public void caseAddInst(AddInst i)
            {
                emitOpTypeInst("add", i);
            }

            public void caseDivInst(DivInst i)
            {
                emitOpTypeInst("div", i);
            }

            public void caseSubInst(SubInst i)
            {
                emitOpTypeInst("sub", i);
            }

            public void caseMulInst(MulInst i)
            {
                emitOpTypeInst("mul", i);
            }

            public void caseRemInst(RemInst i)
            {
                emitOpTypeInst("rem", i);
            }

            public void caseShlInst(ShlInst i)
            {
                emitOpTypeInst("shl", i);
            }

            public void caseAndInst(AndInst i)
            {
                emitOpTypeInst("and", i);
            }

            public void caseOrInst(OrInst i)
            {
                emitOpTypeInst("or", i);
            }

            public void caseXorInst(XorInst i)
            {
                emitOpTypeInst("xor", i);
            }

            public void caseShrInst(ShrInst i)
            {
                emitOpTypeInst("shr", i);
            }

            public void caseUshrInst(UshrInst i)
            {
                emitOpTypeInst("ushr", i);
            }

            public void caseIncInst(IncInst i)
            {
                emit("iinc " + ((Integer) localToSlot.get(i.getLocal())) + " " + i.getConstant());
            }

            public void caseArrayLengthInst(ArrayLengthInst i)
            {
                emit("arraylength", 1);
            }

            public void caseNegInst(NegInst i)
            {
                emitOpTypeInst("neg", i);
            }

            public void caseNewArrayInst(NewArrayInst i)
            {
                if(i.getBaseType() instanceof RefType)
                    emit("anewarray " + slashify(i.getBaseType().toBriefString()), 0);
                else if(i.getBaseType() instanceof ArrayType)
                    emit("anewarray " + jasminDescriptorOf(i.getBaseType()), 0);
                else
                    emit("newarray " + i.getBaseType().toBriefString(), 0);
            }

            public void caseNewMultiArrayInst(NewMultiArrayInst i)
            {
                emit("multianewarray " + jasminDescriptorOf(i.getBaseType()) + " " + 
                     i.getDimensionCount(), -i.getDimensionCount() + 1);
            }

            public void caseLookupSwitchInst(LookupSwitchInst i)
            {
                emit("lookupswitch", -1);

                List lookupValues = i.getLookupValues();
                List targets = i.getTargets();

                for(int j = 0; j < lookupValues.size(); j++)
                    emit("  " + lookupValues.get(j) + " : " + 
                         instToLabel.get(targets.get(j)));

                emit("  default : " + instToLabel.get(i.getDefaultTarget()));
            }

            public void caseTableSwitchInst(TableSwitchInst i)
		{
                emit("tableswitch " + i.getLowIndex() + " ; high = " + i.getHighIndex(), -1);

                List targets = i.getTargets();

                for(int j = 0; j < targets.size(); j++)
                    emit("  " + instToLabel.get(targets.get(j)));

                emit("default : " + instToLabel.get(i.getDefaultTarget()));
            }
            
            public void caseDup1Inst(Dup1Inst i)
	    {
		Type firstOpType = i.getOp1Type();
		if(firstOpType instanceof LongType || firstOpType instanceof DoubleType) 
		    emit("dup2", 2);
		else
		    emit("dup", 1);		
	    }

	    public void caseDup2Inst(Dup2Inst i)
	    {
		Type firstOpType = i.getOp1Type();
		Type secondOpType = i.getOp2Type();
		if(firstOpType instanceof LongType || firstOpType instanceof DoubleType) {
		    emit("dup2", 2);
		    if(secondOpType instanceof LongType || secondOpType instanceof DoubleType) {
			emit("dup2, 2");
		    } else 
			emit("dup", 1);
		} else if(secondOpType instanceof LongType || secondOpType instanceof DoubleType) {
		    if(firstOpType instanceof LongType || firstOpType instanceof DoubleType) {
			emit("dup2, 2");
		    } else 
			emit("dup", 1);
		    emit("dup2", 2);
		} else {
		    //delme[
		    System.out.println("3000:(JasminClass): dup2 created");
		    //delme
		    emit("dup2", 2);
		}
	    }

	    
	    public void caseDup1_x1Inst(Dup1_x1Inst i)
	    {
		Type opType = i.getOp1Type();
		Type underType = i.getUnder1Type();
		
		if(opType instanceof LongType || opType instanceof DoubleType) {
		    if(underType instanceof LongType || underType instanceof DoubleType) {
			emit("dup2_x2", 2);
		    } else 
			emit("dup2_x1", 2);
		} else {
		    if(underType instanceof LongType || underType instanceof DoubleType) 
			emit("dup_x2", 1);
		    else 
			emit("dup_x1", 1);
		}	
	    }
	    

	    public void caseDup1_x2Inst(Dup1_x2Inst i)
	    {
		throw new RuntimeException("undifined");
	    }

	    public void caseDup2_x1Inst(Dup2_x1Inst i)
	    {
		throw new RuntimeException("undifined");
	    }

	   

	    public void caseDup2_x2Inst(Dup2_x2Inst i)
	    {
		throw new RuntimeException("undifined");
	    }

            public void caseSwapInst(SwapInst i)
		{
		    emit("swap");
		}



        });
    }

}

class GroupIntPair
{
    Object group;
    int x;
    
    GroupIntPair(Object group, int x)
    {
        this.group = group;
        this.x = x;
    }
    
    public boolean equals(Object other)
    {
        if(other instanceof GroupIntPair)
            return ((GroupIntPair) other).group.equals(this.group) &&
                    ((GroupIntPair) other).x == this.x;
        else
            return false;
    }
    
    public int hashCode()
    {
        return group.hashCode() + 1013 * x;
    }
    
}
