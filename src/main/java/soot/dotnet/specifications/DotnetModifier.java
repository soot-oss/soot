package soot.dotnet.specifications;

import soot.Modifier;
import soot.dotnet.proto.ProtoAssemblyAllTypes;

/**
 * Converter for the modifier of a type or method
 */
public class DotnetModifier {

    public static int toSootModifier(ProtoAssemblyAllTypes.TypeDefinition protoType) {

        int modifier = convertAccessibility(protoType.getAccessibility());

        if (protoType.getIsAbstract())
            modifier |= Modifier.ABSTRACT;
        if (protoType.getIsStatic())
            modifier |= Modifier.STATIC;
        if (protoType.getIsSealed())
            modifier |= Modifier.FINAL;
        if (protoType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.INTERFACE))
            modifier |= Modifier.INTERFACE;
        if (protoType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.ENUM))
            modifier |= Modifier.ENUM;

        return modifier;
    }

    public static int toSootModifier(ProtoAssemblyAllTypes.MethodDefinition methodDefinition) {

        int modifier = convertAccessibility(methodDefinition.getAccessibility());

        if (methodDefinition.getIsAbstract())
            modifier |= Modifier.ABSTRACT;
        if (methodDefinition.getIsStatic())
            modifier |= Modifier.STATIC;
        // cannot do this due to hiding property of c#
        // if (!methodDefinition.getIsVirtual())
        //     modifier |= Modifier.FINAL;
        if (methodDefinition.getIsSealed())
            modifier |= Modifier.FINAL;
        if (methodDefinition.getIsExtern())
            modifier |= Modifier.NATIVE;

        return modifier;
    }

    public static int toSootModifier(ProtoAssemblyAllTypes.FieldDefinition fieldDefinition) {

        int modifier = convertAccessibility(fieldDefinition.getAccessibility());

        if (fieldDefinition.getIsAbstract() || fieldDefinition.getIsVirtual())
            modifier |= Modifier.ABSTRACT;
        if (fieldDefinition.getIsStatic())
            modifier |= Modifier.STATIC;
        if (fieldDefinition.getIsReadOnly())
            modifier |= Modifier.FINAL;

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
