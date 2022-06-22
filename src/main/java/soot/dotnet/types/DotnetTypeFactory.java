package soot.dotnet.types;

import soot.*;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for generating SootTypes of .NET types
 */
public class DotnetTypeFactory {
    public static Type toSootType(String type) {
        if (type.equals(IntType.v().getTypeAsString())
                || type.equals(DotnetBasicTypes.SYSTEM_INTPTR)
                || type.equals(DotnetBasicTypes.SYSTEM_UINTPTR)
                || type.equals("nint")
                || type.equals("nuint"))
            return IntType.v();
        if (type.equals(ByteType.v().getTypeAsString()))
            return ByteType.v();
        if (type.equals(CharType.v().getTypeAsString()))
            return CharType.v();
        if (type.equals(DoubleType.v().getTypeAsString()))
            return DoubleType.v();
        if (type.equals(FloatType.v().getTypeAsString()))
            return FloatType.v();
        if (type.equals(LongType.v().getTypeAsString()))
            return LongType.v();
        if (type.equals(ShortType.v().getTypeAsString()))
            return ShortType.v();
        if (type.equals(BooleanType.v().getTypeAsString()))
            return BooleanType.v();
        if (type.equals(DotnetBasicTypes.SYSTEM_VOID))
            return VoidType.v();

        // compromise, not available in java
        if (type.equals(DotnetBasicTypes.SYSTEM_UINT32))
            return IntType.v();
        if (type.equals(DotnetBasicTypes.SYSTEM_SBYTE))
            return ByteType.v();
        if (type.equals(DotnetBasicTypes.SYSTEM_DECIMAL))
            return DoubleType.v();
        if (type.equals(DotnetBasicTypes.SYSTEM_UINT64))
            return LongType.v();
        if (type.equals(DotnetBasicTypes.SYSTEM_UINT16))
            return ShortType.v();
//        if (type.equals("nint"))
//            return RefType.v(DotnetBasicTypes.SYSTEM_INTPTR);
//        if (type.equals("nuint"))
//            return RefType.v(DotnetBasicTypes.SYSTEM_UINTPTR);
        if (type.startsWith("`") || type.startsWith("``"))
            return RefType.v(DotnetBasicTypes.SYSTEM_OBJECT);

        return RefType.v(type);
    }

    public static Type toSootType(ProtoAssemblyAllTypes.TypeDefinition dotnetType) {
        if (dotnetType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.ARRAY)
                || dotnetType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.BY_REF_AND_ARRAY))
            return ArrayType.v(toSootType(dotnetType.getFullname()), dotnetType.getArrayDimensions());

        return toSootType(dotnetType.getFullname());
    }

    /**
     * Convert RefType System.Int32 to IntType, etc.
     * Could happen that in method body sth such as System.Int32 occur; convert this to inttype, and so on
     * @param type
     * @return
     */
    public static Type toSootType(Type type) {
        if (type instanceof RefType)
            return toSootType(type.toString());
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
     * @param variable
     * @return
     */
    public static Value initType(Local variable) {
        final Type t = variable.getType();
        return initType(t);
    }

    public static Value initType(Type t) {
        if (t instanceof IntType)
            return IntConstant.v(0);
        if (t instanceof FloatType)
            return FloatConstant.v(0);
        if (t instanceof DoubleType)
            return DoubleConstant.v(0);
        if (t instanceof LongType)
            return LongConstant.v(0);
        if (t instanceof ByteType)
            return IntConstant.v(0);
        if (t instanceof BooleanType)
            return IntConstant.v(0);
        if (t instanceof ShortType)
            return IntConstant.v(0);
        if (t instanceof CharType)
            return IntConstant.v(0);
        return NullConstant.v();
    }

    public static List<String> listOfCilPrimitives() {
        ArrayList<String> lst = new ArrayList<>();
        lst.add(DotnetBasicTypes.SYSTEM_INTPTR);
        lst.add(DotnetBasicTypes.SYSTEM_UINTPTR);
        lst.add("nint");
        lst.add("nuint");
        lst.add(DotnetBasicTypes.SYSTEM_UINT32);
        lst.add(DotnetBasicTypes.SYSTEM_SBYTE);
        lst.add(DotnetBasicTypes.SYSTEM_DECIMAL);
        lst.add(DotnetBasicTypes.SYSTEM_UINT64);
        lst.add(DotnetBasicTypes.SYSTEM_UINT16);
        return lst;
    }
}
