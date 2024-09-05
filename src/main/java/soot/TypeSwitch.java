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

/** Implements Switchable on base Java types. */
public class TypeSwitch<T> implements ITypeSwitch {

  T result;

  @Override
  public void caseArrayType(ArrayType t) {
    defaultCase(t);
  }

  @Override
  public void caseBooleanType(BooleanType t) {
    defaultCase(t);
  }

  @Override
  public void caseByteType(ByteType t) {
    defaultCase(t);
  }

  @Override
  public void caseCharType(CharType t) {
    defaultCase(t);
  }

  @Override
  public void caseDoubleType(DoubleType t) {
    defaultCase(t);
  }

  @Override
  public void caseFloatType(FloatType t) {
    defaultCase(t);
  }

  @Override
  public void caseIntType(IntType t) {
    defaultCase(t);
  }

  @Override
  public void caseLongType(LongType t) {
    defaultCase(t);
  }

  @Override
  public void caseRefType(RefType t) {
    defaultCase(t);
  }

  @Override
  public void caseShortType(ShortType t) {
    defaultCase(t);
  }

  @Override
  public void caseStmtAddressType(StmtAddressType t) {
    defaultCase(t);
  }

  @Override
  public void caseUnknownType(UnknownType t) {
    defaultCase(t);
  }

  @Override
  public void caseVoidType(VoidType t) {
    defaultCase(t);
  }

  @Override
  public void caseAnySubType(AnySubType t) {
    defaultCase(t);
  }

  @Override
  public void caseNullType(NullType t) {
    defaultCase(t);
  }

  @Override
  public void caseErroneousType(ErroneousType t) {
    defaultCase(t);
  }

  @Override
  public void caseUByteType(UByteType t) {
    defaultCase(t);
  }

  @Override
  public void caseULongType(ULongType t) {
    defaultCase(t);
  }

  @Override
  public void caseUShortType(UShortType t) {
    defaultCase(t);
  }

  @Override
  public void caseUIntType(UIntType t) {
    defaultCase(t);
  }

  @Override
  public void caseDecimalType(DecimalType t) {
    defaultCase(t);
  }

  @Override
  public void defaultCase(Type t) {
  }

  /**
   * @deprecated Replaced by defaultCase(Type)
   * @see #defaultCase(Type)
   */
  @Deprecated
  @Override
  public void caseDefault(Type t) {
  }

  public void setResult(T result) {
    this.result = result;
  }

  public T getResult() {
    return this.result;
  }
}
