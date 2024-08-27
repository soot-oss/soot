package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 - Jennifer Lhotak
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

import java.util.Objects;

import org.objectweb.asm.Opcodes;

import soot.RefType;
import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.Type;
import soot.dotnet.types.DotNetBasicTypes;
import soot.options.Options;
import soot.util.Switch;

public class MethodHandle extends Constant {

  private static final long serialVersionUID = -7948291265532721191L;

  public static enum Kind {
    REF_GET_FIELD(Opcodes.H_GETFIELD, "REF_GET_FIELD"), REF_GET_FIELD_STATIC(Opcodes.H_GETSTATIC,
        "REF_GET_FIELD_STATIC"), REF_PUT_FIELD(Opcodes.H_PUTFIELD, "REF_PUT_FIELD"), REF_PUT_FIELD_STATIC(
            Opcodes.H_PUTSTATIC, "REF_PUT_FIELD_STATIC"), REF_INVOKE_VIRTUAL(Opcodes.H_INVOKEVIRTUAL,
                "REF_INVOKE_VIRTUAL"), REF_INVOKE_STATIC(Opcodes.H_INVOKESTATIC, "REF_INVOKE_STATIC"), REF_INVOKE_SPECIAL(
                    Opcodes.H_INVOKESPECIAL, "REF_INVOKE_SPECIAL"), REF_INVOKE_CONSTRUCTOR(Opcodes.H_NEWINVOKESPECIAL,
                        "REF_INVOKE_CONSTRUCTOR"), REF_INVOKE_INTERFACE(Opcodes.H_INVOKEINTERFACE, "REF_INVOKE_INTERFACE");

    private final int val;
    private final String valStr;

    private Kind(int val, String valStr) {
      this.val = val;
      this.valStr = valStr;
    }

    @Override
    public String toString() {
      return valStr;
    }

    public int getValue() {
      return val;
    }

    public static Kind getKind(int kind) {
      for (Kind k : Kind.values()) {
        if (k.getValue() == kind) {
          return k;
        }
      }
      throw new RuntimeException("Error: No method handle kind for value '" + kind + "'.");
    }

    public static Kind getKind(String kind) {
      for (Kind k : Kind.values()) {
        if (k.toString().equals(kind)) {
          return k;
        }
      }
      throw new RuntimeException("Error: No method handle kind for value '" + kind + "'.");
    }
  }

  protected final SootFieldRef fieldRef;
  protected final SootMethodRef methodRef;
  protected final int kind;

  private MethodHandle(SootMethodRef ref, int kind) {
    this.methodRef = ref;
    this.kind = kind;
    this.fieldRef = null;
  }

  private MethodHandle(SootFieldRef ref, int kind) {
    this.fieldRef = ref;
    this.kind = kind;
    this.methodRef = null;
  }

  public static MethodHandle v(SootMethodRef ref, int kind) {
    return new MethodHandle(ref, kind);
  }

  public static MethodHandle v(SootFieldRef ref, int kind) {
    return new MethodHandle(ref, kind);
  }

  @Override
  public String toString() {
    return "methodhandle: \"" + getKindString() + "\" "
        + (methodRef == null ? Objects.toString(fieldRef) : Objects.toString(methodRef));
  }

  @Override
  public Type getType() {
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      return isMethodRef() ? RefType.v(DotNetBasicTypes.SYSTEM_RUNTIMEMETHODHANDLE)
          : RefType.v(DotNetBasicTypes.SYSTEM_RUNTIMEFIELDHANDLE);
    }
    return RefType.v("java.lang.invoke.MethodHandle");
  }

  public SootMethodRef getMethodRef() {
    return methodRef;
  }

  public SootFieldRef getFieldRef() {
    return fieldRef;
  }

  public int getKind() {
    return kind;
  }

  public String getKindString() {
    return Kind.getKind(kind).toString();
  }

  public boolean isFieldRef() {
    return isFieldRef(kind);
  }

  public static boolean isFieldRef(int kind) {
    return kind == Kind.REF_GET_FIELD.getValue() || kind == Kind.REF_GET_FIELD_STATIC.getValue()
        || kind == Kind.REF_PUT_FIELD.getValue() || kind == Kind.REF_PUT_FIELD_STATIC.getValue();
  }

  public boolean isMethodRef() {
    return isMethodRef(kind);
  }

  public static boolean isMethodRef(int kind) {
    return kind == Kind.REF_INVOKE_VIRTUAL.getValue() || kind == Kind.REF_INVOKE_STATIC.getValue()
        || kind == Kind.REF_INVOKE_SPECIAL.getValue() || kind == Kind.REF_INVOKE_CONSTRUCTOR.getValue()
        || kind == Kind.REF_INVOKE_INTERFACE.getValue();
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseMethodHandle(this);
  }

  @Override
  public int hashCode() {
    final int prime = 17;
    int result = 31;
    result = prime * result + Objects.hashCode(methodRef);
    result = prime * result + Objects.hashCode(fieldRef);
    result = prime * result + kind;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    MethodHandle other = (MethodHandle) obj;
    return Objects.equals(this.methodRef, other.methodRef) && Objects.equals(this.fieldRef, other.fieldRef);
  }
}
