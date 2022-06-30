package soot.dexpler.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 * 
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 * 
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.Arrays;
import java.util.List;

import org.jf.dexlib2.iface.instruction.DualReferenceInstruction;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.reference.MethodProtoReference;

import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootMethodRef;
import soot.Unit;
import soot.dexpler.DexBody;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JimpleLocal;

public class InvokePolymorphicInstruction extends MethodInvocationInstruction {

  public InvokePolymorphicInstruction(Instruction instruction, int codeAddress) {
    super(instruction, codeAddress);
  }

  /*
   * Instruction Format for invoke-polymorphic invoke-polymorphic MH.invoke, prototype, {mh, [args]} - MH.invoke - a method
   * handle (i.e. MethodReference in dexlib2) for either the method invoke or invokeExact - prototype - a description of the
   * types for the arguments being passed into invoke or invokeExact and their return type - {mh, [args]} - A list of one or
   * more arguments included in the instruction. The first argument (mh) is always a reference to the MethodHandle object
   * that invoke or invokeExact is called on. The remaining arguments are references to the objects passed into the call to
   * invoke or invokeExact. This is similar to how invoke-virtual functions.
   * 
   * The invoke-polymorphic instruction behaves similar to how reflection functions from a coder standpoint it is just
   * faster. The actual function being called depends on how the mh object is constructed at runtime (i.e. the method name,
   * parameter types and number, return type, and calling object). The prototype included in invoke-polymorphic reflects the
   * types of the arguments passed into invoke or invokeExact and should match the the types of the parameters of the actual
   * method being invoked from a class hierarchy standpoint. However, they are included mainly so the VM knows the types of
   * the variables being passed into the invoke and invokeExact method for sizing purposes (i.e. so the data can be read
   * properly). The actual parameter types for the method invoked is determined at runtime.
   * 
   * From a static analysis standpoint there is no way to tell exactly what method is being called using the
   * invoke-polymorphic instruction. A more complex context sensitive analysis would be required to determine the exact
   * method being called. A less complex analysis could be performed to determine all possible methods that could be invoked
   * by this instruction. However, neither of these options should really be performed when translating dex to bytecode.
   * Instead, they should be performed later on a per-analysis basis. As there is no equivalent instruction to
   * invoke-polymorphic in normal Java bytecode, we simply translate the instruction to a normal invoke-virtual instruction
   * where invoke or invokeExact is the method being called, mh is the object it is being called on, and args are the
   * arguments for the method call if any. This decision does lose the prototype data included in the instruction; however,
   * as described above it does not really aid us in determining the method being called and resolving the method being
   * called can still be done using other data in the code.
   * 
   * See https://www.pnfsoftware.com/blog/android-o-and-dex-version-38-new-dalvik-opcodes-to-support-dynamic-invocation/ for
   * more information on this instruction and the class lang/invoke/Transformers.java for examples of invoke-polymorhpic
   * instructions whose prototype will does not match the actual method being invoked.
   */
  @Override
  public void jimplify(DexBody body) {
    SootMethodRef ref = getVirtualSootMethodRef();
    if (ref.declaringClass().isInterface()) {
      ref = getInterfaceSootMethodRef();
    }

    // The invoking object will always be included in the parameter types here
    List<Local> temp = buildParameters(body,
        ((MethodProtoReference) ((DualReferenceInstruction) instruction).getReference2()).getParameterTypes(), false);
    List<Local> parms = temp.subList(1, temp.size());
    Local invoker = temp.get(0);

    // Only box the arguments into an array if there are arguments and if they are not
    // already in some kind of array
    if (parms.size() > 0 && !(parms.size() == 1 && parms.get(0) instanceof ArrayType)) {
      Body b = body.getBody();
      PatchingChain<Unit> units = b.getUnits();

      // Return type for invoke and invokeExact is Object and paramater type is Object[]
      RefType rf = Scene.v().getObjectType();
      Local newArrL = new JimpleLocal("$u" + (b.getLocalCount() + 1), ArrayType.v(rf, 1));
      b.getLocals().add(newArrL);
      JAssignStmt newArr = new JAssignStmt(newArrL, new JNewArrayExpr(rf, IntConstant.v(parms.size())));
      units.add(newArr);

      int i = 0;
      for (Local l : parms) {
        units.add(new JAssignStmt(new JArrayRef(newArrL, IntConstant.v(i)), l));
        i++;
      }
      parms = Arrays.asList(newArrL);
    }

    if (ref.declaringClass().isInterface()) {
      invocation = Jimple.v().newInterfaceInvokeExpr(invoker, ref, parms);
    } else {
      invocation = Jimple.v().newVirtualInvokeExpr(invoker, ref, parms);
    }
    body.setDanglingInstruction(this);
  }

}
