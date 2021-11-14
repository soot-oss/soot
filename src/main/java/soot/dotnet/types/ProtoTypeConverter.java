package soot.dotnet.types;

import soot.ArrayType;
import soot.PrimType;
import soot.Type;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoDotnetNativeHost;

import java.util.ArrayList;
import java.util.List;

import static soot.dotnet.types.DotnetTypeFactory.toSootType;

/**
 * Converter for Proto Messages
 */
public class ProtoTypeConverter {
    /**
     * Converts from Soot Type to proto-msg SootType
     * @param type Soot Type
     * @return Proto Soot Type
     */
    public static ProtoDotnetNativeHost.SootTypeMsg toProtoSootTypeMsg(Type type) {
        ProtoDotnetNativeHost.SootTypeMsg.Builder builder = ProtoDotnetNativeHost.SootTypeMsg.newBuilder();
        if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType)type;
            builder.setKind(arrayType.baseType instanceof PrimType ?
                    ProtoDotnetNativeHost.SootTypeMsg.Kind.ARRAY_PRIM :
                    ProtoDotnetNativeHost.SootTypeMsg.Kind.ARRAY_REF);
            builder.setTypeName(arrayType.baseType.toString());
            builder.setNumDimensions(arrayType.numDimensions);
            return builder.build();
        }

        builder.setKind(type instanceof PrimType ?
                ProtoDotnetNativeHost.SootTypeMsg.Kind.PRIMITIVE :
                ProtoDotnetNativeHost.SootTypeMsg.Kind.REF);
        builder.setTypeName(type.toString());
        return builder.build();
    }

}
