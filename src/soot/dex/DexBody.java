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
public class DexBody {
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

    private JimpleBody jBody;
    private TryItem[] tries;

    private RefType declaringClassType;

    /**
     * @param code the codeitem that is contained in this body
     * @param method the method that is associated with this body
     */
    public DexBody(CodeItem code, RefType declaringClassType) {
        this.declaringClassType = declaringClassType;
        tries = code.getTries();
        ProtoIdItem prototype = code.getParent().method.getPrototype();
        List<TypeIdItem> paramTypes = TypeListItem.getTypes(prototype.getParameters());
        if (paramTypes != null) {
            parameterTypes = new ArrayList<Type>();
            for (TypeIdItem type : paramTypes)
                parameterTypes.add(DexType.toSoot(type));
        }

        numRegisters = code.getRegisterCount();
        numParameters = parameterTypes == null ? 0 : parameterTypes.size();
        isStatic = Modifier.isStatic(code.getParent().accessFlags);
        // if method is non-static the instance will be passed additionally
        numLocals = numRegisters - code.getParent().method.getPrototype().getParameterRegisterCount();
        if (! isStatic) {
            numParameters++;
            numLocals--;
        }

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
        if (i == null)
            throw new RuntimeException("Address " + address + "not part of method.");
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

        parameters = new Local[numParameters];
        if (!isStatic) {
            Local thisLocal = generateLocal(declaringClassType);
            add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(declaringClassType)));
            parameters[0] = thisLocal;
        }
        int start = isStatic ? 0 : 1;
        for (int i = start; i < parameters.length; i++) {
            int paramNum = i - start;
            Type t = parameterTypes.get(paramNum);
            Local gen = generateLocal(t);
            add(Jimple.v().newIdentityStmt(gen, Jimple.v().newParameterRef(t, paramNum)));
            parameters[i] = gen;
        }
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
        LocalSplitter.v().transform(jBody);
        for (RetypeableInstruction i : instructionsToRetype)
            i.retype();
        DexNullTransformer.v().transform(jBody);
        TypeAssigner.v().transform(jBody);
        LocalPacker.v().transform(jBody);
        LocalNameStandardizer.v().transform(jBody);

        return jBody;
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
