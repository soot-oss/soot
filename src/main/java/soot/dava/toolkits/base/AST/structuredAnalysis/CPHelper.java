package soot.dava.toolkits.base.AST.structuredAnalysis;

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

import soot.BooleanType;
import soot.Value;
import soot.dava.internal.javaRep.DIntConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;

public class CPHelper {

  /*
   * The helper class just checks the type of the data being sent and create a clone of it
   *
   * If it is not a data of interest a null is send back
   */
  public static Object wrapperClassCloner(Object value) {
    if (value instanceof Double) {
      return new Double(((Double) value).doubleValue());
    } else if (value instanceof Float) {
      return new Float(((Float) value).floatValue());
    } else if (value instanceof Long) {
      return new Long(((Long) value).longValue());
    } else if (value instanceof Boolean) {
      return new Boolean(((Boolean) value).booleanValue());
    } else if (value instanceof Integer) {
      return new Integer(((Integer) value).intValue());
    } else {
      return null;
    }
  }

  /*
   * isAConstantValue(Value toCheck) it will check whether toCheck is one of the interesting Constants IntConstant
   * FloatConstant etc etc if yes return the Integer/Long/float/Double
   *
   * Notice for integer the callee has to check whether what is required is a Boolean!!!!
   */
  public static Object isAConstantValue(Value toCheck) {
    Object value = null;

    if (toCheck instanceof LongConstant) {
      value = new Long(((LongConstant) toCheck).value);
    } else if (toCheck instanceof DoubleConstant) {
      value = new Double(((DoubleConstant) toCheck).value);
    } else if (toCheck instanceof FloatConstant) {
      value = new Float(((FloatConstant) toCheck).value);
    } else if (toCheck instanceof IntConstant) {
      int val = ((IntConstant) toCheck).value;
      value = new Integer(val);
    }
    return value;
  }

  public static Value createConstant(Object toConvert) {
    if (toConvert instanceof Long) {
      return LongConstant.v(((Long) toConvert).longValue());
    } else if (toConvert instanceof Double) {
      return DoubleConstant.v(((Double) toConvert).doubleValue());
    } else if (toConvert instanceof Boolean) {
      boolean val = ((Boolean) toConvert).booleanValue();
      if (val) {
        return DIntConstant.v(1, BooleanType.v());
      } else {
        return DIntConstant.v(0, BooleanType.v());
      }
    } else if (toConvert instanceof Float) {
      return FloatConstant.v(((Float) toConvert).floatValue());
    } else if (toConvert instanceof Integer) {
      return IntConstant.v(((Integer) toConvert).intValue());
    } else {
      return null;
    }
  }

}
