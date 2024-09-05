package soot.jimple;

import soot.DecimalConstant;
import soot.ShortConstant;
import soot.UByteConstant;

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

public interface ConstantSwitch extends soot.util.Switch {
  public abstract void caseDoubleConstant(DoubleConstant v);

  public abstract void caseFloatConstant(FloatConstant v);

  public abstract void caseIntConstant(IntConstant v);

  public abstract void caseLongConstant(LongConstant v);

  public abstract void caseNullConstant(NullConstant v);

  public abstract void caseStringConstant(StringConstant v);

  public abstract void caseClassConstant(ClassConstant v);

  public abstract void caseMethodHandle(MethodHandle handle);

  public abstract void caseMethodType(MethodType type);

  public abstract void defaultCase(Object object);

  public default void caseDecimalConstant(DecimalConstant v) {
    throw new RuntimeException("Unsupported");
  }

  public default void caseUIntConstant(UIntConstant v) {
    throw new RuntimeException("Unsupported");
  }

  public default void caseShortConstant(ShortConstant v) {
    caseIntConstant(v);
  }

  public default void caseUByteConstant(UByteConstant v) {
    throw new RuntimeException("Unsupported");
  }

  public default void caseULongConstant(ULongConstant v) {
    throw new RuntimeException("Unsupported");
  }

  public default void caseUShortConstant(UShortConstant v) {
    throw new RuntimeException("Unsupported");
  }
}