/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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






package soot.grimp;

import soot.jimple.*;

public interface GrimpExprSwitch extends ExprSwitch
{
    public abstract void caseAddExpr(AddExpr v);
    public abstract void caseAndExpr(AndExpr v);
    public abstract void caseCmpExpr(CmpExpr v);
    public abstract void caseCmpgExpr(CmpgExpr v);
    public abstract void caseCmplExpr(CmplExpr v);
    public abstract void caseDivExpr(DivExpr v);
    public abstract void caseEqExpr(EqExpr v);
    public abstract void caseNeExpr(NeExpr v);
    public abstract void caseGeExpr(GeExpr v);
    public abstract void caseGtExpr(GtExpr v);
    public abstract void caseLeExpr(LeExpr v);
    public abstract void caseLtExpr(LtExpr v);
    public abstract void caseMulExpr(MulExpr v);
    public abstract void caseOrExpr(OrExpr v);
    public abstract void caseRemExpr(RemExpr v);
    public abstract void caseShlExpr(ShlExpr v);
    public abstract void caseShrExpr(ShrExpr v);
    public abstract void caseUshrExpr(UshrExpr v);
    public abstract void caseSubExpr(SubExpr v);
    public abstract void caseXorExpr(XorExpr v);
    public abstract void caseInterfaceInvokeExpr(InterfaceInvokeExpr v);
    public abstract void caseSpecialInvokeExpr(SpecialInvokeExpr v);
    public abstract void caseStaticInvokeExpr(StaticInvokeExpr v);
    public abstract void caseVirtualInvokeExpr(VirtualInvokeExpr v);
    public abstract void caseNewInvokeExpr(NewInvokeExpr v);
    public abstract void caseCastExpr(CastExpr v);
    public abstract void caseInstanceOfExpr(InstanceOfExpr v);
    public abstract void caseNewArrayExpr(NewArrayExpr v);
    public abstract void caseNewMultiArrayExpr(NewMultiArrayExpr v);
    public abstract void caseNewExpr(NewExpr v);
    public abstract void caseLengthExpr(LengthExpr v);
    public abstract void caseNegExpr(NegExpr v);
    public abstract void defaultCase(Object obj);
}
