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
 * Soot representation of the .NET built-in type 'long'. Implemented as a singleton.
 */
@SuppressWarnings("serial")
public class ULongType extends PrimType implements DotNetINumber {

  public static final int HASHCODE = 0x013DA077;

  public ULongType(Singletons.Global g) {
  }

  public static ULongType v() {
    return G.v().soot_ULongType();
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
    return "ulong";
  }

  @Override
  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseULongType(this);
  }

  @Override
  public String getTypeAsString() {
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      return DotNetBasicTypes.SYSTEM_UINT64;
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
