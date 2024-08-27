package soot;

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

import soot.dotnet.types.DotNetBasicTypes;
import soot.dotnet.types.DotNetINumber;
import soot.options.Options;
import soot.util.Switch;

/**
 * Soot representation of the .NET built-in type 'decimal'. Implemented as a singleton.
 */
@SuppressWarnings("serial")
public class DecimalType extends PrimType implements DotNetINumber {

  public static final int HASHCODE = 0x2B9D7242;

  public DecimalType(Singletons.Global g) {
  }

  public static DecimalType v() {
    return G.v().soot_DecimalType();
  }

  @Override
  public boolean equals(Object t) {
    return this == t;
  }

  @Override
  public int hashCode() {
    return HASHCODE;
  }

  @Override
  public String toString() {
    return "decimal";
  }

  @Override
  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseDecimalType(this);
  }

  @Override
  public String getTypeAsString() {
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      return DotNetBasicTypes.SYSTEM_DECIMAL;
    }
    throw new RuntimeException("Unsupported");
  }

  @Override
  public Class<?> getJavaBoxedType() {
    return null;
  }

  @Override
  public Class<?> getJavaPrimitiveType() {
    return null;
  }
}
