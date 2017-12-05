/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot.baf;

import soot.util.Switch;

public interface InstSwitch extends Switch
{
    public void caseReturnVoidInst(ReturnVoidInst i);
    public void caseReturnInst(ReturnInst i);
    public void caseNopInst(NopInst i);
    public void caseGotoInst(GotoInst i);
    public void caseJSRInst(JSRInst i);
    public void casePushInst(PushInst i);
    public void casePopInst(PopInst i);
    public void caseIdentityInst(IdentityInst i);
    public void caseStoreInst(StoreInst i);
    public void caseLoadInst(LoadInst i);
    public void caseArrayWriteInst(ArrayWriteInst i);
    public void caseArrayReadInst(ArrayReadInst i);
    public void caseIfNullInst(IfNullInst i);
    public void caseIfNonNullInst(IfNonNullInst i);
    public void caseIfEqInst(IfEqInst i);
    public void caseIfNeInst(IfNeInst i);
    public void caseIfGtInst(IfGtInst i);
    public void caseIfGeInst(IfGeInst i);
    public void caseIfLtInst(IfLtInst i);
    public void caseIfLeInst(IfLeInst i);
    public void caseIfCmpEqInst(IfCmpEqInst i);
    public void caseIfCmpNeInst(IfCmpNeInst i);
    public void caseIfCmpGtInst(IfCmpGtInst i);
    public void caseIfCmpGeInst(IfCmpGeInst i);
    public void caseIfCmpLtInst(IfCmpLtInst i);
    public void caseIfCmpLeInst(IfCmpLeInst i);
    public void caseStaticGetInst(StaticGetInst i);
    public void caseStaticPutInst(StaticPutInst i);
    public void caseFieldGetInst(FieldGetInst i);
    public void caseFieldPutInst(FieldPutInst i);
    public void caseInstanceCastInst(InstanceCastInst i);
    public void caseInstanceOfInst(InstanceOfInst i);
    public void casePrimitiveCastInst(PrimitiveCastInst i);
	public void caseDynamicInvokeInst(DynamicInvokeInst i);
    public void caseStaticInvokeInst(StaticInvokeInst i);
    public void caseVirtualInvokeInst(VirtualInvokeInst i);
    public void caseInterfaceInvokeInst(InterfaceInvokeInst i);
    public void caseSpecialInvokeInst(SpecialInvokeInst i);
    public void caseThrowInst(ThrowInst i);
    public void caseAddInst(AddInst i);
    public void caseAndInst(AndInst i);
    public void caseOrInst(OrInst i);
    public void caseXorInst(XorInst i);
    public void caseArrayLengthInst(ArrayLengthInst i);
    public void caseCmpInst(CmpInst i);
    public void caseCmpgInst(CmpgInst i);
    public void caseCmplInst(CmplInst i);
    public void caseDivInst(DivInst i);
    public void caseIncInst(IncInst i);
    public void caseMulInst(MulInst i);
    public void caseRemInst(RemInst i);
    public void caseSubInst(SubInst i);
    public void caseShlInst(ShlInst i);
    public void caseShrInst(ShrInst i);
    public void caseUshrInst(UshrInst i);
    public void caseNewInst(NewInst i);
    public void caseNegInst(NegInst i);
    public void caseSwapInst(SwapInst i);
   
    
    public void caseDup1Inst(Dup1Inst i); 
    public void caseDup2Inst(Dup2Inst i);    
    public void caseDup1_x1Inst(Dup1_x1Inst i);    
    public void caseDup1_x2Inst(Dup1_x2Inst i);    
    public void caseDup2_x1Inst(Dup2_x1Inst i);    
    public void caseDup2_x2Inst(Dup2_x2Inst i);    







    public void caseNewArrayInst(NewArrayInst i);
    public void caseNewMultiArrayInst(NewMultiArrayInst i);
    public void caseLookupSwitchInst(LookupSwitchInst i);
    public void caseTableSwitchInst(TableSwitchInst i);
    public void caseEnterMonitorInst(EnterMonitorInst i);
    public void caseExitMonitorInst(ExitMonitorInst i);
}
