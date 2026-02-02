package soot.dotnet.types;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import soot.ArrayType;
import soot.BooleanConstant;
import soot.BooleanType;
import soot.ByteConstant;
import soot.ByteType;
import soot.CharType;
import soot.DecimalConstant;
import soot.DecimalType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.Type;
import soot.UByteConstant;
import soot.UByteType;
import soot.UIntType;
import soot.ULongType;
import soot.UShortType;
import soot.Value;
import soot.VoidType;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.UIntConstant;
import soot.jimple.ULongConstant;

/**
 * Factory class for generating SootTypes of .NET types
 */
public class DotnetTypeFactory {
  public static Type toSootType(String type) {
    switch (type) {
      case DotNetBasicTypes.SYSTEM_BYTE:
        // unsigned byte
        return UByteType.v();
      case DotNetBasicTypes.SYSTEM_SBYTE:
        // signed byte
        return ByteType.v();
      case DotNetBasicTypes.SYSTEM_CHAR:
        return CharType.v();
      case DotNetBasicTypes.SYSTEM_DOUBLE:
        return DoubleType.v();
      case DotNetBasicTypes.SYSTEM_SINGLE:
        return FloatType.v();
      case DotNetBasicTypes.SYSTEM_INT64:
        return LongType.v();
      case DotNetBasicTypes.SYSTEM_INT16:
        return ShortType.v();
      case DotNetBasicTypes.SYSTEM_BOOLEAN:
        return BooleanType.v();
      case DotNetBasicTypes.SYSTEM_VOID:
        return VoidType.v();
      case DotNetBasicTypes.SYSTEM_UINT32:
        return UIntType.v();
      case DotNetBasicTypes.SYSTEM_DECIMAL:
        return DecimalType.v();
      case DotNetBasicTypes.SYSTEM_UINT64:
        return ULongType.v();
      case DotNetBasicTypes.SYSTEM_UINT16:
        return UShortType.v();
      case DotNetBasicTypes.SYSTEM_INT32:
        return IntType.v();

      case DotNetBasicTypes.SYSTEM_INTPTR:
      case DotNetBasicTypes.SYSTEM_UINTPTR:
      case "nint":
      case "nuint":
        // not supported at the moment
        return IntType.v();
    }
    if (type.startsWith("`") || type.startsWith("``")) {
      return RefType.v(DotNetBasicTypes.SYSTEM_OBJECT);
    }

    return RefType.v(type);
  }

  public static Type toSootType(ProtoAssemblyAllTypes.TypeDefinition dotnetType) {
    if (dotnetType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.ARRAY)
        || dotnetType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.BY_REF_AND_ARRAY)) {
      return ArrayType.v(toSootType(dotnetType.getFullname()), dotnetType.getArrayDimensions());
    }

    return toSootType(dotnetType.getFullname());
  }

  /**
   * Convert RefType System.Int32 to IntType, etc. Could happen that in method body sth such as System.Int32 occur; convert
   * this to inttype, and so on
   *
   * @param type
   * @return
   */
  public static Type toSootType(Type type) {
    if (type instanceof RefType) {
      return toSootType(type.toString());
    }
    return type;
  }

  public static List<Type> toSootTypeList(List<String> types) {
    ArrayList<Type> ret = new ArrayList<>();
    for (String type : types) {
      ret.add(toSootType(type));
    }
    return ret;
  }

  /**
   * Return a initial value for the given type of the variable
   *
   * @param variable
   * @return
   */
  public static Value initType(Local variable) {
    final Type t = variable.getType();
    return initType(t);
  }

  public static Value initType(Type t) {
    if (t instanceof IntType) {
      return IntConstant.v(0);
    }
    if (t instanceof FloatType) {
      return FloatConstant.v(0);
    }
    if (t instanceof DecimalType) {
      return DecimalConstant.v(BigDecimal.ZERO);
    }
    if (t instanceof DoubleType) {
      return DoubleConstant.v(0);
    }
    if (t instanceof LongType) {
      return LongConstant.v(0);
    }
    if (t instanceof ULongType) {
      return ULongConstant.v(0);
    }
    if (t instanceof ByteType) {
      return ByteConstant.v(0);
    }
    if (t instanceof UByteType) {
      return UByteConstant.v(0);
    }
    if (t instanceof BooleanType) {
      return BooleanConstant.v(0);
    }
    if (t instanceof UIntType) {
      return UIntConstant.v(0);
    }
    if ((t instanceof ShortType) || (t instanceof CharType) || (t instanceof UShortType)) {
      return IntConstant.v(0);
    }
    return NullConstant.v();
  }

  public static List<String> listOfCilPrimitives() {
    ArrayList<String> lst = new ArrayList<>();
    lst.add(DotNetBasicTypes.SYSTEM_INTPTR);
    lst.add(DotNetBasicTypes.SYSTEM_UINTPTR);
    lst.add("nint");
    lst.add("nuint");
    lst.add(DotNetBasicTypes.SYSTEM_UINT32);
    lst.add(DotNetBasicTypes.SYSTEM_SBYTE);
    lst.add(DotNetBasicTypes.SYSTEM_DECIMAL);
    lst.add(DotNetBasicTypes.SYSTEM_UINT64);
    lst.add(DotNetBasicTypes.SYSTEM_UINT16);
    return lst;
  }
}
