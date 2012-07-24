/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dex;

import static soot.dex.instructions.InstructionFactory.fromInstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jf.dexlib.CodeItem;
import org.jf.dexlib.CodeItem.EncodedCatchHandler;
import org.jf.dexlib.CodeItem.EncodedTypeAddrPair;
import org.jf.dexlib.CodeItem.TryItem;
import org.jf.dexlib.DebugInfoItem;
import org.jf.dexlib.ProtoIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.TypeListItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Debug.DebugInstructionIterator;


import soot.Body;
import soot.BooleanType;
import soot.IntType;
import soot.Local;
import soot.Modifier;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.dex.instructions.DanglingInstruction;
import soot.dex.instructions.DeferableInstruction;
import soot.dex.instructions.DexlibAbstractInstruction;
import soot.dex.instructions.MoveExceptionInstruction;
import soot.dex.instructions.RetypeableInstruction;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.jimple.toolkits.typing.TypeAssigner;
import soot.toolkits.scalar.LocalPacker;
import soot.toolkits.scalar.LocalSplitter;

/**
 * A DexBody contains the code of a DexMethod and is used as a wrapper around
 * JimpleBody in the jimplification process.
 *
 * @author Michael Markert
 * @author Frank Hartmann
 */
public class DexBody  {
    private List<DexlibAbstractInstruction> instructions;
    // keeps track about the jimple locals that are associated with the dex registers
    private Local[] registerLocals;
    private Local storeResultLocal;
    private Map<Integer, DexlibAbstractInstruction> instructionAtAddress;
    private LocalGenerator localGenerator;

    private List<DeferableInstruction> deferredInstructions;
    private Set<RetypeableInstruction> instructionsToRetype;
    private DanglingInstruction dangling;

    private int numRegisters;
    private int numParameterRegisters;
    private List<Type> parameterTypes;
    private boolean isStatic;
    private String methodString = "";

    private JimpleBody jBody;
    private TryItem[] tries;

    private RefType declaringClassType;
    
    private static LocalSplitter splitter; 

    /**
     * @param code the codeitem that is contained in this body
     * @param method the method that is associated with this body
     */
    public DexBody(CodeItem code, RefType declaringClassType) {
        this.declaringClassType = declaringClassType;
        tries = code.getTries();
        methodString = code.getParent().method.toString();
        ProtoIdItem prototype = code.getParent().method.getPrototype();
        List<TypeIdItem> paramTypes = TypeListItem.getTypes(prototype.getParameters());
        if (paramTypes != null) {
            parameterTypes = new ArrayList<Type>();
            for (TypeIdItem type : paramTypes)
                parameterTypes.add(DexType.toSoot(type));
        } else {
        	parameterTypes = Collections.emptyList();
        }

        numRegisters = code.getRegisterCount();
        numParameterRegisters = prototype.getParameterRegisterCount();
        isStatic = Modifier.isStatic(code.getParent().accessFlags);

        instructions = new ArrayList<DexlibAbstractInstruction>();
        instructionAtAddress = new HashMap<Integer, DexlibAbstractInstruction>();

        registerLocals = new Local[numRegisters];

        int address = 0;

        for (Instruction instruction : code.getInstructions()) {
            DexlibAbstractInstruction dexInstruction = fromInstruction(instruction, address);
            instructions.add(dexInstruction);
            instructionAtAddress.put(address, dexInstruction);
            Debug.printDbg(" put instruction '"+ dexInstruction +"' at 0x"+ Integer.toHexString(address));
            address += instruction.getSize(address);
        }

        DebugInfoItem debugInfoItem = code.getDebugInfo();
        if(debugInfoItem!=null) {


            DebugInstructionIterator.DecodeInstructions(debugInfoItem, numRegisters,
                new DebugInstructionIterator.ProcessDecodedDebugInstructionDelegate() {
                    @Override
                    public void ProcessLineEmit(int codeAddress, final int line) {
                        instructionAtAddress(codeAddress).setLineNumber(line);
                    }
                });
        }
    }

    /**
     * Return the types that are used in this body.
     */
    public Set<DexType> usedTypes() {
        Set<DexType> types = new HashSet<DexType>();
        for (DexlibAbstractInstruction i : instructions)
            types.addAll(i.introducedTypes());
        
        if(tries!=null) {
	        for (TryItem tryItem : tries) {
	            EncodedCatchHandler h = tryItem.encodedCatchHandler;
		        for (EncodedTypeAddrPair handler: h.handlers) {
		            types.add(new DexType(handler.exceptionType));
		        }
	        }
        }

        return types;
    }

    /**
     * Add unit to this body.
     *
     * @param u Unit to add.
     */
    public void add(Unit u) {
        getBody().getUnits().add(u);
    }

    /**
     * Add a deferred instruction to this body.
     *
     * @param i the deferred instruction.
     */
    public void addDeferredJimplification(DeferableInstruction i) {
        deferredInstructions.add(i);
    }

    /**
     * Add a retypeable instruction to this body.
     *
     * @param i the retypeable instruction.
     */
    public void addRetype(RetypeableInstruction i) {
        instructionsToRetype.add(i);
    }

    /**
     * Generate a new local variable.
     *
     * @param t the type of the new variable.
     * @return the generated local.
     */
    public Local generateLocal(Type t) {
        return localGenerator.generateLocal(t);
    }

    /**
     * Return the associated JimpleBody.
     *
     * @throws RuntimeException if no jimplification happened yet.
     */
    public Body getBody() {
        if (jBody == null)
            throw new RuntimeException("No jimplification happened yet, no body available.");
        return jBody;
    }

    /**
     * Return the Locals that are associated with the current register state.
     *
     */
    public Local[] getRegisterLocals() {
        return registerLocals;
    }

    /**
     * Return the Local that are associated with the number in the current
     * register state.
     *
     * Handles if the register number actually points to a method parameter.
     *
     * @param num the register number
     */
    public Local getRegisterLocal(int num) {
        return registerLocals[num];
    }
    
    public Local getStoreResultLocal () {
      return storeResultLocal;
    }

    /**
     * Return the instruction that is present at the byte code address.
     * 
     * @param address the byte code address.
     * @throws RuntimeException if address is not part of this body.
     */
    public DexlibAbstractInstruction instructionAtAddress(int address) {
//      for (int j=address - 10; j< address+10; j++ ){
//        Debug.printDbg(" dump2: 0x"+ Integer.toHexString(j) +" : "+instructionAtAddress.get (j) );
//      }
        DexlibAbstractInstruction i = instructionAtAddress.get(address);
        if (i == null) {
            // catch addresses can be in the middlde of last instruction. Ex. in com.letang.ldzja.en.apk:
            //
            //          042c46: 7020 2a15 0100                         |008f: invoke-direct {v1, v0}, Ljavax/mi...
            //          042c4c: 2701                                   |0092: throw v1
            //          catches       : 4                                                                                                                                                                        
            //              <any> -> 0x0065 
            //            0x0069 - 0x0093
            if ((i = instructionAtAddress.get(address - 1)) == null) {
              if ((i = instructionAtAddress.get(address - 2)) == null) {
                throw new RuntimeException("Address 0x" + Integer.toHexString(address) + "(& -1 -2) not part of method '"+ this.methodString +"'");
              }
            }
        }
        return i;
    }

    public DvkTyper dvkTyper = null;
    /**
     * Return the jimple equivalent of this body.
     *
     * @param m the SootMethod that contains this body
     */
    public Body jimplify(SootMethod m) {
        jBody = Jimple.v().newBody(m);
        localGenerator = new LocalGenerator(jBody);
        deferredInstructions = new ArrayList<DeferableInstruction>();
        instructionsToRetype = new HashSet<RetypeableInstruction>();
        
        if (DvkTyper.ENABLE_DVKTYPER) {
          dvkTyper = new DvkTyper();
        }

        Debug.printDbg("\n[jimplify] start for: "+ methodString);
        
        // process method parameters and generate Jimple locals from Dalvik registers
        List<Local> paramLocals = new LinkedList<Local>();       
        if (!isStatic) {
            int thisRegister = numRegisters - numParameterRegisters - 1;
            
            Local thisLocal = Jimple.v().newLocal("$u"+ thisRegister, UnknownType.v()); //generateLocal(UnknownType.v());
            jBody.getLocals().add(thisLocal);
            
            registerLocals[thisRegister] = thisLocal;
            JIdentityStmt idStmt = (JIdentityStmt) Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(declaringClassType));
            add(idStmt);
            paramLocals.add(thisLocal);
            if (DvkTyper.ENABLE_DVKTYPER) {
              this.dvkTyper.setType(idStmt.leftBox, jBody.getMethod().getDeclaringClass().getType());
            }

        } 
        {
	        int i = 0; // index of parameter type
	        int parameterRegister = numRegisters - numParameterRegisters; // index of parameter register
	        for (Type t: parameterTypes) {  
	          
	            Local gen = Jimple.v().newLocal("$u"+ parameterRegister, UnknownType.v()); //may only use UnknownType here because the local may be reused with a different type later (before splitting)
	            jBody.getLocals().add(gen);
	            
	            Debug.printDbg ("add local for parameter register number: "+ parameterRegister);
	            registerLocals[parameterRegister] = gen;
	            JIdentityStmt idStmt = (JIdentityStmt) Jimple.v().newIdentityStmt(gen, Jimple.v().newParameterRef(t, i++));
	            add(idStmt);
	            paramLocals.add(gen);
	            if (DvkTyper.ENABLE_DVKTYPER) {
	              this.dvkTyper.setType(idStmt.leftBox, t);
	            }
	            
	            // some parameters may be encoded on two registers.
	            // in Jimple only the first Dalvik register name is used
	            // as the corresponding Jimple Local name. However, we also add
	            // the second register to the registerLocals array since it could be 
	            // used later in the Dalvik bytecode
	            if (t.toString().equals("long") || t.toString().equals("double")) {
	              parameterRegister++;
	              Local g = Jimple.v().newLocal("$u"+ parameterRegister, UnknownType.v()); //may only use UnknownType here because the local may be reused with a different type later (before splitting)
	              jBody.getLocals().add (g);
	              registerLocals[parameterRegister] = g;
	            }
	            
	            parameterRegister++;
	        }
        }
        
        for (int i = 0; i < (numRegisters - numParameterRegisters - (isStatic?0:1)); i++) {
            Debug.printDbg ("add local for register number: "+ i);
            registerLocals[i] = Jimple.v().newLocal("$u"+ i, UnknownType.v());
            jBody.getLocals().add(registerLocals[i]);
        }
        
        // add local to store intermediate results
        storeResultLocal = Jimple.v().newLocal("$u-1", UnknownType.v());
        jBody.getLocals().add (storeResultLocal);
        
        // process bytecode instructions
        for(DexlibAbstractInstruction instruction : instructions) {
            if (dangling != null) {
                dangling.finalize(this, instruction);
                dangling = null;
            }
            Debug.printDbg(" current op: 0x"+ Integer.toHexString(instruction.getInstruction().opcode.value));
            instruction.jimplify(this);
        }
        for(DeferableInstruction instruction : deferredInstructions) {
            instruction.deferredJimplify(this);
        }
        if (tries != null)
            addTraps();

        /* We eliminate dead code. Dead code has been shown to occur under the following
         * circumstances.
         * 
         *  0006ec: 0d00                                   |00a2: move-exception v0
            ...
			0006f2: 0d00                                   |00a5: move-exception v0
			...
	        0x0041 - 0x008a
	          Ljava/lang/Throwable; -> 0x00a5
	          <any> -> 0x00a2
	          
	       Here there are two traps both over the same region. But the same always fires, hence
	       rendering the code at a2 unreachable.
	       Dead code yields problems during local splitting because locals within dead code
	       will not be split. Hence we remove all dead code here.
         */
		UnreachableCodeEliminator.v().transform(jBody);
        
        Debug.printDbg("\nbefore splitting");
        Debug.printDbg(""+(Body)jBody);
        
        splitLocals();
        
        Debug.printDbg("\nafter splitting");
        Debug.printDbg(""+(Body)jBody);
        
        for (RetypeableInstruction i : instructionsToRetype)
            i.retype();
        
        if (DvkTyper.ENABLE_DVKTYPER) {
          dvkTyper.assignType();
        } else {
          DexNumTransformer.v().transform (jBody);      
          DexNullTransformer.v().transform(jBody);
          DexIfTransformer.v().transform(jBody);
        }
        
        Debug.printDbg("\nafter Num and Null transformers");
        Debug.printDbg(""+(Body)jBody);
        
        TypeAssigner.v().transform(jBody);
        LocalPacker.v().transform(jBody);
        LocalNameStandardizer.v().transform(jBody);
        
        Debug.printDbg("\nafter type assigner localpacker and name standardizer");
        Debug.printDbg(""+(Body)jBody);
        
        PackManager.v().getPack("jb").apply(jBody);

        return jBody;
    }

	private void splitLocals() {
		if(splitter==null)
        	splitter = new LocalSplitter(new DalvikThrowAnalysis());
        splitter.transform(jBody);
	}

    /**
     * Set a dangling instruction for this body.
     *
     */
    public void setDanglingInstruction(DanglingInstruction i) {
        dangling = i;
    }

    /**
     * Return the instructions that appear (lexically) after the given instruction.
     *
     * @param instruction the instruction which successors will be returned.
     */
    public List<DexlibAbstractInstruction> instructionsAfter(DexlibAbstractInstruction instruction) {
        int i = instructions.indexOf(instruction);
        if (i == -1)
            throw new IllegalArgumentException("Instruction" + instruction + "not part of this body.");

        return instructions.subList(i + 1, instructions.size());
    }

    /**
     * Return the instructions that appear (lexically) before the given instruction.
     *
     * The instruction immediately before the given is the first instruction and so on.
     *
     * @param instruction the instruction which successors will be returned.
     */
    public List<DexlibAbstractInstruction> instructionsBefore(DexlibAbstractInstruction instruction) {
        int i = instructions.indexOf(instruction);
        if (i == -1)
            throw new IllegalArgumentException("Instruction " + instruction + " not part of this body.");

        List<DexlibAbstractInstruction> l = new ArrayList<DexlibAbstractInstruction>();
        l.addAll(instructions.subList(0 , i));
        Collections.reverse(l);
        return l;
    }

    /**
     * Add the traps of this body.
     *
     * Should only be called at the end jimplify.
     */
    private void addTraps() {
      for (TryItem tryItem : tries) {
            int startAddress = tryItem.getStartCodeAddress();
            Debug.printDbg(" start : 0x"+ Integer.toHexString(startAddress));
            int length = tryItem.getTryLength();
            Debug.printDbg(" length: 0x"+ Integer.toHexString(length));
            Debug.printDbg(" end   : 0x"+ Integer.toHexString(startAddress + length));
            int endAddress = startAddress + length;// - 1;
            Unit beginStmt = instructionAtAddress(startAddress).getUnit();
            // (startAddress + length) typically points to the first byte of the first instruction after the try block
            // except if there is no instruction after the try block in which case it points to the last byte of the last
            // instruction of the try block. Removing 1 from (startAddress + length) always points to "somewhere" in
            // the last instruction of the try block since the smallest instruction is on two bytes (nop = 0x0000).
            Unit endStmt =  instructionAtAddress (endAddress).getUnit(); 
            Debug.printDbg("begin instruction (0x"+ Integer.toHexString(startAddress) +"): "+ instructionAtAddress(startAddress).getUnit() +" --- "+ instructionAtAddress(startAddress).getUnit());
            Debug.printDbg("end instruction   (0x"+ Integer.toHexString(endAddress)   +"): "+ instructionAtAddress (endAddress).getUnit()  +" --- "+ instructionAtAddress (endAddress).getUnit());
            
//            for (int i=0x00; i<0x20; i++) {
//              Debug.printDbg("dump  (0x"+ Integer.toHexString(i) +"): "+ instructionAtAddress (i).getUnit()  +" --- "+ instructionAtAddress (i).getUnit());
//            }
            
            EncodedCatchHandler h = tryItem.encodedCatchHandler;

            for (EncodedTypeAddrPair handler: h.handlers) {
                Type t = DexType.toSoot(handler.exceptionType);
                // exceptions can only be of RefType
                if (t instanceof RefType) {
                    SootClass exception = ((RefType) t).getSootClass();
                    DexlibAbstractInstruction instruction = instructionAtAddress(handler.getHandlerAddress());
                    if (! (instruction instanceof MoveExceptionInstruction))
                        throw new RuntimeException("First instruction of trap handler unit not MoveException but " + instruction.getClass());
                    ((MoveExceptionInstruction) instruction).setRealType(this, exception.getType());

                    Trap trap = Jimple.v().newTrap(exception, beginStmt, endStmt, instruction.getUnit());
                    jBody.getTraps().add(trap);
                }
            }
            int catchAllHandlerAddress = h.getCatchAllHandlerAddress();
            if (catchAllHandlerAddress != -1) {
                DexlibAbstractInstruction i = instructionAtAddress(catchAllHandlerAddress); 
                Unit catchAllHandler = i.getUnit();
                SootClass exc = SootResolver.v().makeClassRef("java.lang.Throwable");
                Trap trap = Jimple.v().newTrap(exc, beginStmt, endStmt, catchAllHandler);
                ((RetypeableInstruction) i).setRealType(this, exc.getType());
                jBody.getTraps().add(trap);
            }
        }
    }
    
    
    //from FT
    public Stmt captureAssign(JAssignStmt stmt, int current) {
      ValueBox left = stmt.leftBox;
      ValueBox right = stmt.rightBox;
      Debug.printDbg("current captureAssign: 0x"+ Integer.toHexString(current));
      switch(current) {
      case 0x01:
      case 0x02:
      case 0x03:
        dvkTyper.setConstraint(left, right);
        break;
      case 0x04:
      case 0x05:
      case 0x06:
        dvkTyper.setConstraint(left, right);
        break;
      case 0x07:
      case 0x08:
      case 0x09:
        dvkTyper.setObjectType(right);
        dvkTyper.setConstraint(left, right);
        break;
      case 0xa:
      case 0xb:
        dvkTyper.setConstraint(left, right);
        //dvkTyper.setType(left, right.getValue().getType());
        break;
      case 0xc:
        dvkTyper.setObjectType(right);
        dvkTyper.setConstraint(left, right);
        // 0xc move result object
        // 0xd move exception
        // 0xe return void
        // 0xf return vx
        // 0x10 return vx
        // 0x11 return object vx
      case 0x12:
      case 0x13:
      case 0x14:
        dvkTyper.setConstraint(left,right);
        break;
      case 0x15:
      case 0x16:
      case 0x17:
      case 0x18:
      case 0x19:
        dvkTyper.setConstraint(left,right);
        break;
      case 0x1a:
      case 0x1b:
        dvkTyper.setType(left, Scene.v().getRefType("java.lang.String"));
        break;
      case 0x1c:
        dvkTyper.setType(left, Scene.v().getRefType("java.lang.Class"));
        break;
        // 0x1d monitor-enter vx
        // 0x1e monitor-exit vx
      case 0x1f:
        dvkTyper.setType(left, right.getValue().getType());
        dvkTyper.setType(((JCastExpr) right.getValue()).getOpBox(), right.getValue().getType()); 
        break;
      case 0x20:
        dvkTyper.setType(left, BooleanType.v());
        dvkTyper.setObjectType(((JInstanceOfExpr) right.getValue()).getOpBox());
        break;
      case 0x21:
        dvkTyper.setType(left, IntType.v());
        break;
      case 0x22:
      case 0x23:
      case 0x24:
      case 0x25:
        dvkTyper.setType(left, right.getValue().getType());
        break;
        // 0x25 filled-new-array-range {vx..vy},type_id
        // 0x26 fill-array-data vx,array_data_offset
        // 0x27 throw vx
        // 0x28 goto
        // 0x29 goto
        // 0x2a goto
        // 0x2b switch
        // 0x2c switch
        // 0x2d cmp
        // 0x2e cmp
        // 0x2f cmp
        // 0x30 cmp
        // 0x31 cmp
        // 0x32 if
        // 0x33 if
        // 0x34 if
        // 0x35 if
        // 0x36 if
        // 0x37 if
        // 0x38 if
        // 0x39 if
        // 0x3a if
        // 0x3b if
        // 0x3c if
        // 0x3d if
        // 0x3e -
        // 0x3f -
        // 0x40 -
        // 0x41 -
        // 0x42 -
        // 0x43 -
      case 0x44:
      case 0x45:
      case 0x46:
      case 0x47:
      case 0x48:
      case 0x49:
      case 0x4a:
        Value arrayGetValue = right.getValue();
        if (arrayGetValue instanceof JArrayRef) {
          dvkTyper.setObjectType(((JArrayRef) arrayGetValue).getBaseBox());
        }
        dvkTyper.setConstraint(right,left);
        break;
      case 0x4b:
      case 0x4c:
      case 0x4d:
      case 0x4e:
      case 0x4f:
      case 0x50:
      case 0x51:
        dvkTyper.setConstraint(left, right);
        break;
      case 0x52:
      case 0x53:
      case 0x54:
      case 0x55:
      case 0x56:
      case 0x57:
      case 0x58:
        Value fieldGetValue = right.getValue();
        if (fieldGetValue instanceof JInstanceFieldRef) {
          dvkTyper.setObjectType(((JInstanceFieldRef) fieldGetValue).getBaseBox());
        }
        dvkTyper.setType(left, right.getValue().getType());
        break;
        // 0x59 iput see below
        // 0x5a iput see below
        // 0x5b iput see below
        // 0x5c iput see below
        // 0x5d iput see below
        // 0x5e iput see below
        // 0x5f iput see below
      case 0x60:
      case 0x61:
      case 0x62:
      case 0x63:
      case 0x64:
      case 0x65:
      case 0x66:
        dvkTyper.setType(left, right.getValue().getType());
        break;
      case 0x59:
      case 0x5a:
      case 0x5b:
      case 0x5c:
      case 0x5d:
      case 0x5e:
      case 0x5f:
      case 0x67:
      case 0x68:
      case 0x69:
      case 0x6a:
      case 0x6b:
      case 0x6c:
      case 0x6d:
        dvkTyper.setType(right, left.getValue().getType());
        break;
        // 0x6e invoke method
        // 0x6f invoke method
        // 0x70 invoke method
        // 0x71 invoke method
        // 0x72 invoke method
        // 0x73 -
        // 0x74 invoke method
        // 0x75 invoke method
        // 0x76 invoke method
        // 0x77 invoke method
        // 0x78 invoke method
        // 0x79 to 0xED unused or arithmetic operations
        // 0xEE to 0xFF unused or odex instructions
      default:
        Debug.printDbg("[D2J] warning: No constraint registered for opcode 0x" +  Integer.toHexString(current));
      }
      return stmt;
      
        
    }
    
    
}
