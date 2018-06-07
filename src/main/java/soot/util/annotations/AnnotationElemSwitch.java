package soot.util.annotations;

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

import org.jboss.util.Classes;

import soot.tagkit.AbstractAnnotationElemTypeSwitch;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationBooleanElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElem;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;

/**
 *
 * An {@link AbstractAnnotationElemTypeSwitch} that converts an {@link AnnotationElem} to a mapping of element name and the
 * actual result.
 *
 * @author Florian Kuebler
 *
 */
public class AnnotationElemSwitch extends AbstractAnnotationElemTypeSwitch {

  /**
   *
   * A helper class to map method name and result.
   *
   * @author Florian Kuebler
   *
   * @param <V>
   *          the result type.
   */
  public class AnnotationElemResult<V> {

    private String name;
    private V value;

    public AnnotationElemResult(String name, V value) {
      this.name = name;
      this.value = value;
    }

    public String getKey() {
      return name;
    }

    public V getValue() {
      return value;
    }
  }

  @Override
  public void caseAnnotationAnnotationElem(AnnotationAnnotationElem v) {
    AnnotationInstanceCreator aic = new AnnotationInstanceCreator();

    Object result = aic.create(v.getValue());

    setResult(new AnnotationElemResult<Object>(v.getName(), result));
  }

  @Override
  public void caseAnnotationArrayElem(AnnotationArrayElem v) {

    /*
     * for arrays, apply a new AnnotationElemSwitch to every array element and collect the results. Note that the component
     * type of the result is unknown here, s.t. object has to be used.
     */
    Object[] result = new Object[v.getNumValues()];

    int i = 0;
    for (AnnotationElem elem : v.getValues()) {
      AnnotationElemSwitch sw = new AnnotationElemSwitch();
      elem.apply(sw);
      result[i] = ((AnnotationElemResult<?>) sw.getResult()).getValue();

      i++;
    }

    setResult(new AnnotationElemResult<Object[]>(v.getName(), result));

  }

  @Override
  public void caseAnnotationBooleanElem(AnnotationBooleanElem v) {
    setResult(new AnnotationElemResult<Boolean>(v.getName(), v.getValue()));

  }

  @Override
  public void caseAnnotationClassElem(AnnotationClassElem v) {
    try {
      Class<?> clazz = Classes.loadClass(v.getDesc().replace('/', '.'));
      setResult(new AnnotationElemResult<Class<?>>(v.getName(), clazz));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Could not load class: " + v.getDesc());
    }

  }

  @Override
  public void caseAnnotationDoubleElem(AnnotationDoubleElem v) {
    setResult(new AnnotationElemResult<Double>(v.getName(), v.getValue()));
  }

  @Override
  public void caseAnnotationEnumElem(AnnotationEnumElem v) {
    try {
      Class<?> clazz = Classes.loadClass(v.getTypeName().replace('/', '.'));

      // find out which enum constant is used.
      Enum<?> result = null;
      for (Object o : clazz.getEnumConstants()) {
        try {
          Enum<?> t = (Enum<?>) o;
          if (t.name().equals(v.getConstantName())) {
            result = t;
            break;
          }
        } catch (ClassCastException e) {
          throw new RuntimeException("Class " + v.getTypeName() + " is no Enum");
        }
      }

      if (result == null) {
        throw new RuntimeException(v.getConstantName() + " is not a EnumConstant of " + v.getTypeName());
      }

      setResult(new AnnotationElemResult<Enum<?>>(v.getName(), result));

    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Could not load class: " + v.getTypeName());
    }

  }

  @Override
  public void caseAnnotationFloatElem(AnnotationFloatElem v) {
    setResult(new AnnotationElemResult<Float>(v.getName(), v.getValue()));
  }

  @Override
  public void caseAnnotationIntElem(AnnotationIntElem v) {
    setResult(new AnnotationElemResult<Integer>(v.getName(), v.getValue()));
  }

  @Override
  public void caseAnnotationLongElem(AnnotationLongElem v) {
    setResult(new AnnotationElemResult<Long>(v.getName(), v.getValue()));
  }

  @Override
  public void caseAnnotationStringElem(AnnotationStringElem v) {
    setResult(new AnnotationElemResult<String>(v.getName(), v.getValue()));
  }

  @Override
  public void defaultCase(Object object) {
    throw new RuntimeException("Unexpected AnnotationElem");
  }

}
