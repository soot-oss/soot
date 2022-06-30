package soot.dotnet.members;

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

import soot.Body;
import soot.MethodSource;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.dotnet.AssemblyFile;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoIlInstructions;

/**
 * Represents a .NET Property (Member) SourceLocator -> ClassProvider -> ClassSource -> MethodSource (DotnetProperty,
 * DotnetMethod) A property consists of a getter and a setter method
 */
public class DotnetProperty extends AbstractDotnetMember {
  private static final Logger logger = LoggerFactory.getLogger(DotnetProperty.class);

  private final SootClass declaringClass;
  private final ProtoAssemblyAllTypes.PropertyDefinition protoProperty;
  private DotnetMethod setterMethod;
  private DotnetMethod getterMethod;

  public DotnetProperty(ProtoAssemblyAllTypes.PropertyDefinition protoProperty, SootClass declaringClass) {
    this.protoProperty = protoProperty;
    this.declaringClass = declaringClass;
    initDotnetMethods();
  }

  private void initDotnetMethods() {
    if (getCanGet() && protoProperty.hasGetter()) {
      this.getterMethod
          = new DotnetMethod(protoProperty.getGetter(), declaringClass, DotnetMethod.DotnetMethodType.PROPERTY);
    }
    if (getCanSet() && protoProperty.hasSetter()) {
      this.setterMethod
          = new DotnetMethod(protoProperty.getSetter(), declaringClass, DotnetMethod.DotnetMethodType.PROPERTY);
    }
  }

  public boolean getCanGet() {
    return protoProperty.getCanGet();
  }

  public boolean getCanSet() {
    return protoProperty.getCanSet();
  }

  public SootMethod makeSootMethodGetter() {
    if (!protoProperty.getCanGet() || !protoProperty.hasGetter()) {
      return null;
    }
    return getterMethod.toSootMethod(createPropertyMethodSource(false));
  }

  public SootMethod makeSootMethodSetter() {
    if (!protoProperty.getCanSet() || !protoProperty.hasSetter()) {
      return null;
    }
    return setterMethod.toSootMethod(createPropertyMethodSource(true));
  }

  private MethodSource createPropertyMethodSource(boolean isSetter) {
    return (m, phaseName) -> {
      // Get body of method
      AssemblyFile assemblyFile = (AssemblyFile) SourceLocator.v().dexClassIndex().get(declaringClass.getName());
      ProtoIlInstructions.IlFunctionMsg ilFunctionMsg
          = assemblyFile.getMethodBodyOfProperty(declaringClass.getName(), protoProperty.getName(), isSetter);

      // add jimple body and jimplify
      DotnetMethod dotnetMethod = isSetter ? setterMethod : getterMethod;
      Body b = dotnetMethod.jimplifyMethodBody(ilFunctionMsg);
      m.setActiveBody(b);

      return m.getActiveBody();
    };
  }
}
