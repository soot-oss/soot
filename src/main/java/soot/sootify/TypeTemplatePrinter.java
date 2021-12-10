package soot.sootify;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2010 Hela Oueslati, Eric Bodden
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

import soot.AnySubType;
import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.ErroneousType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.NullType;
import soot.RefType;
import soot.ShortType;
import soot.StmtAddressType;
import soot.Type;
import soot.TypeSwitch;
import soot.UnknownType;
import soot.VoidType;

public class TypeTemplatePrinter extends TypeSwitch {

  private String varName;
  private final TemplatePrinter p;

  public void printAssign(String v, Type t) {
    String oldName = varName;
    varName = v;
    t.apply(this);
    varName = oldName;
  }

  public TypeTemplatePrinter(TemplatePrinter p) {
    this.p = p;
  }

  public void setVariableName(String name) {
    this.varName = name;
  }

  private void emit(String rhs) {
    p.println("Type " + varName + " = " + rhs + ';');
  }

  private void emitSpecial(String type, String rhs) {
    p.println(type + ' ' + varName + " = " + rhs + ';');
  }

  @Override
  public void caseAnySubType(AnySubType t) {
    throw new IllegalArgumentException("cannot print this type");
  }

  @Override
  public void caseArrayType(ArrayType t) {
    printAssign("baseType", t.getElementType());

    p.println("int numDimensions=" + t.numDimensions + ';');

    emit("ArrayType.v(baseType, numDimensions)");
  }

  @Override
  public void caseBooleanType(BooleanType t) {
    emit("BooleanType.v()");
  }

  @Override
  public void caseByteType(ByteType t) {
    emit("ByteType.v()");
  }

  @Override
  public void caseCharType(CharType t) {
    emit("CharType.v()");
  }

  @Override
  public void defaultCase(Type t) {
    throw new IllegalArgumentException("cannot print this type");
  }

  @Override
  public void caseDoubleType(DoubleType t) {
    emit("DoubleType.v()");
  }

  @Override
  public void caseErroneousType(ErroneousType t) {
    throw new IllegalArgumentException("cannot print this type");
  }

  @Override
  public void caseFloatType(FloatType t) {
    emit("FloatType.v()");
  }

  @Override
  public void caseIntType(IntType t) {
    emit("IntType.v()");
  }

  @Override
  public void caseLongType(LongType t) {
    emit("LongType.v()");
  }

  @Override
  public void caseNullType(NullType t) {
    emit("NullType.v()");
  }

  @Override
  public void caseRefType(RefType t) {
    emitSpecial("RefType", "RefType.v(\"" + t.getClassName() + "\")");
  }

  @Override
  public void caseShortType(ShortType t) {
    emit("ShortType.v()");
  }

  @Override
  public void caseStmtAddressType(StmtAddressType t) {
    throw new IllegalArgumentException("cannot print this type");
  }

  @Override
  public void caseUnknownType(UnknownType t) {
    throw new IllegalArgumentException("cannot print this type");
  }

  @Override
  public void caseVoidType(VoidType t) {
    emit("VoidType.v()");
  }
}
