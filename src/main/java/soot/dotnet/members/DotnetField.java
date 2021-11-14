package soot.dotnet.members;

import soot.SootField;
import soot.Type;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetTypeFactory;

import static soot.dotnet.specifications.DotnetModifier.toSootModifier;

/**
 * Represents a .NET Field
 */
public class DotnetField extends AbstractDotnetMember {

    private final ProtoAssemblyAllTypes.FieldDefinition protoField;

    public DotnetField(ProtoAssemblyAllTypes.FieldDefinition protoField) {
        this.protoField = protoField;
    }

    public SootField makeSootField() {
        int modifier = toSootModifier(protoField);
        Type type = DotnetTypeFactory.toSootType(protoField.getType());
        String name = protoField.getName();
        return new SootField(name, type, modifier);
    }
}
