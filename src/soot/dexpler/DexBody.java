/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 * 
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 * 
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

package soot.dexpler;

import static soot.dexpler.instructions.InstructionFactory.fromInstruction;

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
import org.jf.dexlib.DexFile;
import org.jf.dexlib.ProtoIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.TypeListItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.InstructionIterator;
import org.jf.dexlib.Debug.DebugInstructionIterator;

import soot.Body;
import soot.Local;
import soot.Modifier;
import soot.NullType;
import soot.PackManager;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.dexpler.instructions.DanglingInstruction;
import soot.dexpler.instructions.DeferableInstruction;
import soot.dexpler.instructions.DexlibAbstractInstruction;
import soot.dexpler.instructions.MoveExceptionInstruction;
import soot.dexpler.instructions.PseudoInstruction;
import soot.dexpler.instructions.RetypeableInstruction;
import soot.dexpler.typing.DalvikTyper;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.jimple.toolkits.typing.TypeAssigner;
import soot.toolkits.scalar.LocalPacker;
import soot.toolkits.scalar.LocalSplitter;
import soot.toolkits.scalar.UnusedLocalEliminator;

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
    
    // detect array/instructions overlapping obfuscation
    private ArrayList<PseudoInstruction> pseudoInstructionData = new ArrayList<PseudoInstruction>();
    private DexFile dexFile = null;

    PseudoInstruction isAddressInData (int a) {
      for (PseudoInstruction pi: pseudoInstructionData) {
        int fb = pi.getDataFirstByte();
        int lb = pi.getDataLastByte();
        if (fb <= a && a <= lb)
          return pi;
      }
      return null;
    }
    
    /**
     * @param code the codeitem that is contained in this body
     * @param method the method that is associated with this body
     */
    public DexBody(DexFile dexFile, CodeItem code, RefType declaringClassType) {
      this.dexFile = dexFile;
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
            Debug.printDbg(" put instruction '", dexInstruction ,"' at 0x", Integer.toHexString(address));
            address += instruction.getSize(address);
        }
        
        // get addresses of pseudo-instruction data blocks
        for(DexlibAbstractInstruction instruction : instructions) {
          if (instruction instanceof PseudoInstruction) {
            PseudoInstruction pi = (PseudoInstruction)instruction;
            try {
				pi.computeDataOffsets(this);
			} catch (Exception e) {
				throw new RuntimeException("exception while computing data offsets: ", e);
			}
            pseudoInstructionData.add (pi);
            Debug.printDbg("add pseudo instruction: 0x", Integer.toHexString(pi.getDataFirstByte()) ," - 0x", Integer.toHexString(pi.getDataLastByte()) ," : ", pi.getDataSize());
          }
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
//        Debug.printDbg(" dump2: 0x", Integer.toHexString(j) ," : ",instructionAtAddress.get (j) );
//      }
      
      // check if it is a jump to pseudo-instructions data (=obfuscation)
      PseudoInstruction pi = isAddressInData(address);
      if (pi != null && !pi.isLoaded()) {
        System.out.println("warning: attempting to jump to pseudo-instruction data at address 0x"+ Integer.toHexString(address));
        System.out.println("pseudo instruction: "+ pi);
        pi.setLoaded(true);
        instructions.addAll(decodeInstructions(pi)); // TODO: should add a throw instruction here just to be sure...
        Exception e = new Exception();
          e.printStackTrace();
        System.out.println();
      }
      
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

    private ArrayList<DexlibAbstractInstruction> decodeInstructions(PseudoInstruction pi) { 
      final ArrayList<Instruction> instructionList = new ArrayList<Instruction>();
      ArrayList<DexlibAbstractInstruction> dexInstructions = new ArrayList<DexlibAbstractInstruction>();
     
      byte[] encodedInstructions = pi.getData();
      InstructionIterator.IterateInstructions(this.dexFile, encodedInstructions,
              new InstructionIterator.ProcessInstructionDelegate() {
                  public void ProcessInstruction(int codeAddress, Instruction instruction) {
                      instructionList.add(instruction);
                  }
              });

      Instruction[] instructions = new Instruction[instructionList.size()];
      instructionList.toArray(instructions);
      System.out.println("instructionList: ");
      int address = pi.getDataFirstByte();
      for (Instruction i: instructions) {
        DexlibAbstractInstruction dexInstruction = fromInstruction(i, address);
        instructionAtAddress.put(address, dexInstruction);
        dexInstructions.add(dexInstruction);
        System.out.println("i = "+ dexInstruction +" @ 0x"+ Integer.toHexString(address));
        address += i.getSize(address);
      }
      return dexInstructions;
    }

    public IDalvikTyper dalvikTyper = null;
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
        
        if (IDalvikTyper.ENABLE_DVKTYPER) {
          dalvikTyper = new DalvikTyper(); //null; //new DvkTyper();
        }

        Debug.printDbg("\n[jimplify] start for: ", methodString);
        
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
            if (IDalvikTyper.ENABLE_DVKTYPER) {
              this.dalvikTyper.setType(idStmt.leftBox, jBody.getMethod().getDeclaringClass().getType());
            }

        } 
        {
	        int i = 0; // index of parameter type
	        int parameterRegister = numRegisters - numParameterRegisters; // index of parameter register
	        for (Type t: parameterTypes) {  
	          
	            Local gen = Jimple.v().newLocal("$u"+ parameterRegister, UnknownType.v()); //may only use UnknownType here because the local may be reused with a different type later (before splitting)
	            jBody.getLocals().add(gen);
	            
	            Debug.printDbg ("add local for parameter register number: ", parameterRegister);
	            registerLocals[parameterRegister] = gen;
	            JIdentityStmt idStmt = (JIdentityStmt) Jimple.v().newIdentityStmt(gen, Jimple.v().newParameterRef(t, i++));
	            add(idStmt);
	            paramLocals.add(gen);
	            if (IDalvikTyper.ENABLE_DVKTYPER) {
	              this.dalvikTyper.setType(idStmt.leftBox, t);
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
            Debug.printDbg ("add local for register number: ", i);
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
            //Debug.printDbg(" current op to jimplify: 0x", Integer.toHexString(instruction.getInstruction().opcode.value) ," instruction: ", instruction );
            instruction.jimplify(this);
            //System.out.println("jimple: "+ jBody.getUnits().getLast());
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
		DeadAssignmentEliminator.v().transform(jBody);
        
        Debug.printDbg("\nbefore splitting");
        Debug.printDbg("",(Body)jBody);
        
        splitLocals();
        
        Debug.printDbg("\nafter splitting");
        Debug.printDbg("",(Body)jBody);
               
        for (RetypeableInstruction i : instructionsToRetype)
            i.retype();
        
        {
          // remove instructions from instructions list
          List<DexlibAbstractInstruction> iToRemove = new ArrayList<DexlibAbstractInstruction>();
          for (DexlibAbstractInstruction i: instructions)
            if (!jBody.getUnits().contains(i.getUnit()))
              iToRemove.add(i);
          for (DexlibAbstractInstruction i: iToRemove) {
            Debug.printDbg("removing dexinstruction containing unit '", i.getUnit() ,"'");
            instructions.remove(i);
          }
        }
        
        if (IDalvikTyper.ENABLE_DVKTYPER) {
          for(DexlibAbstractInstruction instruction : instructions) {
            instruction.getConstraint(dalvikTyper); // todo: check that this instruction still is in jbody
          }
          Debug.printDbg("[DalvikTyper] resolving typing constraints...");
          dalvikTyper.assignType();
          Debug.printDbg("[DalvikTyper] resolving typing constraints... done.");
          
          Debug.printDbg("\nafter Dalvik Typer");
          
        } else {
          DexNumTransformer.v().transform (jBody);      
          DexNullTransformer.v().transform(jBody);
          DexIfTransformer.v().transform(jBody);
          //DexRefsChecker.v().transform(jBody);
          //DexNullArrayRefTransformer.v().transform(jBody);
          
          Debug.printDbg("\nafter Num and Null transformers");
        }
        Debug.printDbg("",(Body)jBody);
        

        if (IDalvikTyper.ENABLE_DVKTYPER) {
          for (Unit u: jBody.getUnits()) {
            if (u instanceof AssignStmt) {
              AssignStmt ass = (AssignStmt)u;
              if (ass.getRightOp() instanceof IntConstant) {
                System.out.println("instance of int constant: "+ u);
                if (ass.getLeftOp() instanceof Local) {
                  Local l = (Local)ass.getLeftOp();
                  if (!(l.getType() instanceof PrimType)) {
                    System.out.println("left local not instance of primtype! "+ l.getType() +" replacing zero by null...");
                    ass.setRightOp(NullConstant.v());
                  } else {
                    System.out.println("left local instance of primtype! "+ l.getType());
                  }
                }
              }
            }
          }
        }
        
        TypeAssigner.v().transform(jBody);
        
        
        if (IDalvikTyper.ENABLE_DVKTYPER) {
          for (Unit u: jBody.getUnits()) {
            if (u instanceof AssignStmt) {
              AssignStmt ass = (AssignStmt)u;
              // cast expr
              if (ass.getRightOp() instanceof CastExpr) {
                CastExpr c = (CastExpr)ass.getRightOp();
                if (c.getType() instanceof PrimType) {
                  if (c.getOp() instanceof NullConstant || c.getOp() instanceof NullType) {
                    Debug.printDbg("[DalvikTyper] replacing null_type by 0 in cast expr ", u);
                    c.setOp(IntConstant.v(0));
                  } else if (c.getOp() instanceof Local) {
                    Local l = (Local)c.getOp();
                    Debug.printDbg("[DalvikType] local type in cast expr '", u ,"' : ", l.getType());
                    if (l.getType() instanceof NullType) {
                      Debug.printDbg("[DalvikTyper] replacing null_typed local by 0 in cast expr ", u);
                      c.setOp(IntConstant.v(0));
                    }
                  }
                }
              }
            }
          }
          
          for (Local l: jBody.getLocals()) {
            if (l.getType().toString().equals("null_type")) {
              Debug.printDbg("[DalvikTyper] replacing null_type by java.lang.Object for variable ", l);
              l.setType(Scene.v().getRefType("java.lang.Object"));
            }
          }

        }
        
        LocalPacker.v().transform(jBody);
        UnusedLocalEliminator.v().transform(jBody);
        LocalNameStandardizer.v().transform(jBody);
        
        Debug.printDbg("\nafter type assigner localpacker and name standardizer");
        Debug.printDbg("",(Body)jBody);
        
        PackManager.v().getPack("jb").apply(jBody);
        
        
        for (Unit u: jBody.getUnits()) {
          if (u instanceof AssignStmt) {
            AssignStmt ass = (AssignStmt)u;
            if (ass.getRightOp() instanceof CastExpr) {
              CastExpr c = (CastExpr)ass.getRightOp();
              if (c.getType() instanceof NullType) {
                Debug.printDbg("replacing cast to null_type by nullConstant assignment in ", u);
                ass.setRightOp(NullConstant.v());
              }
            }
          }
        }
        
        Debug.printDbg("\nafter jb pack");
        Debug.printDbg("",(Body)jBody);
        
        // fields init

          if (m.getName().equals("<init>") || m.getName().equals("<clinit>")) {
             System.out.println("constant initSm: "+ m);
             Util.addConstantTags(jBody);
          }
        

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
            Debug.printDbg(" start : 0x", Integer.toHexString(startAddress));
            int length = tryItem.getTryLength();
            Debug.printDbg(" length: 0x", Integer.toHexString(length));
            Debug.printDbg(" end   : 0x", Integer.toHexString(startAddress + length));
            int endAddress = startAddress + length;// - 1;
            Unit beginStmt = instructionAtAddress(startAddress).getUnit();
            // (startAddress + length) typically points to the first byte of the first instruction after the try block
            // except if there is no instruction after the try block in which case it points to the last byte of the last
            // instruction of the try block. Removing 1 from (startAddress + length) always points to "somewhere" in
            // the last instruction of the try block since the smallest instruction is on two bytes (nop = 0x0000).
            Unit endStmt =  instructionAtAddress (endAddress).getUnit(); 
            Debug.printDbg("begin instruction (0x", Integer.toHexString(startAddress) ,"): ", instructionAtAddress(startAddress).getUnit() ," --- ", instructionAtAddress(startAddress).getUnit());
            Debug.printDbg("end instruction   (0x", Integer.toHexString(endAddress)   ,"): ", instructionAtAddress (endAddress).getUnit()  ," --- ", instructionAtAddress (endAddress).getUnit());
            
//            for (int i=0x00; i<0x20; i++) {
//              Debug.printDbg("dump  (0x", Integer.toHexString(i) ,"): ", instructionAtAddress (i).getUnit()  ," --- ", instructionAtAddress (i).getUnit());
//            }
            
            EncodedCatchHandler h = tryItem.encodedCatchHandler;

            for (EncodedTypeAddrPair handler: h.handlers) {
              int handlerAddress = handler.getHandlerAddress();
              Debug.printDbg("handler   (0x", Integer.toHexString(handlerAddress)   ,"): ", instructionAtAddress (handlerAddress).getUnit()  ," --- ", instructionAtAddress (handlerAddress-1).getUnit());
                Type t = DexType.toSoot(handler.exceptionType);
                // exceptions can only be of RefType
                if (t instanceof RefType) {
                    SootClass exception = ((RefType) t).getSootClass();
                    DexlibAbstractInstruction instruction = instructionAtAddress(handler.getHandlerAddress());
                    if (! (instruction instanceof MoveExceptionInstruction))
                        Debug.printDbg("First instruction of trap handler unit not MoveException but " , instruction.getClass());
                    else
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
    
     
    
}
