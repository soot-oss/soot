package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.io.Serializable;

import soot.util.Numberable;
import soot.util.Switch;
import soot.util.Switchable;

/** Represents types within Soot, eg <code>int</code>, <code>java.lang.String</code>. */
@SuppressWarnings("serial")
public abstract class Type implements Switchable, Serializable, Numberable {
  public Type() {
    Scene.v().getTypeNumberer().add(this);
  }

  /** Returns a textual representation of this type. */
  public abstract String toString();

  /** Returns a textual (and quoted as needed) representation of this type for serialization, e.g. to .jimple format */
  public String toQuotedString() {
    return toString();
  }

  /**
   * Returns a textual (and quoted as needed) representation of this type for serialization, e.g. to .jimple format Replaced
   * by toQuotedString; only here for backwards compatibility.
   */
  @Deprecated
  public String getEscapedName() {
    return toQuotedString();
  }

  /** Converts the int-like types (short, byte, boolean and char) to IntType. */
  public static Type toMachineType(Type t) {
    if (t.equals(ShortType.v()) || t.equals(ByteType.v()) || t.equals(BooleanType.v()) || t.equals(CharType.v())) {
      return IntType.v();
    } else {
      return t;
    }
  }

  /** Returns the least common superclass of this type and other. */
  public Type merge(Type other, Scene cm) {
    // method overriden in subclasses UnknownType and RefType
    throw new RuntimeException("illegal type merge: " + this + " and " + other);
  }

  /** Method required for use of Switchable. */
  public void apply(Switch sw) {
  }

  public void setArrayType(ArrayType at) {
    arrayType = at;
  }

  public ArrayType getArrayType() {
    return arrayType;
  }

  public ArrayType makeArrayType() {
    return ArrayType.v(this, 1);
  }

  /**
   * Returns <code>true</code> if this type is allowed to appear in final (clean) Jimple code.
   *
   * @return
   */
  public boolean isAllowedInFinalCode() {
    return false;
  }

  public final int getNumber() {
    return number;
  }

  public final void setNumber(int number) {
    this.number = number;
  }

  protected ArrayType arrayType;
  private int number = 0;
}
