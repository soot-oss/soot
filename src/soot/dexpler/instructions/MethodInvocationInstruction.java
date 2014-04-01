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

package soot.dexpler.instructions;

import static soot.dexpler.Util.dottedClassName;
import static soot.dexpler.Util.isFloatLike;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.MethodReference;

import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.SootResolver;
import soot.Type;
import soot.dexpler.Debug;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;

public abstract class MethodInvocationInstruction extends DexlibAbstractInstruction implements DanglingInstruction {
    // stores the dangling InvokeExpr
    protected InvokeExpr invocation;
    protected AssignStmt assign = null;

    public MethodInvocationInstruction(Instruction instruction, int codeAddress) {
        super(instruction, codeAddress);
    }

    public void finalize(DexBody body, DexlibAbstractInstruction successor) {
        // defer final jimplification to move result
        if (successor instanceof MoveResultInstruction) {
//            MoveResultInstruction i = (MoveResultInstruction)successor;
//            i.setExpr(invocation);
//            if (lineNumber != -1)
//                i.setTag(new SourceLineNumberTag(lineNumber));
          assign = Jimple.v().newAssignStmt(body.getStoreResultLocal(), invocation);
          setUnit(assign);
          addTags(assign);
          body.add(assign);
          unit = assign;
        // this is a invoke statement (the MoveResult had to be the direct successor for an expression)
        } else {
            InvokeStmt invoke = Jimple.v().newInvokeStmt(invocation);
            setUnit(invoke);
            addTags(invoke);
            body.add(invoke);
            unit = invoke;
        }

		if (IDalvikTyper.ENABLE_DVKTYPER) {
			Debug.printDbg(IDalvikTyper.DEBUG, "constraint special invoke: "+ assign);
			
          if (invocation instanceof InstanceInvokeExpr) {
            Type t = invocation.getMethodRef().declaringClass().getType();
            DalvikTyper.v().setType(((InstanceInvokeExpr) invocation).getBaseBox(), t, true);
            //DalvikTyper.v().setObjectType(assign.getLeftOpBox());
          }
          int i = 0;
          for (Type pt: (List<Type>)invocation.getMethodRef().parameterTypes()) {
            DalvikTyper.v().setType(invocation.getArgBox(i++), pt, true);
          }
          int op = (int)instruction.getOpcode().value;
          if (assign != null) {
              DalvikTyper.v().setType(assign.getLeftOpBox(), invocation.getType(), false);
          }
          
        }
    }

    public Set<Type> introducedTypes() {
        Set<Type> types = new HashSet<Type>();
        MethodReference method = (MethodReference) (((ReferenceInstruction) instruction).getReference());

        types.add(DexType.toSoot(method.getDefiningClass()));
        types.add(DexType.toSoot(method.getReturnType()));
        List<? extends CharSequence> paramTypes = method.getParameterTypes();
        if (paramTypes != null)
            for (CharSequence type : paramTypes)
                types.add(DexType.toSoot(type.toString()));

        return types;
    }

    // overriden in InvokeStaticInstruction
    @Override
    boolean isUsedAsFloatingPoint(DexBody body, int register) {
        return isUsedAsFloatingPoint(body, register, false);
    }

    /**
     * Determine if register is used as floating point.
     *
     * Abstraction for static and non-static methods. Non-static methods need to ignore the first parameter (this)
     * @param isStatic if this method is static
     */
    protected boolean isUsedAsFloatingPoint(DexBody body, int register, boolean isStatic) {
        MethodReference item = (MethodReference) ((ReferenceInstruction) instruction).getReference();
        List<? extends CharSequence> paramTypes = item.getParameterTypes();
        List<Integer> regs = getUsedRegistersNums();
        if (paramTypes == null)
            return false;

        for (int i = 0, j = 0; i < regs.size(); i++, j++) {
            if (!isStatic && i == 0) {
                j--;
                continue;
            }

            if (regs.get(i) == register && isFloatLike(DexType.toSoot(paramTypes.get(j).toString())))
                return true;
            if (DexType.isWide(paramTypes.get(j).toString()))
                i++;
        }
        return false;
    }

    /**
     * Determine if register is used as object.
     *
     * Abstraction for static and non-static methods. Non-static methods need to ignore the first parameter (this)
     * @param isStatic if this method is static
     */
    protected boolean isUsedAsObject(DexBody body, int register, boolean isStatic) {
        MethodReference item = (MethodReference) ((ReferenceInstruction) instruction).getReference();
        List<? extends CharSequence> paramTypes = item.getParameterTypes();
        List<Integer> regs = getUsedRegistersNums();
        if (paramTypes == null)
            return false;

        // we call a method on the register
        if (!isStatic && regs.get(0) == register)
            return true;

        // we call a method with register as a reftype paramter
        for (int i = 0, j = 0; i < regs.size(); i++, j++) {
            if (!isStatic && i == 0) {
                j--;
                continue;
            }

            if (regs.get(i) == register && (DexType.toSoot(paramTypes.get(j).toString()) instanceof RefType))
                return true;
            if (DexType.isWide(paramTypes.get(j).toString()))
                i++;
        }
        return false;
    }

    /**
     * Return the SootMethodRef for the invoked method.
     *
     */
    protected SootMethodRef getSootMethodRef() {
        return getSootMethodRef(false);
    }

    /**
     * Return the static SootMethodRef for the invoked method.
     *
     */
    protected SootMethodRef getStaticSootMethodRef() {
        return getSootMethodRef(true);
    }

    /**
     * Return the SootMethodRef for the invoked method.
     *
     * @param isStatic for a static method ref
     */
    private SootMethodRef getSootMethodRef(boolean isStatic) {
        MethodReference mItem = (MethodReference) ((ReferenceInstruction) instruction).getReference();
        String tItem = mItem.getDefiningClass();

        String className = tItem;
        Debug.printDbg("tItem: ", tItem ," class name: ", className);
          if (className.startsWith("[")) {
            className = "java.lang.Object";
          } else {
            className = dottedClassName (tItem);
          }

        SootClass sc = SootResolver.v().makeClassRef(className);
        String methodName = mItem.getName();

        Type returnType = DexType.toSoot(mItem.getReturnType());
        List<Type> parameterTypes = new ArrayList<Type>();
        List<? extends CharSequence> paramTypes = mItem.getParameterTypes();
        if (paramTypes != null)
            for (CharSequence type : paramTypes)
                parameterTypes.add(DexType.toSoot(type.toString()));

        Debug.printDbg("sc: ", sc);
        Debug.printDbg("methodName: ", methodName);
        Debug.printDbg("parameterTypes: ");
        for (Type t: parameterTypes)
          Debug.printDbg(" t: ", t);
        Debug.printDbg("returnType: ", returnType);
        Debug.printDbg("isStatic: ", isStatic);
        return Scene.v().makeMethodRef(sc, methodName, parameterTypes, returnType, isStatic);
    }

    /**
     * Build the parameters of this invocation.
     *
     * The first parameter is the instance for which the method is invoked (if method is non-static).
     *
     * @param body the body to build for and into
     * @param isStatic if method is static
     *
     * @return the converted parameters
     */
    protected List<Local> buildParameters(DexBody body, boolean isStatic) {
        MethodReference item = (MethodReference) ((ReferenceInstruction) instruction).getReference();
        List<? extends CharSequence> paramTypes = item.getParameterTypes();

        List<Local> parameters = new ArrayList<Local>();
        List<Integer> regs = getUsedRegistersNums();

        Debug.printDbg(" [methodIdItem]: ", item);
        Debug.printDbg(" params types:");
        if (paramTypes != null) {
          for (CharSequence t: paramTypes) {
            Debug.printDbg(" t: ", t);
          }
        }
        Debug.printDbg(" used registers (", regs.size() ,"): ");
        for (int i: regs) {
          Debug.printDbg( " r: ", i);
        }
        // i: index for register
        // j: index for parameter type
        for (int i = 0, j = 0; i < regs.size(); i++, j++) {
            parameters.add (body.getRegisterLocal (regs.get(i)));
            // if method is non-static the first parameter is the instance
            // pointer and has no corresponding parameter type
            if (!isStatic && i == 0) {
                j--;
                continue;
            }
            // If current parameter is wide ignore the next register.
            // No need to increment j as there is one parameter type
            // for those two registers.
            if (paramTypes != null && DexType.isWide(paramTypes.get(j).toString())) {
                i++;
            }

        }
        return parameters;
    }

    /**
     * Return the indices used in this instruction.
     *
     * @return a list of register indices
     */
    protected List<Integer> getUsedRegistersNums() {
        if (instruction instanceof Instruction35c)
            return getUsedRegistersNums((Instruction35c) instruction);
        else if (instruction instanceof Instruction3rc)
            return getUsedRegistersNums((Instruction3rc) instruction);
        throw new RuntimeException("Instruction is neither a InvokeInstruction nor a InvokeRangeInstruction");
    }

    /**
     * Return the indices used in the given instruction.
     *
     * @param instruction a invocation instruction
     * @return a list of register indices
     */
    private static List<Integer> getUsedRegistersNums(Instruction35c instruction) {
        int[] regs = {
            instruction.getRegisterC(),
            instruction.getRegisterD(),
            instruction.getRegisterE(),
            instruction.getRegisterF(),
            instruction.getRegisterG(),
        };
        List<Integer> l = new ArrayList<Integer>();
        for (int i = 0; i < instruction.getRegisterCount(); i++)
            l.add(regs[i]);
        return l;
    }

    /**
     * Return the indices used in the given instruction.
     *
     * @param instruction a range invocation instruction
     * @return a list of register indices
     */
    private static List<Integer> getUsedRegistersNums(Instruction3rc instruction) {
        List<Integer> regs = new ArrayList<Integer>();
        int start = instruction.getStartRegister();
        for (int i = start; i < start + instruction.getRegisterCount(); i++)
            regs.add(i);

        return regs;
    }
}
