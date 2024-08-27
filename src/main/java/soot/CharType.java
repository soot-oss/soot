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
import soot.jimple.internal.IIntLikeType;
import soot.options.Options;
import soot.util.Switch;

/**
 * Soot representation of the Java built-in type 'char'. Implemented as a singleton.
 */
@SuppressWarnings("serial")
public class CharType extends PrimType implements IntegerType, IJavaType, DotNetINumber, IIntLikeType {

  public static final int HASHCODE = 0x739EA474;

  public CharType(Singletons.Global g) {
  }

  public static CharType v() {
    return G.v().soot_CharType();
  }

  @Override
  public boolean equals(Object t) {
    return this == t;
  }

  @Override
  public String toString() {
    return "char";
  }

  @Override
  public int hashCode() {
    return HASHCODE;
  }

  @Override
  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseCharType(this);
  }

  @Override
  public String getTypeAsString() {
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      return DotNetBasicTypes.SYSTEM_CHAR;
    }
    return JavaBasicTypes.JAVA_LANG_CHARACTER;
  }

  @Override
  public Class<?> getJavaBoxedType() {
    return Character.class;
  }

  @Override
  public Class<?> getJavaPrimitiveType() {
    return char.class;
  }
}
