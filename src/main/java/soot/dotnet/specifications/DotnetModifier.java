package soot.dotnet.specifications;

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

import soot.Modifier;
import soot.dotnet.proto.ProtoAssemblyAllTypes;

/**
 * Converter for the modifier of a type or method
 */
public class DotnetModifier {

  public static int toSootModifier(ProtoAssemblyAllTypes.TypeDefinition protoType) {

    int modifier = convertAccessibility(protoType.getAccessibility());

    if (protoType.getIsAbstract()) {
      modifier |= Modifier.ABSTRACT;
    }
    if (protoType.getIsStatic()) {
      modifier |= Modifier.STATIC;
    }
    if (protoType.getIsSealed()) {
      modifier |= Modifier.FINAL;
    }
    if (protoType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.INTERFACE)) {
      modifier |= Modifier.INTERFACE;
    }
    if (protoType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.ENUM)) {
      modifier |= Modifier.ENUM;
    }

    return modifier;
  }

  public static int toSootModifier(ProtoAssemblyAllTypes.MethodDefinition methodDefinition) {

    int modifier = convertAccessibility(methodDefinition.getAccessibility());

    if (methodDefinition.getIsAbstract()) {
      modifier |= Modifier.ABSTRACT;
    }
    if (methodDefinition.getIsStatic()) {
      modifier |= Modifier.STATIC;
    }
    // cannot do this due to hiding property of c#
    // if (!methodDefinition.getIsVirtual())
    // modifier |= Modifier.FINAL;
    if (methodDefinition.getIsSealed()) {
      modifier |= Modifier.FINAL;
    }
    if (methodDefinition.getIsExtern()) {
      modifier |= Modifier.NATIVE;
    }

    return modifier;
  }

  public static int toSootModifier(ProtoAssemblyAllTypes.FieldDefinition fieldDefinition) {

    int modifier = convertAccessibility(fieldDefinition.getAccessibility());

    if (fieldDefinition.getIsAbstract() || fieldDefinition.getIsVirtual()) {
      modifier |= Modifier.ABSTRACT;
    }
    if (fieldDefinition.getIsStatic()) {
      modifier |= Modifier.STATIC;
    }
    if (fieldDefinition.getIsReadOnly()) {
      modifier |= Modifier.FINAL;
    }

    return modifier;
  }

  private static int convertAccessibility(ProtoAssemblyAllTypes.Accessibility accessibility) {
    int modifier = 0;

    switch (accessibility) {
      case PRIVATE:
        modifier |= Modifier.PRIVATE;
        break;
      case INTERNAL:
      case PROTECTED_AND_INTERNAL:
      case PROTECTED_OR_INTERNAL:
      case PUBLIC:
        modifier |= Modifier.PUBLIC;
        break;
      case PROTECTED:
        modifier |= Modifier.PROTECTED;
        break;
    }
    return modifier;
  }
}
