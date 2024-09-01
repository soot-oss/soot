package soot.dotnet.members;

import static soot.dotnet.specifications.DotnetModifier.toSootModifier;

import soot.Scene;

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

import soot.SootField;
import soot.Type;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetTypeFactory;

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
    return Scene.v().makeSootField(name, type, modifier);
  }
}
