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
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.dex.instructions.DanglingInstruction;
import soot.dex.instructions.DeferableInstruction;
import soot.dex.instructions.DexlibAbstractInstruction;
import soot.dex.instructions.MoveExceptionInstruction;
import soot.dex.instructions.RetypeableInstruction;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
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
    private Map<Integer, DexlibAbstractInstruction> instructionAtAddress;
    private LocalGenerator localGenerator;

    private List<DeferableInstruction> deferredInstructions;
    private Set<RetypeableInstruction> instructionsToRetype;
    private DanglingInstruction dangling;

    private int numRegisters;
    private int numParameters;
    private int numLocals;
    private List<Type> parameterTypes;
    private Local[] parameters;
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
        numParameters = parameterTypes == null ? 0 : parameterTypes.size();
        isStatic = Modifier.isStatic(code.getParent().accessFlags);
        computeParameterAndLocalCounts(paramTypes);

        instructions = new ArrayList<DexlibAbstractInstruction>();
        instructionAtAddress = new HashMap<Integer, DexlibAbstractInstruction>();

        registerLocals = new Local[numLocals];

        int address = 0;

        for (Instruction instruction : code.getInstructions()) {
            DexlibAbstractInstruction dexInstruction = fromInstruction(instruction, address);
            instructions.add(dexInstruction);
            instructionAtAddress.put(address, dexInstruction);
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

    /* numLocals will be the number of local variables
     * numParameters will be the number of parameters, including "this", if applicable
     */
	private void computeParameterAndLocalCounts(List<TypeIdItem> paramTypes) {
		numLocals = numRegisters - numParameters;
        if (! isStatic) {
            numParameters++;
            numLocals--;
        }
        //for each wide parameter, this parameter takes up two registers;
        //hence update local count accordingly
        if(paramTypes!=null) {
	        for(TypeIdItem t: paramTypes) {
	        	if(DexType.isWide(t))
	        		numLocals--;
	        }
        }
	}

    /**
     * Return the types that are used in this body.
     */
    public Set<DexType> usedTypes() {
        Set<DexType> types = new HashSet<DexType>();
        for (DexlibAbstractInstruction i : instructions)
            types.addAll(i.introducedTypes());

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
        if (num >= numLocals) {
            int parameterNumber = num - numLocals;

            if (parameterNumber < parameters.length)
                return parameters[parameterNumber];

            throw new RuntimeException("This method has " + numParameters + " parameters but the code tried to access parameter " + parameterNumber);
        }
        return registerLocals[num];
    }

    /**
     * Return the instruction that is present at the byte code address.
     *
     * @param address the byte code address.
     * @throws RuntimeException if address is not part of this body.
     */
    public DexlibAbstractInstruction instructionAtAddress(int address) {
        DexlibAbstractInstruction i = instructionAtAddress.get(address);
        if (i == null) {
            // catch addresses can be in the middlde of last instruction. Ex. in com.letang.ldzja.en.apk:
            //
            //          042c46: 7020 2a15 0100                         |008f: invoke-direct {v1, v0}, Ljavax/mi...
            //          042c4c: 2701                                   |0092: throw v1
            //          catches       : 4                                                                                                                                                                        
            //              <any> -> 0x0065 
            //            0x0069 - 0x0093
            if ((i = instructionAtAddress.get(address - 1)) == null) { // Alex: should also check for -2 -3 and -4 ?
              throw new RuntimeException("Address 0x" + Integer.toHexString(address) + "(& -1) not part of method '"+ this.methodString +"'");
            }
        }
        return i;
    }

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

        List<Local> paramLocals = new LinkedList<Local>();       
        if (!isStatic) {
            Local thisLocal = generateLocal(UnknownType.v());
            add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(declaringClassType)));
            paramLocals.add(thisLocal);
        } 
        {
	        int i=0;
	        for (Type t: parameterTypes) {
	            Local gen = generateLocal(UnknownType.v()); //may only use UnknownType here because the local may be reused with a different type later (before splitting)
	            add(Jimple.v().newIdentityStmt(gen, Jimple.v().newParameterRef(t, i)));
	            paramLocals.add(gen);
	        }
        }
        parameters = paramLocals.toArray(new Local[paramLocals.size()]);
        
        for (int i = 0; i < numLocals; i++)
            registerLocals[i] = generateLocal(UnknownType.v());

        for(DexlibAbstractInstruction instruction : instructions) {
            if (dangling != null) {
                dangling.finalize(this, instruction);
                dangling = null;
            }
            instruction.jimplify(this);
        }
        for(DeferableInstruction instruction : deferredInstructions) {
            instruction.deferredJimplify(this);
        }
        if (tries != null)
            addTraps();
        splitLocals();
        for (RetypeableInstruction i : instructionsToRetype)
            i.retype();
        DexNullTransformer.v().transform(jBody);
        TypeAssigner.v().transform(jBody);
        LocalPacker.v().transform(jBody);
        LocalNameStandardizer.v().transform(jBody);

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
            Unit beginStmt = instructionAtAddress(startAddress).getBeginUnit();
            Unit endStmt =  instructionAtAddress(startAddress + tryItem.getTryLength()).getEndUnit();
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

                    Trap trap = Jimple.v().newTrap(exception, beginStmt, endStmt, instruction.getBeginUnit());
                    jBody.getTraps().add(trap);
                }
            }
            int catchAllHandlerAddress = h.getCatchAllHandlerAddress();
            if (catchAllHandlerAddress != -1) {
                DexlibAbstractInstruction i = instructionAtAddress(catchAllHandlerAddress); 
                Unit catchAllHandler = i.getBeginUnit();
                SootClass exc = SootResolver.v().makeClassRef("java.lang.Throwable");
                Trap trap = Jimple.v().newTrap(exc, beginStmt, endStmt, catchAllHandler);
                ((RetypeableInstruction) i).setRealType(this, exc.getType());
                jBody.getTraps().add(trap);
            }
        }
    }
}
