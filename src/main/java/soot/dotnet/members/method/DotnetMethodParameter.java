package soot.dotnet.members.method;

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

import soot.Type;
import soot.dotnet.members.ByReferenceWrapperGenerator;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetTypeFactory;

/**
 * Converter for .NET Method Parameters
 */
public class DotnetMethodParameter {

  /**
   * Converts list of proto .NET method parameter definitions to a list of SootTypes
   *
   * @param parameterList
   * @return
   */
  public static List<Type> toSootTypeParamsList(List<ProtoAssemblyAllTypes.ParameterDefinition> parameterList) {
    List<Type> types = new ArrayList<>();
    for (ProtoAssemblyAllTypes.ParameterDefinition parameter : parameterList) {
      Type type = DotnetTypeFactory.toSootType(parameter.getType());
      if (ByReferenceWrapperGenerator.needsWrapper(parameter)) {
        type = ByReferenceWrapperGenerator.getWrapperClass(type).getType();
      }
      types.add(type);
    }
    return types;
  }
}
