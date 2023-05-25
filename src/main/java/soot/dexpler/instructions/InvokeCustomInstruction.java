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

import org.jf.dexlib2.MethodHandleType;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.reference.CallSiteReference;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.iface.reference.MethodHandleReference;
import org.jf.dexlib2.iface.reference.MethodProtoReference;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.iface.value.BooleanEncodedValue;
import org.jf.dexlib2.iface.value.ByteEncodedValue;
import org.jf.dexlib2.iface.value.CharEncodedValue;
import org.jf.dexlib2.iface.value.DoubleEncodedValue;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.iface.value.FloatEncodedValue;
import org.jf.dexlib2.iface.value.IntEncodedValue;
import org.jf.dexlib2.iface.value.LongEncodedValue;
import org.jf.dexlib2.iface.value.MethodHandleEncodedValue;
import org.jf.dexlib2.iface.value.MethodTypeEncodedValue;
import org.jf.dexlib2.iface.value.NullEncodedValue;
import org.jf.dexlib2.iface.value.ShortEncodedValue;
import org.jf.dexlib2.iface.value.StringEncodedValue;
import org.jf.dexlib2.iface.value.TypeEncodedValue;

import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Value;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.jimple.ClassConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.MethodHandle;
import soot.jimple.MethodHandle.Kind;
import soot.jimple.MethodType;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;

public class InvokeCustomInstruction extends MethodInvocationInstruction {

  public InvokeCustomInstruction(Instruction instruction, int codeAddress) {
    super(instruction, codeAddress);
  }

  @Override
  public void jimplify(DexBody body) {
    CallSiteReference callSiteReference = (CallSiteReference) ((ReferenceInstruction) instruction).getReference();
    Reference bootstrapRef = callSiteReference.getMethodHandle().getMemberReference();

    // According to the specification there are two types of references for invoke-custom and method and field type
    if (bootstrapRef instanceof MethodReference) {
      SootMethodRef bootstrapMethodRef = getBootStrapSootMethodRef();
      Kind bootStrapKind = dexToSootMethodHandleKind(callSiteReference.getMethodHandle().getMethodHandleType());

      // The bootstrap method has three required dynamic arguments and the rest are optional but
      // must always be constants
      List<Value> bootstrapValues = constantEncodedValuesToValues(callSiteReference.getExtraArguments());
      SootMethodRef methodRef = getCustomSootMethodRef();
      // The method prototype only includes the method arguments and no invoking object so treat like static
      List<Local> methodArgs = buildParameters(body, callSiteReference.getMethodProto().getParameterTypes(), true);

      invocation = Jimple.v().newDynamicInvokeExpr(bootstrapMethodRef, bootstrapValues, methodRef, bootStrapKind.getValue(),
          methodArgs);
      body.setDanglingInstruction(this);
    } else if (bootstrapRef instanceof FieldReference) {
      // It should not be possible for the boot strap method to be a field reference type but I
      // include a separate check to alert us if this ever does occur.
      // To set/get a field using invoke-custom, a field MethodHandle must be passed into
      // the invoke custom as an extra argument and called using invoke-polymorphic inside the
      // boot strap method.
      throw new RuntimeException("Error: Unexpected FieldReference type for boot strap method.");
    } else {
      throw new RuntimeException("Error: Unhandled MethodHandleReference of type '"
          + callSiteReference.getMethodHandle().getMethodHandleType() + "'");
    }
  }

  /**
   * Convert a list of constant EncodedValues to a list of constant Values. This is used to convert the extra bootstrap args
   * (which are all constants) into Jimple Values.
   * 
   * @param in
   *          A list of constant EncodedValues
   * @return A list of constant Values
   */
  private List<Value> constantEncodedValuesToValues(List<? extends EncodedValue> in) {
    List<Value> out = new ArrayList<>();
    for (EncodedValue ev : in) {
      if (ev instanceof BooleanEncodedValue) {
        out.add(IntConstant.v(((BooleanEncodedValue) ev).getValue() ? 1 : 0));
      } else if (ev instanceof ByteEncodedValue) {
        out.add(IntConstant.v(((ByteEncodedValue) ev).getValue()));
      } else if (ev instanceof CharEncodedValue) {
        out.add(IntConstant.v(((CharEncodedValue) ev).getValue()));
      } else if (ev instanceof DoubleEncodedValue) {
        out.add(DoubleConstant.v(((DoubleEncodedValue) ev).getValue()));
      } else if (ev instanceof FloatEncodedValue) {
        out.add(FloatConstant.v(((FloatEncodedValue) ev).getValue()));
      } else if (ev instanceof IntEncodedValue) {
        out.add(IntConstant.v(((IntEncodedValue) ev).getValue()));
      } else if (ev instanceof LongEncodedValue) {
        out.add(LongConstant.v(((LongEncodedValue) ev).getValue()));
      } else if (ev instanceof ShortEncodedValue) {
        out.add(IntConstant.v(((ShortEncodedValue) ev).getValue()));
      } else if (ev instanceof StringEncodedValue) {
        out.add(StringConstant.v(((StringEncodedValue) ev).getValue()));
      } else if (ev instanceof NullEncodedValue) {
        out.add(NullConstant.v());
      } else if (ev instanceof MethodTypeEncodedValue) {
        MethodProtoReference protRef = ((MethodTypeEncodedValue) ev).getValue();
        out.add(MethodType.v(convertParameterTypes(protRef.getParameterTypes()), DexType.toSoot(protRef.getReturnType())));
      } else if (ev instanceof TypeEncodedValue) {
        out.add(ClassConstant.v(((TypeEncodedValue) ev).getValue()));
      } else if (ev instanceof MethodHandleEncodedValue) {
        MethodHandleReference mh = ((MethodHandleEncodedValue) ev).getValue();
        Reference ref = mh.getMemberReference();
        Kind kind = dexToSootMethodHandleKind(mh.getMethodHandleType());
        MethodHandle handle;
        if (ref instanceof MethodReference) {
          handle = MethodHandle.v(getSootMethodRef((MethodReference) ref, kind), kind.getValue());
        } else if (ref instanceof FieldReference) {
          handle = MethodHandle.v(getSootFieldRef((FieldReference) ref, kind), kind.getValue());
        } else {
          throw new RuntimeException("Error: Unhandled method reference type " + ref.getClass().toString() + ".");
        }
        out.add(handle);
      } else {
        throw new RuntimeException("Error: Unhandled constant type '" + ev.getClass().toString()
            + "' when parsing bootstrap arguments in the call site reference.");
      }
    }
    return out;
  }

  private Kind dexToSootMethodHandleKind(int kind) {
    switch (kind) {
      case MethodHandleType.INSTANCE_GET:
        return MethodHandle.Kind.REF_GET_FIELD;
      case MethodHandleType.STATIC_GET:
        return MethodHandle.Kind.REF_GET_FIELD_STATIC;
      case MethodHandleType.INSTANCE_PUT:
        return MethodHandle.Kind.REF_PUT_FIELD;
      case MethodHandleType.STATIC_PUT:
        return MethodHandle.Kind.REF_PUT_FIELD_STATIC;
      case MethodHandleType.INVOKE_INSTANCE:
        return MethodHandle.Kind.REF_INVOKE_VIRTUAL;
      case MethodHandleType.INVOKE_STATIC:
        return MethodHandle.Kind.REF_INVOKE_STATIC;
      case MethodHandleType.INVOKE_DIRECT:
        return MethodHandle.Kind.REF_INVOKE_SPECIAL;
      case MethodHandleType.INVOKE_CONSTRUCTOR:
        return MethodHandle.Kind.REF_INVOKE_CONSTRUCTOR;
      case MethodHandleType.INVOKE_INTERFACE:
        return MethodHandle.Kind.REF_INVOKE_INTERFACE;
      default:
        throw new RuntimeException("Error: Unknown kind '" + kind + "' for method handle");
    }
  }

  /**
   * Return a dummy SootMethodRef for the method invoked by a invoke-custom instruction.
   */
  protected SootMethodRef getCustomSootMethodRef() {
    CallSiteReference callSiteReference = (CallSiteReference) ((ReferenceInstruction) instruction).getReference();
    SootClass dummyclass = Scene.v().getSootClass(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME);
    String methodName = callSiteReference.getMethodName();
    MethodProtoReference methodRef = callSiteReference.getMethodProto();
    // No reference kind stored in invoke custom instruction for the actual
    // method being invoked so default to static
    return getSootMethodRef(dummyclass, methodName, methodRef.getReturnType(), methodRef.getParameterTypes(),
        Kind.REF_INVOKE_STATIC);
  }

  /**
   * Return a SootMethodRef for the bootstrap method of an invoke-custom instruction.
   */
  protected SootMethodRef getBootStrapSootMethodRef() {
    MethodHandleReference mh = ((CallSiteReference) ((ReferenceInstruction) instruction).getReference()).getMethodHandle();
    return getSootMethodRef((MethodReference) mh.getMemberReference(), dexToSootMethodHandleKind(mh.getMethodHandleType()));
  }

}
