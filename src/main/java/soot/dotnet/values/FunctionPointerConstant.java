package soot.dotnet.values;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.ArrayList;
import java.util.List;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Type;
import soot.dotnet.members.ByReferenceWrapperGenerator;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Constant;
import soot.util.Switch;

/**
 * Function pointer constants point to a specific method. Note that 
 * these are temporary, as they get replaced with delegate handlers
 * during the construction of .NET bodies.
 * 
 * @author Marc Miltenberger
 */
public class FunctionPointerConstant extends Constant {

  private static final long serialVersionUID = 1L;
  private DotnetMethod method;
  private boolean virtual;
  private SootMethodRef methodRef;

  public FunctionPointerConstant(DotnetMethod method, boolean virtual) {
    this.method = method;
    this.virtual = virtual;
    List<Type> parameterDefs = new ArrayList<>();
    for (ProtoAssemblyAllTypes.ParameterDefinition parameterDefinition : method.getProtoMessage().getParameterList()) {
      Type t = DotnetTypeFactory.toSootType(parameterDefinition.getType());
      if (ByReferenceWrapperGenerator.needsWrapper(parameterDefinition)) {
        SootClass sc = ByReferenceWrapperGenerator.getWrapperClass(t);
        t = sc.getType();
      }
      parameterDefs.add(t);

    }
    methodRef = Scene.v().makeMethodRef(method.getDeclaringClass(), method.getName(), parameterDefs,
        DotnetTypeFactory.toSootType(method.getReturnType()), method.isStatic());
  }

  @Override
  public int hashCode() {
    SootMethodRef mr = methodRef;
    int hash = mr.getName().hashCode();
    hash = hash * 31 + mr.getParameterTypes().size();
    hash = hash * 31 + mr.getReturnType().hashCode();
    hash = hash * 31 + (virtual ? 1 : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FunctionPointerConstant) {
      FunctionPointerConstant other = (FunctionPointerConstant) obj;
      SootMethodRef omr = other.methodRef;
      SootMethodRef mr = methodRef;
      if (virtual != other.virtual) {
        return false;
      }
      if (virtual) {
        return mr.resolve() == omr.resolve();
      }
      if (!mr.getName().equals(omr.getName()) || !mr.getParameterTypes().equals(omr.getParameterTypes())
          || !mr.getReturnType().equals(omr.getReturnType())) {
        return false;
      }
      if (mr.getDeclaringClass() != omr.getDeclaringClass()) {
        return false;
      }
      return true;
    }
    return super.equals(obj);
  }

  public DotnetMethod getDotnetMethod() {
    return method;
  }

  public boolean isVirtual() {
    return virtual;
  }

  public SootMethodRef getSootMethodRef() {
    return methodRef;
  }

  public FunctionPointerConstant clone() {
    throw new RuntimeException("Not supported");
  }

  @Override
  public boolean equivTo(Object o) {
    throw new RuntimeException("Not supported");
  }

  @Override
  public int equivHashCode() {
    throw new RuntimeException("Not supported");
  }

  @Override
  public Type getType() {
    return RefType.v("System.Delegate");
  }

  @Override
  public void apply(Switch sw) {
    throw new RuntimeException("Not supported");
  }

  @Override
  public String toString() {
    return "Function Ptr: " + methodRef + " - " + (virtual ? "virtual" : "non-virtual");
  }

}
