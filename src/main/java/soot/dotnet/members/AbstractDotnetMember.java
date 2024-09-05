package soot.dotnet.members;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

import soot.SootClass;
import soot.Value;
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;

/**
 * Represents a .NET Member of a .NET Type
 */
public abstract class AbstractDotnetMember implements DotnetTypeMember {

  /**
   * If we have specific return or assignment characteristics, rewrite it (mostly reftypes) Due to the different eco system
   * of .NET and unsafe methods
   *
   * @param declaringClass
   * @param fieldMethodName
   * @return
   */
  public static Value checkRewriteCilSpecificMember(SootClass declaringClass, String fieldMethodName) {
    /*
     * Normally System.String.Empty == Reftype(System.String), because is string, lead to errors in validation With this
     * fix: System.String.Empty == StringConstant
     */
    if (declaringClass.getName().equals(DotNetBasicTypes.SYSTEM_STRING) && fieldMethodName.equals("Empty")) {
      return StringConstant.v("");
    }
    /*
     * If System.Array.Empty, normal RefType(System.Array) Problem with System.Type[] = System.Array.Empty With this fix
     * null constant
     */
    if (declaringClass.getName().equals(DotNetBasicTypes.SYSTEM_ARRAY) && fieldMethodName.equals("Empty")) {
      // return Jimple.v().newNewExpr(RefType.v(DotnetBasicTypes.SYSTEM_ARRAY));
      return NullConstant.v();
    }

    return null;
  }
}
