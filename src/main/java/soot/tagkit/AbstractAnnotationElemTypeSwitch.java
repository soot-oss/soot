package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

public abstract class AbstractAnnotationElemTypeSwitch implements IAnnotationElemTypeSwitch {

  Object result;

  @Override
  public void caseAnnotationAnnotationElem(AnnotationAnnotationElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationArrayElem(AnnotationArrayElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationBooleanElem(AnnotationBooleanElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationClassElem(AnnotationClassElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationDoubleElem(AnnotationDoubleElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationEnumElem(AnnotationEnumElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationFloatElem(AnnotationFloatElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationIntElem(AnnotationIntElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationLongElem(AnnotationLongElem v) {
    defaultCase(v);

  }

  @Override
  public void caseAnnotationStringElem(AnnotationStringElem v) {
    defaultCase(v);

  }

  @Override
  public void defaultCase(Object object) {

  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }

}
