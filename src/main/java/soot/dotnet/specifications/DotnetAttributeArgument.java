package soot.dotnet.specifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.*;
import soot.tagkit.*;

import java.util.ArrayList;

/**
 * Converter for .NET attributes and Jimple annotations
 */
public class DotnetAttributeArgument {
    private static final Logger logger = LoggerFactory.getLogger(DotnetType.class);

    public static AnnotationElem toAnnotationElem(ProtoAssemblyAllTypes.AttributeArgumentDefinition arg) {
        if (arg.getType().getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.ENUM)) {
            // return new AnnotationIntElem(arg.getValueInt32(0), arg.getName());
            // enums are ints in dotnet
            return new AnnotationEnumElem(arg.getType().getFullname(), arg.getValueString(0), arg.getName());
        }
        if (arg.getType().getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.ARRAY)) {
            ArrayList<AnnotationElem> arrElements = new ArrayList<>();
            switch (arg.getType().getFullname()) {
                case DotnetBasicTypes.SYSTEM_STRING:
                    for (String v : arg.getValueStringList())
                        arrElements.add(new AnnotationStringElem(v, arg.getName()));
                    break;
                case DotnetBasicTypes.SYSTEM_INT32:
                case DotnetBasicTypes.SYSTEM_UINT32:
                    for (int v : arg.getValueInt32List())
                        arrElements.add(new AnnotationIntElem(v, arg.getName()));
                    break;
                case DotnetBasicTypes.SYSTEM_SINGLE:
                    for (float v : arg.getValueFloatList())
                        arrElements.add(new AnnotationFloatElem(v, arg.getName()));
                    break;
                case DotnetBasicTypes.SYSTEM_DOUBLE:
                case DotnetBasicTypes.SYSTEM_DECIMAL:
                    for (double v : arg.getValueDoubleList())
                        arrElements.add(new AnnotationDoubleElem(v, arg.getName()));
                    break;
                case DotnetBasicTypes.SYSTEM_BOOLEAN:
                    for (int v : arg.getValueInt32List())
                        arrElements.add(new AnnotationBooleanElem(v == 1, arg.getName()));
                    break;
                case DotnetBasicTypes.SYSTEM_INT64:
                case DotnetBasicTypes.SYSTEM_UINT64:
                    for (long v : arg.getValueInt64List())
                        arrElements.add(new AnnotationLongElem(v, arg.getName()));
                    break;
                case DotnetBasicTypes.SYSTEM_BYTE:
                case DotnetBasicTypes.SYSTEM_SBYTE:
                    for (int v : arg.getValueInt32List())
                        arrElements.add(new AnnotationIntElem((Byte)(Integer.valueOf(v).byteValue()), arg.getName()));
                    break;
                case DotnetBasicTypes.SYSTEM_CHAR:
                    for (int v : arg.getValueInt32List())
                        arrElements.add(new AnnotationIntElem(Character.valueOf((char)v), arg.getName()));
                    break;
                case DotnetBasicTypes.SYSTEM_INT16: // Short
                case DotnetBasicTypes.SYSTEM_UINT16:
                    for (int v : arg.getValueInt32List())
                        arrElements.add(new AnnotationIntElem(Short.valueOf((short)v), arg.getName()));
                    break;
                // UInt32 - uint
                // UInt16 - ushort
                // Uint64 - ulong
                // SByte
                default:
                    logger.warn("No implemented type for array annotation element, type: " + arg.getType().getFullname());
            }
            return new AnnotationArrayElem(arrElements, arg.getName());
        }
        else
            switch (arg.getType().getFullname()) {
                case DotnetBasicTypes.SYSTEM_STRING:
                    return new AnnotationStringElem(arg.getValueString(0), arg.getName());
                case DotnetBasicTypes.SYSTEM_INT32:
                    return new AnnotationIntElem(arg.getValueInt32(0), arg.getName());
                case DotnetBasicTypes.SYSTEM_SINGLE:
                    return new AnnotationFloatElem(arg.getValueFloat(0), arg.getName());
                case DotnetBasicTypes.SYSTEM_DOUBLE:
                    return new AnnotationDoubleElem(arg.getValueDouble(0), arg.getName());
                case DotnetBasicTypes.SYSTEM_BOOLEAN:
                    return new AnnotationBooleanElem(arg.getValueInt32(0) == 1, arg.getName());
                case DotnetBasicTypes.SYSTEM_INT64:
                    return new AnnotationLongElem(arg.getValueInt64(0), arg.getName());
                case DotnetBasicTypes.SYSTEM_TYPE: //typeof()
                    return new AnnotationClassElem(arg.getType().getFullname(), arg.getName());
                case DotnetBasicTypes.SYSTEM_BYTE:
                    return new AnnotationIntElem((Byte)(Integer.valueOf(arg.getValueInt32(0)).byteValue()), arg.getName());
                case DotnetBasicTypes.SYSTEM_CHAR:
                    return new AnnotationIntElem(Character.valueOf((char)arg.getValueInt32(0)), arg.getName());
                case DotnetBasicTypes.SYSTEM_INT16: // Short
                    return new AnnotationIntElem(Short.valueOf((short)arg.getValueInt32(0)), arg.getName());
                // UInt32 - uint
                // UInt16 - ushort
                // Uint64 - ulong
                // SByte
                default:
                    return new AnnotationClassElem(arg.getType().getFullname(), arg.getName());
            }
    }
}
