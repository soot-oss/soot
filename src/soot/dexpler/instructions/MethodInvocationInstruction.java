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

import org.jf.dexlib.MethodIdItem;
import org.jf.dexlib.ProtoIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.TypeListItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.InstructionWithReference;
import org.jf.dexlib.Code.Format.Instruction35c;
import org.jf.dexlib.Code.Format.Instruction3rc;

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
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.internal.JAssignStmt;

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
          tagWithLineNumber(assign);
          body.add(assign);
          unit = assign;
        // this is a invoke statement (the MoveResult had to be the direct successor for an expression)
        } else {
            InvokeStmt invoke = Jimple.v().newInvokeStmt(invocation);
            setUnit(invoke);
            tagWithLineNumber(invoke);
            body.add(invoke);
            unit = invoke;
        }
        
		}
		public void getConstraint(IDalvikTyper dalvikTyper) {
				if (IDalvikTyper.ENABLE_DVKTYPER) {
          if (invocation instanceof InstanceInvokeExpr) {
            Type t = invocation.getMethodRef().declaringClass().getType();
            dalvikTyper.setType(((InstanceInvokeExpr) invocation).getBaseBox(), t);
            //dalvikTyper.setObjectType(assign.getLeftOpBox());
          }
          int i = 0;
          for (Object pt: invocation.getMethodRef().parameterTypes()) {
            dalvikTyper.setType(invocation.getArgBox(i++), (Type)pt);
          }
          int op = (int)instruction.opcode.value;
          if (assign != null) {
            dalvikTyper.captureAssign((JAssignStmt)assign, op);
          }
        }
    }

    public Set<DexType> introducedTypes() {
        Set<DexType> types = new HashSet<DexType>();
        MethodIdItem method = (MethodIdItem) (((InstructionWithReference) instruction).getReferencedItem());
        types.add(new DexType(method.getContainingClass()));
        ProtoIdItem prototype = method.getPrototype();
        types.add(new DexType(prototype.getReturnType()));
        List<TypeIdItem> paramTypes = TypeListItem.getTypes(prototype.getParameters());
        if (paramTypes != null)
            for (TypeIdItem type : paramTypes)
                types.add(new DexType(type));

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
        MethodIdItem item = (MethodIdItem) ((InstructionWithReference) instruction).getReferencedItem();
        List<TypeIdItem> paramTypes = TypeListItem.getTypes(item.getPrototype().getParameters());
        List<Integer> regs = getUsedRegistersNums();
        if (paramTypes == null)
            return false;

        for (int i = 0, j = 0; i < regs.size(); i++, j++) {
            if (!isStatic && i == 0) {
                j--;
                continue;
            }

            if (regs.get(i) == register && isFloatLike(DexType.toSoot(paramTypes.get(j))))
                return true;
            if (DexType.isWide(paramTypes.get(j)))
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
        MethodIdItem item = (MethodIdItem) ((InstructionWithReference) instruction).getReferencedItem();
        List<TypeIdItem> paramTypes = TypeListItem.getTypes(item.getPrototype().getParameters());
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

            if (regs.get(i) == register && (DexType.toSoot(paramTypes.get(j)) instanceof RefType))
                return true;
            if (DexType.isWide(paramTypes.get(j)))
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
        MethodIdItem mItem = (MethodIdItem) ((InstructionWithReference) instruction).getReferencedItem();
        String tItem = mItem.getContainingClass().getTypeDescriptor();
 
        String className = tItem;
        Debug.printDbg("tItem: ", tItem ," class name: ", className);
          if (className.startsWith("[")) {
            className = "java.lang.Object";
          } else {
            className = dottedClassName (tItem);
          }
        
        SootClass sc = SootResolver.v().makeClassRef(className);
        String methodName = mItem.getMethodName().getStringValue();

        ProtoIdItem prototype = mItem.getPrototype();
        Type returnType = DexType.toSoot(prototype.getReturnType());
        List<Type> parameterTypes = new ArrayList<Type>();
        List<TypeIdItem> paramTypes = TypeListItem.getTypes(prototype.getParameters());
        if (paramTypes != null)
            for (TypeIdItem type : paramTypes)
                parameterTypes.add(DexType.toSoot(type));

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
        MethodIdItem item = (MethodIdItem) ((InstructionWithReference) instruction).getReferencedItem();
        List<TypeIdItem> paramTypes = TypeListItem.getTypes(item.getPrototype().getParameters());

        List<Local> parameters = new ArrayList<Local>();
        List<Integer> regs = getUsedRegistersNums();

        Debug.printDbg(" [methodIdItem]: ", item);
        Debug.printDbg(" params types:");
        if (paramTypes != null) {       
          for (TypeIdItem t: paramTypes) {
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
            if (paramTypes != null && DexType.isWide(paramTypes.get(j))) {
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
            instruction.getRegisterD(),
            instruction.getRegisterE(),
            instruction.getRegisterF(),
            instruction.getRegisterG(),
            instruction.getRegisterA()
        };
        List<Integer> l = new ArrayList<Integer>();
        for (int i = 0; i < instruction.getRegCount(); i++)
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
        for (int i = start; i < start + instruction.getRegCount(); i++)
            regs.add(i);

        return regs;
    }
}
