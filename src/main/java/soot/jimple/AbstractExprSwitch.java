package soot.jimple;

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

public abstract class AbstractExprSwitch<T> implements ExprSwitch {

  T result;

  @Override
  public void caseAddExpr(AddExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseAndExpr(AndExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseCmpExpr(CmpExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseCmpgExpr(CmpgExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseCmplExpr(CmplExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseDivExpr(DivExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseEqExpr(EqExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseNeExpr(NeExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseGeExpr(GeExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseGtExpr(GtExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseLeExpr(LeExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseLtExpr(LtExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseMulExpr(MulExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseOrExpr(OrExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseRemExpr(RemExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseShlExpr(ShlExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseShrExpr(ShrExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseUshrExpr(UshrExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseSubExpr(SubExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseXorExpr(XorExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseStaticInvokeExpr(StaticInvokeExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseCastExpr(CastExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseInstanceOfExpr(InstanceOfExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseNewArrayExpr(NewArrayExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseNewExpr(NewExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseLengthExpr(LengthExpr v) {
    defaultCase(v);
  }

  @Override
  public void caseNegExpr(NegExpr v) {
    defaultCase(v);
  }

  @Override
  public void defaultCase(Object obj) {
  }

  public void setResult(T result) {
    this.result = result;
  }

  public T getResult() {
    return result;
  }
}
