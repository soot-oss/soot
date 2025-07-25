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

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib2.iface.instruction.DualReferenceInstruction;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.reference.MethodProtoReference;

import soot.Local;
import soot.Scene;
import soot.SootMethodRef;
import soot.Type;
import soot.dexpler.DexBody;
import soot.dexpler.Util;
import soot.jimple.Jimple;

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
   * We handle this similar to MethodHandle invocation calls in the JVM bytecode now.
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

    MethodProtoReference r = ((MethodProtoReference) ((DualReferenceInstruction) instruction).getReference2());

    List<? extends CharSequence> pt = r.getParameterTypes();
    ArrayList<Type> types = new ArrayList<>(pt.size());
    for (int i = 0; i < pt.size(); i++) {
      types.add(Util.getType(pt.get(i).toString()));
    }
    Type retType = Util.getType(r.getReturnType());
    // retarget due to PolymorphicSignature annotation
    ref = Scene.v().makeMethodRef(ref.getDeclaringClass(), ref.name(), types, retType, false);
    // The invoking object will always be included in the parameter types here
    List<Local> temp = buildParameters(body, pt, false);
    List<Local> parms = temp.subList(1, temp.size());
    Local invoker = temp.get(0);

    if (ref.declaringClass().isInterface()) {
      invocation = Jimple.v().newInterfaceInvokeExpr(invoker, ref, parms);
    } else {
      invocation = Jimple.v().newVirtualInvokeExpr(invoker, ref, parms);
    }
    body.setDanglingInstruction(this);
  }

}
