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

import soot.Local;

public abstract class AbstractJimpleValueSwitch<T> extends AbstractExprSwitch<T> implements JimpleValueSwitch {

  @Override
  public void caseLocal(Local v) {
    defaultCase(v);
  }

  @Override
  public void caseDoubleConstant(DoubleConstant v) {
    defaultCase(v);
  }

  @Override
  public void caseFloatConstant(FloatConstant v) {
    defaultCase(v);
  }

  @Override
  public void caseIntConstant(IntConstant v) {
    // this if fine for .NET code
  }

  @Override
  public void caseLongConstant(LongConstant v) {
    defaultCase(v);
  }

  @Override
  public void caseNullConstant(NullConstant v) {
    defaultCase(v);
  }

  @Override
  public void caseStringConstant(StringConstant v) {
    defaultCase(v);
  }

  @Override
  public void caseClassConstant(ClassConstant v) {
    defaultCase(v);
  }

  @Override
  public void caseMethodHandle(MethodHandle v) {
    defaultCase(v);
  }

  @Override
  public void caseMethodType(MethodType v) {
    defaultCase(v);
  }

  @Override
  public void caseArrayRef(ArrayRef v) {
    defaultCase(v);
  }

  @Override
  public void caseStaticFieldRef(StaticFieldRef v) {
    defaultCase(v);
  }

  @Override
  public void caseInstanceFieldRef(InstanceFieldRef v) {
    defaultCase(v);
  }

  @Override
  public void caseParameterRef(ParameterRef v) {
    defaultCase(v);
  }

  @Override
  public void caseCaughtExceptionRef(CaughtExceptionRef v) {
    defaultCase(v);
  }

  @Override
  public void caseThisRef(ThisRef v) {
    defaultCase(v);
  }
}
