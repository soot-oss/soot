/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Baf, a Java(TM) bytecode analyzer framework.                      *
 * Copyright (C) 1997-1999 Raja Vallee-Rai                           *
 * (rvalleerai@sable.mcgill.ca) All rights reserved.                 *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on July 5, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Changed caseDefault to defaultCase, to avoid name conflicts (and conform
   to the standard).

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.*;

public interface InstSwitch extends Switch
{
    public void caseReturnVoidInst(ReturnVoidInst i);
    public void caseReturnInst(ReturnInst i);
    public void caseNopInst(NopInst i);
    public void caseGotoInst(GotoInst i);
    public void casePushInst(PushInst i);
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
    public void caseDupInst(DupInst i);
    public void caseNewArrayInst(NewArrayInst i);
    public void caseNewMultiArrayInst(NewMultiArrayInst i);
    public void caseLookupSwitchInst(LookupSwitchInst i);
    public void caseTableSwitchInst(TableSwitchInst i);
    public void caseEnterMonitorInst(EnterMonitorInst i);
    public void caseExitMonitorInst(ExitMonitorInst i);
}
