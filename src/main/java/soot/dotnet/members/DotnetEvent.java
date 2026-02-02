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
 * Represents a .NET Event (Member) SourceLocator -> ClassProvider -> ClassSource -> MethodSource (DotnetEvent,
 * DotnetMethod) An event consists of a add, invoke and remove method (directive)
 */
public class DotnetEvent extends AbstractDotnetMember {
  private static final Logger logger = LoggerFactory.getLogger(DotnetEvent.class);

  private final SootClass declaringClass;
  private final ProtoAssemblyAllTypes.EventDefinition protoEvent;
  private DotnetMethod addAccessorMethod;
  private DotnetMethod invokeAccessorMethod;
  private DotnetMethod removeAccessorMethod;

  public DotnetEvent(ProtoAssemblyAllTypes.EventDefinition protoEvent, SootClass declaringClass) {
    this.protoEvent = protoEvent;
    this.declaringClass = declaringClass;
    initDotnetMethods();
  }

  private void initDotnetMethods() {
    if (getCanAdd() && protoEvent.hasAddAccessorMethod()) {
      this.addAccessorMethod
          = new DotnetMethod(protoEvent.getAddAccessorMethod(), declaringClass, DotnetMethod.DotnetMethodType.EVENT);
    }
    if (getCanInvoke() && protoEvent.hasInvokeAccessorMethod()) {
      this.invokeAccessorMethod
          = new DotnetMethod(protoEvent.getInvokeAccessorMethod(), declaringClass, DotnetMethod.DotnetMethodType.EVENT);
    }
    if (getCanRemove() && protoEvent.hasRemoveAccessorMethod()) {
      this.removeAccessorMethod
          = new DotnetMethod(protoEvent.getRemoveAccessorMethod(), declaringClass, DotnetMethod.DotnetMethodType.EVENT);
    }
  }

  public enum EventDirective {
    ADD, REMOVE, INVOKE
  }

  public boolean getCanAdd() {
    return protoEvent.getCanAdd();
  }

  public boolean getCanInvoke() {
    return protoEvent.getCanInvoke();
  }

  public boolean getCanRemove() {
    return protoEvent.getCanRemove();
  }

  public SootMethod makeSootMethodAdd() {
    if (!getCanAdd() || !protoEvent.hasAddAccessorMethod()) {
      return null;
    }
    return addAccessorMethod.toSootMethod(createMethodSource(EventDirective.ADD));
  }

  public SootMethod makeSootMethodInvoke() {
    if (!getCanInvoke() || !protoEvent.hasInvokeAccessorMethod()) {
      return null;
    }
    return invokeAccessorMethod.toSootMethod(createMethodSource(EventDirective.INVOKE));
  }

  public SootMethod makeSootMethodRemove() {
    if (!getCanRemove() || !protoEvent.hasRemoveAccessorMethod()) {
      return null;
    }
    return removeAccessorMethod.toSootMethod(createMethodSource(EventDirective.REMOVE));
  }

  private MethodSource createMethodSource(EventDirective eventMethodType) {
    return (m, phaseName) -> {
      // Get body of method
      AssemblyFile assemblyFile = (AssemblyFile) SourceLocator.v().dexClassIndex().get(declaringClass.getName());
      ProtoIlInstructions.IlFunctionMsg ilFunctionMsg
          = assemblyFile.getMethodBodyOfEvent(declaringClass.getName(), protoEvent.getName(), eventMethodType);

      // add jimple body and jimplify
      DotnetMethod dotnetMethod;
      switch (eventMethodType) {
        case ADD:
          dotnetMethod = addAccessorMethod;
          break;
        case REMOVE:
          dotnetMethod = removeAccessorMethod;
          break;
        case INVOKE:
          dotnetMethod = invokeAccessorMethod;
          break;
        default:
          throw new RuntimeException("Unexpected selection of event method type!");
      }
      Body b = dotnetMethod.jimplifyMethodBody(ilFunctionMsg);

      m.setActiveBody(b);
      return m.getActiveBody();
    };
  }
}
