package soot.dotnet.members.method;

import soot.Type;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetTypeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for .NET Method Parameters
 */
public class DotnetMethodParameter {

    /**
     * Converts list of proto .NET method parameter definitions to a list of SootTypes
     * @param parameterList
     * @return
     */
    public static List<Type> toSootTypeParamsList(List<ProtoAssemblyAllTypes.ParameterDefinition> parameterList) {
        List<Type> types = new ArrayList<>();
        for (ProtoAssemblyAllTypes.ParameterDefinition parameter : parameterList) {
            Type type = DotnetTypeFactory.toSootType(parameter.getType());
            types.add(type);
        }
        return types;
    }
}
