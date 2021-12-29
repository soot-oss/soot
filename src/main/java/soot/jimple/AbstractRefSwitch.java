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

public abstract class AbstractRefSwitch<T> implements RefSwitch {

  T result;

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
