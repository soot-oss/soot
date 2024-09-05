package soot.dotnet.specifications;

import java.util.ArrayList;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotNetBasicTypes;
import soot.dotnet.types.DotnetType;
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
 * Converter for .NET attributes and Jimple annotations
 */
public class DotnetAttributeArgument {
  private static final Logger logger = LoggerFactory.getLogger(DotnetType.class);

  public static AnnotationElem toAnnotationElem(ProtoAssemblyAllTypes.AttributeArgumentDefinition arg) {
    if (arg.getType().getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.ENUM)) {
      // return new AnnotationIntElem(arg.getValueInt32(0), arg.getName());
      // enums are ints in dotnet
      String name = null;
      if (arg.getValueStringCount() > 0) {
        name = arg.getValueString(0);
      }
      return new AnnotationEnumElem(arg.getType().getFullname(), name, arg.getName());
    }
    if (arg.getType().getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.ARRAY)) {
      ArrayList<AnnotationElem> arrElements = new ArrayList<>();
      switch (arg.getType().getFullname()) {
        case DotNetBasicTypes.SYSTEM_STRING:
          for (String v : arg.getValueStringList()) {
            arrElements.add(new AnnotationStringElem(v, arg.getName()));
          }
          break;
        case DotNetBasicTypes.SYSTEM_INT32:
        case DotNetBasicTypes.SYSTEM_UINT32:
          for (int v : arg.getValueInt32List()) {
            arrElements.add(new AnnotationIntElem(v, arg.getName()));
          }
          break;
        case DotNetBasicTypes.SYSTEM_SINGLE:
          for (float v : arg.getValueFloatList()) {
            arrElements.add(new AnnotationFloatElem(v, arg.getName()));
          }
          break;
        case DotNetBasicTypes.SYSTEM_DOUBLE:
        case DotNetBasicTypes.SYSTEM_DECIMAL:
          for (double v : arg.getValueDoubleList()) {
            arrElements.add(new AnnotationDoubleElem(v, arg.getName()));
          }
          break;
        case DotNetBasicTypes.SYSTEM_BOOLEAN:
          for (int v : arg.getValueInt32List()) {
            arrElements.add(new AnnotationBooleanElem(v == 1, arg.getName()));
          }
          break;
        case DotNetBasicTypes.SYSTEM_INT64:
        case DotNetBasicTypes.SYSTEM_UINT64:
          for (long v : arg.getValueInt64List()) {
            arrElements.add(new AnnotationLongElem(v, arg.getName()));
          }
          break;
        case DotNetBasicTypes.SYSTEM_BYTE:
        case DotNetBasicTypes.SYSTEM_SBYTE:
          for (int v : arg.getValueInt32List()) {
            arrElements.add(new AnnotationIntElem((Byte) (Integer.valueOf(v).byteValue()), arg.getName()));
          }
          break;
        case DotNetBasicTypes.SYSTEM_CHAR:
          for (int v : arg.getValueInt32List()) {
            arrElements.add(new AnnotationIntElem(Character.valueOf((char) v), arg.getName()));
          }
          break;
        case DotNetBasicTypes.SYSTEM_INT16: // Short
        case DotNetBasicTypes.SYSTEM_UINT16:
          for (int v : arg.getValueInt32List()) {
            arrElements.add(new AnnotationIntElem(Short.valueOf((short) v), arg.getName()));
          }
          break;
        // UInt32 - uint
        // UInt16 - ushort
        // Uint64 - ulong
        // SByte
        default:
          logger.warn("No implemented type for array annotation element, type: " + arg.getType().getFullname());
      }
      return new AnnotationArrayElem(arrElements, arg.getName());
    } else {
      switch (arg.getType().getFullname()) {
        case DotNetBasicTypes.SYSTEM_STRING:
          return new AnnotationStringElem(arg.getValueString(0), arg.getName());
        case DotNetBasicTypes.SYSTEM_INT32:
          return new AnnotationIntElem(arg.getValueInt32(0), arg.getName());
        case DotNetBasicTypes.SYSTEM_SINGLE:
          return new AnnotationFloatElem(arg.getValueFloat(0), arg.getName());
        case DotNetBasicTypes.SYSTEM_DOUBLE:
          return new AnnotationDoubleElem(arg.getValueDouble(0), arg.getName());
        case DotNetBasicTypes.SYSTEM_BOOLEAN:
          return new AnnotationBooleanElem(arg.getValueInt32(0) == 1, arg.getName());
        case DotNetBasicTypes.SYSTEM_INT64:
          return new AnnotationLongElem(arg.getValueInt64(0), arg.getName());
        case DotNetBasicTypes.SYSTEM_TYPE: // typeof()
          return new AnnotationClassElem(arg.getType().getFullname(), arg.getName());
        case DotNetBasicTypes.SYSTEM_BYTE:
          return new AnnotationIntElem((Byte) (Integer.valueOf(arg.getValueInt32(0)).byteValue()), arg.getName());
        case DotNetBasicTypes.SYSTEM_CHAR:
          return new AnnotationIntElem(Character.valueOf((char) arg.getValueInt32(0)), arg.getName());
        case DotNetBasicTypes.SYSTEM_INT16: // Short
          return new AnnotationIntElem(Short.valueOf((short) arg.getValueInt32(0)), arg.getName());
        // UInt32 - uint
        // UInt16 - ushort
        // Uint64 - ulong
        // SByte
        default:
          return new AnnotationClassElem(arg.getType().getFullname(), arg.getName());
      }
    }
  }
}
