/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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





package soot.baf;

import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.*;
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


    Map blockToStackHeight = new HashMap(); // maps a block to the stack height upon entering it
    Map blockToLogicalStackHeight = new HashMap(); // maps a block to the logical stack height upon entering it
    

    String slashify(String s)
    {
        return s.replace('.', '/');
    }

    public static int sizeOfType(Type t)
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
    }
    
    public JasminClass(SootClass sootClass)
    {
        if(soot.Main.isProfilingOptimization)
            soot.Main.buildJasminTimer.start();
        
        if(soot.Main.isVerbose)
            System.out.println("[" + sootClass.getName() + "] Constructing baf.JasminClass...");

        code = new LinkedList();

        // Emit the header
        {
            int modifiers = sootClass.getModifiers();

            if(Modifier.isInterface(modifiers))
                {
                modifiers -= Modifier.INTERFACE;

                emit(".interface " + Modifier.toString(modifiers) + " " + slashify(sootClass.getName()));
            }
            else
                emit(".class " + Modifier.toString(modifiers) + " " + slashify(sootClass.getName()));

            if(sootClass.hasSuperclass())
                emit(".super " + slashify(sootClass.getSuperclass().getName()));
            else
                emit(".super " + slashify(sootClass.getName()));

            emit("");
        }

        // Emit the interfaces
        {
            Iterator interfaceIt = sootClass.getInterfaces().iterator();

            while(interfaceIt.hasNext())
            {
                SootClass inter = (SootClass) interfaceIt.next();

                emit(".implements " + slashify(inter.getName()));
            }

            if(sootClass.getInterfaceCount() != 0)
                emit("");
        }





	// emit class attributes.
	Iterator it =  sootClass.getTags().iterator(); 
	while(it.hasNext()) {
	    Tag tag = (Tag) it.next();
	    if(tag instanceof Attribute)
		emit(".class_attribute "  + tag.getName() + " " + Base64.encode(((Attribute)tag).getValue()) + "");
	}





        // Emit the fields
        {
            Iterator fieldIt = sootClass.getFields().iterator();

            while(fieldIt.hasNext())
            {
                SootField field = (SootField) fieldIt.next();

                emit(".field " + Modifier.toString(field.getModifiers()) + " " +
                     "\"" + field.getName() + "\"" + " " + jasminDescriptorOf(field.getType()));


		Iterator attributeIt =  field.getTags().iterator(); 
		while(attributeIt.hasNext()) {
		    Tag tag = (Tag) attributeIt.next();
		    if(tag instanceof Attribute)
			emit(".field_attribute " + tag.getName() + " " + Base64.encode(((Attribute)tag).getValue()));
		}

            }

            if(sootClass.getFieldCount() != 0)
                emit("");
        }

        // Emit the methods
        {
            Iterator methodIt = sootClass.getMethods().iterator();

            while(methodIt.hasNext())
            {
                emitMethod((SootMethod) methodIt.next());
                emit("");
            }
        }
        
        if(soot.Main.isProfilingOptimization)
            soot.Main.buildJasminTimer.end();
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
//              FastColorer.assignColorsToLocals(body, localToGroup,
//                  localToColor, groupToColorCount);

        if(Main.isProfilingOptimization)
            Main.packTimer.end();
                    
    }

    void emitMethod(SootMethod method)
    {
       if (method.isPhantom())
           return;

       // Emit prologue
            emit(".method " + Modifier.toString(method.getModifiers()) + " " +
                 method.getName() + jasminDescriptorOf(method));

       if(method.isConcrete())
       {
            if(!method.hasActiveBody())
                throw new RuntimeException("method: " + method.getName() + " has no active body!");
            else
                emitMethodBody(method);
       }
       
       // Emit epilogue
            emit(".end method");

	    Iterator it =  method.getTags().iterator();
	    while(it.hasNext()) {
		Tag tag = (Tag) it.next();
		if(tag instanceof Attribute)
		    emit(".method_attribute "  + tag.getName() + " " + Base64.encode(tag.getValue()));
	    }	    
    }
    
    void emitMethodBody(SootMethod method)
    {
        if(soot.Main.isProfilingOptimization)
            soot.Main.buildJasminTimer.end();
        
        Body activeBody = method.getActiveBody();
        
        if(!(activeBody instanceof BafBody))
            throw new RuntimeException("method: " + method.getName() + " has an invalid active body!");
        
        BafBody body = (BafBody) activeBody;
        
        if(body == null)
            throw new RuntimeException("method: " + method.getName() + " has no active body!");
            
        if(soot.Main.isProfilingOptimization)
            soot.Main.buildJasminTimer.start();
        
        Chain instList = body.getUnits();

        int stackLimitIndex = -1;
        

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
            
            // calcualte max stack height
            {
                maxStackHeight = 0;
                if(activeBody.getUnits().size() !=  0 ) {
                    BlockGraph blockGraph = new BriefBlockGraph(activeBody);

                
                    List blocks = blockGraph.getBlocks();
                

                    if(blocks.size() != 0) {
                        Block b = (Block) blocks.get(0);                
                    
                        // set the stack height of the entry points
                        List entryPoints = ((DirectedGraph)blockGraph).getHeads();                
                        Iterator entryIt = entryPoints.iterator();
                        while(entryIt.hasNext()) {
                            Block entryBlock = (Block) entryIt.next();
                            Integer initialHeight;
                            if(entryBlock == b) {
                                initialHeight = new Integer(0);
                            } else {
                                initialHeight = new Integer(1);
                            }                                                
                            blockToStackHeight.put(entryBlock, initialHeight);
                            blockToLogicalStackHeight.put(entryBlock, initialHeight); 
                        }                
                                    
                        // dfs the block graph using the blocks in the entryPoints list  as roots 
                        entryIt = entryPoints.iterator();
                        while(entryIt.hasNext()) {
                            Block nextBlock = (Block) entryIt.next();
                            calculateStackHeight(nextBlock);
                            calculateLogicalStackHeightCheck(nextBlock);
                        }                
                    }
                }
            }
            if (!Modifier.isNative(method.getModifiers())
                && !Modifier.isAbstract(method.getModifiers()))
                code.set(stackLimitIndex, "    .limit stack " + maxStackHeight);
        }

	// emit code attributes
	{
	    Iterator it =  body.getTags().iterator();
	    while(it.hasNext()) {
		Tag t = (Tag) it.next();
		if(t instanceof JasminAttribute) {
		    emit(".code_attribute " + t.getName() +" \"" + ((JasminAttribute) t).getJasminValue(instToLabel) +"\"");
		}		
	    }
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
                emit("return");
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

                     public void caseByteType(ByteType t)
                     {
                        emit("ireturn");
                     }

                     public void caseShortType(ShortType t)
                     {
                        emit("ireturn");
                     }

                     public void caseCharType(CharType t)
                     {
                        emit("ireturn");
                     }

                     public void caseBooleanType(BooleanType t)
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

            public void caseNopInst(NopInst i) { emit ("nop"); }

            public void caseEnterMonitorInst(EnterMonitorInst i) 
            { 
                emit ("monitorenter"); 
            }
            
            public void casePopInst(PopInst i) 
                {
                    if(i.getWordCount() == 2) {
                        emit("pop2");
                    }
                    else
                        emit("pop");
                }
                    

            public void caseExitMonitorInst(ExitMonitorInst i) 
            { 
                emit ("monitorexit"); 
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
                        emit("iconst_m1");
                    else if(v.value >= 0 && v.value <= 5)
                        emit("iconst_" + v.value);
                    else if(v.value >= Byte.MIN_VALUE && 
                            v.value <= Byte.MAX_VALUE)
                        emit("bipush " + v.value);
                    else if(v.value >= Short.MIN_VALUE && 
                            v.value <= Short.MAX_VALUE)
                        emit("sipush " + v.value);
                    else
                        emit("ldc " + v.toString());
                }
                else if (i.getConstant() instanceof StringConstant)
                {
                    emit("ldc " + i.getConstant().toString());
                }
                else if (i.getConstant() instanceof DoubleConstant)
                {
                    DoubleConstant v = (DoubleConstant)(i.getConstant());

                    if(v.value == 0)
                        emit("dconst_0");
                    else if(v.value == 1)
                        emit("dconst_1");
                    else {
                        String s = v.toString();
                        
                        if(s.equals("#Infinity"))
                            s="+DoubleInfinity";
                        
                        if(s.equals("#-Infinity"))
                            s="-DoubleInfinity";
                        
                        if(s.equals("#NaN"))
                            s="+DoubleNaN";
                        
                        emit("ldc2_w " + s);
                    }
                }
                else if (i.getConstant() instanceof FloatConstant)
                {
                    FloatConstant v = (FloatConstant)(i.getConstant());
                    if(v.value == 0)
                        emit("fconst_0");
                    else if(v.value == 1)
                        emit("fconst_1");
                    else if(v.value == 2)
                        emit("fconst_2");
                    else {
                        String s = v.toString();
                        
                        if(s.equals("#InfinityF"))
                            s="+FloatInfinity";
                        if(s.equals("#-InfinityF"))
                            s="-FloatInfinity";
                        
                        if(s.equals("#NaNF"))
                            s="+FloatNaN";
                        
                        emit("ldc " + s);
                    }
                }
                else if (i.getConstant() instanceof LongConstant)
                {
                    LongConstant v = (LongConstant)(i.getConstant());
                    if(v.value == 0)
                        emit("lconst_0");
                    else if(v.value == 1)
                        emit("lconst_1");
                    else
                        emit("ldc2_w " + v.toString());
                }
                else if (i.getConstant() instanceof NullConstant)
                    emit("aconst_null");
                else
                    throw new RuntimeException("unsupported opcode");
            }

            public void caseIdentityInst(IdentityInst i)
            {
                if(i.getRightOp() instanceof CaughtExceptionRef &&
                    i.getLeftOp() instanceof Local)
                {
                    int slot = ((Integer) localToSlot.get(i.getLeftOp())).intValue();

                    if(slot >= 0 && slot <= 3)
                        emit("astore_" + slot);
                    else
                        emit("astore " + slot);
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
                                emit("astore_" + slot);
                            else
                                emit("astore " + slot);
                        }

                        public void caseDoubleType(DoubleType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("dstore_" + slot);
                            else
                                emit("dstore " + slot);
                        }

                        public void caseFloatType(FloatType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("fstore_" + slot);
                            else
                                emit("fstore " + slot);
                        }

                        public void caseIntType(IntType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("istore_" + slot);
                            else
                                emit("istore " + slot);
                        }

			public void caseByteType(ByteType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("istore_" + slot);
                            else
                                emit("istore " + slot);
                        }

			public void caseShortType(ShortType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("istore_" + slot);
                            else
                                emit("istore " + slot);
                        }

			public void caseCharType(CharType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("istore_" + slot);
                            else
                                emit("istore " + slot);
                        }

			public void caseBooleanType(BooleanType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("istore_" + slot);
                            else
                                emit("istore " + slot);
                        }

                        public void caseLongType(LongType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("lstore_" + slot);
                            else
                                emit("lstore " + slot);
                        }

                        public void caseRefType(RefType t)
                        {
                            if(slot >= 0 && slot <= 3)
                                emit("astore_" + slot);
                            else
                                emit("astore " + slot);
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
                                emit("astore_" + slot);
                            else
                                emit("astore " + slot);
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
                            emit("aload_" + slot);
                        else
                            emit("aload " + slot);
                    }
            
                    public void defaultCase(Type t)
                    {
                        throw new 
                            RuntimeException("invalid local type to load" + t);
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

		    public void caseByteType(ByteType t)
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("iload_" + slot);
                        else
                            emit("iload " + slot);
                    }

		    public void caseShortType(ShortType t)
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("iload_" + slot);
                        else
                            emit("iload " + slot);
                    }

		    public void caseCharType(CharType t)
                    {
                        if(slot >= 0 && slot <= 3)
                            emit("iload_" + slot);
                        else
                            emit("iload " + slot);
                    }

		    public void caseBooleanType(BooleanType t)
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

            public void caseArrayWriteInst(ArrayWriteInst i)
            {
                i.getOpType().apply(new TypeSwitch()
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
                    }});
                    
                }

            public void caseArrayReadInst(ArrayReadInst i)
            {
                i.getOpType().apply(new TypeSwitch()
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

                    public void caseNullType(NullType ty)
                    {
                        emit("aaload");
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
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpeq " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpeq " + 
                             instToLabel.get(i.getTarget()));
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
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpne " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpne " + 
                             instToLabel.get(i.getTarget()));
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
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpgt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpgt " + 
                             instToLabel.get(i.getTarget()));
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
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmpge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmpge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmpge " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmpge " + 
                             instToLabel.get(i.getTarget()));
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
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmplt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("iflt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("iflt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("iflt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmplt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmplt " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmplt " + 
                             instToLabel.get(i.getTarget()));
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
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseBooleanType(BooleanType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseShortType(ShortType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseCharType(CharType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseByteType(ByteType t)
                    {
                        emit("if_icmple " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("dcmpg");
                        emit("ifle " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseLongType(LongType t)
                    {
                        emit("lcmp");
                        emit("ifle " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("fcmpg");
                        emit("ifle " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseArrayType(ArrayType t)
                    {
                        emit("if_acmple " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseRefType(RefType t)
                    {
                        emit("if_acmple " + 
                             instToLabel.get(i.getTarget()));
                    }

                    public void caseNullType(NullType t)
                    {
                        emit("if_acmple " + 
                             instToLabel.get(i.getTarget()));
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
                     jasminDescriptorOf(field.getType()));
            }

            public void caseStaticPutInst(StaticPutInst i)
            {
                emit("putstatic " + 
                     slashify(i.getField().getDeclaringClass().getName()) + 
                     "/" + i.getField().getName() + " " + 
                     jasminDescriptorOf(i.getField().getType()));
            }

            public void caseFieldGetInst(FieldGetInst i)
            {
                emit("getfield " + 
                     slashify(i.getField().getDeclaringClass().getName()) + 
                     "/" + i.getField().getName() + " " + 
                     jasminDescriptorOf(i.getField().getType()));
            }

            public void caseFieldPutInst(FieldPutInst i)
            {
                emit("putfield " + 
                     slashify(i.getField().getDeclaringClass().getName()) + 
                     "/" + i.getField().getName() + " " + 
                     jasminDescriptorOf(i.getField().getType()));
            }

            public void caseInstanceCastInst(InstanceCastInst i)
            {
                Type castType = i.getCastType();

                if(castType instanceof RefType)
                    emit("checkcast " + slashify(castType.toBriefString()));
                else if(castType instanceof ArrayType)
                    emit("checkcast " + jasminDescriptorOf(castType));
            }

            public void caseInstanceOfInst(InstanceOfInst i)
            {
                Type checkType = i.getCheckType();

                if(checkType instanceof RefType)
                    emit("instanceof " + slashify(checkType.toBriefString()));
                else if(checkType instanceof ArrayType)
                    emit("instanceof " + jasminDescriptorOf(checkType));
            }

            public void caseNewInst(NewInst i)
            {
                emit("new "+slashify(i.getBaseType().toString()));
            }

            public void casePrimitiveCastInst(PrimitiveCastInst i)
            {
                emit(i.toString());
            }

            public void caseStaticInvokeInst(StaticInvokeInst i)
            {
                SootMethod m = i.getMethod();

                emit("invokestatic " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m));
            }
            
            public void caseVirtualInvokeInst(VirtualInvokeInst i)
            {
                SootMethod m = i.getMethod();

                emit("invokevirtual " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m));
            }

            public void caseInterfaceInvokeInst(InterfaceInvokeInst i)
            {
                SootMethod m = i.getMethod();

                emit("invokeinterface " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m) + " " + (argCountOf(m) + 1));
            }

            public void caseSpecialInvokeInst(SpecialInvokeInst i)
            {
                SootMethod m = i.getMethod();

                emit("invokespecial " + slashify(m.getDeclaringClass().getName()) + "/" +
                    m.getName() + jasminDescriptorOf(m));
            }

            public void caseThrowInst(ThrowInst i)
            {
                emit("athrow");
            }

            public void caseCmpInst(CmpInst i)
            {
                emit("lcmp");
            }

            public void caseCmplInst(CmplInst i)
            {
                if(i.getOpType().equals(FloatType.v()))
                    emit("fcmpl");
                else
                    emit("dcmpl");
            }

            public void caseCmpgInst(CmpgInst i)
            {
                if(i.getOpType().equals(FloatType.v()))
                    emit("fcmpg");
                else
                    emit("dcmpg");
            }

            private void emitOpTypeInst(final String s, final OpTypeArgInst i)
            {
                i.getOpType().apply(new TypeSwitch()
                {
                    private void handleIntCase()
                    {
                        emit("i"+s);
                    }

                    public void caseIntType(IntType t) { handleIntCase(); }
                    public void caseBooleanType(BooleanType t) { handleIntCase(); }
                    public void caseShortType(ShortType t) { handleIntCase(); }
                    public void caseCharType(CharType t) { handleIntCase(); }
                    public void caseByteType(ByteType t) { handleIntCase(); }

                    public void caseLongType(LongType t)
                    {
                        emit("l"+s);
                    }

                    public void caseDoubleType(DoubleType t)
                    {
                        emit("d"+s);
                    }

                    public void caseFloatType(FloatType t)
                    {
                        emit("f"+s);
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
                if(((ValueBox) i.getUseBoxes().get(0)).getValue() != ((ValueBox) i.getDefBoxes().get(0)).getValue())
                    throw new RuntimeException("iinc def and use boxes don't match");
                    
                emit("iinc " + ((Integer) localToSlot.get(i.getLocal())) + " " + i.getConstant());
            }

            public void caseArrayLengthInst(ArrayLengthInst i)
            {
                emit("arraylength");
            }

            public void caseNegInst(NegInst i)
            {
                emitOpTypeInst("neg", i);
            }

            public void caseNewArrayInst(NewArrayInst i)
            {
                if(i.getBaseType() instanceof RefType)
                    emit("anewarray " + slashify(i.getBaseType().toBriefString()));
                else if(i.getBaseType() instanceof ArrayType)
                    emit("anewarray " + jasminDescriptorOf(i.getBaseType()));
                else
                    emit("newarray " + i.getBaseType().toBriefString());
            }

            public void caseNewMultiArrayInst(NewMultiArrayInst i)
            {
                emit("multianewarray " + jasminDescriptorOf(i.getBaseType()) + " " + 
                     i.getDimensionCount());
            }

            public void caseLookupSwitchInst(LookupSwitchInst i)
            {
                emit("lookupswitch");

                List lookupValues = i.getLookupValues();
                List targets = i.getTargets();

                for(int j = 0; j < lookupValues.size(); j++)
                    emit("  " + lookupValues.get(j) + " : " + 
                         instToLabel.get(targets.get(j)));

                emit("  default : " + instToLabel.get(i.getDefaultTarget()));
            }

            public void caseTableSwitchInst(TableSwitchInst i)
                {
                emit("tableswitch " + i.getLowIndex() + " ; high = " + i.getHighIndex());

                List targets = i.getTargets();

                for(int j = 0; j < targets.size(); j++)
                    emit("  " + instToLabel.get(targets.get(j)));

                emit("default : " + instToLabel.get(i.getDefaultTarget()));
            }
            
            public void caseDup1Inst(Dup1Inst i)
            {
                Type firstOpType = i.getOp1Type();
                if(firstOpType instanceof LongType || firstOpType instanceof DoubleType) 
                    emit("dup2");
                else
                    emit("dup");                
            }

            public void caseDup2Inst(Dup2Inst i)
            {
                Type firstOpType = i.getOp1Type();
                Type secondOpType = i.getOp2Type();
                if(firstOpType instanceof LongType || firstOpType instanceof DoubleType) {
                    emit("dup2");
                    if(secondOpType instanceof LongType || secondOpType instanceof DoubleType) {
                        emit("dup2");
                    } else 
                        emit("dup");
                } else if(secondOpType instanceof LongType || secondOpType instanceof DoubleType) {
                    if(firstOpType instanceof LongType || firstOpType instanceof DoubleType) {
                        emit("dup2");
                    } else 
                        emit("dup");
                    emit("dup2");
                } else {
                    //delme[
                    System.out.println("3000:(JasminClass): dup2 created");
                    //delme
                    emit("dup2");
                }
            }

            
            public void caseDup1_x1Inst(Dup1_x1Inst i)
            {
                Type opType = i.getOp1Type();
                Type underType = i.getUnder1Type();
                
                if(opType instanceof LongType || opType instanceof DoubleType) {
                    if(underType instanceof LongType || underType instanceof DoubleType) {
                        emit("dup2_x2");
                    } else 
                        emit("dup2_x1");
                } else {
                    if(underType instanceof LongType || underType instanceof DoubleType) 
                        emit("dup_x2");
                    else 
                        emit("dup_x1");
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
   


 
    private void calculateStackHeight(Block aBlock)
    {
        Iterator it = aBlock.iterator();
        int blockHeight =  ((Integer)blockToStackHeight.get(aBlock)).intValue();
        
        while(it.hasNext()) {
          Inst nInst = (Inst) it.next();
          
          blockHeight -= nInst.getInMachineCount();
          if(blockHeight < 0 ){            
            throw new RuntimeException("Negative Stack height has been attained\n:" +
                                       "StackHeight: " + blockHeight + 
                                       "\nAt instruction:" + nInst +
                                       "\nBlock:\n" + aBlock +
                                       "\n\nMethod: " + aBlock.getBody().getMethod().getName() 
                                       + "\n" +  aBlock.getBody().getMethod()                                       
                                       );
          }
          
          blockHeight += nInst.getOutMachineCount();
          if( blockHeight > maxStackHeight) {
            maxStackHeight = blockHeight;
          }
          //System.out.println(">>> " + nInst + " " + blockHeight);            
        }
        
        
        Iterator succs = aBlock.getSuccs().iterator();
        while(succs.hasNext()) {
            Block b = (Block) succs.next();
            Integer i = (Integer) blockToStackHeight.get(b);
            if(i != null) {
                if(i.intValue() != blockHeight) {
                    throw new RuntimeException("incoherent stack height at block merge point " + b + aBlock);
                }
                
            } else {
                blockToStackHeight.put(b, new Integer(blockHeight));
                calculateStackHeight(b);
            }            
        }        
    }


    private void calculateLogicalStackHeightCheck(Block aBlock)
    {
        Iterator it = aBlock.iterator();
        int blockHeight =  ((Integer)blockToLogicalStackHeight.get(aBlock)).intValue();
        
        while(it.hasNext()) {
            Inst nInst = (Inst) it.next();
          
            blockHeight -= nInst.getInCount();
            if(blockHeight < 0 ){            
                throw new RuntimeException("Negative Stack Logical height has been attained\n:" +
                                           "StackHeight: " + blockHeight + 
                                           "\nAt instruction:" + nInst +
                                           "\nBlock:\n" + aBlock +
                                           "\n\nMethod: " + aBlock.getBody().getMethod().getName() 
                                           + "\n" +  aBlock.getBody().getMethod()                                       
                                           );
            }
          
            blockHeight += nInst.getOutCount();
            
            //System.out.println(">>> " + nInst + " " + blockHeight);            
        }
        
        
        Iterator succs = aBlock.getSuccs().iterator();
        while(succs.hasNext()) {
            Block b = (Block) succs.next();
            Integer i = (Integer) blockToLogicalStackHeight.get(b);
            if(i != null) {
                if(i.intValue() != blockHeight) {
                    throw new RuntimeException("incoherent logical stack height at block merge point " + b + aBlock);
                }
                
            } else {
                blockToLogicalStackHeight.put(b, new Integer(blockHeight));
                calculateLogicalStackHeightCheck(b);
            }            
        }        
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
