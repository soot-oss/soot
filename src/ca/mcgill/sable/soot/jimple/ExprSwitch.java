/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
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
 The reference version is: $JimpleVersion: 0.5 $

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

 - Modified on October 31, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

public interface ExprSwitch extends ca.mcgill.sable.util.Switch
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
    public abstract void caseCastExpr(CastExpr v);
    public abstract void caseInstanceOfExpr(InstanceOfExpr v);
    public abstract void caseNewArrayExpr(NewArrayExpr v);
    public abstract void caseNewMultiArrayExpr(NewMultiArrayExpr v);
    public abstract void caseNewExpr(NewExpr v);
    public abstract void caseLengthExpr(LengthExpr v);
    public abstract void caseNegExpr(NegExpr v);
    public abstract void defaultCase(Object obj);
}
