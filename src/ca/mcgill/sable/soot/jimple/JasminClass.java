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
import ca.mcgill.sable.util.*;
import java.io.*;

class JasminClass
{
    Map stmtToLabel;
    Map localToSlot;
    Map subroutineToReturnAddressSlot;
    
    List code;

    boolean isEmittingMethodCode;
    int labelCount;

    boolean isNextGotoAJsr;
    int returnAddressSlot;
                
    String slashify(String s)
    {
        return s.replace('.', '/');
    }

    int sizeOfType(Type t)
    {
        if(t instanceof DoubleType || t instanceof LongType)
            return 2;
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
        if(isEmittingMethodCode && !s.endsWith(":"))
            code.add("    " + s);
        else
            code.add(s);
            
        if(Main.isVerbose)
            System.out.println(s);
    }

    public JasminClass(SootClass SootClass)
    {
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
    }
        
    void emitMethod(SootMethod method)
    {
        StmtListBody listBody = new StmtListBody(method.getInstListBody());
        StmtList stmtList = listBody.getStmtList();
        
        // Emit prologue
            emit(".method " + Modifier.toString(method.getModifiers()) + " " + 
                 method.getName() + jasminDescriptorOf(method));    

        subroutineToReturnAddressSlot = new HashMap(10, 0.7f);
        
        // Determine the stmtToLabel map
        {
            Iterator boxIt = listBody.getStmtBoxes().iterator();
                        
            stmtToLabel = new HashMap(stmtList.size() * 2 + 1, 0.7f);
            labelCount = 0;
            
            while(boxIt.hasNext())
            {
                // Assign a label for each statement reference
                {
                    StmtBox box = (StmtBox) boxIt.next();
                        
                    if(!stmtToLabel.containsKey(box.getStmt()))
                        stmtToLabel.put(box.getStmt(), "label" + labelCount++);
                }
            }
        }
   
        // Emit the exceptions
        {
            Iterator trapIt = listBody.getTrapTable().getTraps().iterator();
            
            while(trapIt.hasNext())
            {
                StmtTrap trap = (StmtTrap) trapIt.next();
                
                if(trap.getBeginStmt() != trap.getEndStmt())
                    emit(".catch " + slashify(trap.getException().getName()) + " from " + 
                        stmtToLabel.get(trap.getBeginStmt()) + " to " + stmtToLabel.get(trap.getEndStmt()) + 
                        " using " + stmtToLabel.get(trap.getHandlerStmt())); 
            }
        }
             
        /*
        // Emit the exceptions
        {
            ListIterator stmtIt = stmtList.listIterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                if(s instanceof BeginCatchStmt)
                {
                    BeginCatchStmt begin = (BeginCatchStmt) s;
                    EndCatchStmt end = null;
                    
                    // Determine end
                    {
                        ListIterator endList = stmtList.listIterator(stmtIt.nextIndex());
                        
                        while(endList.hasNext())
                        {
                            Stmt r = (Stmt) endList.next();
                            
                            if(r instanceof EndCatchStmt &&
                                begin.getException() == ((EndCatchStmt) r).getException())
                            {
                                end = (EndCatchStmt) r;
                                break;
                            }
                        }
                    }
                       
                    if(end == null)
                        throw new RuntimeException("unmatched BeginCatchStmt for " + begin.getException());
                        
                    // Output exception
                        
                }
            }
        }
                            */
                            
        // Determine where the locals go
        {
            int localCount = 0;
            int[] paramSlots = new int[method.getParameterCount()];
            int thisSlot = 0;
            Set assignedLocals = new HashSet();
            
            localToSlot = new HashMap(listBody.getLocalCount() * 2 + 1, 0.7f);
            
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
                Iterator stmtIt = stmtList.iterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
        
                    if(s instanceof IdentityStmt && ((IdentityStmt) s).getLeftOp() instanceof Local)
                    {
                        Local l = (Local) ((IdentityStmt) s).getLeftOp();
                        IdentityValue identity = (IdentityValue) ((IdentityStmt) s).getRightOp();
                        
                        if(identity instanceof ThisRef)
                        {
                            if(method.isStatic())
                                throw new RuntimeException("Attempting to use 'this' in static method");
                                
                            localToSlot.put(l, new Integer(thisSlot));
                            assignedLocals.add(l);
                        }
                        else if(identity instanceof ParameterRef)
                        {
                            localToSlot.put(l, new Integer(
                                paramSlots[((ParameterRef) identity).getIndex()]));
                            assignedLocals.add(l);
                        }
                    }
                }
            }
            
            // Assign the rest of the locals
            {
                Iterator localIt = listBody.getLocals().iterator();
                
                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();
                    
                    if(!assignedLocals.contains(local))
                    {   
                        localToSlot.put(local, new Integer(localCount));
                        localCount += sizeOfType(local.getType());
                        assignedLocals.add(local);
                    }
                }
                   
                emit("    .limit stack 20");
                emit("    .limit locals " + localCount);
            }
        }
        
        // Emit code in one pass
        {
            Iterator codeIt = stmtList.iterator();
            
            isEmittingMethodCode = true;
            isNextGotoAJsr = false;
            
            while(codeIt.hasNext())
            {
                Stmt s = (Stmt) codeIt.next();
                
                if(stmtToLabel.containsKey(s))
                    emit(stmtToLabel.get(s) + ":");
                    
                if(subroutineToReturnAddressSlot.containsKey(s))
                    emit("astore " + subroutineToReturnAddressSlot.get(s));
                    
                emitStmt(s);
            }
            
            isEmittingMethodCode = false;
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
        final Variable lvalue = (Variable) stmt.getLeftOp();
        final RValue rvalue = (RValue) stmt.getRightOp();
                        
        lvalue.apply(new ValueSwitch()
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
                        emit("aastore");
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dastore");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {   
                        emit("fastore");
                    }
                    
                    public void caseIntType(IntType t)
                    {
                        emit("iastore");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lastore");
                    }
                    
                    public void caseRefType(RefType t)
                    {   
                        emit("aastore");
                    }
                    
                    public void caseByteType(ByteType t)
                    {   
                        emit("bastore");
                    }
                    
                    public void caseBooleanType(BooleanType t)
                    {   
                        emit("bastore");
                    }
                    
                    public void caseCharType(CharType t)
                    {   
                        emit("castore");
                    }
                    
                    public void caseShortType(ShortType t)
                    {   
                        emit("sastore");
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
                    v.getField().getName() + " " + jasminDescriptorOf(v.getField().getType()));
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
                            emit("astore_" + slot);
                        else
                            emit("astore " + slot);
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emitValue(rvalue);
                        
                        if(slot >= 0 && slot <= 3)
                            emit("dstore_" + slot);
                        else
                            emit("dstore " + slot);
                    }
                    
                    public void caseFloatType(FloatType t)
                    {   
                        emitValue(rvalue);

                        if(slot >= 0 && slot <= 3)
                            emit("fstore_" + slot);
                        else
                            emit("fstore " + slot);
                    }
                    
                    public void caseIntType(IntType t)
                    {
                        emitValue(rvalue);
                        
                        if(slot >= 0 && slot <= 3)
                            emit("istore_" + slot);
                        else
                            emit("istore " + slot);
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emitValue(rvalue);
                        
                        if(slot >= 0 && slot <= 3)
                            emit("lstore_" + slot);
                        else
                            emit("lstore " + slot);
                    }
                    
                    public void caseRefType(RefType t)
                    {   
                        emitValue(rvalue);
                        
                        if(slot >= 0 && slot <= 3)
                            emit("astore_" + slot);
                        else
                            emit("astore " + slot);
                    }
                    
                    public void caseStmtAddressType(StmtAddressType t)
                    {
                        isNextGotoAJsr = true;
                        returnAddressSlot = slot;
                    }

                    public void caseNullType(NullType t)
                    {
                        emitValue(rvalue);
                        
                        if(slot >= 0 && slot <= 3)
                            emit("astore_" + slot);
                        else
                            emit("astore " + slot);
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
                    field.getName() + " " + jasminDescriptorOf(field.getType()));
            }
        });                
    }

    void emitIfStmt(IfStmt stmt)
    {
        Condition cond = stmt.getCondition();
        
        final Value op1 = ((BinopExpr) cond).getOp1();
        final Value op2 = ((BinopExpr) cond).getOp2();
        final String label = (String) stmtToLabel.get(stmt.getTarget());
        
        emitValue(op1);
        emitValue(op2);

        
        cond.apply(new ValueSwitch()
        {
            public void caseEqExpr(EqExpr expr)
            {
                op1.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpeq " + label);
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifeq " + label);
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifeq " + label);
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifeq " + label);
                    }
                    
                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpeq " + label);
                    }
                    
                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpeq " + label);
                    }                   
                    
                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpeq " + label);
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
                        emit("if_icmpne " + label);
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifne " + label);
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifne " + label);
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifne " + label);
                    }
                    
                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpne " + label);
                    }
                    
                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpne " + label);
                    }                    
                    
                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpne " + label);
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
                        emit("if_icmplt " + label);
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("iflt " + label);
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("iflt " + label);
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("iflt " + label);
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
                        emit("if_icmple " + label);
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifle " + label);
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifle " + label);
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifle " + label);
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
                        emit("if_icmpgt " + label);
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifgt " + label);
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifgt " + label);
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifgt " + label);
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
                        emit("if_icmpge " + label);
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifge " + label);
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifge " + label);
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifge " + label);
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
        stmt.apply(new StmtSwitch()
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
                    
                    emit("astore " + slot);
                }
            }
            
            public void caseBreakpointStmt(BreakpointStmt s)
            {
                emit("breakpoint");
            }
            
            public void caseInvokeStmt(InvokeStmt s)
            {
                emitValue(s.getInvokeExpr());
                
                Type returnType = ((InvokeExpr) s.getInvokeExpr()).getMethod().getReturnType();
                
                if(!returnType.equals(VoidType.v()))
                {
                    // Need to do some cleanup because this value is not used.
                    
                    if(sizeOfType(returnType) == 1)
                        emit("pop");
                    else
                        emit("pop2");
                }
            }
            
            public void defaultCase(Stmt s)
            {
                throw new RuntimeException("invalid stmt: " + s);
            }
            
            public void caseEnterMonitorStmt(EnterMonitorStmt s)
            {
                emitValue(s.getOp());
                emit("monitorenter");
            }
            
            public void caseExitMonitorStmt(ExitMonitorStmt s)
            {
                emitValue(s.getOp());
                emit("monitorexit");
            }
            
            public void caseGotoStmt(GotoStmt s)
            {
                if(isNextGotoAJsr)
                {
                    emit("jsr " + stmtToLabel.get(s.getTarget()));
                    isNextGotoAJsr = true;
                    
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
                emit("lookupswitch");
                        
                List lookupValues = s.getLookupValues();
                List targets = s.getTargets();
                
                for(int i = 0; i < lookupValues.size(); i++)
                    emit("  " + lookupValues.get(i) + " : " + stmtToLabel.get(targets.get(i)));
                    
                emit("  default : " + stmtToLabel.get(s.getDefaultTarget()));        
            }
            
            public void caseNopStmt(NopStmt s)
            {   
                emit("nop");
            }

            public void caseRetStmt(RetStmt s)
            {
                emit("ret " + localToSlot.get(s.getStmtAddress()));
            }
            
            public void caseReturnStmt(ReturnStmt s)
            {
                emitValue(s.getReturnValue());
                
                Value returnValue = s.getReturnValue();
                
                returnValue.getType().apply(new TypeSwitch()
                {
                    public void defaultCase(Type t)
                    { 
                        throw new RuntimeException("invalid return type" + t.toString());
                     }
                     
                     public void caseDoubleType(DoubleType t)
                     {
                        emit("dreturn");
                     }
                     
                     public void caseFloatType(FloatType t)
                     {
                        emit("freturn");
                     }
                     
                     public void caseIntType(IntType t)
                     {
                        emit("ireturn");
                     }
                     
                     public void caseLongType(LongType t)
                     {
                        emit("lreturn");
                     }
                     
                     public void caseArrayType(ArrayType t)
                     {
                        emit("areturn");
                     }
                     
                     public void caseRefType(RefType t)
                     {
                        emit("areturn");
                     }
                     
                     public void caseNullType(NullType t)
                     {
                        emit("areturn");
                     }
                     
                });
            }

            public void caseReturnVoidStmt(ReturnVoidStmt s)
            {
                emit("return");
            }
            
            public void caseTableSwitchStmt(TableSwitchStmt s)
            {
                emitValue(s.getKey());
                emit("tableswitch " + s.getLowIndex() + " ; high = " + s.getHighIndex());
                
                List targets = s.getTargets();
                
                for(int i = 0; i < targets.size(); i++)
                    emit("  " + stmtToLabel.get(targets.get(i)));
                    
                emit("default : " + stmtToLabel.get(s.getDefaultTarget()));
            }
            
            public void caseThrowStmt(ThrowStmt s)
            {
                emitValue(s.getOp());
                emit("athrow");
            }
        });
    }
    
    void emitValue(Value value)
    {
        value.apply(new ValueSwitch()
        {  
            public void caseAddExpr(AddExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());
                
                v.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("iadd");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("ladd");
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dadd");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fadd");
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
                    public void caseIntType(IntType t)
                    {
                        emit("iadd");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("ladd");
                    }
                    
                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for and");
                    }
                });
            }
            
            public void caseArrayRef(ArrayRef v)
            {
                Type baseType = ((ArrayType) v.getBase().getType()).baseType;
                
                emitValue(v.getBase());
                emitValue(v.getIndex());
                
                v.getType().apply(new TypeSwitch()
                {
                    public void caseArrayType(ArrayType ty)
                    {
                        emit("aaload");
                    }
                        
                    public void caseBooleanType(BooleanType ty)
                    {
                        emit("baload");
                    }
                        
                    public void caseByteType(ByteType ty)
                    {
                        emit("baload");
                    }
                        
                    public void caseCharType(CharType ty)
                    {
                        emit("caload");
                    }
                        
                    public void defaultCase(Type ty)
                    {
                        throw new RuntimeException("invalid base type");
                    }
                            
                    public void caseDoubleType(DoubleType ty)
                    {
                        emit("daload");
                    }
                            
                    public void caseFloatType(FloatType ty)
                    {
                        emit("faload");
                    }
                            
                    public void caseIntType(IntType ty)
                    {
                        emit("iaload");
                    }
                            
                    public void caseLongType(LongType ty)
                    {
                        emit("laload");
                    }
                            
                    public void caseRefType(RefType ty)
                    {
                        emit("aaload");
                    }
                            
                    public void caseShortType(ShortType ty)
                    {
                        emit("saload");
                    }
                });
            }
            
            public void caseCastExpr(final CastExpr v)
            {
                final Type toType = v.getCastType();
                final Type fromType = v.getOp().getType();
                
                emitValue(v.getOp());
                
                if(toType instanceof RefType)
                    emit("checkcast " + slashify(toType.toString()));
                else if(toType instanceof ArrayType)
                    emit("checkcast " + jasminDescriptorOf(toType));  
                else {
                    fromType.apply(new TypeSwitch()
                    {
                        public void defaultCase(Type ty)
                        {
                            throw new RuntimeException("invalid from type" + fromType);
                        }
                                
                        public void caseDoubleType(DoubleType ty)
                        {
                            if(toType.equals(IntType.v()))
                                emit("d2i");
                            else if(toType.equals(LongType.v()))
                                emit("d2l");
                            else if(toType.equals(FloatType.v()))
                                emit("d2f");
                            else
                                throw new RuntimeException("invalid toType from double: " + toType);
                        }
                                
                        public void caseFloatType(FloatType ty)
                        {
                            if(toType.equals(IntType.v()))
                                emit("f2i");
                            else if(toType.equals(LongType.v()))
                                emit("f2l");
                            else if(toType.equals(DoubleType.v()))
                                emit("f2d");
                            else
                                throw new RuntimeException("invalid toType from float: " + toType);
                        }
                                
                        public void caseIntType(IntType ty)
                        {
                            if(toType.equals(ByteType.v()))
                                emit("i2b");
                            else if(toType.equals(CharType.v()))
                                emit("i2c");
                            else if(toType.equals(ShortType.v()))
                                emit("i2s");
                            else if(toType.equals(FloatType.v()))
                                emit("i2f");
                            else if(toType.equals(LongType.v()))
                                emit("i2l");
                            else if(toType.equals(DoubleType.v()))
                                emit("i2d");                                
                            else
                                throw new RuntimeException("invalid toType from int: " + toType + 
                                    " " + v.toString());
                        }
                                
                        public void caseLongType(LongType ty)
                        {
                            if(toType.equals(IntType.v()))
                                emit("l2i");
                            else if(toType.equals(FloatType.v()))
                                emit("l2f");
                            else if(toType.equals(DoubleType.v()))
                                emit("l2d");
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
                emit("lcmp");
            }
            
            public void caseCmpgExpr(CmpgExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());
                
                if(v.getOp1().getType().equals(FloatType.v()))
                    emit("fcmpg");
                else
                    emit("dcmpg");
            }
            
            public void caseCmplExpr(CmplExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());
                
                if(v.getOp1().getType().equals(FloatType.v()))
                    emit("fcmpl");
                else
                    emit("dcmpl");
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
                    public void caseIntType(IntType t)
                    {
                        emit("idiv");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("ldiv");
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("ddiv");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fdiv");
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
                    emit("dconst_0");
                else if(v.value == 1)
                    emit("dconst_1");
                else 
                    emit("ldc2_w " + v.value);
            }
            
            public void caseFloatConstant(FloatConstant v)
            {
                if(v.value == 0)
                    emit("fconst_0");
                else if(v.value == 1)
                    emit("fconst_1");
                else if(v.value == 2)
                    emit("fconst_2");
                else
                    emit("ldc " + v.value);
            }
            
            
            public void caseInstanceFieldRef(InstanceFieldRef v)
            {
                emitValue(v.getBase());
                
                emit("getfield " + slashify(v.getField().getDeclaringClass().getName()) + "/" + 
                    v.getField().getName() + " " + jasminDescriptorOf(v.getField().getType()));
            }
            
            public void caseInstanceOfExpr(InstanceOfExpr v)
            {
                emitValue(v.getOp());
                
                emit("instanceof " + slashify(v.getCheckType().toString()));
            }
            
            public void caseIntConstant(IntConstant v)
            {
                if(v.value == -1)
                    emit("iconst_m1");
                else if(v.value >= 0 && v.value <= 5)
                    emit("iconst_" + v.value);
                else
                    emit("ldc " + v.value);
            }
            
            public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v)
            {
                SootMethod m = v.getMethod();
                
                emitValue(v.getBase());
                
                for(int i = 0; i < m.getParameterCount(); i++)
                    emitValue(v.getArg(i)); 
                    
                emit("invokeinterface " + slashify(m.getDeclaringClass().getName()) + "/" + 
                    m.getName() + jasminDescriptorOf(m) + " " + (argCountOf(m) + 1));
            }
                        
            public void caseLengthExpr(LengthExpr v)
            {
                emitValue(v.getOp());
                emit("arraylength");
            }
            
            public void caseLocal(Local v)
            {
                final int slot = ((Integer) localToSlot.get(v)).intValue();
                
                v.getType().apply(new TypeSwitch()
                {
                    public void caseArrayType(ArrayType t)
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("aload_" + slot);
                        else
                            emit("aload " + slot);                        
                    }
                                        
                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("invalid local type to load" + t);
                    }
                    
                    public void caseDoubleType(DoubleType t) 
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("dload_" + slot);
                        else
                            emit("dload " + slot);
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("fload_" + slot);
                        else
                            emit("fload " + slot);
                    }
                    
                    public void caseIntType(IntType t) 
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("iload_" + slot);
                        else
                            emit("iload " + slot);
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("lload_" + slot);
                        else 
                            emit("lload " + slot);
                    }
                    
                    public void caseRefType(RefType t)
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("aload_" + slot);
                        else
                            emit("aload " + slot);
                    }                    
                    
                    public void caseNullType(NullType t)
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("aload_" + slot);
                        else
                            emit("aload " + slot);                        
                    }
                });
                
            }
            
            public void caseLongConstant(LongConstant v)
            {
                if(v.value == 0)
                    emit("lconst_0");
                else if(v.value == 1)
                    emit("lconst_1");
                else
                    emit("ldc2_w " + v.value);
            }
            
            
            public void caseMulExpr(MulExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());
                
                v.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("imul");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lmul");
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dmul");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fmul");
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
                        emit("dcmpg");
                        emitBooleanBranch("iflt");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emitBooleanBranch("iflt");
                    }
                    
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmplt");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
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
                        emit("dcmpg");
                        emitBooleanBranch("ifle");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emitBooleanBranch("ifle");
                    }
                    
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmple");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
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
                        emit("dcmpg");
                        emitBooleanBranch("ifgt");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emitBooleanBranch("ifgt");
                    }
                    
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpgt");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
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
                        emit("dcmpg");
                        emitBooleanBranch("ifge");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emitBooleanBranch("ifge");
                    }
                    
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpge");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
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
                        emit("dcmpg");
                        emit("iconst_0");
                        emitBooleanBranch("if_icmpne");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("iconst_0");
                        emitBooleanBranch("if_icmpne");
                    }
                    
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpne");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("iconst_0");
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
                        emit("dcmpg");
                        emit("iconst_0");
                        emitBooleanBranch("if_icmpeq");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("iconst_0");
                        emitBooleanBranch("if_icmpeq");
                    }
                    
                    public void caseIntType(IntType t)
                    {
                        emit("if_icmpeq");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("iconst_0");
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
            
            public void caseNegExpr(NegExpr v)
            {
                emitValue(v.getOp());
                
                v.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("ineg");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lneg");
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dneg");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fneg");
                    }
                    
                    public void defaultCase(Type t)
                    {
                        throw new RuntimeException("Invalid argument type for neg");
                    }
                });
                
            }
            
            public void caseNewArrayExpr(NewArrayExpr v)
            {
                Immediate size = v.getSize();
                
                emitValue(size);
                
                if(v.getBaseType() instanceof RefType)
                    emit("anewarray " + slashify(v.getBaseType().toString()));
                else if(v.getBaseType() instanceof ArrayType)
                    emit("anewarray " + jasminDescriptorOf(v.getBaseType())); 
                else
                    emit("newarray " + v.getBaseType().toString());
            }
            
            public void caseNewMultiArrayExpr(NewMultiArrayExpr v)
            {
                List sizes = v.getSizes();
                
                for(int i = 0; i < sizes.size(); i++)
                    emitValue((Immediate) sizes.get(i));
                
                emit("multianewarray " + jasminDescriptorOf(v.getBaseType()) + " " + sizes.size());
            }
            
            public void caseNewExpr(NewExpr v)
            {
                emit("new " + slashify(v.getBaseType().toString()));   
            }
            
            
            public void caseNullConstant(NullConstant v)
            {
                emit("aconst_null");
            }
            
            public void caseOrExpr(OrExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());
                
                v.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("ior");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lor");
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
                    public void caseIntType(IntType t)
                    {
                        emit("irem");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lrem");
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("drem");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("frem");
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
                    public void caseIntType(IntType t)
                    {
                        emit("ishl");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lshl");
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
                    public void caseIntType(IntType t)
                    {
                        emit("ishr");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lshr");
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
                    m.getName() + jasminDescriptorOf(m));
            }
            
            public void caseStaticInvokeExpr(StaticInvokeExpr v)
            {
                SootMethod m = v.getMethod();
                
                for(int i = 0; i < m.getParameterCount(); i++)
                    emitValue(v.getArg(i)); 
                    
                emit("invokestatic " + slashify(m.getDeclaringClass().getName()) + "/" + 
                    m.getName() + jasminDescriptorOf(m));
            }
            
            public void caseStaticFieldRef(StaticFieldRef v)
            {
                emit("getstatic " + slashify(v.getField().getDeclaringClass().getName()) + "/" + 
                    v.getField().getName() + " " + jasminDescriptorOf(v.getField().getType()));
            }
            
            public void caseStringConstant(StringConstant v)
            {
                StringBuffer src = new StringBuffer(v.value);
                StringBuffer dest = new StringBuffer();
                
                for(int i = 0; i < src.length(); i++)
                {
                    if(src.charAt(i) == '\"')
                        dest.append("\\\"");
                    else
                        dest.append(src.charAt(i));
                }
                
                emit("ldc " + '"' + dest.toString() + '"');
            }

            public void caseSubExpr(SubExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());
                
                v.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("isub");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lsub");
                    }
                    
                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dsub");
                    }
                    
                    public void caseFloatType(FloatType t)
                    {
                        emit("fsub");
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
                    public void caseIntType(IntType t)
                    {
                        emit("iushr");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lushr");
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
                    m.getName() + jasminDescriptorOf(m));
            }
            
            public void caseXorExpr(XorExpr v)
            {
                emitValue(v.getOp1());
                emitValue(v.getOp2());
                
                v.getType().apply(new TypeSwitch()
                {
                    public void caseIntType(IntType t)
                    {
                        emit("ixor");
                    }
                    
                    public void caseLongType(LongType t)
                    {
                        emit("lxor");
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
        emit(s + " label" + labelCount);
        emit("iconst_0");
        emit("goto label" + labelCount+1);
        emit("label" + labelCount++ + ":");
        emit("iconst_1");
        emit("label" + labelCount++ + ":");
    }
        
}






