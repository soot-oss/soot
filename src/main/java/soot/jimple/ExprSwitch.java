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

public interface ExprSwitch extends soot.util.Switch {
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

  public abstract void caseDynamicInvokeExpr(DynamicInvokeExpr v);

  public abstract void caseCastExpr(CastExpr v);

  public abstract void caseInstanceOfExpr(InstanceOfExpr v);

  public abstract void caseNewArrayExpr(NewArrayExpr v);

  public abstract void caseNewMultiArrayExpr(NewMultiArrayExpr v);

  public abstract void caseNewExpr(NewExpr v);

  public abstract void caseLengthExpr(LengthExpr v);

  public abstract void caseNegExpr(NegExpr v);

  public abstract void defaultCase(Object obj);
}
