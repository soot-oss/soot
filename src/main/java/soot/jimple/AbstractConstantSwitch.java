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

public abstract class AbstractConstantSwitch implements ConstantSwitch {
  Object result;

  public void caseDoubleConstant(DoubleConstant v) {
    defaultCase(v);
  }

  public void caseFloatConstant(FloatConstant v) {
    defaultCase(v);
  }

  public void caseIntConstant(IntConstant v) {
    defaultCase(v);
  }

  public void caseLongConstant(LongConstant v) {
    defaultCase(v);
  }

  public void caseNullConstant(NullConstant v) {
    defaultCase(v);
  }

  public void caseStringConstant(StringConstant v) {
    defaultCase(v);
  }

  public void caseClassConstant(ClassConstant v) {
    defaultCase(v);
  }

  public void caseMethodHandle(MethodHandle v) {
    defaultCase(v);
  }

  public void defaultCase(Object v) {
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }
}
