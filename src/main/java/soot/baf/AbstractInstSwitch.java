package soot.baf;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Timothy Hoffman
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

/**
 * @author Timothy Hoffman
 */
public abstract class AbstractInstSwitch<T> implements InstSwitch {

  protected T result;

  public void setResult(T result) {
    this.result = result;
  }

  public T getResult() {
    return result;
  }

  public void defaultCase(Object obj) {
  }

  @Override
  public void caseReturnVoidInst(ReturnVoidInst i) {
    defaultCase(i);
  }

  @Override
  public void caseReturnInst(ReturnInst i) {
    defaultCase(i);
  }

  @Override
  public void caseNopInst(NopInst i) {
    defaultCase(i);
  }

  @Override
  public void caseGotoInst(GotoInst i) {
    defaultCase(i);
  }

  @Override
  public void caseJSRInst(JSRInst i) {
    defaultCase(i);
  }

  @Override
  public void casePushInst(PushInst i) {
    defaultCase(i);
  }

  @Override
  public void casePopInst(PopInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIdentityInst(IdentityInst i) {
    defaultCase(i);
  }

  @Override
  public void caseStoreInst(StoreInst i) {
    defaultCase(i);
  }

  @Override
  public void caseLoadInst(LoadInst i) {
    defaultCase(i);
  }

  @Override
  public void caseArrayWriteInst(ArrayWriteInst i) {
    defaultCase(i);
  }

  @Override
  public void caseArrayReadInst(ArrayReadInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfNullInst(IfNullInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfNonNullInst(IfNonNullInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfEqInst(IfEqInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfNeInst(IfNeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfGtInst(IfGtInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfGeInst(IfGeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfLtInst(IfLtInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfLeInst(IfLeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfCmpEqInst(IfCmpEqInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfCmpNeInst(IfCmpNeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfCmpGtInst(IfCmpGtInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfCmpGeInst(IfCmpGeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfCmpLtInst(IfCmpLtInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIfCmpLeInst(IfCmpLeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseStaticGetInst(StaticGetInst i) {
    defaultCase(i);
  }

  @Override
  public void caseStaticPutInst(StaticPutInst i) {
    defaultCase(i);
  }

  @Override
  public void caseFieldGetInst(FieldGetInst i) {
    defaultCase(i);
  }

  @Override
  public void caseFieldPutInst(FieldPutInst i) {
    defaultCase(i);
  }

  @Override
  public void caseInstanceCastInst(InstanceCastInst i) {
    defaultCase(i);
  }

  @Override
  public void caseInstanceOfInst(InstanceOfInst i) {
    defaultCase(i);
  }

  @Override
  public void casePrimitiveCastInst(PrimitiveCastInst i) {
    defaultCase(i);
  }

  @Override
  public void caseDynamicInvokeInst(DynamicInvokeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseStaticInvokeInst(StaticInvokeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseVirtualInvokeInst(VirtualInvokeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseInterfaceInvokeInst(InterfaceInvokeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseSpecialInvokeInst(SpecialInvokeInst i) {
    defaultCase(i);
  }

  @Override
  public void caseThrowInst(ThrowInst i) {
    defaultCase(i);
  }

  @Override
  public void caseAddInst(AddInst i) {
    defaultCase(i);
  }

  @Override
  public void caseAndInst(AndInst i) {
    defaultCase(i);
  }

  @Override
  public void caseOrInst(OrInst i) {
    defaultCase(i);
  }

  @Override
  public void caseXorInst(XorInst i) {
    defaultCase(i);
  }

  @Override
  public void caseArrayLengthInst(ArrayLengthInst i) {
    defaultCase(i);
  }

  @Override
  public void caseCmpInst(CmpInst i) {
    defaultCase(i);
  }

  @Override
  public void caseCmpgInst(CmpgInst i) {
    defaultCase(i);
  }

  @Override
  public void caseCmplInst(CmplInst i) {
    defaultCase(i);
  }

  @Override
  public void caseDivInst(DivInst i) {
    defaultCase(i);
  }

  @Override
  public void caseIncInst(IncInst i) {
    defaultCase(i);
  }

  @Override
  public void caseMulInst(MulInst i) {
    defaultCase(i);
  }

  @Override
  public void caseRemInst(RemInst i) {
    defaultCase(i);
  }

  @Override
  public void caseSubInst(SubInst i) {
    defaultCase(i);
  }

  @Override
  public void caseShlInst(ShlInst i) {
    defaultCase(i);
  }

  @Override
  public void caseShrInst(ShrInst i) {
    defaultCase(i);
  }

  @Override
  public void caseUshrInst(UshrInst i) {
    defaultCase(i);
  }

  @Override
  public void caseNewInst(NewInst i) {
    defaultCase(i);
  }

  @Override
  public void caseNegInst(NegInst i) {
    defaultCase(i);
  }

  @Override
  public void caseSwapInst(SwapInst i) {
    defaultCase(i);
  }

  @Override
  public void caseDup1Inst(Dup1Inst i) {
    defaultCase(i);
  }

  @Override
  public void caseDup2Inst(Dup2Inst i) {
    defaultCase(i);
  }

  @Override
  public void caseDup1_x1Inst(Dup1_x1Inst i) {
    defaultCase(i);
  }

  @Override
  public void caseDup1_x2Inst(Dup1_x2Inst i) {
    defaultCase(i);
  }

  @Override
  public void caseDup2_x1Inst(Dup2_x1Inst i) {
    defaultCase(i);
  }

  @Override
  public void caseDup2_x2Inst(Dup2_x2Inst i) {
    defaultCase(i);
  }

  @Override
  public void caseNewArrayInst(NewArrayInst i) {
    defaultCase(i);
  }

  @Override
  public void caseNewMultiArrayInst(NewMultiArrayInst i) {
    defaultCase(i);
  }

  @Override
  public void caseLookupSwitchInst(LookupSwitchInst i) {
    defaultCase(i);
  }

  @Override
  public void caseTableSwitchInst(TableSwitchInst i) {
    defaultCase(i);
  }

  @Override
  public void caseEnterMonitorInst(EnterMonitorInst i) {
    defaultCase(i);
  }

  @Override
  public void caseExitMonitorInst(ExitMonitorInst i) {
    defaultCase(i);
  }
}
